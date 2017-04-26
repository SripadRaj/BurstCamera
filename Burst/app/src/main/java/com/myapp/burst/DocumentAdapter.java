package com.myapp.burst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myapp.burst.CameraListener;
import com.myapp.burst.FooterViewHolder;
import com.myapp.burst.ItemViewHolder;
import com.myapp.burst.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by SRamesh on 3/7/2017.
 */

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Uri> documents;
    CameraListener cameraListener;
    Bitmap mBitmap;

    public DocumentAdapter(Context context, List<Uri> documents, CameraListener cameraListener){
        this.context = context;
        this.documents = documents;
        this.cameraListener = cameraListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ViewType.NORMAL){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image,null);
            return new ItemViewHolder(v);
        }else if(viewType == ViewType.FOOTER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item,null);
            return new FooterViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Uri uri = documents.get(position);

            /*try{
                Bitmap bitmap = new LoadingHandler().execute(documents.get(position)).get();
                itemViewHolder.docImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }*/

            Log.e("uri","image uri: "+ uri);

            Glide.with(context).load(uri).override(200,200).into(itemViewHolder.docImage);


            itemViewHolder.removeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    documents.remove(position);
                    notifyDataSetChanged();
                }
            });

            itemViewHolder.docImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,FullImageAcitvity.class);
                    intent.putExtra("uri",uri.toString());
                    context.startActivity(intent);
                }
            });

        }else if(holder instanceof FooterViewHolder){
            ((FooterViewHolder) holder).getCameraImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cameraListener!=null){
                        cameraListener.onCameraClicked();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return documents.size() + 1;
    }


    private class ViewType{
        public static final int NORMAL = 0;
        public static final int FOOTER = 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionFooter(position)) {
            return ViewType.FOOTER;
        }else{
            return ViewType.NORMAL;
        }
    }

    private boolean isPositionFooter(int position){
        return position == documents.size();
    }



    public Bitmap getBitmap(byte[] byteArray) {
        try {

            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            mBitmap = decodeSampledBitmapFromResource(byteArray,50,50);
//            mBitmap.compress(Bitmap.CompressFormat.JPEG,10,blob);


        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        return mBitmap;
    }


    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;	//Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(byte[] bytearray,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length,options);
    }

    public class LoadingHandler extends AsyncTask<byte[],Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(byte[]... bytes) {
            return getBitmap(bytes[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    public Uri getImage(){
        return documents.get(0);
    }


}
