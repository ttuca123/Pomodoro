package br.edu.fa7.pomodoro;

/**
 * Created by tuca on 04/06/16.
 */
public enum EnuStatus {


    NAO_INICIADO(0, "Não iniciado"),

    ATIVO(1, "Iniciado"),

    CONCLUIDO (2, "Concluído");

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String descricao;


    EnuStatus(final int id, final String descricao)
    {
        this.id=id;
        this.descricao = descricao;
    }


    public String getDescricao()
    {
        return descricao;
    }


    public void setDescricao(final String descricao)
    {
        this.descricao = descricao;
    }

    public static EnuStatus valueOf(final EnuStatus enumStatusBci)
    {
        EnuStatus result = null;

        final EnuStatus[] values = EnuStatus.values();


        for (final EnuStatus situacao : values)
        {

            if ( situacao.equals(enumStatusBci) )
            {
                result = situacao;
                break;
            }
        }
        return result;
    }

}
