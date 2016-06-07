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

import br.edu.fa7.pomodoro.EnuStatus;
import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.dao.TarefaDAO;
import br.edu.fa7.pomodoro.entity.Tarefa;

public class TarefaActivity extends Activity implements View.OnClickListener {

    Button btnSalvar;
    TextView mTitulo;
    TextView mDescricao;
    TextView mPomodoro;
    private DataBaseHelper helper;

    private static final String TITULO_REQUIRED = "Favor preencher mTitulo da tarefa";
    private static final String SUBTITULO_REQUIRED = "Favor preencher descrição da tarefa";
    private static final String POMODORO_REQUIRED = "Favor preencher número de pomodoros da tarefa";
    private static final String REG_SALVO = "Registro Salvo";
    private static final String REG_ATUALIZADO = "Registro Atualizado";
    private static final String ERRO_SALVAR = "Ocorreu um erro ao salvar no banco";
    private Tarefa tarefa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);

        btnSalvar = (Button) findViewById(R.id.btnTask);
        btnSalvar.setOnClickListener(this);
        mTitulo = (TextView) findViewById(R.id.titulo);
        mDescricao = (TextView) findViewById(R.id.descricao);
        mPomodoro = (TextView) findViewById(R.id.nrPomodoro);
        helper = new DataBaseHelper(this);

        preencherDados();
    }


    private void preencherDados() {

        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            String id = bundle.getString("_id");

            String titulo = bundle.getString("mTitulo");
            String descricao = bundle.getString("mDescricao");
            String pomodoro = bundle.getString("mPomodoro");


            tarefa = new Tarefa();
            tarefa.setId(Integer.parseInt(id));


            this.mTitulo.setText(titulo);
            this.mDescricao.setText(descricao);
            mPomodoro.setText(pomodoro);
        }

    }

    @Override
    public void onClick(View v) {

        if (validarCampos()) {

            if (tarefa == null) {

                tarefa = new Tarefa();
                tarefa.setTitulo(mTitulo.getText().toString());
                tarefa.setDescricao(mDescricao.getText().toString());
                tarefa.setPomodoro(Integer.parseInt(mPomodoro.getText().toString()));
                tarefa.setStatus(EnuStatus.NAO_INICIADO.getId());

                Intent it = new Intent(this, MainActivity.class);

                setResult(RESULT_OK, it);

                salvarTarefa(tarefa);


            }else{

                atualizarTarefa();

              Toast  toast = Toast.makeText(this,
                        "Alteração com sucesso  " ,
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            Intent it = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(it);
        }
    }

    private boolean validarCampos() {
        boolean condicao = true;

        if (mTitulo.getText().toString().isEmpty()) {

            mTitulo.requestFocus();
            mTitulo.setError(TITULO_REQUIRED);
            condicao = false;
        }
        if (mDescricao.getText().toString().isEmpty()) {

            mDescricao.requestFocus();
            mDescricao.setError(SUBTITULO_REQUIRED);
            condicao = false;
        }
        if (mPomodoro.getText().toString().isEmpty() || mPomodoro.getText().toString().equals("0")) {

            mPomodoro.requestFocus();
            mPomodoro.setError(POMODORO_REQUIRED);
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

    public void atualizarTarefa() {
        long resultado;

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("mTitulo", mTitulo.getText().toString());
        valores.put("mDescricao", mDescricao.getText().toString());
        valores.put("qtd_pomodoro", mPomodoro.getText().toString());

        valores.put("sta_status", EnuStatus.NAO_INICIADO.getId());
        TarefaDAO tarefaDAO = new TarefaDAO(getBaseContext());

        resultado = tarefaDAO.update(tarefa.getId()+"", valores);


        if ((resultado) != -1) {
            Toast.makeText(this, REG_ATUALIZADO, Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, ERRO_SALVAR, Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();

    }
}
