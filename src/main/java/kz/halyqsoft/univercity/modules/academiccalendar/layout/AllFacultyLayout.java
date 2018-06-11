package kz.halyqsoft.univercity.modules.academiccalendar.layout;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.modules.academiccalendar.item.AbstractACItem;
import kz.halyqsoft.univercity.modules.academiccalendar.item.FourDateItem;
import kz.halyqsoft.univercity.modules.academiccalendar.item.OneDateItem;
import kz.halyqsoft.univercity.modules.academiccalendar.item.TwoDateItem;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Oct 26, 2016 9:36:10 AM
 */
@SuppressWarnings("serial")
public final class AllFacultyLayout extends VerticalLayout implements AcademicCalendarLayout {

    private final ACADEMIC_CALENDAR academicCalendar;
    private List<AbstractACItem> itemList = new ArrayList<>();
    private ACADEMIC_CALENDAR_DETAIL acd;

    public AllFacultyLayout(ACADEMIC_CALENDAR academicCalendar) {
        this.academicCalendar = academicCalendar;
    }

    @Override
    public void initLayout() throws Exception {
        setWidth(70, Unit.PERCENTAGE);
        setSpacing(true);
        setMargin(new MarginInfo(true, false, false, false));

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);

        Label l = new Label(CommonUtils.getUILocaleUtil().getCaption("events.duration"));
        l.setSizeUndefined();
        l.addStyleName("bold-16");
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        l = new Label(CommonUtils.getUILocaleUtil().getCaption("period"));
        l.setSizeUndefined();
        l.addStyleName("bold-16");
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        addComponent(hl);

        QueryModel<ACADEMIC_CALENDAR_DETAIL> acdQM = new QueryModel<>(ACADEMIC_CALENDAR_DETAIL.class);
        acdQM.addWhere("academicCalendar", ECriteria.EQUAL, academicCalendar.getId());
        acdQM.addOrder("academicCalendarItem");
        List<ACADEMIC_CALENDAR_DETAIL> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(acdQM);

