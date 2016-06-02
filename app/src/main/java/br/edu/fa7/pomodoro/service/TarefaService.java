package br.edu.fa7.pomodoro.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */
public class TarefaService extends Service {

    private int counter = 5;
    private int incrementBy = 1;

    private Timer timer;

    private static boolean isRunning = false;

    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    public static final int MSG_REGISTER_CLIENT = 0;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_SET_STRING_VALUE = 4;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public Toast toast;

    private final String MSG_TAREFA_INCIADA = "Tarefa iniciada com sucesso!!!";


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private final IBinder mBinder = new LocalBinder();
    final Messenger mMessenger = new Messenger(new IncomingHandler());


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_INT_VALUE:
                    incrementBy = msg.arg1;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                onTimerTick();
            }
        }, 1, 1000L);

        isRunning = true;

        toast = Toast.makeText(this,
                MSG_TAREFA_INCIADA,
                Toast.LENGTH_SHORT);
        toast.show();



        Bundle extras = intent.getExtras();

        Intent it = new Intent("TAREFA_ALARM");
        it.putExtras(extras);

        mostrarNotificacao(it);


        Log.i("MyService", "Received start mId " + startId + ": " + intent);


        return Service.START_STICKY;
    }


    public static boolean isRunning() {
        return isRunning;
    }


    public class LocalBinder extends Binder {
        public TarefaService getService() {
            return TarefaService.this;
        }
    }


    private void sendMessageToUI(int intvaluetosend) {

        if (intvaluetosend <= 0) {
            timer.cancel();
            counter = 5;
        } else {

            for (int i = mClients.size() - 1; i >= 0; i--) {

                if (mClients.size() > 0) {
                    try {

                        Bundle b = new Bundle();
                        b.putString("param_cron", intvaluetosend + "");
                        Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                        msg.setData(b);
                        mClients.get(i).send(msg);
                        Log.i("TimerTick", "Timer doing work." + counter);

                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        mClients.remove(i);
                    }
                } else {
                    Log.i("TimerTick", " Nenhum cliente");
                }
            }
        }
    }


    private void mostrarNotificacao(Intent it) {


        pendingIntent = PendingIntent.getBroadcast(TarefaService.this, 1, it, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, counter);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }


    private void onTimerTick() {

        try {
            counter -= incrementBy;


            sendMessageToUI(counter);

        } catch (Throwable t) {
            Log.e("TimerTick", "Timer Tick Failed.", t);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        alarmManager.cancel(pendingIntent);
        counter = 5;

        Log.i("MyService", "Service Stopped.");
        isRunning = false;

        toast = Toast.makeText(this,
                "Tarefa finalizada com sucesso!",
                Toast.LENGTH_SHORT);
        toast.show();

    }


}
