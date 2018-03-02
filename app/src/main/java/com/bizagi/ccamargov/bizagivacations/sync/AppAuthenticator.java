package com.bizagi.ccamargov.bizagivacations.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bizagi.ccamargov.bizagivacations.LoginActivity;
import com.bizagi.ccamargov.bizagivacations.model.AuthenticateServerState;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;
import com.bizagi.ccamargov.bizagivacations.utilities.NetworkUtilities;
import com.bizagi.ccamargov.bizagivacations.R;

/**
 * Authenticator class. Manage Bizagi accounts usind the Android System Accounts.
 * This class is necessary to work with the android synchronization process.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

class AppAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;
    /**
     *  Constructor class
     *  @param context Application context
     */
    AppAuthenticator(Context context) {
        super(context);
        mContext = context;
    }
    // Add new account to the device
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType, String[] requiredFeatures,
                             Bundle options) {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Constants.PARAM_AUTHTOKEN_TYPE,
                authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }
    // Check account credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account, Bundle options) {
        throw new UnsupportedOperationException();
    }
    // Edit account properties, adding or deleting properties.
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        throw new UnsupportedOperationException();
    }
    // Gets an auth token of the specified type for a particular account
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account, String authTokenType, Bundle loginOptions) {
        if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                    "LP T&A: Invalid authTokenType");
            return result;
        }
        final AccountManager am = AccountManager.get(mContext);
        final String password = am.getPassword(account);
        if (password != null) {
            final boolean verified =
                    onlineConfirmPassword(account.name, password);
            if (verified) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE,
                        Constants.ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, password);
                return result;
            }
        }
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Constants.PARAM_USERNAME, account.name);
        intent.putExtra(Constants.PARAM_AUTHTOKEN_TYPE,
                authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }
    // Gets an String that contains the applicaiton authToken label.
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
            return mContext.getString(R.string.label_auth_token);
        }
        return null;

    }
    // Finds out whether a particular account has all the specified features.
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
    // Confirm the password stored in the device account, to check if is equal to Server Account Password
    private boolean onlineConfirmPassword(String username, String password) {
        AuthenticateServerState oAuthServerState =
                NetworkUtilities.authenticateOnServer(username, password,
                        null, null);
        return oAuthServerState.isUserAuthorized();
    }
    // Operation not used
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account, String authTokenType, Bundle loginOptions) {
        throw new UnsupportedOperationException();
    }

}
