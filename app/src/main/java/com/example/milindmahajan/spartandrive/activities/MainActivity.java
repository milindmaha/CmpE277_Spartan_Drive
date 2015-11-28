package com.example.milindmahajan.spartandrive.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.utils.Common;

public class MainActivity extends AppCompatActivity {




    private boolean mLoggedIn, onResume;

    private DropboxAPI<AndroidAuthSession>  mApi;

    private Button dropboxLogin = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        mApi.getSession().startOAuth2Authentication(MainActivity.this);
       /* dropboxLogin = (Button)findViewById(R.id.dropbox_login);
        dropboxLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }

        );*/
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(Common.APP_KEY, Common.APP_SECRET);
        AndroidAuthSession session = null;
        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, Common.ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, Common.ACCESS_TYPE);
        }
        return session;
    }

    protected void onResume() {
        super.onResume();

        if (mApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mApi.getSession().finishAuthentication();

                String accessToken = mApi.getSession().getOAuth2AccessToken();
                System.out.print(accessToken);
                storeKeys("oauth2:", accessToken);
                setLoggedIn(onResume);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
                showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());
            }
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
    public void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            /*UploadFile upload = new UploadFile(Main.this, mApi, DIR, f);
            upload.execute();
            */
            onResume = false;

        }
    }
    private void storeKeys(String key, String secret) {
        SharedPreferences prefs = getSharedPreferences(
                Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Common.ACCESS_KEY_NAME, key);
        edit.putString(Common.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }
    private void logOut() {
        mApi.getSession().unlink();

        clearKeys();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(
                Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(
                Common.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(Common.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Common.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
