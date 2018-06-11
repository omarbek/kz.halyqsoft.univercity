package kz.halyqsoft.univercity.modules.academiccalendar.item;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * @author Omarbek Dinassil
 * @created Oct 27, 2016 10:58:06 AM
 */
public final class TwoDateItem extends AbstractACItem {

    private DateField df1;
    private DateField df2;

    public TwoDateItem(ACADEMIC_CALENDAR_DETAIL academicCalendarDetail) {
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

        return hl;
    }

    @Override
    public void save() throws Exception {
        Date date1 = df1.getValue();
        Date date2 = df2.getValue();

        ACADEMIC_CALENDAR_DETAIL acd = getAcademicCalendarDetail();
        if (!date1.equals(acd.getDate1()) || !date2.equals(acd.getDate2())) {
            acd.setDate1(date1);
            acd.setDate2(date2);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(acd);
        }

        if (acd.getAcademicCalendarItem().getItemType().equals("i") || acd.getAcademicCalendarItem().getItemType().equals("j")) {
            QueryModel<SEMESTER_DATA> sdQM = new QueryModel<SEMESTER_DATA>(SEMESTER_DATA.class);
            sdQM.addWhere("year", ECriteria.EQUAL, acd.getAcademicCalendar().getYear().getId());
            if (acd.getAcademicCalendarItem().getItemType().equals("i")) {
                sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, ID.valueOf(1));
            } else {
                sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, ID.valueOf(2));
            }

            SEMESTER_DATA sd;
            try {
                sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
            } catch (NoResultException e) {
                sd = null;
            }

            if (sd != null && (!sd.getBeginDate().equals(date1) || !sd.getEndDate().equals(date2))) {
                sd.setBeginDate(date1);
                sd.setEndDate(date2);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sd);
            }
        }
    }

    @Override
    public void check() throws Exception {
        if (df1.getValue() == null || df2.getValue() == null) {
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
}
