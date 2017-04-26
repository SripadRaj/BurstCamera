package com.myapp.burst;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by SRamesh on 3/8/2017.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {

    ImageView cameraImage;

    public FooterViewHolder(View itemView) {
        super(itemView);
        cameraImage = (ImageView) itemView.findViewById(R.id.footer);
    }

    public ImageView getCameraImage() {
        return cameraImage;
    }
}
