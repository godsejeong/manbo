package com.wark.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class MainActivity extends AppCompatActivity {
    private static final String GOAL = "10000";
    Intent manboService;
    BroadcastReceiver receiver;

    private boolean isServiceOn = true;

    int Discrimination=1;//판별
    DecoView artview;
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

        try {
            IntentFilter mainFilter = new IntentFilter("make.a.yong.manbo");
            registerReceiver(receiver, mainFilter);
            startService(manboService);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final String save =pref.getString("step", "");
        Deco(save);
        PlayingReceiver play = new PlayingReceiver();
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2")).setRange(0, 10000, 10000).build();
        int backseries = artview.addSeries(seriesItem1);
        //백그라운드에 그려지는거

        // 이벤트
        artview.addEvent(new DecoEvent.Builder(10000).setIndex(backseries).build());

        artview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (Discrimination){
                    case 1:
                        Discrimination=2;
                        Deco(save);
                        break;
                    case 2:
                        Discrimination=3;
                        Deco(save);
                        break;
                    case 3:
                        Discrimination=1;
                        Deco(save);
                        break;
                }
            }
        });
    }


    public void notice(String a) {
        NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class); //인텐트 생성.


        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 제거

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(MainActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.play)
                .setTicker("목표 : 10000")
                .setWhen(System.currentTimeMillis())
                .setNumber(1)
                .setContentTitle("만보기")
                .setContentText(a + "걸음" + "  "+ (float)((((170 -100) * new Integer(a))/100))/1000 + "Km" + "  "+ new Integer((int) (((((170 -100) * new Integer(a))/100)) * (((((3.7103 + 0.2678*60) + 0.359*70*60*0.0006213))*2)*60) * 0.0006213)) + "calorie")
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

        int cal = (int) ((((3.7103 + 0.2678*60) +(0.0359*70*60*0.0006213))*2)*60);
        if(Discrimination==1) {
            decoText.setText(a + "걸음");
        }
        else if(Discrimination==2) {
            decoText.setText((float)((((170 -100) * new Integer(a))/100))/1000 + "Km");
        }else if(Discrimination==3) {
            decoText.setText(new Integer((int) (((((170 -100) * new Integer(a))/100)) * cal * 0.0006213))+ "calorie");//칼로리
        }final SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 229, 10, 1)).setRange(0, 10000, 0)
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
            Toast.makeText(getApplicationContext(), "manbo", Toast.LENGTH_SHORT).show();
            deta = serviceData;

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("step",serviceData);
            editor.commit();

            Deco(serviceData);
           notice(serviceData);
        }
    }

}

