package com.example.managerportal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.UploadPolicy;
import com.example.managerportal.Adapters.NewCourseAdapter;
import com.example.managerportal.Models.NewCourse;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 1;

    androidx.appcompat.widget.Toolbar Toolbar;
    BottomSheetDialog bottomSheetDragHandleView;
    ImageButton CourseImageButton;
    EditText CourseNameEdittext, ImageURIEdittext;
    Button UploadCourseButton;
    ProgressBar Progress;
     Uri imageUri;
     String ImageURL;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        try {
            ProviderInstaller.installIfNeeded(Dashboard.this);
        } catch (GooglePlayServicesRepairableException e) {
            // Prompt user to update Google Play Services
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int status = apiAvailability.isGooglePlayServicesAvailable(Dashboard.this);
            if (apiAvailability.isUserResolvableError(status)) {
                apiAvailability.getErrorDialog((Activity) Dashboard.this, status, 9000).show();
            }
        } catch (GooglePlayServicesNotAvailableException e) {
            // Log the error or notify the user
            Log.e("ProviderInstaller", "Google Play Services not available: " + e.getMessage());
            Toast.makeText(Dashboard.this, "Google Play Services not available on this device.", Toast.LENGTH_LONG).show();
        }


        setToolbar();
        fetchCourses();
        setBottomSheet();
        initConfig();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  Set the menu to an activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.courses_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.AddCourseOption == item.getItemId()) {
            @SuppressLint("InflateParams") View view1 = LayoutInflater.from(Dashboard.this).inflate(R.layout.course_bottomsheet, null);
            bottomSheetDragHandleView.setContentView(view1);
            CourseImageButton = view1.findViewById(R.id.CourseImageButton);
            CourseNameEdittext = view1.findViewById(R.id.CourseNameEdittext);
            ImageURIEdittext = view1.findViewById(R.id.ImageURIEdittext);
            UploadCourseButton = view1.findViewById(R.id.UploadCourseButton);
            Progress = view1.findViewById(R.id.Progress);
            bottomSheetDragHandleView.show();

            // trigger when course ImageButton press and call openMedia()method
            CourseImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMedia();
                }
            });

            // trigger when Upload Course Button press and call addToCloudinary()
            UploadCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String course = CourseNameEdittext.getText().toString();
                    String imageURI = ImageURIEdittext.getText().toString();
                    if (TextUtils.isEmpty(course) || TextUtils.isEmpty(imageURI)) {
                        Login.showErrorAndRequestFocus(CourseNameEdittext, "Please provide both Course Image  and Course Name");
                    } else {
                        CourseNameEdittext.setFocusable(false);
                        CourseImageButton.setEnabled(false);
                        UploadCourseButton.setVisibility(View.GONE);
                        addToCloudinary(imageUri,course);
                    }
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageURIEdittext.setText(imageUri.toString());
            Glide.with(Dashboard.this).load(imageUri).placeholder(R.drawable.loading).error(R.drawable.error).into(CourseImageButton);

        }
    }


    private void openMedia() {
        // openMedia() method to open file storage of user
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public void setToolbar(){
        Toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(Toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Computer Science Courses");
        }
    }

    public void setBottomSheet(){
        bottomSheetDragHandleView = new BottomSheetDialog(Dashboard.this);
/*
        bottomSheetDragHandleView.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Toast.makeText(Dashboard.this, "No added any course", Toast.LENGTH_SHORT).show();
            }
        });
*/
    }

    public void initConfig() {
        Map config = new HashMap();
        config.put("cloud_name", "dxddhl59g");
        config.put("api_key", "339777695948436");
        config.put("api_secret", "tzN2vVqhWQ1tqyCze4g1qx8g5oc");
        // Initialize MediaManager
        MediaManager.init(Dashboard.this, config);
        // initConfig method is created to configure cloudinary
    }

    private void addToCloudinary(Uri imageURI , String courseName) {
        MediaManager.get().upload(imageURI).option("resource_type", "image").option("folder", "courses/").policy(new UploadPolicy.Builder().maxRetries(3).build()).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("upload to cloudinary","start");
                Progress.setVisibility(View.VISIBLE);
            }

            // onProgress() to handle ongoing progress
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                int progress = (int) ((bytes * 100) / totalBytes); // Calculate percentage
                Log.d("Upload Progress", "Progress: " + progress + "%");
            }

            // onSuccess() to handle after image upload to cloudinary
            @Override
            public void onSuccess(String requestId, Map resultData) {
                ImageURL = Objects.requireNonNull(resultData.get("secure_url")).toString();
                Log.d("upload to cloudinary","success " + ImageURL);
                addDetailsToFirestore(ImageURL,courseName);
                Progress.setVisibility(View.GONE);
                bottomSheetDragHandleView.dismiss();
                Toast.makeText(Dashboard.this, "New course added successfully", Toast.LENGTH_SHORT).show();
            }

            // onError() to handle error while progressing
            @Override
            public void onError(String requestId, ErrorInfo error) {
                bottomSheetDragHandleView.dismiss();
                Toast.makeText(Dashboard.this, error.getDescription() + "Please try again later ", Toast.LENGTH_SHORT).show();
            }

            // onReschedule() if there is something went wrong
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d("Cloudinary", "Upload rescheduled: " + error.getDescription());
            }
        }).dispatch(); // .dispatch() is always necessary to start upload progress
    }

    private void addDetailsToFirestore(String URL , String name){
         firebaseFirestore = FirebaseFirestore.getInstance();

        Map<String, Object> coursemap = new HashMap<>();
        coursemap.put("URL", URL);
        coursemap.put("name", name);
        firebaseFirestore.collection("Course Details").document(name).set(coursemap);
        Progress.setVisibility(View.GONE);
        bottomSheetDragHandleView.dismiss();
        Toast.makeText(Dashboard.this, "New course added successfully", Toast.LENGTH_SHORT).show();
    }

    private void fetchCourses() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference courseRef = firestore.collection("Course Details");

        courseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<NewCourse> courseList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    NewCourse course = document.toObject(NewCourse.class);
                    courseList.add(course);
                }

                // Initialize RecyclerView
                RecyclerView recyclerView = findViewById(R.id.RecyclerView);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                NewCourseAdapter adapter = new NewCourseAdapter(courseList, this);

                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Failed to fetch data: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}