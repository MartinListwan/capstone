package capstone.project.curl.StateAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import capstone.project.curl.QuickAnswerSmsParser;
import capstone.project.curl.R;

public class SmmryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_DIRECTION = 1;

    private String smmry;

    public SmmryAdapter(String smmryString) {
        this.smmry = smmryString;
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
            return new SmryHolder(view);
        }  else {
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SmryHolder) {
            SmryHolder quickViewHolder = (SmryHolder) holder;
            quickViewHolder.quickAnswerText.setText(smmry.toString());
            quickViewHolder.quickAnswerText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            quickViewHolder.quickAnswerText.setMovementMethod(ScrollingMovementMethod.getInstance());
            quickViewHolder.quickAnswerText.setVerticalScrollBarEnabled(true);
        } else{
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public int getItemCount() {
        if (this.smmry != null && this.smmry != ""){
            return 1;
        } else {
            return 0;
        }
    }

    public void setNavigationModel(String smmry){
        this.smmry = smmry;
        notifyDataSetChanged();
    }

    private class SmryHolder extends RecyclerView.ViewHolder {
        private TextView quickAnswerText;

        SmryHolder(@NonNull View itemView) {
            super(itemView);
            quickAnswerText = itemView.findViewById(R.id.quick_answer_text);
        }
    }
}

