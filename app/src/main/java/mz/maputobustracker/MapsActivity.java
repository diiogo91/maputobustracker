package mz.maputobustracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mz.maputobustracker.adapter.BadgeView;
import mz.maputobustracker.adapter.CustomArrayAdapterNot;
import mz.maputobustracker.adapter.UserRecyclerAdapter;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.util.AppLocationService;
import mz.maputobustracker.domain.Autocarro;
import mz.maputobustracker.domain.Historico;
import mz.maputobustracker.domain.util.LocationAddress;
import mz.maputobustracker.domain.LocationU;
import mz.maputobustracker.domain.Ponto;
import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Viagem;
import mz.maputobustracker.domain.util.LibraryClass;
import mz.maputobustracker.domain.util.MakeRoute;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, GoogleMap.OnMarkerClickListener , SlidingUpPanelLayout.PanelSlideListener{
    public GoogleMap mMap;
    private static final int INITIAL_ZOOM_LEVEL = 16;
    private Circle searchCircle;
    private Circle searchCircle2;
    private FirebaseAuth mAuth;
    private UserRecyclerAdapter adapter;
    private Utente ut;
    private String nomeUtilizador = "Eu";
    private FirebaseAuth.AuthStateListener authStateListener;
    private Rota selectedRota;
    private Marker user;
    private Marker pontoParagem;
    private Handler handler;
    private Ponto pontoMaixProximo;
    private HashMap<String,Marker> mapaParagens;
    private HashMap<String,Marker> mapaAutocarros;
    private HashMap<String,Circle> mapaCirculos;
    private HashMap<String,Boolean> mapaEmViagem;
    private String codigo ="";
    private int cont;
    private boolean vemdoMenuPrincipal= false;
    private Ponto inicioParagem;
    private boolean notificado =false;
    private Ponto actualParagem;
    private Double distanciaMaxima;
    private Double distanciaminima;
    private Double tempoEstimado;
    private LocationU locationU;
    private ArrayList<String> rotasId;
    private boolean isPontoProximoCheck;
    private ArrayList<Ponto> listaParagens = new ArrayList<Ponto>();
    private DatabaseReference databaseReference;
    private Marker bus;
    private ArrayList<Autocarro> autocarros;
    public static List<LatLng> polyRoute = new ArrayList<>();
    public static List<LatLng> routeBZ = new ArrayList<>();
    public static List<LatLng> routeZB = new ArrayList<>();
    private Autocarro autocarro;
    private AppLocationService appLocationService;
    private Viagem vg;
    private Runnable timerRunnable;
    private ArrayList<LatLng> pontosfix;
    private LatLng origem;
    private LatLng destino;
    //Auoutcarros
    private Double currentlat = -25.971584;
    private Double currentlong = 32.56531;
    private ArrayList<Viagem> viagens;
    private static Firebase databasereference;
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(-25.971584, 32.565313);
    private static final LatLng defaultLocation = new LatLng(-25.966308, 32.562239);
    private static final LatLng at1 = new LatLng(-25.969896, 32.565999);
    private static final LatLng at2 = new LatLng(-25.964503, 32.567639);
    //private static final LatLng at5 = new LatLng(-25.959524, 32.554966);
    // BusStop
    public Rota rotaNavegada;
    // Animate Camera Things
    private static final int ANIMATE_SPEEED_TURN = 1000;
    private static final int BEARING_OFFSET = 20;
    private boolean emviagem;
    ArrayList<LatLng> points = new ArrayList<>(2);
    private MakeRoute makeRoute;
    private MakeRoute makeRoute2;
    private GeoFire geoFire;
    public static ProgressDialog progressDialog;
    private Historico.Itinerario selectedItinerario;
    private int inicio;
    private int actual;
    private int fim;
    public static TSnackbar snackbar;
    private boolean chegou = false;
    private boolean notif =false;
    private String rotaID;
    private LocationAddress locationAddress;
    public static boolean makedroute =false;
    public static ProgressBar progressBar;
    private String endereco;
    private SlidingUpPanelLayout mLayout;
    private ImageView sliderImage;
    private View mapView;
    private Button googleMapsButton;
    public static ListView listaNotificacoes;
    private ArrayList<String> listaNot;
    private ArrayList<String> copyList;
    private Button btnNotf;
    private BadgeView badge_corner;
    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createRoutes();
        //createPontos();
        //LibraryClass.getFirebase().child("Viagens").removeValue();
        Intent intent = getIntent();
        endereco ="";
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        selectedRota = (Rota) intent.getSerializableExtra("RotaSelecionada");
        selectedItinerario = (Historico.Itinerario) intent.getSerializableExtra("selectedItinerario");
        ut= (Utente) intent.getSerializableExtra("utilizador");
        vemdoMenuPrincipal = intent.getBooleanExtra("vemdoMenuPrincipal",false);
        locationAddress = new LocationAddress();
        listaNotificacoes = (ListView) findViewById(R.id.listaNotificacoes);
        listaNot= new ArrayList<>();
        copyList = new ArrayList<>();
        appLocationService = new AppLocationService(
                MapsActivity.this);
        if(ut != null)
        {
            nomeUtilizador = ut.getName();
            locationU = new LocationU();
            locationU.setIdUtilizador(ut.getId());
            locationU.setIdDispositivo(getIMEI());
            locationU.setLatitude(defaultLocation.latitude);
            locationU.setLongitude(defaultLocation.longitude);
            //    LibraryClass.getFirebase().child("Localizacoes").child(getIMEI()+ut.getId()).setValue(locationU);
        }

        sliderImage = (ImageView)findViewById(R.id.ivSliderArrow);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mapaAutocarros = new HashMap<>();
        mapaParagens = new HashMap<>();
        mapaCirculos = new HashMap<>();
        mapaEmViagem  = new HashMap<>();
        databaseReference = LibraryClass.getFirebase();
        isPontoProximoCheck = intent.getBooleanExtra("isPontoProximo", false);
        // createViatura();
        setContentView(R.layout.activity_slidingupgeral);
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map));
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( firebaseAuth.getCurrentUser() == null  ){
                    Intent intent = new Intent( MapsActivity.this, LoginActivity.class );
                    startActivity( intent );
                    finish();
                }
            }
        };

        btnNotf = (Button) findViewById(R.id.btnNotfCount);
        badge_corner = new BadgeView(this, btnNotf);
        badge_corner .setText("0");
        badge_corner .setTextColor(Color.WHITE);
        badge_corner .setBackgroundResource(R.mipmap.notification);
        badge_corner .setTextSize(25);
        badge_corner.setTextColor(Color.RED);
        badge_corner .setBadgeMargin(10,0);
        badge_corner .show();
        btnNotf.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                mLayout.requestLayout(); // call requestLayout before expandPanel
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        googleMapsButton = (Button) findViewById(R.id.btnObterDirecoes);
        googleMapsButton.setVisibility(Button.INVISIBLE);
        googleMapsButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("A navegação sera feita via Google Maps. Deseja navegar na rota para o ponto mais próximo?")
                        .setCancelable(true)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Locale pt = new Locale("pt" , "PT");
                                String uri = String.format(pt,"http://maps.google.com/maps?saddr="+locationU.getLatitude()+","+locationU.getLongitude()+"&daddr="+destino.latitude+","+destino.longitude+"&mode=walking&sensor=false");
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setPackage("com.google.android.apps.maps");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();



            }
        });
    }

    public String getIMEI()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return   telephonyManager.getDeviceId();
    }
    public void createRoutes() {
        databaseReference = LibraryClass.getFirebase();
        Rota baixaZimpeto = new Rota();
        baixaZimpeto.setName("Zimeto - Baixa");
        baixaZimpeto.setLat_origem(-25.830474);
        baixaZimpeto.setLong_origem(32.574576);
        baixaZimpeto.setLat_destino(-25.971280);
        baixaZimpeto.setLong_destino(32.564965);
        baixaZimpeto.setDistancia("19.1 Km");
        baixaZimpeto.setId("1");
        databaseReference.child("Rotas").child("rota2").setValue(baixaZimpeto);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.custom_infowindowbus, null);
                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.title);
                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.snippet);
                // Setting the latitude
                tvLat.setText(arg0.getTitle());
                // Setting the longitude
                tvLng.setText(arg0.getSnippet());
                // Returning the view containing InfoWindow contents
                return v;
            }
        });
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng baixaMaputo = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);
        LibraryClass.getFirebase().child("utilizadores").child(ut.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Utente.class) != null) {
                    Utente post = dataSnapshot.getValue(Utente.class);
                    ut = dataSnapshot.getValue( Utente.class );
                    nomeUtilizador = ut.getName();
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mMap.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationU.getLatLang(), INITIAL_ZOOM_LEVEL));
                return true;
            }
        });
        mMap.setOnMarkerClickListener(MapsActivity.this);
        rotaID ="rota1";
        mapaParagens = new HashMap<>();
        mapaCirculos = new HashMap<>();
        listaParagens = new ArrayList<>();
        if(selectedItinerario != null && isPontoProximoCheck == false )
        {
            rotaID = selectedItinerario.getCod_rota();
            LibraryClass.getFirebase().child("Rotas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    origem = new LatLng(selectedItinerario.getPontoOrigem().getLatitude(),selectedItinerario.getPontoOrigem().getLongitude());
                    destino = new LatLng(selectedItinerario.getPontoDestino().getLatitude(),selectedItinerario.getPontoDestino().getLongitude());
                    points = new ArrayList<>(2);
                    points.add(origem);
                    points.add(destino);
                    LibraryClass.getFirebase().child("Paragens").child(rotaID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                Ponto post = postSnapshot.getValue(Ponto.class);
                                listaParagens.add(post);
                            }
                            Double distPointMax = CalculationByDistance(selectedItinerario.getLatlngOrigem(),selectedItinerario.getLatlngDestino());
                            ArrayList<Ponto> filtredListaParagens = new ArrayList<>();
                            pontosfix = new ArrayList<LatLng>();
                            for(Ponto ponto: listaParagens)
                            {   Double refParagOrg=CalculationByDistance(selectedItinerario.getLatlngOrigem(),ponto.getLatlng());
                                Double refParagDst=CalculationByDistance(selectedItinerario.getLatlngDestino(),ponto.getLatlng());
                                //if(refParagOrg<=distPointMax && refParagDst <=distPointMax)
                                //{
                                    filtredListaParagens.add(ponto);
                                    pontoParagem =    mMap.addMarker(new MarkerOptions().position(ponto.getLatlng()).title(ponto.getNome()).draggable(true)
                                            .snippet(ponto.getDescricao())
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.busstop2)));

                                    mapaParagens.put(ponto.getId(),pontoParagem);
                                    searchCircle = mMap.addCircle(new CircleOptions().center(ponto.getLatlng()).radius(50));
                                    searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                                    searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                                    mapaCirculos.put(ponto.getId(),searchCircle);
                                    System.out.println("PONTOS: "+ponto.getNome());
                                //}
                            }
                            listaParagens =filtredListaParagens;
                            System.out.println("NR PONTOS: "+listaParagens.size());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem, INITIAL_ZOOM_LEVEL));
                            searchCircle = mMap.addCircle(new CircleOptions().center(origem).radius(10));
                            searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                            searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                            searchCircle2 = mMap.addCircle(new CircleOptions().center(destino).radius(10));
                            searchCircle2.setFillColor(Color.argb(66,255,0,255));
                            searchCircle2.setStrokeColor(Color.argb(66,0,0,0));
                            makeRoute = new MakeRoute();
                            String lang = "portuguese";
                            makeRoute.drawRoute(mMap, MapsActivity.this, points, true, lang, true,Color.GREEN,"driving",true,false);
                            fetchAutocarros();
                            fetchUserLocation();
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                            snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                            snackbar.show();
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                    snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                }
            });

        }
        else if (selectedRota != null && isPontoProximoCheck == true)
        {
            googleMapsButton.setVisibility(Button.VISIBLE);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    // instantiate the location manager, note you will need to request permissions in your manifest
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    // get the last know location from your location manager.
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Check Permissions Now
                        // Check Permissions Now
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                1);
                    } else {
                        // permission has been granted, continue as usual
                        // now get the lat/lon from the location and do something with it.
                        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        System.out.println("ENABLED: "+statusOfGPS);
                        if (statusOfGPS == false) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            builder.setMessage("Para obter a sua localização actual é necessário habilitar o GPS, deseja habilitar?")
                                    .setCancelable(false)
                                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    })
                                    .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else
                        {
                            locationU.setLatitude(location.getLatitude());
                            locationU.setLongitude(location.getLongitude());
                            LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                                    getApplicationContext(), new GeocoderHandler());
                            //LibraryClass.getFirebase().child("Localizacoes").child(getIMEI() + ut.getId()).setValue(locationU);
                            user.remove();
                            user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                                    .snippet("Minha Localização Actual: " + endereco)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(user.getPosition()));
                            searchCircle.remove();
                            searchCircle2.remove();
                            mapaCirculos = new HashMap<String, Circle>();
                            mapaParagens = new HashMap<String, Marker>();
                            mMap.clear();
                            for (Ponto post : listaParagens) {
                                LatLng coord = new LatLng(post.getLatitude(), post.getLongitude());
                                pontoParagem = mMap.addMarker(new MarkerOptions().position(coord).title(post.getNome()).draggable(true)
                                        .snippet(post.getDescricao())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.busstop2)));
                                mapaParagens.put(post.getId(), pontoParagem);
                                searchCircle = mMap.addCircle(new CircleOptions().center(post.getLatlng()).radius(50));
                                searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                                searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                                mapaCirculos.put(post.getId(), searchCircle);
                            }
                            getNearstLocationMarker();
                            user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                                    .snippet("Minha Localização Actual: " + endereco)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), INITIAL_ZOOM_LEVEL));
                        }
                    }
                    return true;
                }
            });


            rotaID = selectedRota.getId();
            mapaParagens = new HashMap<>();
            LibraryClass.getFirebase().child("Paragens").child(rotaID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        System.out.println("ENTROU PARAGENS");
                        Ponto post = postSnapshot.getValue(Ponto.class);
                        listaParagens.add(post);
                        LatLng coord = new LatLng(post.getLatitude(),post.getLongitude());
                        pontoParagem =  mMap.addMarker(new MarkerOptions().position(coord).title(post.getNome()).draggable(true)
                                .snippet(post.getDescricao())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.busstop2)));
                        mapaParagens.put(post.getId(),pontoParagem);
                        searchCircle = mMap.addCircle(new CircleOptions().center(post.getLatlng()).radius(50));
                        searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                        searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                        mapaCirculos.put(post.getId(),searchCircle);
                    }
                    fetchUserLocationAndNearestPlace();
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                    snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                }

            });
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                    snackbar=TSnackbar .make(progressBar,"" +
                            "Utente está a deslocar-se..determinando paragem mais proxima de si.",TSnackbar.LENGTH_INDEFINITE);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                }
                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    // TODO Auto-generated method stub
                    if(arg0.getId().equals(user.getId())){
                        Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                        locationU.setLatitude(arg0.getPosition().latitude);
                        locationU.setLongitude(arg0.getPosition().longitude);
                        locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                                getApplicationContext(), new GeocoderHandler());
                        LibraryClass.getFirebase().child("Localizacoes").child(getIMEI()+ut.getId()).setValue(locationU);
                        user.remove();
                        user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                                .snippet("Minha Localizacao Actual: "+endereco)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                        searchCircle.remove();
                        searchCircle2.remove();
                        mapaCirculos = new HashMap<String, Circle>();
                        mapaParagens = new HashMap<String, Marker>();
                        mMap.clear();
                        for (Ponto post: listaParagens) {
                            LatLng coord = new LatLng(post.getLatitude(),post.getLongitude());
                            pontoParagem =  mMap.addMarker(new MarkerOptions().position(coord).title(post.getNome()).draggable(true)
                                    .snippet(post.getDescricao())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.busstop2)));
                            mapaParagens.put(post.getId(),pontoParagem);
                            searchCircle = mMap.addCircle(new CircleOptions().center(post.getLatlng()).radius(50));
                            searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                            searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                            mapaCirculos.put(post.getId(),searchCircle);
                        }
                        getNearstLocationMarker();
                    }
                }
                @Override
                public void onMarkerDrag(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.i("System out", "onMarkerDrag...");
                }
            });
        }
        else {
            rotasId = new ArrayList<>();
            mapaParagens = new HashMap<>();
            LibraryClass.getFirebase().child("Rotas").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Rota rota = postSnapshot.getValue(Rota.class);
                        String rotaIds=rota.getId();
                        LibraryClass.getFirebase().child("Paragens").child(rotaIds).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    Ponto post = postSnapshot.getValue(Ponto.class);
                                    listaParagens.add(post);
                                    LatLng coord = new LatLng(post.getLatitude(), post.getLongitude());
                                    pontoParagem = mMap.addMarker(new MarkerOptions().position(coord).title(post.getNome()).draggable(true)
                                            .snippet(post.getDescricao())
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.busstop2)));
                                    mapaParagens.put(post.getId(),pontoParagem);
                                    searchCircle = mMap.addCircle(new CircleOptions().center(post.getLatlng()).radius(50));
                                    searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                                    searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
                                    mapaCirculos.put(post.getId(),searchCircle);
                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, INITIAL_ZOOM_LEVEL));
                                fetchAutocarros();
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                                snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                    snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                }
            });



        }

    }

    public void fetchUserLocation() {
        LibraryClass.getFirebase().child("Localizacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                LocationU post = null;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    post = postSnapshot.getValue(LocationU.class);
                    System.out.println("POST READ>"+post.getIdDispositivo());
                    if(post.getIdDispositivo().equals(getIMEI()) && post.getIdUtilizador().equals(ut.getId()))
                    {
                        locationU.setLatitude(post.getLatitude());
                        locationU.setLongitude(post.getLongitude());
                        locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                                getApplicationContext(), new GeocoderHandler());
                        user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                                .snippet("Minha Localizacao Actual: "+endereco)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                    }
                }
                if(user ==null)
                {
                    locationU.setLatitude(defaultLocation.latitude);
                    locationU.setLongitude(defaultLocation.longitude);
                    locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());
                    LibraryClass.getFirebase().child("Localizacoes").child(getIMEI()+ut.getId()).setValue(locationU);
                    user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                            .snippet("Minha Localizacao Actual: "+endereco)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                snackbar.show();
            }
        });
    }

    public void fetchUserLocationAndNearestPlace() {
        LibraryClass.getFirebase().child("Localizacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                LocationU post = null;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    post = postSnapshot.getValue(LocationU.class);
                    System.out.println("POST READ>"+post.getIdDispositivo());
                    if(post.getIdDispositivo().equals(getIMEI()) && post.getIdUtilizador().equals(ut.getId()))
                    {
                        locationU.setLatitude(post.getLatitude());
                        locationU.setLongitude(post.getLongitude());
                        locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                                getApplicationContext(), new GeocoderHandler());
                        user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                                .snippet("Minha Localizacao Actual: "+endereco)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationU.getLatLang(), INITIAL_ZOOM_LEVEL));
                        getNearstLocationMarker();
                    }
                }
                if(user ==null)
                {
                    locationU.setLatitude(defaultLocation.latitude);
                    locationU.setLongitude(defaultLocation.longitude);
                    locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());
                    LibraryClass.getFirebase().child("Localizacoes").child(getIMEI()+ut.getId()).setValue(locationU);
                    user = mMap.addMarker(new MarkerOptions().position(locationU.getLatLang()).title(nomeUtilizador).draggable(true)
                            .snippet("Minha Localizacao Actual: "+endereco)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person2)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationU.getLatLang(), INITIAL_ZOOM_LEVEL));
                    getNearstLocationMarker();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                snackbar.show();
            }
        });
    }
    private void fetchAutocarros() {
        autocarros = new ArrayList<>();
        mapaAutocarros = new HashMap<>();
        cont=0;
        LibraryClass.getFirebase().child("Autocarros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if(postSnapshot.getValue(Autocarro.class) != null){
                        Autocarro post = postSnapshot.getValue(Autocarro.class);
                        autocarros.add(post);
                        cont +=1;
                        if(!listaParagens.isEmpty()) {
                            if(cont ==1) {
                                bus = mMap.addMarker(new MarkerOptions()
                                        .position(at1).draggable(true)
                                        .title(post.getId())
                                        .snippet(post.getNome())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus4))
                                );
                                mapaAutocarros.put(post.getId(), bus);
                                if (selectedItinerario != null) {
                                    bus.showInfoWindow();
                                    bus.setVisible(false);
                                }
                            }
                            else if(cont == 2)
                            {
                                bus = mMap.addMarker(new MarkerOptions()
                                        .position(at2).draggable(true)
                                        .title(post.getId())
                                        .snippet(post.getNome())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus4))
                                );
                                mapaAutocarros.put(post.getId(), bus);
                                if (selectedItinerario != null) {
                                    bus.showInfoWindow();
                                    bus.setVisible(false);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                snackbar=TSnackbar .make(progressBar,"Ocorreu um erro ao buscar a informação: " + firebaseError.getMessage(),TSnackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                snackbar.show();
            }
        });
        viagens = new ArrayList<>();
        LibraryClass.getFirebase().child("Viagens").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Viagem.class) != null) {
                    Viagem post = dataSnapshot.getValue(Viagem.class);
                    viagens.add(post);
                    if (mapaParagens.containsKey(post.getCod_anteriorParagem()) && mapaParagens.containsKey(post.getCod_proximaParagem())) {
                        if (post != null && mapaParagens.get(post.getCod_proximaParagem()) != null && mapaAutocarros.get(post.getCod_autocarro()) != null) {
                            listaNotificacoes = (ListView) findViewById(R.id.listaNotificacoes);
                            LatLng newPosition = new LatLng(post.getLatitude(), post.getLongitude());
                            mapaAutocarros.get(post.getCod_autocarro()).setPosition(newPosition);
                            mapaAutocarros.get(post.getCod_autocarro()).setTitle("Informação actualizada em tempo real");
                            mapaAutocarros.get(post.getCod_autocarro()).setSnippet(post.toString());
                            mapaAutocarros.get(post.getCod_autocarro()).setVisible(true);
                            // mapaAutocarros.get(post.getCod_autocarro()).showInfoWindow();
                            progressBar = (ProgressBar) findViewById(R.id.maps_progress);
                            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, INITIAL_ZOOM_LEVEL));
                            if (chegou == false && notificado == false) {
                                copyList = listaNot;
                                listaNot = new ArrayList<String>();
                                Date finaldate = new Date();
                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                final String datenc = DATE_FORMAT.format(finaldate);
                                String not= "Data: "+ datenc +"\n"
                                        +"Mensagem:"+"\nO " + post.getDescricao() + " está a deslocar-se em direção a " + post.getProximaParagem();
                                listaNot.add(not);
                                listaNot.addAll(copyList);
                                badge_corner .setText(listaNot.size()+"");
                                if (listaNotificacoes != null) {
                                    System.out.println("ENTROU NO SET");
                                    listaNotificacoes.setAdapter(new CustomArrayAdapterNot(MapsActivity.this, android.R.layout.simple_list_item_1, listaNot));
                                }
                                notificado = true;
                                notif = false;
                                mapaAutocarros.get(post.getCod_autocarro()).showInfoWindow();

                            }
                            else if (post.getChegouDestino() == true && chegou == false && notif == false) {
                                System.out.println("PROXIMA PARAGEM>" + post.getCod_proximaParagem());
                                System.out.println(mapaParagens.get(post.getCod_proximaParagem()));
                                mapaParagens.get(post.getCod_proximaParagem()).setSnippet("O " + post.getDescricao() + " acaba de chegar a " + post.getProximaParagem());
                                copyList = listaNot;
                                listaNot = new ArrayList<String>();
                                Date finaldate = new Date();
                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                final String datenc = DATE_FORMAT.format(finaldate);
                                String not= "Data: "+ datenc +"\n"
                                        +"Mensagem:"+"\nO " + post.getDescricao() + " acaba de chegar a " + post.getProximaParagem();
                                listaNot.add(not);
                                listaNot.addAll(copyList);
                                badge_corner .setText(listaNot.size()+"");
                                if (listaNotificacoes != null) {
                                    System.out.println("ENTROU NO SET");
                                    listaNotificacoes.setAdapter(new CustomArrayAdapterNot(MapsActivity.this, android.R.layout.simple_list_item_1, listaNot));
                                }
                                mapaAutocarros.get(post.getCod_autocarro()).hideInfoWindow();
                                mapaAutocarros.get(post.getCod_autocarro()).setSnippet("O" + post.getDescricao() + " chegou ao destino " + post.getProximaParagem());
                                mapaParagens.get(post.getCod_proximaParagem()).showInfoWindow();
                                chegou = true;
                            }
                            else if (chegou == true && post.getChegouDestino() == false ) {
                                chegou = false;
                                notificado = false;
                                copyList = listaNot;
                                listaNot = new ArrayList<String>();
                                Date finaldate = new Date();
                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                final String datenc = DATE_FORMAT.format(finaldate);
                                String not= "Data: "+ datenc +"\n"
                                        +"Mensagem:"+"\nO " + post.getDescricao() + " acaba de sair da paragem " + post.getAnteriorParagem();
                                listaNot.add(not);
                                listaNot.addAll(copyList);
                                badge_corner .setText(listaNot.size()+"");
                                if (listaNotificacoes != null) {
                                    System.out.println("ENTROU NO SET");
                                    listaNotificacoes.setAdapter(new CustomArrayAdapterNot(MapsActivity.this, android.R.layout.simple_list_item_1, listaNot));
                                }
                            }

                        } else {
                            LatLng newPosition = new LatLng(post.getLatitude(), post.getLongitude());
                            mapaAutocarros.get(post.getCod_autocarro()).setPosition(newPosition);
                            mapaAutocarros.get(post.getCod_autocarro()).setTitle("Informação actualizada em tempo real");
                            mapaAutocarros.get(post.getCod_autocarro()).setSnippet(post.toString());

                            if (chegou == true) {
                                mapaAutocarros.get(post.getCod_autocarro()).hideInfoWindow();
                                mapaAutocarros.get(post.getCod_autocarro()).setSnippet(post.getDescricao() + " - Proxima Paragem: " + post.getProximaParagem());
                                copyList = listaNot;
                                listaNot = new ArrayList<String>();
                                Date finaldate = new Date();
                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                final String datenc = DATE_FORMAT.format(finaldate);
                                String not= "Data: "+ datenc +"\n"
                                        +"Mensagem:"+"\nO " + post.getDescricao() + " passou da " + post.getAnteriorParagem() + " em direção a " + post.getProximaParagem();
                                listaNot.add(not);
                                listaNot.addAll(copyList);
                                badge_corner .setText(listaNot.size()+"");
                                if (listaNotificacoes != null) {
                                    System.out.println("ENTROU NO SET");
                                    listaNotificacoes.setAdapter(new CustomArrayAdapterNot(MapsActivity.this, android.R.layout.simple_list_item_1, listaNot));
                                }
                                chegou = false;
                                notificado = false;
                            }
                            if (post.getChegouDestino() == true && chegou == false) {
                                mapaAutocarros.get(post.getCod_autocarro()).hideInfoWindow();
                                mapaAutocarros.get(post.getCod_autocarro()).setSnippet("O" + post.getDescricao() + " chegou ao destino " + post.getProximaParagem());
                                copyList = listaNot;
                                listaNot = new ArrayList<String>();
                                Date finaldate = new Date();
                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                final String datenc = DATE_FORMAT.format(finaldate);
                                String not= "Data: "+ datenc +"\n"
                                        +"Mensagem:"+"\nO" + post.getDescricao() + " chegou ao destino " + post.getProximaParagem();
                                listaNot.add(not);
                                listaNot.addAll(copyList);
                                badge_corner .setText(listaNot.size()+"");
                                if (listaNotificacoes != null) {
                                    System.out.println("ENTROU NO SET");
                                    listaNotificacoes.setAdapter(new CustomArrayAdapterNot(MapsActivity.this, android.R.layout.simple_list_item_1, listaNot));
                                }
                                chegou = true;
                            }
                        }
                    }else
                    {   if(mapaParagens.get(post.getCod_anteriorParagem()) != null){
                        mapaParagens.get(post.getCod_anteriorParagem()).hideInfoWindow();}
                        //mapaAutocarros.get(post.getCod_autocarro()).remove();
                    }
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Viagem.class) != null) {
                    Viagem post = dataSnapshot.getValue(Viagem.class);
                    mapaAutocarros.get(post.getCod_autocarro()).setVisible(false);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getNearstLocationMarker() {
        System.out.println("ENTROU NEAREST LOCATION");
        List<Ponto> m = listaParagens;
        double menor =0;
        int pos = 0;
        for (int i = 0; i < m.size(); i++) {
            //Double distancia = distanceFrom(currentlat.doubleValue(),currentlong.doubleValue(),m.get(i).getLatitude(), m.get(i).getLongitude());
            LatLng destino = new LatLng(m.get(i).getLatitude(), m.get(i).getLongitude());
            Double distancia = CalculationByDistance(locationU.getLatLang(),destino);
            if (i == 0) menor = distancia;
            else if (distancia < menor ) {
                menor = distancia;
                pos = i;
            }
        }
        pontoMaixProximo = listaParagens.get(pos);
        points = new ArrayList<>(2);
        destino = new LatLng(pontoMaixProximo.getLatitude(), pontoMaixProximo.getLongitude());
        points.add(locationU.getLatLang());
        locationAddress.getAddressFromLocation(locationU.getLatitude(), locationU.getLongitude(),
                getApplicationContext(), new GeocoderHandler());
        points.add(destino);
        searchCircle = mMap.addCircle(new CircleOptions().center(locationU.getLatLang()).radius(15));
        searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
        searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
        searchCircle2 = mMap.addCircle(new CircleOptions().center(destino).radius(10));
        searchCircle2.setFillColor(Color.argb(66, 255, 0, 255));
        searchCircle2.setStrokeColor(Color.argb(66, 0, 0, 0));
        makeRoute = new MakeRoute();
        String lang = "pt-PT";
        makeRoute.drawRoute(mMap, MapsActivity.this, points, true, lang, true, Color.GREEN,"walking",true,true);
        progressBar = (ProgressBar) findViewById(R.id.maps_progress);

        PolylineOptions rectOptions = new PolylineOptions()
                .add(locationU.getLatLang())
                .add(destino)
                .width(8)
                .color(Color.RED)
                .clickable(true);
        //   Polyline polyline = mMap.addPolyline(rectOptions);
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);
        return Radius * c;
    }
    public void createViatura()
    {
        databaseReference = LibraryClass.getFirebase().child("Autocarros").child("at2");
        Autocarro at = new Autocarro();
        at.setId("AT2");
        at.setNome("Autocarro 2 Baixa - Zimpeto");
        at.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at);


        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at2");
        Autocarro at2 = new Autocarro();
        at2.setId("AT2");
        at2.setNome("Autocarro 2 Baixa - Zimpeto");
        at2.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at2);

        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at3");
        Autocarro at3 = new Autocarro();
        at3.setId("AT3");
        at3.setNome("Autocarro 3 Baixa - Zimpeto");
        at3.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at3);

        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at4");
        Autocarro at4 = new Autocarro();
        at4.setId("AT4");
        at4.setNome("Autocarro 4 Baixa - Zimpeto");
        at4.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at4);

        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at5");
        Autocarro at5 = new Autocarro();
        at5.setId("AT5");
        at5.setNome("Autocarro 5 Baixa - Zimpeto");
        at5.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at5);

        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at6");
        Autocarro at6 = new Autocarro();
        at2.setId("AT6");
        at3.setNome("Autocarro 6 Baixa - Zimpeto");
        at4.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at6);

        databaseReference = LibraryClass.getFirebase().child("AutocarrosSimulado").child("at2");
        Autocarro at1 = new Autocarro();
        at1.setId("AT2");
        at1.setNome("Autocarro 1 Baixa - Zimpeto");
        at1.setDetalhes("Clique no Autocarro para começar a simulação");
        databaseReference.setValue(at1);

    }
    public void createPontos()
    {
        databaseReference = LibraryClass.getFirebase().child("Paragens").child("rota2");
        Ponto it1 = new Ponto();
        it1.setId("rota2pt01");
        it1.setNome("Terminal Zimpeto");
        it1.setDescricao("Paragem Terminal Zimpeto");
        it1.setLatitude(-25.830474);
        it1.setLongitude(32.574576);
        databaseReference.child("rota2pt01").setValue(it1);
        // --------------------------------------
        Ponto it2 = new Ponto();
        it2.setId("rota2pt02");
        it2.setNome("Mercado Zimpeto");
        it2.setDescricao("Paragem Mercado Zimpeto");
        it2.setLatitude(-25.830701);
        it2.setLongitude(32.569338);
        databaseReference.child("rota2pt02").setValue(it2);
        // --------------------------------------
        Ponto it3 = new Ponto();
        it3.setId("rota2pt03");
        it3.setNome("Mabor");
        it3.setDescricao("Paragem Mabor");
        it3.setLatitude(-25.836988);
        it3.setLongitude(32.568930);
        databaseReference.child("rota2pt03").setValue(it3);
        // --------------------------------------
        Ponto it4 = new Ponto();
        it4.setId("rota2pt04");
        it4.setNome("Benfica");
        it4.setDescricao("Paragem Benfica");
        it4.setLatitude(-25.884929);
        it4.setLongitude(32.566494);
        databaseReference.child("rota2pt04").setValue(it4);
        // --------------------------------------

        // --------------------------------------
        Ponto it6 = new Ponto();
        it6.setId("rota2pt06");
        it6.setNome("Bagamoyo");
        it6.setDescricao("Paragem Bagamoyo");
        it6.setLatitude(-25.894079);
        it6.setLongitude(32.563812);
        databaseReference.child("rota2pt06").setValue(it6);
        // --------------------------------------
        Ponto it7 = new Ponto();
        it7.setId("rota2pt07");
        it7.setNome("Bairo 25 de Junho");
        it7.setDescricao("Paragem Bairo 25 de Junho");
        it7.setLatitude(-25.909929);
        it7.setLongitude(32.565597);
        databaseReference.child("rota2pt07").setValue(it7);
        // --------------------------------------
        Ponto it8 = new Ponto();
        it8.setId("rota2pt08");
        it8.setNome("Bombas Jardim");
        it8.setDescricao("Paragem Bombas Jardim");
        it8.setLatitude(-25.891256);
        it8.setLongitude(32.564295);
        databaseReference.child("rota2pt08").setValue(it8);
        // --------------------------------------
        Ponto it9 = new Ponto();
        it9.setId("rota2pt09");
        it9.setNome("Faculdade de Engenharias UEM");
        it9.setDescricao("Paragem Faculdade de Engenharias UEM");
        it9.setLatitude(-25.935726);
        it9.setLongitude(32.549435);
        databaseReference.child("rota2pt09").setValue(it9);
        // --------------------------------------
        Ponto it10 = new Ponto();
        it10.setId("rota2pt10");
        it10.setNome("Brigada Montada");
        it10.setDescricao("Paragem Brigada Montada");
        it10.setLatitude(-25.943744);
        it10.setLongitude(32.541699);
        databaseReference.child("rota2pt10").setValue(it10);
        // --------------------------------------
        Ponto it11 = new Ponto();
        it11.setId("rota2pt11");
        it11.setNome("Praça dos Trabalhadores");
        it11.setDescricao("Paragem Praça dos Trabalhadores");
        it11.setLatitude(-25.971280);
        it11.setLongitude(32.564965);
        databaseReference.child("rota2pt11").setValue(it11);
        // --------------------------------------
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( keyCode== KeyEvent.KEYCODE_BACK)
        {
            mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            if(mLayout.getPanelState() ==SlidingUpPanelLayout.PanelState.EXPANDED)
            {

                mLayout.requestLayout(); // call requestLayout before expandPanel
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return true;

            }
            else if(mLayout.getPanelState() ==SlidingUpPanelLayout.PanelState.COLLAPSED ) {
                this.finish();
            }else
            {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        System.out.println("VEM DO MENU "+vemdoMenuPrincipal);
        if(user !=null && marker.getId().equals(user.getId()))
        {
            marker.showInfoWindow();
        }
        else
        {
            marker.showInfoWindow();
        }

        return false;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

        float angle = slideOffset * 180;
        sliderImage.setRotation(angle);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    public void callGoogleMaps(View view)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+locationU.getLatitude()+","+locationU.getLongitude()+"&daddr="+destino.latitude+","+destino.longitude));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            endereco = locationAddress;
            if(user !=null)
            {
                user.setSnippet("Minha localização actual: "+endereco);
            }
        }
    }

}
