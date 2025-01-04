# 서비스 소개
사용자가 축제를 쉽고 직관적으로 예약하고, 참여자들과 정보를 공유하며 실시간 소통할 수 있는 종합 축제 예약 플랫폼


<br/>


## 서비스 주요 기능
1. 회원가입, 로그인
2. 축제 정보 제공
    - 축제 이름, 시작일과 종료일, 상세 설명 등 축제에 대한 전반적인 정보를 한눈에 확인할 수 있도록 제공
3. 축제 예약 
    - 원하는 축제를 쉽고 빠르게 예약할 수 있으며, 대기열 페이지를 통해 실시간으로 예약 순서를 확인할 수 있도록 구성
4. 채팅 시스템
    - 실시간으로 소통하며 정보를 공유할 수 있는 채팅 기능을 제공
5. 축제 검색
    - 최신 축제 정보를 탐색하거나 사용자가 입력한 키워드를 통해 원하는 축제를 빠르게 검색할 수 있도록 지원
6. 마이페이지
    - 사용자가 예약한 축제와 참여 중인 채팅방을 확인하고 관리할 수 있도록 지원
7. 축제 알림 
    - 예약한 축제가 시작되기 하루 전에 푸시 알림을 제공


<br/>


## 기술 스택
- Tech Stack
  - Spring Boot, Spring JPA, Spring Security, JWT, AWS Cognito, AWS SQS, Kafka, Stomp
- DB
  - AWS RDS(MySQL), AWS ElastiCache, DynamoDB
- DevOps
  - AWS EC2, AWS Application Load Balancer, AWS Code Deploy, Github Actions, Docker


<br/>


# 아키텍처 설계


<br/>


# Back-End
## ERD
<img src="https://github.com/user-attachments/assets/ce39f713-489f-46b5-b8aa-b778a05ecf27" />


<br/>


## 회원가입/로그인


<br/>


## 대기열 서비스


<br/>


## 채팅 시스템


<br/>


## 검색 기능: ElastiCache와 Redis를 활용한 성능 개선

### AWS ElastiCache란?
- AWS에서 제공하는 완전 관리형 캐싱 서비스
- Redis나 Memcached 같은 오픈소스 캐시 엔진을 기반으로 동작
- 데이터 접근 속도를 높이고 애플리케이션 성능을 향상시키는 데 최적화

<br/>

### 검색 동작 흐름
1. Redis에서 캐시 조회
    - 캐시된 검색 결과가 있으면 반환
2. 캐시에 검색 결과가 없는 경우
    - DB에서 검색 → 결과를 Redis에 캐싱한 뒤 반환

<br/>

### 캐시 설정 및 전략
- 캐싱 TTL(Time-To-Live): 10분
    - 최신 데이터를 유지하면서 불필요한 캐싱 비용 및 메모리 사용량을 줄이기 위함
- 캐시 무효화 전략
    - 새로운 축제 데이터 생성 시 관련 캐시를 삭제하여 DB 데이터와 캐시 데이터 간 일관성을 보장

<br/>

### 성능 테스트

- 테스트 환경
    - 100개의 축제 데이터를 대상으로 특정 키워드 검색 API를 100번 호출
- 결과
    - 캐시 적용 전: 약 0.91초
    - 캐시 적용 후: 약 0.63초
    - 성능 약 **0.28초** 개선
 
<br/>

<img src="https://github.com/user-attachments/assets/4b07ce6d-c1c4-4cc6-92af-c12d68949fb1">

<br/>


## 축제 알림

