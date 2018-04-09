package kz.halyqsoft.univercity.utils;

import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 13.03.2018
 */
public class ErrorUtils {

    public static final Logger LOG = LoggerFactory.getLogger("ROOT");
    public static int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    public static UILocaleUtil getUILocaleUtil() {
        return AbstractWebUI.getInstance().getUILocaleUtil();
    }

    public static void showSavedNotification() {
        AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
    }
}
