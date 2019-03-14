package capstone.project.curl.StateAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import capstone.project.curl.Models.MapsApi.NavigationModel;
import capstone.project.curl.R;

public class DirectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_DIRECTION = 1;
    private static int TYPE_LOADING = 2;

    private NavigationModel navigationModel;

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

    public DirectionsAdapter(NavigationModel navigationModel) {
        this.navigationModel = navigationModel;
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
            if (position == 0){
                directionViewHolder.durationRow.setVisibility(View.GONE);
                directionViewHolder.directionRow.setVisibility(View.GONE);
                directionViewHolder.distanceRow.setVisibility(View.GONE);
                directionViewHolder.originRow.setVisibility(View.VISIBLE);
                directionViewHolder.destinationRow.setVisibility(View.VISIBLE);
                directionViewHolder.originText.setText(navigationModel.startingAddress);
                directionViewHolder.destinationText.setText(navigationModel.endAddress);
            } else {
                directionViewHolder.distanceValue.setText(navigationModel.directionMessages.get(position - 1).distance);
                directionViewHolder.directionText.setText(navigationModel.directionMessages.get(position - 1).direction);
                directionViewHolder.durationValue.setText(navigationModel.directionMessages.get(position - 1).duration);
            }
        } else{
            // TODO, make a loading view
            throw new IllegalStateException("Viewtype is unknown");
        }
    }

    @Override
    public int getItemCount() {
        if (!this.navigationModel.errorWhileParsing){
            return this.navigationModel.directionMessages.size() + 1;
        } else {
            return 0;
        }
    }

    public void setNavigationModel(NavigationModel navigationModel){
        this.navigationModel = navigationModel;
        notifyDataSetChanged();
    }

    private class DirectionViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout originRow;
        private TextView originText;
        private LinearLayout destinationRow;
        private TextView destinationText;
        private LinearLayout durationRow;
        private TextView durationTitle;
        private TextView durationValue;
        private LinearLayout distanceRow;
        private TextView distanceTitle;
        private TextView distanceValue;
        private LinearLayout directionRow;
        private TextView directionText;

        DirectionViewHolder(@NonNull View itemView) {
            super(itemView);
            originRow = itemView.findViewById(R.id.originRow);
            originText = itemView.findViewById(R.id.originText);
            destinationRow = itemView.findViewById(R.id.destinationRow);
            destinationText = itemView.findViewById(R.id.destinationText);
            durationRow = itemView.findViewById(R.id.durationRow);
            durationValue = itemView.findViewById(R.id.durationText);
            distanceRow = itemView.findViewById(R.id.distanceRow);
            directionRow = itemView.findViewById(R.id.directionRow);
            distanceValue = itemView.findViewById(R.id.distanceText);
            directionText = itemView.findViewById(R.id.directionText);
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

