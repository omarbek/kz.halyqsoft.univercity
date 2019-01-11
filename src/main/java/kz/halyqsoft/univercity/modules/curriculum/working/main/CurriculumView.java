package kz.halyqsoft.univercity.modules.curriculum.working.main;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_AFTER_SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.DiplomaTypeChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.EntranceYearChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.SpecialityChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.schedule.SchedulePanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.CreditCountByComponentsPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.SubjectsTab;
import kz.halyqsoft.univercity.modules.regapplicants.IUPS_TYPE;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import javax.persistence.NoResultException;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.Calendar;
import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created 05.11.2018
 */
@SuppressWarnings({"serial"})
public final class CurriculumView extends AbstractTaskView implements EntityListener {

    private CURRICULUM curriculum;
    private TabSheet mainTS;

    public ComboBox specialityCB;
    public ComboBox entranceYearCB;
    public ComboBox diplomaTypeCB;
    private Label statusLabel;
    private String createStatus;

    public Label academicDegreeLabel;
    public Label studyPeriodLabel;
    private final Label errorLabel = new Label();

    private Button createButton;
    private Button conformButton;
    private Button approveButton;

    private Label creditSumLabel;
    private Label totalCreditSumLabel;//in current semester

    private List<SubjectsTab> subjectsTabs;
    private SubjectsTab addProgramPanel;
    private SubjectsTab afterSemesterProgamPanel;
    private CreditCountByComponentsPanel creditCountByComponentsPanel;
    private SchedulePanel schedulePanel;
    private static int fontSize = 7;
    private Document document;
    private PdfPTable pdfPTable;
    private ByteArrayOutputStream byteArrayOutputStream;
    FileDownloader fileDownloaderr = null;

    private static final int NUMBER_OF_YEARS_AHEAD = 1;

    private StreamResource.StreamSource streamSource;


    private StreamResource createResource() {
        String defaultName = "default.pdf";
        return new StreamResource(streamSource, defaultName);
    }

    public CurriculumView(AbstractTask task) throws Exception {
        super(task);
        subjectsTabs = new ArrayList<>();
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        initFilter();
        initLabels();
        initButtons();
        initCreditSum();

        mainTS = new TabSheet();
        initTabs();
        mainTS.addSelectedTabChangeListener(TabChangeListener());
        mainTS.addSelectedTabChangeListener(new CurriculumTabChangeListener());

        getContent().addComponent(mainTS);
        getContent().setComponentAlignment(mainTS, Alignment.MIDDLE_CENTER);
        getContent().setExpandRatio(mainTS, 1);
    }

