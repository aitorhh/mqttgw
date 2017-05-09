package xyz.sevenstringargs.appiot.gw.mqtt;

import com.ericsson.appiot.gateway.device.Device;
import com.ericsson.appiot.gateway.device.DeviceManager;
import com.ericsson.appiot.gateway.device.smartobject.SmartObject;
import com.ericsson.appiot.gateway.device.smartobject.resource.Resource;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.Logger;

public class MqttRelay implements MqttCallback {

    // MQTT ------------------------------------------------------------------------------------------------------------

    private String url;
    private String clientId;
    private String topicPrefix;
    private String user;
    private String password;
    private MqttClient client;

    // -----------------------------------------------------------------------------------------------------------------

    // AppIoT ----------------------------------------------------------------------------------------------------------

    private DeviceManager manager;

    // -----------------------------------------------------------------------------------------------------------------

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    // Constructors ----------------------------------------------------------------------------------------------------

    public MqttRelay(DeviceManager manager, String url, String clientId, String topicPrefix, String user, String password) {
        this.manager = manager;
        this.url = url;
        this.clientId = clientId;
        this.topicPrefix = topicPrefix;
        this.user = user;
        this.password = password;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // MQTT ------------------------------------------------------------------------------------------------------------

    public void connect() throws MqttException {
        MqttConnectOptions connOpt = new MqttConnectOptions();

        if (user != null && user.length() > 0) {
            connOpt.setUserName(user);
        }

        if (password != null && password.length() > 0) {
            connOpt.setPassword(password.toCharArray());
        }

        connOpt.setCleanSession(true);
        connOpt.setAutomaticReconnect(true);

        client = new MqttClient(url, clientId, new MemoryPersistence());
        client.setCallback(this);
        client.connect(connOpt);

        String topic = "+/+/+/+";
        if (topicPrefix != null && topicPrefix.length() > 0) {
            topic = topicPrefix + topic;
        }

        client.subscribe(topic);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        handleMessage(s, mqttMessage);
    }

    @Override
    public void connectionLost(Throwable throwable) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken){}

    // -----------------------------------------------------------------------------------------------------------------

    // AppIoT ----------------------------------------------------------------------------------------------------------

    private void handleMessage(String s, MqttMessage mqttMessage) {
        IpsoTopic ipsoTopic = IpsoTopic.parse(s, mqttMessage);
        if (ipsoTopic == null) {
            logger.warning(String.format("Failed to parse mqtt message %s -> %s", s, new String(mqttMessage.getPayload())));
            return;
        }

        Device device = manager.getDevice(ipsoTopic.getEndpoint());
        if (device == null) {
            logger.warning(String.format("Missing device %s", ipsoTopic.getEndpoint()));
            return;
        }

        SmartObject smartObject = device.getSmartObjectInstance(ipsoTopic.getObjectId(), ipsoTopic.getInstanceId());
        if (smartObject == null) {
            logger.warning(String.format("Missing instance %d of smart object %d", ipsoTopic.getInstanceId(), ipsoTopic.getObjectId()));
            return;
        }

        Resource resource = smartObject.getResource(ipsoTopic.getResourceId());
        if (resource == null) {
            logger.warning(String.format("Missing resource %d", ipsoTopic.getResourceId()));
            return;
        }

        String payload = new String(mqttMessage.getPayload());
        if (payload.length() < 1) {
            logger.warning(String.format("Empty payload for %s", ipsoTopic));
            return;
        }

        try {
            switch (resource.getResourceModel().getType()) {
                case BOOLEAN:
                    ipsoTopic.setPayload(Boolean.parseBoolean(payload));
                    break;
                case FLOAT:
                    ipsoTopic.setPayload(Float.parseFloat(payload));
                    break;
                case INTEGER:
                    ipsoTopic.setPayload(Integer.parseInt(payload));
                    break;
                case STRING:
                    ipsoTopic.setPayload(payload);
                    break;
                case OBJLNK:
                    logger.warning("Not implemented");
                    return;
                case TIME:
                    logger.warning("Not implemented");
                    return;
                case OPAQUE:
                    ipsoTopic.setPayload(payload.getBytes());
                    break;
                default:
                    logger.warning(String.format("%s is not a supported data type", resource.getResourceModel().getType()));
                    return;
            }

            logger.info(String.format("Sent data | %s -> %s", ipsoTopic.toString(), ipsoTopic.getPayload()));
            device.onResourceValueChanged(resource, ipsoTopic.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
}
