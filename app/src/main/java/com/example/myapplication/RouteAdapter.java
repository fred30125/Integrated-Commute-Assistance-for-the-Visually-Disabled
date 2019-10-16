package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<RouterData> mRouterData;
    //define interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;

        public ViewHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);
            description=itemView.findViewById(R.id.description);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
    public RouteAdapter(List<RouterData> mRouterData) {
        this.mRouterData=mRouterData;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_routecard, parent, false);
        ViewHolder evh = new ViewHolder(view,mOnItemClickListener);
        return  evh;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        RouterData routerData=mRouterData.get(i);
        viewHolder.description.setText(routerData.getDescription());

    }


    @Override
    public int getItemCount() {
        return mRouterData.size();
    }



}
