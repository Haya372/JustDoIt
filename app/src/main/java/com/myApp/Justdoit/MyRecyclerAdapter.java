package com.myApp.Justdoit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>{
    private LayoutInflater mInflater;
    private ArrayList<Memo> mDataset;
    private MyRecyclerListener mListener;
    private Context mContext;

    MyRecyclerAdapter(Context context,ArrayList<Memo>dateset,MyRecyclerListener listener){
        mInflater = LayoutInflater.from(context);
        mContext=context;
        mDataset=dateset;
        mListener=listener;
    }
    //Layout ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        CheckBox locked;
        ConstraintLayout constraintLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.recyclerView_title);
            date=(TextView)itemView.findViewById(R.id.recyclerView_date);
            locked=(CheckBox)itemView.findViewById(R.id.recyclerView_checkBox);
            constraintLayout=(ConstraintLayout)itemView.findViewById(R.id.constraintLayout);
        }
    }

    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        return new MyViewHolder(mInflater.inflate(R.layout.recycler_view_content, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder,final  int i){
        //bind data
        if(mDataset!=null&&i<mDataset.size()&&mDataset.get(i)!=null){
            Memo memo=mDataset.get(i);
            viewHolder.title.setText(memo.getTitle());
            viewHolder.date.setText(memo.getEditDate());
            viewHolder.locked.setChecked(memo.isLocked());
        }
        //clicked
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRecyclerClicked(v,i);
            }
        });
        viewHolder.locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRecyclerClicked(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else {
            return 0;
        }
    }

    //swiped action
    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
}
