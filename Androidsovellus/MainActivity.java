package t.newdatabaseapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

//Kyseiseen ohjelmaan tarvitaan PHP-skripti, johon ohjelma yhdistää ja josta ohjelma hakee tietoja

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    private TextView textViewLampotila;
    private TextView textViewKellonaika;
    private Button buttonGet;

    public static final String MY_JSON = "MY_JSON";
    private static final String JSON_URL = "http://169.254.173.15/connect2.php";
    private static final String JSON_URL2 = "http://169.254.173.15/connect3.php";

     public boolean haeLampotila = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLampotila = (TextView) findViewById(R.id.textPrintLampotila);
        textViewKellonaika = (TextView) findViewById(R.id.textPrintKellonaika);
        textViewLampotila.setMovementMethod(new ScrollingMovementMethod());
        buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonGet.setOnClickListener(this);
    }


    private void showParseActivity() {
        Intent intent = new Intent(this, ParseJSON.class);
        intent.putExtra(MY_JSON,textViewLampotila.getText().toString());
        startActivity(intent);
    }

    private void getJSON(String url) {
        class GetJSON extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Odota hetki...",null,true,true);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");

                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                String lampotilaTeksti = textViewLampotila.getText().toString();
                int lampotila = Integer.parseInt(lampotilaTeksti);

                if(haeLampotila == true){
                    textViewLampotila.setText(s);
                    haeLampotila = false;
                    //lampotila = 00;
                }
                else if (haeLampotila == false){
                    textViewKellonaika.setText(s);
                    haeLampotila = true;
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);


        Log.d("getJSON", MY_JSON);


    }



    @Override
    public void onClick(View v) {
        if(v==buttonGet){
            //haeLampotila = true;
            getJSON(JSON_URL);
            getJSON(JSON_URL2);
        }



        /*
        if(v==buttonParse){
            showParseActivity();
        }
        */
    }


    public void onButtonGoParseClicked(View view){
            Log.d("MY_JSON", MY_JSON);

    }

}
