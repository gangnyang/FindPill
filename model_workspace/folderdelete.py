import os
import shutil

# 이미지, 라벨이 여러 하위 폴더에 흩어져 있는 루트 경로
src_img_root = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\images\train"
src_label_root = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\labels\train"

# 평탄화 후 최종 저장할 폴더 (하위 폴더 없이 한 곳에 모음)
dst_img_dir = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\images_flat"
dst_label_dir = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\labels_flat"

os.makedirs(dst_img_dir, exist_ok=True)
os.makedirs(dst_label_dir, exist_ok=True)

def flatten_files(src_root, dst_dir, exts):
    for root, dirs, files in os.walk(src_root):
        for file in files:
            if file.lower().endswith(exts):
                src_path = os.path.join(root, file)
                dst_path = os.path.join(dst_dir, file)

                # 같은 이름 파일 있으면 이름 변경
                base, ext = os.path.splitext(file)
                count = 1
                while os.path.exists(dst_path):
                    dst_path = os.path.join(dst_dir, f"{base}_{count}{ext}")
                    count += 1

                shutil.move(src_path, dst_path)
                print(f"이동 완료: {src_path} -> {dst_path}")

print("이미지 파일 평탄화 중...")
flatten_files(src_img_root, dst_img_dir, ('.png', '.jpg', '.jpeg'))

print("라벨 파일 평탄화 중...")
flatten_files(src_label_root, dst_label_dir, ('.txt',))

print("✅ 평탄화 완료!")
