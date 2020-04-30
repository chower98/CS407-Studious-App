package com.example.studious;

import android.telephony.SmsManager;

public class SendIntroSMS {

    public boolean send(String sender, String receiver, String phone) {
        String message = "Hello " + receiver + ", my name is " + sender
                + "! We were matched on Studious and I would love to study with you!";
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            return true; //returns if message sent successfully
        } catch (Exception e) {
            return false; //returns if message sen unsuccessfully
        }
    }
}
