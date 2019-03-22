package capstone.project.curl.StateAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import capstone.project.curl.Models.MapsApi.NavigationModel;
import capstone.project.curl.QuickAnswerSmsParser;
import capstone.project.curl.R;

public class QuickAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_DIRECTION = 1;

    private QuickAnswerSmsParser.QuickAnswerModel quickAnswerModel;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public TextView mTextView;

        public ItemViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }
    }

    public QuickAnswerAdapter(QuickAnswerSmsParser.QuickAnswerModel quickAnswerModel) {
        this.quickAnswerModel = quickAnswerModel;
    }

    @Override
    public int getItemViewType(int position) {
        if (true){
            return TYPE_DIRECTION;
        } else {
            // TODO, make a loading view
            throw new IllegalStateException("Not all directions retrieved is set but haven't" +
                    " implemented loading view");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_DIRECTION){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quickanswerresource, parent, false);
            return new QuickViewHolder(view);
        }  else {
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QuickViewHolder) {
            QuickViewHolder quickViewHolder = (QuickViewHolder) holder;
            StringBuilder stringBuilder = new StringBuilder();
            for (QuickAnswerSmsParser.QuickAnswerModel.WebsiteQuickAnswer websiteQuickAnswer : quickAnswerModel.websiteQuickAnswers){
                stringBuilder.append(websiteQuickAnswer.URL).append(System.getProperty("line.separator")).append(websiteQuickAnswer.message).append(System.getProperty("line.separator"));
                stringBuilder.append(System.getProperty("line.separator"));
            }
            String answer = stringBuilder.toString();
            if (answer.contains("feature:answerbox")){
                try{
                    answer = answer.split("feature:answerbox")[1];
                } catch(Exception e){

                }
            }
            quickViewHolder.quickAnswerText.setText(answer);
            quickViewHolder.quickAnswerText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            quickViewHolder.quickAnswerText.setMovementMethod(ScrollingMovementMethod.getInstance());
        } else{
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public int getItemCount() {
        if (this.quickAnswerModel.validData){
            return 1;
        } else {
            return 0;
        }
    }

    public void setNavigationModel(QuickAnswerSmsParser.QuickAnswerModel navigationModel){
        this.quickAnswerModel = navigationModel;
        notifyDataSetChanged();
    }

    private class QuickViewHolder extends RecyclerView.ViewHolder {
        private TextView quickAnswerText;

        QuickViewHolder(@NonNull View itemView) {
            super(itemView);
            quickAnswerText = itemView.findViewById(R.id.quick_answer_text);
        }
    }
}

