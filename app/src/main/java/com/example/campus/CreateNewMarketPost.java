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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateNewMarketPost extends AppCompatActivity {

    FirebaseAuth mAuth;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageViewMarket;
    private String postID;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_market_post);

        // Set the image view the user added
        imageViewMarket = findViewById(R.id.imageViewMarket);

        // Post helper instance
        NewPostHelper postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        // Add photo button
        Button uploadPhotoBtn = (Button) findViewById(R.id.addPhotoSale);
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
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

        Button postBtn = (Button) findViewById(R.id.newPostSaleBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get post info
                EditText salePostTitle = (EditText) findViewById(R.id.newSaleTitle);
                EditText salePostPhone = (EditText) findViewById(R.id.newSalePhone);
                EditText salePostDescription = (EditText) findViewById(R.id.newSaleDescription);
                if (salePostTitle.getText().toString() != null && !salePostTitle.getText().toString().equals("")
                        && salePostPhone.getText().toString() != null && !salePostPhone.getText().toString().equals("")
                        && salePostDescription.getText().toString() != null && !salePostDescription.getText().toString().equals("")) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        MarketPost post = new MarketPost(salePostTitle.getText().toString(),
                                salePostPhone.getText().toString(),
                                salePostDescription.getText().toString(),
                                mAuth.getCurrentUser().getDisplayName(),
                                postID,
                                location.getLatitude(),
                                location.getLongitude(),
                                mAuth.getUid());

                        // Post the post!
                        postID = postHelper.postMarket(post);
                        salePostTitle.setError(null);
                        salePostPhone.setError(null);
                        salePostDescription.setError(null);
                        upload(imageViewMarket, postID);
                        Intent intent = new Intent(CreateNewMarketPost.this, MainFeedsActivity.class);
                        intent.putExtra("select", "market");
                        startActivity(intent);
                    }
                } else {
                    salePostTitle.setError("Please enter you post's title");
                    salePostPhone.setError("Please enter your phone number");
                    salePostDescription.setError("Please enter the sale's description");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewMarket.setImageURI(selectedImage);
        }
    }

    public void upload(ImageView imageView, String postID) {
        try{
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] marketPhotoByteStream = baos.toByteArray();

            String imageFilePath = "images/" + postID;

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(imageFilePath);

            UploadTask uploadTask = imageRef.putBytes(marketPhotoByteStream);
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