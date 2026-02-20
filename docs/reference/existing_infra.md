# 기존 인프라 현황 (ExemOne 기반)

엑셈원2 개발에 활용 가능한 기존 서버, DB, WAS 및 인프라 정보를 정리한 문서.

---

## 1. 서버 현황

### 접속 정보 및 비밀번호 패턴

```
SSH 접속:  ssh -t exemone@10.10.52.{N} "sudo -i"
SSH 비밀번호: exemone123!{N}
root 비밀번호: Root123!{N}

※ 155는 로컬호스트 (현재 작업 서버)
※ 152는 예외적으로 SSH 비밀번호가 exemone123!151
※ SSH 포트는 전체 2022
```

### 전체 서버 리소스 현황 (2026.02.20 측정)

| 호스트명 | IP | OS | CPU | Mem 총 | Mem 사용 | / 총 | / 사용 | /home 총 | /home 사용 | 용도 |
|---------|-----|-----|-----|-------|---------|------|--------|---------|-----------|------|
| exemONE-155 | .155 (로컬) | Rocky 9.5 | 16C | 32GB | 16GB (50%) | 70GB | 31GB (44%) | 154GB | 87GB (57%) | ExemOne 서버 (Docker 18개) |
| exemone151 | .151 | Rocky 9.5 | 32C | 32GB | 14GB (44%) | 86GB | 8.5GB (10%) | 400GB | 155GB (39%) | ExemOne 서버 (Docker) |
| exemone152 | .152 | Rocky 9.5 | 32C | 32GB | 13GB (41%) | 86GB | 13GB (16%) | 400GB | 101GB (26%) | 에이전트, WAS, 웹서버 |
| elk156 | .156 | Rocky 9.5 | 16C | 32GB | 8.2GB (26%) | 70GB | 29GB (41%) | 154GB | 29GB (19%) | ELK (ES 9.2 + Kibana 9.2) |
| exemONE-171 | .171 | Rocky 9.5 | 16C | 32GB | 14GB (44%) | 70GB | 31GB (44%) | 154GB | 57GB (37%) | ExemOne 서버 (Docker) |
| exemone170 | .170 | Rocky 9.5 | 32C | 32GB | 12GB (38%) | 86GB | 23GB (26%) | 400GB | 34GB (9%) | (확인 필요) |
| ol8-19 | .173 | Oracle Linux 8.6 | 8C | 16GB | 5.3GB (33%) | 60GB | 23GB (38%) | 125GB | 17GB (14%) | 에이전트, Oracle 19c |

### 서버별 상세

**155 (로컬, ExemOne 메인)**
- Docker 18개 컨테이너 (ExemOne 전체 스택)
- PG 17.3, ClickHouse 24.1, Redis 7.2, Kafka 3.3

**151 (ExemOne 서버)**
- Docker ExemOne 스택 (receiver, gateway, api, core, ingester, alerter 등)
- host-agent v3.0.27.2

**152 (에이전트/WAS)**
- Petclinic (Spring Boot), Tomcat 9 (oracle_compose), Apache 2.4.63 (mod_exem)
- host-agent v3.0.27.1, manager-agent

**156 (ELK)**
- Elasticsearch 9.2.0 (:9200, 인증 활성), Kibana 9.2.0 (:5601)

**171 (ExemOne 서버)**
- Docker ExemOne 스택 (receiver, gateway, api, core, ingester, alerter 등)
- /home 37% 사용 (정리 완료)

**170**
- 32C/32GB, Rocky 9.5, 디스크 여유 충분 (/home 9%)

**173 (에이전트/DB)**
- Oracle 19c (:1521), host-agent, db-agent v3.0.508.72, manager-agent v3.0.6

---

## 2. 데이터베이스 현황

### ExemOne 내장 DB

