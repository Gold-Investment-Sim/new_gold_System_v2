# app.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import numpy as np
import pandas as pd
import tensorflow as tf
import joblib, json
from datetime import timedelta

# ====== 모델/아티팩트 경로 ======
MODEL_PATH  = "models/krwG_lstm.keras"
SCALER_PATH = "models/scaler_all_cols.gz"
META_PATH   = "models/meta.json"

# ====== 로드 ======
app = FastAPI(title="Gold LSTM Serving")
try:
    model  = tf.keras.models.load_model(MODEL_PATH)
    scaler = joblib.load(SCALER_PATH)
    meta   = json.load(open(META_PATH, "r", encoding="utf-8"))
except Exception as e:
    raise RuntimeError(f"artifact load failed: {e}")

ALL_COLS     = meta["all_cols"]          # ["KRW_G_OPEN","FX_RATE","VIX","ETF_VOLUME","KRW_G_CLOSE"]
TARGET_INDEX = meta["target_index"]      # 4
WINDOW       = meta["window_size"]       # 30

# ====== I/O 모델 ======
class Row(BaseModel):
    date: str
    KRW_G_OPEN: float
    FX_RATE: float
    VIX: float
    ETF_VOLUME: float
    KRW_G_CLOSE: float

class Inp(BaseModel):
    rows: List[Row]
    return_last_n: Optional[int] = None
    next_day: bool = False  # ← 새 옵션 추가

class Out(BaseModel):
    timestamps: List[str]
    y_pred: List[float]

# ====== 유틸 ======
def make_seq(arr: np.ndarray, w: int) -> np.ndarray:
    return np.array([arr[i:i+w] for i in range(len(arr)-w)], dtype="float32")

def inv_target(y2d: np.ndarray) -> np.ndarray:
    dummy = np.zeros((len(y2d), len(ALL_COLS)), dtype="float32")
    dummy[:, TARGET_INDEX] = y2d[:, 0]
    return scaler.inverse_transform(dummy)[:, TARGET_INDEX]

# ====== 엔드포인트 ======
@app.get("/health")
def health():
    return {"ok": True, "window": WINDOW, "cols": ALL_COLS}

@app.post("/predict", response_model=Out)
def predict(body: Inp):
    # ✅ 최소 행 개수 체크
    min_needed = WINDOW if body.next_day else (WINDOW + 1)
    if len(body.rows) < min_needed:
        raise HTTPException(
            status_code=400,
            detail=f"rows<{min_needed}; need at least {min_needed} rows for "
                   f"{'next_day' if body.next_day else 'sliding'} mode"
        )

    # ✅ DataFrame 구성 및 정렬
    df = pd.DataFrame([r.dict() for r in body.rows])
    try:
        df["date"] = pd.to_datetime(df["date"], errors="raise")
    except Exception:
        raise HTTPException(status_code=400, detail="invalid date format; use ISO YYYY-MM-DD")
    df = df.sort_values("date").reset_index(drop=True)

    # ✅ 컬럼 검증
    missing = [c for c in ALL_COLS if c not in df.columns]
    if missing:
        raise HTTPException(status_code=400, detail=f"missing columns: {missing}")

    vals = df[ALL_COLS].to_numpy(dtype="float32")
    scaled = scaler.transform(vals).astype("float32")

    # ✅ 다음날 예측 모드
    if body.next_day:
        last_win = scaled[-WINDOW:]
        X = np.expand_dims(last_win, axis=0)
        try:
            yhat = model.predict(X, verbose=0)
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"prediction failed: {e}")

        pred = inv_target(yhat)
        next_ts = (df["date"].iloc[-1] + timedelta(days=1)).strftime("%Y-%m-%d")
        return Out(timestamps=[next_ts], y_pred=[float(pred[0])])

    # ✅ 기본 슬라이딩 모드
    X = make_seq(scaled, WINDOW)
    if X.shape[0] == 0:
        raise HTTPException(status_code=400, detail="not enough rows after windowing")

    try:
        yhat = model.predict(X, verbose=0)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"prediction failed: {e}")

    pred = inv_target(yhat)
    ts = df["date"].dt.strftime("%Y-%m-%d").tolist()[WINDOW:]

    if body.return_last_n:
        ts = ts[-body.return_last_n:]
        pred = pred[-body.return_last_n:]

    return Out(timestamps=ts, y_pred=pred.astype(float).tolist())
