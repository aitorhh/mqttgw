package xyz.sevenstringargs.appiot.gw.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.logging.Logger;


public class IpsoTopic {
    private static final int TOPIC_PARTS_MIN = 4;
    private static final int TOPIC_ENDPOINT_INDEX_REL = 0;
    private static final int TOPIC_OBJECT_INDEX_REL = 1;
    private static final int TOPIC_INSTANCE_INDEX_REL = 2;
    private static final int TOPIC_RESOURCE_INDEX_REL = 3;

    private static final Logger logger = Logger.getAnonymousLogger();

    private String endpoint;
    private int objectId;
    private int instanceId;
    private int resourceId;
    private Object payload;

    public static IpsoTopic parse(String topic, MqttMessage msg) {
        String[] topicParts = topic.split("/");
        if (topicParts.length < TOPIC_PARTS_MIN) {
            return null;
        }

        try {
            int startIndex = topicParts.length - TOPIC_PARTS_MIN;
            IpsoTopic ipsoTopic = new IpsoTopic();
            ipsoTopic.setEndpoint(topicParts[startIndex + TOPIC_ENDPOINT_INDEX_REL]);
            ipsoTopic.setObjectId(Integer.parseInt(topicParts[startIndex + TOPIC_OBJECT_INDEX_REL]));
            ipsoTopic.setInstanceId(Integer.parseInt(topicParts[startIndex + TOPIC_INSTANCE_INDEX_REL]));
            ipsoTopic.setResourceId(Integer.parseInt(topicParts[startIndex + TOPIC_RESOURCE_INDEX_REL]));
            return ipsoTopic;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return String.format("%s/%d/%d/%d", getEndpoint(), getObjectId(), getInstanceId(), getResourceId());
    }

}
