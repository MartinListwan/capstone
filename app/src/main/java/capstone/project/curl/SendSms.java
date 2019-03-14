package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

import capstone.project.curl.StateAdapter.TextMessageModel;

public class SendSms {

    private final Activity activity;
    Queue<TextMessageModel> textMessageModelQueue = new LinkedList<>();

    public SendSms(Activity activity){
        this.activity = activity;
    }

    /**
     * Called externally when a fragment wants to send an SMS
     * @param phoneNo
     * @param textMessage
     * @return
     */
    public boolean sendSms(String phoneNo, String textMessage) {
        synchronized (this){
            textMessageModelQueue.add(new TextMessageModel(phoneNo,textMessage));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 0);
                return false;
            }
        } else {
            // Always granted permissions below 23
            sendSms();
        }
        return true;
    }

    /**
     * Starts runnables to send all of the text messages in our queue
     */
    private void sendSms(){
        synchronized (this){
            for (TextMessageModel textMessageModel : textMessageModelQueue){
                SendTextMessagesRunnable sendTextMessagesRunnable = new SendTextMessagesRunnable(activity, textMessageModel);
                new Thread(sendTextMessagesRunnable).start();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Permission granted", Toast.LENGTH_SHORT).show();
                    sendSms();
                } else {
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private class SendTextMessagesRunnable implements Runnable{
        private TextMessageModel textMessageModel;
        private int maximumRetryCount = 30;
        private final Activity activity;

        public SendTextMessagesRunnable(Activity activity, TextMessageModel textMessageModel){
            this.activity = activity;
            this.textMessageModel = textMessageModel;
        }

        @Override
        public void run() {
            while (!sendSms(textMessageModel) && maximumRetryCount-- > 0){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (maximumRetryCount <= 0){
                Toast.makeText(activity, "Couldn't send text message :(", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * You should check that isSMSPermissionGranted is true before sending an sms
         */
        public boolean sendSms(TextMessageModel textMessageModel) {
            try {
                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(textMessageModel.phoneNumber, null, textMessageModel.textMessage, null, null);
                return true;
            } catch (Exception ex) {
                Toast.makeText(activity, ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                return false;
            }
        }
    }
}
