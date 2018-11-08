package kz.halyqsoft.univercity.modules.curriculum.working.main;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.DiplomaTypeChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.EntranceYearChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.changelisteners.SpecialityChangeListener;
import kz.halyqsoft.univercity.modules.curriculum.working.cycle.CyclePanel;
import kz.halyqsoft.univercity.modules.curriculum.working.schedule.SchedulePanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.AddProgramPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.AfterSemesterProgamPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.CreditCountByComponentsPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.SemesterDetailPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private List<SemesterDetailPanel> semesterDetailPanels;
    private AddProgramPanel addProgramPanel;
    private AfterSemesterProgamPanel afterSemesterProgamPanel;
    private CreditCountByComponentsPanel creditCountByComponentsPanel;
    private CyclePanel cyclePanel;
    private SchedulePanel schedulePanel;

    private static final int NUMBER_OF_YEARS_AHEAD = 1;

    public CurriculumView(AbstractTask task) throws Exception {
        super(task);
        semesterDetailPanels = new ArrayList<>();
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
            SemesterDetailPanel semesterDetailPanel = new SemesterDetailPanel(this, semester);
            semesterDetailPanel.setWidth(100, Unit.PERCENTAGE);
            semesterDetailPanel.setCurriculum(curriculum);
            semesterDetailPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
            semesterDetailPanel.initPanel();

            semesterDetailPanels.add(semesterDetailPanel);
            mainTS.addTab(semesterDetailPanel, getUILocaleUtil().getCaption("semester." + semester.getId().toString()));
        }

//        for (SemesterDetailPanel semesterDetailPanel : semesterDetailPanels) {//TODO maybe
//            semesterDetailPanel.setCreditCountByComponentsPanel(creditCountByComponentsPanel);
//        }

        addProgramPanel = new AddProgramPanel(this);
        addProgramPanel.setCurriculum(curriculum);
        addProgramPanel.initPanel();
        mainTS.addTab(addProgramPanel, getUILocaleUtil().getCaption("add.education.programm"));

//        afterSemesterProgamPanel = new AfterSemesterProgamPanel(this);//TODO return
//        afterSemesterProgamPanel.setCurriculum(curriculum);
//        afterSemesterProgamPanel.initPanel();
//        tabSheet.addTab(afterSemesterProgamPanel, getUILocaleUtil().getEntityLabel(V_CURRICULUM_AFTER_SEMESTER.class));
//
//        creditCountByComponentsPanel = new CreditCountByComponentsPanel(this);
//        creditCountByComponentsPanel.setCurriculum(curriculum);
//        creditCountByComponentsPanel.initPanel();
//        tabSheet.addTab(creditCountByComponentsPanel, getUILocaleUtil().getEntityLabel(VCurriculumCreditCycleSum.class));
//
//        cyclePanel = new CyclePanel(this);
//        cyclePanel.setCurriculum(curriculum);
//        cyclePanel.initPanel();
//        tabSheet.addTab(cyclePanel, getUILocaleUtil().getCaption("by.cycle"));
//
//        schedulePanel = new SchedulePanel(this);
//        schedulePanel.setCurriculum(curriculum);
//        schedulePanel.initPanel();
//        tabSheet.addTab(schedulePanel, getUILocaleUtil().getCaption("curriculum.schedule"));
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
        printButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                //TODO Assyl
            }
        });

        getContent().addComponent(toolbarHL);
        getContent().setComponentAlignment(toolbarHL, Alignment.TOP_CENTER);
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
                if (curriculum.getCurriculumStatus().getId().equals(CURRICULUM_STATUS.IN_CREATING)) {
                    conformButton.setEnabled(true);
                    approveButton.setEnabled(false);
                } else if (curriculum.getCurriculumStatus().getId().equals(CURRICULUM_STATUS.IN_CONFORMING)) {
                    conformButton.setEnabled(false);
                    approveButton.setEnabled(true);
                } else {
                    conformButton.setEnabled(false);
                    approveButton.setEnabled(false);
                }
            } else {
                createButton.setEnabled(true);
                conformButton.setEnabled(false);
                approveButton.setEnabled(false);
            }

            for (SemesterDetailPanel semesterDetailPanel : semesterDetailPanels) {
                semesterDetailPanel.setCurriculum(curriculum);
                semesterDetailPanel.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
                semesterDetailPanel.refresh();
            }

            addProgramPanel.setCurriculum(curriculum);
            addProgramPanel.refresh();
