package com.abdurehman.elocator.fragments;

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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.abdurehman.elocator.R;
import com.abdurehman.elocator.utilities.AlertUtils.AlertUtilities;
import com.abdurehman.elocator.utilities.FireBaseDataInsertion;
import com.abdurehman.elocator.utilities.GeneralUtils;
import com.abdurehman.elocator.utilities.ShareUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class SignUpFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.et_signup_name)
    EditText etName;
    @BindView(R.id.et_signup_email)
    EditText etEmail;
    @BindView(R.id.et_signup_password)
    EditText etPassword;
    @BindView(R.id.tv_aleady_login)
    TextView tvLogin;
    @BindView(R.id.btn_sign_up)
    Button btnSignup;

    boolean valid = false;
    String strName, strEmail, strPassword, strProfileImagePath;
    FirebaseAuth auth;

    private Uri imageURI=null;
    File sourceFile;
    final int CAMERA_CAPTURE = 10;
    final int RESULT_LOAD_IMAGE = 20;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userSignupReference;
    private boolean checkProfile = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        ShareUtils.grantPermission(getActivity());

        auth = FirebaseAuth.getInstance();
        initUI();
        return view;
    }

    private void initUI() {
        firebaseDatabase = FirebaseDatabase.getInstance();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBuilder();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String[] splitStr = strEmail.split("@");
                    String child = splitStr[0];
                    alertDialog = AlertUtilities.createProgressDialog(getActivity());
                    alertDialog.show();
                    userSignupReference = firebaseDatabase.getReference().child("Profile").child(child);
                    uploadProfileImage(getActivity(), sourceFile, imageURI);

                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragmentWithBack(getActivity(), new LoginFragment());
            }
        });
    }



    public void cameraBuilder() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getActivity());
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
            imageURI = data.getData();
            String imagepath = getPath(imageURI);
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
            imageURI = Uri.fromFile(sourceFile);

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
            ivProfile.setImageBitmap(thumbnail);

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
        ivProfile.setImageBitmap(BitmapFactory.decodeFile(filePath));
        return cursor.getString(column_index);

    }


    public boolean uploadProfileImage(final Context context, final File fileImage,
                                      Uri uriImagePath) {


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("storeImages");

        if (fileImage != null) {
            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
            final StorageReference riversRef = mStorageRef.child(out + ".png");

            riversRef.putFile(uriImagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    strProfileImagePath = uri.toString();
                                    userSignUp();
                                    FireBaseDataInsertion.userSignup(userSignupReference,
                                            strProfileImagePath,
                                            strName,
                                            strEmail,
                                            strPassword);
                                }
                            });
                            checkProfile = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            checkProfile = false;
                        }
                    });

        }
        return checkProfile;
    }

    private void userSignUp() {
        auth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    alertDialog.dismiss();
                    Toast.makeText(getActivity(), "user successfully added", Toast.LENGTH_SHORT).show();
                    GeneralUtils.connectFragment(getActivity(), new LoginFragment());
                } else {
                    Toast.makeText(getActivity(), "try with another email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validate() {
        valid = true;

        strName = etName.getText().toString();
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString();

        if (strName.isEmpty()) {
            etName.setError("enter your full name");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }


        if (strPassword.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (sourceFile == null) {
            Toast.makeText(getActivity(), "please select profile image", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
}
