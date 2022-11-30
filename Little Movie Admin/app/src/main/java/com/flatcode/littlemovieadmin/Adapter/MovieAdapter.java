package com.flatcode.littlemovieadmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovieadmin.Filter.MovieFilter;
import com.flatcode.littlemovieadmin.Model.Movie;
import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ItemMovieBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {

    private ItemMovieBinding binding;
    private final Activity activity;

    public ArrayList<Movie> list, filterList;
    private MovieFilter filter;

    public MovieAdapter(Activity activity, ArrayList<Movie> list) {
        this.activity = activity;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemMovieBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Movie item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String viewsCount = DATA.EMPTY + item.getViewsCount();
        String lovesCount = DATA.EMPTY + item.getLovesCount();
        String movieLink = DATA.EMPTY + item.getMovieLink();
        String categoeyId = DATA.EMPTY + item.getCategoryId();

        VOID.Glide(false, activity, image, holder.image);

        if (item.getName().equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        if (viewsCount.equals(DATA.EMPTY))
            holder.numberViews.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberViews.setText(viewsCount);

        if (lovesCount.equals(DATA.EMPTY))
            holder.numberLoves.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberLoves.setText(lovesCount);

        VOID.isFavorite(holder.add, item.getId(), DATA.FirebaseUserUid);
        holder.add.setOnClickListener(view -> VOID.checkFavorite(holder.add, id));

        holder.item.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_transition_animation));

        holder.more.setOnClickListener(v -> VOID.moreDeleteMovie(activity, item, DATA.CATEGORIES, categoeyId
                , DATA.MOVIES_COUNT, false, true));

        holder.item.setOnClickListener(view -> VOID.IntentExtra2(activity, CLASS.MOVIE_DETAILS,
                DATA.MOVIE_ID, id, DATA.MOVIE_LINK, movieLink));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MovieFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, add;
        ImageButton more;
        TextView name, numberViews, numberLoves;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            add = binding.add;
            name = binding.name;
            more = binding.more;
            numberViews = binding.nrViews;
            numberLoves = binding.nrLoves;
            item = binding.item;
        }
    }
}