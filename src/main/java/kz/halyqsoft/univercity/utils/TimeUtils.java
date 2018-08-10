package kz.halyqsoft.univercity.utils;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TIME;
import org.r3a.common.vaadin.widget.dialog.Message;

/**
 * @author Omarbek
 * @created on 01.08.2018
 */
public class TimeUtils {

    private boolean error;
    private String minutes;
    private String hours;

    public TimeUtils(TIME time) {
        String timeName = time.getTimeName();
        String symbol = timeName.substring(timeName.length() - 3, timeName.length() - 2);
        if (!(symbol.equals(":") || symbol.equals("-"))) {
            Message.showInfo("please fill time with definite format");//TODO
            error = true;
            return;
        }
        if (timeName.length() == 4) {
            timeName = "0" + timeName;
        }
        minutes = timeName.substring(timeName.lastIndexOf(symbol) + 1);
        hours = timeName.substring(0, timeName.lastIndexOf(symbol));
        error = false;
    }

    public boolean isError() {
        return error;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getHours() {
        return hours;
    }
}