    private void initFilter() throws Exception {
        HorizontalLayout filterHL = new HorizontalLayout();
        filterHL.setSpacing(true);
        filterHL.setMargin(true);
        filterHL.addStyleName("form-panel");

        Label specialityLabel = new Label();
        specialityLabel.addStyleName("bold");
        specialityLabel.setWidthUndefined();
        specialityLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "speciality"));
        filterHL.addComponent(specialityLabel);

        QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
        specialityQM.addWhere("deleted", Boolean.FALSE);
        specialityQM.addWhere("level", ECriteria.EQUAL, LEVEL.BACHELOR);
        specialityQM.addOrder("specName");
        BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        specialityCB = new ComboBox();
        specialityCB.setContainerDataSource(specialityBIC);
        specialityCB.setImmediate(true);
        specialityCB.setNullSelectionAllowed(true);
        specialityCB.setTextInputAllowed(true);
        specialityCB.setFilteringMode(FilteringMode.STARTSWITH);
        specialityCB.setWidth(400, Unit.PIXELS);
        specialityCB.setPageLength(0);
        filterHL.addComponent(specialityCB);

        Label entranceYearLabel = new Label();
        entranceYearLabel.addStyleName("bold");
        entranceYearLabel.setWidthUndefined();
        entranceYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "entranceYear"));
        filterHL.addComponent(entranceYearLabel);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
        entranceYearQM.addWhere("beginYear", ECriteria.LESS_EQUAL, currentYear + NUMBER_OF_YEARS_AHEAD);
        entranceYearQM.addWhereAnd("endYear", ECriteria.LESS_EQUAL, currentYear + NUMBER_OF_YEARS_AHEAD);
        entranceYearQM.addOrderDesc("beginYear");
        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(
                ENTRANCE_YEAR.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                .lookup(entranceYearQM));
        entranceYearCB = new ComboBox();
        entranceYearCB.setContainerDataSource(entranceYearBIC);
        entranceYearCB.setImmediate(true);
        entranceYearCB.setNullSelectionAllowed(true);
        entranceYearCB.setTextInputAllowed(true);
        entranceYearCB.setFilteringMode(FilteringMode.STARTSWITH);
        filterHL.addComponent(entranceYearCB);

        Label diplomaTypeLabel = new Label();
        diplomaTypeLabel.addStyleName("bold");
        diplomaTypeLabel.setWidthUndefined();
        diplomaTypeLabel.setValue(getUILocaleUtil().getCaption("diploma.type"));
        filterHL.addComponent(diplomaTypeLabel);

        QueryModel<STUDENT_DIPLOMA_TYPE> diplomaTypeQM = new QueryModel<>(STUDENT_DIPLOMA_TYPE.class);
        BeanItemContainer<STUDENT_DIPLOMA_TYPE> diplomaTypeBIC = new BeanItemContainer<>(
                STUDENT_DIPLOMA_TYPE.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(diplomaTypeQM));
        diplomaTypeCB = new ComboBox();
        diplomaTypeCB.setContainerDataSource(diplomaTypeBIC);
        diplomaTypeCB.setImmediate(true);
        diplomaTypeCB.setNullSelectionAllowed(true);
        diplomaTypeCB.setTextInputAllowed(true);
        diplomaTypeCB.setFilteringMode(FilteringMode.STARTSWITH);
        diplomaTypeCB.setWidth(200, Unit.PIXELS);
        diplomaTypeCB.setPageLength(0);
        filterHL.addComponent(diplomaTypeCB);

        statusLabel = new Label();
        statusLabel.addStyleName("bold");
        statusLabel.setWidthUndefined();
        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus"));
        filterHL.addComponent(statusLabel);

        createStatus = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)).getStatusName();

        specialityCB.addValueChangeListener(new SpecialityChangeListener(this));
        entranceYearCB.addValueChangeListener(new EntranceYearChangeListener(this));
        diplomaTypeCB.addValueChangeListener(new DiplomaTypeChangeListener(this));

        getContent().addComponent(filterHL);
        getContent().setComponentAlignment(filterHL, Alignment.TOP_CENTER);
    }

    private void initTabs() throws Exception {
        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
        semesterQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(8));
        semesterQM.addOrder("id");
        List<SEMESTER> semesterList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM);
        for (SEMESTER semester : semesterList) {
            SubjectsTab subjectsTab = new SubjectsTab(this, semester,
                    SubjectsTab.SubjectsType.MAIN_SUBJECTS);//here can be elective subject type
            subjectsTab.setWidth(100, Unit.PERCENTAGE);
            subjectsTab.setCurriculum(curriculum);
            subjectsTab.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
            subjectsTab.initPanel();

            subjectsTabs.add(subjectsTab);
            mainTS.addTab(subjectsTab, getUILocaleUtil().getCaption("semester." + semester.getId().toString()));
        }

        addProgramPanel = new SubjectsTab(this, null, SubjectsTab.SubjectsType.ADDING_SUBJECTS);
        addProgramPanel.setWidth(100, Unit.PERCENTAGE);
        addProgramPanel.setCurriculum(curriculum);
        addProgramPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
        addProgramPanel.initPanel();
        mainTS.addTab(addProgramPanel, getUILocaleUtil().getCaption("add.education.programm"));

        afterSemesterProgamPanel = new SubjectsTab(this, null,
                SubjectsTab.SubjectsType.AFTER_SEMESTER_SUBJECTS);
        afterSemesterProgamPanel.setWidth(100, Unit.PERCENTAGE);
        afterSemesterProgamPanel.setCurriculum(curriculum);
        afterSemesterProgamPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
        afterSemesterProgamPanel.initPanel();
        mainTS.addTab(afterSemesterProgamPanel, getUILocaleUtil().getEntityLabel(
                V_CURRICULUM_AFTER_SEMESTER.class));

        schedulePanel = new SchedulePanel(this);
        schedulePanel.setCurriculum(curriculum);
        schedulePanel.initPanel();
        mainTS.addTab(schedulePanel, getUILocaleUtil().getCaption("curriculum.schedule"));
    }

    private void initCreditSum() {
        creditSumLabel = new Label();
        creditSumLabel.setWidthUndefined();
        creditSumLabel.addStyleName("bold");
        creditSumLabel.addStyleName("margin-left-30");
        creditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("credit.sum"), 0));
        getContent().addComponent(creditSumLabel);
        getContent().setComponentAlignment(creditSumLabel, Alignment.TOP_LEFT);

        totalCreditSumLabel = new Label();
        totalCreditSumLabel.setWidthUndefined();
        totalCreditSumLabel.addStyleName("bold");
        totalCreditSumLabel.addStyleName("margin-left-30");
        totalCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), 0));
        getContent().addComponent(totalCreditSumLabel);
        getContent().setComponentAlignment(totalCreditSumLabel, Alignment.TOP_LEFT);
    }

    private void initButtons() {
        HorizontalLayout toolbarHL = new HorizontalLayout();
        toolbarHL.setSpacing(true);

        createButton = new Button();
        createButton.setEnabled(false);
        createButton.setCaption(getUILocaleUtil().getCaption("create"));
        createButton.setWidth(120, Unit.PIXELS);
        createButton.setIcon(new ThemeResource("img/button/add.png"));
        createButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    create();
                    findCurriculum((SPECIALITY) specialityCB.getValue(),
                            (ENTRANCE_YEAR) entranceYearCB.getValue(),
                            (STUDENT_DIPLOMA_TYPE) diplomaTypeCB.getValue());
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    LOG.error("Unable to save curriculum: ", ex);
                    Message.showInfo(ex.getMessage());
                }
            }
        });
        toolbarHL.addComponent(createButton);

        conformButton = new Button();
        conformButton.setEnabled(false);
        conformButton.setCaption(getUILocaleUtil().getCaption("conform"));
        conformButton.setWidth(120, Unit.PIXELS);
        conformButton.addStyleName("conform");
        conformButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    conform();
                } catch (Exception ex) {
                    LOG.error("Unable to conform curriculum: ", ex);
                    Message.showInfo(ex.getMessage());
                }
            }
        });
        toolbarHL.addComponent(conformButton);

        approveButton = new Button();
        approveButton.setEnabled(false);
        approveButton.setCaption(getUILocaleUtil().getCaption("approve"));
        approveButton.setWidth(120, Unit.PIXELS);
        approveButton.addStyleName("approve");
        approveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    approve();
                } catch (Exception ex) {
                    LOG.error("Unable to approve curriculum: ", ex);
                    String message = ex.getMessage();
                    if (message != null) {
                        Message.showInfo(message);
                    } else {
                        Message.showError(ex.toString());
                    }
                }
            }
        });
        toolbarHL.addComponent(approveButton);

        Button printButton = new Button();
        printButton.setCaption(getUILocaleUtil().getCaption("print"));
        printButton.setWidth(120, Unit.PIXELS);
        printButton.setIcon(new ThemeResource("img/button/printer.png"));
        printButton.addStyleName("print");
        toolbarHL.addComponent(printButton);

        Button printButtonC = new Button("Скачать ГУП");
        toolbarHL.addComponent(printButtonC);

        printButtonC.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String headers[] = {" ","Специальность","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52"};
                tableHeader.addAll(Arrays.asList(headers));
                String sql="";
                for(int k=1; k<=4;k++) {
                     sql = "select *\n" +
                             "from crosstab('select spec.spec_name,w.week_code,css.symbol\n" +
                             "from curriculum_schedule sched\n" +
                             "       inner join curriculum_schedule_symbol css on sched.symbol_id = css.id\n" +
                             "       inner join curriculum c on sched.curriculum_id = c.id\n" +
                             "       inner join speciality spec on c.speciality_id = spec.id\n" +
                             "       inner join week w on sched.week_id = w.id\n" +
                             "where study_year_id ="+k+"',\n" +
                             "              'select m from generate_series(1,52) m') as (\n" +
                             "     spec_name varchar(255),\n" +
                             "     \"1\" varchar(255),\n" +
                             "     \"2\" varchar(255),\n" +
                             "     \"3\" varchar(255),\n" +
                             "     \"4\" varchar(255),\n" +
                             "     \"5\" varchar(255),\n" +
                             "     \"6\" varchar(255),\n" +
                             "     \"7\" varchar(255),\n" +
                             "     \"8\" varchar(255),\n" +
                             "     \"9\" varchar(255),\n" +
                             "     \"10\" varchar(255),\n" +
                             "     \"11\" varchar(255),\n" +
                             "     \"12\" varchar(255),\n" +
                             "     \"13\" varchar(255),\n" +
                             "     \"14\" varchar(255),\n" +
                             "     \"15\" varchar(255),\n" +
                             "     \"16\" varchar(255),\n" +
                             "     \"17\" varchar(255),\n" +
                             "     \"18\" varchar(255),\n" +
                             "     \"19\" varchar(255),\n" +
                             "     \"20\" varchar(255),\n" +
                             "     \"21\" varchar(255),\n" +
                             "     \"22\" varchar(255),\n" +
                             "     \"23\" varchar(255),\n" +
                             "     \"24\" varchar(255),\n" +
                             "     \"25\" varchar(255),\n" +
                             "     \"26\" varchar(255),\n" +
                             "     \"27\" varchar(255),\n" +
                             "     \"28\" varchar(255),\n" +
                             "     \"29\" varchar(255),\n" +
                             "     \"30\" varchar(255),\n" +
                             "     \"31\" varchar(255),\n" +
                             "     \"32\" varchar(255),\n" +
                             "     \"33\" varchar(255),\n" +
                             "     \"34\" varchar(255),\n" +
                             "     \"35\" varchar(255),\n" +
                             "     \"36\" varchar(255),\n" +
                             "     \"37\" varchar(255),\n" +
                             "     \"38\" varchar(255),\n" +
                             "     \"39\" varchar(255),\n" +
                             "     \"40\" varchar(255),\n" +
                             "     \"41\" varchar(255),\n" +
                             "     \"42\" varchar(255),\n" +
                             "     \"43\" varchar(255),\n" +
                             "     \"44\" varchar(255),\n" +
                             "     \"45\" varchar(255),\n" +
                             "     \"46\" varchar(255),\n" +
                             "     \"47\" varchar(255),\n" +
                             "     \"48\" varchar(255),\n" +
                             "     \"49\" varchar(255),\n" +
                             "     \"50\" varchar(255),\n" +
                             "     \"51\" varchar(255),\n" +
                             "     \"52\" varchar(255)\n" +
                             "\n" +
                             "     );";

                    // getFirstMonday(2019, 1);

                    List<String> list = new ArrayList<>();

                    try {
                        List<Object> tmpList = new ArrayList<>();
                        Map<Integer, Object> param = null;
                        tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                        if (!tmpList.isEmpty()) {
                            for (Object o : tmpList) {
                                Object[] oo = (Object[]) o;
                                ArrayList<String> valuesList = new ArrayList();
                                for (int i = 0; i < oo.length; i++) {
                                    valuesList.add(oo[i] != null ? String.valueOf(oo[i]) : "");
                                }
                                tableBody.add(Collections.singletonList("1"));
                                tableBody.add(valuesList);

                            }
                        }
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to load department list", ex);
                    }
                }
                String fileName = "document";

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);

            }
        });

        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        String REPORT = getUILocaleUtil().getCaption("report");

        printButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Document document = new Document();
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                int allCredits = 0, allEcts = 0, allLcount = 0, allPcount = 0, allLbcount = 0, allwtcount = 0, allOcount = 0, allCnt = 0, allexam = 0;
                int creditNum = 0, countadd = 0, fcountadd = 0, ffcountadd = 0, countall = 0;
                int ectsNum = 0, lcount = 0, pcount = 0, lbcount = 0, wtcount = 0, ocount = 0, allcount = 0, examc = 0;
                int fallCredits = 0, fallEcts = 0, fallLcount = 0, fallPcount = 0, fallLbcount = 0, fallwtcount = 0, fallOcount = 0, fallCnt = 0, fallexam = 0;
                int ffallCredits = 0, ffallEcts = 0, ffallLcount = 0, ffallPcount = 0, ffallLbcount = 0, ffallwtcount = 0, ffallOcount = 0, ffallCnt = 0, ffallexam = 0, fin = 0, ffin = 0;
                PdfPTable table = new PdfPTable(15);
                insertCell(table, "Білім беру бағдарламасының оқу жоспары (модульдердің еңбек мөлшері мен меңгерілу ретіне қарай оқу жылдарына бөлінуі)  \n" +
                        "Учебный план образовательной программы (распределение модулей по годам обучения с учетом трудоемкости и порядка освоения)", Element.ALIGN_CENTER, 16, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                String[] headers = new String[]{"Модуль түрлері\nВиды модулей",
                        "Пәндер циклы\nЦикл дисципли" +
                                "н", "Білім беру модулі\nМодуль обучения", "Пәннің коды\nКод дисциплины", "Пәндер", "Дисциплины", "РК", "ECTS", "Дәрістер\nЛекции", "Тәжірибелік/сем.\nПрактические/сем.", "Зертханалық\nЛабораторные", "СОӨЖ\nСРСП", "СӨЖ\nСРС", "Барлық сағат саны\nВсего в часх", "Бақылау формасы\nФорма контроля"};
                try {
                    PdfWriter.getInstance(document, byteArr);
                    document.open();
                    for (String s : headers) {
                        insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }
                    insertCell(table, "1 курс", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    insertCell(table, "1 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    table.setWidthPercentage(100);
                    Paragraph p = new Paragraph("");

                    if (curriculum != null && curriculum.getId() != null) {


                        List<ArrayList<String>> myData = getData(curriculum.getId().getId().intValue(), 1, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }
                        }

                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 1, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;

                        insertCell(table, "Всего за 1 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allcount = 0;
                        allexam = 0;

                        insertCell(table, "2 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 2, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 2, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;
                        ffallCredits += fallCredits;
                        ffallEcts += fallEcts;
                        ffallLcount += fallLcount;
                        ffallPcount += fallPcount;
                        ffallLbcount += fallLbcount;
                        ffallwtcount += fallwtcount;
                        ffallOcount += fallOcount;
                        ffallCnt += fallCnt;
                        ffallexam += fallexam;
                        insertCell(table, "Всего за 2 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                        insertCell(table, "Всего за 1 курс", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;
                        fallCredits = 0;
                        fallEcts = 0;
                        fallLcount = 0;
                        fallPcount = 0;
                        fallLbcount = 0;
                        fallwtcount = 0;
                        fallOcount = 0;
                        fallCnt = 0;
                        fallexam = 0;
                        insertCell(table, "2 курс", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "3 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 3, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 3, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;


                        insertCell(table, "Всего за 3 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;

                        insertCell(table, "4 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 4, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 4, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }
                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;
                        ffallCredits += fallCredits;
                        ffallEcts += fallEcts;
                        ffallLcount += fallLcount;
                        ffallPcount += fallPcount;
                        ffallLbcount += fallLbcount;
                        ffallwtcount += fallwtcount;
                        ffallOcount += fallOcount;
                        ffallCnt += fallCnt;
                        ffallexam += fallexam;

                        insertCell(table, "Всего за 4 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;
                        insertCell(table, "Всего за 2 курс", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        fallCredits = 0;
                        fallEcts = 0;
                        fallLcount = 0;
                        fallPcount = 0;
                        fallLbcount = 0;
                        fallwtcount = 0;
                        fallOcount = 0;
                        fallCnt = 0;
                        allexam = 0;
                        fallexam = 0;
                        insertCell(table, "3 курс", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "5 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 5, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 5, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;

                        insertCell(table, "Всего за 5 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;

                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        examc = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        allexam = 0;


                        insertCell(table, "6 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 6, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 6, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;

                        insertCell(table, "Всего за 6 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += examc;
                        ffallCredits += fallCredits;
                        ffallEcts += fallEcts;
                        ffallLcount += fallLcount;
                        ffallPcount += fallPcount;
                        ffallLbcount += fallLbcount;
                        ffallwtcount += fallwtcount;
                        ffallOcount += fallOcount;
                        ffallCnt += fallCnt;
                        ffallexam += fallexam;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;

                        insertCell(table, "Всего за 3 курс", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        fallCredits = 0;
                        fallEcts = 0;
                        fallLcount = 0;
                        fallPcount = 0;
                        fallLbcount = 0;
                        fallwtcount = 0;
                        fallOcount = 0;
                        fallCnt = 0;
                        fallexam = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;

                        insertCell(table, "4 курс", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "7 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 7, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }

                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 7, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }
                        }

                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;

                        insertCell(table, "Всего за 7 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;

                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;

                        insertCell(table, "8 семестр", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, "Міндетті компонент    Обязательный компонент", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 8, IUPS_TYPE.MANDATORY);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }
                        }
                        insertCell(table, "Таңдау компоненті    Компонент по выбору", Element.ALIGN_CENTER, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 8, IUPS_TYPE.CHOOSEN);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                examc++;
                            }
                        }
                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;
                        fallCredits += allCredits;
                        fallEcts += allEcts;
                        fallLcount += allLcount;
                        fallPcount += allPcount;
                        fallLbcount += allLbcount;
                        fallwtcount += allwtcount;
                        fallOcount += allOcount;
                        fallCnt += allCnt;
                        fallexam += allexam;
                        ffallCredits += fallCredits;
                        ffallEcts += fallEcts;
                        ffallLcount += fallLcount;
                        ffallPcount += fallPcount;
                        ffallLbcount += fallLbcount;
                        ffallwtcount += fallwtcount;
                        ffallOcount += fallOcount;
                        ffallCnt += fallCnt;
                        ffallexam += fallexam;
                        insertCell(table, "Всего за 8 семестр", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                        insertCell(table, "Всего за 4 курс", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(fallexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                        insertCell(table, "Итого ", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallexam) + "Е/Э", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                        fallCredits = 0;
                        fallEcts = 0;
                        fallLcount = 0;
                        fallPcount = 0;
                        fallLbcount = 0;
                        fallwtcount = 0;
                        fallOcount = 0;
                        fallCnt = 0;
                        allexam = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;

                        insertCell(table, "     ", Element.ALIGN_LEFT, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        String var = null;
                        myData = getData(curriculum.getId().getId().intValue(), 0, IUPS_TYPE.ADDITIONAL);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }
                            var = myRow.get(6);
                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() > 0) {
                                countadd++;
                            }
                        }
                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        countall += countadd;

                        insertCell(table, "Всего ", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, allCredits + "кр  " + String.valueOf(countall) + "д/з", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                        ffallCredits += allCredits;
                        ffallEcts += allEcts;
                        ffallLcount += allLcount;
                        ffallPcount += allPcount;
                        ffallLbcount += allLbcount;
                        ffallwtcount += allwtcount;
                        ffallOcount += allOcount;
                        ffallCnt += allCnt;
                        ffallexam += allexam;
                        fin += countall;
                        allCredits = 0;
                        allEcts = 0;
                        allLcount = 0;
                        allPcount = 0;
                        allLbcount = 0;
                        allwtcount = 0;
                        allOcount = 0;
                        allCnt = 0;
                        allexam = 0;
                        countall = 0;
                        creditNum = 0;
                        ectsNum = 0;
                        lcount = 0;
                        pcount = 0;
                        lbcount = 0;
                        wtcount = 0;
                        ocount = 0;
                        allcount = 0;
                        examc = 0;

                        insertCell(table, "     ", Element.ALIGN_LEFT, 15, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        myData = getData(curriculum.getId().getId().intValue(), 0, IUPS_TYPE.AFTER_SEMESTER);
                        for (List<String> myRow : myData) {
                            for (String value : myRow) {
                                insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                            }

                            creditNum += Integer.parseInt(myRow.get(6).trim().length() > 0 ? myRow.get(6) : "0");
                            ectsNum += Integer.parseInt(myRow.get(7).trim().length() > 0 ? myRow.get(7) : "0");
                            lcount += Integer.parseInt(myRow.get(8).trim().length() > 0 ? myRow.get(8) : "0");
                            pcount += Integer.parseInt(myRow.get(9).trim().length() > 0 ? myRow.get(9) : "0");
                            lbcount += Integer.parseInt(myRow.get(10).trim().length() > 0 ? myRow.get(10) : "0");
                            wtcount += Integer.parseInt(myRow.get(11).trim().length() > 0 ? myRow.get(11) : "0");
                            ocount += Integer.parseInt(myRow.get(12).trim().length() > 0 ? myRow.get(12) : "0");
                            allcount += Integer.parseInt(myRow.get(13).trim().length() > 0 ? myRow.get(13) : "0");
                            if (myRow.get(14).trim().length() == 0) {
                                examc++;
                            }
                            if (myRow.get(14).trim().length() > 0) {
                                countall++;
                            }
                        }
                        fin += countall;
                        allCredits += creditNum;
                        allEcts += ectsNum;
                        allLcount += lcount;
                        allPcount += pcount;
                        allLbcount += lbcount;
                        allwtcount += wtcount;
                        allOcount += ocount;
                        allCnt += allcount;
                        allexam += examc;
                        ffallCredits += allCredits;
                        ffallEcts += allEcts;
                        ffallLcount += allLcount;
                        ffallPcount += allPcount;
                        ffallLbcount += allLbcount;
                        ffallwtcount += allwtcount;
                        ffallOcount += allOcount;
                        ffallCnt += allCnt;//ffallexam+=allexam;
                        insertCell(table, "Всего ", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(allCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        if (countall > 0) {
                            insertCell(table, String.valueOf(countall) + "д/з", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        } else {
                            insertCell(table, String.valueOf(allexam) + "МЕ/ГЭ", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        }


                        insertCell(table, "ИТОГО по учебному плану ", Element.ALIGN_LEFT, 6, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallCredits), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallEcts), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallLcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallPcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallLbcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallwtcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallOcount), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallCnt), Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                        insertCell(table, String.valueOf(ffallexam) + "Е/Э " + String.valueOf(fin) + "д/з", Element.ALIGN_RIGHT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                    }
                    document.add(table);
                    document.close();

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (fileDownloaderr == null) {
                    try {
                        fileDownloaderr = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fileDownloaderr != null) {
                    }
                } else {
                    fileDownloaderr.setFileDownloadResource(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
                }
                fileDownloaderr.extend(printButton);
            }


        });


        ClassResource resource = new ClassResource("enterprise-app.pdf");
        FileDownloader downloader = new FileDownloader(resource);
        downloader.extend(printButton);

        getContent().addComponent(toolbarHL);
        getContent().setComponentAlignment(toolbarHL, Alignment.TOP_CENTER);
    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);
    }

    public void updateCount(int id) {
        int allCredits = 0, allEcts = 0, allLcount = 0, allPcount = 0, allLbcount = 0, allwtcount = 0, allOcount = 0, allCnt = 0;
        int creditNum = 0;
        int ectsNum = 0, lcount = 0, pcount = 0, lbcount = 0, wtcount = 0, ocount = 0, allcount = 0;


    }

    public static List<ArrayList<String>> getData(int curriculumId, int semesterId, IUPS_TYPE iupsType) {

        List<ArrayList<String>> result = new ArrayList();
        List<V_CURRICULUM_DETAIL> detailList = new ArrayList<>();
        Map<Integer, Object> param = new HashMap<>();

        if (iupsType == IUPS_TYPE.MANDATORY) {
            String sql = "select t0.module_short_name, t0.cycle_short_name,t0.education_module_type_name, t0.subject_code,t0.subject_name_kz,t0.subject_name_ru,\n" +
                    "  t0.credit,t0.ects_count,t0.lc_count,t0.pr_count,t0.lb_count,t0.with_teacher_count,t0.own_count,t0.total_count,t0.control_type_name\n" +
                    "from V_CURRICULUM_DETAIL t0 where t0.CURRICULUM_ID = " + curriculumId +
                    " AND t0.SEMESTER_ID =  " + semesterId;
            try {
                List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        ArrayList<String> valuesList = new ArrayList();
                        for (int i = 0; i < oo.length; i++) {
                            valuesList.add(oo[i] != null ? (String.valueOf(oo[i])) : "");
                        }
                        result.add(valuesList);
                    }
                } else {
                    ArrayList<String> valuesList = new ArrayList();
                    for (int i = 0; i < 15; i++) {
                        valuesList.add(" ");
                    }
                    result.add(valuesList);
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
        }
        if (iupsType == IUPS_TYPE.CHOOSEN) {
            String sql = "select t0.module_short_name, t0.cycle_short_name,t0.education_module_type_name, t0.subject_code,t0.subject_name_kz,t0.subject_name_ru,\n" +
                    "  t0.credit,t0.ects_count,t0.lc_count,t0.pr_count,t0.lb_count,t0.with_teacher_count,t0.own_count,t0.total_count,t0.control_type_name \n" +
                    "from V_ELECTIVE_SUBJECT t0 where t0.CURRICULUM_ID = " + curriculumId + " AND t0.SEMESTER_ID = " + semesterId;
            try {
                List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        ArrayList<String> valuesList = new ArrayList();
                        for (int i = 0; i < oo.length; i++) {
                            valuesList.add(oo[i] != null ? (String.valueOf(oo[i])) : "");
                        }
                        result.add(valuesList);
                    }
                } else {
                    ArrayList<String> valuesList = new ArrayList();
                    for (int i = 0; i < 15; i++) {
                        valuesList.add(" ");
                    }
                    result.add(valuesList);
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
        }
        if (iupsType == IUPS_TYPE.ADDITIONAL) {
            String sql = "select t0.module_short_name, t0.cycle_short_name,t0.education_module_type_name, t0.subject_code,t0.subject_name_kz,t0.subject_name_ru,\n" +
                    "                     t0.credit,t0.ects_count,t0.lc_count,t0.pr_count,t0.lb_count,t0.with_teacher_count,t0.own_count,t0.total_count,(t0.semester_data_id ,'сем', t0.creditability_id,'кр')\n" +
                    "                    from v_curriculum_add_program t0 where t0.CURRICULUM_ID = " + curriculumId;
            try {
                List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        ArrayList<String> valuesList = new ArrayList();
                        for (int i = 0; i < oo.length; i++) {
                            valuesList.add(oo[i] != null ? (String.valueOf(oo[i])) : "");
                        }
                        result.add(valuesList);
                    }
                } else {
                    ArrayList<String> valuesList = new ArrayList();
                    for (int i = 0; i < 15; i++) {
                        valuesList.add(" ");
                    }
                    result.add(valuesList);
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
        }
        if (iupsType == IUPS_TYPE.AFTER_SEMESTER) {
            String sql = "select t0.module_short_name, t0.module_short_name,t0.education_module_type_name,\n" +
                    "  t0.subject_code,t0.subject_name_kz,t0.subject_name_ru,\n" +
                    "                      t0.credit,t0.ects_count,t0.lc_count,t0.week_number,\n" +
                    "  t0.lb_count,t0.with_teacher_count,t0.own_count,t0.total_count,\n" +
                    "  CASE WHEN t0.week_number IS NOT NULL\n" +
                    "    THEN  (t0.semester_data_id,'сем',t0.week_number,'апта') END from v_curriculum_after_semester t0  where t0.CURRICULUM_ID = " + curriculumId;
            try {
                List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        ArrayList<String> valuesList = new ArrayList();
                        for (int i = 0; i < oo.length; i++) {
                            valuesList.add(oo[i] != null ? (String.valueOf(oo[i])) : "");
                        }
                        result.add(valuesList);
                    }
                } else {
                    ArrayList<String> valuesList = new ArrayList();
                    for (int i = 0; i < 15; i++) {
                        valuesList.add(" ");
                    }
                    result.add(valuesList);
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
        }
        return result;

    }

    private void create() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.speciality.and.entry.year"));
        }

        curriculum.setDiplomaType((STUDENT_DIPLOMA_TYPE) diplomaTypeCB.getValue());
        curriculum.setSpeciality((SPECIALITY) specialityCB.getValue());
        curriculum.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)));
        curriculum.setCreated(new Date());
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(curriculum);
    }


    private static int getFirstMonday(int year, int month) {

        Calendar cacheCalendar = Calendar.getInstance();
        cacheCalendar.set(Calendar.YEAR, year);
        cacheCalendar.set(Calendar.MONTH, month);

        if(cacheCalendar.getFirstDayOfWeek()!=Calendar.MONDAY ){
            cacheCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY+1);
        }
        cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        cacheCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return cacheCalendar.get(Calendar.DATE);
    }


    private void initLabels() {
        academicDegreeLabel = new Label();
        academicDegreeLabel.addStyleName("bold");
        academicDegreeLabel.addStyleName("margin-left-30");
        getContent().addComponent(academicDegreeLabel);
        getContent().setComponentAlignment(academicDegreeLabel, Alignment.TOP_CENTER);

        studyPeriodLabel = new Label();
        studyPeriodLabel.addStyleName("bold");
        studyPeriodLabel.addStyleName("margin-left-30");
        getContent().addComponent(studyPeriodLabel);
        getContent().setComponentAlignment(studyPeriodLabel, Alignment.TOP_CENTER);

        errorLabel.addStyleName("error");
        errorLabel.setWidthUndefined();
        errorLabel.setValue(getUILocaleUtil().getMessage("curriculum.not.filled"));
        errorLabel.setVisible(false);
        getContent().addComponent(errorLabel);
        getContent().setComponentAlignment(errorLabel, Alignment.TOP_CENTER);
    }

    private void refresh() {
        try {
            countSumOfCreditBySemester();
            if (curriculum.getId() != null) {
                createButton.setEnabled(false);
                setAllButtonsEnabled(true);
                if (curriculum.getCurriculumStatus().getId().equals(CURRICULUM_STATUS.IN_CREATING)) {
                    conformButton.setEnabled(true);
                    approveButton.setEnabled(false);
                } else if (curriculum.getCurriculumStatus().getId().equals(CURRICULUM_STATUS.IN_CONFORMING)) {
                    conformButton.setEnabled(false);
                    approveButton.setEnabled(true);
                } else {//APPROVE
                    conformButton.setEnabled(false);
                    approveButton.setEnabled(false);
                    setAllButtonsEnabled(false);
                }
            } else {
                createButton.setEnabled(true);
                conformButton.setEnabled(false);
                approveButton.setEnabled(false);
            }

            for (SubjectsTab subjectsTab : subjectsTabs) {
                subjectsTab.setCurriculum(curriculum);
                subjectsTab.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
                subjectsTab.refresh();
            }

            addProgramPanel.setCurriculum(curriculum);
            addProgramPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
            addProgramPanel.refresh();
            afterSemesterProgamPanel.setCurriculum(curriculum);
            afterSemesterProgamPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
            afterSemesterProgamPanel.refresh();
            schedulePanel.setCurriculum(curriculum);
            schedulePanel.refresh();
        } catch (Exception ex) {
            LOG.error("Unable to refresh semester panel: ", ex);
        }
    }

    private void setAllButtonsEnabled(boolean enabled) {
        for (SubjectsTab subjectsTab : subjectsTabs) {
            setButtonsEnabled(subjectsTab.getMainSubjectsGW(),enabled);
            setButtonsEnabled(subjectsTab.getElectiveSubjectsGW(),enabled);
        }
        setButtonsEnabled(addProgramPanel.getAddingSubjectsGW(), enabled);
        setButtonsEnabled(afterSemesterProgamPanel.getAfterSemesterSubjectsGW(), enabled);
        schedulePanel.getEditButton().setEnabled(enabled);
    }

    private void setButtonsEnabled(GridWidget mainSubjectsGW, boolean enabled) {
        mainSubjectsGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, enabled);
        mainSubjectsGW.setButtonEnabled(AbstractToolbar.EDIT_BUTTON, enabled);
        mainSubjectsGW.setButtonEnabled(AbstractToolbar.DELETE_BUTTON, enabled);
        mainSubjectsGW.setButtonEnabled(AbstractToolbar.REFRESH_BUTTON, enabled);
        mainSubjectsGW.setButtonEnabled(AbstractToolbar.HELP_BUTTON, enabled);
    }

    public void setTotalCreditSum() {
        int sum = 0;
        int totalSumAns;

        for (SubjectsTab subjectsTab : subjectsTabs) {
            sum += subjectsTab.getTotalCredit();//here can be elective subject type
        }

        totalSumAns = sum + addProgramPanel.getTotalCredit()
                + afterSemesterProgamPanel.getTotalCredit();
        creditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("credit.sum"), totalSumAns));

    }

    private void conform() throws Exception {
//        checkForConform();

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, CURRICULUM_STATUS.IN_CONFORMING));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);
        approveButton.setEnabled(true);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());

        schedulePanel.generateSchedule();

        refresh();
    }

    private void approve() throws Exception {
//        checkForConform();

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, CURRICULUM_STATUS.APPROVED));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());

        setStudentSubjects();

        refresh();
    }

    private void setStudentSubjects() throws Exception {
        if (CommonUtils.getStudyYearByEntranceYear(curriculum.getEntranceYear()) == 1) {
            List<STUDENT_SUBJECT> starStudentSubjects = getStarStudentSubjects();
            STUDENT_EDUCATION starStudentEducation = starStudentSubjects.get(0).getStudentEducation();

            List<STUDENT_EDUCATION> studentEducations = getStudentEducations(starStudentEducation);

            for (STUDENT_EDUCATION studentEducation : studentEducations) {
                deleteStudentSubject(studentEducation);

                createStudentSubject(starStudentSubjects, studentEducation);

                deleteStudentTeacherSubject(studentEducation);

                createStudentTeacherSubject(starStudentEducation, studentEducation);
            }
        }
    }

    private List<STUDENT_EDUCATION> getStudentEducations(STUDENT_EDUCATION starStudentEducation) throws Exception {
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        FromItem studentFI = studentEducationQM.addJoin(EJoin.INNER_JOIN, "student", STUDENT.class, "id");
        studentEducationQM.addWhere("id", ECriteria.NOT_EQUAL, starStudentEducation.getId());
        studentEducationQM.addWhereNull("child");
        studentEducationQM.addWhere("speciality", ECriteria.EQUAL, curriculum.getSpeciality().getId());
        studentEducationQM.addWhere(studentFI, "entranceYear", ECriteria.EQUAL,
                curriculum.getEntranceYear().getId());
        studentEducationQM.addWhere(studentFI, "diplomaType", ECriteria.EQUAL,
                curriculum.getDiplomaType().getId());
        return CommonUtils.getQuery().lookup(studentEducationQM);
    }

    private List<STUDENT_SUBJECT> getStarStudentSubjects() throws Exception {
        String sql = "select stu_subj.* " +
                "from curriculum_individual_plan curr " +
                "       inner join users usr on usr.code = curr.student_code " +
                "       inner join student stu on usr.id = stu.id " +
                "       inner join student_education stu_edu on stu.id = stu_edu.student_id " +
                "                                                 and stu_edu.child_id is null " +
                "       inner join student_subject stu_subj on stu_edu.id = stu_subj.student_id " +
                "where curr.speciality_id = ? " +
                "  and curr.entrance_year_id = ? " +
                "  and curr.diploma_type_id = ? ";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, curriculum.getSpeciality().getId().getId());
        params.put(2, curriculum.getEntranceYear().getId().getId());
        params.put(3, curriculum.getDiplomaType().getId().getId());
        return CommonUtils.getQuery().lookup(sql, params,
                STUDENT_SUBJECT.class);
    }

    private void deleteStudentSubject(STUDENT_EDUCATION studentEducation) throws Exception {
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        studentSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        studentSubjectQM.addWhere("deleted", false);
        List<STUDENT_SUBJECT> studentSubjects = CommonUtils.getQuery().lookup(studentSubjectQM);
        for (STUDENT_SUBJECT studentSubject : studentSubjects) {
            studentSubject.setDeleted(true);
        }
        CommonUtils.getQuery().merge(studentSubjects);
    }

    private void createStudentSubject(List<STUDENT_SUBJECT> starStudentSubjects, STUDENT_EDUCATION studentEducation) throws Exception {
        for (STUDENT_SUBJECT studentSubject : starStudentSubjects) {
            studentSubject.setStudentEducation(studentEducation);
            studentSubject.setRegDate(new Date());
            CommonUtils.getQuery().create(studentSubject);
        }
    }

    private void deleteStudentTeacherSubject(STUDENT_EDUCATION studentEducation) throws Exception {
        QueryModel<STUDENT_TEACHER_SUBJECT> studentTeacherSubjectQM = new QueryModel<>(
                STUDENT_TEACHER_SUBJECT.class);
        studentTeacherSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        List<STUDENT_TEACHER_SUBJECT> studentTeacherSubjects = CommonUtils.getQuery().lookup(
                studentTeacherSubjectQM);
        CommonUtils.getQuery().delete(studentTeacherSubjects);
    }

    private void createStudentTeacherSubject(STUDENT_EDUCATION starStudentEducation,
                                             STUDENT_EDUCATION studentEducation) throws Exception {
        QueryModel<STUDENT_TEACHER_SUBJECT> starStudentTeacherSubjectQM = new QueryModel<>(
                STUDENT_TEACHER_SUBJECT.class);
        starStudentTeacherSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, starStudentEducation.getId());
        List<STUDENT_TEACHER_SUBJECT> starStudentTeacherSubjects = CommonUtils.getQuery().lookup(
                starStudentTeacherSubjectQM);
        for (STUDENT_TEACHER_SUBJECT studentTeacherSubject : starStudentTeacherSubjects) {
            studentTeacherSubject.setStudentEducation(studentEducation);
            CommonUtils.getQuery().create(studentTeacherSubject);
        }
    }

    private void checkForConform() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.speciality.and.entry.year"));
        }

        for (SubjectsTab subjectsTab : subjectsTabs) {
            subjectsTab.checkForConform();//here can be main subjects
        }

        addProgramPanel.checkForConform();
        afterSemesterProgamPanel.checkForConform();
        schedulePanel.checkForConform();
    }

    public final boolean isBachelorCurriculum() throws Exception {
        SPECIALITY speciality = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SPECIALITY.class, curriculum.getSpeciality().getId());

        return speciality.getLevel().getId().equals(ID.valueOf(1));
    }

    public String getSpecialityText() {
        return String.format(getUILocaleUtil().getCaption("curriculum.speciality"),
                curriculum.getSpeciality().toString(), curriculum.getEntranceYear().toString());
    }

    public String getAcademicDegreeText() {
        return academicDegreeLabel.getValue();
    }

    public String getStudyPeriodText() {
        return studyPeriodLabel.getValue();
    }

    private void showErrorLabel(boolean show) {
        errorLabel.setVisible(show);
    }

    public void findCurriculum(SPECIALITY speciality, ENTRANCE_YEAR entranceYear,
                               STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        QueryModel<CURRICULUM> qm = new QueryModel<>(CURRICULUM.class);
        qm.addWhere("speciality", ECriteria.EQUAL, speciality.getId());
        qm.addWhereAnd("entranceYear", ECriteria.EQUAL, entranceYear.getId());
        qm.addWhereAnd("diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        qm.addWhere("deleted", Boolean.FALSE);
        try {
            curriculum = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
            changeStatusAndRemoveLabel();
        } catch (NoResultException e) {
            showErrorLabel(true);
            curriculum = new CURRICULUM();
            curriculum.setSpeciality(speciality);
            curriculum.setEntranceYear(entranceYear);
            curriculum.setDiplomaType(studentDiplomaType);
            try {
                curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, CURRICULUM_STATUS.IN_CREATING));
                statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + createStatus);
            } catch (Exception ex1) {
                LOG.error("Unable to set curriculum status: ", ex1);
                Message.showError(ex1.toString());
            }
        } catch (Exception ex) {
            LOG.error("Unable to find curriculum: ", ex);
            Message.showError(ex.toString());
        }
        if (curriculum != null) {
            refresh();
        }
    }

    private void changeStatusAndRemoveLabel() {
        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().toString());
        showErrorLabel(false);
    }

    private SelectedTabChangeListener TabChangeListener() {
        return new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent selectedTabChangeEvent) {
                countSumOfCreditBySemester();
            }
        };
    }

    public void countSumOfCreditBySemester() {
        int mainSubjectsTotalSum = 0;
        int electiveSubjectsTotalSum = 0;
        int totalSum = 0;//in current semester
        if (curriculum != null && curriculum.getId() != null) {
            if (mainTS.getSelectedTab() instanceof SubjectsTab) {
                SubjectsTab subjectsTab = (SubjectsTab) mainTS.
                        getSelectedTab();

                if (subjectsTab.getSemester() != null) {
                    QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
                    semesterQM.addWhere("id", ECriteria.EQUAL, subjectsTab.getSemester().getId());
                    semesterQM.addWhere("studyYear", ECriteria.EQUAL, subjectsTab.getSemester().
                            getStudyYear().getId());
                    semesterQM.addOrder("id");

                    List<SEMESTER> semesters = new ArrayList<>();
                    try {
                        semesters = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM);
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to get semesters", e);
                    }

                    QueryModel<V_CURRICULUM_DETAIL> curriculumDetailQM = new QueryModel<>(
                            V_CURRICULUM_DETAIL.class);
                    curriculumDetailQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());

                    QueryModel<V_ELECTIVE_SUBJECT> electiveSubjectQM = new QueryModel<>(
                            V_ELECTIVE_SUBJECT.class);
                    electiveSubjectQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());

                    for (SEMESTER semester : semesters) {
                        curriculumDetailQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
                        electiveSubjectQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
                        List<V_CURRICULUM_DETAIL> detailList = new ArrayList<>();
                        List<V_ELECTIVE_SUBJECT> electiveSubjects = new ArrayList<>();
                        try {
                            detailList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(curriculumDetailQM);
                            electiveSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(electiveSubjectQM);
                        } catch (Exception e) {
                            CommonUtils.showMessageAndWriteLog("Unable to get subjects", e);
                        }

                        for (V_CURRICULUM_DETAIL mainSubject : detailList) {
                            if (mainSubject.getSubjectCode() != null) {
                                mainSubjectsTotalSum += mainSubject.getCredit();
                            }

                        }
                        for (V_ELECTIVE_SUBJECT electiveSubject : electiveSubjects) {
                            if (electiveSubject.getSubjectCode() != null && electiveSubject.isConsiderCredit()) {
                                mainSubjectsTotalSum += electiveSubject.getCredit();
                            }
                        }
                        totalSum = mainSubjectsTotalSum + electiveSubjectsTotalSum;
                    }
                } else {
                    if (subjectsTab.getSubjectType().equals(
                            SubjectsTab.SubjectsType.ADDING_SUBJECTS)) {
                        totalSum += addProgramPanel.getTotalCredit();
                    } else {
                        totalSum += afterSemesterProgamPanel.getTotalCredit();
                    }
                }
            }
        }
        totalCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), totalSum));
    }

    private class CurriculumTabChangeListener implements SelectedTabChangeListener {

        @Override
        public void selectedTabChange(SelectedTabChangeEvent ev) {
            AbstractCurriculumPanel curriculumPanel = (AbstractCurriculumPanel) ev.getTabSheet().getSelectedTab();
            curriculumPanel.setCurriculum(curriculum);
            try {
                curriculumPanel.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to refresh: ", ex);
            }
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return true;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
    }

    @Override
    public void onFilter(Object source, QueryModel qm, int buttonId) {
    }

    @Override
    public void onAccept(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        return true;
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
    }
}
