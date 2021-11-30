package com.example.campus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MarketPostAdapter extends FirebaseRecyclerAdapter<MarketPost, MarketPostAdapter.marketPostViewHolder> {

    /**
     * Constructor
     */
    public MarketPostAdapter(@NonNull FirebaseRecyclerOptions<MarketPost> options) {
        super(options);
    }

    /**
     * Inflate the view and return it to the caller via the apdater
     */
    @NonNull
    @Override
    public MarketPostAdapter.marketPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_post_layout, parent, false);
        return new MarketPostAdapter.marketPostViewHolder(view);
    }

    /**
     * Binds XML (market_post_layout) to the required data from SocialPost class
     */
    @Override
    public void onBindViewHolder(@NonNull MarketPostAdapter.marketPostViewHolder holder, int position, MarketPost post) {
        // Add posts content to view in XML
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        holder.number.setText(post.getPhoneNum());
    }

    /**
     * Subclass to get references to view objects by ID
     */
    class marketPostViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView number;
        TextView description;

        /**
         * Constructor
         */
        public marketPostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.marketPostTitle);
            number = (TextView) itemView.findViewById(R.id.marketPostNumber);
            description = (TextView) itemView.findViewById(R.id.marketPostDescription);
        }
    }
}
