package com.sudjunham.boonyapon;

public class FilteHelper {

    private boolean cb_ui , cb_outsude , cb_credit1,cb_credit2,cb_credit3,cb_credit4,cb_credit5;
    private int minMonth , maxMonth;

    private String faculty;
    private static final FilteHelper instance = new FilteHelper();

    public FilteHelper() {
        this.cb_ui = cb_ui;
        this.cb_outsude = cb_outsude;
        this.cb_credit1 = cb_credit1;
        this.cb_credit2 = cb_credit2;
        this.cb_credit3 = cb_credit3;
        this.cb_credit4 = cb_credit4;
        this.cb_credit5 = cb_credit5;
        this.minMonth = minMonth;
        this.maxMonth = maxMonth;
        this.faculty = faculty;
    }

    public static FilteHelper getInstance() {
        return instance;
    }

    public boolean isCb_ui() {
        return cb_ui;
    }

    public void setCb_ui(boolean cb_ui) {
        this.cb_ui = cb_ui;
    }

    public boolean isCb_outsude() {
        return cb_outsude;
    }

    public void setCb_outsude(boolean cb_outsude) {
        this.cb_outsude = cb_outsude;
    }

    public boolean isCb_credit1() {
        return cb_credit1;
    }

    public void setCb_credit1(boolean cb_credit1) {
        this.cb_credit1 = cb_credit1;
    }

    public boolean isCb_credit2() {
        return cb_credit2;
    }

    public void setCb_credit2(boolean cb_credit2) {
        this.cb_credit2 = cb_credit2;
    }

    public boolean isCb_credit3() {
        return cb_credit3;
    }

    public void setCb_credit3(boolean cb_credit3) {
        this.cb_credit3 = cb_credit3;
    }

    public boolean isCb_credit4() {
        return cb_credit4;
    }

    public void setCb_credit4(boolean cb_credit4) {
        this.cb_credit4 = cb_credit4;
    }

    public boolean isCb_credit5() {
        return cb_credit5;
    }

    public void setCb_credit5(boolean cb_credit5) {
        this.cb_credit5 = cb_credit5;
    }

    public int getMinMonth() {
        return minMonth;
    }

    public void setMinMonth(int minMonth) {
        this.minMonth = minMonth;
    }

    public int getMaxMonth() {
        return maxMonth;
    }

    public void setMaxMonth(int maxMonth) {
        this.maxMonth = maxMonth;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
