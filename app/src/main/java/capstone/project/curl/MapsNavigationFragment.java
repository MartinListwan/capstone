package capstone.project.curl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import capstone.project.curl.Models.MapsApi.NavigationModel;
import capstone.project.curl.StateAdapter.DirectionsAdapter;
import capstone.project.curl.StateAdapter.RecyclerViewStateAdapter;
import devlight.io.library.ntb.NavigationTabBar;
public class MapsNavigationFragment extends Fragment implements SmsBroadcastReceiver.SmsOnReceiveListener {
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
    public static String origin = "ORIGIN";
    public static String destinaiton = "DESTINATION";
    public static String model = NavigationMode.class.getCanonicalName();
    public static String stateAdapterState = "STATE_ADAPTER_STATE";
    public static int adapaterState = RecyclerViewStateAdapter.STATE_EMPTY;
    EditText originText;
    private static NavigationModel navigationModel = new NavigationModel("");
    EditText destinationText;
    private GpsLocationTracker gpsLocationTracker;
    private ImageView useGpsImage = null;

    public MapsNavigationFragment() {
        // Required empty public constructor
    }

    public static MapsNavigationFragment newInstance(SendSms sendSms, GpsLocationTracker mGpsLocationTracker) {
        MapsNavigationFragment fragment = new MapsNavigationFragment();
        fragment.sendSms = sendSms;
        fragment.gpsLocationTracker = mGpsLocationTracker;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationModes = new ArrayList<>();
        navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_walk_black_48dp, "walking"));
        navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_bike_black_48dp, "bicycling"));
        navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_car_black_48dp, "driving"));
        navigationModes.add(
                new NavigationMode(R.drawable.outline_directions_bus_black_48dp, "transit"));
        NAGIVATION_TEXT = navigationModes.get(defaultNavigationModeIndex).name;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        outState.putString(origin, originText.getText().toString());
        outState.putString(destinaiton, destinationText.getText().toString());
        outState.putInt(stateAdapterState, adapaterState);
        outState.putParcelable(model,navigationModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.maps_navigation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: LOAD THE DATA

        // Setup swap image view
        final ImageButton imageview = (ImageButton) getView().findViewById(R.id.swapButton);
        originText = (EditText) getView().findViewById(R.id.your_location_text);
        destinationText = (EditText) getView().findViewById(R.id.choose_destination_text);
        useGpsImage = (ImageView) getView().findViewById(R.id.imageView);
        if (!navigationModel.errorWhileParsing){
            adapaterState = RecyclerViewStateAdapter.STATE_NORMAL;
        }
        useGpsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (gpsLocationTracker.hasPermission()){
                        Toast.makeText(getActivity(), "Need location permissions", Toast.LENGTH_SHORT).show();
                        gpsLocationTracker.requestPermissions();
                    } else {
                        Location location = gpsLocationTracker.getLocation();
                        if (location != null){
                            originText.setText(location.getLatitude() + " " + location.getLongitude());
                        }
                    }
                } catch (Exception e){
                    Log.d("Martin", "Exception while getting location");
                }
            }
        });

        if (savedInstanceState != null){
            //Toast.makeText(getActivity(), "Info loaded", Toast.LENGTH_SHORT).show();
            // Origin text
            // Destination text
            // Mode
            originText.setText(savedInstanceState.getString(origin));
            destinationText.setText(savedInstanceState.getString(destinaiton));
            adapaterState = savedInstanceState.getInt(stateAdapterState);
            navigationModel = savedInstanceState.getParcelable(model);
        }

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
        // TODO: remove hard code
        ((TextView)(errorView.findViewById(R.id.textView2))).setText("We won't be able to provide any functionality until you give us SMS permissions");
        ((TextView)(errorView.findViewById(R.id.textView))).setText("Why no permissions?");
        // TODO: Do this in XML or do both programmatically
        ImageView imageView= (ImageView) errorView.findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.error);

        directionsAdapter = new DirectionsAdapter(navigationModel);
        recyclerViewStateAdapter = new RecyclerViewStateAdapter(directionsAdapter, loadingView, emptyView, errorView);
        rv.setAdapter(recyclerViewStateAdapter);
        recyclerViewStateAdapter.setState(adapaterState);
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
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                navigationModels.get(0).setBadgeTitle("We have directions!");
            }
        });

        final EditText chooseDestinationText = (EditText) getView().findViewById(R.id.choose_destination_text);

        chooseDestinationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // After the search button is pressed in the soft keyboard while typing in editText
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String mode = navigationModes.get(currentNavigationMode).name;
                    //Toast.makeText(getActivity(), String.format("Search started " + navigationModes.get(currentNavigationMode).name), Toast.LENGTH_SHORT).show();

                    String origin = originText.getText().toString();
                    String dest = destinationText.getText().toString();
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    if (origin.trim().equals("") || dest.trim().equals("")){
                        Toast.makeText(getActivity(), String.format("Neither origin or destination can be empty"), Toast.LENGTH_LONG).show();
                        recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_EMPTY);
                        return true;
                    }
                    recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_LOADING);

                    if (SendSms.hasPermissions(getContext(), SendSms.PERMISSIONS)){
                        String message = mode + "%" + origin + "%"  + dest;
                         sendSms.sendSms("2264065956", message, SendSms.GOOGLE_MAPS);
                        //sendSms.sendSms("6474724006", message, SendSms.GOOGLE_MAPS);

                    } else {
                        recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_ERROR);
                        sendSms.showRequestPermissionsInfoAlertDialog();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onTextReceived(String text) {
        // TODO: Add the result of the directions
        navigationModel = MapsSmsParser.ParseSmsForNavigation(text);
        directionsAdapter.setNavigationModel(navigationModel);
        if (!navigationModel.errorWhileParsing){
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_NORMAL);
        } else {
            Toast.makeText(getActivity(), String.format("Problem retrieving directions, please retry :)"), Toast.LENGTH_LONG).show();
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_EMPTY);
        }
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
