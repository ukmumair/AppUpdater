package com.umair.appupdater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    FirebaseDatabase database;
    DatabaseReference version;
    FirebaseRemoteConfig firebaseRemoteConfig;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv =findViewById(R.id.tv);
//        tv.setText("APP VERSION " + getVersionInfo());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("new_version_code",getVersionInfo());
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        database = FirebaseDatabase.getInstance();
        version = database.getReference("App Version");
        version.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ver = Objects.requireNonNull(snapshot.child("latest_version").getValue()).toString();
                    final String download_url = Objects.requireNonNull(snapshot.child("download_url").getValue()).toString();
                    if (!getVersionInfo().equals(ver))
                    {
                        ShowDialog(download_url,ver);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ShowDialog(final String download_url, String version)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setMessage("New Update Available Version " + version + " You Need To Update The App In Order To Use.")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_url));
                        startActivity(browserIntent);
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public String getVersionInfo()
    {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        assert packageInfo != null;
        return packageInfo.versionName;
    }

    @Override
    protected void onPause() {
        super.onPause();
        version.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ver = Objects.requireNonNull(snapshot.child("latest_version").getValue()).toString();
                final String download_url = Objects.requireNonNull(snapshot.child("download_url").getValue()).toString();
                if (!getVersionInfo().equals(ver))
                {
                    ShowDialog(download_url,ver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void buttonLightMode(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        startActivity(new Intent(MainActivity.this,MainActivity.class));
        finish();
        tv.setText("LIGHT MODE");
    }

    public void buttonDarkMode(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        startActivity(new Intent(MainActivity.this,MainActivity.class));
        finish();
        tv.setText("DARK MODE");
    }
}