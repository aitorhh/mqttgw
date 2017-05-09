# AppIoT | MQTT Proxy Gateway

This is an AppIoT soft-gateway that will poll Open Weather Map based on its registered devices. The gateway uses CouchDB as backing storage for its device registry and the project includes instructions for running the database and gateway in docker containers. Multiple gateways can use the same CouchDB instance as long as the gateways are registered in the same device network.

## AppIoT Setup

* Create a new gateway of any type or use a pre-existing gateway.
* Download the gateway's registration ticket and save for it for later.

## Server Setup

###### Build the MQTT-gateway
``` bash
$ ./build.sh

``` 
###### Run CouchDB with Docker
```bash
# Change directory to docker/couchdb
cd docker/couchdb

# Run CouchDB. To configure the container see comments in the run.sh file.
./run.sh
```
###### Run CouchDB with Docker
```bash
# Change directory to docker/mosquitto
cd docker/mosquitto

# Run Mosquitto. To configure the container see comments in the run.sh file.
./run.sh
```
###### Run without Docker
```bash
# Create a config file based on the config/example file.
# Run the run.sh script with your config file as the first argument

./run.sh config/your-config
```
###### Run with Docker

```bash
# Change directory to docker/appiot-mqtt
cd docker/appiot-mqtt

# Build the docker image
./build.sh

# Create a config file based on the docker/appiot-mqtt/config/example
# Run the run.sh script with your config file as the first argument
./run.sh config/your-docker-config
```
