package com.myapp.burst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRamesh on 3/20/2017.
 */

public class GridViewActivity extends Activity implements CameraListener{

    RecyclerView imagesRecyclerView;
//    public static List<Bitmap> documents;
    DocumentAdapter documentAdapter;
    ImagePicker imagePicker;
    public static List<Uri> byteArrayList;
    public static List<File> filesList;
    ImageView imageView;
    Button getFiles,deleteFiles;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grid_activity);
//        documents = new ArrayList<>();

        byteArrayList = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.sample_iv);

        imagePicker = ImagePicker.getInstance(GridViewActivity.this);

        imagesRecyclerView = (RecyclerView) findViewById(R.id.document_list);

        getFiles = (Button) findViewById(R.id.get_files);
        deleteFiles = (Button) findViewById(R.id.delete_files);

        filesList = new ArrayList<>();

        getFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File("storage/emulated/0/data/user/0/com.myapp.burst/cache/");
                if(file.isDirectory()){
                    File[] files = file.listFiles();
                    Log.e("no of files",""+files.length);
                    for(File f:files){
                        f.delete();
                    }
                }else{

                }
            }
        });

        deleteFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        /*LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(GridViewActivity.this, Utils.calculateNoOfColumns(GridViewActivity.this));

        imagesRecyclerView.setLayoutManager(gridLayoutManager);

        documentAdapter = new DocumentAdapter(GridViewActivity.this, byteArrayList, this);
        imagesRecyclerView.setAdapter(documentAdapter);

        if(byteArrayList.size()>0) {
            imagesRecyclerView.setAdapter(documentAdapter);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            documentAdapter = new DocumentAdapter(GridViewActivity.this, byteArrayList, this);
            imagesRecyclerView.setAdapter(documentAdapter);

        }
    }

    @Override
    public void onCameraClicked() {
        Intent intent = new Intent(GridViewActivity.this,CameraDemo.class);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(File f:filesList){

            ;
        }
    }
}
