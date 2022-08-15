package com.flatcode.littlemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flatcode.littlemovie.Adapter.SettingAdapter;
import com.flatcode.littlemovie.Model.Setting;
import com.flatcode.littlemovie.Model.User;
import com.flatcode.littlemovie.R;
import com.flatcode.littlemovie.Unit.CLASS;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.Unit.VOID;
import com.flatcode.littlemovie.databinding.FragmentSettingsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private ArrayList<Setting> list;
    private SettingAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(LayoutInflater.from(getContext()), container, false);

        binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new SettingAdapter(getContext(), list);
        binding.recyclerView.setAdapter(adapter);

        binding.toolbar.item.setOnClickListener(v ->
                VOID.IntentExtra(getContext(), CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid));

        return binding.getRoot();
    }

    int CAST = 0, CAT = 0, FAV = 0;

    private void nrItems() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).
                child(DATA.FirebaseUserUid).child(DATA.CAST);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CAST = (int) dataSnapshot.getChildrenCount();
                nrCategories();
            }

            private void nrCategories() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).
                        child(DATA.FirebaseUserUid).child(DATA.CATEGORIES);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CAT = (int) dataSnapshot.getChildrenCount();
                        nrFavorites();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrFavorites() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                        .child(DATA.FirebaseUserUid);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FAV = (int) dataSnapshot.getChildrenCount();
                        loadSettings(CAST, CAT, FAV);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.USERS);
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User item = snapshot.getValue(User.class);
                assert item != null;
                String ProfileImage = item.getProfileImage();
                String Username = item.getUsername();
                String Contact = item.getEmail();
                VOID.Glide(true, getContext(), ProfileImage, binding.toolbar.imageProfile);
                binding.toolbar.username.setText(Username);
                binding.toolbar.email.setText(Contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSettings(int myCast, int myCategories, int favorites) {
        list.clear();
        Setting item = new Setting("1", "Edit Profile", R.drawable.ic_edit_white, 0, CLASS.PROFILE_EDIT);
        Setting item2 = new Setting("2", "My Cast", R.drawable.ic_cast, myCast, CLASS.MY_CAST);
        Setting item3 = new Setting("3", "My Categories", R.drawable.ic_category_gray, myCategories, CLASS.MY_CATEGORIES);
        Setting item4 = new Setting("4", "Favorites", R.drawable.ic_star_selected, favorites, CLASS.FAVORITES);
        Setting item5 = new Setting("5", "About App", R.drawable.ic_info, 0, null);
        Setting item6 = new Setting("6", "Logout", R.drawable.ic_logout_white, 0, null);
        Setting item7 = new Setting("7", "Share App", R.drawable.ic_share, 0, null);
        Setting item8 = new Setting("8", "Rate APP", R.drawable.ic_heart_selected, 0, null);
        Setting item9 = new Setting("9", "Privacy Policy", R.drawable.ic_privacy_policy, 0, CLASS.PRIVACY_POLICY);
        list.add(item);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(item5);
        list.add(item6);
        list.add(item7);
        list.add(item8);
        list.add(item9);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        loadUserInfo();
        nrItems();
        super.onResume();
    }
}