package stevenyoon.housemates;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PaymentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        Toast.makeText(getApplicationContext(), "whoo!", Toast.LENGTH_SHORT);

        new PostRequestAsyncTask().execute();


    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            HttpGet request = new HttpGet("https://secure.splitwise.com/api/v3.0/get_groups");
            //consumer.sign(request);

            System.out.println("Sending  request to Spltiwise...");

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                response = httpClient.execute(request);

                String msg = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                        + response.getStatusLine().getReasonPhrase());
                System.out.println(msg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean status){
            Log.v("Debugging", "worked!");
        }

    }
}
