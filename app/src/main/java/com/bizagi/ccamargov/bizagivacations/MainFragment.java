package com.bizagi.ccamargov.bizagivacations;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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

    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}