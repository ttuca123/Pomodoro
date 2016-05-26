package br.edu.fa7.pomodoro.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tuca on 26/05/16.
 */
public class TarefaService extends Service {

    private Intent it;
    private Toast toast;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        toast = Toast.makeText( this,
                "Tarefa iniciada com sucesso!",
                Toast.LENGTH_SHORT);
        toast.show();

        new Thread(new Runnable()
        {
            @Override
            public void run() {

                try {
                    for(int i=1500; i>0; i--)
                    {
                        Thread.sleep(1000);
                        Log.i("App", "Value"+i);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

        return  Service.START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        toast = Toast.makeText( this,
                "Tarefa finalizada com sucesso!",
                Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
