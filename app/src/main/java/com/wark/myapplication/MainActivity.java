package com.wark.myapplication;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class MainActivity extends AppCompatActivity {
    private static final String GOAL = "10000";
    Intent manboService;
    BroadcastReceiver receiver;
    NotificationManager nMN;
    AlertDialog.Builder builder;

    private boolean isServiceOn = true;

    int t;
    DecoView artview;
    ImageView imageView;
    public static final int[] CHART_RED = {Color.rgb(229, 10, 1)};
    public static final int[] CHART_GREAE = {Color.rgb(223, 223, 223)};

    TextView decoText_1;
    boolean flag = true;
    TextView decoText;
    String deta;
    private Notification mNoti;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manboService = new Intent(this, StepCheckService.class);

        receiver = new PlayingReceiver();

        decoText_1 = (TextView) findViewById(R.id.decotext_1);
        decoText = (TextView) findViewById(R.id.decotext);
        artview = (DecoView) findViewById(R.id.dynamicArcView);
        getPreferences();
        PlayingReceiver play = new PlayingReceiver();
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2")).setRange(0, 10000, 10000).build();
        int backseries = artview.addSeries(seriesItem1);
        //백그라운드에 그려지는거

        // 이벤트
        artview.addEvent(new DecoEvent.Builder(10000).setIndex(backseries).build());


    }

    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        pref.getString("service", "");
        pref.getString("step", "");
    }

    public void notice(String a) {
        NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class); //인텐트 생성.


        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(MainActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.play)
                .setTicker("목표 : 10000")
                .setWhen(System.currentTimeMillis())
                .setNumber(1)
                .setContentTitle("만보기")
                .setContentText("현제 걸음 수 : " + a)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        notificationManager.notify(1, builder.build()); // Notification send


//        PendingIntent mPendingIntent = PendingIntent.getActivity(
//                getApplicationContext(), 0, new Intent(getApplicationContext(),
//                        MainActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        nMN = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNoti = new NotificationCompat.Builder(getApplicationContext())
//                .setContentTitle("만보기")
//                .setContentText("현제 걸음 수 : " + Float.parseFloat(a))
//                .setTicker("현제 걸음 수 : " + Float.parseFloat(a))
//                .setAutoCancel(true)
//                .setContentIntent(mPendingIntent)
//                .build();
//    }
    }


    public void Deco(String a) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 229, 10, 1)).setRange(0, 10000, 0)
//                  .setSeriesLabel(new SeriesLabel.Builder("%.0f%%")//값 표시
                .build();
        //값변경
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float v, float v1) {
                float percentFilled = ((v1 - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        int series_one = artview.addSeries(seriesItem);
        artview.addEvent(new DecoEvent.Builder(Float.parseFloat(a)).setIndex(series_one).build());

    }


    class PlayingReceiver extends BroadcastReceiver {
        String serviceData;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PlayignReceiver", "IN");
                serviceData = intent.getStringExtra("stepService");
                decoText.setText(serviceData+"걸음");
            Toast.makeText(getApplicationContext(), "manbo", Toast.LENGTH_SHORT).show();
            deta = serviceData;
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("service",serviceData);
            editor.commit();

           Deco(serviceData);
           notice(serviceData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Drawable d = null;
        switch (id) {
            case R.id.play:
                Log.e("asfd", String.valueOf(flag));
                if (flag) {
                    try {
                        Log.e("aasdf", "ture");
                        IntentFilter mainFilter = new IntentFilter("make.a.yong.manbo");
                        item.setTitle("Stop");
                        d = getResources().getDrawable(R.drawable.stop);
                        registerReceiver(receiver, mainFilter);
                        startService(manboService);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        unregisterReceiver(receiver);
                        Log.e("aasdf", "flase");
                        item.setTitle("Start");
                        d = getResources().getDrawable(R.drawable.play);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                flag = !flag;
                d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                item.setIcon(d);
                return true;
        }
                return flag;
        }

}

