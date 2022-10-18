package com.rsdesign.wallpaper.adapter;

import static com.rsdesign.wallpaper.util.utils.convertCount;

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
import com.rsdesign.wallpaper.model.userProfile.Wallpaper;
import com.rsdesign.wallpaper.util.utils;

import java.util.List;

public class ShowUserPhotoAdapterWithAd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_PHOTO = 978;
    private static final int ITEM_TYPE_BANNER_AD = 979;
    private List<Object> allResultList;
    private Context context;

    public OnClickPhoto onClickPhoto;


    public void setOnClickPhoto(ShowUserPhotoAdapterWithAd.OnClickPhoto onClickPhoto) {
        this.onClickPhoto = onClickPhoto;
    }


    public ShowUserPhotoAdapterWithAd(List<Object> allResultList, Context context) {
        this.allResultList = allResultList;
        this.context = context;
    }

    public void updatePhotoList(List<Object> allResultList) {
        this.allResultList.addAll(allResultList);
    }

    public interface OnClickPhoto {
        void onClickPhoto(Wallpaper datum);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
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

        switch (viewType) {
            case ITEM_TYPE_BANNER_AD:
                if (allResultList.get(position) instanceof AdView) {
                    AdVIewHolder adVIewHolder = (AdVIewHolder) holder;
                    AdView adView = (AdView) allResultList.get(position);
                    ViewGroup adCardView = (ViewGroup) adVIewHolder.itemView;
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);


                }
                break;
            case ITEM_TYPE_PHOTO:
            default:
                if (allResultList.get(position) instanceof Wallpaper) {
                    PhotoVIewHolder photoVIewHolder = (PhotoVIewHolder) holder;
                    Wallpaper result = (Wallpaper) allResultList.get(position);

                    if (position == 4){
                        photoVIewHolder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = photoVIewHolder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.itemView.setLayoutParams(params);
                    }

                    //Set Title Name
                    photoVIewHolder.photoTitle.setText(result.getTitle());
                    photoVIewHolder.photoType.setText(result.getCategories().get(0).getName());
                    photoVIewHolder.likeCount.setText(convertCount(Integer.parseInt(result.getLike())));
                    photoVIewHolder.seeDetails.setOnClickListener(l -> onClickPhoto.onClickPhoto(result));

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
        if (position == 0 || allResultList.get(position) instanceof Wallpaper) {
            return ITEM_TYPE_PHOTO;
        } else {
            if (position % utils.AD_PER_PHOTO == 0) {
                return ITEM_TYPE_BANNER_AD;
            } else
                return ITEM_TYPE_PHOTO;
        }
    }

    //photo view holder
    class PhotoVIewHolder extends RecyclerView.ViewHolder {
        TextView photoTitle, photoType, likeCount;
        ImageView image, favourite;
        MaterialCardView seeDetails;


        public PhotoVIewHolder(@NonNull View itemView) {
            super(itemView);
            photoTitle = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            photoType = itemView.findViewById(R.id.photoType);
            seeDetails = itemView.findViewById(R.id.seeDetails);
            likeCount = itemView.findViewById(R.id.likeCount);
            favourite = itemView.findViewById(R.id.favourite);

        }
    }

    //Ad view holder
    class AdVIewHolder extends RecyclerView.ViewHolder {
        // TemplateView nativeAd;

        public AdVIewHolder(@NonNull View itemView) {
            super(itemView);
            // nativeAd = itemView.findViewById(R.id.my_native_template);

        }
    }


}