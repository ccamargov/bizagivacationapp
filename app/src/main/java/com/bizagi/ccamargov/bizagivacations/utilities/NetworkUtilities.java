package com.bizagi.ccamargov.bizagivacations.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.bizagi.ccamargov.bizagivacations.LoginActivity;
import com.bizagi.ccamargov.bizagivacations.model.AuthenticateServerState;
import com.bizagi.ccamargov.bizagivacations.model.NetworkServiceError;
import com.bizagi.ccamargov.bizagivacations.model.User;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getSimpleName();
    private static HttpClient mHttpClient;

    private static void prepareApacheHttpConnection() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                    Constants.REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, Constants.REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, Constants.REGISTRATION_TIMEOUT);
        }
    }

    private static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        t.start();
        return t;
    }

    public static AuthenticateServerState authenticateOnServer(String username, String password,
                                                               Handler handler, final Context context) {
        AuthenticateServerState oAuthServerState = new AuthenticateServerState();
        final HttpResponse oResponse;
        final ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(Constants.PARAM_USERNAME, username));
        params.add(new BasicNameValuePair(Constants.PARAM_PASSWORD, password));
        HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        final HttpPost post = new HttpPost(Constants.LOGIN_SERVER_URL);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        prepareApacheHttpConnection();
        try {
            oResponse = mHttpClient.execute(post);
            String sResponseString = EntityUtils.toString(oResponse.getEntity());
            if (oResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Bizagi: Auth, Successful authentication");
                }
                User oUser = new Gson().fromJson(sResponseString, User.class);
                sendResultToActivity(true, handler, context, null, oUser);
                oAuthServerState.setServerResponseReceived(true);
                oAuthServerState.setUserAuthorized(true);
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Bizagi: Auth, Error authenticating => " + oResponse.getStatusLine());
                }
                try {
                    NetworkServiceError oError = new Gson().fromJson(sResponseString,
                            NetworkServiceError.class);
                    sendResultToActivity(false, handler, context, oError, null);
                    oAuthServerState.setServerResponseReceived(true);
                    oAuthServerState.setUserAuthorized(false);
                } catch (Exception e) {
                    Log.v(TAG, "Bizagi: Route not found", e);
                }
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Bizagi: Auth, IOException when getting authtoken", e);
            }
            sendResultToActivity(false, handler, context, null, null);
            oAuthServerState.setServerResponseReceived(false);
            oAuthServerState.setUserAuthorized(false);
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Bizagi: Auth, getAuthtoken completing");
            }
        }
        return oAuthServerState;
    }

    private static void sendResultToActivity(final Boolean result, final Handler handler,
                                             final Context context, final NetworkServiceError error,
                                             final User user) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((LoginActivity) context).onAuthenticationResult(result, error, user);
            }
        });
    }

    public static Thread attemptAuth(final String username,
                                     final String password, final Handler handler,
                                     final Context context) {
        final Runnable runnable = new Runnable() {
            public void run() {
                authenticateOnServer(username, password, handler, context);
            }
        };
        return NetworkUtilities.performOnBackgroundThread(runnable);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

