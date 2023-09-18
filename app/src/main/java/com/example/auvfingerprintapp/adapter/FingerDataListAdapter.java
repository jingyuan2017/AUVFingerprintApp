package com.example.auvfingerprintapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.auvfingerprintapp.R;
import com.example.auvfingerprintapp.bean.Data;
import com.example.auvfingerprintapp.view.FingerRegisterDialog;

import java.util.List;

public class FingerDataListAdapter extends BaseQuickAdapter<Data, BaseViewHolder> {


    public FingerDataListAdapter(int layoutResId, @Nullable List<Data> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder holder, Data data) {
        holder.setText(R.id.main_rv_item_id, "ID :" + data.getId());
        holder.setText(R.id.main_rv_item_name, data.getNickname());

        if (data.getFingerprint_feature().isEmpty()){
            holder.findView(R.id.main_rv_item_btn_register).setVisibility(View.VISIBLE);
            holder.findView(R.id.main_rv_item_btn_del).setVisibility(View.GONE);
            holder.findView(R.id.main_rv_item_img_finger).setVisibility(View.GONE);
        }else {
            holder.findView(R.id.main_rv_item_btn_register).setVisibility(View.GONE);
            holder.findView(R.id.main_rv_item_btn_del).setVisibility(View.VISIBLE);
            holder.findView(R.id.main_rv_item_img_finger).setVisibility(View.VISIBLE);
        }
    }
}
