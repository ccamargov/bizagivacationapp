package com.bizagi.ccamargov.bizagivacations.model;

public class RequestVacation {

    private int id;
    private String process;
    private String activity;
    private String request_date;
    private String employee;
    private String begin_date;
    private String end_date;
    private String last_vacation_on;
    private boolean approved;

    public RequestVacation(int id, String process, String activity, String request_date, String employee, String begin_date, String end_date, String last_vacation_on, boolean approved) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getRequestDate() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getBeginDate() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getlastVacationOn() {
        return last_vacation_on;
    }

    public void setLast_vacation_on(String last_vacation_on) {
        this.last_vacation_on = last_vacation_on;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}