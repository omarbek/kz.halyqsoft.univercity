package kz.halyqsoft.univercity.modules.academiccalendar.item;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.widget.dialog.Message;

/**
 * @author Omarbek Dinassil
 * @created Oct 27, 2016 10:54:55 AM
 */
public abstract class AbstractACItem {

    private final ACADEMIC_CALENDAR_DETAIL academicCalendarDetail;
    private TextField descrField;
    private boolean descrVisible;

    public AbstractACItem(ACADEMIC_CALENDAR_DETAIL academicCalendarDetail) {
        this.academicCalendarDetail = academicCalendarDetail;
    }

    public final ACADEMIC_CALENDAR_DETAIL getAcademicCalendarDetail() {
        return academicCalendarDetail;
    }

    protected final TextField getDescrField() {
        if (descrField == null) {
            descrField = new TextField();
            descrField.setWidth(194, Unit.PIXELS);
            descrField.setNullRepresentation("");
            descrField.setValue(academicCalendarDetail!=null?academicCalendarDetail.getDescr():"");
        }

        return descrField;
    }

    public boolean isDescrVisible() {
        return descrVisible;
    }

    public void setDescrVisible(boolean descrVisible) {
        this.descrVisible = descrVisible;
    }

    protected final void showIncorrect() {
        String message = String.format(CommonUtils.getUILocaleUtil().getMessage("filled.incorrectly"), academicCalendarDetail.getAcademicCalendarItem().getItemName());
        Message.showError(message);
    }

    public abstract AbstractComponent getComponent() throws Exception;

    public abstract void save() throws Exception;

    public abstract void check() throws Exception;
}
