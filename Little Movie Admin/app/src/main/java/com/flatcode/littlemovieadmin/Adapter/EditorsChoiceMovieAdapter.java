package com.flatcode.littlemovieadmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovieadmin.Filter.EditorsChoiceFilter;
import com.flatcode.littlemovieadmin.Model.Movie;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ItemEditorsChoiceBinding;

import java.util.ArrayList;

public class EditorsChoiceMovieAdapter extends RecyclerView.Adapter<EditorsChoiceMovieAdapter.ViewHolder> implements Filterable {

    private ItemEditorsChoiceBinding binding;
    private final Activity activity;

    public ArrayList<Movie> list, filterList;
    private EditorsChoiceFilter filter;
    public int number;
    public String oldId;

    public EditorsChoiceMovieAdapter(Activity activity, String oldId, ArrayList<Movie> list, int number) {
        this.oldId = oldId;
        this.activity = activity;
        this.list = list;
        this.filterList = list;
        this.number = number;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Movie item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String nrViews = DATA.EMPTY + item.getViewsCount();
        String nrLoves = DATA.EMPTY + item.getLovesCount();

        VOID.Glide(false, activity, image, holder.image);

        if (name.equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        holder.nrViews.setText(nrViews);
        holder.nrLoves.setText(nrLoves);

        holder.add.setOnClickListener(view -> {
            if (oldId != null) {
                VOID.addToEditorsChoice(activity, activity, id, number);
                VOID.addToEditorsChoice(activity, activity, oldId, 0);
            } else {
                VOID.addToEditorsChoice(activity, activity, id, number);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new EditorsChoiceFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView add, image;
        TextView name, nrViews, nrLoves;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            nrViews = binding.nrViews;
            nrLoves = binding.nrLoves;
            add = binding.add;
            item = binding.item;
        }
    }
}