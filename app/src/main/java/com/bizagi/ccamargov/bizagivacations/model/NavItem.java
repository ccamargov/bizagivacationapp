package com.bizagi.ccamargov.bizagivacations.model;

public class NavItem {

    private String title;
    private String subtitle;
    private int icon;

    public NavItem(String title, String subtitle, int icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

}
