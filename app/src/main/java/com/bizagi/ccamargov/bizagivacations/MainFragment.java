package com.bizagi.ccamargov.bizagivacations;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;
import com.bizagi.ccamargov.bizagivacations.sync.SyncAdapter;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;
import com.bizagi.ccamargov.bizagivacations.utilities.NetworkUtilities;
import com.bizagi.ccamargov.bizagivacations.utilities.RecyclerViewEmptySupport;
import com.bizagi.ccamargov.bizagivacations.utilities.RequestListAdapter;

public class MainFragment extends Fragment implements RequestListAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public ImageView oImageRefresh;
    public boolean isManualRefresh;
    private MainActivity oMainActivity;
    private RequestListAdapter oRequestAdapter;
    private AlertDialog oAlertDialogRequest;
    private View oDialogViewRequest;
    private static int iCurrentRequestRemoteId = -1;

    public MainFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(1, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View oView = inflater.inflate(R.layout.fragment_main, container, false);
        oMainActivity = (MainActivity) getActivity();

        CardView oEmptyCard = oView.findViewById(R.id.empty_cardview);
        RecyclerViewEmptySupport oRequestVacationsList = oView.findViewById(R.id.requests_list);
        oRequestVacationsList.setEmptyView(oEmptyCard);
        oRequestAdapter = new RequestListAdapter(getActivity(), this, MainFragment.this);
        oRequestVacationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        oRequestVacationsList.setAdapter(oRequestAdapter);
        oImageRefresh = oView.findViewById(R.id.btn_refresh_requests_list);
        oImageRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtilities.isNetworkAvailable(getContext())) {
                    isManualRefresh = true;
                    SyncAdapter.syncNow(getActivity(), Constants.DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC);
                    RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(Animation.INFINITE);
                    anim.setDuration(700);
                    oImageRefresh.startAnimation(anim);
                } else {
                    oMainActivity.showAlertDialog(Constants.ALERT_DANGER,
                            getResources().getString(R.string.no_net_to_login_sync));
                }
            }
        });
        createRequestDialog();
        return oView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), ContractModel.RequestVacation.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (oRequestAdapter != null) {
            oRequestAdapter.swapCursor(data);
            if (isManualRefresh) {
                oImageRefresh.clearAnimation();
                Toast.makeText(getActivity(), getString(R.string.list_synced_updated),
                        Toast.LENGTH_LONG).show();
                isManualRefresh = false;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onClick(RequestListAdapter.ViewHolder holder, int idLine) {
        oRequestAdapter.getCursor().moveToPosition(idLine);
        Cursor oCursor = oRequestAdapter.getCursor();
        iCurrentRequestRemoteId = oCursor
                .getInt(oCursor.getColumnIndex(ContractModel.RequestVacation.REMOTE_ID));
        TextView oEmployee = oDialogViewRequest.findViewById(R.id.employee_name);
        TextView oProcess = oDialogViewRequest.findViewById(R.id.process);
        TextView oActivity = oDialogViewRequest.findViewById(R.id.activity);
        TextView oRequestDate = oDialogViewRequest.findViewById(R.id.request_date);
        TextView oBeginDate = oDialogViewRequest.findViewById(R.id.begin_date);
        TextView oEndDate = oDialogViewRequest.findViewById(R.id.end_date);
        TextView oLastVacationDate = oDialogViewRequest.findViewById(R.id.last_vacation_date);
        TextView oApproved = oDialogViewRequest.findViewById(R.id.request_status);
        Button oConfirmButton = oDialogViewRequest.findViewById(R.id.dialog_request_confirm);
        oEmployee.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.EMPLOYEE)));
        oProcess.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.PROCESS)));
        oActivity.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.ACTIVITY)));
        oRequestDate.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.REQUEST_DATE)));
        oBeginDate.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.BEGIN_DATE)));
        oEndDate.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.END_DATE)));
        oLastVacationDate.setText(oCursor
                .getString(oCursor.getColumnIndex(ContractModel.RequestVacation.LAST_VACATION_ON)));
        boolean bIsApproved = oCursor
                .getInt(oCursor.getColumnIndex(ContractModel.RequestVacation.IS_APPROVED)) > 0;
        if (bIsApproved) {
            oApproved.setTextColor(getResources().getColor(R.color.colorAlertSuccess));
            oApproved.setText(getResources().getString(R.string.approved));
            oConfirmButton.setText(getResources().getString(R.string.approved));
            oConfirmButton.setEnabled(false);
        } else {
            oApproved.setTextColor(getResources().getColor(R.color.colorAlertDanger));
            oApproved.setText(getResources().getString(R.string.not_approved));
        }
        oAlertDialogRequest.show();
    }

    private void createRequestDialog() {
        oAlertDialogRequest = null;
        LayoutInflater oInflater = LayoutInflater.from(getActivity());
        AlertDialog.Builder oAlertDialogBuilder;
        Typeface fontAwesomeFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/fontawesome-webfont.ttf");
        oDialogViewRequest = oInflater.inflate(R.layout.request_dialog_layout, null);
        String sTextRequestTitle = getResources().getString(R.string.fa_card_request);
        TextView oTitleDialogRequest = oDialogViewRequest.findViewById(R.id.dialog_title_request);
        Button oButtonConfirmDialog
                = oDialogViewRequest.findViewById(R.id.dialog_request_confirm);
        Button oButtonCancelDialog
                = oDialogViewRequest.findViewById(R.id.dialog_request_cancel);
        oTitleDialogRequest.setTypeface(fontAwesomeFont);
        oTitleDialogRequest.setText(sTextRequestTitle);
        oAlertDialogBuilder = new AlertDialog.Builder(getActivity());
        oAlertDialogBuilder.setView(oDialogViewRequest);
        oAlertDialogRequest = oAlertDialogBuilder.create();
        final Dialog oFinalRequestAlterDialog = oAlertDialogRequest;
        oButtonConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCurrentRequestRemoteId != -1) {
                    ContentValues oValues = new ContentValues();
                    oValues.put(ContractModel.RequestVacation.IS_APPROVED, Constants.INTEGER_VALUE_TRUE);
                    getActivity().getContentResolver()
                            .update(ContractModel.RequestVacation.CONTENT_URI, oValues,
                                    ContractModel.RequestVacation.REMOTE_ID + " = ?",
                                    new String[] {String.valueOf(iCurrentRequestRemoteId)});
                    oFinalRequestAlterDialog.dismiss();
                    restartDialogRequestElements();
                }
            }

        });
        oButtonCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oFinalRequestAlterDialog.dismiss();
                restartDialogRequestElements();
            }
        });
        oAlertDialogRequest.setCanceledOnTouchOutside(false);
    }

    private void restartDialogRequestElements() {
        TextView oEmployee = oDialogViewRequest.findViewById(R.id.employee_name);
        TextView oProcess = oDialogViewRequest.findViewById(R.id.process);
        TextView oActivity = oDialogViewRequest.findViewById(R.id.activity);
        TextView oRequestDate = oDialogViewRequest.findViewById(R.id.request_date);
        TextView oBeginDate = oDialogViewRequest.findViewById(R.id.begin_date);
        TextView oEndDate = oDialogViewRequest.findViewById(R.id.end_date);
        TextView oLastVacationDate = oDialogViewRequest.findViewById(R.id.last_vacation_date);
        TextView oApproved = oDialogViewRequest.findViewById(R.id.request_status);
        Button oConfirmButton = oDialogViewRequest.findViewById(R.id.dialog_request_confirm);
        oConfirmButton.setText(getResources().getString(R.string.action_approve));
        oConfirmButton.setEnabled(true);
        oEmployee.setText("");
        oProcess.setText("");
        oActivity.setText("");
        oRequestDate.setText("");
        oBeginDate.setText("");
        oEndDate.setText("");
        oLastVacationDate.setText("");
        oApproved.setText("");
        iCurrentRequestRemoteId = -1;
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}