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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bizagi.ccamargov.bizagivacations.model.RequestVacation;
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
    private Spinner oStateFilter;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_schedule_data:
                oMainActivity.showSyncProcessDialog();
                SyncAdapter.syncNow(getActivity(), Constants.UPLOAD_REQUEST_RECORDS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View oView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
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
        oStateFilter = oView.findViewById(R.id.state_filter);
        String[] oFilterOptions = new String[]{
                getResources().getString(R.string.all),
                getResources().getString(R.string.pending),
                getResources().getString(R.string.approved),
                getResources().getString(R.string.rejected)
        };
        ArrayAdapter<String> oFilterAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, oFilterOptions);
        oStateFilter.setAdapter(oFilterAdapter);
        oStateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                reloadRequestData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        createRequestDialog();
        return oView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String sFilterSelected = (String) oStateFilter.getSelectedItem();
        int iFilterState = -1;
        if (sFilterSelected.equals(getResources().getString(R.string.pending))) {
            iFilterState = RequestVacation.PENDING_REQUEST;
        } else if (sFilterSelected.equals(getResources().getString(R.string.approved))) {
            iFilterState = RequestVacation.APPROVED_REQUEST;
        } else if (sFilterSelected.equals(getResources().getString(R.string.rejected))) {
            iFilterState = RequestVacation.REJECTED_REQUEST;
        }
        if (iFilterState != -1) {
            String sQuerySelection = ContractModel.ROUT_REQUEST_VACATIONS + "." +
                    ContractModel.RequestVacation.REQUEST_STATUS + " = ?";
            String oShiftSelectionArgs[] = {
                    String.valueOf(iFilterState)
            };
            return new CursorLoader(getActivity(), ContractModel.RequestVacation.CONTENT_URI,
                    null, sQuerySelection, oShiftSelectionArgs, null);
        } else {
            return new CursorLoader(getActivity(), ContractModel.RequestVacation.CONTENT_URI,
                    null, null, null, null);
        }
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
        restartDialogRequestElements();
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
        int iRequestState = oCursor
                .getInt(oCursor.getColumnIndex(ContractModel.RequestVacation.REQUEST_STATUS));
        if (iRequestState == RequestVacation.PENDING_REQUEST) {
            oApproved.setTextColor(getResources().getColor(R.color.colorAlertWarning));
            oApproved.setText(getResources().getString(R.string.pending));
        } else if (iRequestState == RequestVacation.APPROVED_REQUEST) {
            oApproved.setTextColor(getResources().getColor(R.color.colorAlertSuccess));
            oApproved.setText(getResources().getString(R.string.approved));
        } else {
            oApproved.setTextColor(getResources().getColor(R.color.colorAlertDanger));
            oApproved.setText(getResources().getString(R.string.rejected));
        }
        oAlertDialogRequest.show();
    }

    private void reloadRequestData() {
        getLoaderManager().restartLoader(1, null, this);
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
        Button oButtonRejectDialog
                = oDialogViewRequest.findViewById(R.id.dialog_request_reject);
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
                    oValues.put(ContractModel.RequestVacation.REQUEST_STATUS, RequestVacation.APPROVED_REQUEST);
                    oValues.put(ContractModel.RequestVacation.UPDATE_STATE, Constants.RECORD_STATE_PENDING_SYNC);
                    getActivity().getContentResolver()
                            .update(ContractModel.RequestVacation.CONTENT_URI, oValues,
                                    ContractModel.RequestVacation.REMOTE_ID + " = ?",
                                    new String[] {String.valueOf(iCurrentRequestRemoteId)});
                    oFinalRequestAlterDialog.dismiss();
                    SyncAdapter.syncNow(getActivity(), Constants.UPLOAD_REQUEST_RECORDS);
                }
            }

        });
        oButtonRejectDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCurrentRequestRemoteId != -1) {
                    ContentValues oValues = new ContentValues();
                    oValues.put(ContractModel.RequestVacation.REQUEST_STATUS, RequestVacation.REJECTED_REQUEST);
                    oValues.put(ContractModel.RequestVacation.UPDATE_STATE, Constants.RECORD_STATE_PENDING_SYNC);
                    getActivity().getContentResolver()
                            .update(ContractModel.RequestVacation.CONTENT_URI, oValues,
                                    ContractModel.RequestVacation.REMOTE_ID + " = ?",
                                    new String[] {String.valueOf(iCurrentRequestRemoteId)});
                    oFinalRequestAlterDialog.dismiss();
                    SyncAdapter.syncNow(getActivity(), Constants.UPLOAD_REQUEST_RECORDS);
                }
            }
        });
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