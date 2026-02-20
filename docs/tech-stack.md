# ExemOne2 기술 스택

## 1. 확정 사항

### 백엔드

| 항목 | 선택 |
|------|------|
| 언어 | Java 21 (LTS) |
| 프레임워크 | Spring Boot 3.x |
| 빌드 도구 | Gradle (Kotlin DSL) |
| API 스타일 | REST (JSON) |

### 프론트엔드

| 항목 | 선택 |
|------|------|
| 프레임워크 | React 19 |
| 언어 | TypeScript 5.x |
| 빌드 도구 | Vite 6.x |
| 상태 관리 | (추후 결정) |
| 차트/시각화 | (추후 결정) |

### 데이터 저장소

| 용도 | DB | 접근 방식 |
|------|-----|----------|
| 시계열 메트릭 조회 | ClickHouse 24.1 (기존) | 읽기 전용 |
| 메타데이터 조회 | PostgreSQL 17.3 (기존, :5433) | 읽기 전용 |
| 에이전트/인스턴스 상태 | Redis 7.2 (기존, :6379) | 읽기 전용 |
| 사용자/설정/대시보드 | PostgreSQL 17.x (자체) | 읽기/쓰기 |

### 배포

| 항목 | 선택 |
|------|------|
| 백엔드 | JAR 단독 실행 (`java -jar`) |
| 프론트엔드 (개발) | Vite dev server (`npm run dev`) |
| 프론트엔드 (프로덕션) | 빌드 후 Spring Boot 내장 서빙 또는 Nginx |
| 컨테이너화 | 미사용 (기존 ExemOne만 Docker) |

---

## 2. 아키텍처

### 전체 구조

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

- **기존 ExemOne**: 에이전트 수집 → Kafka → Ingester → ClickHouse/PostgreSQL 파이프라인 유지
- **ExemOne2**: 기존 DB를 읽기 전용으로 연결하여 데이터 조회/분석/시각화
- **자체 PostgreSQL**: ExemOne2 고유 데이터만 관리
  - 사용자 계정 및 인증
  - 대시보드 레이아웃/위젯 설정
  - 알림 규칙 및 이력
  - 사용자 환경설정

### Spring Boot 멀티 데이터소스

```
DataSource 구성:
  1. exemone-clickhouse  (읽기 전용) - 시계열 메트릭
  2. exemone-postgres    (읽기 전용) - 메타데이터
  3. exemone-redis       (읽기 전용) - 실시간 상태
  4. exemone2-postgres   (읽기/쓰기) - 자체 설정/사용자
```

---

## 3. 기존 ExemOne DB 접속 정보

| DB | 호스트 | 포트 | 사용자 | 비고 |
|----|--------|------|--------|------|
| ClickHouse | localhost | 8123 (HTTP), 9000 (Native) | exemone | ~1,248 테이블 |
| PostgreSQL | localhost | 5433 | postgres | ~245 테이블 |
| Redis | localhost | 6379 | (인증 없음) | 94개 키, TTL 기반 |

※ ExemOne2를 .155에서 실행 시 localhost로 접근 가능 (Docker 포트 포워딩)

---

## 4. 선택 근거

### Java + Spring Boot 유지 이유
- 기존 팀 숙련도 활용
- Spring Boot 3의 GraalVM Native Image 지원 (향후 경량화 가능)
- 풍부한 DB 커넥터 (ClickHouse JDBC, R2DBC 등)
- 기존 ExemOne API 패턴 참고 용이

### React + TypeScript 선택 이유
- 모니터링 UI 라이브러리 풍부 (Recharts, ECharts, AG Grid 등)
- 가장 큰 프론트엔드 생태계
- TypeScript로 대규모 코드베이스 안정성 확보

### 기존 DB 읽기 전용 연결 이유
- 데이터 중복 저장 불필요
- 기존 수집 파이프라인 검증 완료
- 개발 범위를 UI/API에 집중 가능
- 점진적 이관 시 수집 계층도 교체 가능
