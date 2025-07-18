# 🚀 iOVU-BACK 설정 가이드

이 가이드는 iOVU-BACK 프로젝트를 처음부터 설정하고 실행하는 방법을 단계별로 설명합니다.

## � 전제 조건

- Java 17 이상
- MySQL 8.0 이상
- Git
- IDE (IntelliJ IDEA, VS Code 등)

## 🛠 1단계: 프로젝트 클론 및 초기 설정

### 1.1 저장소 클론

```bash
git clone https://github.com/sunwook-dev/iOVU-BACK.git
cd iOVU-BACK
```

### 1.2 환경 변수 파일 생성

```bash
# .env.example을 .env로 복사
cp .env.example .env
```

## 🔑 2단계: OAuth2 앱 등록 및 키 발급

### 2.1 Google OAuth2 설정

1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택
3. "API 및 서비스" → "사용자 인증 정보" 이동
4. "사용자 인증 정보 만들기" → "OAuth 클라이언트 ID" 선택
5. 애플리케이션 유형: **웹 애플리케이션** 선택
6. 승인된 리디렉션 URI 추가:
   ```
   http://localhost:8081/login/oauth2/code/google
   ```
7. 생성된 **클라이언트 ID**와 **클라이언트 보안 비밀** 복사

### 2.2 Kakao OAuth2 설정

