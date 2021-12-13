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
import com.squareup.picasso.Picasso;

public class SocialPostAdapter extends FirebaseRecyclerAdapter<SocialPost, SocialPostAdapter.socialPostViewHolder> {

    /**
     * Constructor
     */
    public SocialPostAdapter(@NonNull FirebaseRecyclerOptions<SocialPost> options) {
        super(options);
    }

    /**
     * Inflate the view and return it to the caller via the adapter
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
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View another user's profile
                Context context = view.getContext();
                Intent intent = new Intent(context, OtherProfileActivity.class);
                intent.putExtra("back", "social");
                intent.putExtra("uid", post.getUID());
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
    class socialPostViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView content;
        ImageView image;

        /**
         * Constructor
         */
        public socialPostViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.socialPostUsername);
            content = (TextView) itemView.findViewById(R.id.socialPostContent);
            image = (ImageView) itemView.findViewById(R.id.socialPostImage);
        }
    }

    public void downloadAndSet(String postID, socialPostViewHolder holder) {

        String imagePath = "images/" + postID;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child(imagePath);

        final long ONE_MEGABYTE = 1024 * 1024;

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);if (bitmap.getHeight() != 600 && bitmap.getWidth() >= 600) {
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
                Log.i("SocialPostAdapter", "Image Download failed -- " + imagePath);
            }
        });
    }
}
