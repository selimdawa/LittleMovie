package com.flatcode.littlemovieadmin.Activity;

import static com.flatcode.littlemovieadmin.Unit.DATA.castMovie;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ActivityMovieAddBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieAddActivity extends AppCompatActivity {

    private ActivityMovieAddBinding binding;
    Activity activity = MovieAddActivity.this;

    Uri imageUri = null, videoUri = null;
    StorageTask uploadsTask;

    private ProgressDialog dialog;

    private ArrayList<String> categoryId, categoryList;
    String durations;

    MediaMetadataRetriever metadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityMovieAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadCategories();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        binding.toolbar.nameSpace.setText(R.string.add_new_movie);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.image.setOnClickListener(v -> VOID.CropVideoSquare(activity));
        binding.chooseMovie.setOnClickListener(v -> openVideoFiles());
        binding.toolbar.ok.setOnClickListener(v -> validateData());

        metadataRetriever = new MediaMetadataRetriever();
    }

    private String name = DATA.EMPTY, description = DATA.EMPTY, yearText = DATA.EMPTY;

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        yearText = binding.yearEt.getText().toString();

        //validate data
        if (TextUtils.isEmpty(name))
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(description))
            Toast.makeText(activity, "Enter Description...", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(yearText))
            Toast.makeText(activity, "Enter Date...", Toast.LENGTH_SHORT).show();
        else if ((Integer.parseInt(yearText) < DATA.MIN_YEAR) || (Integer.parseInt(yearText) > DATA.MAX_YEAR))
            Toast.makeText(activity, "Invalid Date...", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(selectedCategoryTitle))
            Toast.makeText(activity, "Pick Category...", Toast.LENGTH_SHORT).show();
        else if (castMovie.size() <= 0)
            Toast.makeText(activity, "Enter Cast...", Toast.LENGTH_SHORT).show();
        else if (imageUri == null)
            Toast.makeText(activity, "Pick Image...", Toast.LENGTH_SHORT).show();
        else if (videoUri == null)
            Toast.makeText(activity, "Pick Movie...", Toast.LENGTH_SHORT).show();
        else
            uploadFileToDB();
    }

    public void uploadFileToDB() {
        if (binding.choose.equals("No file Selected")) {
            Toast.makeText(this, "Please selected an image!", Toast.LENGTH_SHORT).show();
        } else {
            if (uploadsTask != null && uploadsTask.isInProgress()) {
                Toast.makeText(this, "Movies uploads in already progress!", Toast.LENGTH_SHORT).show();
            } else {
                uploadVideoToStorage();
            }
        }
    }

    private void uploadVideoToStorage() {
        Toast.makeText(this, "Uploads please wait!", Toast.LENGTH_SHORT).show();

        dialog.setMessage("Uploads Movie...");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        String id = ref.push().getKey();

        String filePathAndName = "Movies/" + id;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedVideoUrl = "" + uriTask.getResult();
            dialog.dismiss();

            uploadToStorage(uploadedVideoUrl, id, ref);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            dialog.setMessage("uploaded " + ((int) progress) + "%.....");
        });
    }

    private void uploadToStorage(String uploadedMovieUrl, String id, DatabaseReference ref) {
        dialog.setMessage("Uploading Movie...");
        dialog.show();

        String filePathAndName = "Images/Movie/" + id;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            uploadInfoToDB(uploadedMovieUrl, uploadedImageUrl, id, ref);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Cast upload failed due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            dialog.setMessage("uploaded " + ((int) progress) + "%.....");
        });
    }

    private void uploadInfoToDB(String uploadedMovieUrl, String uploadedImageUrl, String id, DatabaseReference ref) {
        dialog.setMessage("Uploading movie info...");
        dialog.show();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.PUBLISHER, DATA.EMPTY + DATA.FirebaseUserUid);
        hashMap.put(DATA.TIMESTAMP, System.currentTimeMillis());
        hashMap.put(DATA.ID, id);
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.DESCRIPTION, DATA.EMPTY + description);
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);
        hashMap.put(DATA.DURATION, DATA.EMPTY + durations);
        hashMap.put(DATA.YEAR, Integer.parseInt(yearText));
        hashMap.put(DATA.MOVIE_LINK, DATA.EMPTY + uploadedMovieUrl);
        hashMap.put(DATA.IMAGE, DATA.EMPTY + uploadedImageUrl);
        hashMap.put(DATA.EDITORS_CHOICE, DATA.ZERO);
        hashMap.put(DATA.LOVES_COUNT, DATA.ZERO);
        hashMap.put(DATA.VIEWS_COUNT, DATA.ZERO);
        hashMap.put(DATA.CAST_COUNT, castMovie.size());

        //db reference: DB > Movies
        assert id != null;
        ref.child(id).setValue(hashMap).addOnSuccessListener(unused -> {
            if (selectedCategoryId != null)
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT);
            uploadCastToDB(id);
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failure to upload to db due to :" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadCastToDB(String id) {
        dialog.setMessage("Uploading Cast Movie...");
        dialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE);

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i < castMovie.size(); i++) {
            hashMap.put(castMovie.get(i), true);
            VOID.incrementItemCount(DATA.CAST, castMovie.get(i), DATA.MOVIES_COUNT);
        }

        //db reference: DB > CastMovie
        assert id != null;
        ref.child(id).setValue(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failure to upload to db due to :" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void openVideoFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            videoUri = data.getData();
            //duration
            metadataRetriever.setDataSource(this, videoUri);
            assert metadataRetriever != null;
            durations = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            binding.duration.setText(VOID.convertDuration(Long.parseLong(durations)));
            //Ok Choose Movie
            binding.choose.setText(R.string.ok);
        }
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = CropImage.getPickImageResultUri(activity, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(activity, uri)) {
                imageUri = uri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                VOID.CropVideoSquare(activity);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                binding.image.setImageURI(imageUri);
                VOID.GlideBlurUri(activity, imageUri, binding.imageBlur, 50);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error! " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadCategories() {
        categoryList = new ArrayList<>();
        categoryId = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryId.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = DATA.EMPTY + data.child(DATA.ID).getValue();
                    String name = DATA.EMPTY + data.child(DATA.NAME).getValue();

                    categoryList.add(name);
                    categoryId.add(id);
                }
                binding.cast.setOnClickListener(v -> VOID.Intent(activity, CLASS.CAST_MOVIE));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        String[] categories = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Pick Category").setItems(categories, (dialog, which) -> {
            selectedCategoryTitle = categoryList.get(which);
            selectedCategoryId = categoryId.get(which);
            binding.category.setText(selectedCategoryTitle);
        }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.cast.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, castMovie.size()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        castMovie.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        castMovie.clear();
    }
}