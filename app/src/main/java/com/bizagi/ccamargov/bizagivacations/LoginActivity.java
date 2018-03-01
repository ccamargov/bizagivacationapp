package com.bizagi.ccamargov.bizagivacations;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bizagi.ccamargov.bizagivacations.model.NetworkServiceError;
import com.bizagi.ccamargov.bizagivacations.model.User;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;
import com.bizagi.ccamargov.bizagivacations.utilities.NetworkUtilities;
import com.bizagi.ccamargov.bizagivacations.utilities.Validation;

public class LoginActivity extends AccountAuthenticatorActivity implements View.OnClickListener {

    private final Handler oHandler = new Handler();
    private AccountManager oAccountManager;
    private Thread oAuthThread;
    private String sAuthTokenType;
    private boolean bRequestNewAccount = false;
    private String sPassword;
    private String sUsername;
    private EditText oTxtEmail;
    private EditText oTxtPassword;
    private TextInputLayout oEditLayoutEmail;
    private TextInputLayout oEditLayoutPass;
    private ProgressDialog oDialogProcess;
    private Context oContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(),
                "fonts/fontawesome-webfont.ttf");
        oAccountManager = AccountManager.get(this);
        final Intent oIntent = getIntent();
        sUsername = oIntent.getStringExtra(Constants.PARAM_USERNAME);
        sAuthTokenType = oIntent.getStringExtra(Constants.PARAM_AUTHTOKEN_TYPE);
        bRequestNewAccount = sUsername == null;
        setContentView(R.layout.activity_login);
        TextView oLabelIcon = findViewById(R.id.lblIcon);
        oLabelIcon.setTypeface(fontAwesomeFont);
        oTxtEmail = findViewById(R.id.txtEmail);
        oTxtPassword = findViewById(R.id.txtPassword);
        oEditLayoutEmail = findViewById(R.id.emailLayout);
        oEditLayoutPass = findViewById(R.id.passLayout);
        Button oBtnLogin = findViewById(R.id.btnLogin);
        oBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                Validation objValidate = new Validation(this);
                if (objValidate.isEmailAddress(oTxtEmail, oEditLayoutEmail) && objValidate.hasText(oTxtPassword, oEditLayoutPass)) {
                    online_login();
                }
                break;
            default:
                break;
        }
    }

    private void online_login() {
        showProgress();
        if (bRequestNewAccount) {
            sUsername = oTxtEmail.getText().toString();
        }
        sPassword = oTxtPassword.getText().toString();
        if (NetworkUtilities.isNetworkAvailable(oContext)) {
            oAuthThread = NetworkUtilities.attemptAuth(sUsername, sPassword, oHandler,
                    LoginActivity.this);
        } else {
            hideProgress();
            Toast.makeText(oContext, getText(R.string.login_no_internet_detected), Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog createdDialog() {
        oDialogProcess = new ProgressDialog(this);
        oDialogProcess.setMessage(getText(R.string.ui_activity_authenticating));
        oDialogProcess.setIndeterminate(true);
        oDialogProcess.setCancelable(false);
        oDialogProcess.setCanceledOnTouchOutside(false);
        oDialogProcess.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (oAuthThread != null) {
                    oAuthThread.interrupt();
                    finish();
                }
            }
        });
        return oDialogProcess;
    }

    private void showProgress() {
        createdDialog().show();
    }

    private void hideProgress() {
        if (oDialogProcess.isShowing()) {
            oDialogProcess.dismiss();
        }
    }

    public void onAuthenticationResult(boolean result, NetworkServiceError error, User user) {
        hideProgress();
        if (result) {
            finishLogin(user);
        } else {
            if (error != null) {
                switch (error.getWebCode()) {
                    case 1:
                        Toast.makeText(LoginActivity.this, getText(R.string.login_invalid_data),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(LoginActivity.this, getText(R.string.login_server_problem),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishLogin(User user) {
        final Account oAccountUser = new Account(sUsername, Constants.ACCOUNT_TYPE);
        if (bRequestNewAccount) {
            final Bundle oExtraData = new Bundle();
            oExtraData.putString(Constants.PARAM_USER_REMOTE_ID, String.valueOf(user.getRemoteId()));
            oExtraData.putString(Constants.PARAM_USER_API_KEY, user.getApiKey());
            oExtraData.putString(Constants.PARAM_LAST_SYNC, getString(R.string.no_synced));
            if (oAccountManager.addAccountExplicitly(oAccountUser, sPassword, oExtraData)) {
                ContentResolver.setIsSyncable(oAccountUser,
                        getString(R.string.provider_authority), 1);
                ContentResolver.setSyncAutomatically(oAccountUser,
                        getString(R.string.provider_authority), true);
                ContentResolver.addPeriodicSync(oAccountUser,
                        getString(R.string.provider_authority), new Bundle(), Constants.POLL_FREQUENCY);
            }
        } else {
            oAccountManager.setPassword(oAccountUser, sPassword);
        }
        final Intent oIntentAccount = new Intent();
        String sAuthToken = sPassword;
        oIntentAccount.putExtra(AccountManager.KEY_ACCOUNT_NAME, sUsername);
        oIntentAccount.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        if (sAuthTokenType != null
                && sAuthTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
            oIntentAccount.putExtra(AccountManager.KEY_AUTHTOKEN, sAuthToken);
        }
        setAccountAuthenticatorResult(oIntentAccount.getExtras());
        setResult(RESULT_OK, oIntentAccount);
        finish();
    }

}

