package com.flatcode.littlemovieadmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovieadmin.Filter.CastFilter;
import com.flatcode.littlemovieadmin.Model.Cast;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ItemCastBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> implements Filterable {

    private ItemCastBinding binding;
    private final Activity activity;

    public ArrayList<Cast> list, filterList;
    private CastFilter filter;

    public CastAdapter(Activity activity, ArrayList<Cast> list) {
        this.activity = activity;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCastBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Cast item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String aboutMy = DATA.EMPTY + item.getAboutMy();
        String interestedCount = DATA.EMPTY + item.getInterestedCount();
        String moviesCount = DATA.EMPTY + item.getMoviesCount();

        VOID.Glide(true, activity, image, holder.image);

        if (item.getName().equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        if (interestedCount.equals(DATA.EMPTY))
            holder.numberInterested.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberInterested.setText(interestedCount);

        if (moviesCount.equals(DATA.EMPTY))
            holder.numberMovies.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberMovies.setText(moviesCount);

        holder.more.setOnClickListener(v -> VOID.moreDeleteCast(activity, item, DATA.NULL, DATA.NULL, DATA.NULL
                , DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL));

        holder.item.setOnClickListener(view ->
                VOID.IntentExtra4(activity, CLASS.CAST_DETAILS, DATA.CAST_ID, id, DATA.CAST_NAME,
                        name, DATA.CAST_IMAGE, image, DATA.CAST_ABOUT, aboutMy));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CastFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageButton more;
        TextView name, numberMovies, numberInterested;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            more = binding.more;
            numberMovies = binding.numberMovies;
            numberInterested = binding.numberInterested;
            item = binding.item;
        }
    }
}