package de.uhh.detectives.frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.uhh.detectives.frontend.databinding.SplashScreenBinding;
import de.uhh.detectives.frontend.permissionhelper.LocationPermissionService;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;

public class SplashActivity extends AppCompatActivity {
    private LocationPermissionService permissionHandler;
    private SplashScreenBinding binding;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        binding = SplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        activity = this;

        permissionHandler = new LocationPermissionService(this);
        permissionHandler.askCoarseLocation();
        permissionHandler.askFineLocationPermissions();

        addOnTouchListener();
    }

    private void addOnTouchListener(){
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionHandler.isFineLocationPermissionGranted()) {
                    PushMessageService messageHandler = new PushMessageService(activity);
                    //messageHandler.pushMapExitMessage();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    permissionHandler.askCoarseLocation();
                    permissionHandler.askFineLocationPermissions();
                }
            }
        });
    }
}
