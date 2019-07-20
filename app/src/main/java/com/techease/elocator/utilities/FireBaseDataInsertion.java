package com.techease.elocator.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techease.elocator.fragments.LoginFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FireBaseDataInsertion {
    public static boolean checkProfile;
    public static boolean successfulBoolean = false;
    public static String strFirebaseImageUrl;
    FirebaseAuth auth;

    public static void StoreDataInsertion(Context context, DatabaseReference databaseReference, String strTitle, String strContact, String strAddress, String strLatitude, String strLongitude) {


        HashMap hashMap_record = new HashMap<String, String>();
        hashMap_record.put("title", strTitle);
        hashMap_record.put("address", strAddress);
        hashMap_record.put("latitude", strLatitude);
        hashMap_record.put("longitude", strLongitude);
        hashMap_record.put("contact", strContact);
        hashMap_record.put("image", FireBaseDataInsertion.strFirebaseImageUrl);

        databaseReference.push().setValue(hashMap_record);
    }


    public static boolean uploadImage(final Context context, final File fileImage, Uri uriImagePath) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("storeImages");

        if (fileImage != null) {
            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
            final StorageReference riversRef = mStorageRef.child(out + ".jpg");

            riversRef.putFile(uriImagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    strFirebaseImageUrl = uri.toString();
                                }
                            });
                            successfulBoolean = true;
                            Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            successfulBoolean = false;
                        }
                    });

            return successfulBoolean;

        }

        return false;
    }


    public static void userSignup(DatabaseReference databaseReference,String path, String name, String email, String password) {

        HashMap userSignup = new HashMap<String, String>();
        userSignup.put("image", path);
        userSignup.put("name", name);
        userSignup.put("email", email);
        userSignup.put("password", password);

        databaseReference.setValue(userSignup);
    }

}
