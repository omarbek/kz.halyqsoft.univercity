package kz.halyqsoft.univercity.modules.academiccalendar.item;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;

import java.util.Date;

/**
 * @author Omarbek Dinassil
 * @created Oct 28, 2016 11:36:32 AM
 */
public final class FourDateItem extends AbstractACItem {

    private DateField df1;
    private DateField df2;
    private DateField df3;
    private DateField df4;
    private String caption1;
    private String caption2;

    public FourDateItem(ACADEMIC_CALENDAR_DETAIL academicCalendarDetail) {
        super(academicCalendarDetail);
    }

    public String getCaption1() {
        return caption1;
    }

    public void setCaption1(String caption1) {
        this.caption1 = caption1;
    }

    public String getCaption2() {
        return caption2;
    }

    public void setCaption2(String caption2) {
        this.caption2 = caption2;
    }

    @Override
    public AbstractComponent getComponent() throws Exception {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setWidthUndefined();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);

        if (caption1 != null) {
            Label l1 = new Label();
            l1.setSizeUndefined();
            l1.addStyleName("bold");
            l1.setValue(caption1);
            hl.addComponent(l1);
        }

        Label l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(CommonUtils.getUILocaleUtil().getCaption("date.format.ru"));
        hl.addComponent(l);

        df1 = new DateField();
        df1.setWidth(100, Unit.PIXELS);
        ACADEMIC_CALENDAR_DETAIL academicCalendarDetail = getAcademicCalendarDetail();
        if (academicCalendarDetail != null) {
            df1.setValue(academicCalendarDetail.getDate1());
        }
        hl.addComponent(df1);

        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(" - ");
        hl.addComponent(l);

        df2 = new DateField();
        df2.setWidth(100, Unit.PIXELS);
        if (academicCalendarDetail != null) {
            df2.setValue(academicCalendarDetail.getDate2());
        }
        hl.addComponent(df2);

        vl.addComponent(hl);

        hl = new HorizontalLayout();
        hl.setSpacing(true);

        if (caption2 != null) {
            Label l2 = new Label();
            l2.setSizeUndefined();
            l2.addStyleName("bold");
            l2.setValue(caption2);
            hl.addComponent(l2);
        }

        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(CommonUtils.getUILocaleUtil().getCaption("date.format.ru"));
        hl.addComponent(l);

        df3 = new DateField();
        df3.setWidth(100, Unit.PIXELS);
        if (academicCalendarDetail != null) {
            df3.setValue(academicCalendarDetail.getDate3());
        }
        hl.addComponent(df3);

        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(" - ");
        hl.addComponent(l);

        df4 = new DateField();
        df4.setWidth(100, Unit.PIXELS);
        if (academicCalendarDetail != null) {
            df4.setValue(academicCalendarDetail.getDate4());
        }
        hl.addComponent(df4);
        vl.addComponent(hl);

        return vl;
    }

    @Override
    public void save() throws Exception {
        Date date1 = df1.getValue();
        Date date2 = df2.getValue();
        Date date3 = df3.getValue();
        Date date4 = df4.getValue();

        ACADEMIC_CALENDAR_DETAIL acd = getAcademicCalendarDetail();
        if (!date1.equals(acd.getDate1()) || !date2.equals(acd.getDate2()) || !date3.equals(acd.getDate3()) || !date4.equals(acd.getDate4())) {
            acd.setDate1(date1);
            acd.setDate2(date2);
            acd.setDate3(date3);
            acd.setDate4(date4);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(acd);
        }
    }

    @Override
    public void check() throws Exception {
        if (df1.getValue() == null || df2.getValue() == null || df3.getValue() == null || df4.getValue() == null) {
            showIncorrect();
            throw new Exception();
        }
    }

    public Date getDate1() {
        return df1.getValue();
    }

    public Date getDate2() {
        return df2.getValue();
    }

    public Date getDate3() {
        return df3.getValue();
    }

    public Date getDate4() {
        return df4.getValue();
    }
}
