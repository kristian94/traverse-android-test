package com.cborum.traverse;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cborum.traverse.backend.Place;
import com.cborum.traverse.backend.TraverseApiWrapper;

import java.util.Locale;

/**
 * Created by ChristopherBorum on 08/10/16.
 * help from http://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private TraverseApiWrapper taw;
    private OnItemClickListener mItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cardView;
        public TextView title, count;
        public ImageView thumbnail, visited;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            title = (TextView) view.findViewById(R.id.title);
            visited = (ImageView) view.findViewById(R.id.visited);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    public CardAdapter(Context mContext) {
        this.mContext = mContext;
        this.taw = TraverseApiWrapper.Companion.getInstance();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // todo if the places havent changed don't reload photo etc.
        // todo some images are incorrect

        Place place = taw.getPlaces().get(position);
        holder.title.setText(place.getName());

        if (place.getVisited()) {
            holder.visited.setVisibility(View.VISIBLE);
        } else {
            holder.visited.setVisibility(View.INVISIBLE);
        }
        if (taw.getLastKnownLocation() != null) {
            Location placeLocation = new Location("");
            placeLocation.setLatitude(place.getGeometry().getLocation().getLat());
            placeLocation.setLongitude(place.getGeometry().getLocation().getLng());
            holder.count.setText(getFormattedDistanceTo(placeLocation));
        }

        if (place.getPhotos() != null && place.getPhotos().size() > 0 && place.getPhotos().get(0).getPhoto_reference() != "") {
            String photoUrl = taw.getPhotoUrl(place.getPhotos().get(0).getPhoto_reference());
            Glide.with(mContext).load(photoUrl).centerCrop().crossFade().into(holder.thumbnail);
        } else {
//            Glide.with(mContext).load(R.drawable.travel_card_paris_01).centerCrop().crossFade().into(holder.thumbnail);
        }
    }

    private String getFormattedDistanceTo(Location location) {
        float distance = taw.getLastKnownLocation().distanceTo(location);
        if (distance < 500) {
            return String.format(Locale.ENGLISH, "%.0f m", distance);
        } else {
            return String.format(Locale.ENGLISH, "%.2f km", distance / 1000);
        }
    }

    @Override
    public int getItemCount() {
        return taw.getPlacesSize();
    }

//    public void updateNoPlacesFound(){
//        if(taw.getPlaces() == null || taw.getPlaces().size() == 0){
//            System.out.println("set visible");
//            findViewById(R.id.noPlacesFound).setVisibility(View.VISIBLE);
//        }else{
//            System.out.println("set invisible");
//            getActivity().findViewById(R.id.noPlacesFound).setVisibility(View.INVISIBLE);
//        }
//    }

}