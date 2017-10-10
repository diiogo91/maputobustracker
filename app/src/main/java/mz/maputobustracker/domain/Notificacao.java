package mz.maputobustracker.domain;

/**
 * Created by Hawkingg on 30/05/2017.
 */

public class Notificacao {
    public static String PROVIDER = "mz.maputobustracker.domain.Notificacao.PROVIDER";

    private String mensagem;
    private String cod_vidagem;

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCod_vidagem() {
        return cod_vidagem;
    }

    public void setCod_vidagem(String cod_vidagem) {
        this.cod_vidagem = cod_vidagem;
    }
}
