package com.rsdesign.wallpaper.adapter;

import static com.rsdesign.wallpaper.util.utils.searchJobCategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ItemPhotoCategoryBinding;
import com.rsdesign.wallpaper.model.Result;
import com.rsdesign.wallpaper.model.categoryList.Datum;

import java.util.List;

public class ShowAllCategoryAdapter extends RecyclerView.Adapter<ShowAllCategoryAdapter.ViewHolder> {
    private List<Datum> allResultList;
    private Context context;

    public OnClickCategory onClickCategory;


    public void setOnClickCategory(OnClickCategory onClickCategory) {
        this.onClickCategory = onClickCategory;

    }


    public ShowAllCategoryAdapter(List<Datum> allResultList, Context context) {
        this.allResultList = allResultList;
        this.context = context;
    }


    public void clearAll() {
        this.allResultList.clear();
    }


    public void updateCategoryList(List<Datum> allResultList) {
        this.allResultList.addAll(allResultList);
    }

    public interface OnClickCategory {
        void onClickCategory(int id);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoCategoryBinding categoryBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_photo_category, parent, false);

        return new ViewHolder(categoryBinding.getRoot(), categoryBinding);
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
        private final ItemPhotoCategoryBinding categoryBinding;

        public ViewHolder(@NonNull View itemView, ItemPhotoCategoryBinding categoryBinding) {
            super(itemView);
            this.categoryBinding = categoryBinding;
        }

        public void bind(Datum datum) {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo);
            if (datum.getImage() instanceof String){
                Glide.with(context).load(datum.getImage()).apply(options).into(categoryBinding.categoryImage);
            }

            categoryBinding.category.setOnClickListener(l -> {
                searchJobCategory = datum.getDisplayName();
                onClickCategory.onClickCategory(datum.getId());
            });
            categoryBinding.categoryName.setText(datum.getDisplayName());

            categoryBinding.executePendingBindings();

        }
    }
}
