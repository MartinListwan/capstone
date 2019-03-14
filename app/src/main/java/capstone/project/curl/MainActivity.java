package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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

public class MainActivity extends FragmentActivity {

    private SendSms sendSms;
    private FragmentCurlPagerAdapter pageAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_viewpager);
        initUI();
        sendSms = new SendSms(this);
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
                        .title("Web Browser")
                        .badgeTitle("state")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);
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
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
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

            switch(position) {
                case 0: return MapsNavigationFragment.newInstance(sendSms);
                case 1: return GoogleQuickAnswerFragment.newInstance();
                case 2: return WebBrowserFragment.newInstance("ThirdFragment, Instance 1", "Random");
                default: return WebBrowserFragment.newInstance("Shouldn't exist", "shouldn't exist");
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        sendSms.onRequestPermissionsResult(requestCode,grantResults);
    }
}
