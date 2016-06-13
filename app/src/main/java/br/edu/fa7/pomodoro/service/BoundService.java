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

/**
 * Criado por Artur Cavalcante 07/06/2016
 */

public class BoundService extends Service implements ServiceNotifier {

    private int counter = 1500;
    private ListenValue obj;
    private IBinder binder;
    private boolean stop;
    private boolean isCountStarted;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private final String MSG_TAREFA_INICIADA = "Tarefa iniciada, id=";
    private Thread thread;

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
                Toast toast = Toast.makeText(this,
                        MSG_TAREFA_INICIADA + intent.getStringExtra("_id"),
                        Toast.LENGTH_SHORT);
                toast.show();


                thread = new Thread() {
                    @Override
                    public void run() {

                        Bundle extras = intent.getExtras();

                        Intent it = new Intent("TAREFA_ALARM");
                        it.putExtras(extras);

                        mostrarNotificacao(it);

                        do {
                            try {
                                counter -= 1;


                                int minutos = counter/60;
                                int segundos = counter%60;

                                notifyValue(minutos+" : "+segundos);
                                Log.i("App", "Valor: " + counter);
                                Thread.sleep(1000);

                                if (counter == 0) {
                                    stopCounter();

                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (!stop);
                        stop = false;
                        isCountStarted = false;
                        counter = 1500;
                    }

                };

                thread.start();
            }

        }
    }


    public void stopCounter() {
        thread.interrupt();
        this.stop = true;

    }

    public void closeService() {

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);


            Log.i("MyService", "Service Stopped.");


            Toast toast = Toast.makeText(this,
                    "Tarefa finalizada com sucesso!",
                    Toast.LENGTH_SHORT);
            toast.show();

            stopCounter();
            alarmManager=null;

            stopSelf();
        }
    }

    @Override
    public void add(ListenValue obj) {
        this.obj = obj;
    }




    public void notifyValue(String value) {
        obj.newValue(value);
    }

    public class LocalBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

}
