package com.techease.elocator.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.techease.elocator.R;
import com.techease.elocator.adapters.CustomSpinnerAdapter;
import com.techease.elocator.utilities.AlertUtils.AlertUtilities;
import com.techease.elocator.utilities.ShareUtils;
import com.techease.elocator.utilities.FireBaseDataInsertion;
import com.techease.elocator.utilities.GeneralUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class AddStoreFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_contact)
    EditText etContact;
    @BindView(R.id.et_Address)
    EditText etAddress;
    @BindView(R.id.iv_store)
    ImageView ivStore;
    @BindView(R.id.iv_add_store)
    ImageView ivAddStore;
    @BindView(R.id.spinner_category)
    Spinner spCategory;
    @BindView(R.id.btn_register)
    Button btnAddStore;

    File sourceFile;
    final int CAMERA_CAPTURE = 10;
    final int RESULT_LOAD_IMAGE = 20;
    private Uri imageURI;
    boolean valid = false;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference mStorageRef,riversRef;

    String strLatitude="34.016473", strLongitude="71.525673",strTitle,strContact,strAddress,strCategory="Others";

    public static double lattitude, longitude;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 100;
    private static final long LOCATION_REFRESH_TIME = 1;
    private static final float LOCATION_REFRESH_DISTANCE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_store, container, false);
        getActivity().setTitle("Register your store");
        ButterKnife.bind(this, view);
        ShareUtils.grantPermission(getActivity());
        checkLocation();
        initViews();
        return view;
    }

    private void initViews() {
        spCategory.setAdapter(new CustomSpinnerAdapter(getActivity(), R.layout.spinner_layout, getActivity().getResources().getStringArray(R.array.categories), "Choose Category"));
        firebaseDatabase = FirebaseDatabase.getInstance();


        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategory = spCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ivAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBuilder();
            }
        });

        btnAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = AlertUtilities.createProgressDialog(getActivity());
                alertDialog.show();
                if(validate()){
                    databaseReference = firebaseDatabase.getReference().child("Stores").child(strCategory);
                    FireBaseDataInsertion.StoreDataInsertion(getActivity(), databaseReference,strTitle,strContact,strAddress,strLatitude,strLongitude);

                    if(FireBaseDataInsertion.successfulBoolean){
                        alertDialog.dismiss();
                        GeneralUtils.connectFragmentDrawerWithoutBaack(getActivity(),new HomeFragment());
                    }
                }
            }
        });
    }

    private void checkLocation(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
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
            imageURI = selectedImageUri;
            String imagepath = getPath(selectedImageUri);
            sourceFile = new File(imagepath);
            try {
                sourceFile = new Compressor(getActivity()).compressToFile(sourceFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FireBaseDataInsertion.uploadImage(getActivity(), sourceFile, imageURI);

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE && data != null) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageURI = data.getData();
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
            FireBaseDataInsertion.uploadImage(getActivity(), sourceFile, imageURI);

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

    //getting current location
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;

            } else {
                Toast.makeText(getActivity(), "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }

            strLatitude = String.valueOf(lattitude);
            strLongitude = String.valueOf(longitude);
        }
    }


    protected void buildAlertMessageNoGps() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {


        }
    };

    private boolean validate(){
        valid = true;
        strTitle = etTitle.getText().toString();
        strContact = etContact.getText().toString();
        strAddress = etAddress.getText().toString();

        if(strTitle.isEmpty() || strTitle == null){
            etTitle.setError("Title is compulsory");
            valid = false;
        }
        else {
            valid = true;
        }


        if(strContact.isEmpty() || strContact == null){
            etContact.setError("please enter you contact number");
            valid = false;
        }
        else {
            valid = true;
        }

        if(strAddress.isEmpty() || strAddress == null){
            etAddress.setError("please enter your address");
            valid = false;
        }
        else {
            valid = true;
        }

        if(strCategory.equals("Choose Category") || strCategory.isEmpty() || strAddress == null){
            Toast.makeText(getActivity(), "please choose your category", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {
            valid = true;
        }

        if(sourceFile == null){
            Toast.makeText(getActivity(), "please upload poster image", Toast.LENGTH_SHORT).show();
            valid = false;
        }


        return valid;
    }
}
