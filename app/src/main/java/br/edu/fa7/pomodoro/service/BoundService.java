package br.edu.fa7.pomodoro.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BoundService extends Service implements ServiceNotifier {

    private ListenValue obj;
    private IBinder binder;
    private boolean stop;
    private boolean isCountStarted;

    public static final int MSG_REGISTER_CLIENT = 0;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_SET_STRING_VALUE = 4;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    int counter = 25;
    private Timer timer;
    private final String MSG_TAREFA_INICIADA = "Tarefa iniciada, id=";


    public BoundService() {
        this.stop = false;
        this.isCountStarted = false;
        this.binder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void mostrarNotificacao(Intent it) {


        pendingIntent = PendingIntent.getBroadcast(BoundService.this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, counter);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }




    public void startCounter(final Intent intent) {


        if (intent.getExtras() != null) {

            if (!isCountStarted) {
                isCountStarted = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Bundle extras = intent.getExtras();

                        Intent it = new Intent("TAREFA_ALARM");
                        it.putExtras(extras);

                        mostrarNotificacao(it);

//                        Toast toast = Toast.makeText(this,
//                                MSG_TAREFA_INICIADA + it.getStringExtra("_id"),
//                                Toast.LENGTH_SHORT);
//                        toast.show();


//                        long count = 25;
                        while (!stop) {
                            try {
                                counter -= 1;
                                notifyValue(counter);
                                Log.i("App", "Valor: " + counter);
                                Thread.sleep(1000);

                                if(counter==0){
                                    stop=Boolean.TRUE;
                                    counter=25;


                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        stop = false;
                        isCountStarted = false;
                    }
                }).start();




            }

        }
    }


    public void stopCounter() {
        this.stop = true;
    }

    public void closeService() {
        stopSelf();
    }

    @Override
    public void add(ListenValue obj) {
        this.obj = obj;
    }

    @Override
    public void notifyValue(long value) {
        obj.newValue(value);
    }

    public class LocalBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

}
