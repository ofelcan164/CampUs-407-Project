package com.example.campus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MarketPostAdapter extends RecyclerView.Adapter<MarketPostAdapter.marketPostViewHolder> {

    private ArrayList<MarketPost> posts;

    /**
     * Constructor
     */
    public MarketPostAdapter(ArrayList<MarketPost> posts) {
        this.posts = posts;
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

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * Binds XML (market_post_layout) to the required data from MarketPost class
     */
    @Override
    public void onBindViewHolder(@NonNull MarketPostAdapter.marketPostViewHolder holder, int position) {
        // Add posts content to view in XML
        MarketPost post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.username.setText(post.getUsername());
        holder.description.setText(post.getDescription());
        holder.number.setText(post.getPhoneNum());

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View another user's profile
                Context context = view.getContext();
                Intent intent = new Intent(context, OtherProfileActivity.class);
                intent.putExtra("uid", post.getUID());
                intent.putExtra("back", "market");
                context.startActivity(intent);
            }
        });

        if (post.getPostID() != null) {
            downloadAndSet(post.getPostID(), holder);
        }
    }

    /**
     * Subclass to get references to view objects by ID
     */
    class marketPostViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView username;
        TextView number;
        TextView description;
        ImageView image;

        /**
         * Constructor
         */
        public marketPostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.marketPostTitle);
            username = (TextView) itemView.findViewById(R.id.marketPostUsername);
            number = (TextView) itemView.findViewById(R.id.marketPostNumber);
            description = (TextView) itemView.findViewById(R.id.marketPostDescription);
            image = (ImageView) itemView.findViewById(R.id.marketPostImage);
        }
    }

    public void downloadAndSet(String postID, MarketPostAdapter.marketPostViewHolder holder) {

        String imagePath = "images/" + postID;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child(imagePath);

        final long ONE_MEGABYTE = 1024 * 1024;

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap.getHeight() != 600 && bitmap.getWidth() >= 600) {
                    int y = ((bitmap.getHeight()) / 2) - 200;
                    int x = ((bitmap.getWidth()) / 2) - 200;
                    bitmap = Bitmap.createBitmap(bitmap, x, y, 400, 400);
                    holder.image.setImageBitmap(bitmap);
                } else {
                    holder.image.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("MarketPostAdapter", "Image Download failed -- " + imagePath);
            }
        });
    }
}
