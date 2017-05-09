#!/bin/bash

echo 'Building project'
mvn compile assembly:single
if [ $? -ne 0 ]; then
  echo 'Failed to build, exiting ...'
  exit 1
fi
echo 'Build complete'

echo 'Cleaning up'
rm appiot-mqtt.jar > /dev/null 2>&1
mv target/gw-mqtt-1.0-SNAPSHOT-jar-with-dependencies.jar appiot-mqtt.jar
cp appiot-mqtt.jar docker/appiot-mqtt/
rm -rf target > /dev/null 2>&1
echo 'Done'
