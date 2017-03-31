package mz.maputobustracker.domain;

import java.io.Serializable;

/**
 * Created by Diogo Amaral on 13/07/2016.
 */
public class Autocarro implements Serializable {

    private String id;
    private String nome;
    private String detalhes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return getNome();
    }
}
