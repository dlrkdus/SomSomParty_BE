# 서비스 소개
사용자가 축제를 쉽고 직관적으로 예약하고, 참여자들과 정보를 공유하며 실시간 소통할 수 있는 종합 축제 예약 플랫폼

## 서비스 주요 기능
1. 회원가입, 로그인
2. 축제 정보 제공
3. 축제 예약 
4. 채팅 시스템
5. 축제 검색
6. 마이페이지
7. 축제 알림 


<br/>


## 기술 스택
- Tech Stack
  - Spring Boot, Spring JPA, Apache Kafka, Redis, Stomp, FCM, AWS EventBridge, AWS SNS, AWS SQS
- DB
  - AWS RDS(MySQL), AWS ElastiCache, DynamoDB
- Infra
  - AWS EC2 Auto Scaling, AWS ALB, AWS VPC
- DevOps
  - AWS Code Deploy, AWS ECR, AWS S3, Github Actions, Docker
 
## 담당 업무 
- 아키텍처 설계
- 인프라 프로비저닝
- CI/CD 파이프라인 구축
- 채팅 시스템 구현


<br/>

## ERD
<img src="https://github.com/user-attachments/assets/ce39f713-489f-46b5-b8aa-b778a05ecf27" />

<br/>


# 아키텍처 설계
![image](https://github.com/user-attachments/assets/bcd3da7f-7b91-4f23-93d5-1734c79c6f40)

- 오토스케일링 그룹과 ALB를 활용해 고가용성과 확장성을 지닌 아키텍처 구성
- EC2, RDS, ElasticCache 와 같은 리소스는 멀티 AZ 배포를 해 단일 장애점을 방지
- 데이터베이스와 브로커는 EC2와 다른 프라이빗 서브넷에 배치함으로써 EC2로만 접근이 가능해 보안이 향상


<br/>


# CI/CD
## Back-End
![image](https://github.com/user-attachments/assets/9e189e86-3002-415f-916c-6c47c2917f64)
![image](https://github.com/user-attachments/assets/06a1b010-6010-460a-b3ee-1c4803f59ffd)

- Github Action, AWS ECR, Code Deploy를 활용한 Blue/Green 무중단 배포 파이프라인 구성
- Blue/Green이란 대체 인스턴스(Green)를 생성해 배포하는 과정에서 원본 인스턴스(Blue)를 유지하고, 배포가 완료되면 트래픽을 전환한 후 Blue 그룹을 안전히 제거함으로써 배포 과정에서 서버가 중단되지 않는 배포 방식
- 여러 명이 동시에 작업 중일 때도 배포로 인해 서버가 중단되지 않아 개발 효율이 증진됨

<br/>


# 채팅 시스템

## Kafka & Stomp & DynamoDB & Redis 기반 채팅 시스템
![image](https://github.com/user-attachments/assets/ca1763a3-d521-4525-9da0-22dbe8be6b0f)

분산 서버 환경에서 채팅 시스템의 요구사항은 다음과 같습니다. 

> 1. 실시간으로 메세지를 송수신 해야 합니다.
> 2. 메세지의 정확성을 보장 해야 합니다. 
> 3. 대규모 데이터인 메세지를 빠르게 로드 해와야 합니다.

이 3가지 조건을 각각 다음과 같은 구성으로 만족시켰습니다.

## 1. 실시간 메세지 송수신

![솜솜파티 실시간채팅GIF](https://github.com/user-attachments/assets/e7b3333c-83f3-45d8-a0e0-f50c17e7221e)


- 채팅에 자주 사용되는 STOMP를 사용해 구현했습니다. 

- 기본적으로 STOMP는 내장 메세지 브로커를 사용해 메세지 상태(순서, 채팅방 정보 등)을 서버 메모리에 저장합니다.

- 그러나 분산 서버 환경에선 서버 간 구독 정보가 공유되지 않기 때문에 내장 메세지 브로커를 사용할 수 없습니다. 따라서 분산 서버 환경에선 **중앙 집중 메세지 브로커**가 필요합니다.

## 2. [메세지 정확성 보장 & 병렬 처리로 처리량 극대화](https://velog.io/@dlrkdus/StompKafka-%EA%B8%B0%EB%B0%98-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B2%84-%EA%B5%AC%EC%B6%95-1-%EC%96%91%EB%B0%A9%ED%96%A5-%ED%86%B5%EC%8B%A0-%EA%B5%AC%ED%98%84-6ce00xxv)

- 분산 서버 환경에서 메세지의 순서, 채팅방 등 정확성을 보장하기 위해 중앙 집중 메세지 브로커인 카프카를 도입했습니다.
  
- STOMP의 메세지 브로커 역할을 카프카가 대신함으로써 분산된 서버의 각 사용자들에게 정확하게 메세지를 전달할 수 있게 되었습니다.
  
- 또한, 카프카의 특성인 파티션과 컨슈머 그룹을 이용해 메세지를 병렬로 처리하기 때문에 대규모 트래픽을 안정적으로 처리할 수 있게 되었습니다.
  
- **추가적으로, DB 저장용 컨슈머 그룹과 Websocker 전달용 컨슈머 그룹을 분리해 DB 저장 과정에서 병목 현상이 발생해도, 사용자가 실시간으로 메세지를 전달 받는 데 영향을 끼치지 않게 설계했습니다.**

## 3.  [대규모 데이터 로드](https://velog.io/@dlrkdus/StompKafka-%EA%B8%B0%EB%B0%98-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B2%84-%EA%B5%AC%EC%B6%95-2-Redis%EC%99%80-NoSQL-%EA%B5%AC%ED%98%84)

![솜솜파티 무한스크롤GIF](https://github.com/user-attachments/assets/76fd8e91-3e8b-4e58-9d18-c02255ba92d0)


- 채팅 메세지 내역은 대규모 데이터이기 때문에 RDS만으로는 빠른 처리가 불가하다고 판단했습니다.
  
- 따라서 Key-Value 기반의 DynamoDB에 메세지 데이터를 저장하여 빠르게 데이터를 로드해오게 구현하였습니다. 

## + 추가 기능 구현

![솜솜파티 채팅알림GIF](https://github.com/user-attachments/assets/a68216a8-ae4e-4ccd-a4dc-c86b8fe222c3)

- 읽지 않은 메세지 알림을 구현하였습니다.
  
- 이를 위해 채팅방 온라인 유저와 오프라인 유저를 구별해야 했는데, 이는 채팅방 입장/퇴장 시마다 호출되는 메서드이어야 하므로 사용자 경험을 위해선 빠른 처리와 RDS 부하 감소가 필수였습니다.
  
- 따라서 Redis가 온라인/오프라인 유저 관리와 알림 개수 계산을 담당하게 구현하였습니다.

