import requests

url = "http://beatmania.app:8000/upload"  # 서버 주소
front_image_path = "test_images/3.jpg"
back_image_path = "test_images/3.jpg"  # 더미 파일

with open(front_image_path, 'rb') as front_file, open(back_image_path, 'rb') as back_file:
    files = {
        'front': ('6.jpg', front_file, 'image/jpeg'),
        'back': ('blank.jpg', back_file, 'image/jpeg')
    }
    response = requests.post(url, files=files)

    if response.status_code == 200:
        try:
            json_data = response.json()
            import json
            print("✅ JSON 응답:")
            print(json.dumps(json_data, ensure_ascii=False, indent=2))
        except ValueError:
            print("❌ 응답이 JSON 형식이 아닙니다.")
    else:
        print(f"❌ 오류 발생: HTTP {response.status_code}")