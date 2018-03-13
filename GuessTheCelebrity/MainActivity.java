package com.example.alexzander.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String sourceHtmlSite = "http://www.posh24.se/kandisar";
    ArrayList<String> celebritiesImages = new ArrayList<String>();
    ArrayList<String> celebritiesNames = new ArrayList<String>();;
    int celebrities;
    int currentCelebrityIndex;
    int rightButtonNumber;

    ImageView imageCelebrityView;
    Button buttons[] = new Button[4];

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

    public void downloadCelebrities(String htmlString) {
        Pattern pattern = Pattern.compile("src=\"(.*?)\" alt");
        Matcher matcher = pattern.matcher(htmlString);
        while (matcher.find()) {
            celebritiesImages.add(matcher.group(1));
        }

        pattern = Pattern.compile("alt=\"(.*?)\"");
        matcher = pattern.matcher(htmlString);
        while (matcher.find()) {
            celebritiesNames.add(matcher.group(1));
        }

        celebrities = celebritiesImages.size();
    }

    public void fillInTheStuff() {
        currentCelebrityIndex = (int) (Math.random() * 99);
        rightButtonNumber = (int) (Math.random() * 3);

        for (int i=0; i<=3; i++) {
            if (i==rightButtonNumber) {
                buttons[i].setText(celebritiesNames.get(currentCelebrityIndex));
            }
            else {
                buttons[i].setText(celebritiesNames.get((int)(Math.random()*99)));
            }
        }

        ImageDownloader imageDownloaderTask = new ImageDownloader();
        Bitmap currentCelebrityImage;

        try {
            currentCelebrityImage = imageDownloaderTask.execute(celebritiesImages.get(currentCelebrityIndex)).get();

            imageCelebrityView.setImageResource(0);
            imageCelebrityView.setImageBitmap(currentCelebrityImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("current ID and name:", String.valueOf(currentCelebrityIndex) + " " + celebritiesNames.get(currentCelebrityIndex));

    }

    public void buttonTapped (View view) {
        //Button currentButton = (Button) view;
        String currentButtonPressed = view.getTag().toString();
        if (String.valueOf(rightButtonNumber).equals(currentButtonPressed) ) {
            Log.i("!!!!!!!!! Right Button Has Just Pressed !!!!!!!!", String.valueOf(rightButtonNumber));
            Toast.makeText(this, "CORRECT", Toast.LENGTH_LONG).show();
            fillInTheStuff();
        }
        else {
            Log.i("RightButtonNumber is ", String.valueOf(rightButtonNumber));
            Log.i("But you tapped ", currentButtonPressed);
            Toast.makeText(this, "WRONG! It was " + celebritiesNames.get(currentCelebrityIndex), Toast.LENGTH_LONG).show();
            fillInTheStuff();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebrities = 0;
        imageCelebrityView = findViewById(R.id.imageViewForCelebrity);
        buttons[0] = findViewById(R.id.button0);
        buttons[1] = findViewById(R.id.button1);
        buttons[2] = findViewById(R.id.button2);
        buttons[3] = findViewById(R.id.button3);

        // Here we'll try to get an HTML string of the web-page
        DownloadHTMLTask downloadHtmlTask = new DownloadHTMLTask();
        String htmlResult = null;
        try {
            htmlResult = downloadHtmlTask.execute(sourceHtmlSite ).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // now we should chop the HTML-string to slices and get all image urls to one array
        // and all names to an another one
        downloadCelebrities(htmlResult);
        
        // fill buttons with random names and assign the one of them to the index of a celebrity
        // we have chosen. Also we download and set the image of the celebrity.  
        fillInTheStuff();
    }
}
