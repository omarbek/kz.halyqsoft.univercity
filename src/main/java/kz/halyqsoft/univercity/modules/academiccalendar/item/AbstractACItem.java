package kz.halyqsoft.univercity.modules.academiccalendar.item;

import com.vaadin.ui.AbstractComponent;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.widget.dialog.Message;

/**
 * @author Omarbek Dinassil
 * @created Oct 27, 2016 10:54:55 AM
 */
public abstract class AbstractACItem {

    private final ACADEMIC_CALENDAR_DETAIL academicCalendarDetail;

    public AbstractACItem(ACADEMIC_CALENDAR_DETAIL academicCalendarDetail) {
        this.academicCalendarDetail = academicCalendarDetail;
    }

    public final ACADEMIC_CALENDAR_DETAIL getAcademicCalendarDetail() {
        return academicCalendarDetail;
    }

    protected final void showIncorrect() {
        String message = String.format(CommonUtils.getUILocaleUtil().getMessage("filled.incorrectly"), academicCalendarDetail.getAcademicCalendarItem().getItemName());
        Message.showError(message);
    }

    public abstract AbstractComponent getComponent() throws Exception;

    public abstract void save() throws Exception;

    public abstract void check() throws Exception;
}
