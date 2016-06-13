package br.edu.fa7.pomodoro.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import br.edu.fa7.pomodoro.activity.MainActivity;
import br.edu.fa7.pomodoro.R;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    int NOTIFICATION_SERVICE = 1;
    int NOTIFICATION_PENDING_INTENT = 2;

    private final String TITLE_NOTIFICACAO = "Notificação Tarefa";
    private final String ALARM_NOTIFICACAO = "TAREFA_ALARM";


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        StringBuilder descNotificacao = new  StringBuilder();


        descNotificacao.append("Tarefa "+intent.getExtras().getString("_id"));

        descNotificacao.append(" concluída");

        if(action.equals(ALARM_NOTIFICACAO))
        {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_check_circle_black_24dp)
                    .setContentTitle(TITLE_NOTIFICACAO).setContentText(descNotificacao.toString())
                    .setAutoCancel(true).setTicker("Nova Mensagem!").setSound(alarmSound);

            builder.setLights(100, 50, 70);

            Intent it = new Intent(context, MainActivity.class);

            Bundle extras = intent.getExtras();
            it.putExtras(extras);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);

            stackBuilder.addNextIntent(it);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_PENDING_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            builder.setFullScreenIntent(pendingIntent, false);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_SERVICE, builder.build());

        }
    }
}
