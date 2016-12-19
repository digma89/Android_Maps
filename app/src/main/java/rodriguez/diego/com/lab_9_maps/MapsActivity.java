package rodriguez.diego.com.lab_9_maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    //private GoogleApiClient mGoogleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;

        //permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            //Toast.makeText(this, “Should ask for the approval/denial here”, Toast.LENGTH_LONG).show();
        }
/*
        Criteria criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        String bestProvider = locationManager.getBestProvider(criteria, true);
*/
    }

    // in emulator this is invoked when you send a new GPS location from DDM or extended controls
    // in real device is invoked when you move to a new location
    public void onLocationChanged(Location location) {
        String locInfo = String.
                format("Current loc = (%f, %f) @ (%f meters up)",
                        location.getLatitude(), location.getLongitude(),
                        location.getAltitude());
        if (lastLocation != null) {
            float distance = location.distanceTo(lastLocation);
            locInfo += String.
                    format("\n Distance from last = %f meters", distance);
        }
        lastLocation = location;
        System.out.print(locInfo);
        Toast.makeText(this, locInfo, Toast.LENGTH_LONG).show();

        // update the map
        LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newLoc).title("Marker new location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));

        // Display geo information about the new location
        Geocoder coder = new Geocoder(this);
        try {
            Iterator<Address> addresses = coder
                    .getFromLocation(location.getLatitude(),
                            location.getLongitude(), 3).iterator();
            if (addresses != null) {
                while (addresses.hasNext()) {
                    Address namedLoc = addresses.next();
                    String placeName = namedLoc.getLocality();
                    String featureName = namedLoc.getFeatureName();
                    String country = namedLoc.getCountryName();
                    String road = namedLoc.getThoroughfare();
                    locInfo += String.format("\n[%s][%s][%s][%s]",
                            placeName, featureName, road, country);
                    int addIdx = namedLoc.getMaxAddressLineIndex();
                    while (addIdx >= 0) {
                        String addLine = namedLoc.getAddressLine(addIdx);
                        locInfo += String.
                                format("\nLine %d: %s", addIdx, addLine);
                        addIdx--;
                    }
                    System.out.println("GEO - LOCINFO " + locInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProviderDisabled (String provider) {

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng toronto = new LatLng(43, -79);
        mMap.addMarker(new MarkerOptions().position(toronto).title("Marker in Toronto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));



    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this,
                "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onStatusChanged (String provider,
                                 int status,
                                 Bundle extras) {

    }
}
