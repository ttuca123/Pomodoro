package br.edu.fa7.pomodoro.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.entity.Tarefa;

import br.edu.fa7.pomodoro.service.TarefaService;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */

public class MainActivity extends Activity implements View.OnClickListener {


    private static final short NEW_ACTIVITY_ID = 1;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnTask;

    public TextView cronometro;
    private Messenger mService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean mIsBound;
    Intent intent;







    public class IncomingHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case TarefaService.MSG_SET_STRING_VALUE:

                    String str1 = msg.getData().getString("param_cron");
                    cronometro.setText(str1);

                    break;
                case TarefaService.MSG_SET_INT_VALUE:
                    cronometro.setText("Int Message: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getInstancy();

        restoreMe(savedInstanceState);

        btnTask.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(this);


        mAdapter = new MyAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        intent = new Intent(this, TarefaService.class);


    }


    private void restoreMe(Bundle state) {
        if (state != null) {

            cronometro.setText("25:00");
        }
    }


    private void getInstancy() {
        recyclerView = (RecyclerView) findViewById(R.id.mainReclyclerView);

        btnTask = (Button) findViewById(R.id.btnTask);

        cronometro = (TextView) findViewById(R.id.cronometro);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) {

            outState.putString("textStatus", cronometro.getText().toString());

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_ACTIVITY_ID:
                if (resultCode == RESULT_OK) {

                    mAdapter = new MyAdapter(this);

                    mLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);


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


    private ServiceConnection mConnection = new ServiceConnection()

    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {


            try {
                mIsBound = true;
                mService = new Messenger(service);

                cronometro.setText("25:01");


                Message msg = Message.obtain(null, TarefaService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                Log.i("MyService", "Cliente Conectado.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            cronometro.setText("Disconnected.");
            try {
                Message msg = Message.obtain(null, TarefaService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mService;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mIsBound = false;
        }
    };


    public void doBindService() {

        if(mIsBound) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = false;
        }
//            cronometro.setText("Binding.");
    }


    void doUnbindService() {
        if (mIsBound) {

            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, TarefaService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mService;
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            unbindService(mConnection);
            mIsBound = false;
            cronometro.setText("25:01");
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private final String MSG_UPDATE_SUCESSO = "Pomodoro atualizado com sucesso!!!";
        private final String MSG_DELETE_SUCESSO = "Pomodoro concluído com sucesso!!!";
        private final String MSG_ERRO_SQL = "Erro ao manipular a base de dados";
        private List<Tarefa> tarefas;
        private LayoutInflater mLayoutInflater;
        private DataBaseHelper helper;

        private boolean mCounterStarted;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
            MyViewHolder mViewHolder = new MyViewHolder(view);

            mCounterStarted = false;

            return mViewHolder;
        }

        public MyAdapter(Context context) {

            helper = new DataBaseHelper(context);
            this.tarefas = listarTarefas();
            this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        private List<Tarefa> listarTarefas() {

            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM TAREFA", null);
            cursor.moveToFirst();

            tarefas = new ArrayList<>();

            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(0);

                String titulo = cursor.getString(1);

                String descricao = cursor.getString(2);

                Integer nrPomodoro = cursor.getInt(3);

                tarefas.add(new Tarefa(id, titulo, descricao, nrPomodoro, R.drawable.evolution_tasks));

                cursor.moveToNext();
            }
            cursor.close();

            return tarefas;

        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Tarefa tarefa = tarefas.get(position);
            holder.imageView.setImageResource(tarefa.getImagem());
            holder.titulo.setText(tarefa.getTitulo());
            holder.descricao.setText(tarefa.getDescricao());
            holder.pomodoro.setText(tarefa.getPomodoro().toString());
            holder.mId.setText(tarefa.getId() + "");


        }


        @Override
        public int getItemCount() {
            return tarefas.size();
        }




        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView imageView;
            public TextView titulo;
            public TextView descricao;
            public TextView pomodoro;
            public TextView mId;
            public Button btnStart;
            public Button btnStop;
            private AlertDialog alertDialog;

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public MyViewHolder(View itemView) {
                super(itemView);

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                titulo = (TextView) itemView.findViewById(R.id.tarefa);
                descricao = (TextView) itemView.findViewById(R.id.descricao);
                pomodoro = (TextView) itemView.findViewById(R.id.lblPomodoro);
                btnStart = (Button) itemView.findViewById(R.id.start);
                btnStop = (Button) itemView.findViewById(R.id.stop);
                btnStart.setOnClickListener(this);
                btnStop.setOnClickListener(this);
                mId = (TextView) itemView.findViewById(R.id._id);


                this.alertDialog = criaAlertDialog(itemView.getContext());
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        Intent intent = new Intent(view.getContext(), TarefaActivity.class);


                        alertDialog.show();
                        return true;
                    }
                });


            }


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {

                String id = mId.getText().toString();

                switch (view.getId()) {
                    case R.id.start:
                        Bundle bundle = new Bundle();
                        bundle.putString("_id", id);
                        intent.putExtras(bundle);

                        startService(intent);
                        doBindService();
                        itemView.setBackgroundColor(Color.RED);
                        btnStart.setEnabled(Boolean.FALSE);
                        btnStop.setEnabled(Boolean.TRUE);
                        break;
                    case R.id.stop:
                        doUnbindService();
                        stopService(intent);
                        itemView.setBackgroundColor(Color.GREEN);
                        btnStop.setEnabled(Boolean.FALSE);
                        btnStart.setEnabled(Boolean.TRUE);

                        String nrPomodoro = pomodoro.getText().toString();


                        if (nrPomodoro.equals("1")) {

                            remove(id);

                        } else {

                            atualizar(id, nrPomodoro);

                        }

                        break;

                }
            }

            private void refreshView() {

                mAdapter = new MyAdapter(MainActivity.this);
                recyclerView.setAdapter(mAdapter);

                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);

            }

            private void atualizar(String id, String nrPomodoro) {

                int pomodoro = Integer.parseInt(nrPomodoro) - 1;
                Toast toast;


                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("qtd_pomodoro", pomodoro);

                long resultado = db.update("tarefa", values, "_id = ?",
                        new String[]{id});

                if (resultado != -1) {
                    refreshView();
                    toast = Toast.makeText(getApplicationContext(),
                            MSG_UPDATE_SUCESSO,
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getApplicationContext(),
                            MSG_ERRO_SQL,
                            Toast.LENGTH_SHORT);
                    toast.show();

                }
            }


            private void remove(String id) {
                long resultado;
                SQLiteDatabase db = helper.getWritableDatabase();
                String where[] = new String[]{id};
                resultado = db.delete("tarefa", "_id=?", where);
                Toast toast;

                if (resultado != -1) {
                    refreshView();
                    toast = Toast.makeText(getApplicationContext(),
                            MSG_DELETE_SUCESSO,
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getApplicationContext(),
                            MSG_ERRO_SQL,
                            Toast.LENGTH_SHORT);
                    toast.show();

                }

            }

            private AlertDialog criaAlertDialog(Context contexto) {
                String editar = String.valueOf(R.string.editar);
                String remover = String.valueOf(R.string.remover);

                final CharSequence[] items = {
                        editar,
                        remover};


                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(contexto).setTitle("OPÇÕES");
                builder.setCancelable(true).setPositiveButton("EDITAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
                return builder.create();
            }
        }


    }


}
