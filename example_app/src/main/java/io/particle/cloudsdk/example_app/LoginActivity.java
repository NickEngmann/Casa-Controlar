package io.particle.cloudsdk.example_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        final MediaPlayer mp =MediaPlayer.create(LoginActivity.this,R.raw.bass);
        findViewById(R.id.login_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String email = ((EditText) findViewById(R.id.email)).getText().toString();
                        final String password = ((EditText) findViewById(R.id.password)).getText().toString();

                        // Don't:
                        AsyncTask task = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] params) {
                                try {
                                    ParticleCloud.get(LoginActivity.this).logIn(email, password);

                                } catch (final ParticleCloudException e) {
                                    Runnable mainThread = new Runnable() {
                                        @Override
                                        public void run() {
                                            Toaster.l(LoginActivity.this, e.getBestMessage());
                                            e.printStackTrace();
                                            Log.d("info", e.getBestMessage());
//                                            Log.d("info", e.getCause().toString());
                                        }
                                    };
                                    runOnUiThread(mainThread);

                                }

                                return null;
                            }

                        };
//                        task.execute();

                        //-------

                        // DO!:
                        Async.executeAsync(ParticleCloud.get(v.getContext()), new Async.ApiWork<ParticleCloud, Object>() {

                            private ParticleDevice mDevice;

                            @Override
                            public Object callApi(final ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                                sparkCloud.logIn(email, password);
                                sparkCloud.getDevices();
                                sparkCloud.subscribeToMyDevicesEvents(null, new ParticleEventHandler() {
                                    @Override
                                    public void onEvent(String eventName, ParticleEvent particleEvent) {
                                        Log.i("BANANA", "onEvent: " + eventName + particleEvent);
                                        Toaster.s(LoginActivity.this, "Someone is here!");
                                        mp.start();
                                    }

                                    @Override
                                    public void onEventError(Exception e) {
                                        Log.e("BANANA", "OH NOES, onEventError: ", e);
                                    }
                                });


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Async.executeAsync(sparkCloud, new Async.ApiProcedure<ParticleCloud>() {
                                            @Override
                                            public Void callApi(ParticleCloud particleCloud)
                                                    throws ParticleCloudException, IOException {

                                                try {
                                                    Thread.sleep(5000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                Log.i("BANANA", "callApi: PUBLISH");
                                                particleCloud.publishEvent("BANANA_EVENT", "banana_time",
                                                        ParticleEventVisibility.PRIVATE, 300);

                                                return null;
                                            }

                                            @Override
                                            public void onFailure(ParticleCloudException exception) {

                                            }
                                        });
                                    }
                                });
                                mDevice = sparkCloud.getDevice("2c0047000447343233323032");
                                Object obj;

                                try {
                                    obj = mDevice.getVariable("analogvalue");
                                    Log.d("BANANA", "analogvalue: " + obj);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Toaster.s(LoginActivity.this, "Error reading variable");
                                    obj = -1;
                                }

                                try {
                                    String strVariable = mDevice.getStringVariable("stringvalue");
                                    Log.d("BANANA", "stringvalue: " + strVariable);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Toaster.s(LoginActivity.this, "Error reading variable");
                                }

                                try {
                                    double dVariable = mDevice.getDoubleVariable("doublevalue");
                                    Log.d("BANANA", "doublevalue: " + dVariable);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Toaster.s(LoginActivity.this, "Error reading variable");
                                }

                                try {
                                    int intVariable = mDevice.getIntVariable("analogvalue");
                                    Log.d("BANANA", "int analogvalue: " + intVariable);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Toaster.s(LoginActivity.this, "Error reading variable");
                                }

                                return -1;

                            }

                            @Override
                            public void onSuccess(Object value) {
                                Toaster.l(LoginActivity.this, "Logged in");
                                Intent intent = ValueActivity.buildIntent(LoginActivity.this, 123, mDevice.getID());
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(ParticleCloudException e) {
                                Toaster.l(LoginActivity.this, e.getBestMessage());
                                e.printStackTrace();
                                Log.d("info", e.getBestMessage());
                            }
                        });


                    }
                }

        );
    }

}
