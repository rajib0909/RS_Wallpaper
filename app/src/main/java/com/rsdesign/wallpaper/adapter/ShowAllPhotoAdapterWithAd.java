package com.rsdesign.wallpaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.material.card.MaterialCardView;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.util.utils;

import java.util.List;

public class ShowAllPhotoAdapterWithAd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_PHOTO = 978;
    private static final int ITEM_TYPE_BANNER_AD = 979;
    private List<Object> allResultList;
    private Context context;

    public OnClickPhoto onClickPhoto;


    public void setOnClickPhoto(ShowAllPhotoAdapterWithAd.OnClickPhoto onClickPhoto) {
        this.onClickPhoto = onClickPhoto;

    }


    public ShowAllPhotoAdapterWithAd(List<Object> allResultList, Context context) {
        this.allResultList = allResultList;
        this.context = context;
    }

    public void updatePhotoList(List<Object> allResultList) {
        this.allResultList.addAll(allResultList);
    }

    public interface OnClickPhoto {
        void onClickPhoto(int id);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType)
        {
            case ITEM_TYPE_BANNER_AD:
                View bannerLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_ad, parent, false);
                //Create View Holder
                AdVIewHolder adVIewHolder = new AdVIewHolder(bannerLayoutView);
                return adVIewHolder;
            case ITEM_TYPE_PHOTO:
            default:
                View photoLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_list, parent, false);
                //Create View Holder
                PhotoVIewHolder photoVIewHolder = new PhotoVIewHolder(photoLayoutView);
                return photoVIewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType)
        {
            case ITEM_TYPE_BANNER_AD:
                if (allResultList.get(position) instanceof AdView)
                {
                    AdVIewHolder adVIewHolder = (AdVIewHolder) holder;
                    AdView adView = (AdView) allResultList.get(position);
                    ViewGroup adCardView = (ViewGroup) adVIewHolder.itemView;
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.getChildCount() > 0)
                    {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null)
                    {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);



                }
                break;
            case ITEM_TYPE_PHOTO:
            default:
                if (allResultList.get(position) instanceof Datum)
                {
                    PhotoVIewHolder photoVIewHolder = (PhotoVIewHolder) holder;
                    Datum result = (Datum) allResultList.get(position);

                    //Set Title Name
                    photoVIewHolder.photoTitle.setText(result.getTitle());
                    photoVIewHolder.photoType.setText(result.getCategories().get(0).getName());
                    photoVIewHolder.seeDetails.setOnClickListener(l -> onClickPhoto.onClickPhoto(1));
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo);

                    Glide.with(context).load(result.getImage()).apply(options).into(photoVIewHolder.image);

                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allResultList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0 || allResultList.get(position) instanceof Datum){
            return ITEM_TYPE_PHOTO;
        }else{
            if (position % utils.AD_PER_PHOTO ==0){
                return ITEM_TYPE_BANNER_AD;
            }else
                return ITEM_TYPE_PHOTO;
        }
    }

    //photo view holder
    class PhotoVIewHolder extends RecyclerView.ViewHolder{
        TextView photoTitle, photoType;
        ImageView image;
        MaterialCardView seeDetails;


        public PhotoVIewHolder(@NonNull View itemView) {
            super(itemView);
            photoTitle = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            photoType = itemView.findViewById(R.id.photoType);
            seeDetails = itemView.findViewById(R.id.seeDetails);

        }
    }

    //Ad view holder
    class AdVIewHolder extends RecyclerView.ViewHolder{
       // TemplateView nativeAd;

        public AdVIewHolder(@NonNull View itemView) {
            super(itemView);
           // nativeAd = itemView.findViewById(R.id.my_native_template);

        }
    }


}
