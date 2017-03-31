package mz.maputobustracker.domain;

import java.io.Serializable;

/**
 * Created by Hawkingg on 28/08/2016.
 */
public class Viagem implements Serializable, Comparable<Viagem> {

    private String id;
    private String cod_rota;
    private String cod_autocarro;
    private String descricao;
    private Double latitude;
    private Double longitude;
    private String Velocidade;
    private String tempoEstChgada;
    private String cod_proximaParagem;
    private String proximaParagem;
    private Boolean chegouDestino;
    private String cod_anteriorParagem;
    private String anteriorParagem;
    private String kmpercorridos;
    private String kmapercorrer;
    private Double tempoReal;

    public String getKmpercorridos() {
        return kmpercorridos;
    }

    public void setKmpercorridos(String kmpercorridos) {
        this.kmpercorridos = kmpercorridos;
    }

    public String getKmapercorrer() {
        return kmapercorrer;
    }

    public void setKmapercorrer(String kmapercorrer) {
        this.kmapercorrer = kmapercorrer;
    }


    public Double getTempoReal() {
        return tempoReal;
    }

    public void setTempoReal(Double tempoReal) {
        this.tempoReal = tempoReal;
    }

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

    public String getCod_autocarro() {
        return cod_autocarro;
    }

    public void setCod_autocarro(String cod_autocarro) {
        this.cod_autocarro = cod_autocarro;
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

    public String getCod_anteriorParagem() {
        return cod_anteriorParagem;
    }

    public void setCod_anteriorParagem(String cod_anteriorParagem) {
        this.cod_anteriorParagem = cod_anteriorParagem;
    }

    public String getAnteriorParagem() {
        return anteriorParagem;
    }

    public void setAnteriorParagem(String anteriorParagem) {
        this.anteriorParagem = anteriorParagem;
    }

    public String getProximaParagem() {
        return proximaParagem;
    }

    public void setProximaParagem(String proximaParagem) {
        this.proximaParagem = proximaParagem;
    }

    public String getVelocidade() {
        return Velocidade;
    }

    public void setVelocidade(String velocidade) {
        Velocidade = velocidade;
    }

    public String getTempoEstChgada() {
        return tempoEstChgada;
    }

    public void setTempoEstChgada(String tempoEstChgada) {
        this.tempoEstChgada = tempoEstChgada;
    }

    public String getCod_proximaParagem() {
        return cod_proximaParagem;
    }

    public void setCod_proximaParagem(String cod_proximaParagem) {
        this.cod_proximaParagem = cod_proximaParagem;
    }

    public Boolean getChegouDestino() {
        return chegouDestino;
    }

    public void setChegouDestino(Boolean chegouDestino) {
        this.chegouDestino = chegouDestino;
    }

    @Override
    public String toString() {
        return "O "+getDescricao()
                +" está a deslocar-se em direção a "+ getProximaParagem()+
                " a uma velocidade estimada de "+getVelocidade()
                +" com o tempo estimado de chegada "+getTempoEstChgada()+".\n"
                +"Latitude: "+getLatitude()+" - Longitude: "+getLongitude();
    }
    public String toString2() {
        return "O "+getDescricao()
                +" está a deslocar-se em direção a "+ getProximaParagem()+
                " a uma  velocidade estimada de "+getVelocidade()
                +" com o tempo estimado de chegada de menos de 1 minuto"+".\n"
                +"Latitude: "+getLatitude()+" - Longitude: "+getLongitude();
    }


    @Override
    public int compareTo(Viagem another) {

            if(this.tempoReal < another.getTempoReal())
            {
                return  -1;
            }
            if(this.tempoReal >another.getTempoReal())
            {
                return  1;
            }
        return 0;
    }
}