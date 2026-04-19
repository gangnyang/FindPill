import os

# 검증 이미지와 라벨 디렉토리 경로
val_img_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\images_new\val"
val_label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\val"


# 빈 라벨 파일 삭제
def remove_empty_labels(img_dir, label_dir):
    for label_file in os.listdir(label_dir):
        label_path = os.path.join(label_dir, label_file)

        # 라벨 파일이 텍스트 파일이고 비어 있는지 확인
        if os.path.isfile(label_path):
            with open(label_path, 'r') as f:
                if f.read().strip() == '':  # 빈 파일이면 삭제
                    print(f"Empty label found: {label_file}. Deleting...")
                    # 해당 이미지와 라벨 파일 삭제
                    img_file = label_file.replace('.txt', '.jpg')  # 이미지 확장자에 맞게 변경
                    img_path = os.path.join(img_dir, img_file)
                    os.remove(label_path)
                    os.remove(img_path)
                    print(f"Deleted {img_file} and {label_file}")


remove_empty_labels(val_img_dir, val_label_dir)
