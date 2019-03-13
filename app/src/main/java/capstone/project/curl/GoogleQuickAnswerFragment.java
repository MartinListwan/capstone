package capstone.project.curl;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleQuickAnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleQuickAnswerFragment extends Fragment {
    private static final String TAG = GoogleQuickAnswerFragment.class.getCanonicalName();

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
    public static GoogleQuickAnswerFragment newInstance() {
        GoogleQuickAnswerFragment fragment = new GoogleQuickAnswerFragment();
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

}
