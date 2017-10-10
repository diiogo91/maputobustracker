package mz.maputobustracker.domain;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Hawkingg on 30/05/2017.
 */

public class Itinerario implements Serializable {

    private String id;
    private String cod_rota;
    private Historico.Itinerario.Ponto pontoOrigem;
    private Historico.Itinerario.Ponto pontoDestino;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCod_rota() {
        return cod_rota;
    }

    public void setCod_rota(String cod_rota) {
        this.cod_rota = cod_rota;
    }

    public Historico.Itinerario.Ponto getPontoOrigem() {
        return pontoOrigem;
    }

    public void setPontoOrigem(Historico.Itinerario.Ponto pontoOrigem) {
        this.pontoOrigem = pontoOrigem;
    }

    public Historico.Itinerario.Ponto getPontoDestino() {
        return pontoDestino;
    }

    public void setPontoDestino(Historico.Itinerario.Ponto pontoDestino) {
        this.pontoDestino = pontoDestino;
    }

    public LatLng getLatlngOrigem()
    {
        return  new LatLng(getPontoOrigem().getLatitude(),getPontoOrigem().getLongitude());
    }

    public LatLng getLatlngDestino()
    {
        return  new LatLng(getPontoDestino().getLatitude(),getPontoDestino().getLongitude());
    }

}