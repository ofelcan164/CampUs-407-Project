package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.campus.databinding.ActivityMainFeedsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateNewSocialPost extends AppCompatActivity {

    FirebaseAuth mAuth;
    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageViewSocial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_social_post);
        imageViewSocial = findViewById(R.id.imageViewSocial);

        // Post helper instance
        NewPostHelper postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        Button uploadPhotoBtn = (Button) findViewById(R.id.addPhotoSocial);
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                // upload(v);
            }
        });

        Button postBtn = (Button) findViewById(R.id.newSocialPostBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get post info
                EditText socialPostContent = (EditText) findViewById(R.id.newSocialPostContent);
                if (socialPostContent.getText().toString() != null && !socialPostContent.getText().toString().equals("")) {
                    SocialPost post = new SocialPost(socialPostContent.getText().toString(), mAuth.getCurrentUser().getDisplayName(), mAuth.getUid(), imageViewSocial);

                    // Post the post!
                    postHelper.postSocial(post);
                    socialPostContent.setError(null);
                    Intent intent = new Intent(CreateNewSocialPost.this, MainFeedsActivity.class);
                    intent.putExtra("select", "social");
                    startActivity(intent);
                }
                else {
                    socialPostContent.setError("Please enter you post's content");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewSocial.setImageURI(selectedImage);
        }
    }

    /*public void upload(View view) {
        try{
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference socialPhotoRef = storageRef.child("images/badgerGame");

            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_badger_game);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] socialPhotoByteStream = baos.toByteArray();
            UploadTask uploadTask = upload(imageViewSocial);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("ImageUpload", "Image successfully uploaded to Firebase.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", "Image upload failed.");
        }
    }*/
}