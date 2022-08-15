package com.flatcode.littlemovie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovie.Filter.MovieFilter;
import com.flatcode.littlemovie.Model.Movie;
import com.flatcode.littlemovie.R;
import com.flatcode.littlemovie.Unit.CLASS;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.Unit.VOID;
import com.flatcode.littlemovie.databinding.ItemMovieBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {

    private ItemMovieBinding binding;
    private final Context context;

    public ArrayList<Movie> list, filterList;
    private MovieFilter filter;
    private final Boolean animation;

    public MovieAdapter(Context context, ArrayList<Movie> list, Boolean animation) {
        this.context = context;
        this.list = list;
        this.filterList = list;
        this.animation = animation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemMovieBinding.inflate(LayoutInflater.from(context), parent, false);
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

        VOID.Glide(false, context, image, holder.image);

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

        VOID.isFavorite(holder.add, id, DATA.FirebaseUserUid);
        holder.add.setOnClickListener(view -> VOID.checkFavorite(holder.add, id));

        VOID.isLoves(holder.love, id);
        VOID.nrLoves(holder.numberLoves, id);
        holder.love.setOnClickListener(view -> VOID.checkLove(holder.love, id));

        if (animation)
            holder.item.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));

        holder.item.setOnClickListener(view -> VOID.IntentExtra2(context, CLASS.MOVIE_DETAILS,
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

        ImageView image, add, love;
        TextView name, numberViews, numberLoves;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            love = binding.love;
            add = binding.add;
            name = binding.name;
            numberViews = binding.nrViews;
            numberLoves = binding.nrLoves;
            item = binding.item;
        }
    }
}