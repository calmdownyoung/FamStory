package com.hongsam.famstrory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hongsam.famstrory.R;
import com.hongsam.famstrory.data.LetterContants;

import java.util.ArrayList;
import java.util.List;

public class LetterListAdapter extends RecyclerView.Adapter<LetterListAdapter.ViewHolder> {

    ArrayList<LetterContants> letterItemList;
    private Context context;

    public LetterListAdapter(ArrayList<LetterContants> letterItemList, Context context) {

        this.letterItemList = letterItemList;
        this.context = context;


    }

    @NonNull
    @Override
    public LetterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.latter_list_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LetterListAdapter.ViewHolder holder, final int position) {

        holder.sender.setText(letterItemList.get(position).getSender());
        holder.contants.setText(letterItemList.get(position).getContants());
        holder.date.setText(letterItemList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return letterItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView sender;
        public TextView contants;
        public TextView date;
        public LinearLayout linearLayout;
        public RelativeLayout viewBackgound;

        //뷰홀더 생성자로 전달되는 뷰 객체 참조
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰객체에 들어있는 텍스트뷰 등 참고
//            itemImage=itemView.findViewById(R.id.itemImg);
            sender = itemView.findViewById(R.id.ltter_list_sender_tv);
            contants = itemView.findViewById(R.id.ltter_list_contents_tv);
            date = itemView.findViewById(R.id.ltter_list_date_tv);
            linearLayout = itemView.findViewById(R.id.layout_id);
            viewBackgound = itemView.findViewById(R.id.view_background);

        }
    }

    //아이템 지우기
    public void removeItem(int position) {
        letterItemList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }
}

