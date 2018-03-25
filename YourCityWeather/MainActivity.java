package com.example.alexzander.yourcityweathertoday;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    ImageView backgroundImageView;
    EditText cityNameTextView;
    TextView mainWeather;
    TextView weatherTemperature;
    Random rnd;
    final String DEGREE  = "\u00b0";
    String backgroundSite = "https://pxhere.com/en/photos?order=popular&direction=vertical";  //q=drizzle&
    String weatherAPIString = "http://api.openweathermap.org/data/2.5/weather?";
    String weatherAPIKey = "&appid=2cc3b355b75a74cf59d3e2afcf129a9f";                         //&q=Yekaterinburg,ru"
    DownloadHTMLTask downloadHtmlTask;

    public class Weather {
        public final String image;
        public final String temp;
        public final String mainWeather;
        public final String descWeather;

        public Weather(String image, String temp, String mainWeather, String descWeather) {
            this.image = image;
            this.temp = temp;
            this.mainWeather = mainWeather;
            this.descWeather = descWeather;
        }

        public void FromJson(final JSONObject object) {
            final String image = object.optString("");
        }
    }

    public class DownloadHTMLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... sourceUrl) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(sourceUrl[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL celebrityImageUrl = new URL (urls[0]);
                HttpURLConnection connection = (HttpURLConnection) celebrityImageUrl.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap celebrityBitmap = BitmapFactory.decodeStream(in);

                return celebrityBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String findTheBackgroundUrl(String htmlString) {
        ArrayList<String> tempImgHTLM =  new ArrayList<>();;
        Pattern pattern = Pattern.compile("<img alt=\".*?nature.*? src=\"(.*?)\">");
        Matcher matcher = pattern.matcher(htmlString);
        int i=0;
        while (matcher.find()) {
            tempImgHTLM.add(matcher.group(1));
            i++;
        }
        int rndBcgNumb = rnd.nextInt(tempImgHTLM.size());
        return tempImgHTLM.get(rndBcgNumb);
    }

    public void downloadTheBackground(String theBackgroundUrl) {
        ImageDownloader imageDownloaderTask = new ImageDownloader();
        Bitmap theBackgroundImage;

        try {
            theBackgroundImage = imageDownloaderTask.execute(theBackgroundUrl).get();

            backgroundImageView.setImageResource(0);
            backgroundImageView.setImageBitmap(theBackgroundImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCityName(View view){
        String cityName = cityNameTextView.getText().toString();

        Log.i("CityName is: ", cityName);

        try {
            downloadHtmlTask = new DownloadHTMLTask();
            String weatherJsonString = downloadHtmlTask.execute(weatherAPIString + "&q=" + cityName + weatherAPIKey).get();
            String weatherMain = null;
            String weatherDesc = null;
            Double weatherTemp = null;

            JSONObject weatherJSON = new JSONObject(weatherJsonString);

            String weatherMainString = weatherJSON.getString("weather");
            Log.i("Main weather string ", weatherMainString);
            String weatherAddString = weatherJSON.getString("main");
            weatherAddString = "[" + weatherAddString + "]";
            Log.i("Additional weather string ", weatherAddString );

            JSONArray weatherMainArray = new JSONArray(weatherMainString);

            for (int i=0; i<weatherMainArray.length(); i++){
                JSONObject jsonPart = weatherMainArray.getJSONObject(i);
                weatherMain = jsonPart.get("main").toString();
                weatherDesc = jsonPart.get("description").toString();
            }

            JSONArray weatherAddArray = new JSONArray(weatherAddString);
            for (int i=0; i<weatherAddArray.length(); i++){
                JSONObject jsonPart = weatherAddArray.getJSONObject(i);
                weatherTemp = (Double.parseDouble(jsonPart.get("temp").toString()) - 273);
            }

            downloadHtmlTask = new DownloadHTMLTask();
            String weatherBackgroundHTML = downloadHtmlTask.execute(backgroundSite + "&q=" + weatherMain).get();

            String theBackgroundUrl = findTheBackgroundUrl(weatherBackgroundHTML);
            Log.i("the Background URL", theBackgroundUrl);
            downloadTheBackground(theBackgroundUrl);

            mainWeather.setText(weatherMain + ": " + weatherDesc);
            Log.i("Weather temp ", String.valueOf(BigDecimal.valueOf(weatherTemp).setScale(2, RoundingMode.HALF_UP).doubleValue()) + "C"+DEGREE);
            weatherTemperature.setText("temperature: " + String.valueOf(BigDecimal.valueOf(weatherTemp).setScale(2, RoundingMode.HALF_UP).doubleValue()) + "C"+DEGREE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backgroundImageView = findViewById(R.id.backgroundImageView);
        cityNameTextView = (EditText) findViewById(R.id.cityNameTextEdit);
        mainWeather = findViewById(R.id.mainWeatherTextView);
        weatherTemperature = findViewById(R.id.temperatureTextView);
        rnd = new Random();

        String weatherLike = "sunny";
        String htmlPage = "";
        String theBackgroundUrl = "";

        try {
            downloadHtmlTask = new DownloadHTMLTask();
            htmlPage = downloadHtmlTask.execute(backgroundSite + "&q=" + weatherLike).get();

            //Log.i("html page: ", htmlPage);

            theBackgroundUrl = findTheBackgroundUrl(htmlPage);

            Log.i("the Background URL", theBackgroundUrl);

            downloadTheBackground(theBackgroundUrl);

            //downloadHtmlTask.onPostExecute(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
