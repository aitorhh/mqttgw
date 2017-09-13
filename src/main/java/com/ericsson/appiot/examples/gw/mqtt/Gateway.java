package com.ericsson.appiot.examples.gw.mqtt;

import com.ericsson.appiot.examples.gw.deviceregistry.couchdb.Registry;
import com.ericsson.appiot.gateway.AppIoTGateway;
import com.ericsson.appiot.gateway.GatewayException;
import com.ericsson.appiot.gateway.model.AppIoTModel;

import com.ericsson.appiot.gateway.device.DeviceAppIoTListener;
import com.ericsson.appiot.gateway.device.DeviceManager;
import com.ericsson.appiot.gateway.device.smartobject.resource.type.ResourceBase;
import com.ericsson.appiot.gateway.deviceregistry.DeviceRegistry;

import com.ericsson.appiot.gateway.dto.DeviceRegisterRequest;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Gateway extends DeviceAppIoTListener {

    // Start Up / Setup Plumbing ---------------------------------------------------------------------------------------

    public static void main(String[] args) throws com.ericsson.appiot.gateway.GatewayException{
        DeviceManager deviceManager = new DeviceManager();
        Home home = new Home(System.getenv(ENV_KEY_APPIOT_REGISTRATION_TICKET));

        String dbUrl = System.getenv(ENV_KEY_COUCHDB_URL);
        String dbUser = System.getenv(ENV_KEY_COUCHDB_USER);
        String dbPassword = System.getenv(ENV_KEY_COUCHDB_PASSWORD);

        Registry registry;
        if (dbUser != null && dbUser.length() > 0 && dbPassword != null && dbPassword.length() > 0) {
            registry = new Registry(home.getRegistrationTicket().getDataCollectorId(), dbUrl, dbUser, dbPassword);
        } else {
            registry = new Registry(home.getRegistrationTicket().getDataCollectorId(), dbUrl);
        }

        MqttRelay mqttRelay = new MqttRelay(
                deviceManager,
                System.getenv(ENV_KEY_APPIOT_MQTT_URL),
                System.getenv(ENV_KEY_APPIOT_MQTT_CLIENT_ID),
                System.getenv(ENV_KEY_APPIOT_MQTT_TOPIC_PREFIX),
                System.getenv(ENV_KEY_APPIOT_MQTT_USER),
                System.getenv(ENV_KEY_APPIOT_MQTT_PASSWORD)
        );

        Gateway gateway = new Gateway(home, registry, deviceManager, mqttRelay);
        gateway.run();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    // AppIoT Env Keys -------------------------------------------------------------------------------------------------

    private static final String ENV_KEY_APPIOT_REGISTRATION_TICKET = "APPIOT_REGISTRATION_TICKET";

    // -----------------------------------------------------------------------------------------------------------------

    // MQTT Env Keys ---------------------------------------------------------------------------------------------------

    private static final String ENV_KEY_APPIOT_MQTT_TOPIC_PREFIX = "APPIOT_MQTT_TOPIC_PREFIX";
    private static final String ENV_KEY_APPIOT_MQTT_URL = "APPIOT_MQTT_URL";
    private static final String ENV_KEY_APPIOT_MQTT_USER = "APPIOT_MQTT_USER";
    private static final String ENV_KEY_APPIOT_MQTT_PASSWORD = "APPIOT_MQTT_PASSWORD";
    private static final String ENV_KEY_APPIOT_MQTT_CLIENT_ID = "APPIOT_MQTT_CLIENT_ID";

    // -----------------------------------------------------------------------------------------------------------------

    // CouchDB Env Keys ------------------------------------------------------------------------------------------------

    private static final String ENV_KEY_COUCHDB_URL = "APPIOT_COUCHDB_URL";
    private static final String ENV_KEY_COUCHDB_USER = "APPIOT_COUCHDB_USER";
    private static final String ENV_KEY_COUCHDB_PASSWORD = "APPIOT_COUCHDB_PASSWORD";

    // -----------------------------------------------------------------------------------------------------------------

    // Gateway ---------------------------------------------------------------------------------------------------------

    private AppIoTGateway appIoTGateway;
    private MqttRelay mqttRelay;

    // -----------------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Gateway(Home home, DeviceRegistry registry, DeviceManager deviceManager, MqttRelay mqttRelay) {
        super(deviceManager);
        appIoTGateway = new AppIoTGateway(this);
        appIoTGateway.setHomeDirectory(home);
        appIoTGateway.setDeviceRegistry(registry);
        this.mqttRelay = mqttRelay;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Gateway ---------------------------------------------------------------------------------------------------------

    private void run()  {
        appIoTGateway.start();
        try {

            appIoTGateway.sendConfigurationRequest();
            AppIoTModel model = appIoTGateway.getAppIoTModel();
            logger.log(Level.INFO, String.format("model: %s", model));
            if(model.getObjectModels().size() == 0) {
                logger.log(Level.INFO, "No data model present. Requesting from AppIoT.");

                appIoTGateway.sendResourceModelRequest();
            }
        } catch (GatewayException e) {
            logger.log(Level.WARNING, "Failed to request settings or data model.", e);
        }
        getDeviceManager().getDevices().forEach(d -> d.getSmartObjects().forEach(s -> s.getResources().forEach(r -> ((ResourceBase)r).requestObserve())));
        mqttRelay.connect();
    }

    // -----------------------------------------------------------------------------------------------------------------

    // AppIoTDeviceListener --------------------------------------------------------------------------------------------

    @Override
    public void onDeviceRegisterRequest(String correlationId, String endpoint, DeviceRegisterRequest deviceRegisterRequest){
        super.onDeviceRegisterRequest(correlationId, endpoint, deviceRegisterRequest);
        getDeviceManager().getDevices().forEach(d -> d.getSmartObjects().forEach(s -> s.getResources().forEach(r -> ((ResourceBase)r).requestObserve())));
    }

    // -----------------------------------------------------------------------------------------------------------------
}
