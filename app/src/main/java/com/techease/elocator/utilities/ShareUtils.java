package com.techease.elocator.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.core.Context;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techease.elocator.R;

import java.util.List;

public class ShareUtils {

    public static void grantPermission(Activity context){
        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    public static Intent shareApp(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at:\n https://play.google.com/store/apps/details?id=com.squaresdevelopers.lateststockwallpapers&hl=en");
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public static Intent loadApp(Activity activity){
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        return  goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    public static void showImage(Activity context,String url,String name){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);
        ImageView imageView = dialog.findViewById(R.id.iv_show);
        TextView tvName = dialog.findViewById(R.id.tv_name);
        Glide.with(context).load(url).into(imageView);
        tvName.setText(name);
        dialog.show();
    }

}
