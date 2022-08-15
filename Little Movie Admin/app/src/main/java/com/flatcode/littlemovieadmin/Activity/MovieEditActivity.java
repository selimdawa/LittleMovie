package com.flatcode.littlemovieadmin.Activity;

import static com.flatcode.littlemovieadmin.Unit.DATA.castMovie;
import static com.flatcode.littlemovieadmin.Unit.DATA.castMovieOld;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.Model.Movie;
import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ActivityMovieEditBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieEditActivity extends AppCompatActivity {

    private ActivityMovieEditBinding binding;
    Activity activity = MovieEditActivity.this;

    private String movieId, category;
    private ArrayList<String> categoryList, categoryId;

    private Uri imageUri;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityMovieEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        movieId = getIntent().getStringExtra(DATA.MOVIE_ID);
        category = getIntent().getStringExtra(DATA.CATEGORY_ID);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadInfo();

        binding.toolbar.nameSpace.setText(R.string.edit_movie);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.editImage.setOnClickListener(v -> VOID.CropVideoSquare(activity));
        binding.cast.setOnClickListener(v -> VOID.Intent(activity, CLASS.CAST_MOVIE));
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY, description = DATA.EMPTY, yearText = DATA.EMPTY;
    private String selectedCategoryId, selectedCategoryTitle;

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
        else if (category.isEmpty())
            Toast.makeText(activity, "Pick Category...", Toast.LENGTH_SHORT).show();
        else if (castMovie.size() <= 0)
            Toast.makeText(activity, "Enter Cast...", Toast.LENGTH_SHORT).show();
        else
            update();
    }

    private void update() {
        dialog.setMessage("Updating Movie...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.DESCRIPTION, DATA.EMPTY + description);
        hashMap.put(DATA.YEAR, Integer.parseInt(yearText));
        hashMap.put(DATA.CAST_COUNT, castMovie.size());
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        reference.child(movieId).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Movie updated...", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            if (!selectedCategoryId.equals(category)) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT);
                if (category != null)
                    VOID.incrementItemRemoveCount(DATA.CATEGORIES, category, DATA.MOVIES_COUNT);
            }
            updateCast();
            if (imageUri != null) {
                uploadImage();
            } else {
                onBackPressed();
            }
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImage() {
        dialog.setMessage("Updating Movie...");
        dialog.show();

        String filePathAndName = "Images/Movie/" + movieId;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName
                + DATA.DOT + VOID.getFileExtension(imageUri, activity));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            updateImageMovie(uploadedImageUrl, movieId);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to upload image due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateImageMovie(String imageUrl, String id) {
        dialog.setMessage("Updating image movie...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imageUri != null) {
            hashMap.put(DATA.IMAGE, DATA.EMPTY + imageUrl);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Image updated...", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCast() {
        dialog.setMessage("Updating Cast Movie...");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE);

        //Remove data to upload
        for (int r = 0; r < castMovieOld.size(); r++) {
            if (!castMovie.contains(castMovieOld.get(r))) {
                ref.child(movieId).child(castMovieOld.get(r)).removeValue();
            }
        }

        //Add data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i < castMovie.size(); i++) {
            hashMap.put(castMovie.get(i), true);
        }

        //db reference: DB > CastMovie
        assert movieId != null;
        ref.child(movieId).setValue(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Cast Movie updated...", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            if (!selectedCategoryId.equals(category)) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.MOVIES_COUNT);
                if (category != null)
                    VOID.incrementItemRemoveCount(DATA.CATEGORIES, category, DATA.MOVIES_COUNT);
            }
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        reference.child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Movie item = snapshot.getValue(Movie.class);
                assert item != null;
                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).getValue();
                String name = DATA.EMPTY + item.getName();
                String image = DATA.EMPTY + item.getImage();
                String description = DATA.EMPTY + item.getDescription();
                String year = DATA.EMPTY + item.getYear();
                String durations = DATA.EMPTY + item.getDuration();
                String castCount = DATA.EMPTY + item.getCastCount();

                VOID.Glide(true, activity, image, binding.image);
                VOID.GlideBlur(false, activity, image, binding.imageBlur, 50);
                binding.nameEt.setText(name);
                binding.descriptionEt.setText(description);
                binding.yearEt.setText(year);
                binding.duration.setText(VOID.convertDuration(Long.parseLong(durations)));
                binding.cast.setText(castCount);

                DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
                refCategory.child(selectedCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();

                        binding.category.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                DatabaseReference refCast = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE);
                refCast.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        castMovie.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            castMovie.add(snapshot.getKey());
                            castMovieOld.add(snapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                imageUri = result.getUri();
                binding.image.setImageURI(imageUri);
                VOID.GlideBlurUri(activity, imageUri, binding.imageBlur, 50);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error! " + error, Toast.LENGTH_SHORT).show();
            }
        }
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
        castMovieOld.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        castMovie.clear();
        castMovieOld.clear();
    }
}