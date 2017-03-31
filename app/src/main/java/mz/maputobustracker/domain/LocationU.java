package mz.maputobustracker.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hawkingg on 07/09/2016.
 */
public class LocationU {

    private String idUtilizador;
    private String idDispositivo;
    private Double latitude;
    private Double longitude;

    public String getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(String idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
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

    public LatLng getLatLang ()
    {
        return  new LatLng(getLatitude(),getLongitude());
    }
}
