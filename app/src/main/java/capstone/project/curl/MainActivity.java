package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import devlight.io.library.ntb.NavigationTabBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements SmsBroadcastReceiver.SmsOnReceiveListener {

    private SendSms sendSms;
    private FragmentCurlPagerAdapter pageAdapter;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private List<SmsBroadcastReceiver.SmsOnReceiveListener> fragments = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_viewpager);
        fragments = new ArrayList<>();
        sendSms = new SendSms(this);
        Fragment fragment = MapsNavigationFragment.newInstance(sendSms, new GpsLocationTracker(this,this));
        fragment.setRetainInstance(true);
        fragments.add((SmsBroadcastReceiver.SmsOnReceiveListener) fragment);
        fragment = GoogleQuickAnswerFragment.newInstance(sendSms);
        fragment.setRetainInstance(true);
        fragments.add((SmsBroadcastReceiver.SmsOnReceiveListener) fragment);
        fragment = WebBrowserFragment.newInstance(sendSms);
        fragment.setRetainInstance(true);
        fragments.add((SmsBroadcastReceiver.SmsOnReceiveListener) fragment);
        initUI();
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setSmsOnReceiveListener(this);
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        pageAdapter = new FragmentCurlPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pageAdapter);

        // Setup Horizontal Navigation bar at the bottomn
        final String[] colors = getResources().getStringArray(R.array.default_preview);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.location_icon),
                        Color.parseColor(colors[0]))
                        .title("Google Navigation")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Quick Answer")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Website SMMRY")
                        .badgeTitle("state")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        ExtendedViewPagerListener extendedViewPagerListener = new ExtendedViewPagerListener(this,navigationTabBar);
        navigationTabBar.setOnPageChangeListener(extendedViewPagerListener);

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }

    // Todo DELETE THIS
    public static String exampleMapText = "start_address:Paperbirch Crescent, London, ON N6G 1L7, Canada|end_address:Masonville Place Stop #1 - #1140, London, ON N6G 2N2, Canada|duration:55 mins|Walk to Masonville Place Stop #1 - #1140, London, ON N6G 2N2, Canada%55 mins%4.5 km|Head east on Paperbirch Crescent toward Rippleton Rd%1 min%4.5 km|Turn left onto Rippleton Rd%2 mins%87 m|Turn right onto Sarnia Rd%16 mins%0.2 km|Turn left%1 min%1.3 km|Turn right%1 min%24 m|Turn right toward Western Rd%1 min%0.1 km|Turn left toward Western Rd%1 min%6 m|Turn right toward Western Rd%1 min%85 m|Turn left onto Western Rd%23 mins%13 m|Slight left toward Richmond St%2 mins%1.9 km|Turn left onto Richmond St%7 mins%0.1 km|Turn right at Hillview Blvd%1 min%0.5 km|Turn left%1 min%63 m|";
    @Override
    public void onTextReceived(String text) {
        if (getWhichUseCaseTheDataWasFor(text) == 0){
            fragments.get(0).onTextReceived(text);
        } else if (getWhichUseCaseTheDataWasFor(text) == 1){
            fragments.get(1).onTextReceived(text);
        } else if (getWhichUseCaseTheDataWasFor(text) == 2){
            fragments.get(2).onTextReceived(text);
        }
    }

    private int getWhichUseCaseTheDataWasFor(String textMessage){
        if (textMessage.contains("feature:" + SendSms.GOOGLE_MAPS)){
            return 0;
        } else if (textMessage.contains("feature:"+ SendSms.ANSWER_BOX) || textMessage.contains("No relevent Search Results")){
            return 1;
        } else if (textMessage.contains("smmry") || textMessage.contains("Sent from your Twilio trial account - x")){
            return 2;
        } else {
            Log.d("Martin unkown feature", textMessage);
        }
        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smsBroadcastReceiver!=null)
        {
            unregisterReceiver(smsBroadcastReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        sendSms.onRequestPermissionsResult(requestCode,grantResults);
        Log.d("MArtin", "check if result code is 1" + requestCode);
    }

    /* PagerAdapter class */
    public class FragmentCurlPagerAdapter extends FragmentPagerAdapter {
        private final Activity activity;
        public FragmentCurlPagerAdapter(FragmentManager fm, Activity activity) {
            super(fm);
            this.activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            // Hide the keyboard
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            return (Fragment) fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class ExtendedViewPagerListener implements ViewPager.OnPageChangeListener {
        private final Activity activity;
        private final NavigationTabBar navigationTabBar;

        public ExtendedViewPagerListener(Activity activity, NavigationTabBar navigationTabBar){
            super();
            this.activity = activity;
            this.navigationTabBar = navigationTabBar;
        }
        @Override
        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            navigationTabBar.getModels().get(position).hideBadge();
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(INPUT_METHOD_SERVICE);
            // Hide the keyboard
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }
}
