package com.example.g6.jumpclient.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g6.jumpclient.Add.AddLocale;
import com.example.g6.jumpclient.Add.AddRestaurant;
import com.example.g6.jumpclient.Class.Locale;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.MapsActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class LocaleList extends ToolBarActivity {

    private static final Integer TAG_CODE_PERMISSION_LOCATION = 1;
    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button addLocaleButton;
    private static Location myLocation;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locale_list);
        mItemList = (RecyclerView) findViewById(R.id.localeList);
        mItemList.setHasFixedSize(false);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addLocaleButton = (Button) findViewById(R.id.addLocaleButton);
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(LocaleList.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }

        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            myLocation = location;
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }

        FirebaseRecyclerAdapter<Locale, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter<Locale, ItemViewHolder>(
                Locale.class,
                R.layout.single_locale_item,
                ItemViewHolder.class,
                mDatabase.child("locales")
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Locale model, final int position) {
                if (model.getStatus() == null || model.getStatus() == Locale.DELETED) {
                    viewHolder.hideLayout();
                }
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setMap(model.getName(),model.getLatitude(),model.getLongitude());

                if (myLocation != null) {
                    Location targetLoc = new Location("");
                    targetLoc.setLatitude(model.getLatitude());
                    targetLoc.setLongitude(model.getLongitude());
                    String distStr;
                    float distanceInMeters = myLocation.distanceTo(targetLoc);
                    if (distanceInMeters > 1000){
                        float distanceInKM = distanceInMeters/1000;
                        distStr = String.format("%.1f", distanceInKM) + "km";
                    }else{
                        distStr = String.format("%.1f", distanceInMeters) + "m";
                    }
                    viewHolder.setDistance(distStr);
                }else{
                    viewHolder.setDistance("");
                }

                final String LocaleKey = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LocaleList.this, RestaurantList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("localeKey", LocaleKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mItemList.setAdapter(FRBA);
    }


    protected static class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String id;
        Button locationButton = itemView.findViewById(R.id.mapButton);

        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(Context ctx, String image) {
            ImageView itemImage = mView.findViewById(R.id.localeImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }

        public void setName(String name) {
            TextView itemName = mView.findViewById(R.id.localeName);
            itemName.setText(name);
        }

        public void setDesc(String desc) {
            TextView itemDesc = mView.findViewById(R.id.localeDesc);
            itemDesc.setText(desc);
        }

        public void setDistance(String distance) {
            TextView itemDist = mView.findViewById(R.id.localeDistance);
            itemDist.setText(distance);
        }
        public void setMap(final String name, final Double latitude, final Double longitude){
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void setId(String id) {
            this.id = id;
        }

        public void hideLayout() {
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }

    }

    public void addLocaleClicked(View view) {
        Intent intent = new Intent(LocaleList.this, AddLocale.class);
        intent.putExtra("type", AddLocale.ADD);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



}

