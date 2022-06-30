# ㅋㅋㄷ (Cute Cat Dog)

## 빌드 및 배포
### 개발 환경
    💡 Infra 
    - AWS EC2
    - Ubuntu 20.04
    - nginx 1.18.0
    - Jenkins 2.289.2
　

    💡 Front - Android
    - mvvm
    - tensorflow lite
    - android sdk min 24 target 30
    - firebase storage
　

    💡 Back
    - spring boot (2.4.5)
    - mybatis (2.2.2)
    - lombok
    - maven (4.0.0)
    - JAVA 17

　

<div align=center> <h1> 📱 Front 📱</h1></div>

### 🚧 Android Apk 설치
   ## 1. [D103](https://j6d103.p.ssafy.io/) 배포 사이트에서 apk 파일을 다운로드 합니다.
   (또는 [S06P12D103 > client >  app > release](https://lab.ssafy.com/s06-ai-image-sub2/S06P22D103/-/tree/develop/Client/app/release) 폴더 내에서 본인 핸드폰 버전에 맞는 apk 파일을 다운로드 합니다.)
      
   <img src="/uploads/435261f91f7c6238d15fac00d9ba3600/apk.jpg">

   ## 2. 다운로드 받은 apk 파일을 사용자 휴대폰에 업로드한다.
   기종에 따라 방식이 다르기 때문에 옮기는 방법은 검색을 추천.
   [참고](https://sbnet.co.kr/install-apk-on-android/)

   ## 3. 휴대폰에서 apk 파일 찾아서 클릭 후 설치 진행
  
---  
---  
### ⭐ Android 실행 방법 및 앱 빌드


   ## 1. Android studio를 설치합니다.
   

   https://developer.android.com/studio?gclid=Cj0KCQjw1a6EBhC0ARIsAOiTkrHs2pne0fbirqMfuaqgSYhktBtCr_y7qyEZ9YptQ6pHlX8BuYxiIAEaAmIIEALw_wcB&gclsrc=aw.ds
   <img src = "/uploads/1fed737b01ad83c43ab3a61b862edd23/android_install.png" width="50%">

   - 다운로드 시 android-studio-2020.3.1.24-windows 버전 권장
     
      <img src = "/uploads/ab63d8551829a31ef6bf04ccf698210b/android_version.png" width="50%">


   - [설치에 어려움이 있으신 분은 링크 참고하시면 됩니다.](https://crazykim2.tistory.com/455)
   


   ## 2. Android studio를 실행 후 'File - Open'에서 Client 프로젝트 선택 후 'OK'를 클릭합니다.  
   <img src = "/uploads/6fc3ce9e6187a0e4e298cc0e4d370d0c/android_open_project.jpg" width="100%">
 


   ## 3. 실행 전 google-services.json 파일 존재 여부를 확인합니다.(없으면 실행 X)
   - 1번 클릭 후 Project 선택 -> 2의 'google-service.json' 파일 존재 확인 -> 있으면 1번 클릭 후 3번 사진의 Android 선택
   
      <img src = "/uploads/d278a8f8d26c7a3ad62232e1fe6854ae/project_check.jpg">



   ## 4. Local 서버인 경우 접속 서버 주소 변경
   - 'app - java - com.ssafy.ccd - config - ApplicationClass.kt' 클릭 -> SERVER_URL 값을 본인 ip 주소로 변경
   - **접속하려는 핸드폰도 같은 네트워크로 연결되어 있어야 함.**
      
      <img src = "/uploads/a51f57284eabfdddf5826face9bc7ede/android_server_ip_check.jpg">

   ## 5. **Android Version 9.0, API 레벨 28 이상인 [Android 기종 연결]**(#cf-핸드폰-연결)
   - Android version 7.0, API 레벨 24도 설치는 가능하나 많은 기능에 제약이 있음.
   - 에뮬레이터는 권장하지 않음

   ## 6. 실행할 휴대폰 선택 후 run 클릭

   <img src="/uploads/eb71ada32e8d9af4d2fa1073fb191eed/android_run.jpg">


   
   **cf. 핸드폰 연결**
     * 안드로이드 폰이 없는 경우 (안드로이드 스튜디오를 이용해 컴퓨터에서 에뮬레이터 실행)
       1. 안드로이드 스튜디오 우측 상단에 AVD Manager를 클릭합니다.
       2. 좌측 하단 create virtual device를 클릭합니다.
       3. phone 메뉴에서 자신이 원하는 기종을 하나 선택 후 next 합니다.
       4. 설치하고자 하는 API level을 선택 후 next 합니다.
       5. device의 이름을 설정한 후 finish하면 AVD가 생성됩니다.

     * 안드로이드 폰이 있는 경우 (개발자 도구를 이용해 폰에서 앱 실행)
       1. usb 케이블을 이용해 핸드폰과 컴퓨터와 연결
       2. 핸드폰의 설정 > 휴대전화 정보 > 소프트웨어 정보 > 빌드번호를 7번 탭
       3. 핸드폰의 설정 > 휴대전화 정보 밑에 개발자 옵션이 생김
       4. 개발자 옵션에서 USB 디버깅을 허용     

### ERD
[산출물/ERD](https://lab.ssafy.com/s06-ai-image-sub2/S06P22D103/-/tree/develop/%EC%82%B0%EC%B6%9C%EB%AC%BC/ERD)
![image](/uploads/6fa1983c2581ccd96e9d1e5a99353db7/image.png)
　

## 2. [프로젝트에서 사용하는 외부 서비스 정보](https://lab.ssafy.com/s06-webmobile4-sub2/S06P12D109/-/blob/develop/exec/%EC%99%B8%EB%B6%80%EC%84%9C%EB%B9%84%EC%8A%A4.md)
### 소셜인증
- 로그인
    - 구글
    - 카카오톡
    - 페이스북
- 일기 공유
    - 카카오톡
    
### 유튜브
　

## 3. DB 덤프 파일 최신본
DB 덤프 파일 [다운로드]()
　

## 4. [시연 시나리오](https://lab.ssafy.com/s06-ai-image-sub2/S06P22D103/-/blob/develop/exec/4.%EC%8B%9C%EC%97%B0%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%ED%8A%B9%ED%99%94PJT_D103_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4.pdf)

