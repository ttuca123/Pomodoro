package br.edu.fa7.pomodoro.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.activity.MainActivity;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.entity.Tarefa;

public class TarefaActivity extends Activity implements View.OnClickListener {

    Button btnSalvar;
    TextView titulo;
    TextView descricao;
    TextView nrPomodoro;

    private DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        btnSalvar = (Button) findViewById(R.id.btnTask);
        btnSalvar.setOnClickListener(this);
        titulo = (TextView) findViewById(R.id.titulo);
        descricao = (TextView) findViewById(R.id.descricao);
        nrPomodoro = (TextView) findViewById(R.id.nrPomodoro);
        helper = new DataBaseHelper(this);
    }

    @Override
    public void onClick(View v) {

        if (validarCampos()) {
            Tarefa tarefa = new Tarefa();
            tarefa.setTitulo(titulo.getText().toString());
            tarefa.setDescricao(descricao.getText().toString());
            tarefa.setPomodoro(Integer.parseInt(nrPomodoro.getText().toString()));


            Intent it = new Intent(this, MainActivity.class);

            setResult(RESULT_OK, it);

            salvarTarefa(tarefa);

            finish();
        }
    }

    private boolean validarCampos() {
        boolean condicao = true;

        if (titulo.getText().toString().isEmpty()) {

            titulo.requestFocus();
            titulo.setError("Favor preencher titulo da tarefa");
            condicao = false;
        }
        if (descricao.getText().toString().isEmpty()) {

            descricao.requestFocus();
            descricao.setError("Favor preencher descrição da tarefa");
            condicao = false;
        }
        if (nrPomodoro.getText().toString().isEmpty() || nrPomodoro.getText().toString().equals("0")) {

            nrPomodoro.requestFocus();
            nrPomodoro.setError("Favor preencher número de pomodoros da tarefa");
            condicao = false;
        }
        return condicao;

    }

    public void salvarTarefa(Tarefa tarefa) {
        long resultado;

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("titulo", tarefa.getTitulo());
        valores.put("descricao", tarefa.getDescricao());
        valores.put("qtd_pomodoro", tarefa.getPomodoro());
        valores.put("nr_Imagem", tarefa.getImagem());


        resultado = db.insert("tarefa", null, valores);


        if ((resultado) != -1) {
            Toast.makeText(this, "Registro Salvo", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Ocorreu um erro ao salvar no banco", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
