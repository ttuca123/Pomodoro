package br.edu.fa7.pomodoro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.List;

import br.edu.fa7.pomodoro.EnuStatus;
import br.edu.fa7.pomodoro.connection.DataBaseHelper;
import br.edu.fa7.pomodoro.entity.Tarefa;

/**
 * Created by Artur on 04/06/16.
 */
public class TarefaDAO {

    private DataBaseHelper helper;

    private Context contexto;
    private static final String TABELA = "tarefa";

    public TarefaDAO(Context contexto) {
        this.contexto = contexto;
        this.helper = new DataBaseHelper(contexto);

    }


    public long update(String id, ContentValues values) {


        return helper.update(TABELA, id, values);


    }


    public List<Tarefa> getTarefas() {


        return (List<Tarefa>) helper.getList("SELECT * FROM TAREFA");

    }


    public long delete(String id) {
        long resultado;
        SQLiteDatabase db = helper.getWritableDatabase();
        String where[] = new String[]{id};
        resultado = db.delete(TABELA, "_id=?", where);
        Toast toast;
        return resultado;


    }


}
