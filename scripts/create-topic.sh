#!/bin/bash

KAFKA_BROKER="kafka:9092"
TOPIC="chat-topic"

# Kafka가 응답하는지 확인하기 위한 반복문
until docker exec kafka kafka-topics.sh --list --bootstrap-server $KAFKA_BROKER; do
  echo "Waiting for Kafka to be ready..."
  sleep 5
done

# Kafka가 준비되었으면, 토픽 생성
docker exec kafka kafka-topics.sh --create \
    --bootstrap-server $KAFKA_BROKER \
    --replication-factor 1 \
    --partitions 3 \
    --topic $TOPIC

echo "Topic '$TOPIC' created."