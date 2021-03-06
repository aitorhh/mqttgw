package com.ericsson.appiot.examples.gw.deviceregistry.couchdb;

import com.ericsson.appiot.examples.gw.deviceregistry.couchdb.dto.DeviceRegistrationDTO;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class Repository extends CouchDbRepositorySupport<DeviceRegistrationDTO> {
    public Repository(CouchDbConnector conn) {
        super(DeviceRegistrationDTO.class, conn);
    }
}
