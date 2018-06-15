package kz.halyqsoft.univercity.modules.curriculum.individual.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VIndividualCurriculumPrint;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.utility.DateUtils;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.table.column.LineBreakTableColumn;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek Dinassil
 * @created Mar 28, 2017 4:18:46 PM
 */
@SuppressWarnings({"serial", "unchecked"})
final class PrintView extends AbstractFormWidgetView {

    private static final String EMPTY_SIGN = "_____________________";
    private final VStudent student;
    private final FStudentFilter filter;

    public PrintView(VStudent student, FStudentFilter filter) {
        super();
        this.student = student;
        this.filter = filter;
    }

    @Override
    public String getViewName() {
        return "printView";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return null;
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        addStyleName("widget-panel");

        CustomLayout printCL = new CustomLayout("ic-print-layout");
        printCL.setSizeFull();

        Embedded logo = new Embedded();
        logo.setHeight(79, Unit.PIXELS);
        logo.setWidth(343, Unit.PIXELS);
        logo.setSource(new ThemeResource("img/book.png"));//TODO
        printCL.addComponent(logo, "logo");

        Label l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("application.title", new Locale("ru", "RU")));
        printCL.addComponent(l, "appname1");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("application.title", new Locale("en", "US")));
        printCL.addComponent(l, "appname2");

        l = new Label();
        l.setWidthUndefined();
        l.setContentMode(ContentMode.HTML);
        l.addStyleName("text-center");
        l.setValue("university address");//TODO
        printCL.addComponent(l, "address");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("ic-print-title");
        l.setValue(getUILocaleUtil().getCaption("individual.curriculum.print"));
        printCL.addComponent(l, "ic-title");

        String sql = "select b.CODE, trim(b.LAST_NAME||' '||substr(b.FIRST_NAME, 1, 2)||'. '||(case when b.MIDDLE_NAME is null then '' else substr(b.MIDDLE_NAME, 1, 2)||'.' end)) FIO, e.STUDY_YEAR, f.DEPT_NAME FACULTY, g.SPEC_NAME from STUDENT a inner join USER b on a.ID = b.ID inner join STUDENT_EDUCATION d on a.ID = d.STUDENT_ID and d.CHILD_ID is null inner join STUDY_YEAR e on d.STUDY_YEAR_ID = e.ID inner join DEPARTMENT f on d.FACULTY_ID = f.ID inner join SPECIALITY g on d.SPECIALITY_ID = g.ID where a.ID = ?1";
        Map<Integer, Object> params = new HashMap<Integer, Object>(1);
        params.put(1, student.getId().getId());
        Object[] o = (Object[]) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        if (semesterData.getSemesterPeriod().getId().getId().intValue() > 2) {
            QueryModel<SEMESTER_DATA> sdQM = new QueryModel<SEMESTER_DATA>(SEMESTER_DATA.class);
            sdQM.addWhere("year", ECriteria.EQUAL, semesterData.getYear().getId());
            sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, ID.valueOf(2));
            semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
        }

        QueryModel<SEMESTER> semesterQM = new QueryModel<SEMESTER>(SEMESTER.class);
        FromItem semesterFI = semesterQM.addJoin(EJoin.INNER_JOIN, "studyYear", STUDY_YEAR.class, "id");
        semesterQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterData.getSemesterPeriod().getId());
        semesterQM.addWhereAnd(semesterFI, "studyYear", ECriteria.EQUAL, ((BigDecimal) o[2]).intValue());
        SEMESTER semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterQM);

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("student.fio"));
        printCL.addComponent(l, "student-fio-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue((String) o[1]);
        printCL.addComponent(l, "student-fio");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("specialty3"));
        printCL.addComponent(l, "specialty-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue((String) o[4]);
        printCL.addComponent(l, "specialty");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("student.id"));
        printCL.addComponent(l, "student-code-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue((String) o[0]);
        printCL.addComponent(l, "student-code");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("semester"));
        printCL.addComponent(l, "semester-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(semester.getId().toString());
        printCL.addComponent(l, "semester");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("faculty"));
        printCL.addComponent(l, "faculty-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue((String) o[3]);
        printCL.addComponent(l, "faculty");

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("study.year.2"));
        printCL.addComponent(l, "study-year-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(((BigDecimal) o[2]).toString());
        printCL.addComponent(l, "study-year");

        sql = "select distinct f.CODE SUBJECT_CODE, f.NAME_KZ SUBJECT_NAME_KZ, f.NAME_EN SUBJECT_NAME_EN, f.NAME_RU SUBJECT_NAME_RU," + " decode(f.MANDATORY, 1, 'A', 'B') SUBJECT_STATUS, g.CYCLE_SHORT_NAME, h.CREDIT," + " i.LAST_NAME||' '||substr(i.FIRST_NAME, 1, 2)||'.'||(decode(i.MIDDLE_NAME, null, '', substr(i.MIDDLE_NAME, 1, 2)||'.')) TEACHER_FIO" + " from STUDENT_SCHEDULE a inner join STUDENT_EDUCATION b on a.STUDENT_ID = b.ID" + " inner join SCHEDULE_DETAIL c on a.SCHEDULE_DETAIL_ID = c.ID" + " inner join SCHEDULE d on c.SCHEDULE_ID = d.ID" + " inner join SEMESTER_SUBJECT e on c.SUBJECT_ID = e.ID" + " inner join SUBJECT f on e.SUBJECT_ID = f.ID" + " inner join SUBJECT_CYCLE g on f.SUBJECT_CYCLE_ID = g.ID" + " inner join CREDITABILITY h on f.CREDITABILITY_ID = h.ID" + " inner join USER i on c.TEACHER_ID = i.ID" + " where a.DELETED = ?1 and b.STUDENT_ID = ?2 and d.SEMESTER_DATA_ID = ?3";
        params.put(1, 0);
        params.put(2, student.getId().getId());
        params.put(3, semesterData.getId().getId());
        //		params.put(3, semesterData.getId().getId().subtract(BigInteger.ONE)); //TODO: debug
        List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

        int totalCredit = 0;
        int i = 1;
        List<VIndividualCurriculumPrint> dataList = new ArrayList<VIndividualCurriculumPrint>(tmpList.size());
        for (Object o1 : tmpList) {
            Object[] oo = (Object[]) o1;
            VIndividualCurriculumPrint icp = new VIndividualCurriculumPrint();
            icp.setRecordNo(i++);
            icp.setSubjectCode((String) oo[0]);
            icp.setSubjectNameKZ((String) oo[1]);
            icp.setSubjectNameEN((String) oo[2]);
            icp.setSubjectNameRU((String) oo[3]);
            icp.setSubjectStatus(((String) oo[4]).charAt(0));
            icp.setCycleShortName((String) oo[5]);
            icp.setCredit(((BigDecimal) oo[6]).intValue());
            icp.setTeacherFIO((String) oo[7]);
            totalCredit += icp.getCredit();
            dataList.add(icp);
        }

        Table dataTable = new Table();
        dataTable.setEditable(false);
        dataTable.setColumnCollapsingAllowed(false);
        dataTable.setSelectable(false);
        dataTable.setImmediate(false);
        dataTable.setSortEnabled(false);
        dataTable.setPageLength(dataList.size());
        dataTable.setColumnReorderingAllowed(false);
        dataTable.setFooterVisible(true);
        dataTable.addStyleName("ic-print-data-table");
        dataTable.setColumnHeader("recordNo", getUILocaleUtil().getCaption("number"));
        dataTable.setColumnWidth("recordNo", 30);
        dataTable.setColumnAlignment("recordNo", Table.Align.RIGHT);
        dataTable.setColumnHeader("subjectCode", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectCode"));
        dataTable.setColumnWidth("subjectCode", 120);
        dataTable.setColumnAlignment("subjectCode", Table.Align.LEFT);
        dataTable.setColumnHeader("subjectNameRU", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectName"));
        dataTable.setColumnWidth("subjectNameRU", 420);
        dataTable.setColumnAlignment("subjectNameRU", Table.Align.LEFT);
        dataTable.setColumnHeader("subjectStatus", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectStatus"));
        dataTable.setColumnWidth("subjectStatus", 60);
        dataTable.setColumnAlignment("subjectStatus", Table.Align.CENTER);
        dataTable.setColumnHeader("cycleShortName", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "cycleShortName"));
        dataTable.setColumnWidth("cycleShortName", 50);
        dataTable.setColumnAlignment("cycleShortName", Table.Align.CENTER);
        dataTable.setColumnHeader("credit", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "credit"));
        dataTable.setColumnWidth("credit", 60);
        dataTable.setColumnAlignment("credit", Table.Align.RIGHT);
        dataTable.setColumnHeader("teacherFIO", getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "teacherFIO"));
        dataTable.setColumnWidth("teacherFIO", 150);
        dataTable.setColumnAlignment("teacherFIO", Table.Align.LEFT);
        dataTable.setColumnFooter("subjectNameRU", getUILocaleUtil().getCaption("total.credit.sum.semester"));
        dataTable.setColumnFooter("credit", String.valueOf(totalCredit));
        dataTable.addGeneratedColumn("subjectNameRU", new LineBreakTableColumn());
        BeanItemContainer<VIndividualCurriculumPrint> dataBIC = new BeanItemContainer<>(VIndividualCurriculumPrint.class, dataList);
        dataTable.setContainerDataSource(dataBIC);
        dataTable.setVisibleColumns("recordNo", "subjectCode", "subjectNameRU", "subjectStatus", "cycleShortName", "credit", "teacherFIO");

        printCL.addComponent(dataTable, "data-table");

        getContent().addComponent(printCL);
        getContent().setComponentAlignment(printCL, Alignment.MIDDLE_CENTER);

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("subject.a"));
        printCL.addComponent(l, "status-a");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("subject.b"));
        printCL.addComponent(l, "status-b");

        params.clear();
        sql = "select a.SIGNATURE_TYPE_ID, c.LAST_NAME||' '||substr(c.FIRST_NAME, 1, 2)||'.'||decode(c.MIDDLE_NAME, null, '', substr(c.MIDDLE_NAME, 1, 2)||'.') SIGN_FIO from REGISTRATION_SIGNATURE a inner join STUDENT_EDUCATION b on a.STUDENT_ID = b.ID inner join USER c on a.SIGN_USER_ID = c.ID where a.SEMESTER_DATA_ID = ?1 and b.STUDENT_ID = ?2";
        params.put(1, semesterData.getId().getId());
        params.put(2, student.getId().getId());
        List<Object> signList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

        String studentSign = null;
        String adviserSign = null;
        String chairChiefSign = null;
        String facultyChiefSign = null;

        if (!signList.isEmpty()) {
            studentSign = getSign(signList, 1);
            adviserSign = getSign(signList, 2);
            chairChiefSign = getSign(signList, 3);
            facultyChiefSign = getSign(signList, 4);
        }

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("student"));
        printCL.addComponent(l, "student-caption");

        l = new Label();
        l.setWidthUndefined();
        if (studentSign != null) {
            l.setValue(studentSign);
        } else {
            l.setValue(EMPTY_SIGN);
        }
        printCL.addComponent(l, "student-sign");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("adviser"));
        printCL.addComponent(l, "adviser-caption");

        l = new Label();
        l.setWidthUndefined();
        if (adviserSign != null) {
            l.setValue(adviserSign);
        } else {
            l.setValue(EMPTY_SIGN);
        }
        printCL.addComponent(l, "adviser-sign");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("chair.head"));
        printCL.addComponent(l, "chair-chief-caption");

        l = new Label();
        l.setWidthUndefined();
        if (chairChiefSign != null) {
            l.setValue(chairChiefSign);
        } else {
            l.setValue(EMPTY_SIGN);
        }
        printCL.addComponent(l, "chair-chief-sign");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("faculty.head"));
        printCL.addComponent(l, "faculty-chief-caption");

        l = new Label();
        l.setWidthUndefined();
        if (facultyChiefSign != null) {
            l.setValue(facultyChiefSign);
        } else {
            l.setValue(EMPTY_SIGN);
        }
        printCL.addComponent(l, "faculty-chief-sign");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(getUILocaleUtil().getCaption("registration.date"));
        printCL.addComponent(l, "reg-date-caption");

        l = new Label();
        l.setWidthUndefined();
        l.setValue(new SimpleDateFormat(DateUtils.LONG_FORMAT).format(new Date()));
        printCL.addComponent(l, "reg-date");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        Button b = new NativeButton();
        b.setCaption(getUILocaleUtil().getCaption("print"));
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                JavaScript.getCurrent().execute("document.title = \"\";");
                JavaScript.getCurrent().execute("print();");
            }
        });
        hl.addComponent(b);

        b = new NativeButton();
        b.setCaption(getUILocaleUtil().getCaption("back"));
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
//				CurriculumUI.getInstance().openCommonView(new StudentList(filter));//TODO
            }
        });
        hl.addComponent(b);

        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.BOTTOM_CENTER);
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;//TODO
    }

    private static String getSign(List<Object> signList, int signType) {
        String sign = null;
        for (Object o : signList) {
            Object[] oo = (Object[]) o;
            if ((long) oo[0] == signType) {
                sign = (String) oo[1];
                break;
            }
        }

        return sign;
    }
}
