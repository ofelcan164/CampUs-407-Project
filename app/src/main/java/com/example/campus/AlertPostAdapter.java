package com.example.campus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AlertPostAdapter extends FirebaseRecyclerAdapter<AlertPost, AlertPostAdapter.alertPostViewHolder> {

    /**
     * Constructor
     */
    public AlertPostAdapter(@NonNull FirebaseRecyclerOptions<AlertPost> options) {
        super(options);
    }

    /**
     * Inflate the view and return it to the caller via the adapter
     */
    @NonNull
    @Override
    public AlertPostAdapter.alertPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_post_layout, parent, false);
        return new AlertPostAdapter.alertPostViewHolder(view);
    }

    /**
     * Binds XML (alert_post_layout) to the required data from AlertPost class
     */
    @Override
    public void onBindViewHolder(@NonNull AlertPostAdapter.alertPostViewHolder holder, int position, AlertPost post) {
        // Add posts content to view in XML
        holder.title.setText(post.getTitle());
        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
        holder.urgencyRating.setText(post.getUrgencyRating());

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View another user's profile
                Context context = view.getContext();
                Intent intent = new Intent(context, OtherProfileActivity.class);
                intent.putExtra("uid", post.getUID());
                intent.putExtra("back", "alert");
                context.startActivity(intent);
            }
        });
    }

    /**
     * Subclass to get references to view objects by ID
     */
    class alertPostViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView urgencyRating;
        TextView content;
        TextView username;

        /**
         * Constructor
         */
        public alertPostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.alertPostTitle);
            content = (TextView) itemView.findViewById(R.id.alertPostContent);
            urgencyRating = (TextView) itemView.findViewById(R.id.alertPostUrgencyRating);
            username = (TextView) itemView.findViewById(R.id.alertPostUsername);
        }
    }
}
