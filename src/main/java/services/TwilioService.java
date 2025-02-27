package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {
    public static final String ACCOUNT_SID = "AC793093cbcaeb2c5b55cee45eaeb038a9";
    public static final String AUTH_TOKEN = "5628750d408e9045c14270a416b00679";
    private static final String TWILIO_PHONE_NUMBER = "+16562282650";

    public TwilioService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String to, String message) {
        PhoneNumber toNumber = new PhoneNumber(to);
        PhoneNumber fromNumber = new PhoneNumber(TWILIO_PHONE_NUMBER);
        Message.creator(toNumber, fromNumber, message).create();
    }
}
