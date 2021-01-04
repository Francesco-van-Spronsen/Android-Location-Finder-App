package com.android2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android2.auth.AccountManager;
import com.android2.ui.compass.CompassFragment;
import com.android2.ui.people.PeopleFragment;
import com.android2.ui.people.Person;
import com.android2.ui.profile.ProfileFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity"; //Used for logging
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final LatLng DEFAULT_LOCATION = new LatLng(51.5079, -0.0877);

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mMyLocation;
    private BottomNavigationView mBottomNavigationView;
    private GoogleMap mGoogleMap;
    private UiSettings mMapUiSettings;

    private Map<Integer, Runnable> methods;
    {
        methods = new HashMap<>();
        methods.put(R.id.navigation_map, () -> loadMapFragment());
        methods.put(R.id.navigation_people, () -> loadPeopleFragment());
        methods.put(R.id.navigation_compass, () -> loadCompassFragment());
        methods.put(R.id.navigation_profile, () -> loadProfileFragment());
    }

    private List<Marker> markerList = new ArrayList<>();
    private List<Person> peopleList = new ArrayList<>();
    private Person person = new Person();
    private FirebaseUser currentUser;

    public List<Person> getPeopleList() { return peopleList; }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            methods.get(item.getItemId()).run();
            return true;
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        currentUser = AccountManager.getInstance(this).getCurrentUser();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location lastLocation = locationResult.getLastLocation();

                if (mMyLocation != null &&
                        mMyLocation.getLongitude() == lastLocation.getLongitude() &&
                        mMyLocation.getLatitude() == lastLocation.getLatitude()) {
                    return;
                }

                mMyLocation = lastLocation;

                if (mGoogleMap != null && hasLocationPermission()) {

                    person = new Person(
                            currentUser.getEmail(),
                            currentUser.getPhotoUrl() == null ? null : currentUser.getPhotoUrl().toString(),
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude(),
                            true);
                    createOrUpdatePerson(person);

                    LatLng latLng = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
                    moveCamera(latLng);
                }
            }
        };

        // default
        loadMapFragment();
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready and loading");

        if (!hasLocationPermission()) {
            return;
        }

        mGoogleMap = googleMap;
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        mMapUiSettings = mGoogleMap.getUiSettings();
        mMapUiSettings.setZoomControlsEnabled(true);
        mMapUiSettings.setMyLocationButtonEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);

        if (mMyLocation != null) {
            LatLng latLng = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            moveCamera(latLng);
        }

        if (person != null)
            getLoggedInPeople();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Log.d(TAG, "onRequestPermissionsResult: Called");

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    mBottomNavigationView.setSelectedItemId(R.id.navigation_people);
                    //Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                    return;
                }
            }

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            loadMapFragment();
            //Log.d(TAG, "onRequestPermissionsResult: Permission granted");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        person.setLoggedIn(false);
        createOrUpdatePerson(person);

        AccountManager.getInstance(this).signOut();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void loadMapFragment() {
        if (isGooglePlayServicesAvailable() && hasLocationPermission()) {
            setTitle(R.string.title_map);
            SupportMapFragment mapFragment = new SupportMapFragment();
            mapFragment.getMapAsync(this);
            loadFragment(mapFragment);

            return;
        }

        requestLocationPermission();
    }

    public void loadPeopleFragment()
    {
        setTitle(R.string.title_people);
        loadFragment(new PeopleFragment());
    }

    public void loadCompassFragment()
    {
        setTitle(R.string.title_compass);
        loadFragment(new CompassFragment());
    }

    public void loadProfileFragment()
    {
        setTitle(R.string.title_profile);
        loadFragment(new ProfileFragment());
    }

    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean isGooglePlayServicesAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //Log.d(TAG, "isServicesOK: checking google services version");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //Log.d(TAG, "isServicesOK: an error occurred but we can resolve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "We can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void getLoggedInPeople() {
        DatabaseReference mPeopleRef = FirebaseDatabase.getInstance().getReference().child("people");
        mPeopleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Marker m : markerList)
                    m.remove();

                markerList.clear();
                peopleList.clear();
                for (DataSnapshot personSnapshot : dataSnapshot.getChildren()) {
                    Person p = personSnapshot.getValue(Person.class);

                    if (p.isLoggedIn()) {
                        peopleList.add(p);

                        if (mGoogleMap != null && hasLocationPermission()) {
                            LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                            markerList.add(mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(p.getName())));

                            if (person.getName() == p.getName() && person.getLatitude() != p.getLatitude() && person.getLongitude() != p.getLongitude())
                                moveCamera(latLng);
                        }
                    }
                }

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof PeopleFragment) {
                    ((PeopleFragment) currentFragment).getPeopleAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasLocationPermission() {
        return hasPermission(FINE_LOCATION) && hasPermission(COARSE_LOCATION);
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        String[] permissions = { FINE_LOCATION, COARSE_LOCATION };
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void moveCamera(LatLng latLng) {
        //Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mGoogleMap.animateCamera(location);
    }

    private void createOrUpdatePerson(Person person)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("people").child(currentUser.getUid()).setValue(person);
    }

    private void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
