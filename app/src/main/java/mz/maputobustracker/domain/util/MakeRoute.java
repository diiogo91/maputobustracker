package mz.maputobustracker.domain.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import mz.maputobustracker.MapsActivity;

/**
 * Created by Diogo Amaral on 28/06/2016.
 */
public class MakeRoute {
    GoogleMap mMap;
    Context context;
    String lang;
    boolean showprogress=true;
    boolean contornos = true;
    public static JSONArray stepsArray;
    public static legs leg;
    static String LANGUAGE_SPANISH = "es";
    static String LANGUAGE_ENGLISH = "en";
    static String LANGUAGE_PORTUGUESE = "pt";
    static String LANGUAGE_FRENCH = "fr";
    static String LANGUAGE_GERMAN = "de";
    static String LANGUAGE_CHINESE_SIMPLIFIED = "zh-CN";
    static String LANGUAGE_CHINESE_TRADITIONAL = "zh-TW";
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    static String TRANSPORT_DRIVING = "driving";
    static String TRANSPORT_WALKING = "walking";
    static String TRANSPORT_BIKE = "bicycling";
    static String TRANSPORT_TRANSIT = "transit";
    static int color = Color.GREEN;

    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, boolean withIndications, String language, boolean optimize, int col,String mode,boolean prog, boolean contornos)
    {
        showprogress = prog;
        this.contornos =contornos;
        mMap = map;
        context = c;
        lang = language;
        color = col;
        if(points.size() == 2)
        {
            String url = makeURL(points.get(0).latitude,points.get(0).longitude,points.get(1).latitude,points.get(1).longitude,mode);
            new connectAsyncTask(url,withIndications).execute();
            return true;
        }
        else if(points.size() > 2)
        {
            String url = makeURL(points,"driving",optimize);
            new connectAsyncTask(url,withIndications).execute();
            return true;
        }

        return false;

    }
    private String makeURL (ArrayList<LatLng> points, String mode, boolean optimize){
        StringBuilder urlString = new StringBuilder();

        if(mode == null)
            mode = "driving";

        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append( points.get(0).latitude);
        urlString.append(',');
        urlString.append(points.get(0).longitude);
        urlString.append("&destination=");
        urlString.append(points.get(points.size()-1).latitude);
        urlString.append(',');
        urlString.append(points.get(points.size()-1).longitude);

        urlString.append("&waypoints=");
        if(optimize)
            urlString.append("optimize:true|");
        urlString.append( points.get(1).latitude);
        urlString.append(',');
        urlString.append(points.get(1).longitude);

        for(int i=2;i<points.size()-1;i++)
        {
            urlString.append('|');
            urlString.append( points.get(i).latitude);
            urlString.append(',');
            urlString.append(points.get(i).longitude);
        }


        urlString.append("&sensor=true&mode="+mode+"&key=AIzaSyDflvraodkhbj9_wV8kttvyLcBTMozL9rE");


        return urlString.toString();
    }

    private String makeURL (double sourcelat, double sourcelog, double destlat, double destlog,String mode){
        StringBuilder urlString = new StringBuilder();

        if(mode == null)
            mode = "driving";

        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode="+mode+"&alternatives=true&language="+lang+"&key=AIzaSyDflvraodkhbj9_wV8kttvyLcBTMozL9rE");
        return urlString.toString();
    }




    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }




    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;
        boolean steps;
        connectAsyncTask(String urlPass, boolean withSteps){
            url = urlPass;
            steps = withSteps;

        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(showprogress ==true) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Determinando Rota, Por favor aguarde...");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        }
        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            String value ="";
            byte ptext[] = json.getBytes(ISO_8859_1);
            value = new String(ptext, UTF_8);
            return value;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(showprogress ==true) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
            if(result!=null) {
                drawPath(result, steps);
                //makeT(result);
                if (contornos == true) {
                    if (stepsArray.length() == 0) {
                        MapsActivity.snackbar = TSnackbar.make(MapsActivity.progressBar, "Infelizmente não foi possivel obter a sugestão da rota para o ponto mais próximo de si. Tente novamente mais tarde. ", TSnackbar.LENGTH_LONG);
                    } else {
                        MapsActivity.snackbar = TSnackbar.make(MapsActivity.progressBar, "Tempo Estimado de Percurso a Pé: " + leg.duration, TSnackbar.LENGTH_INDEFINITE);
                    }
                    View snackBarView = MapsActivity.snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    MapsActivity.snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    MapsActivity.snackbar.show();

                } else {

                }
            }
        }
    }
    private void drawPath(String  result, boolean withSteps) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(10)
                        .color(color).geodesic(true));
            }
            if(withSteps)
            {
                JSONArray arrayLegs = routes.getJSONArray("legs");
                JSONObject legs = arrayLegs.getJSONObject(0);
                leg = new legs(legs);
                stepsArray = legs.getJSONArray("steps");
                //put initial point

                for(int i=0;i<stepsArray.length();i++)
                {
                    Step step = new Step(stepsArray.getJSONObject(i));

                    if(contornos == true) {
                        mMap.addMarker(new MarkerOptions()
                                .position(step.location)
                                .title("Distância: "+step.distance)
                                .snippet("Instrução: "+step.instructions)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }
            MapsActivity.makedroute =true;
        }
        catch (JSONException e) {

        }
    }


    /**
     * Class that represent every step of the directions. It store distance, location and instructions
     */
    private class Step
    {
        public String distance;
        public LatLng location;
        public String instructions;
        public String duration;
        Step(JSONObject stepJSON)
        {
            JSONObject startLocation;
            try {
                duration =stepJSON.getJSONObject("duration").getString("text");
                distance = stepJSON.getJSONObject("distance").getString("text");
                startLocation = stepJSON.getJSONObject("start_location");
                location = new LatLng(startLocation.getDouble("lat"),startLocation.getDouble("lng"));
                try {
                    instructions = URLDecoder.decode(Html.fromHtml(stepJSON.getString("html_instructions")).toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                };

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private class legs
    {
        public String distance;
        public String duration;
        public String origem;
        public String destino;

        legs(JSONObject legsJSON)
        {
            JSONObject startLocation;
            try {
                duration =legsJSON.getJSONObject("duration").getString("text");
                distance = legsJSON.getJSONObject("distance").getString("text");
                origem = legsJSON.getJSONObject("end_address").toString();
                destino= legsJSON.getJSONObject("start_address").toString();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}