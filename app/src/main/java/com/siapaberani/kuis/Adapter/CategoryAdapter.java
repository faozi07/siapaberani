package com.siapaberani.kuis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import com.siapaberani.kuis.Model.Category;
import com.siapaberani.kuis.Network.ApiService;
import com.siapaberani.kuis.Network.NetworkCall;
import com.siapaberani.kuis.Network.QueryCallback;
import com.siapaberani.kuis.QuestionActivity;
import com.siapaberani.kuis.R;
import com.siapaberani.kuis.SplashActivity;
import com.siapaberani.kuis.SubCategoriesActivity;
import com.siapaberani.kuis.Utils.Config;
import com.siapaberani.kuis.Utils.SharedPref;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arif Khan on 12/29/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> category;
    private SharedPref sharedPref;
    private boolean isPurchased;

    public CategoryAdapter(Context context, List<Category> category, boolean isPurchased) {
        this.context = context;
        this.category = category;
        sharedPref = SharedPref.getPreferences(SplashActivity.context);
        this.isPurchased = isPurchased;
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        // Conditional Layout assign for Small & Regular Screen
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        if (screenHeight <= 800 & screenWidth <= 480) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_small, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        }
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.CategoryViewHolder holder, final int position) {
        final String data = new Gson().toJson(category.get(position));

        // Set Category Image
        if (category.get(position).getThumbnail() == null) {
            holder.ivCategoryImage.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        } else {
            Picasso.with(context)
                    .load(Config.BASE_URL + Config.CATEGORY_IMAGES_ROOT + category.get(position).getThumbnail())
                    .fit()
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.ivCategoryImage);
        }

        // Set Image Foreground if Android version is less than Marshmallow
        if (Build.VERSION.SDK_INT <= 22) {
            holder.ivCategoryImage.setColorFilter(Color.argb(127, 0, 0, 0));
        }

        // Set Other Data
        holder.tvCategoryTitle.setText(category.get(position).getTitle());
        holder.tvCategoryDes.setText(category.get(position).getDescription());
        if (Integer.valueOf(category.get(position).getChildrenCount()) > 0) {
            holder.tvQuestionCount.setVisibility(View.GONE);
        } else {
            if (category.get(position).getLimitQuestions() != null) {
                holder.tvQuestionCount.setText(String.valueOf(category.get(position).getLimitQuestions()));
            } else {
                holder.tvQuestionCount.setText(String.valueOf(category.get(position).getQuestionCount()));
            }
        }

        // OnClick Category open QuestionActiivty or Subcategory(if have)
        holder.layoutListCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String type;
                if (isPurchased) {
                    type = Config.API_SUFFIX_ALL;
                } else {
                    type = Config.API_SUFFIX_FREE;
                }

                if (Integer.valueOf(category.get(position).getChildrenCount()) > 0) {
                    ApiService apiService = new NetworkCall();
                    apiService.getSubCategories(category.get(position).getId(), type, new QueryCallback<List<Category>>() {
                        @Override
                        public void onSuccess(List<Category> data) {
                            String categories = new Gson().toJson(data);
                            sharedPref.setStringData(category.get(position).getTitle(), categories);
                            Intent sr = new Intent(holder.layoutListCategory.getContext(), SubCategoriesActivity.class);
                            sr.putExtra(Config.CATEGORY, category.get(position).getTitle());
                            sr.putExtra(Config.CATEGORY_ID, category.get(position).getId().toString());
                            view.getContext().startActivity(sr);
                        }

                        @Override
                        public void onError(Throwable th) {
                            String subCategortData = sharedPref.getStringData(category.get(position).getTitle(), "");
                            if (subCategortData != null) {
                                Intent sr = new Intent(holder.layoutListCategory.getContext(), SubCategoriesActivity.class);
                                sr.putExtra(Config.CATEGORY, category.get(position).getTitle());
                                sr.putExtra(Config.CATEGORY_ID, category.get(position).getId().toString());
                                view.getContext().startActivity(sr);
                            } else {
                                Toast.makeText(context, R.string.internet_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Intent sr = new Intent(holder.layoutListCategory.getContext(), QuestionActivity.class);
                    view.getContext().startActivity(sr.putExtra(Config.QUESTIONS_DATA, data));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layoutListCategory)
        ConstraintLayout layoutListCategory;
        @BindView(R.id.ivCategoryImage)
        ImageView ivCategoryImage;
        @BindView(R.id.tvCategoryTitle)
        TextView tvCategoryTitle;
        @BindView(R.id.tvQuestionCount)
        TextView tvQuestionCount;
        @BindView(R.id.tvCategoryDes)
        TextView tvCategoryDes;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
