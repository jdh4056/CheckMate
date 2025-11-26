## 아키텍처
<img width="225" height="249" alt="image" src="https://github.com/user-attachments/assets/b4551234-a241-48a5-b84c-e38626eba3d8" />
<img width="452" height="118" alt="image" src="https://github.com/user-attachments/assets/680f0f22-09fa-4547-9a49-4e82ca4e0402" />


## 앱 화면
<img width="198" height="295" alt="image" src="https://github.com/user-attachments/assets/84348051-9e68-4e4b-bfca-b26b223129d0" />

## 인프라 아키텍처 개요
본 프로젝트는 제한된 리소스 내에서 최적의 성능과 보안을 확보하는 것을 목표로 설계

* **Compute**: AWS EC2 (Ubuntu) - 단일 인스턴스 내 Spring Boot(8080)와 FastAPI(5000) 동시 운용
* **Database**: AWS RDS (MySQL) - 데이터 영속성 및 백업 관리
* **Network & Security**:
  * **DNS**: AWS Route 53 (`api.ssucheckmate.com`)
  * **WebServer**: Nginx (Reverse Proxy)
  * **SSL/TLS**: Certbot (Let's Encrypt)을 활용한 HTTPS 적용
* **CI/CD**: GitHub Actions + Appleboy (SSH/SCP Action)

---

## 배포 자동화 및 트러블 슈팅 로그

### Issue 1: GitHub Actions 배포 중 프로세스 종료(Kill) 실패
- **문제 상황**: 배포 스크립트 실행 중 기존 서버 프로세스를 종료하기 위해 `pkill` 명령어를 사용했으나, `Process exited with status 143` 에러와 함께 GitHub Actions 파이프라인이 중단됨.
- **원인 분석**: `ssh-action`이 원격 쉘에서 시그널(`SIGTERM`)을 통해 프로세스가 종료되는 것을 비정상 종료로 인식하여 파이프라인을 실패 처리함.
- **해결**: 배포 스크립트 내 불안정한 `pkill` 로직을 주석 처리하고, 포트 점유 확인(`fuser`) 및 예외 처리를 강화하여 배포 안정성을 확보함. 향후 `systemctl`을 통한 서비스 관리 방식으로 고도화 예정.

### Issue 2: 클라우드 환경 변수(Env) 주입 문제
- **문제 상황**: GitHub Secrets와 `.env` 파일을 통해 API Key를 주입하려 했으나, `nohup`으로 실행되는 백그라운드 프로세스에 환경 변수가 제대로 전달되지 않아 Google Generative AI 클라이언트 인증 실패 발생.
- **해결**: 배포 스크립트 의존성을 제거하기 위해 EC2 인스턴스의 `/etc/environment`에 시스템 레벨 환경 변수로 `GOOGLE_API_KEY`를 등록하고, Python 코드에서 `os.environ.get()`으로 호출하도록 변경하여 보안성과 안정성을 동시에 해결함.

### Issue 3: Python 라이브러리 의존성 및 모듈 충돌
- **문제 상황**: 로컬 환경과 달리 EC2 배포 시 `ImportError: cannot import name 'genai' from 'google'` 오류 발생.
- **해결**: `google-generativeai` 패키지의 네임스페이스 구조를 분석하여 비표준 임포트 방식을 `import google.generativeai as genai`로 표준화하고, `genai.configure()`를 통해 클라이언트 초기화 로직을 재설계하여 해결.

### Issue 4: RAG 파이프라인(ChromaDB) 초기화 오류
- **문제 상황**: 임베딩 생성 후 반환 타입 불일치로 인해 ChromaDB 데이터 적재 실패.
- **해결**: Gemini API의 응답 딕셔너리 구조를 디버깅하여 `response['embedding']` 형태로 데이터를 추출하도록 커스텀 임베딩 함수(`GeminiEmbeddingFunction`)를 수정, 정상적으로 벡터 DB가 구축되도록 조치함.
