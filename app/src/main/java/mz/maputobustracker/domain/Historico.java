package mz.maputobustracker.domain;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Hawkingg on 13/07/2016.
 */
public class Historico implements Serializable, Comparable<Historico>{

    public String id;
    public String cod_rota;
    public String cod_utilizador;
    public Itinerario itinerario;
    private String data;
    private String dataUltima;

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

    public String getCod_utilizador() {
        return cod_utilizador;
    }

    public void setCod_utilizador(String cod_utilizador) {
        this.cod_utilizador = cod_utilizador;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public String getDataUltima() {
        return dataUltima;
    }

    public void setDataUltima(String dataUltima) {
        this.dataUltima = dataUltima;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int compareTo(Historico another) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyyhhmmss");
        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        try {
            if(DATE_FORMAT.parse(getDataUltima()).before( DATE_FORMAT.parse(another.getDataUltima())))
            {
                return  1;
            }
            if(DATE_FORMAT.parse(getDataUltima()).after( DATE_FORMAT.parse(another.getDataUltima())))
            {
                return  -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class Itinerario implements Serializable {

        private String id;
        private String cod_rota;
        private Ponto pontoOrigem;
        private Ponto pontoDestino;


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

        public Ponto getPontoOrigem() {
            return pontoOrigem;
        }

        public void setPontoOrigem(Ponto pontoOrigem) {
            this.pontoOrigem = pontoOrigem;
        }

        public Ponto getPontoDestino() {
            return pontoDestino;
        }

        public void setPontoDestino(Ponto pontoDestino) {
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
        public static class Ponto implements Serializable{

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

            @Override
            public String toString() {
                return getNome();
            }
        }
    }


    @Override
    public String toString() {
        return "Código da Rota: "+ getCod_rota() +"\n"
                +"Origem: "+getItinerario().getPontoOrigem().getNome()+"\n"
                +"Destino: "+getItinerario().getPontoDestino().getNome()+"\n"
                +"Data de Criação: " +getData() +"\n"
                +"Data da última navegação: " +getDataUltima() +"\n";
    }
}
