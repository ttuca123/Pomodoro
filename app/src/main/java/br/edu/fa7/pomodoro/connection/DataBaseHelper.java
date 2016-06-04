package br.edu.fa7.pomodoro.connection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.fa7.pomodoro.R;
import br.edu.fa7.pomodoro.entity.Tarefa;

/**
 * Created by tuca on 29/05/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {


    private static final String BANCO_DADOS = "Pomodoro";

    private static int VERSAO = 1;

    public DataBaseHelper(Context context){

        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE tarefa (_id INTEGER PRIMARY KEY,"+
        " mTitulo TEXT, mDescricao TEXT, qtd_pomodoro INTEGER, nr_Imagem INTEGER, sta_status INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long update(String tabela, String id, ContentValues values){

        SQLiteDatabase db = this.getWritableDatabase();



        return db.update(tabela, values, "_id = ?",
                new String[]{id});

    }


    public List<?> getList(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        List<Object> objects=null;

        try {
            cursor= db.rawQuery(query, null);
            cursor.moveToFirst();

            objects = new ArrayList<>();

            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(0);

                String titulo = cursor.getString(1);

                String descricao = cursor.getString(2);

                Integer nrPomodoro = cursor.getInt(3);

                Integer status = cursor.getInt(5);

                objects.add(new Tarefa(id, titulo, descricao, nrPomodoro, R.drawable.evolution_tasks, status));

                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            cursor.close();
        }



        return objects;
    }




    public long delete(String tabela , String id){


        SQLiteDatabase db = this.getWritableDatabase();
        String where[] = new String[]{id};
        return db.delete("tarefa", "_id=?", where);

    }

}
