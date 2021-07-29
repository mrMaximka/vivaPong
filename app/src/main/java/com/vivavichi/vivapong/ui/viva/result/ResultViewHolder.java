package com.vivavichi.vivapong.ui.viva.result;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vivavichi.vivapong.R;
import com.vivavichi.vivapong.databinding.ItemResultBinding;
import com.vivavichi.vivapong.model.ResultModel;

public class ResultViewHolder extends RecyclerView.ViewHolder {

    public ResultViewHolder(@NonNull ItemResultBinding itemResultBinding) {
        super(itemResultBinding.getRoot());
    }

    public void bind(ResultModel model, int position){

        CardView recordContainer = itemView.findViewById(R.id.result_container);
        TextView itemRecordPos = itemView.findViewById(R.id.item_result_pos);
        TextView itemRecordDate = itemView.findViewById(R.id.item_result_date);
        TextView itemRecordScore = itemView.findViewById(R.id.item_result_score);

        if (model.getDate().equals("unknown")){
            recordContainer.setVisibility(View.GONE);
        }else{
            itemRecordPos.setText(String.valueOf(position + 1));
            itemRecordDate.setText(model.getDate());
            itemRecordScore.setText(String.valueOf(model.getScore()));
        }
    }
}
