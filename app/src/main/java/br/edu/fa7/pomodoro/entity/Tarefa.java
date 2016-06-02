package br.edu.fa7.pomodoro.entity;

/**
 * Created by tuca on 21/05/16.
 */
public class Tarefa {


    private int id;

    private Integer pomodoro;

    private int imagem;

    private String titulo;

    private String descricao;

    public Tarefa(){

    }

    public Tarefa(int id, String titulo, String descricao, int pomodoro, int imagem) {
      this.id = id;
        this.imagem = imagem;
        this.pomodoro = pomodoro;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }



    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPomodoro() {
        return pomodoro;
    }

    public void setPomodoro(Integer pomodoro) {
        this.pomodoro = pomodoro;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }
}