| DB | 버전 | 포트 | 용도 | 테이블 수 |
|----|------|------|------|----------|
| PostgreSQL | 17.3 | 5433 (외부) / 5432 (내부) | 메타데이터, 설정, 사용자 | ~245개 |
| ClickHouse | 24.1.5.6 | 8123 (HTTP), 9000 (Native) | 시계열 메트릭, 성능 데이터 | ~1,248개 |
| Redis | 7.2.11 | 6379 | 세션/캐시 (94개 키, 전체 TTL) |
| Kafka | 3.3.2 (KRaft) | 9092 | 메시지 큐 (172개 토픽) |

### 모니터링 대상 DB

| DB | 호스트 | 포트 | db_link_key | 인스턴스명 |
|----|--------|------|-------------|-----------|
| Oracle 19c | 10.10.52.173 | 1521 | `3391483644` | oracle19c |
| PostgreSQL | 10.10.52.155 | 5433 | `7589448444321325077` | meta_pg |
| PostgreSQL | 10.10.52.151 | 5432 | `7535696417857839125` | pg_pet / petclinic |

### 지원 DBMS 목록

Oracle, MySQL, PostgreSQL, SQL Server, Altibase, CUBRID, MongoDB, Redis, RedShift, SingleStore

---

## 3. WAS (Web Application Server) 현황

### Petclinic (Spring Boot)

| 항목 | 값 |
|------|-----|
| 서버 | 10.10.52.152 |
| WAS 그룹 | pet01 |
| 설정 프로파일 | curr 0 (config_id=3) |
| java-agent 경로 | `/data/exem/exem/java/lib/exem-java-agent.jar` |
| java.conf 경로 | `/data/exem/exem/java/cfg/agent/java.conf` |
| JVM 옵션 | `-javaagent:...exem-java-agent.jar -Dexem.groupid=pet01 -Dexem.bid=$(hostname) -Dexem.agent.name=petclinic` |

### oracle_compose (Tomcat 9)

| 항목 | 값 |
|------|-----|
| 서버 | 10.10.52.152 |
| WAS 그룹 | oracle-compose |
| Tomcat 경로 | `/was1/apache-tomcat-9.0.98/` |
| java-agent 경로 | `/was1/apache-tomcat-9.0.98/exem/java/lib/exem-java-agent.jar` |
| JVM 옵션 | catalina.sh 274번 줄에 `-javaagent` 설정 |

### WAS 설정 프로파일

| config_id | name | is_default | 사용 그룹 |
|-----------|------|------------|-----------|
| 1 | default | true | Default, pet01-init-161 |
| 3 | curr 0 | false | pet01, pet01-init, oracle-compose, ${EXEM_GROUP_ID} |

### java-agent 주요 설정 (153개 키)

설정 우선순위: **웹 UI(PostgreSQL) > 로컬 java.conf**

자주 변경하는 설정:

| 키 | 기본값 | 용도 |
|----|--------|------|
| `RESP_HEADER_TID` | false | WAS↔Web 연계 (true 필수) |
| `CURR_TRACE_TXN` | (빈값) | 콜트리 수집 조건 (예: `*:3000`) |
| `EXCLUDE_SERVICE` | gif,js,css... | 수집 제외 서비스 |
| `TRX_NAME_TYPE` | 0 | 트랜잭션 이름 방식 |
| `REPLACE_URL_PATTERNS` | (빈값) | URL 패턴 치환 |
| `DISABLE_SQL_BIND` | false | SQL 바인드 수집 비활성화 |
| `MTD_ELAPSE_TIME` | false | 메서드 경과시간 필터 |

---

## 4. 웹서버 현황

| 항목 | 값 |
|------|-----|
| 서버 | 10.10.52.152 |
| 종류 | Apache 2.4.63 |
| 모듈 | mod_exem (WSM 연동) |
| host_key | `exemone152` |
| 에이전트 버전 | 3.0.10 |
| 포트 | 12848 (wsm-agent) |

---

## 5. 에이전트 정보

### 에이전트 종류

