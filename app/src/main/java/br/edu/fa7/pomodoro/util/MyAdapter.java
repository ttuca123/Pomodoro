package br.edu.fa7.pomodoro.util;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.fa7.pomodoro.MainActivity;
import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.entity.Tarefa;
import br.edu.fa7.pomodoro.service.TarefaService;

/**
 * Created by tuca on 21/05/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Tarefa> tarefas;
    private LayoutInflater mLayoutInflater;


    public MyAdapter(Context context, List<Tarefa> tarefas)
    {
        this.tarefas = tarefas;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(view);


        return mViewHolder;
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
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.start:


                    view.getContext().startService( new Intent(view.getContext(), TarefaService.class));




                    break;
                case R.id.stop:

                    view.getContext().stopService( new Intent(view.getContext(), TarefaService.class));

                    break;



            }
        }




    }




}





