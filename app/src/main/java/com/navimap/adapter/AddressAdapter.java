package com.navimap.adapter;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.navimap.PickupActivity;
import com.navimap.R;
import com.navimap.utils.MapUtils;
import com.navimap.utils.StringUtils;

import java.util.List;

/**
 * Created by Makvit on 21.07.2015.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private List<PickupActivity.AddressDTO> mDataset;
    private ViewHolder.IMyViewHolderClicks listener;
    LatLng myLatLng;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AddressAdapter(List<PickupActivity.AddressDTO> myDataset, ViewHolder.IMyViewHolderClicks listener, LatLng myLatLng) {
        mDataset = myDataset;
        this.listener = listener;
        this.myLatLng = myLatLng;
    }

    public void setItems(List<PickupActivity.AddressDTO> mDataset, boolean notify) {
        this.mDataset = mDataset;
        if (notify)
            notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        CardView container = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_list_item, parent, false);

        ViewHolder vh = new ViewHolder(container, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PickupActivity.AddressDTO addressDTO = mDataset.get(position);
        if (StringUtils.isNullOrEmpty(addressDTO.getNaviAddress())) {
            holder.naviAddressContainer.setVisibility(View.INVISIBLE);
        } else {
            String value= addressDTO.getNaviAddress();
            holder.naviTextView.setText(addressDTO.getNaviAddress());
            if (myLatLng != null) {
                MapUtils.City city = MapUtils.getNearestCity(myLatLng);
                if (value.contains("(" + city.getNaviCode().replace("0", "+") + ")")) {
                    int start = value.indexOf("(");
                    int end = value.indexOf(")") + 1;
                    if (start<end) {
                        Spannable wordtoSpan = new SpannableString(value);
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.LTGRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.naviTextView.setText(wordtoSpan);
                    }
                }
            }
            holder.naviAddressContainer.setVisibility(View.VISIBLE);

        }
        if (StringUtils.isNullOrEmpty(addressDTO.getAddressName()))
            holder.nameTextView.setText("");
        else
            holder.nameTextView.setText(addressDTO.getAddressName());
        if (addressDTO.isFavorite())
            holder.favoriteImageView.setImageResource(android.R.drawable.btn_star_big_on);
        else
            holder.favoriteImageView.setImageResource(android.R.drawable.btn_star_big_off);
        holder.container.setTag(addressDTO);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateItem(PickupActivity.AddressDTO addressDTO) {
        for (PickupActivity.AddressDTO item : mDataset) {
            if (item.getNaviAddress().equals(addressDTO.getNaviAddress()) && item.getAddressName().equals(addressDTO.getAddressName())) {
                item.setIsFavorite(addressDTO.isFavorite());
            }
        }
        notifyDataSetChanged();

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        public CardView container;
        public TextView naviTextView;
        public RelativeLayout naviAddressContainer;
        public TextView nameTextView;
        public ImageView favoriteImageView;
        public IMyViewHolderClicks mListener;

        public ViewHolder(CardView container, IMyViewHolderClicks listener) {
            super(container);
            mListener = listener;
            this.container = container;
            naviTextView = (TextView) container.findViewById(R.id.naviTextView);
            naviAddressContainer = (RelativeLayout) container.findViewById(R.id.naviAddressContainer);
            nameTextView = (TextView) container.findViewById(R.id.nameTextView);
            favoriteImageView = (ImageView) container.findViewById(R.id.favoriteImageView);
            container.setOnClickListener(this);
            naviTextView.setOnClickListener(this);
            naviAddressContainer.setOnClickListener(this);
            nameTextView.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteImageView) {
                mListener.onFavoriteClick((PickupActivity.AddressDTO) container.getTag());
            } else {
                mListener.onItemClick((PickupActivity.AddressDTO) container.getTag());
            }
        }

        public static interface IMyViewHolderClicks {
            public void onItemClick(PickupActivity.AddressDTO addressDTO);

            public void onFavoriteClick(PickupActivity.AddressDTO addressDTO);
        }
    }


}