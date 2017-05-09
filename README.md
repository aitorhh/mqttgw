# AppIoT | MQTT Proxy Gateway

This is an AppIoT soft-gateway that will subscribe to a MQTT broker and forward the payload to AppIoT based on the devices registered on the gateway. The gateway uses CouchDB as backing storage for its device registry and the project includes instructions for running the database, the Mosquitto broker and the gateway in docker containers. Multiple gateways can use the same CouchDB instance as long as the gateways are registered in the same device network. 

The gateway will assume that the topics are formatted like this: ENDPOINT/OBJECT_ID/INSTANCE_ID/RESOURCE_ID.
There is also an option to add a prefix to the subscribed topic, see config files for more examples.
The gateway will also assume the payload is in a string format and will try to parse the string to the correct data type based on the IPSO definition.

---

When you've succesfully setup the MQTT gateway you should be able to publish the following on the broker.
```
TOPIC: my-device-endpoint/3303/0/5700
PAYLOAD: 13.02
```
If you have a device registered in AppIoT with the device endpoint of 'my-device-endpoint' with a temperature smart object it should now have the sensor value(5700) of 13.02.

## AppIoT Setup

* Create a new gateway of any type or use a pre-existing gateway.
* Download the gateway's registration ticket and save for it for later.
* Register any number of devices on the gateway.

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
