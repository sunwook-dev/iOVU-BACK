# 🔒 보안 주의사항

## ⚠️ 중요: Git에 커밋하기 전에 반드시 확인하세요!

이 프로젝트는 OAuth2 키, 데이터베이스 비밀번호, JWT 시크릿 등 민감한 정보를 포함하고 있습니다.

### ✅ 안전한 설정 (현재 상태)

1. **환경 변수 사용**: `application.yml`에서 모든 민감한 정보는 환경 변수로 처리됨
2. **`.env` 파일 제외**: `.gitignore`에 `.env` 파일이 포함되어 Git에서 제외됨
3. **`.env.example` 제공**: 다른 개발자들이 쉽게 설정할 수 있도록 템플릿 제공

### 🚨 절대 하지 말아야 할 것들

- ❌ 실제 OAuth2 키를 `application.yml`에 하드코딩
- ❌ 데이터베이스 비밀번호를 코드에 직접 입력
- ❌ `.env` 파일을 Git에 커밋
- ❌ JWT 시크릿을 공개 저장소에 노출

### 🔧 로컬 개발 설정

```bash
# 1. .env.example을 .env로 복사
cp .env.example .env

# 2. .env 파일에 실제 값들 입력
# (OAuth2 키, DB 비밀번호 등)

# 3. Git 상태 확인 (.env가 제외되었는지 확인)
git status
```

### 🌐 운영 환경 설정

운영 서버에서는 다음 방법 중 하나를 사용하세요:

1. **환경 변수 직접 설정**:

   ```bash
   export GOOGLE_CLIENT_ID="your_production_key"
   export DB_PASSWORD="your_production_password"
   ```

2. **시스템 환경 변수**:

   - Linux: `/etc/environment` 또는 systemd 서비스 파일
   - Docker: `docker-compose.yml`의 `environment` 섹션
   - AWS: Parameter Store, Secrets Manager
   - Azure: Key Vault
   - GCP: Secret Manager

3. **CI/CD 환경 변수**:
   - GitHub Actions: Repository Secrets
   - GitLab CI: CI/CD Variables
   - Jenkins: Credentials

### 🔍 보안 체크리스트

커밋하기 전에 다음을 확인하세요:

- [ ] `application.yml`에 실제 키/비밀번호가 없음
- [ ] `.env` 파일이 `.gitignore`에 포함됨
- [ ] `git status`에서 `.env` 파일이 나타나지 않음
- [ ] 모든 민감한 정보가 환경 변수로 처리됨
- [ ] `.env.example`이 올바른 템플릿을 제공함

### 🚨 만약 실수로 키를 커밋했다면?

1. **즉시 키 폐기**: OAuth2 앱에서 해당 키를 즉시 삭제/재발급
2. **Git 히스토리 정리**: `git filter-branch` 또는 `BFG Repo-Cleaner` 사용
3. **새 키 생성**: 새로운 보안 키들을 생성하여 교체

```bash
# Git 히스토리에서 파일 완전 삭제
git filter-branch --force --index-filter \
'git rm --cached --ignore-unmatch path/to/file/with/secrets' \
--prune-empty --tag-name-filter cat -- --all
```

### 📞 도움이 필요하시면

보안 관련 질문이나 문제가 있으시면 팀 리더에게 문의하세요.

**기억하세요: 한 번 Git에 올라간 정보는 완전히 삭제하기 어렵습니다!**