//            afterSemesterProgamPanel.setCurriculum(curriculum);//TODO return
//            afterSemesterProgamPanel.refresh();
//            creditCountByComponentsPanel.setCurriculum(curriculum);
//            creditCountByComponentsPanel.refresh();
//            cyclePanel.setCurriculum(curriculum);
//            cyclePanel.refresh();
//            schedulePanel.setCurriculum(curriculum);
//            schedulePanel.refresh();
        } catch (Exception ex) {
            LOG.error("Unable to refresh semester panel: ", ex);
        }
    }

    public void setTotalCreditSum() {
        int sum = 0;
        int totalSumAns;

        for (SemesterDetailPanel semesterDetailPanel : semesterDetailPanels) {
            sum += semesterDetailPanel.getTotalCredit();
        }

        totalSumAns = sum + addProgramPanel.getTotalCredit()
        /*+ afterSemesterProgamPanel.getTotalCredit()*/;//TODO return
        creditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("credit.sum"),
                totalSumAns));

    }

    public void save() throws Exception {
        for (SemesterDetailPanel semesterDetailPanel : semesterDetailPanels) {
            semesterDetailPanel.setCurriculum(curriculum);
        }

            addProgramPanel.setCurriculum(curriculum);
//            afterSemesterProgamPanel.setCurriculum(curriculum);//TODO return
//            creditCountByComponentsPanel.setCurriculum(curriculum);
//            cyclePanel.setCurriculum(curriculum);
//            //			cyclePanel.saveButton();
//            schedulePanel.setCurriculum(curriculum);
//            schedulePanel.generateSchedule();
//            schedulePanel.save();
    }

    private void conform() throws Exception {
//        checkForConform();//TODO return

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, CURRICULUM_STATUS.IN_CONFORMING));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);
        approveButton.setEnabled(true);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());
        refresh();
    }

    private void approve() throws Exception {
//        checkForConform();//TODO return

        addProgramPanel.approve();
//        afterSemesterProgamPanel.approve();//TODO return
//        cyclePanel.checkForApprove();

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, CURRICULUM_STATUS.APPROVED));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());
        refresh();
    }

    private void checkForConform() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.speciality.and.entry.year"));
        }

        for (SemesterDetailPanel semesterDetailPanel : semesterDetailPanels) {
            semesterDetailPanel.checkForConform();
        }

        addProgramPanel.checkForConform();
//        afterSemesterProgamPanel.checkForConform();//TODO return
//        schedulePanel.checkForConform();
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
        if (curriculum.getId() != null) {
            if (mainTS.getSelectedTab() instanceof SemesterDetailPanel) {
                SemesterDetailPanel semesterDetailPanel = (SemesterDetailPanel) mainTS.
                        getSelectedTab();

                QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
                semesterQM.addWhere("id", ECriteria.EQUAL, semesterDetailPanel.getSemester().getId());
                semesterQM.addWhere("studyYear", ECriteria.EQUAL, semesterDetailPanel.getSemester().
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
            } else if (mainTS.getSelectedTab() instanceof AddProgramPanel) {
                AddProgramPanel addProgramPanel = (AddProgramPanel) mainTS.getSelectedTab();
                totalSum += addProgramPanel.getTotalCredit();
            } else if (mainTS.getSelectedTab() instanceof AfterSemesterProgamPanel) {
                AfterSemesterProgamPanel afterSemesterProgamPanel = (AfterSemesterProgamPanel) mainTS.getSelectedTab();
                totalSum += afterSemesterProgamPanel.getTotalCredit();
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
