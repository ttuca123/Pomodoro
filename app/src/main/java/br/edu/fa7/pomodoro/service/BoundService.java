package br.edu.fa7.pomodoro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundService extends Service implements ServiceNotifier{

    private ListenValue obj;
    private IBinder binder;
    private boolean stop;
    private boolean isCountStarted;

    public BoundService() {
        this.stop = false;
        this.isCountStarted = false;
        this.binder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startCounter(){
        if(!isCountStarted){
            isCountStarted = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long count = 0;
                    while(!stop){
                        try {
                            count += 1;
                            notifyValue(count);
                            Log.i("App", "Valor: " + count);
                            Thread.sleep(1000);
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

    public void stopCounter(){
        this.stop = true;
    }

    public void closeService(){
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

    public class LocalBinder extends Binder{
        public BoundService getService(){
            return BoundService.this;
        }
    }

}
