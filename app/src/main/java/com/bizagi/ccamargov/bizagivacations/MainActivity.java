package com.bizagi.ccamargov.bizagivacations;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bizagi.ccamargov.bizagivacations.model.NavItem;
import com.bizagi.ccamargov.bizagivacations.sync.SyncAdapter;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;
import com.bizagi.ccamargov.bizagivacations.utilities.NavListAdapter;
import com.bizagi.ccamargov.bizagivacations.utilities.NetworkUtilities;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Main Activity. This activity manages all the processes included in the fragments of the application.
 * It is responsible for configuring the NavigationViewDrawer, custom alert messages, logoff, and fragment exchange.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private ListView oNavList;
    private ArrayList<NavItem> aNavItems = new ArrayList<>();
    private ArrayList<NavItem> aDropDownLangItems = new ArrayList<>();
    private AccountManager oAccountManager;
    private Account oUserAccount;
    private Context oContext = this;
    private ProgressDialog oDialogProcess;
    private static final int DIALOG_LANG = 1;
    private static final int DIALOG_LOGOUT = 2;
    private static final int DIALOG_EXIT_BY_BACK = 3;
    private static final int DIALOG_ALERT = 4;
    private MainFragment oMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oAccountManager = AccountManager.get(this);
        final Account oAvailableAccounts[] = oAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (oAvailableAccounts.length == 0) {
            Toast.makeText(this, getText(R.string.request_login), Toast.LENGTH_SHORT).show();
            finish();
            Intent oIntent = new Intent(this, LoginActivity.class);
            startActivity(oIntent);
        } else {
            if (getIntent().getStringExtra(Constants.PARAM_FROM_TO) != null &&
                    getIntent().getStringExtra(Constants.PARAM_FROM_TO)
                            .equals(Constants.VALUE_FROM_LOGIN_TO_HOME) &&
                    NetworkUtilities.isNetworkAvailable(oContext)) {
                if (!ContentResolver.getMasterSyncAutomatically()) {
                    SyncAdapter.syncNow(this, Constants.DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC);
                }
            }
            JodaTimeAndroid.init(this);
            oUserAccount = oAvailableAccounts[0];
            setLocale();
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView oEmailUserTextView = findViewById(R.id.nav_email_user);
            oEmailUserTextView.setText(oUserAccount.name);
            TextView oNameUserTextView = findViewById(R.id.nav_full_name_user);
            oNameUserTextView.setText(oAccountManager.getUserData(oUserAccount,
                    Constants.PARAM_FULL_NAME));
            aNavItems.add(new NavItem(getResources().getStringArray(R.array.nav_items)[0],
                    Locale.getDefault().getLanguage().toUpperCase(), R.drawable.ic_earth));
            aNavItems.add(new NavItem(getResources().getStringArray(R.array.nav_items)[1],
                    getLastSyncSubtitleNavItem(), R.drawable.ic_sync));
            aNavItems.add(new NavItem(getResources().getStringArray(R.array.nav_items)[2],
                    null, R.drawable.ic_logout));
            DrawerLayout oDrawerLayout = findViewById(R.id.main_layout);
            oNavList = findViewById(R.id.nav_menu_list);
            NavListAdapter oItemAdapter = new NavListAdapter(this, aNavItems);
            oNavList.setAdapter(oItemAdapter);
            oNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            showDialog(DIALOG_LANG);
                            break;
                        case 1:
                            syncAllDataManual();
                            break;
                        case 2:
                            showDialog(DIALOG_LOGOUT);
                            break;
                    }
                }
            });
            ActionBarDrawerToggle oDrawerToogle = new ActionBarDrawerToggle(
            this, oDrawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
            oDrawerLayout.addDrawerListener(oDrawerToogle);
            oDrawerToogle.syncState();
            if (savedInstanceState == null) {
                loadMainFragment();
            }
        }
    }

    private void loadMainFragment() {
        oMainFragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout_container, oMainFragment);
        fragmentTransaction.commit();
    }
    // Manage the successful result of synchronization
    private BroadcastReceiver synFinishedWithErrors = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSyncSubtitleNavItem(getLastSyncSubtitleNavItem());
            resetSyncElements();
            Toast.makeText(context, getText(R.string.sync_server_failed_msg),
                    Toast.LENGTH_SHORT).show();
        }
    };
    // Manage the error result of synchronization
    private BroadcastReceiver syncInOrderFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSyncSubtitleNavItem(getLastSyncSubtitleNavItem());
            resetSyncElements();
            Toast.makeText(context, getText(R.string.succesfull_sync),
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(synFinishedWithErrors,
                new IntentFilter(Constants.SYNC_IN_ORDER_FINISHED_WITH_ERRORS));
        registerReceiver(syncInOrderFinished,
                new IntentFilter(Constants.SYNC_IN_ORDER_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(synFinishedWithErrors);
        unregisterReceiver(syncInOrderFinished);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog oAlertDialog = null;
        LayoutInflater oInflater = LayoutInflater.from(this);
        View oDialogView;
        AlertDialog.Builder oAlertDialogBuilder;
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(),
                "fonts/fontawesome-webfont.ttf");
        switch (id) {
            case DIALOG_LANG:
                oDialogView = oInflater.inflate(R.layout.lang_dialog_layout, null);
                String sTitleLangDialog = getResources().getString(R.string.fa_language);
                TextView oTitleDialogLang = oDialogView.findViewById(R.id.dialog_title_lang);
                Button oButtonLangDialog = oDialogView.findViewById(R.id.dialog_lang_button);
                oTitleDialogLang.setTypeface(fontAwesomeFont);
                oTitleDialogLang.setText(sTitleLangDialog);
                Spinner oDropDownLang = oDialogView.findViewById(R.id.dropdown_lang);
                oDropDownLang.setAdapter(null);
                aDropDownLangItems.add(new NavItem(
                        getResources().getStringArray(R.array.lang_drop_items_title)[0],
                        getResources().getStringArray(R.array.lang_drop_items_subtitle)[0],
                        R.drawable.ic_flag));
                aDropDownLangItems.add(new NavItem(
                        getResources().getStringArray(R.array.lang_drop_items_title)[1],
                        getResources().getStringArray(R.array.lang_drop_items_subtitle)[1],
                        R.drawable.ic_flag));
                NavListAdapter oLangItemAdapter = new NavListAdapter(this, aDropDownLangItems);
                oDropDownLang.setAdapter(oLangItemAdapter);
                oAlertDialogBuilder = new AlertDialog.Builder(this);
                oAlertDialogBuilder.setView(oDialogView);
                oAlertDialog = oAlertDialogBuilder.create();
                final Dialog oFinalLangAlertDialog = oAlertDialog;
                oButtonLangDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView oSubtitle = oNavList.getChildAt
                                (oNavList.getFirstVisiblePosition())
                                .findViewById(R.id.sub_title_nav_item);
                        Spinner oLangSpinner = oFinalLangAlertDialog.
                                findViewById(R.id.dropdown_lang);
                        NavItem oItemLang = (NavItem) oLangSpinner.getSelectedItem();
                        NavItem oCurrentItem = (NavItem) oNavList.getAdapter().getItem(0);
                        oSubtitle.setText(oItemLang.getSubtitle());
                        oCurrentItem.setSubtitle(oItemLang.getSubtitle());
                        changeLang(oItemLang.getSubtitle().toLowerCase());
                        oFinalLangAlertDialog.dismiss();
                        finish();
                        Intent oIntent = new Intent(oContext, MainActivity.class);
                        startActivity(oIntent);
                    }
                });
                break;
            case DIALOG_LOGOUT:
                oDialogView = oInflater.inflate(R.layout.logout_dialog_layout, null);
                String sTextLogoutTitle = getResources().getString(R.string.fa_sign_out);
                TextView oTitleDialogLogout = oDialogView.findViewById(R.id.dialog_title_logout);
                Button oButtonLogoutConfirmDialog
                        = oDialogView.findViewById(R.id.dialog_logout_confirm);
                Button oButtonLogoutCancelDialog
                        = oDialogView.findViewById(R.id.dialog_logout_cancel);
                oTitleDialogLogout.setTypeface(fontAwesomeFont);
                oTitleDialogLogout.setText(sTextLogoutTitle);
                oAlertDialogBuilder = new AlertDialog.Builder(this);
                oAlertDialogBuilder.setView(oDialogView);
                oAlertDialog = oAlertDialogBuilder.create();
                final Dialog oFinalLogoutAlterDialog = oAlertDialog;
                oButtonLogoutConfirmDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logOut();
                    }
                });
                oButtonLogoutCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oFinalLogoutAlterDialog.dismiss();
                    }
                });
                break;
            case DIALOG_EXIT_BY_BACK:
                oDialogView = oInflater.inflate(R.layout.exit_back_dialog_layout, null);
                String sTextExitTitle = getResources().getString(R.string.fa_sign_out);
                TextView oTitleDialogExit = oDialogView.findViewById(R.id.dialog_title_exit);
                Button oButtonExitConfirmDialog
                        = oDialogView.findViewById(R.id.dialog_exit_confirm);
                Button oButtonExitCancelDialog
                        = oDialogView.findViewById(R.id.dialog_exit_cancel);
                oTitleDialogExit.setTypeface(fontAwesomeFont);
                oTitleDialogExit.setText(sTextExitTitle);
                oAlertDialogBuilder = new AlertDialog.Builder(this);
                oAlertDialogBuilder.setView(oDialogView);
                oAlertDialog = oAlertDialogBuilder.create();
                final Dialog oFinalExitAlterDialog = oAlertDialog;
                oButtonExitConfirmDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                oButtonExitCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oFinalExitAlterDialog.dismiss();
                    }
                });
                break;
            case DIALOG_ALERT:
                oDialogView = oInflater.inflate(R.layout.alert_dialog_layout, null);
                Button oButtonAlertConfirmDialog
                        = oDialogView.findViewById(R.id.alert_dialog_button);
                oAlertDialogBuilder = new AlertDialog.Builder(this);
                oAlertDialogBuilder.setView(oDialogView);
                oAlertDialog = oAlertDialogBuilder.create();
                final Dialog oFinalAlertDialog = oAlertDialog;
                oButtonAlertConfirmDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oFinalAlertDialog.dismiss();
                    }
                });
                break;
        }
        return oAlertDialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        switch (id) {
            case DIALOG_ALERT:
                LinearLayout oLayoutAlertTitle = dialog.findViewById(R.id.alert_dialog_header_layout);
                Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(),
                        "fonts/fontawesome-webfont.ttf");
                TextView oTitleAlertDialog = dialog.findViewById(R.id.dialog_alert_title);
                oTitleAlertDialog.setTypeface(fontAwesomeFont);
                TextView oMessageAlertDialog = dialog.findViewById(R.id.dialog_alert_message);
                oLayoutAlertTitle.setBackgroundColor(bundle.getInt(Constants.ALERT_DIALOG_COLOR_KEY));
                oTitleAlertDialog.setText(bundle.getString(Constants.ALERT_DIALOG_TITLE_KEY));
                oMessageAlertDialog.setText(bundle.getString(Constants.ALERT_DIALOG_MESSAGE_KEY));
                break;
        }
    }

    private void resetSyncElements() {
        hideProcessDialog();
        oMainFragment.oImageRefresh.clearAnimation();
        oMainFragment.isManualRefresh = false;
    }

    public void showAlertDialog(String type, String message) {
        Bundle bundle = new Bundle();
        switch (type) {
            case Constants.ALERT_SUCCESS:
                bundle.putInt(Constants.ALERT_DIALOG_COLOR_KEY, getResources().getColor(R.color.colorAlertSuccess));
                bundle.putString(Constants.ALERT_DIALOG_TITLE_KEY, getResources().getString(R.string.fa_success));
                break;
            case Constants.ALERT_WARNING:
                bundle.putInt(Constants.ALERT_DIALOG_COLOR_KEY, getResources().getColor(R.color.colorAlertWarning));
                bundle.putString(Constants.ALERT_DIALOG_TITLE_KEY, getResources().getString(R.string.fa_warning));
                break;
            case Constants.ALERT_DANGER:
                bundle.putInt(Constants.ALERT_DIALOG_COLOR_KEY, getResources().getColor(R.color.colorAlertDanger));
                bundle.putString(Constants.ALERT_DIALOG_TITLE_KEY, getResources().getString(R.string.fa_danger));
                break;
            case Constants.ALERT_INFO:
                bundle.putInt(Constants.ALERT_DIALOG_COLOR_KEY, getResources().getColor(R.color.colorAlertInfo));
                bundle.putString(Constants.ALERT_DIALOG_TITLE_KEY, getResources().getString(R.string.fa_info));
                break;
        }
        bundle.putString(Constants.ALERT_DIALOG_MESSAGE_KEY, message);
        showDialog(DIALOG_ALERT, bundle);
    }
    // Change favorite language application
    private void changeLang(String sCodeLang) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.DEFAULT_LANG_PREF,
                MODE_PRIVATE).edit();
        editor.putString(Constants.DEFAULT_LANG_KEY, sCodeLang);
        editor.apply();
        Locale locale = new Locale(sCodeLang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void setLocale() {
        SharedPreferences prefs = getSharedPreferences(Constants.DEFAULT_LANG_PREF, MODE_PRIVATE);
        if (prefs.contains(Constants.DEFAULT_LANG_KEY)) {
            changeLang(prefs.getString(Constants.DEFAULT_LANG_KEY, Locale.getDefault()
                    .getLanguage()));
        }
    }
    // Close session and remove account
    private void logOut() {
        oAccountManager.removeAccount(oUserAccount, null, null);
        finish();
        Intent oIntent = new Intent(oContext, LoginActivity.class);
        startActivity(oIntent);
    }
    // Update the DateTime information of the last success synchronization
    private void setSyncSubtitleNavItem(String sSubtitleSync) {
        if (oNavList.getChildCount() > 0) {
            TextView oSubtitleSync = oNavList.getChildAt
                    (oNavList.getFirstVisiblePosition() + 1)
                    .findViewById(R.id.sub_title_nav_item);
            NavItem oSyncItem = (NavItem) oNavList.getAdapter().getItem(1);
            oSubtitleSync.setText(sSubtitleSync);
            oSyncItem.setSubtitle(sSubtitleSync);
        }
    }

    private String getLastSyncSubtitleNavItem() {
        return getString(R.string.synced) + ": " +
                oAccountManager.getUserData(oUserAccount, Constants.PARAM_LAST_SYNC);
    }

    private Dialog createProcessDialog(String message) {
        oDialogProcess = new ProgressDialog(this);
        oDialogProcess.setMessage(message);
        oDialogProcess.setIndeterminate(true);
        oDialogProcess.setCancelable(true);
        oDialogProcess.setCanceledOnTouchOutside(false);
        return oDialogProcess;
    }

    private void hideProcessDialog() {
        if (oDialogProcess != null && oDialogProcess.isShowing()) {
            oDialogProcess.dismiss();
        }
    }

    public void showSyncProcessDialog() {
        createProcessDialog(getString(R.string.syncing)).show();
    }
    // Synchronize all information manually
    private void syncAllDataManual() {
        if (NetworkUtilities.isNetworkAvailable(oContext)) {
            showSyncProcessDialog();
            SyncAdapter.syncNow(this, Constants.DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC);
        } else {
            showAlertDialog(Constants.ALERT_DANGER,
                    getResources().getString(R.string.no_net_to_login_sync));
        }
    }

    private void exitByBackKey() {
        showDialog(DIALOG_EXIT_BY_BACK);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}