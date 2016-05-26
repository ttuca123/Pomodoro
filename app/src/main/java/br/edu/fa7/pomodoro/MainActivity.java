package br.edu.fa7.pomodoro;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.fa7.pomodoro.entity.Tarefa;
import br.edu.fa7.pomodoro.util.MyAdapter;

public class MainActivity extends Activity implements View.OnClickListener
 {



    private Button btnTask;
    private static final short NEW_ACTIVITY_ID=1;
    private RecyclerView recyclerView;
     private RecyclerView.Adapter mAdapter;
     private RecyclerView.LayoutManager mLayoutManager;
    private List<Tarefa> tarefas;
     public TextView cronometro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mainReclyclerView);

        btnTask = (Button) findViewById(R.id.btnTask);

        btnTask.setOnClickListener(this);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        tarefas = new ArrayList<>();

        tarefas.add(new Tarefa("Tarefa 1","Descricao ", 2, R.mipmap.ic_launcher));


        cronometro = (TextView) findViewById(R.id.cronometro);


        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter =  new MyAdapter(this, tarefas);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         switch (requestCode) {
             case NEW_ACTIVITY_ID:
                 if(resultCode == RESULT_OK)
                 {
                     String titulo = data.getStringExtra("titulo");
                     String descricao = data.getStringExtra("descricao");
                     String nrPomodoro = data.getStringExtra("nrPomodoro");
                     tarefas.add(new Tarefa(titulo, descricao, Integer.parseInt(nrPomodoro), R.mipmap.ic_launcher));
                 }
                 break;
         }

     }








    @Override
    public void onClick(View v) {

            Intent it = new Intent(this, TarefaActivity.class);
            startActivityForResult(it, NEW_ACTIVITY_ID);
    }
}
