package com.manav.weather_now;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ImageView weatherImage;
    ConstraintLayout myConstraintLayout;
    TextView infoText;
    String symbol;

    public void processingData(View view) {
        Bitmap myBitmap;
        EditText infoText = (EditText) findViewById( R.id.urlText );
        String city = String.valueOf( infoText.getText() );

        downloadTask task = new downloadTask();
        task.execute( "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=11c92c5e209ebd3b3db78e6644d968bd" );

        imageDownload imageTask = new imageDownload();
        imageTask.execute("http://openweathermap.org/img/wn/"+symbol+"@2x.png");

        InputMethodManager mgr = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow( infoText.getWindowToken(),0 );

        myConstraintLayout.setVisibility( View.VISIBLE );
    }

    public class imageDownload extends AsyncTask<String,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            Bitmap myBitmap;
            try{
                url = new URL( urls[0] );
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream( in  );
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap myBitmap) {
            super.onPostExecute( myBitmap );
            weatherImage.setImageBitmap( myBitmap );
        }
    }

    public class downloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                url = new URL( urls[0] );
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader( in );
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute( result );

            Log.i( "urlpast", result );
            try{
                JSONObject jsonObject = new JSONObject( result );
                String weatherInfo = jsonObject.getString( "weather" );
                String tempInfo = jsonObject.getString( "main" );
                String windinfo = jsonObject.getString( "wind" );
                Log.i("weather",weatherInfo);
                Log.i("temp",tempInfo);
                Log.i("wind",windinfo);
                JSONArray jsonArray = new JSONArray( weatherInfo );
                JSONObject jsonObject1 = jsonArray.getJSONObject( 0 );
                symbol = jsonObject1.getString( "icon" );
                infoText.setText( jsonObject1.getString( "main" )+" : "+jsonObject1.getString( "description" ) );


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        weatherImage = (ImageView)findViewById( R.id.imageView4 );
        myConstraintLayout = (ConstraintLayout)findViewById( R.id.myConstraintLayout );
        infoText = (TextView)findViewById( R.id.infoText );
    }
}