        List<ACADEMIC_CALENDAR_DETAIL> acdList = new ArrayList<ACADEMIC_CALENDAR_DETAIL>();
        for (ACADEMIC_CALENDAR_DETAIL acd : tmpList) {
            acdList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(ACADEMIC_CALENDAR_DETAIL.class, acd.getId()));
        }

        acd = get(acdList, "a");

        addTwoLabel(acdList, "b", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "c", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "d", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "e", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "f", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()), Alignment.MIDDLE_RIGHT, false);

        int beginYear = academicCalendar.getYear().getBeginYear() + 1;
        int endYear = academicCalendar.getYear().getEndYear() + 1;
        addTwoLabel(acdList, "h", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                beginYear + "-" + endYear), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "i", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "j", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "k", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "l", CommonUtils.getUILocaleUtil().getCaption("examination.session") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "m", CommonUtils.getUILocaleUtil().getCaption("examination.session") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "n", CommonUtils.getUILocaleUtil().getCaption("winter.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "o", CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "p", CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "q", CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "r", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "s", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "t", CommonUtils.getUILocaleUtil().getCaption("undergraduate.practice") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "u", CommonUtils.getUILocaleUtil().getCaption("undergraduate.practice") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "v", CommonUtils.getUILocaleUtil().getCaption("undergraduate.defense") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "w", CommonUtils.getUILocaleUtil().getCaption("undergraduate.defense") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "x", CommonUtils.getUILocaleUtil().getCaption("state.exam") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "y", CommonUtils.getUILocaleUtil().getCaption("state.exam") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "z", CommonUtils.getUILocaleUtil().getCaption("diploma.defense") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "0", CommonUtils.getUILocaleUtil().getCaption("diploma.defense") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "1", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().getEndYear()), false);

        addTwoLabel(acdList, "2", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "3", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "4", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "5", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "6", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "7", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "8", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "9", CommonUtils.getUILocaleUtil().getCaption("summer.semester") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "10", CommonUtils.getUILocaleUtil().getCaption("summer.semester") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, null, CommonUtils.getUILocaleUtil().getCaption("summer.semester.bs") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        hl = new HorizontalLayout();
        hl.setSizeFull();
        l = new Label(CommonUtils.getUILocaleUtil().getCaption("fall.semester").toUpperCase());
        l.setSizeUndefined();
        l.addStyleName("bold-16");
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        addComponent(hl);
        acd = get(acdList, "11");

        addTwoLabel(acdList, "12", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "13", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "14", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "15", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "16", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "17", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().getEndYear()), false);

        addTwoLabel(acdList, "18", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "19", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addOneLabel(acdList, "20", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addTwoLabel(acdList, "21", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "22", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "23", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "24", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "25", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "26", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "27", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "28", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "29", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addTwoLabel(acdList, "30", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "31", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "32", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addOneLabel(acdList, "33", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addOneLabel(acdList, "34", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addTwoLabel(acdList, "35", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "36", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addTwoLabel(acdList, "37", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, true);

        addTwoLabel(acdList, "38", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, null, acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        hl = new HorizontalLayout();
        hl.setSizeFull();
        l = new Label(CommonUtils.getUILocaleUtil().getCaption("spring.semester").toUpperCase());
        l.setSizeUndefined();
        l.addStyleName("bold-16");
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        addComponent(hl);
        acd = get(acdList, "39");

        addOneLabel(acdList, "40", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addFourLabel(acdList, "41", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()));

        addTwoLabel(acdList, "42", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "43", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addTwoLabel(acdList, "44", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "45", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "46", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "47", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "48", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "49", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                beginYear + "-" + endYear), Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "50", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "51", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addOneLabel(acdList, "52", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addTwoLabel(acdList, "53", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "54", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, true);

        addTwoLabel(acdList, "55", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "56", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().getEndYear()), false);

        addTwoLabel(acdList, "57", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "58", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addOneLabel(acdList, "59", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addTwoLabel(acdList, "60", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "61", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addOneLabel(acdList, "62", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addOneLabel(acdList, "63", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addOneLabel(acdList, "64", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", false);

        addFourLabel(acdList, "65", String.format(acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                academicCalendar.getYear().toString()));

        addTwoLabel(acdList, "66", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "67", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "68", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "69", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "70", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "71", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "72", acd != null ? acd.getAcademicCalendarItem().getItemName() : "",
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "73", CommonUtils.getUILocaleUtil().getCaption("summer.semester") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "74", CommonUtils.getUILocaleUtil().getCaption("summer.semester") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addOneLabel(acdList, "75", acd != null ? acd.getAcademicCalendarItem().getItemName() : "", true);

        addTwoLabel(acdList, "76", CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, "77", CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);

        addTwoLabel(acdList, null, CommonUtils.getUILocaleUtil().getCaption("summer.break") + ": "
                        + (acd != null ? acd.getAcademicCalendarItem().getItemName() : ""),
                Alignment.MIDDLE_RIGHT, false);
    }

    private void addFourLabel(List<ACADEMIC_CALENDAR_DETAIL> acdList, String type, String format) throws Exception {
        HorizontalLayout hl;
        Label l;
        FourDateItem fdi;
        AbstractComponent ac;
        hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);
        hl.addStyleName("academ-calendar-item");
        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(format);
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
        fdi = new FourDateItem(acd);
        fdi.setCaption1(CommonUtils.getUILocaleUtil().getCaption("filing") + ": ");
        fdi.setCaption2(CommonUtils.getUILocaleUtil().getCaption("result") + ": ");
        itemList.add(fdi);
        ac = fdi.getComponent();
        hl.addComponent(ac);
        hl.setComponentAlignment(ac, Alignment.MIDDLE_RIGHT);
        addComponent(hl);
        acd = get(acdList, type);
    }

    private void addTwoLabel(List<ACADEMIC_CALENDAR_DETAIL> acdList, String next, String value,
                             Alignment alignment, boolean addErrorStyle) throws Exception {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
//        hl.setWidth(1000, Unit.PIXELS);
        hl.setSpacing(true);
        hl.addStyleName("academ-calendar-item");
        Label l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        if (addErrorStyle) {
            l.addStyleName("error");
        }
        l.setValue(value);
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
        TwoDateItem tdi = new TwoDateItem(acd);
        itemList.add(tdi);
        AbstractComponent ac = tdi.getComponent();
        hl.addComponent(ac);
        hl.setComponentAlignment(ac, alignment);
        addComponent(hl);
        if (next != null) {
            acd = get(acdList, next);
        }
    }

    private void addOneLabel(List<ACADEMIC_CALENDAR_DETAIL> acdList, String next1, String newStringValue,
                             boolean addErrorStyle) throws Exception {
        HorizontalLayout hl;
        Label l;
        OneDateItem odi;
        AbstractComponent ac;
        hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);
        hl.addStyleName("academ-calendar-item");
        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        if (addErrorStyle) {
            l.addStyleName("error");
        }
        l.setValue(newStringValue);
        hl.addComponent(l);
        hl.setComponentAlignment(l, Alignment.MIDDLE_LEFT);
        odi = new OneDateItem(acd);
        itemList.add(odi);
        ac = odi.getComponent();
        hl.addComponent(ac);
        hl.setComponentAlignment(ac, Alignment.MIDDLE_RIGHT);
        addComponent(hl);
        acd = get(acdList, next1);
    }

    private ACADEMIC_CALENDAR_DETAIL get(List<ACADEMIC_CALENDAR_DETAIL> acdList, String type) {
        ACADEMIC_CALENDAR_DETAIL acd = null;
        for (ACADEMIC_CALENDAR_DETAIL acd1 : acdList) {
            if (acd1.getAcademicCalendarItem().getItemType().equals(type)) {
                acd = acd1;
                break;
            }
        }

        return acd;
    }

    @Override
    public AbstractLayout getLayout() {
        return this;
    }

    @Override
    public List<AbstractACItem> getItems() {
        return itemList;
    }

    @Override
    public void save() throws Exception {
        for (AbstractACItem item : itemList) {
            item.check();
            item.save();
        }
    }
}
