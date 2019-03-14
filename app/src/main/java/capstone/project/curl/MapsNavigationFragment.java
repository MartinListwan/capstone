package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import capstone.project.curl.Models.Directions;
import capstone.project.curl.StateAdapter.DirectionsAdapter;
import capstone.project.curl.StateAdapter.RecyclerViewStateAdapter;
import devlight.io.library.ntb.NavigationTabBar;
public class MapsNavigationFragment extends Fragment {
    private static final String TAG = MapsNavigationFragment.class.getCanonicalName();
    private static String NAGIVATION_TEXT = "";
    private static final int defaultNavigationModeIndex = 0;
    private static int currentNavigationMode = defaultNavigationModeIndex;
    private List<NavigationMode> navigationModes;
    private View loadingView;
    private View errorView;
    private View emptyView;
    private DirectionsAdapter directionsAdapter;
    private RecyclerViewStateAdapter recyclerViewStateAdapter;
    private SendSms sendSms;

    public MapsNavigationFragment() {
        // Required empty public constructor
    }

    public static MapsNavigationFragment newInstance(SendSms sendSms) {
        MapsNavigationFragment fragment = new MapsNavigationFragment();
        fragment.sendSms = sendSms;
        fragment.navigationModes = new ArrayList<>();
        fragment.navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_walk_black_48dp, "walking"));
        fragment.navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_bike_black_48dp, "bike"));
        fragment.navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_car_black_48dp, "car"));
        fragment.navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_bus_black_48dp, "bus"));
        fragment.navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_railway_black_48dp, "railway"));
        NAGIVATION_TEXT = fragment.navigationModes.get(defaultNavigationModeIndex).name;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.maps_navigation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Setup swap image view
        final ImageButton imageview = (ImageButton) getView().findViewById(R.id.swapButton);
        final EditText originText = (EditText) getView().findViewById(R.id.your_location_text);
        final EditText destinationText = (EditText) getView().findViewById(R.id.choose_destination_text);;
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textTemp = originText.getText().toString();
                originText.setText(destinationText.getText().toString());
                destinationText.setText(textTemp);
            }
        });

        // Setup directions recycler view
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.directionsRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        loadingView = getLayoutInflater().inflate(R.layout.loading, rv, false); // No loading view
        emptyView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false);
        errorView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false); // no empty state for now
        ArrayList<String> tempDirections = new ArrayList<>();
        tempDirections.add("go right");
        tempDirections.add("go righasd sad sad sad sa dsa dsat");
        tempDirections.add("go asdsad sadnsajdknsakd sadkhs adsa d sad sa ");
        tempDirections.add("go right");
        tempDirections.add("go righasd sad sad sad sa dsa dsat");
        tempDirections.add("go asdsad sadnsajdknsakd sadkhs adsa d sad sa ");
        tempDirections.add("go right");
        tempDirections.add("go righasd sad sad sad sa dsa dsat");
        tempDirections.add("go asdsad sadnsajdknsakd sadkhs adsa d sad sa ");
        tempDirections.add("go right");
        tempDirections.add("go righasd sad sad sad sa dsa dsat");
        tempDirections.add("go asdsad sadnsajdknsakd sadkhs adsa d sad sa ");
        Directions directions = new Directions(true,tempDirections);
        directionsAdapter = new DirectionsAdapter(directions);
        recyclerViewStateAdapter = new RecyclerViewStateAdapter(directionsAdapter, loadingView, emptyView, errorView);
        rv.setAdapter(recyclerViewStateAdapter);
        recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_EMPTY);
        // rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Setup modes of transportation navigation bar
        final NavigationTabBar navigationTabBar = (NavigationTabBar) getView().findViewById(R.id.navigation_mode_bar);
        final ArrayList<NavigationTabBar.Model> navigationModels = new ArrayList<>();
        for (NavigationMode mode : navigationModes){
            navigationModels.add(
                    new NavigationTabBar.Model.Builder(
                            getResources().getDrawable(mode.resourceId), Color.WHITE
                    ).build()
            );
        }

        navigationTabBar.setModels(navigationModels);
        navigationTabBar.setModelIndex(0, true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
                Toast.makeText(getActivity(), String.format(navigationModes.get(index).name + " Selected"), Toast.LENGTH_SHORT).show();
                currentNavigationMode = index;
                recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_NORMAL);
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                //Toast.makeText(getActivity(), String.format("onEndTabSelected #%d", index), Toast.LENGTH_SHORT).show();
                navigationModels.get(0).setBadgeTitle("We have directions!");
            }
        });

        final EditText chooseDestinationText = (EditText) getView().findViewById(R.id.choose_destination_text);

        chooseDestinationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // After the search button is pressed in the soft keyboard while typing in editText
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(getActivity(), String.format("Search started " + navigationModes.get(currentNavigationMode).name), Toast.LENGTH_SHORT).show();

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_LOADING);

                    sendSms.sendSms("6474724006", navigationModes.get(currentNavigationMode).name + " " + originText.getText().toString() + " " + destinationText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private static class NavigationMode {
        public int resourceId = 0;
        public String name = "";
        public NavigationMode(int resourceID, String name){
            this.resourceId = resourceID;
            this.name = name;
        }
    }
}
