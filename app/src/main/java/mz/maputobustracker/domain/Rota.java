package mz.maputobustracker.domain;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import mz.maputobustracker.domain.util.LibraryClass;

/**
 * Created by Hawkingg on 13/07/2016.
 */
public class Rota implements Serializable {

    public static String PROVIDER = "mz.maputobustracker.domain.Rota.PROVIDER";
    private String id;
    private String name;
    private Double lat_origem;
    private Double long_origem;
    private Double lat_destino;
    private Double long_destino;
    private String distancia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setNameInMap( Map<String, Object> map ) {
        if( getName() != null ){
            map.put( "name", getName() );
        }
    }
    public void setNameIfNull(String name) {
        if( this.name == null ){
            this.name = name;
        }
    }

    public Double getLat_origem() {
        return lat_origem;
    }

    public void setLat_origem(Double lat_origem) {
        this.lat_origem = lat_origem;
    }

    private void setLat_origemInMap( Map<String, Object> map ) {
        if( getLat_origem() != null ){
            map.put("lat_origem", getLat_origem() );
        }
    }
    public void setLat_origemIfNull(Double lat_origem) {
        if( this.lat_origem == null ){
            this.lat_origem = lat_origem;
        }
    }
    public Double getLat_destino() {
        return lat_destino;
    }

    public void setLat_destino(Double lat_destino) {
        this.lat_destino = lat_destino;
    }

    private void setLat_destinoInMap( Map<String, Object> map ) {
        if( getLat_destino() != null ){
            map.put("lat_destino", getLat_destino() );
        }
    }
    public void setLat_destinoIfNull(Double lat_destino) {
        if( this.lat_destino == null ){
            this.lat_destino = lat_destino;
        }
    }

    public Double getLong_origem() {
        return long_origem;
    }

    public void setLong_origem(Double long_origem) {
        this.long_origem = long_origem;
    }

    private void setLong_origemInMap( Map<String, Object> map ) {
        if( getLat_origem() != null ){
            map.put( "long_origem", getLat_origem() );
        }
    }
    public void setLong_origemIfNull(Double long_origem) {
        if( this.long_origem == null ){
            this.long_origem = long_origem;
        }
    }
    public Double getLong_destino() {
        return long_destino;
    }

    public void setLong_destino(Double long_destino) {
        this.long_destino = long_destino;
    }

    private void setLong_destinoInMap( Map<String, Object> map ) {
        if( getLong_destino() != null ){
            map.put( "long_destino", getLong_destino() );
        }
    }
    public void setLong_destinoIfNull(Double long_destino) {
        if( this.long_destino == null ){
            this.long_destino = long_destino;
        }
    }
    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    private void setDistanciaInMap( Map<String, Object> map ) {
        if( getDistancia() != null ){
            map.put( "distancia", getDistancia() );
        }
    }
    public void setDistanciaIfNull(String distancia) {
        if( this.distancia == null ){
            this.distancia = distancia;
        }
    }

    public void saveProviderSP(Context context, String token ){
        LibraryClass.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context ){
        return( LibraryClass.getSP( context, PROVIDER) );
    }


    public void saveDB( Rota Rota ){
        DatabaseReference firebase = LibraryClass.getFirebase().child("Rotas").child( getId() );

        if( Rota.getId() == null ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, Rota);
        }
    }
    public void saveDB( DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = LibraryClass.getFirebase().child("Rotas").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = LibraryClass.getFirebase().child("Rotas").child( getId() );

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setLong_destinoInMap(map);
        setLat_destinoInMap(map);
        setLong_origemInMap(map);
        setLat_origemInMap(map);
        setDistanciaInMap(map);

        if( map.isEmpty() ){
            return;
        }

        if( completionListener.length > 0 ){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }
    }

    public void removeDB( DatabaseReference.CompletionListener completionListener ){

        DatabaseReference firebase = LibraryClass.getFirebase().child("Rotas").child( getId() );
        firebase.setValue(null, completionListener);
    }

    public void contextDataDB( Context context ){
        DatabaseReference firebase = LibraryClass.getFirebase().child("Rotas").child( getId() );

        firebase.addListenerForSingleValueEvent( (ValueEventListener) context );
    }

    @Override
    public String toString() {
        return getName();
    }
}