1. [Kakao Developers](https://developers.kakao.com/) 접속 후 로그인
2. "내 애플리케이션" → "애플리케이션 추가하기"
3. 앱 이름, 사업자명 입력 후 생성
4. "앱 설정" → "플랫폼" → "Web 플랫폼 등록"
5. 사이트 도메인: `http://localhost:8081`
6. "제품 설정" → "카카오 로그인" 활성화
7. Redirect URI 설정:
   ```
   http://localhost:8081/login/oauth2/code/kakao
   ```
8. "동의항목" 설정:
   - 닉네임: 필수 동의
   - 이메일: 선택 동의
   - 프로필 이미지: 선택 동의
9. "앱 키" 탭에서 **REST API 키**와 **Client Secret** 복사

### 2.3 Naver OAuth2 설정

1. [Naver Developers](https://developers.naver.com/) 접속 후 로그인
2. "Application" → "애플리케이션 등록"
3. 애플리케이션 정보 입력:
   - 애플리케이션 이름: iOVU
   - 사용 API: 네이버 로그인
   - 환경: PC 웹
4. 서비스 URL: `http://localhost:8081`
5. Callback URL:
   ```
   http://localhost:8081/login/oauth2/code/naver
   ```
6. 제공정보 선택: 이름, 이메일, 프로필 이미지
7. 등록 완료 후 **Client ID**와 **Client Secret** 복사

## 🗄️ 3단계: 데이터베이스 설정

### 3.1 MySQL 설치 및 실행

```bash
# MySQL 서비스 시작 (Windows)
net start mysql

# MySQL 서비스 시작 (macOS)
brew services start mysql

# MySQL 서비스 시작 (Ubuntu/Debian)
sudo systemctl start mysql
```

### 3.2 데이터베이스 생성

```bash
# 자동 설정 스크립트 실행 (Windows)
setup-database.bat

# 수동 설정 (모든 OS)
mysql -u root -p < setup-database.sql
```

또는 MySQL 클라이언트에서 직접 실행:

```sql
-- 데이터베이스 생성
CREATE DATABASE iovu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 (선택사항)
CREATE USER 'iovu_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON iovu.* TO 'iovu_user'@'localhost';
FLUSH PRIVILEGES;
```

## ⚙️ 4단계: 환경 변수 설정

`.env` 파일을 열어서 다음 값들을 실제 값으로 교체하세요:

```bash
# OAuth2 설정 - 2단계에서 발급받은 키들로 교체
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here

KAKAO_CLIENT_ID=your_kakao_rest_api_key_here
KAKAO_CLIENT_SECRET=your_kakao_client_secret_here

NAVER_CLIENT_ID=your_naver_client_id_here
NAVER_CLIENT_SECRET=your_naver_client_secret_here

# 데이터베이스 설정
DB_URL=jdbc:mysql://localhost:3306/iovu?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_mysql_password_here

# JWT 설정 (보안을 위해 복잡한 키로 변경)
JWT_SECRET=your_super_secure_jwt_secret_key_at_least_256_bits_long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# CORS 설정 (프론트엔드 URL)
CORS_ALLOWED_ORIGINS=http://localhost:3000

# 서버 포트
SERVER_PORT=8081
```

## 🚀 5단계: 애플리케이션 실행

### 5.1 의존성 설치 및 빌드

```bash
# wrapper파일 생성
gradle wrapper

# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

### 5.2 애플리케이션 실행

```bash
# Windows - 일반 실행
gradlew.bat bootRun

# Windows - 빠른 실행 스크립트
start-backend.bat

# macOS/Linux
./gradlew bootRun

# IDE에서 실행
# OAuth2BackendApplication.java 파일의 main 메서드 실행
```

### 5.3 실행 확인

서버가 정상적으로 시작되면 다음과 같은 로그가 표시됩니다:

```
Started OAuth2BackendApplication in X.XXX seconds (JVM running for X.XXX)
```

브라우저에서 다음 URL로 테스트:

```
http://localhost:8081/actuator/health
```

## 🧪 6단계: OAuth2 로그인 테스트

### 6.1 직접 URL 테스트

브라우저에서 다음 URL들로 각 소셜 로그인을 테스트할 수 있습니다:

- **Google**: `http://localhost:8081/oauth2/authorization/google`
- **Kakao**: `http://localhost:8081/oauth2/authorization/kakao`
- **Naver**: `http://localhost:8081/oauth2/authorization/naver`

### 6.2 API 테스트

Postman이나 curl로 API 테스트:

```bash
# 사용자 정보 조회 (JWT 토큰 필요)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8081/api/current-user-id

# 내 리포트 목록 조회
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8081/api/reports/me
```

## 🔧 7단계: 개발 환경 설정

### 7.1 IDE 설정

**IntelliJ IDEA:**

1. 프로젝트 열기
2. Project Structure → SDK → Java 17 설정
3. Gradle 프로젝트로 import

**VS Code:**

1. Java Extension Pack 설치
2. Spring Boot Extension Pack 설치
3. 프로젝트 폴더 열기

### 7.2 환경별 설정 파일

개발, 테스트, 운영 환경별로 설정 파일을 생성할 수 있습니다:

```bash
# 개발 환경
cp application.yml application-dev.yml

# 테스트 환경
cp application.yml application-test.yml

# 운영 환경
cp application.yml application-prod.yml
```

환경별 실행:

```bash
# 개발 환경으로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 테스트 환경으로 실행
./gradlew bootRun --args='--spring.profiles.active=test'
```

## 🐛 문제 해결

### 일반적인 오류들

#### 1. OAuth2 인증 실패

**증상**: `redirect_uri_mismatch` 오류
**해결책**:

- OAuth2 앱에서 설정한 Redirect URI와 코드의 URI가 정확히 일치하는지 확인
- 포트 번호(8081) 확인
- http/https 프로토콜 확인

#### 2. 데이터베이스 연결 실패

**증상**: `Communications link failure` 오류
**해결책**:

- MySQL 서비스가 실행 중인지 확인
- 데이터베이스 이름, 사용자명, 비밀번호 확인
- 방화벽 설정 확인

#### 3. JWT 토큰 오류

**증상**: `JWT signature does not match` 오류
**해결책**:

- JWT_SECRET 키가 256비트 이상인지 확인
- 모든 인스턴스에서 동일한 시크릿 키 사용

#### 4. CORS 오류

**증상**: 브라우저에서 CORS 정책 오류
**해결책**:

- `CORS_ALLOWED_ORIGINS`에 프론트엔드 URL 추가
- 프론트엔드와 백엔드 포트 확인

### 디버깅 방법

```bash
# 상세 로그로 실행
./gradlew bootRun --debug

# 특정 패키지 로그 레벨 변경
./gradlew bootRun --args='--logging.level.org.springframework.security=DEBUG'

# OAuth2 관련 로그만 보기
./gradlew bootRun --args='--logging.level.org.springframework.security.oauth2=TRACE'
```

## 📚 추가 자료

### 공식 문서

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [MyBatis Documentation](https://mybatis.org/mybatis-3/)

### OAuth2 Provider 문서

- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [Kakao Login API](https://developers.kakao.com/docs/latest/ko/kakaologin/common)
- [Naver Login API](https://developers.naver.com/docs/login/api/)

## ✅ 설정 완료 체크리스트

- [ ] Java 17 이상 설치됨
- [ ] MySQL 설치 및 실행됨
- [ ] Google OAuth2 앱 등록 및 키 발급
- [ ] Kakao OAuth2 앱 등록 및 키 발급
- [ ] Naver OAuth2 앱 등록 및 키 발급
- [ ] `.env` 파일 생성 및 설정
- [ ] 데이터베이스 생성됨
- [ ] 애플리케이션 정상 실행됨
- [ ] OAuth2 로그인 테스트 성공
- [ ] API 엔드포인트 테스트 성공

설정이 완료되었다면 이제 iOVU 프론트엔드와 연동하여 전체 시스템을 테스트할 수 있습니다!
