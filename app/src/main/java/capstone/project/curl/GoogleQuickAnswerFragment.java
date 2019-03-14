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

import capstone.project.curl.StateAdapter.DirectionsAdapter;
import capstone.project.curl.StateAdapter.QuickAnswerAdapter;
import capstone.project.curl.StateAdapter.RecyclerViewStateAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleQuickAnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleQuickAnswerFragment extends Fragment implements SmsBroadcastReceiver.SmsOnReceiveListener {
    private static final String TAG = GoogleQuickAnswerFragment.class.getCanonicalName();
    private static QuickAnswerSmsParser.QuickAnswerModel quickAnswerModel = QuickAnswerSmsParser.getQuickAnswrModel("");
    private static int quickAnswerState = RecyclerViewStateAdapter.STATE_EMPTY;
    private View loadingView;
    private View errorView;
    private View emptyView;
    private QuickAnswerAdapter quickAnswerAdapter;
    private RecyclerViewStateAdapter recyclerViewStateAdapter;
    private SendSms sendSms;
    // TODO: Rename parameter arguments, choose names that match
    public GoogleQuickAnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoogleQuickAnswerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoogleQuickAnswerFragment newInstance(SendSms sendSms) {
        GoogleQuickAnswerFragment fragment = new GoogleQuickAnswerFragment();
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
        return inflater.inflate(R.layout.fragment_google_quick_answer, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final EditText editText = view.findViewById(R.id.quick_answer_text);

        // Setup directions recycler view
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.directionsRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        loadingView = getLayoutInflater().inflate(R.layout.loading, rv, false); // No loading view
        emptyView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false);
        ((TextView)emptyView.findViewById(R.id.textView2)).setText("Browse the web for answers");
        ((TextView)emptyView.findViewById(R.id.textView)).setText("Ask a question");
        ((ImageView)emptyView.findViewById(R.id.imageView2)).setImageResource(R.drawable.quick_answer);
        errorView = getLayoutInflater().inflate(R.layout.directions_no_content, rv, false); // no empty state for now
        // TODO: remove hard code
        ((TextView)(errorView.findViewById(R.id.textView2))).setText("We won't be able to provide any functionality until you give us SMS permissions");
        ((TextView)(errorView.findViewById(R.id.textView))).setText("Why no permissions?");
        // TODO: Do this in XML or do both programmatically
        ImageView imageView= (ImageView) errorView.findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.error);

        quickAnswerAdapter = new QuickAnswerAdapter(quickAnswerModel);
        recyclerViewStateAdapter = new RecyclerViewStateAdapter(quickAnswerAdapter, loadingView, emptyView, errorView);
        rv.setAdapter(recyclerViewStateAdapter);
        recyclerViewStateAdapter.setState(quickAnswerState);
        // rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));


        ((EditText) getView().findViewById(R.id.quick_answer_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // After the search button is pressed in the soft keyboard while typing in editText
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_LOADING);

                    if (SendSms.hasPermissions(getContext(), SendSms.PERMISSIONS)){
                        String message = editText.getText().toString();
                        sendSms.sendSms("2264065956", message, SendSms.ANSWER_BOX);
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
        quickAnswerModel = QuickAnswerSmsParser.getQuickAnswrModel(text);
        quickAnswerAdapter.setNavigationModel(quickAnswerModel);
        if (quickAnswerModel.validData){
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_NORMAL);
        } else {
            Toast.makeText(getActivity(), String.format("Problem retrieving quick answer, please retry :)"), Toast.LENGTH_LONG).show();
            recyclerViewStateAdapter.setState(RecyclerViewStateAdapter.STATE_EMPTY);
        }
    }
}
