package com.bizagi.ccamargov.bizagivacations.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizagi.ccamargov.bizagivacations.MainFragment;
import com.bizagi.ccamargov.bizagivacations.R;
import com.bizagi.ccamargov.bizagivacations.model.RequestVacation;
import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    private final Context oContext;
    private Cursor oRequestData;
    private OnItemClickListener oListener;

    public interface OnItemClickListener {
        void onClick(ViewHolder holder, int idLine);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView oStatusRequest;
        private TextView oTxtEmployeeName;
        private TextView oTxtRangeRequest;
        private TextView oTxtNumDaysRange;
        private ImageView oMarkImageApproved;

        ViewHolder(View v) {
            super(v);
            oStatusRequest = v.findViewById(R.id.status_request_vacation);
            oTxtEmployeeName = v.findViewById(R.id.employee_name);
            oTxtRangeRequest = v.findViewById(R.id.range_dates);
            oTxtNumDaysRange = v.findViewById(R.id.num_days_range);
            oMarkImageApproved = v.findViewById(R.id.approval_state);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            oListener.onClick(this, getAdapterPosition());
        }
    }

    public RequestListAdapter(Context contexto, OnItemClickListener listener, MainFragment main_fragment) {
        this.oContext = contexto;
        this.oListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_vacation_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        oRequestData.moveToPosition(position);
        int iColorStatus;
        RequestVacation oRequest = new RequestVacation(
                oContext,
                oRequestData.getInt(oRequestData.getColumnIndex(ContractModel.RequestVacation.REMOTE_ID)),
                oRequestData.getString(oRequestData.getColumnIndex(ContractModel.RequestVacation.EMPLOYEE)),
                oRequestData.getString(oRequestData.getColumnIndex(ContractModel.RequestVacation.BEGIN_DATE)),
                oRequestData.getString(oRequestData.getColumnIndex(ContractModel.RequestVacation.END_DATE)),
                oRequestData.getInt(oRequestData.getColumnIndex(ContractModel.RequestVacation.IS_APPROVED)) > 0
        );
        holder.oTxtEmployeeName.setText(oRequest.getEmployee());
        holder.oTxtRangeRequest.setText(oRequest.getRangeRequest());
        holder.oTxtNumDaysRange.setText(oRequest.getDaysBetweenRequest());
        if (oRequest.isApproved()) {
            iColorStatus = ContextCompat.getColor(oContext,
                    R.color.colorAlertSuccess);
            holder.oMarkImageApproved.setVisibility(View.VISIBLE);
        } else {
            iColorStatus = ContextCompat.getColor(oContext,
                    R.color.colorAlertDanger);
            holder.oMarkImageApproved.setVisibility(View.GONE);
        }
        holder.oStatusRequest.setBackgroundColor(iColorStatus);
    }

    @Override
    public int getItemCount() {
        if (oRequestData != null)
            return oRequestData.getCount();
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            oRequestData = newCursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return oRequestData;
    }

}
