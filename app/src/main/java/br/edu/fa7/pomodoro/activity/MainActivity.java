package br.edu.fa7.pomodoro.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;

import br.edu.fa7.pomodoro.EnuStatus;
import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.dao.TarefaDAO;
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

    private Handler handler = new TesteHandler();
    private boolean mIsBound;
    Intent intent;
    private Timer timer;

    private TarefaDAO tarefaDAO;
    private List<Tarefa> tarefas;

    private static final String DESCONECTADO = "Disconnected.";

    public class TesteHandler extends Handler {


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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readMessage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getInstancy();

        restoreMe(savedInstanceState);

        btnTask.setOnClickListener(this);

        intent = new Intent(this, TarefaService.class);

        tarefaDAO = new TarefaDAO(getBaseContext());

        readMessage();

        mLayoutManager = new LinearLayoutManager(this);


        mAdapter = new MyAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


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


    private void onTimerTick() {

        try {
            int counter = 5;
            int incrementBy = 1;

            counter -= incrementBy;


            sendMessageToUI(counter);

        } catch (Throwable t) {
            Log.e("TimerTick", "Timer Tick Failed.", t);
        }
    }


    private void sendMessageToUI(int intvaluetosend) {

        if (intvaluetosend <= 0) {
            timer.cancel();

        } else {

            cronometro.setText(intvaluetosend + "");


        }


    }


    private ServiceConnection mConnection = new ServiceConnection()

    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {


            try {
                mIsBound = true;
                mService = new Messenger(service);

                Message msg = new Message();
                msg.what = TarefaService.MSG_SET_STRING_VALUE;


//                Message msg = Message.obtain(null, TarefaService.MSG_REGISTER_CLIENT);
//                msg.replyTo = mMessenger;
//                mService.send(msg);
                Log.i("MyService", "Cliente Conectado.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            cronometro.setText(DESCONECTADO);
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

        if (mIsBound) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = false;
        }

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


    public void enviarMessage() {

//                for(int i=0;i<10;i++){
//                    Message msg = new Message();
//                    msg.what = TarefaService.MSG_SET_STRING_VALUE;
//                    Bundle b = new Bundle();
//                    b.putString("param_cron", i+Math.random()+"");
//                    msg.setData(b);
//
//                    handler.sendMessageAtTime(msg, 1000);
//
//                }


//        timer = new Timer();
//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                onTimerTick();
//            }
//        }, 1, 1000L);

    }


    public void readMessage() {

        String id = getIntent().getStringExtra("_id");

        if (id != null) {

            StringBuilder msgs = new StringBuilder();
            msgs.append("Tarefa: ");
            msgs.append(id);
            msgs.append(", Título: ");
            msgs.append(getIntent().getStringExtra("nome"));
            msgs.append(" finalizada com sucesso.");


            for (Tarefa tarefa : tarefaDAO.getTarefas()) {
                if (tarefa.getId() == Integer.parseInt(id)) {
                    int status = EnuStatus.CONCLUIDO.getId();
                    if (tarefa.getPomodoro() > 1) {
                        tarefaDAO.update(id, tarefa.getPomodoro()-1, status);

                        Toast.makeText(getApplicationContext(), msgs.toString(), Toast.LENGTH_LONG).show();

                    } else {

                        tarefaDAO.delete(id);
                        tarefa = null;
                        Toast.makeText(getApplicationContext(), msgs.toString(), Toast.LENGTH_LONG).show();
                    }

                    break;
                }

            }


        }

    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        private LayoutInflater mLayoutInflater;
        private TarefaDAO tarefaDAO;
        private Context contexto;
        private boolean mCounterStarted;

        private static final String MSG_UPDATE_SUCESSO = "Pomodoro atualizado com sucesso!!!";
        private static final String MSG_DELETE_SUCESSO = "Pomodoro concluído com sucesso!!!";
        private static final String MSG_ERRO_SQL = "Erro ao manipular a base de dados";


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
            MyViewHolder mViewHolder = new MyViewHolder(view);


            mCounterStarted = false;

            return mViewHolder;
        }

        public MyAdapter(Context context) {


            tarefaDAO = new TarefaDAO(context);
            this.contexto = context;

            tarefas = tarefaDAO.getTarefas();

            this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public void update(String id, int status) {


            Toast toast;

            long resultado = tarefaDAO.update(id, status);


            if (resultado != -1) {
                refreshView();
                toast = Toast.makeText(contexto,
                        MSG_UPDATE_SUCESSO,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(contexto,
                        MSG_ERRO_SQL,
                        Toast.LENGTH_SHORT);
                toast.show();

            }
        }


        private void update(String id, int nrPomodoro, int status) {


            Toast toast;

            long resultado = tarefaDAO.update(id, (nrPomodoro - 1), status);


            if (resultado != -1) {
                refreshView();
                toast = Toast.makeText(contexto,
                        MSG_UPDATE_SUCESSO,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(contexto,
                        MSG_ERRO_SQL,
                        Toast.LENGTH_SHORT);
                toast.show();

            }
        }


        private void remover(String id) {

            Toast toast;

            if (tarefaDAO.delete(id) != -1) {
                refreshView();
                toast = Toast.makeText(contexto,
                        MSG_DELETE_SUCESSO,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(contexto,
                        MSG_ERRO_SQL,
                        Toast.LENGTH_SHORT);
                toast.show();

            }

        }


        private void refreshView() {

            mAdapter = new MyAdapter(MainActivity.this);
            recyclerView.setAdapter(mAdapter);

            mLayoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);

        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Tarefa tarefa = tarefas.get(position);
            holder.imageView.setImageResource(tarefa.getImagem());
            holder.titulo.setText(tarefa.getTitulo());
            holder.descricao.setText(tarefa.getDescricao());
            holder.pomodoro.setText(tarefa.getPomodoro().toString());
            holder.mId.setText(tarefa.getId() + "");

            switch (tarefa.getStatus()) {

                case 0:
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    holder.btnStart.setEnabled(Boolean.TRUE);
                    holder.btnStop.setEnabled(Boolean.TRUE);
                    break;
                case 1:
                    holder.itemView.setBackgroundColor(Color.RED);
                    holder.btnStart.setEnabled(Boolean.FALSE);
                    holder.btnStop.setEnabled(Boolean.TRUE);
                    break;
                case 2:

                    holder.itemView.setBackgroundColor(Color.GREEN);
                    holder.btnStop.setEnabled(Boolean.FALSE);
                    holder.btnStart.setEnabled(Boolean.TRUE);
                    break;
                default:


            }

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


                        alertDialog.show();
                        return true;
                    }
                });


            }


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {

                String id = mId.getText().toString();
                String nome = titulo.getText().toString();
                int nrPomodoro = Integer.parseInt(pomodoro.getText().toString());

                switch (view.getId()) {
                    case R.id.start:
                        Bundle bundle = new Bundle();
                        bundle.putString("_id", id);
                        bundle.putString("nome", nome);
                        intent.putExtras(bundle);

//                        enviarMessage();

                        startService(intent);
                        doBindService();


                        update(id, EnuStatus.ATIVO.getId());

                        break;
                    case R.id.stop:
//                    doUnbindService();
                        stopService(intent);

                        if (nrPomodoro <= 1) {

                            remover(id);

                        } else {

                            update(id, nrPomodoro, EnuStatus.CONCLUIDO.getId());

                        }

                        break;

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
