package br.edu.fa7.pomodoro.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.entity.Tarefa;
import br.edu.fa7.pomodoro.service.TarefaService;

import static android.provider.Settings.Global.getString;


/**
 * Criado por Artur Cavalcante 29/50/2016
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

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

    public MyAdapter(Context context)
    {

        helper = new DataBaseHelper(context);
        this.tarefas = listarTarefas();
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    private List<Tarefa> listarTarefas(){

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAREFA", null);
        cursor.moveToFirst();

        tarefas = new ArrayList<>();

        for(int i=0; i< cursor.getCount(); i++)
        {
            String titulo = cursor.getString(0);

            String descricao = cursor.getString(1);

            Integer nrPomodoro = cursor.getInt(1);

            tarefas.add(new Tarefa(titulo,descricao, nrPomodoro,R.drawable.evolution_tasks));

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
    }



    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public ImageView imageView;
        public TextView titulo;
        public TextView descricao;
        public TextView pomodoro;
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

            Context contexto = view.getContext();

            Intent intent = new Intent(contexto, TarefaService.class);

            switch (view.getId())
            {
                case R.id.start:

                    contexto.startService(intent);



                    break;
                case R.id.stop:

                    contexto.stopService( intent);



                    break;

            }
        }

        private AlertDialog criaAlertDialog(Context contexto) {
            String editar = String.valueOf(R.string.editar);
            String remover = String.valueOf(R.string.remover);

            final CharSequence[] items = {
                    editar,
                    remover };


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
