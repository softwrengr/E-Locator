package com.techease.elocator.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.techease.elocator.R;
import com.techease.elocator.utilities.BaseNetworking;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class AddStoreFragment extends Fragment {
   View view;
   @BindView(R.id.iv_store)
    ImageView ivStore;
   @BindView(R.id.iv_add_store)
   ImageView ivAddStore;

    File sourceFile;
    final int CAMERA_CAPTURE = 10;
    final int RESULT_LOAD_IMAGE = 20;
    boolean valid = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_store, container, false);
        getActivity().setTitle(getResources().getString(R.string.app_name));
        ButterKnife.bind(this,view);
        BaseNetworking.grantPermission(getActivity());
        initViews();
        return view;
    }

    private void initViews(){
        ivAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              cameraBuilder();
            }
        });
    }

    public void cameraBuilder() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Open");
        String[] pictureDialogItems = {
                "\tGallery",
                "\tCamera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                galleryIntent();

                                break;
                            case 1:
                                cameraIntent();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void cameraIntent() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, CAMERA_CAPTURE);
    }

    public void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImageUri = data.getData();
            String imagepath = getPath(selectedImageUri);
            sourceFile = new File(imagepath);
            try {
                sourceFile = new Compressor(getActivity()).compressToFile(sourceFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE && data != null) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
            sourceFile = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".png");
            FileOutputStream fo;
            try {
                sourceFile.createNewFile();
                fo = new FileOutputStream(sourceFile);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivStore.setImageBitmap(thumbnail);

        }
    }

    @SuppressLint("SetTextI18n")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        Log.d("path", filePath);
        ivStore.setImageBitmap(BitmapFactory.decodeFile(filePath));
        return cursor.getString(column_index);

    }
}
