<h1>프론트엔드 구현 (담당자: 전동원)</h1>
애플리케이션의 기능은 네 가지로 나뉜다
1. 알약 이미지 검색
2. 알약 식별 검색
3. 즐겨찾기한 알약 보기
4. 알약 캘린더(복용 알람)

<h2>1. 알약 이미지 검색</h2>
<h3>입력으로는 다음과 같은 알약 사진의 앞, 뒷면을 입력받는다. 알약이 여러 개일 경우 좌에서 우로 순서대로 놓고 촬영하여야 한다.</h3>

![pill_back](https://github.com/user-attachments/assets/11100ec0-32fd-4c16-8309-1e73fd833cec)
![pill_front](https://github.com/user-attachments/assets/ba64aac9-6be5-4960-9387-96bf748e9585)

결과로는 배경제거, YOLO 모델을 거친 후 OCR 모델 결과와 적절히 조합하여 결과 알약 하나를 반환한다.



<h2>2. 알약 식별 검색</h2>

![image](https://github.com/user-attachments/assets/8e745c46-3311-4020-8c99-518acac16b55)

위와 같이 알약을 직접 보고 알약의 특징을 입력하여 서버로 보내면 알약 리스트를 반환한다.


<h2>3. 즐겨찾기한 알약 보기</h2>

![image](https://github.com/user-attachments/assets/4910f2c5-9fa5-4702-a8ea-bb9c2d2d82f4)


즐겨찾기한 알약을 확인할 수 있다. 각 알약의 상세페이지에서 즐겨찾기를 등록하면 된다.

<h2>4. 알약 캘린더(복용 알람)</h2>

![image](https://github.com/user-attachments/assets/8bdc73e9-d466-4b47-b952-8bd1defb7f49)

먹어야 할 알약을 리스트로 보여주고 있을 경우 특정 시간에 알림을 보낸다.
