package stevenyoon.housemates;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//import com.google.android.gms.auth.api.credentials.Credential;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import java.io.IOException;

public class SplitwiseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitwise);

        String authorizationUrl = new GenericUrl("https://secure.splitwise.com/authorize").build();
    }


    public static HttpResponse executeGet(
            HttpTransport transport, JsonFactory jsonFactory, String accessToken, GenericUrl url)
            throws IOException {

        Credential credential =
                new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
        return requestFactory.buildGetRequest(url).execute();
    }

    public static Credential createCredentialWithRefreshToken(
            HttpTransport transport, JsonFactory jsonFactory, TokenResponse tokenResponse) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
                transport)
                .setJsonFactory(jsonFactory)
                .setTokenServerUrl(
                        new GenericUrl("https://server.example.com/token"))
                .setClientAuthentication(new BasicAuthentication("s6BhdRkqt3", "7Fjfp0ZBr1KtDRbnfVdmIw"))
                .build()
                .setFromTokenResponse(tokenResponse);
    }
}
