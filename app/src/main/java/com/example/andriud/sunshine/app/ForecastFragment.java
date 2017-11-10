package com.example.andriud.sunshine.app;

/**
 * Created by M0J0 on 11/8/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    // Ensure that an options menu is created
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)); //getString (type, string)
        weatherTask.execute(location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            //FetchWeatherTask weatherTask = new FetchWeatherTask();
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); // Loading up our shared preferences
            //String location = prefs.getString(getString(R.string.pref_location_key),        // and using them to extract our prefered location key
             //       getString(R.string.pref_location_default));
            //weatherTask.execute(location);
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override  // Create/ Initialize UI the View
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        // Dummy data for ListView.
        String[] data = {
                "Mon 11/6 - Rainy -  31/17",
                "Tues 11/6 - Rainy -  31/17",
                "Wed 11/6 - Rainy -  31/17",
                "Thur 11/6 - Rainy -  31/17",
                "Fri 11/6 - Rainy -  31/17",
                "Sat 11/6 - Rainy -  31/17",
                "Sun 11/6 - Rainy -  31/17"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now create an Array Adapter to take data from a source and populated a ListView
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Grab reference to ListView and attach adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        // Add a touch/click listener to watch for touch events on our items.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // Extends AsyncTask so that it can be done off main thread
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(long time){
            // the API runs a unix timestamp (measured in seconds)
            // It must be converted to milliseconds
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        // Format the highs/lows
        private String formatHighLows(double high, double low){
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        /**Take in Weather JSON string, pull out needed data
         *
         * @param forecastJsonStr
         * @param numDays
         * @return
         * @throws JSONException
         *
         * Example Webaddress
         * http://api.openweathermap.org/data/2.5/forecast?q=94043&mode=json&units=metric&cnt=7&APPID=5608746ddb3ba819e551b07cb12c82fa
         * Example JSON Data
         * {"cod":"200","message":0.3296,"cnt":7,"list":[{"dt":1510272000,"main":{"temp":17.25,"temp_min":15.25,"temp_max":17.25,"pressure":992.41,"sea_level":1030.84,"grnd_level":992.41,"humidity":80,"temp_kf":2},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02n"}],"clouds":{"all":24},"wind":{"speed":1.11,"deg":235.502},"rain":{},"sys":{"pod":"n"},"dt_txt":"2017-11-10 00:00:00"},{"dt":1510282800,"main":{"temp":10.42,"temp_min":8.92,"temp_max":10.42,"pressure":993,"sea_level":1031.64,"grnd_level":993,"humidity":95,"temp_kf":1.5},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":36},"wind":{"speed":1.02,"deg":225.001},"rain":{},"sys":{"pod":"n"},"dt_txt":"2017-11-10 03:00:00"},{"dt":1510293600,"main":{"temp":7.03,"temp_min":6.02,"temp_max":7.03,"pressure":993.75,"sea_level":1032.33,"grnd_level":993.75,"humidity":93,"temp_kf":1},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":44},"wind":{"speed":0.96,"deg":174.5},"rain":{},"sys":{"pod":"n"},"dt_txt":"2017-11-10 06:00:00"},{"dt":1510304400,"main":{"temp":5.8,"temp_min":5.3,"temp_max":5.8,"pressure":993.27,"sea_level":1032.1,"grnd_level":993.27,"humidity":94,"temp_kf":0.5},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":56},"wind":{"speed":1.02,"deg":200.002},"rain":{},"sys":{"pod":"n"},"dt_txt":"2017-11-10 09:00:00"},{"dt":1510315200,"main":{"temp":6.78,"temp_min":6.78,"temp_max":6.78,"pressure":992.94,"sea_level":1031.72,"grnd_level":992.94,"humidity":96,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":68},"wind":{"speed":0.71,"deg":153},"rain":{"3h":0.025},"sys":{"pod":"n"},"dt_txt":"2017-11-10 12:00:00"},{"dt":1510326000,"main":{"temp":7.66,"temp_min":7.66,"temp_max":7.66,"pressure":992.74,"sea_level":1031.62,"grnd_level":992.74,"humidity":99,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":64},"wind":{"speed":0.79,"deg":132},"rain":{"3h":0.050000000000001},"sys":{"pod":"d"},"dt_txt":"2017-11-10 15:00:00"},{"dt":1510336800,"main":{"temp":13.46,"temp_min":13.46,"temp_max":13.46,"pressure":993.2,"sea_level":1031.77,"grnd_level":993.2,"humidity":90,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":48},"wind":{"speed":1.16,"deg":243.508},"rain":{"3h":0.09},"sys":{"pod":"d"},"dt_txt":"2017-11-10 18:00:00"}],"city":{"id":5375480,"name":"Mountain View","coord":{"lat":37.3861,"lon":-122.0839},"country":"US"}}
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)

            throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            //final String OWM_TEMPERATURE = "temp";
            final String OWM_TEMPERATURE = "main";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM gives local time of city asked for, so we need to translate
            // Data is sent in order, so we are going to use that

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we use UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++){
                String day;
                String description;
                String highAndLow;

                // Get Json OBJ repping the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // day/time is returned as long, so need to convert it
                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called weather, which is 1 element long
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures in child are called "temp"
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs){
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        // Done in Background so that it does not hamper the main thread
        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }

            // declared outside try/catch so that they can be closed in Finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {


                // Construct URL string
                final String FORECAST_BASE_URL =
                                "http://api.openweathermap.org/data/2.5/forecast?";
                final String QUERY_PARAM = "zip";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
                 URL url = new URL(builtUri.toString());
                 Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Old URL string
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast?q=94043&mode=json&units=metric&cnt=7";
                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseUrl.concat(apiKey));

                // Create request to OWM and open in
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read input steream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // Read through the JSON
                String line;
                while ((line = reader.readLine()) != null) {
                    // For readability purposes
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // no data gotten
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closting stream", e);
                    }
                }
            }


            // Parse pour Json
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result!= null){
                mForecastAdapter.clear();
                // Can perform add all on honeycomb or better
                for(String dayForecastStr : result){
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
