package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.campus.databinding.ActivityMainFeedsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.NoSuchElementException;

public class CreateNewSocialPost extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageViewSocial;
    private String postID;
    private boolean photo;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private NewPostHelper postHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_social_post);

        // Set the image view the user added
        imageViewSocial = findViewById(R.id.imageViewSocial);

        // Post helper instance
        postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        // Add photo button
        Button uploadPhotoBtn = (Button) findViewById(R.id.addPhotoSocial);
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                photo = true; // TODO THIS SHOULDN'T EVEN BE SET LIKE THIS CANT ASSUME USER ACTUALLY PICKED A PHOTO
            }
        });

        // Set up location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        startListening();

        // Post button
        Button postBtn = (Button) findViewById(R.id.newSocialPostBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClicked();
            }
        });
    }

    private void onPostClicked() {
        // Get post info
        EditText socialPostContent = (EditText) findViewById(R.id.newSocialPostContent);
        if (socialPostContent.getText().toString() != null && !socialPostContent.getText().toString().equals("")) {
            // Get post location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                SocialPost post = new SocialPost(socialPostContent.getText().toString(),
                        mAuth.getCurrentUser().getDisplayName(),
                        postID,
                        location.getLatitude(),
                        location.getLongitude(),
                        mAuth.getUid());

                // Post the post!
                postID = postHelper.postSocial(post);
                socialPostContent.setError(null);
                if (photo) {
                    upload(imageViewSocial, postID);
                }
                Intent intent = new Intent(CreateNewSocialPost.this, MainFeedsActivity.class);
                intent.putExtra("select", "social");
                startActivity(intent);
            }
            else {
                Log.i("CreateSocial", "Location permissions denied.;");
            }
        }
        else {
            socialPostContent.setError("Please enter you post's content");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewSocial.setImageURI(selectedImage);
        }
    }

    public void upload(ImageView imageView, String postID) {
        try{
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] socialPhotoByteStream = baos.toByteArray();

            String imageFilePath = "images/" + postID;

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(imageFilePath);

            UploadTask uploadTask = imageRef.putBytes(socialPhotoByteStream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("ImageUpload", "Image successfully uploaded to Firebase.");
                }
            });

        } catch (Exception e) {
            Log.i("Error", "Image upload failed. Error:" + e);
        }
    }

    /**
     * Assign the LocationManager (when permission is granted)
     */
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
