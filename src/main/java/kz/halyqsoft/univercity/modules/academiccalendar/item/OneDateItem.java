package kz.halyqsoft.univercity.modules.academiccalendar.item;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;

import java.util.Date;

/**
 * @author Omarbek Dinassil
 * @created Oct 28, 2016 10:25:28 AM
 */
public final class OneDateItem extends AbstractACItem {

    private DateField df1;

    public OneDateItem(ACADEMIC_CALENDAR_DETAIL academicCalendarDetail) {
        super(academicCalendarDetail);
    }

    @Override
    public AbstractComponent getComponent() throws Exception {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);

        Label l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(CommonUtils.getUILocaleUtil().getCaption("date.format.ru"));
        hl.addComponent(l);

        df1 = new DateField();
        df1.setWidth(80, Unit.PIXELS);
        ACADEMIC_CALENDAR_DETAIL academicCalendarDetail = getAcademicCalendarDetail();
        if (academicCalendarDetail != null) {
            df1.setValue(academicCalendarDetail.getDate1());
        }
        hl.addComponent(df1);

        if (isDescrVisible()) {
            hl.addComponent(getDescrField());
        }

        return hl;
    }

    @Override
    public void save() throws Exception {
        Date date1 = df1.getValue();

        ACADEMIC_CALENDAR_DETAIL acd = getAcademicCalendarDetail();
        if (!date1.equals(acd.getDate1())) {
            acd.setDate1(date1);
            if (isDescrVisible()) {
                acd.setDescr(getDescrField().getValue());
            }
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(acd);
        }
    }

    @Override
    public void check() throws Exception {
        if (df1.getValue() == null) {
            showIncorrect();
            throw new Exception();
        }
    }

    public Date getDate1() {
        return df1.getValue();
    }
}
