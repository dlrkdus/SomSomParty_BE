#!/bin/bash

KAFKA_BROKER="kafka:9092"
TOPIC="chat-topic"

kafka-topics.sh --create \
    --bootstrap-server $KAFKA_BROKER \
    --replication-factor 1 \
    --partitions 3 \
    --topic $TOPIC