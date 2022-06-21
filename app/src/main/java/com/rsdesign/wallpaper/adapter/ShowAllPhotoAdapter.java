package com.rsdesign.wallpaper.adapter;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ItemAdminPhotoListBinding;
import com.rsdesign.wallpaper.databinding.ItemPhotoListBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;

import java.util.List;
import java.util.Random;

public class ShowAllPhotoAdapter extends RecyclerView.Adapter<ShowAllPhotoAdapter.ViewHolder> {
    private List<Datum> allResultList;
    private Context context;
    public OnClickPhoto onClickPhoto;


    public void setOnClickPhoto(OnClickPhoto onClickPhoto) {
        this.onClickPhoto = onClickPhoto;

    }


    public ShowAllPhotoAdapter(List<Datum> allResultList, Context context) {
        this.allResultList = allResultList;
        this.context = context;
    }


    public void clearAll() {
        this.allResultList.clear();
    }


    public void updatePhotoList(List<Datum> allResultList) {
        this.allResultList.addAll(allResultList);
    }

    public interface OnClickPhoto {
        void onClickPhoto(int id);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemAdminPhotoListBinding photoListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_admin_photo_list, parent, false);

        return new ViewHolder(photoListBinding.getRoot(), photoListBinding);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datum datum = allResultList.get(position);
        holder.bind(datum);

    }






    @Override
    public int getItemCount() {
        return allResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminPhotoListBinding photoListBinding;

        public ViewHolder(@NonNull View itemView, ItemAdminPhotoListBinding photoListBinding) {
            super(itemView);
            this.photoListBinding = photoListBinding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Datum datum) {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo);

            Glide.with(context).load(datum.getImage()).apply(options).into(photoListBinding.image);
            photoListBinding.tag.setText(datum.getTags());
            photoListBinding.category.setText(datum.getCategories().get(0).getName());
            photoListBinding.reportCount.setText("DMCA report ("+ datum.getCopyrightReport()+")");
           // photoListBinding.seeDetails.setOnClickListener(l -> onClickPhoto.onClickPhoto(1));

            photoListBinding.executePendingBindings();

        }

    }
}
