package mz.maputobustracker.domain;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Hawkingg on 13/07/2016.
 */
public class Ponto implements Serializable{
    public static String PROVIDER = "mz.maputobustracker.domain.Ponto.PROVIDER";


    private String id;
    private String nome;
    private String descricao;
    private Double latitude;
    private Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatlng()
    {
        return  new LatLng(getLatitude(),getLongitude());
    }

    @Override
    public String toString() {
        return getNome();
    }
}