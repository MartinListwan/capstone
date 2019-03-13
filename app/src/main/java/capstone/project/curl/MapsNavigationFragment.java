package capstone.project.curl;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsNavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsNavigationFragment extends Fragment {
    private static final String TAG = MapsNavigationFragment.class.getCanonicalName();
    private static String NAGIVATION_TEXT = "";
    private static final int defaultNavigationModeIndex = 0;
    private static int currentNavigationMode = defaultNavigationModeIndex;
    private List<NavigationMode> navigationModes;

    public MapsNavigationFragment() {
        // Required empty public constructor
    }

    public static MapsNavigationFragment newInstance() {
        MapsNavigationFragment fragment = new MapsNavigationFragment();
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
                //Toast.makeText(getActivity(), String.format("onEndTabSelected #%d", index), Toast.LENGTH_SHORT).show();
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
