package br.edu.fa7.pomodoro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TarefaActivity extends Activity implements View.OnClickListener {

    Button btnSalvar;
    TextView titulo;
    TextView descricao;
    TextView nrPomodoro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        btnSalvar = (Button) findViewById(R.id.btnTask);
        btnSalvar.setOnClickListener(this);
        titulo = (TextView) findViewById(R.id.titulo);
        descricao = (TextView) findViewById(R.id.descricao);
        nrPomodoro = (TextView) findViewById(R.id.nrPomodoro);
    }

    @Override
    public void onClick(View v) {

        if(validarCampos()) {
            Bundle bundle = new Bundle();
            bundle.putString("titulo", titulo.getText().toString());
            bundle.putString("descricao", descricao.getText().toString());
            bundle.putString("nrPomodoro", nrPomodoro.getText().toString());

            Intent it = new Intent(this, MainActivity.class);
            it.putExtras(bundle);
            setResult(RESULT_OK, it);

            finish();
        }
    }

    private boolean validarCampos(){
        boolean condicao = true;

        if(titulo.getText().toString().isEmpty()){

            titulo.requestFocus();
            titulo.setError("Favor preencher titulo da tarefa");
            condicao = false;
        }
        if(descricao.getText().toString().isEmpty()){

            descricao.requestFocus();
            descricao.setError("Favor preencher descrição da tarefa");
            condicao = false;
        }
        if(nrPomodoro.getText().toString().isEmpty() || nrPomodoro.getText().toString().equals("0")){

            nrPomodoro.requestFocus();
            nrPomodoro.setError("Favor preencher número de pomodoros da tarefa");
            condicao = false;
        }
        return condicao;

    }
}
