package br.edu.fa7.pomodoro.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.fa7.pomodoro.EnuStatus;
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

    private static final String TITULO_REQUIRED = "Favor preencher titulo da tarefa";
    private static final String SUBTITULO_REQUIRED = "Favor preencher descrição da tarefa";
    private static final String POMODORO_REQUIRED = "Favor preencher número de pomodoros da tarefa";
    private static final String REG_SALVO = "Registro Salvo";
    private static final String ERRO_SALVAR = "Ocorreu um erro ao salvar no banco";

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
            tarefa.setStatus(EnuStatus.NAO_INICIADO.getId());

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
            titulo.setError(TITULO_REQUIRED);
            condicao = false;
        }
        if (descricao.getText().toString().isEmpty()) {

            descricao.requestFocus();
            descricao.setError(SUBTITULO_REQUIRED);
            condicao = false;
        }
        if (nrPomodoro.getText().toString().isEmpty() || nrPomodoro.getText().toString().equals("0")) {

            nrPomodoro.requestFocus();
            nrPomodoro.setError(POMODORO_REQUIRED);
            condicao = false;
        }
        return condicao;

    }

    public void salvarTarefa(Tarefa tarefa) {
        long resultado;

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("mTitulo", tarefa.getTitulo());
        valores.put("mDescricao", tarefa.getDescricao());
        valores.put("qtd_pomodoro", tarefa.getPomodoro());
        valores.put("nr_Imagem", tarefa.getImagem());
        valores.put("sta_status", tarefa.getStatus());


        resultado = db.insert("tarefa", null, valores);


        if ((resultado) != -1) {
            Toast.makeText(this, REG_SALVO, Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, ERRO_SALVAR, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
