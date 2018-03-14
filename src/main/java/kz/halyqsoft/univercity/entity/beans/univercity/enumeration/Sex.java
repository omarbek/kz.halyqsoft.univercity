package kz.halyqsoft.univercity.entity.beans.univercity.enumeration;

import org.r3a.common.vaadin.AbstractWebUI;

public enum Sex {

    MALE(1, getCaption("sex.male")),
    FEMALE(2, getCaption("sex.female"));

    private static String getCaption(String key) {
        return AbstractWebUI.getInstance().getUILocaleUtil().getCaption(key);
    }

    private int id;
    private String name;

    Sex(int id, String name) {
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
