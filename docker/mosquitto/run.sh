# Bind to persistent config file on host
# -v /media/sevenstringargs/Vertex3/server-apps/general/mosquitto/mosquitto.conf:/mosquitto/config/mosquitto.conf \

# Bind to peristent password file on host
# -v /media/sevenstringargs/Vertex3/server-apps/general/mosquitto/passwd:/etc/mosquitto/passwd \

sudo docker rm -f mosquitto > /dev/null 2>&1
sudo docker run \
  -p 1883:1883 \
  -p 9001:9001 \
  -d \
  --name mosquitto eclipse-mosquitto
