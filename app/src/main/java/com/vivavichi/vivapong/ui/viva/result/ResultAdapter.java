package com.vivavichi.vivapong.ui.viva.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivavichi.vivapong.databinding.ItemResultBinding;
import com.vivavichi.vivapong.model.ResultModel;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultViewHolder> {

    private ArrayList<ResultModel> dbList = new ArrayList<>();

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemResultBinding itemResultBinding = ItemResultBinding.inflate(layoutInflater, parent, false);
        return new ResultViewHolder(itemResultBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.bind(dbList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dbList.size();
    }

    public void updateResult(ArrayList<ResultModel> list){
        this.dbList = list;
        notifyDataSetChanged();
    }
}
