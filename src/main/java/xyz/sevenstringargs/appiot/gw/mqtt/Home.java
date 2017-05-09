package xyz.sevenstringargs.appiot.gw.mqtt;

import com.ericsson.appiot.gateway.core.DefaultHomeDirectory;
import com.ericsson.appiot.gateway.dto.RegistrationTicket;
import com.google.gson.Gson;

public class Home  extends DefaultHomeDirectory {

    private RegistrationTicket registrationTicket;

    public Home(String ticket) {
        registrationTicket = new Gson().fromJson(ticket, RegistrationTicket.class);
    }

    @Override
    public RegistrationTicket getRegistrationTicket() {
        return registrationTicket;
    }

    @Override
    public void deleteRegistrationTicket(){
        registrationTicket = null;
    }

    @Override
    public void saveRegistrationTicket(RegistrationTicket registrationTicket){
        this.registrationTicket = registrationTicket;
    }
}
