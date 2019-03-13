package capstone.project.curl.StateAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import capstone.project.curl.Models.Directions;
import capstone.project.curl.R;

public class SimpleStringAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_DIRECTION = 1;
    private static int TYPE_LOADING = 2;

    private Directions directions;

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

    public SimpleStringAdapter(Directions directions) {
        this.directions = directions;
    }

    @Override
    public int getItemViewType(int position) {
        if (directions.allDirectionsRetrieved){
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direction, parent, false);
            return new DirectionViewHolder(view);
        } else if (viewType == TYPE_LOADING){
            // TODO, make a loading view
            throw new IllegalStateException("Not all directions retrieved is set but haven't" +
                    " implemented loading view");
        } else {
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DirectionViewHolder) {
            DirectionViewHolder directionViewHolder = (DirectionViewHolder) holder;
            directionViewHolder.directionsText.setText(this.directions.directions.get(position));
        } else{
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public int getItemCount() {
        if (directions.allDirectionsRetrieved){
            return directions.directions.size();
        } else {
            // TODO, add an additional view at the end that shows a loading screen
            throw new IllegalStateException("Not all directions retrieved is set but haven't" +
                    " implemented loading view");
        }
    }

    public void appendDirections(List<String> directions) {
        if (this.directions.allDirectionsRetrieved){
            int count = getItemCount();
            this.directions.directions.addAll(directions);
            notifyItemRangeInserted(count, directions.size());
        } else {
            // TODO, notify a larger range so that the loading view is changed as well
            throw new IllegalStateException("Not all directions retrieved is set but haven't" +
                    " implemented loading view");
        }
    }

    private class DirectionViewHolder extends RecyclerView.ViewHolder {
        private TextView directionsText;

        DirectionViewHolder(@NonNull View itemView) {
            super(itemView);
            directionsText = itemView.findViewById(R.id.directions);
        }
    }

    /**
     * Todo. Make a "load more" item view
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

