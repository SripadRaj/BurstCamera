    package com.myapp.burst;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by SRamesh on 3/8/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{
    public ImageView docImage,removeImage;

    public ItemViewHolder(View itemView) {
        super(itemView);
        docImage = (ImageView) itemView.findViewById(R.id.doc_image);
        removeImage = (ImageView) itemView.findViewById(R.id.remove_image);
    }

    public ImageView getDocImage() {
        return docImage;
    }
}
