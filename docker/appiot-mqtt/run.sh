#!/bin/bash

if [ $# -eq 0 ]; then
  echo 'Missing config file path'
  exit 1
fi

source $1

if [ -z $APPIOT_CONTAINER_NAME ]; then
  echo 'Missing container name'
  exit 1
fi

if [[ -z $APPIOT_REGISTRATION_TICKET ]]; then
  echo 'Missing registration ticket'
  exit 1
fi

if [ -z $APPIOT_COUCHDB_URL ]; then
  echo 'Missing CouchDB URL'
  exit 1
fi

if [ -z $APPIOT_MQTT_URL ]; then
  echo 'Missing MQTT URL'
  exit 1
fi

if [ -z $APPIOT_MQTT_CLIENT_ID ]; then
  echo 'Missing MQTT Client ID'
  exit 1
fi

docker rm -f "appiot-mqtt-$APPIOT_CONTAINER_NAME" > /dev/null 2>&1
docker run \
  -e APPIOT_REGISTRATION_TICKET="$APPIOT_REGISTRATION_TICKET" \
  -e APPIOT_COUCHDB_URL=$APPIOT_COUCHDB_URL \
  -e APPIOT_COUCHDB_USER=$APPIOT_COUCHDB_USER \
  -e APPIOT_COUCHDB_PASSWORD=$APPIOT_COUCHDB_PASSWORD \
  -e APPIOT_MQTT_URL=$APPIOT_MQTT_URL \
  -e APPIOT_MQTT_CLIENT_ID=$APPIOT_MQTT_CLIENT_ID \
  -e APPIOT_MQTT_TOPIC_PREFIX=$APPIOT_MQTT_TOPIC_PREFIX \
  -e APPIOT_MQTT_USER=$APPIOT_MQTT_USER \
  -e APPIOT_MQTT_PASSWORD=$APPIOT_MQTT_PASSWORD \
  -d \
  --name "appiot-mqtt-$APPIOT_CONTAINER_NAME" appiot-mqtt

