package com.hongsam.famstrory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hongsam.famstrory.R;
import com.hongsam.famstrory.data.LetterPaper;

import java.util.ArrayList;

public class LetterPaperAdapter extends RecyclerView.Adapter<LetterPaperAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LetterPaper> letterPaperItemList;

    public LetterPaperAdapter(Context context, ArrayList<LetterPaper> letterPaperItemList){
        this.context = context;
        this.letterPaperItemList = letterPaperItemList;
    }

    @NonNull
    @Override
    public LetterPaperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_letter_paper, parent, false);
        LetterPaperAdapter.ViewHolder viewHolder = new LetterPaperAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull LetterPaperAdapter.ViewHolder holder,final int position) {

        holder.paperImage.setImageResource(letterPaperItemList.get(position).getImage());

    }

    @Override
    public int getItemCount() { return letterPaperItemList.size(); }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView paperImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paperImage = itemView.findViewById(R.id.letter_paper_icon);
        }
    }
}