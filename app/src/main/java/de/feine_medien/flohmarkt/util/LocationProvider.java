package de.feine_medien.flohmarkt.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import de.feine_medien.flohmarkt.event.OnGeoLocationFoundEvent;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Looper.getMainLooper;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class LocationProvider extends LocationCallback {

    private static final int LOCATION_DISPLACEMENT = 50;
    private static final int LOCATION_INTERVAL = 3 * 60 * 1000;
    private static final int LOCATION_TOO_OLD_DELTA = 1000 * 60 * 2;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation = null;
    private Activity activity;

    public LocationProvider(final Activity activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
    }

    @Nullable
    public Location getCurrentLocation() {
        return currentLocation;
    }

    public boolean isPermissionGiven() {
        return checkSelfPermission(activity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED || checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    public void askForPermission(final Activity activity, final int requestCode) {
        requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, requestCode);
    }

    @Override
    public void onLocationResult(final LocationResult locationResult) {
        final Location location = locationResult.getLastLocation();

        if (currentLocation == null) {
            currentLocation = location;

        } else {
            final long timeDelta = location.getTime() - currentLocation.getTime();
            final boolean isSignificantlyNewer = timeDelta > LOCATION_TOO_OLD_DELTA;
            final int accuracyDelta = (int) (location.getAccuracy() - currentLocation.getAccuracy());
            if (isSignificantlyNewer || accuracyDelta < 0 || timeDelta > 0 && accuracyDelta <= 0) {
                currentLocation = location;
            }
        }

        EventBus.getDefault().postSticky(new OnGeoLocationFoundEvent(currentLocation));
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        final LocationRequest locationRequest = LocationRequest.create()
                .setInterval(LOCATION_INTERVAL)
                .setFastestInterval(0)
                .setSmallestDisplacement(LOCATION_DISPLACEMENT)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (isPermissionGiven()) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, getMainLooper());
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }
}

