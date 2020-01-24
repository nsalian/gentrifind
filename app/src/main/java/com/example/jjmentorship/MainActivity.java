package com.example.jjmentorship;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import static com.example.jjmentorship.BuildConfig.QUANDL_KEY;
import static com.example.jjmentorship.BuildConfig.YELP_KEY;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    private double medianListing;
    private int numStarbucks;

    private TextView finalPrice;
    private TextView averagePrice;
    private TextView modifiedPrice;
    private TextView medianListingTextView;

    DecimalFormat df = new DecimalFormat("#.##");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION };

        df.setGroupingUsed(true);
        df.setGroupingSize(3);

        requestPermissions(permissions,100);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button runButton = findViewById(R.id.run);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("button title", v.toString());
                runEverything();
            }
        });

        finalPrice = findViewById(R.id.finalPrice);
        averagePrice = findViewById(R.id.averagePrice);
        modifiedPrice = findViewById(R.id.modifiedPrice);
        medianListingTextView = findViewById(R.id.medianListingText);

    }

    void runEverything (){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            getStarbucks(makeStarbucksURL(location.getLatitude(), location.getLongitude(), 1));
                        }
                    }
                });
    }

    void showPriceToUser() {
        // get calculated price
        double priceToShow = adjustedPrice(medianListing, numStarbucks);

        // insert that into textview

        String formattedPrice = "$" + df.format(priceToShow);
        String formattedmedianPrice = "$" + df.format(medianListing);


        finalPrice.setText(formattedPrice);
        averagePrice.setVisibility(View.VISIBLE);
        modifiedPrice.setVisibility(View.VISIBLE);
        medianListingTextView.setText(formattedmedianPrice);
    }

    String makeStarbucksURL (double lat, double lon, int miles){
        String base = "https://api.yelp.com/v3/businesses/search"+"?"+"term=starbucks";
        String latitude = "latitude="+Double.toString(lat);
        String longitude = "longitude="+Double.toString(lon);
        String fin = base + "&" + latitude + "&" + longitude + "&" + "radius=" + convertToMeters(miles);

        return fin;
    }

    String makeZillowURL (String zip){
        return "https://www.quandl.com/api/v3/datasets/ZILLOW/Z"+ zip + "_MLPAH.json?api_key=" + QUANDL_KEY;
    }

      void getStarbucks(String url){
          RequestQueue queue = Volley.newRequestQueue(this);
          StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                  new Response.Listener<String>() {
                      @Override
                      public void onResponse(String response) {
                          // Display the first 500 characters of the response string.
                          Log.i("YELP", response);
                          try {
                              JSONObject json = new JSONObject(response);
                              numStarbucks = json.getInt("total");

                              if (numStarbucks>0){
                                  Log.i("total", Integer.toString(numStarbucks));

                                  JSONArray businesses = json.getJSONArray("businesses");
                                  JSONObject firstBusiness = businesses.getJSONObject(0);
                                  JSONObject location = firstBusiness.getJSONObject("location");
                                  String zipcode = location.getString("zip_code");
                                  getPrice(makeZillowURL(zipcode));
                              }
                               else {
                                   finalPrice.setText("Your neighborhood is not at risk of gentrification");
                              }

                          } catch (JSONException e) {
                              Log.i("JSONParsing",e.getMessage());
                          }
                      }
                  }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                    Log.i("YELP","something went wrong");
              }
          }

      ) { @Override
      public Map<String, String> getHeaders() throws AuthFailureError
  {
      Map<String, String> params = new HashMap<String, String>();
      params.put("Authorization", "Bearer " + YELP_KEY);
      return params;
      }
    };
      queue.add(stringRequest);
  }

    void getPrice(String url){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("ZILLOW", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject dataset = json.getJSONObject("dataset");
                            JSONArray data = dataset.getJSONArray("data");
                            JSONArray mostRecentRecord = data.getJSONArray(0);
                            medianListing = mostRecentRecord.getDouble(1);

                            Log.i("listingprice", Double.toString(medianListing));

                            showPriceToUser();

                        } catch (JSONException e) {
                            Log.i("JSONParsing",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ZILLOW","something went wrong");
            }
        }

        );
        queue.add(stringRequest);

    }

    int convertToMeters(int miles){
        return (int)(miles*1609.34);
    }

    int convertToMeters(double miles) {
        return (int)(miles * 1609.34);
    }

    static double adjustedPrice(double price, int numberOfStarbucks){
        if (numberOfStarbucks==0) {
            return price;
        }

        return 1.005 * adjustedPrice(price,numberOfStarbucks-1);
    }
}

