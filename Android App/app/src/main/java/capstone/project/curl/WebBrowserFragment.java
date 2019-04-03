package capstone.project.curl;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capstone.project.curl.StateAdapter.QuickAnswerAdapter;
import capstone.project.curl.StateAdapter.RecyclerViewStateAdapter;
import capstone.project.curl.StateAdapter.SmmryAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebBrowserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebBrowserFragment extends Fragment implements SmsBroadcastReceiver.SmsOnReceiveListener {
    private static final String TAG = WebBrowserFragment.class.getCanonicalName();
    private static String quickAnswerModel = "";
    private static int quickAnswerState = RecyclerViewStateAdapter.STATE_EMPTY;
    private View loadingView;
    private View errorView;
    private View emptyView;
    private static SmmryAdapter smmryAdapter = null;
    private static RecyclerViewStateAdapter recyclerViewStateAdapter = null;
    private SendSms sendSms;


    public WebBrowserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WebBrowserFragment newInstance(SendSms sendSms) {
        WebBrowserFragment fragment = new WebBrowserFragment();
        fragment.sendSms = sendSms;
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
        return inflater.inflate(R.layout.fragment_web_browser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final EditText editText = view.findViewById(R.id.summry_text_edit);

        // Setup directions recycler view
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.directionsRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        loadingView = getLayoutInflater().inflate(R.layout.loading, rv, false); // No loading view
        emptyView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false);
        ((TextView)emptyView.findViewById(R.id.textView2)).setText("Enter the url and we will summarize the contents of the web page");
        ((TextView)emptyView.findViewById(R.id.textView)).setText("Summarize the web");
        ((ImageView)emptyView.findViewById(R.id.imageView2)).setImageResource(R.drawable.smrry);
        ((ImageView)emptyView.findViewById(R.id.imageView2)).setImageResource(R.drawable.smrry);
        errorView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false); // no empty state for now
        // TODO: remove hard code
        ((TextView)(errorView.findViewById(R.id.textView2))).setText("We won't be able to provide any functionality until you give us SMS permissions");
        ((TextView)(errorView.findViewById(R.id.textView))).setText("Why no permissions?");
        // TODO: Do this in XML or do both programmatically
        ImageView imageView= (ImageView) errorView.findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.error);

        if (smmryAdapter == null){
            smmryAdapter = new SmmryAdapter(quickAnswerModel);
        }
        recyclerViewStateAdapter = new RecyclerViewStateAdapter(smmryAdapter, loadingView, emptyView, errorView);
        rv.setAdapter(recyclerViewStateAdapter);
        recyclerViewStateAdapter.setState(quickAnswerState);
        // rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));


        ((EditText) getView().findViewById(R.id.summry_text_edit)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // After the search button is pressed in the soft keyboard while typing in editText
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_LOADING);
                    quickAnswerState = RecyclerViewStateAdapter.STATE_LOADING;
                    if (SendSms.hasPermissions(getContext(), SendSms.PERMISSIONS)){
                        String message = editText.getText().toString();
                        sendSms.sendSms("2264065956", message, SendSms.WEB_BROWSING);
                        //onTextReceived("sadsads-sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldksadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldk sadmslkadmnklsandlsandlksalkdnlsandklqnsaldk");
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
        Log.d("QuickAnswer", "Got text" + text);
        smmryAdapter.setNavigationModel(text);
        String smmryText ="";
        try{
            smmryText = (text.substring(text.indexOf("-")));
            smmryText = smmryText.substring(smmryText.indexOf("ry") + 3);
        } catch (Exception e){

        }
        quickAnswerModel = smmryText;
        quickAnswerState = RecyclerViewStateAdapter.STATE_NORMAL;
        Log.d("QuickAnswer", "Got texst" + smmryText);

        if (!smmryText.trim().equals("x")){
            quickAnswerState = RecyclerViewStateAdapter.STATE_NORMAL;
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_NORMAL);
            smmryAdapter.setNavigationModel(smmryText);
        } else {
            Toast.makeText(getActivity(), String.format("No summary available for that page, please retry :)"), Toast.LENGTH_LONG).show();
            quickAnswerState = RecyclerViewStateAdapter.STATE_EMPTY;
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_EMPTY);
        }
    }
}
