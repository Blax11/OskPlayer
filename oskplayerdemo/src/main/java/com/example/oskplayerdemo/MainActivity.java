package com.example.oskplayerdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grantPermission();
        setContentView(R.layout.activity_main);
        initUI();
    }
    private void initUI() {
        Button demo = (Button) findViewById(R.id.player);
        demo.setOnClickListener(mClickListener);
    }
    private void demoFunc() {
        Intent intent = new Intent(MainActivity.this, BasicPlayActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.player) {
                demoFunc();
            }
        }
    };

    protected void grantPermission(){
        final int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck== PackageManager.PERMISSION_GRANTED) {

        } else if (permissionCheck <= PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    1);
        }
    }
}
