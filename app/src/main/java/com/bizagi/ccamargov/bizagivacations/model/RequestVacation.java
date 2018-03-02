package com.bizagi.ccamargov.bizagivacations.model;

import android.content.Context;

import com.bizagi.ccamargov.bizagivacations.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Object that represents the structure of a vacation request. Each instance represents a request.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class RequestVacation {

    // Constants that store the integer value of each state that a request can have.
    public static final int PENDING_REQUEST = 0;
    public static final int APPROVED_REQUEST = 1;
    public static final int REJECTED_REQUEST = 2;

    private int id;
    private String process;
    private String activity;
    private String request_date;
    private String employee;
    private String begin_date;
    private String end_date;
    private String last_vacation_on;
    private int approved;
    private Context context;

    /**
     *  Constructor class
     *  @param id Record id from server (Original id)
     *  @param process Process type
     *  @param activity Activity type
     *  @param request_date Date on which the request was made
     *  @param employee Employee requested
     *  @param begin_date Start date of the vacations
     *  @param end_date End date of the vacations
     *  @param last_vacation_on Date of last vacation
     *  @param approved Request status (Can take 3 values)
     */
    public RequestVacation(int id, String process, String activity, String request_date, String employee, String begin_date, String end_date, String last_vacation_on, int approved) {
        this.id = id;
        this.process = process;
        this.activity = activity;
        this.request_date = request_date;
        this.employee = employee;
        this.begin_date = begin_date;
        this.end_date = end_date;
        this.last_vacation_on = last_vacation_on;
        this.approved = approved;
    }
    /**
     *  Constructor class
     */
    public RequestVacation(Context context, int id, String employee, String begin_date, String end_date, int approved) {
        this.id = id;
        this.employee = employee;
        this.begin_date = begin_date;
        this.end_date = end_date;
        this.approved = approved;
        this.context = context;
    }

    /**
     *  Access method
     */
    public int getId() {
        return id;
    }
    /**
     *  Access method
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     *  Access method
     */
    public String getProcess() {
        return process;
    }
    /**
     *  Access method
     */
    public void setProcess(String process) {
        this.process = process;
    }
    /**
     *  Access method
     */
    public String getActivity() {
        return activity;
    }
    /**
     *  Access method
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }
    /**
     *  Access method
     */
    public String getRequestDate() {
        return request_date;
    }
    /**
     *  Access method
     */
    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }
    /**
     *  Access method
     */
    public String getEmployee() {
        return employee;
    }
    /**
     *  Access method
     */
    public void setEmployee(String employee) {
        this.employee = employee;
    }
    /**
     *  Access method
     */
    public String getBeginDate() {
        return begin_date;
    }
    /**
     *  Access method
     */
    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }
    /**
     *  Access method
     */
    public String getEndDate() {
        return end_date;
    }
    /**
     *  Access method
     */
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
    /**
     *  Access method
     */
    public String getlastVacationOn() {
        return last_vacation_on;
    }
    /**
     *  Access method
     */
    public void setLast_vacation_on(String last_vacation_on) {
        this.last_vacation_on = last_vacation_on;
    }
    /**
     *  Access method
     */
    public int getStatusRequest() {
        return approved;
    }
    /**
     *  Access method
     */
    public void setStatusRequest(int approved) {
        this.approved = approved;
    }
    /**
     *  Access method
     *  Concatenates the begin date and the end date to build a time interval in a text string.
     */
    public String getRangeRequest() {
        return this.begin_date + " " + this.context.getResources().getString(R.string.to) + " " +
                this.end_date;
    }
    /**
     *  Access method
     *  Calculate the number of days that the employee has requested for his vacation.
     */
    public String getDaysBetweenRequest() {
        DateTimeFormatter oFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime oDateBegin = DateTime.parse(this.begin_date, oFormatter);
        DateTime oDateEnd = DateTime.parse(this.end_date, oFormatter);
        int iCountDays = Days.daysBetween(oDateBegin.withTimeAtStartOfDay(),
                oDateEnd.withTimeAtStartOfDay()).getDays();
        return iCountDays + " " + this.context.getResources().getString(R.string.days_requested);
    }
}