package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {
    private Context mContext;
    private List<RouterData> mRouterData;
    public RouteAdapter(Context context,List<RouterData> mRouterData) {
        this.mContext = context;
        this.mRouterData=mRouterData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_routecard, parent, false);


        return new ViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.description.setText(mRouterData.get(i).getDescription());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,mRouterData.get(i).getLatLng()+"",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mRouterData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            description=itemView.findViewById(R.id.description);
            cardView=itemView.findViewById(R.id.cardView);
        }
    }
}
