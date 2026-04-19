import os
import shutil
import random

# 기존 train 이미지/라벨 폴더 (기준 폴더)
base_img_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\images\train"
base_label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\train"

# 최종 train/val 폴더 경로 (val은 새로 나눠서 만듦)
train_img_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\images\train"
val_img_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\images\val"

train_label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\train"
val_label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\val"  # 여기 수정됨

# 폴더 없으면 생성
os.makedirs(train_img_dir, exist_ok=True)
os.makedirs(val_img_dir, exist_ok=True)
os.makedirs(train_label_dir, exist_ok=True)
os.makedirs(val_label_dir, exist_ok=True)

# val 폴더 내 기존 파일 삭제 (이미지 + 라벨)
def clear_folder(folder):
    for f in os.listdir(folder):
        path = os.path.join(folder, f)
        if os.path.isfile(path):
            os.remove(path)

clear_folder(val_img_dir)
clear_folder(val_label_dir)

# train 폴더 내 이미지 파일 리스트 (기준)
image_files = [f for f in os.listdir(base_img_dir) if f.lower().endswith(('.png', '.jpg', '.jpeg'))]

random.seed(42)
random.shuffle(image_files)

# 20%를 val로 분할
val_count = int(len(image_files) * 0.2)
val_files = image_files[:val_count]
train_files = image_files[val_count:]

print(f"전체 {len(image_files)}개 중 {val_count}개를 검증용으로 분리")

def move_files(file_list, img_src, img_dst, label_src, label_dst):
    for file_name in file_list:
        src_img_path = os.path.join(img_src, file_name)
        dst_img_path = os.path.join(img_dst, file_name)
        if os.path.exists(src_img_path):
            shutil.move(src_img_path, dst_img_path)
        else:
            print(f"이미지 없음: {src_img_path}")

        label_name = os.path.splitext(file_name)[0] + ".txt"
        src_label_path = os.path.join(label_src, label_name)
        dst_label_path = os.path.join(label_dst, label_name)

        if os.path.exists(src_label_path):
            shutil.move(src_label_path, dst_label_path)
        else:
            print(f"라벨 없음: {src_label_path}")

move_files(train_files, base_img_dir, train_img_dir, base_label_dir, train_label_dir)
move_files(val_files, base_img_dir, val_img_dir, base_label_dir, val_label_dir)

print("✅ train/val 분할 완료!")
