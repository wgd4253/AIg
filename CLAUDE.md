# ExemOne2 (엑셈원2)

통합 모니터링 솔루션

## Project Overview

엑셈원2는 서버, 애플리케이션, 데이터베이스, 네트워크 등 IT 인프라 전반을 통합 모니터링하는 솔루션입니다.
기존 ExemOne(v3.0.508.x) 아키텍처를 참고하여 새롭게 설계합니다.

## Reference

- `docs/reference/existing_infra.md` - 기존 인프라 현황 (서버, DB, WAS, 에이전트, 연계키, 아키텍처)
- 기존 ExemOne 문서: `/root/claude/` (01_architecture, 02_agents, 03_linkage, 04_db)

### 사용 가능한 인프라 요약

| 리소스 | 상세 |
|--------|------|
| .155 (로컬) | 16C/32GB, ExemOne 메인 서버 (Docker 18개) |
| .151 | 32C/32GB, ExemOne 서버 (Docker) |
| .152 | 32C/32GB, 에이전트/WAS (Petclinic, Tomcat 9, Apache 2.4) |
| .156 | 16C/32GB, ELK (Elasticsearch 9.2, Kibana 9.2) |
| .171 | 16C/32GB, ExemOne 서버 (Docker) |
| .170 | 32C/32GB, (용도 확인 필요) |
| .173 | 8C/16GB, 에이전트/DB (Oracle 19c, db-agent) |
| 내장 DB | PostgreSQL 17.3, ClickHouse 24.1, Redis 7.2, Kafka 3.3 |
| 지원 DBMS | Oracle, MySQL, PostgreSQL, SQL Server, Altibase, CUBRID + α |

### SSH 접속 패턴

```
ssh -t exemone@10.10.52.{N} "sudo -i"
SSH 비밀번호: exemone123!{N}  /  root: Root123!{N}
155는 로컬 / 152는 예외(SSH pw: exemone123!151) / 포트: 2022
```

## Tech Stack

| 영역 | 기술 | 버전 | 용도 |
|------|------|------|------|
| 백엔드 | Java + Spring Boot | Java 21, Spring Boot 3.x | REST API 서버 |
| 프론트엔드 | React + TypeScript | React 19, TS 5.x | SPA UI |
| 빌드 도구 | Vite | 6.x | 프론트엔드 빌드/개발 서버 |
| 자체 DB | PostgreSQL | 17.x | 사용자, 설정, 대시보드 관리 |
| 기존 DB (읽기) | ClickHouse | 24.1 | 시계열 메트릭 조회 (기존 ExemOne) |
| 기존 DB (읽기) | PostgreSQL | 17.3 | 메타데이터 조회 (기존 ExemOne) |
| 기존 DB (읽기) | Redis | 7.2 | 에이전트/인스턴스 상태 조회 (기존 ExemOne) |

### 아키텍처 개요

```
[기존 ExemOne Docker 스택 (.155)]       [ExemOne2 - 독립 실행]
┌──────────────────────┐               ┌─────────────────────────┐
│ ClickHouse (:8123)   │── 읽기 ──────→│ Spring Boot 3 (API)     │
│ PostgreSQL (:5433)   │── 읽기 ──────→│   - 기존 DB 조회        │
│ Redis (:6379)        │── 읽기 ──────→│   - 자체 PG (설정/사용자)│
│                      │               │         ↕               │
│ Agents/Receiver/     │               │ React + TypeScript (UI) │
│ Kafka/Ingester/Core  │               │   - Vite dev server     │
└──────────────────────┘               └─────────────────────────┘
```

### 데이터 소스 전략

- **기존 ExemOne DB**: 수집/저장은 기존 스택이 담당, ExemOne2는 읽기 전용 연결
- **자체 PostgreSQL**: ExemOne2 고유 데이터 (사용자 계정, 대시보드 설정, 알림 규칙 등)

## Project Structure

```
exemone2/
├── CLAUDE.md
├── .gitignore
├── docs/
│   ├── reference/
│   │   └── existing_infra.md           # 기존 인프라 현황
│   └── tech-stack.md                   # 기술 스택 상세
├── backend/                            # Spring Boot 3 (Java 17)
│   ├── build.gradle.kts                # Gradle Kotlin DSL
│   ├── settings.gradle.kts
│   ├── gradlew / gradlew.bat
│   └── src/main/java/com/exem/exemone2/
│       ├── ExemOne2Application.java    # 메인 엔트리포인트
│       ├── config/
│       │   └── DataSourceConfig.java   # 멀티 데이터소스 설정
│       └── controller/
│           └── HealthController.java   # 헬스체크 API
└── frontend/                           # React 19 + TypeScript (Vite)
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── main.tsx
        └── App.tsx
```

## Development Conventions

- 커밋 메시지는 한글로 작성
- 코드 주석은 한글 허용
- 문서는 한글로 작성

## Build & Run

```bash
# 백엔드 (Spring Boot)
cd backend
./gradlew bootRun

# 프론트엔드 (React + Vite)
cd frontend
npm run dev
```

- 배포: 단독 실행 (JAR + Vite dev server)
- 프로덕션: React 빌드 → Spring Boot static 서빙 가능

## Key Decisions

- **2026-02-20**: 기술 스택 확정 - Java 21 + Spring Boot 3 / React + TypeScript / Vite
- **2026-02-20**: 기존 ExemOne DB를 읽기 전용으로 연결, 자체 PostgreSQL은 설정/사용자 관리용
- **2026-02-20**: 배포 방식 - 단독 실행 (JAR + Node), Docker 미사용
