package br.edu.fa7.pomodoro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tuca on 26/05/16.
 */
public class TarefaBroadCastReceive extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent tarefaSerice = new Intent(context, TarefaService.class);
        context.startService(tarefaSerice);
    }
}
