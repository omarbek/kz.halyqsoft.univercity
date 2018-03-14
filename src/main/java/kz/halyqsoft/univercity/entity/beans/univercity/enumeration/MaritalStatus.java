package kz.halyqsoft.univercity.entity.beans.univercity.enumeration;

import org.r3a.common.vaadin.AbstractWebUI;

public enum MaritalStatus {

    NOT_MARRIED(1, getCaption("maritalstatus.not.married")),
    DIVORCED(2, getCaption("maritalstatus.divorced")),
    MARRIED(3, getCaption("maritalstatus.married")),
    CIVIL_MARRIAGE(4, getCaption("maritalstatus.in.a.civil.marriage"));

    private static String getCaption(String key) {
        return AbstractWebUI.getInstance().getUILocaleUtil().getCaption(key);
    }

    private int id;
    private String name;

    MaritalStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
