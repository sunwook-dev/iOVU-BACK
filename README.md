# iOVU-BACK

## 📝 프로젝트 개요

iOVU-BACK은 OAuth2 기반 소셜 로그인을 지원하는 Spring Boot 백엔드 서버입니다.
사용자 인증, 리포트 관리, JWT 토큰 기반 보안, MyBatis를 통한 데이터베이스 연동을 제공합니다.

## ✨ 주요 기능

- 🔐 **OAuth2 소셜 로그인**: Google, Kakao, Naver 지원
- 🎫 **JWT 토큰 기반 인증**: 안전한 사용자 인증 및 권한 관리
- 📊 **리포트 관리**: 사용자별 리포트 등록, 조회, 상세보기
- 🛡️ **보안 설정**: CORS, 인증 미들웨어, 보안 헤더
- 🗄️ **데이터베이스 연동**: MyBatis를 통한 MySQL 연동

## 🛠 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: MyBatis
- **Authentication**: Spring Security + OAuth2 + JWT
- **API Documentation**: REST API

## 🚀 빠른 시작

### 전제 조건

- Java 17 이상
- MySQL 8.0 이상
- Gradle (Wrapper 포함)

### 1. 리포지토리 클론

```bash
git clone https://github.com/sunwook-dev/iOVU-BACK.git
cd iOVU-BACK
```

### 2. 환경 변수 설정

```bash
# .env.example을 .env로 복사
cp .env.example .env

# .env 파일을 열어서 실제 값들로 수정
# - OAuth2 클라이언트 ID/Secret
# - 데이터베이스 연결 정보
# - JWT 시크릿 키
```

### 3. 데이터베이스 설정

```bash
# Windows
setup-database.bat

# 또는 MySQL에 직접 실행
mysql -u root -p < setup-database.sql
```

### 4. 애플리케이션 실행

```bash
# Windows
gradlew.bat bootRun

# macOS/Linux
./gradlew bootRun

# 또는 빠른 실행 스크립트 (Windows)
start-backend.bat
```

### 5. 실행 확인

```bash
# 서버 상태 확인
curl http://localhost:8081/actuator/health

# OAuth2 로그인 테스트 URL
# Google: http://localhost:8081/oauth2/authorization/google
# Kakao: http://localhost:8081/oauth2/authorization/kakao
# Naver: http://localhost:8081/oauth2/authorization/naver
```

## 📁 프로젝트 구조

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/iovu/iovuback/          # 메인 애플리케이션 패키지
│   │   │   │   ├── controller/             # REST API 컨트롤러
│   │   │   │   ├── domain/                 # 엔티티 클래스
│   │   │   │   ├── dto/                    # 데이터 전송 객체
│   │   │   │   ├── mapper/                 # MyBatis 매퍼 인터페이스
│   │   │   │   ├── service/                # 비즈니스 로직
│   │   │   │   └── util/                   # 유틸리티 클래스
│   │   │   └── com/iovu/oauth2/            # OAuth2 패키지
│   │   │       ├── config/                 # 보안 및 OAuth2 설정
│   │   │       ├── controller/             # OAuth2 컨트롤러
│   │   │       ├── dto/                    # OAuth2 DTO
│   │   │       ├── handler/                # 인증 성공/실패 핸들러
│   │   │       ├── mapper/                 # OAuth2 매퍼
│   │   │       ├── model/                  # OAuth2 모델
│   │   │       ├── repository/             # 사용자 리포지토리
│   │   │       └── service/                # OAuth2 서비스
│   │   └── resources/
│   │       ├── application.yml             # 메인 설정 파일
│   │       ├── schema.sql                  # 데이터베이스 스키마
│   │       ├── mapper/                     # MyBatis XML 매퍼
│   │       └── templates/                  # 템플릿 파일
│   └── test/                               # 테스트 코드
├── build.gradle                            # Gradle 빌드 설정
├── .env.example                            # 환경 변수 예시 파일
├── setup-database.sql                      # 데이터베이스 초기 설정
├── start-backend.bat                       # Windows 실행 스크립트
├── SETUP.md                               # 상세 설정 가이드
└── README.md                              # 프로젝트 문서
```

## 🔗 주요 API 엔드포인트

### 인증 관련

- `POST /api/auth/social` - 소셜 로그인
- `GET /api/current-user-id` - JWT 토큰으로 현재 사용자 ID 조회
- `GET /api/user-id` - 소셜 정보로 사용자 ID 조회

### 리포트 관리

- `GET /api/reports/me` - 내 리포트 목록 조회 (JWT 기반)
- `GET /api/reports/{userUid}` - 특정 사용자 리포트 목록
- `GET /api/report/{reportId}` - 리포트 상세 조회
- `POST /api/report` - 새 리포트 등록

### OAuth2 인증 URL

- Google: `/oauth2/authorization/google`
- Kakao: `/oauth2/authorization/kakao`
- Naver: `/oauth2/authorization/naver`

## ⚙️ 개발 환경 설정

### 로컬 개발

```bash
# 개발 모드로 실행 (Hot Reload)
./gradlew bootRun --args='--spring.profiles.active=dev'

# 테스트 실행
./gradlew test

# 빌드
./gradlew build
```

### 환경별 설정

- **개발환경**: `application-dev.yml`
- **테스트환경**: `application-test.yml`
- **운영환경**: `application-prod.yml`

### VS Code Tasks

프로젝트에는 다음 VS Code 태스크가 설정되어 있습니다:

- `gradle: bootRun` - 애플리케이션 실행
- `gradle: build` - 프로젝트 빌드
- `gradle: test` - 테스트 실행
- `gradle: clean` - 빌드 파일 정리

## 🔧 설정 가이드

상세한 OAuth2 설정 및 환경 구성은 [`SETUP.md`](./SETUP.md) 참조

### OAuth2 앱 등록 필요 사항

1. **Google Cloud Console**: OAuth 클라이언트 ID 생성
2. **Kakao Developers**: 앱 등록 및 카카오 로그인 활성화
3. **Naver Developers**: 애플리케이션 등록

### 데이터베이스 설정

```sql
-- 데이터베이스 생성
CREATE DATABASE iovu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 및 권한 부여
CREATE USER 'iovu_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON iovu.* TO 'iovu_user'@'localhost';
```

## 🐛 문제 해결

### 자주 발생하는 오류

1. **OAuth2 인증 실패**

   - Redirect URI 설정 확인
   - Client ID/Secret 값 검증

2. **데이터베이스 연결 오류**

   - MySQL 서비스 실행 상태 확인
   - 연결 정보 및 권한 검증

3. **CORS 오류**
   - `application.yml`의 CORS 설정 확인
   - 프론트엔드 URL이 허용 목록에 포함되었는지 확인

### 디버깅

```bash
# 상세 로그로 실행
./gradlew bootRun --debug

# 특정 로그 레벨로 실행
./gradlew bootRun --args='--logging.level.org.springframework.security=DEBUG'
```

## 🤝 기여 방법

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 👥 팀

- **Backend Developer**: [@sunwook-dev](https://github.com/sunwook-dev)

## 📞 지원

문제가 발생하거나 질문이 있으시면 [Issues](https://github.com/sunwook-dev/iOVU-BACK/issues)에 등록해 주세요.