| 에이전트 | 용도 | 실행 계정 | 통신 |
|---------|------|----------|------|
| host-agent | OS 리소스 (CPU/Mem/Disk/Net/Process) | root | gRPC :9009 |
| db-agent | DB 성능 (SQL 실행) | exemone | gRPC :9009 |
| java-agent | JVM/APM (트랜잭션/SQL/메서드) | WAS 계정 | HTTP :9008 |
| manager-agent | 에이전트 원격 관리 | root | - |
| webserver-agent | 웹서버 상태 | - | TCP :12848 |
| cloud-agent | AWS/Azure 메트릭 | - | gRPC :9009 |

### host-agent 주요 설정

| 설정 | 기본값 | 설명 |
|------|--------|------|
| `INTERVAL_METRIC` | 60 | 메트릭 수집 주기 (초) |
| `INTERVAL_PROCESS` | 300 | 프로세스 수집 주기 |
| `LOG_LEVEL` | info | 로그 레벨 |
| `COLLECT_MOUNT_TYPE_LIST` | ext,vxfs,xfs,btrfs | 수집 대상 파일시스템 |

### db-agent Oracle 수집 쿼리 (42개)

주요 쿼리:

| 쿼리 | 대상 뷰 | 용도 |
|------|---------|------|
| ActiveSessionExecutorQuery | V$SESSION, V$SQL 등 | ACTIVE 세션 상세 |
| SqlStatExecutorQuery | V$SQL | SQL 통계 (주기적) |
| TablespaceExecutorQuery | DBA_FREE_SPACE, DBA_DATA_FILES | 테이블스페이스 사용량 |
| WaitChainExecutorQuery | V$WAIT_CHAINS | 블로킹/대기 체인 |
| DBStatMetricExecutorQuery | V$SYSSTAT | 시스템 통계 메트릭 |
| OsStatExecutorQuery | V$OSSTAT | OS CPU/메모리 |
| SgaExecutorQuery | V$SGASTAT | SGA 메모리 |
| SessionListQuery | V$SESSION, V$SQL | 세션 목록 (동적 WHERE) |

---

## 6. 플랫폼 연계 키

| 연계 | 키 | 매칭 방식 |
|------|-----|----------|
| WAS ↔ DB | `db_link_key` | `application_txn_detail_v2.db_calls.db_link_key` = `xm_instance.db_link_key` |
| WAS ↔ Host | `host_key` (exemBID) | `xm_application.host_key` = `xm_infra_host.host_key` |
| WAS ↔ Web | `tid` ↔ `xm_tid` | `application_txn_detail_v2.tid` = `webserver_completed_txn.xm_tid` |
| Web ↔ Host | `host_key` (exemBID) | `xm_wsm.host_key` = `xm_infra_host.host_key` |
| WAS ↔ Container | `container_key` (exemWID) | `xm_application.container_key` = Pod의 `target_id` |

### exemBID vs exemWID

| 환경 | host_key (exemBID) | container_key (exemWID) |
|------|---------------------|--------------------------|
| 일반 호스트 | 값 있음 | 비어있음 |
| K8s | 비어있음 | 값 있음 |

---

## 7. 아키텍처 (데이터 흐름)

```
[에이전트] → [Receiver:9008-9010] → [Kafka:9092] → [Ingester] → [ClickHouse]
                  ↓                                      ↓
               [Redis] ←←←←←←←←←←←←←←←←←←←←←←←←← [Core] → [PostgreSQL]
                  ↑                                      ↓
[웹 UI] ← [HTTPS:8443] ← [Gateway:8080] ← [Cache] ← [API:8081]
                                                         ↑
                                                     [Alerter]
```

### 컨테이너 포트 맵

