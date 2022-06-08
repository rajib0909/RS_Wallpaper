package com.rsdesign.wallpaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ItemPhotoListBinding;
import com.rsdesign.wallpaper.model.Result;

import java.util.List;
import java.util.Random;

public class ShowAllPhotoAdapter extends RecyclerView.Adapter<ShowAllPhotoAdapter.ViewHolder> {
    private List<Result> allResultList;
    private Context context;
    private int lastPosition = -1;
    public OnClickPhoto onClickPhoto;


    public void setOnClickPhoto(OnClickPhoto onClickPhoto) {
        this.onClickPhoto = onClickPhoto;

    }


    public ShowAllPhotoAdapter(List<Result> allResultList, Context context) {
        this.allResultList = allResultList;
        this.context = context;
    }


    public void clearAll() {
        this.allResultList.clear();
    }


    public void updatePhotoList(List<Result> allResultList) {
        this.allResultList.addAll(allResultList);
    }

    public interface OnClickPhoto {
        void onClickPhoto(int id);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoListBinding photoListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_photo_list, parent, false);

        return new ViewHolder(photoListBinding.getRoot(), photoListBinding);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result datum = allResultList.get(position);
        holder.bind(datum);

    }






    @Override
    public int getItemCount() {
        return allResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPhotoListBinding photoListBinding;

        public ViewHolder(@NonNull View itemView, ItemPhotoListBinding photoListBinding) {
            super(itemView);
            this.photoListBinding = photoListBinding;
        }

        public void bind(Result datum) {

          /*  RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.bongo)
                    .error(R.drawable.bongo);

            Glide.with(context).load("https://image.tmdb.org/t/p/original" + datum.getPosterPath()).apply(options).into(allTopRatedMovieBinding.posterImage);
*/
            photoListBinding.seeDetails.setOnClickListener(l -> onClickPhoto.onClickPhoto(1));

            photoListBinding.executePendingBindings();

        }

    }
}
