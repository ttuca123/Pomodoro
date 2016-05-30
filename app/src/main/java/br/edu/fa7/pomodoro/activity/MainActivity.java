package br.edu.fa7.pomodoro.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.entity.Tarefa;

import br.edu.fa7.pomodoro.service.TarefaService;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */

public class MainActivity extends Activity implements View.OnClickListener
{



    private static final short NEW_ACTIVITY_ID=1;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnTask;

    public TextView cronometro;
    private Messenger mensegerService;

    private boolean mIsBound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getInstancy();

        restoreMe(savedInstanceState);

        btnTask.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter =  new MyAdapter(this);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);


    }




    private void restoreMe(Bundle state) {
        if (state!=null) {

            cronometro.setText("25:00");
        }
    }


    private void getInstancy(){
        recyclerView = (RecyclerView) findViewById(R.id.mainReclyclerView);

        btnTask = (Button) findViewById(R.id.btnTask);

        cronometro = (TextView) findViewById(R.id.cronometro);



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState==null){

            outState.putString("textStatus", cronometro.getText().toString());

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_ACTIVITY_ID:
                if(resultCode == RESULT_OK)
                {

                    mAdapter =  new MyAdapter(this);

                    mLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);

//                    String titulo = data.getStringExtra("titulo");
//                    String descricao = data.getStringExtra("descricao");
//                    String nrPomodoro = data.getStringExtra("nrPomodoro");
//                    tarefas.add(new Tarefa(titulo, descricao, Integer.parseInt(nrPomodoro), R.drawable.evolution_tasks));
                }
                default:

                break;
        }

    }

    @Override
    public void onClick(View v) {

        Intent it = new Intent(this, TarefaActivity.class);
        startActivityForResult(it, NEW_ACTIVITY_ID);

    }


    ServiceConnection mConnection = new ServiceConnection()

    {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mIsBound = true;
            mensegerService = new Messenger(service);

            cronometro.setText("25:01");
            try {
                Message msg = Message.obtain(null, TarefaService.MSG_REGISTER_CLIENT);
                msg.replyTo = mensegerService;
                mensegerService.send(msg);
            } catch (RemoteException e) {
               e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            cronometro.setText("Disconnected.");
            try {
                Message msg = Message.obtain(null, TarefaService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mensegerService;
                mensegerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mIsBound = false;
        }
    };


    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case TarefaService.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("param_cron");
                    cronometro.setText(str1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void doBindService() {

        if(!TarefaService.isRunning()) {
            bindService(new Intent(this, TarefaService.class), mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }
    }


    void doUnbindService() {
        if (mIsBound) {

            if (mensegerService != null) {
                try {
                    Message msg = Message.obtain(null, TarefaService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mensegerService;
                    mensegerService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            unbindService(mConnection);
            mIsBound = false;
            cronometro.setText("25:01");
        }



    }








}
