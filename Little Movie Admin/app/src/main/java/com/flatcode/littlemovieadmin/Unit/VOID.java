package com.flatcode.littlemovieadmin.Unit;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.flatcode.littlemovieadmin.Unit.DATA.castList;
import static com.flatcode.littlemovieadmin.Unit.DATA.movieList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.flatcode.littlemovieadmin.Model.Cast;
import com.flatcode.littlemovieadmin.Model.Category;
import com.flatcode.littlemovieadmin.Model.Movie;
import com.flatcode.littlemovieadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VOID {

    //static List<String> castList;

    public static void IntentClear(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void Intent(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void IntentExtra(Context context, Class c, String key, String value) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public static void IntentExtra2(Context context, Class c, String key, String value, String key2, String value2) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        context.startActivity(intent);
    }

    public static void IntentExtra4(Context context, Class c, String key, String value
            , String key2, String value2, String key3, String value3, String key4, String value4) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        intent.putExtra(key3, value3);
        intent.putExtra(key4, value4);
        context.startActivity(intent);
    }

    public static void Glide(Boolean isUser, Context context, String Url, ImageView Image) {
        try {
            if (Url.equals(DATA.BASIC)) {
                if (isUser)
                    Image.setImageResource(R.drawable.basic_user);
                else
                    Image.setImageResource(R.drawable.basic_music);
            } else
                Glide.with(context).load(Url).placeholder(R.color.image_profile).into(Image);
        } catch (Exception e) {
            Image.setImageResource(R.drawable.basic_music);
        }
    }

    public static void GlideBlur(Boolean isUser, Context context, String Url, ImageView Image, int level) {
        try {
            if (Url.equals(DATA.BASIC)) {
                if (isUser) Image.setImageResource(R.drawable.basic_user);
                else Image.setImageResource(R.drawable.basic_music);
            } else
                Glide.with(context).load(Url).placeholder(R.color.image_profile)
                        .apply(bitmapTransform(new BlurTransformation(level))).into(Image);
        } catch (Exception e) {
            Image.setImageResource(R.drawable.basic_music);
        }
    }

    public static void GlideBlurUri(Context context, Uri uri, ImageView Image, int level) {
        if (uri != null)
            Glide.with(context).load(uri).placeholder(R.color.image_profile)
                    .apply(bitmapTransform(new BlurTransformation(level))).into(Image);
    }

    public static void incrementItemCount(String database, String id, String childDB) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(database);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String itemsCount = DATA.EMPTY + snapshot.child(childDB).getValue();
                if (itemsCount.equals(DATA.EMPTY) || itemsCount.equals(DATA.NULL)) {
                    itemsCount = DATA.EMPTY + DATA.ZERO;
                }

                long newItemsCount = Long.parseLong(itemsCount) + 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(childDB, newItemsCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementItemRemoveCount(String database, String id, String childDB) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(database);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String lovesCount = DATA.EMPTY + snapshot.child(childDB).getValue();
                if (lovesCount.equals(DATA.EMPTY) || lovesCount.equals(DATA.NULL))
                    lovesCount = DATA.EMPTY + DATA.ZERO;

                int i = Integer.parseInt(lovesCount);
                if (i > 0) {
                    int removeLovesCount = Integer.parseInt(lovesCount) - 1;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(childDB, removeLovesCount);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
                    reference.child(id).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void isFavorite(final ImageView add, final String Id, final String UserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.FAVORITES).child(UserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Id).exists()) {
                    add.setImageResource(R.drawable.ic_star_selected);
                    add.setTag("added");
                } else {
                    add.setImageResource(R.drawable.ic_star_unselected);
                    add.setTag("add");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void checkFavorite(ImageView image, String id) {
        if (image.getTag().equals("add"))
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).setValue(true);
        else
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).removeValue();
    }

    public static void nrLoves(TextView number, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.LOVES).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number.setText(MessageFormat.format(" {0} ", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void CropImageSquare(Activity activity) {
        CropImage.activity()
                .setMinCropResultSize(DATA.MIX_SQUARE, DATA.MIX_SQUARE)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity);
    }

    public static void CropVideoSquare(Activity activity) {
        CropImage.activity()
                .setMinCropResultSize(DATA.MIX_VIDEO_X, DATA.MIX_VIDEO_Y)
                .setAspectRatio(10, 14)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity);
    }

    public static void CropImageSlider(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setMinCropResultSize(DATA.MIX_SLIDER_X, DATA.MIX_SLIDER_Y)
                .setAspectRatio(16, 9)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity);
    }

    public static void Intro(Context context, ImageView background, ImageView
            backWhite, ImageView backDark) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.background_day);
            backWhite.setVisibility(View.VISIBLE);
            backDark.setVisibility(View.GONE);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.background_night);
            backWhite.setVisibility(View.GONE);
            backDark.setVisibility(View.VISIBLE);
        }
    }

    public static void Logo(Context context, ImageView background) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.logo);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.logo_night);
        }
    }

    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static void moreDeleteCategory(Activity activity, Category item, String DB, String idDB
            , String childDB, Boolean cast, Boolean movie) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra(activity, CLASS.CATEGORY_EDIT, DATA.CATEGORY_ID, id);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.CATEGORY, DATA.CATEGORIES, false, DB, idDB
                        , childDB, cast, movie);
            }
        }).show();
    }

    public static void moreDeleteCast(Activity activity, Cast item, String DB, String idDB
            , String childDB, Boolean cast, Boolean movie) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0)
                VOID.IntentExtra(activity, CLASS.CAST_EDIT, DATA.CAST_ID, id);
            else if (which == 1)
                dialogOptionDelete(activity, id, name, DATA.CAST, DATA.CAST
                        , false, DB, idDB, childDB, cast, movie);
        }).show();
    }

    public static void moreDeleteMovie(Activity activity, Movie item, String DB, String idDB
            , String childDB, Boolean cast, Boolean movie) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String categoryId = DATA.EMPTY + item.getCategoryId();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra2(activity, CLASS.MOVIE_EDIT, DATA.MOVIE_ID, id, DATA.CATEGORY_ID, categoryId);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.MOVIE, DATA.MOVIES, false, DB, idDB
                        , childDB, cast, movie);
            }
        }).show();
    }

    public static void dialogOptionDelete(Activity activity, String id, String name, String type
            , String nameDB, boolean isEditorsChoice, String DB, String idDB, String childDB
            , Boolean cast, Boolean movie) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Do you want to delete " + name + " ( " + type + " ) " + "?");

        dialog.findViewById(R.id.yes).setOnClickListener(view -> {
            if (isEditorsChoice)
                dialogUpdateEditorsChoice(dialog, activity, id);
            else
                deleteDB(dialog, activity, id, name, nameDB, DB, idDB, childDB);
            if (cast)
                deleteCastInfo(id);
            else if (movie)
                deleteMovieInfo(id);
        });

        dialog.findViewById(R.id.no).setOnClickListener(view2 -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private static void deleteMovieInfo(String id) {
        castList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE).child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                castList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    castList.add(snapshot.getKey());

                for (int i = 0; castList.size() > i; i++)
                    incrementItemRemoveCount(DATA.CAST, castList.get(i), DATA.MOVIES_COUNT);
                ref.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void deleteCastInfo(String id) {
        movieList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    if (snapshot.child(id).exists())
                        movieList.add(snapshot.getKey());

                for (int i = 0; movieList.size() > i; i++) {
                    ref.child(movieList.get(i)).child(id).removeValue();
                    incrementItemRemoveCount(DATA.MOVIES, movieList.get(i), DATA.CAST_COUNT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void dialogUpdateEditorsChoice(Dialog dialogDelete, Context context, String id) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Editors Choice...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.EDITORS_CHOICE, 0);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
            //finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
        });
    }

    public static void deleteDB(Dialog dialogDelete, Activity activity, String id, String name
            , String nameDB, String DB, String idDB, String childDB) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("Deleting " + name + " ...");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(nameDB);
        reference.child(id).removeValue().addOnSuccessListener(unused1 -> {
            if ((DB != null) & (idDB != null) & (childDB != null))
                incrementItemRemoveCount(DB, idDB, childDB);
            DATA.isChange = true;
            activity.onBackPressed();
            dialog.dismiss();
            Toast.makeText(activity, name + " Deleted Successfully...", Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public static void addToEditorsChoice(Context context, Activity activity, String id, int number) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Editors Choice...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.EDITORS_CHOICE, number);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show();
            activity.finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public static void dialogAboutArtist(Context context, String imageDB, String nameDB, String aboutDB) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_artist);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView image = dialog.findViewById(R.id.image);
        TextView name = dialog.findViewById(R.id.name);
        TextView aboutTheArtist = dialog.findViewById(R.id.aboutTheArtist);

        VOID.Glide(false, context, imageDB, image);
        name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB));
        aboutTheArtist.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB));

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static String convertDuration(long duration) {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;
    }

    public static void loadCategory(String categoryId, TextView category) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        ref.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String Category = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();
                category.setText(Category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}