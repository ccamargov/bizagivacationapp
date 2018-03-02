package com.bizagi.ccamargov.bizagivacations.model;

/**
 * Object that represents the components of each item that is included
 * in the NavigationViewDrawer (Menu)
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class NavItem {

    private String title;
    private String subtitle;
    private int icon;

    /**
     *  Constructor class
     *  @param title Main item text
     *  @param subtitle Description of the item
     *  @param icon Representative item icon
     */
    public NavItem(String title, String subtitle, int icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }
    /**
     *  Access method
     */
    public String getTitle() {
        return title;
    }
    /**
     *  Access method
     */
    public String getSubtitle() {
        return subtitle;
    }
    /**
     *  Access method
     */
    public int getIcon() {
        return icon;
    }
    /**
     *  Access method
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

}
