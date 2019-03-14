package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

import capstone.project.curl.Models.TextMessageModel;

public class SendSms {

    public static final String ANSWER_BOX = "answerbox";
    public static final String GOOGLE_MAPS = "directions";
    public static final String WEB_BROWSING = "smmry";


    public static final String[] PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
    };

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
    public boolean sendSms(String phoneNo, String textMessage, String featureType) {
        textMessage = featureType + "%" + textMessage;
        synchronized (this){
            textMessageModelQueue.add(new TextMessageModel(phoneNo,textMessage));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                showRequestPermissionsInfoAlertDialog();
                return false;
            }
        } else {
            // Always granted permissions below 23
            sendSms();
        }
        return true;
    }

    /**
     * Displays an AlertDialog explaining the user why the SMS permission is going to be requests
     */
    public void showRequestPermissionsInfoAlertDialog() {
        showRequestPermissionsInfoAlertDialog(true);
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("We need your permission to send & read texts!"); // Your own title
        builder.setMessage("We send text messages to get information from our server. Our server is" +
                " connected to the internet and it's how we provide services to you! :)"); // Your own message

        builder.setPositiveButton("Sounds good!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Display system runtime permission request?
                if (makeSystemRequest) {
                    if(!hasPermissions(activity, PERMISSIONS)){
                        ActivityCompat.requestPermissions(activity, PERMISSIONS, 0);
                    }
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Starts runnables to send all of the text messages in our queue
     */
    private void sendSms(){
        synchronized (this){
            while (textMessageModelQueue.size() != 0){
                TextMessageModel textMessageModel = textMessageModelQueue.poll();
                SendTextMessagesRunnable sendTextMessagesRunnable = new SendTextMessagesRunnable(activity, textMessageModel);
                new Thread(sendTextMessagesRunnable).start();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {

            case 0: { //  for Read SMS

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
                Log.d(SendSms.class.getName(), "Couldn't send the text message");
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
                Log.d(SendSms.class.getName(), ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
    }
}