| 컴포넌트 | 내부 포트 | 외부 포트 | 용도 |
|---------|----------|----------|------|
| HTTPS | 8443 | 8443 | 웹 UI (SSL) |
| Gateway | 8080 | 8080 | API Gateway |
| API | 8080 | 8081 | REST API (72그룹, 2799 EP) |
| API (gRPC) | 9091 | 9091 | 내부 서비스 통신 |
| Receiver | 9008-9010 | 9008-9010 | 에이전트 데이터 수신 |
| Filerepo | 80 | 9011 | 에이전트 파일 배포 |
| PostgreSQL | 5432 | 5433 | 메타데이터 DB |
| ClickHouse | 8123/9000 | 8123/9000 | 시계열 DB |
| Redis | 6379 | 6379 | 캐시/세션 |
| Kafka | 9092 | 9092 | 메시지 큐 |

### 컨테이너 리소스 (Top 5 메모리)

| 컨테이너 | Limit | 실제 사용 |
|---------|-------|----------|
| exemone-clickhouse | 4C / 10GB | 3.6 GB |
| exemone-api | 2C / 10GB | 3.0 GB (HEAP 6GB) |
| exemone-kafka | 2C / 5GB | 1.3 GB (HEAP 1GB) |
| exemone-db-agent | 2C / 4GB | 926 MB |
| exemone-ingester | 2C / 4GB | 363 MB |

실제 총 사용량: ~10 GB (서버 32GB의 31%)

---

## 8. 웹 UI 메뉴 구조

```
├── HOME
├── Dashboard          # 커스텀 대시보드 (21종 위젯)
├── Alert              # 이벤트/규칙 알림
├── Application        # APM (WAS, Web Server, RUM, URL Monitoring)
├── Database           # DB 모니터링 (10종 DBMS)
├── Infrastructure     # Host, Container, Network Device
├── Kubernetes         # K8s 클러스터
├── Logs               # 키워드 로깅, Live Tail
├── Performance_Analysis  # 트랜잭션/DB 성능 분석
├── Business           # 비즈니스 모니터링
├── Report             # 리포트
├── Message_Queue      # MQ 모니터링
└── Setting            # 계정, 알림, 에이전트, 플랫폼 설정
```

### API 구조 (요약)

총 72개 API 그룹, 2799개 엔드포인트 (GET 1707, POST 654, PUT 32, PATCH 253, DELETE 153)

Swagger UI: `https://<서버>:8443/api/<그룹명>/swagger-ui/index.html`

---

## 9. Redis 키 패턴

| 키 패턴 | 용도 |
|---------|------|
| `keep-logged-in.*` | 로그인 유지 세션 |
| `eyJ...` (JWT) | 인증/리프레시 토큰 |
| `AgentState.*` | 에이전트 연결 상태 |
| `XmAgentState.<type>.*` | 에이전트 타입별 상태 |
| `ApplicationState.*` | 애플리케이션 상태 |
| `InstanceState.*` | DB 인스턴스 상태 |
| `InfraState.*` | 인프라 상태 |
| `ClusterState.*` | K8s 클러스터 상태 |
| `CustomMetricConfig.*` | 커스텀 메트릭 설정 |

---

## 10. Kafka 토픽 구조 (172개)

| 카테고리 | 토픽 패턴 | 용도 |
|---------|----------|------|
| Host | `host.*` | HostStatMsg, DiskStatMsg, ProcessInfo |
| Application | `application.*` | AppAgentJvmStatMessage, TxnDetailMsg |
| Oracle | `v2.oracle.*` | activesession, sqltextmsg, parametermsg |
| MySQL | `v2.mysql.*` | activesession, objectmsg |
| PostgreSQL | `v2.postgresql.*` | databasestat, sqlmessage |
| SQL Server | `v2.sqlserver.*` | querystatmsg, lockinfomsg |
| K8s | `kube.v2.*` | PodMessage, NodeMessage, Deployment |
| WebServer | `webserver.*` | WebActives, WebStatus |
| Alert | `alert.*` | AlertMetric |
| Event | `event.*` | LogEvent, SystemEvent |
| Custom | `custom_metric.*` | History |
