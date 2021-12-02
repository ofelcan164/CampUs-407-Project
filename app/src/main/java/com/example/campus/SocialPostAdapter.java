package com.example.campus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SocialPostAdapter extends FirebaseRecyclerAdapter<SocialPost, SocialPostAdapter.socialPostViewHolder> {

    /**
     * Constructor
     */
    public SocialPostAdapter(@NonNull FirebaseRecyclerOptions<SocialPost> options) {
        super(options);
    }

    /**
     * Inflate the view and return it to the caller via the apdater
     */
    @NonNull
    @Override
    public socialPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_post_layout, parent, false);
        return new socialPostViewHolder(view);
    }

    /**
     * Binds XML (social_post_layout) to the required data from SocialPost class
     */
    @Override
    public void onBindViewHolder(@NonNull socialPostViewHolder holder, int position, SocialPost post) {
        // Add posts content to view in XML
        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
    }

    /**
     * Subclass to get references to view objects by ID
     */
    class socialPostViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView content;

        /**
         * Constructor
         */
        public socialPostViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.socialPostUsername);
            content = (TextView) itemView.findViewById(R.id.socialPostContent);
        }
    }
}
