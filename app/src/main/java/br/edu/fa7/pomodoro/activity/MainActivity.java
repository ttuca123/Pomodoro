package br.edu.fa7.pomodoro.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import br.edu.fa7.pomodoro.dao.TarefaDAO;
import br.edu.fa7.pomodoro.entity.Tarefa;

import br.edu.fa7.pomodoro.service.BoundService;
import br.edu.fa7.pomodoro.service.ListenValue;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListenValue {


    private static final short NEW_ACTIVITY_ID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBtnTask;
    public TextView mCronometro;
    private Intent mBoundServiceIntent;
    private BoundService mBoundService;
    private boolean mIsBound = false;

    private Timer timer;
    private TarefaDAO tarefaDAO;
    private List<Tarefa> tarefas;
    private Handler mHandler;
    private static final String POMODORO = "25:00";
    public int idTarefa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getInstancy();

        readMessage();

        refreshView();

        doBindService();
    }


    private void getInstancy() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mainReclyclerView);

        mBtnTask = (Button) findViewById(R.id.btnTask);

        mCronometro = (TextView) findViewById(R.id.cronometro);

        mBtnTask.setOnClickListener(this);


        tarefaDAO = new TarefaDAO(getBaseContext());

        mBoundServiceIntent = new Intent(this, BoundService.class);

        startService(mBoundServiceIntent);

        mHandler = new Handler();


    }

    public void refreshView() {

        mAdapter = new MyAdapter(MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    private void readMessage() {

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

                    ContentValues values = new ContentValues();
                    values.put("qtd_pomodoro", tarefa.getPomodoro() - 1);
                    values.put("sta_status", status);

                    if (tarefa.getPomodoro() > 1) {
                        tarefaDAO.update(id, values);

                        Toast.makeText(getApplicationContext(), msgs.toString(), Toast.LENGTH_LONG).show();


                    } else {

                        tarefaDAO.delete(id);


                        Toast.makeText(getApplicationContext(), msgs.toString(), Toast.LENGTH_LONG).show();
                    }
                    mIsBound = Boolean.FALSE;

                    break;
                }

            }


        }

    }


    @Override
    public void newValue(final String value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCronometro.setText(String.valueOf(value));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_ACTIVITY_ID:
                if (resultCode == RESULT_OK) {

                    mAdapter = new MyAdapter(this);

                    mLayoutManager = new LinearLayoutManager(this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

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




                    BoundService.LocalBinder binder = (BoundService.LocalBinder) service;

                    mBoundService = binder.getService();
                    mBoundService.add(MainActivity.this);
                    mIsBound = true;

                    Log.i("MyService", "Cliente Conectado.");


        }

        @Override
        public void onServiceDisconnected(ComponentName className) {

            mIsBound = false;
            Log.i("MyService", "Cliente Desconectado.");
        }
    };

    private void doBindService() {


            bindService(mBoundServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = Boolean.TRUE;


    }

    private void doUnbindService() {
        if (!mIsBound) {
            mBoundService.closeService();

            mCronometro.setText(POMODORO);
            mIsBound = Boolean.FALSE;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


            doUnbindService();

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


            ContentValues values = new ContentValues();

            values.put("sta_status", status);

            long resultado = tarefaDAO.update(id, values);


            if (resultado != -1) {
                refreshView();
                exibirMensagem(MSG_UPDATE_SUCESSO);

            } else {
                exibirMensagem(MSG_ERRO_SQL);


            }
        }


        private void exibirMensagem(String mensagem) {

            Toast toast = Toast.makeText(contexto,
                    mensagem,
                    Toast.LENGTH_SHORT);
        }

        private void update(String id, int nrPomodoro, int status) {


            ContentValues values = new ContentValues();
            values.put("qtd_pomodoro", nrPomodoro - 1);
            values.put("sta_status", status);

            long resultado = tarefaDAO.update(id, values);


            if (resultado != -1) {
                refreshView();
                exibirMensagem(MSG_UPDATE_SUCESSO);

            } else {

                exibirMensagem(MSG_ERRO_SQL);

            }
        }


        private void remover(String id) {

            if (tarefaDAO.delete(id) != -1) {

                exibirMensagem(MSG_DELETE_SUCESSO);
                refreshView();

            } else {

                exibirMensagem(MSG_ERRO_SQL);

            }

        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Tarefa tarefa = tarefas.get(position);
            holder.mImageView.setImageResource(tarefa.getImagem());
            holder.mTitulo.setText(tarefa.getTitulo());
            holder.mDescricao.setText(tarefa.getDescricao());
            holder.mPomodoro.setText(tarefa.getPomodoro().toString());
            holder.mId.setText(tarefa.getId() + "");

            switch (tarefa.getStatus()) {

                case 0:
                    holder.mImageView.setImageResource(R.mipmap.ic_alarm_off_black_24dp);
                    holder.mBtnStart.setEnabled(Boolean.TRUE);
                    holder.mBtnStop.setEnabled(Boolean.FALSE);
                    break;
                case 1:
                    holder.mImageView.setImageResource(R.mipmap.ic_alarm_on_black_24dp);

                    holder.mBtnStart.setEnabled(Boolean.FALSE);
                    holder.mBtnStop.setEnabled(Boolean.TRUE);
                    break;
                case 2:

                    holder.mImageView.setImageResource(R.mipmap.ic_check_circle_black_24dp);
                    holder.mBtnStop.setEnabled(Boolean.FALSE);
                    holder.mBtnStart.setEnabled(Boolean.TRUE);
                    break;
                default:
            }

        }


        @Override
        public int getItemCount() {
            return tarefas.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mImageView;
            public TextView mTitulo;
            public TextView mDescricao;
            public TextView mPomodoro;
            public TextView mId;
            public Button mBtnStart;
            public Button mBtnStop;
            private AlertDialog alertDialog;

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public MyViewHolder(View itemView) {
                super(itemView);

                mImageView = (ImageView) itemView.findViewById(R.id.imageView);
                mTitulo = (TextView) itemView.findViewById(R.id.tarefa);
                mDescricao = (TextView) itemView.findViewById(R.id.descricao);
                mPomodoro = (TextView) itemView.findViewById(R.id.lblPomodoro);
                mBtnStart = (Button) itemView.findViewById(R.id.start);
                mBtnStop = (Button) itemView.findViewById(R.id.stop);
                mBtnStart.setOnClickListener(this);
                mBtnStop.setOnClickListener(this);
                mId = (TextView) itemView.findViewById(R.id._id);


                mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence[] items = {
                                "Editar",
                                "Remover"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto).setTitle("Opções");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i) {

                                    case 0:
                                        Bundle bundle = new Bundle();
                                        bundle.putString("_id", mId.getText().toString());
                                        bundle.putString("mTitulo", mTitulo.getText().toString());

                                        bundle.putString("mDescricao", mDescricao.getText().toString());

                                        bundle.putString("mPomodoro", mPomodoro.getText().toString());

                                        Intent it = new Intent(getBaseContext(), TarefaActivity.class);

                                        it.putExtras(bundle);

                                        startActivity(it);

                                        break;
                                    case 1:

                                        remover(mId.getText().toString());

                                        break;


                                }


                            }
                        }).show();
                        return true;
                    }
                });


            }


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {

                String id = mId.getText().toString();
                String nome = mTitulo.getText().toString();
                int nrPomodoro = Integer.parseInt(mPomodoro.getText().toString());

                switch (view.getId()) {
                    case R.id.start:


                        Bundle bundle = new Bundle();
                        bundle.putString("_id", id);
                        bundle.putString("nome", nome);
                        mBoundServiceIntent.putExtras(bundle);
                        mBoundService.startCounter(mBoundServiceIntent);
                        mIsBound = Boolean.TRUE;
                        update(id, EnuStatus.ATIVO.getId());

                        break;
                    case R.id.stop:

                        mBoundService.closeService();

                        mIsBound = Boolean.FALSE;


                        if (nrPomodoro <= 1) {

                            remover(id);

                        } else {

                            update(id, nrPomodoro, EnuStatus.CONCLUIDO.getId());

                        }

                        break;

                }
            }


        }
    }


}
