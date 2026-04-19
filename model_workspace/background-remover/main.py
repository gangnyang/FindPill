from fastapi import FastAPI, UploadFile, File
from fastapi.responses import StreamingResponse
from rembg import remove, new_session
import io

app = FastAPI()

# ONNX 커스텀 모델 세션 초기화
model_path = "models/u2net_pill_136000.onnx"
session = new_session(model_name='u2net_custom', model_path=model_path)

@app.post("/upload")
async def upload_image(front: UploadFile = File(...), back: UploadFile = File(...)):
    # front 이미지만 처리
    input_bytes = await front.read()
    output_bytes = remove(input_bytes, session=session)

    # 결과 PNG 스트림으로 반환
    return StreamingResponse(
        io.BytesIO(output_bytes),
        media_type="image/png",
        headers={"Content-Disposition": "inline; filename=front_nobg.png"}
    )
