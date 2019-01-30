package kz.halyqsoft.univercity.modules.loadtochair;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.LOAD_TO_CHAIR;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT_ALL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_WITH_GROUPS;
import kz.halyqsoft.univercity.filter.FChairFilter;
import kz.halyqsoft.univercity.filter.panel.ChairFilterPanel;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * @author Omarbek
 * @created on 29.06.2018
 */
public class LoadToChairView extends AbstractTaskView implements FilterPanelListener {

    private ChairFilterPanel filterPanel;
    private ENTRANCE_YEAR currentYear;

    private GridWidget loadGW;
    private GridWidget countGW;
    private GridWidget totalCountGW;

    private QueryModel subjectQM;
    private QueryModel semesterQM;
    private QueryModel curriculumQM;
    private QueryModel streamQM;
    private QueryModel groupQM;

    private ComboBox chairCB;
    private ComboBox studyYearCB;
    private ComboBox diplomaTypeCB;
    private ComboBox semesterPeriodCB;
    private ComboBox yearCB;

    private double withTeacherCount;
    private double ratingCount;
    private double ticketExamCount;
    private double notTicketExamCount;
    private double controlCount;
    private double courseWorkCount;

    private double diplomaCount;
    private double mekCount;
    private double protectDiplomaCount;

    public LoadToChairView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ChairFilterPanel(new FChairFilter());

        withTeacherCount = setValue(CONSTANTS.WITH_TEACHER);
        ratingCount = setValue(CONSTANTS.RATING);
        ticketExamCount = setValue(CONSTANTS.TICKET);
        notTicketExamCount = setValue(CONSTANTS.EXAM);
        controlCount = setValue(CONSTANTS.CONTROL);
        courseWorkCount = setValue(CONSTANTS.COURSE);
        diplomaCount = setValue(CONSTANTS.DIPLOMA);
        mekCount = setValue(CONSTANTS.MEK);
        protectDiplomaCount = setValue(CONSTANTS.PROTECT_DIPLOMA);
    }

    private double setValue(ID id) throws Exception {
        QueryModel<CONSTANTS> constantsQM = new QueryModel<>(CONSTANTS.class);
        constantsQM.addWhere("id", ECriteria.EQUAL, id);
        CONSTANTS constant = CommonUtils.getQuery().lookupSingle(constantsQM);
        return constant.getValue();
    }


    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        HorizontalLayout loadHL = CommonUtils.createButtonPanel();

        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                CommonUtils.getQuery().lookup(entranceYearQM));
        yearCB = new ComboBox();
        yearCB.setContainerDataSource(entranceYearBIC);
        yearCB.setNullSelectionAllowed(false);
        yearCB.setTextInputAllowed(true);
        yearCB.setFilteringMode(FilteringMode.CONTAINS);
        loadHL.addComponent(yearCB);

        Button generateButton = new Button();
        generateButton.setCaption(getUILocaleUtil().getCaption("generate"));
        generateButton.setEnabled(false);
        generateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                generate((ENTRANCE_YEAR) yearCB.getValue());
            }
        });
        loadHL.addComponent(generateButton);
        yearCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
                if (semesterData != null && semesterData.getYear().equals(event.getProperty().getValue())) {
                    generateButton.setEnabled(true);
                } else {
                    generateButton.setEnabled(false);
                }
            }
        });

        getContent().addComponent(loadHL);
        getContent().setComponentAlignment(loadHL, Alignment.TOP_CENTER);

        QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
        chairQM.addWhereNotNull("parent");
        chairQM.addWhereAnd("deleted", Boolean.FALSE);
        chairQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
        chairCB = new ComboBox();
        chairCB.setContainerDataSource(chairBIC);
        chairCB.setImmediate(true);
        chairCB.setNullSelectionAllowed(true);
        chairCB.setTextInputAllowed(true);
        chairCB.setFilteringMode(FilteringMode.CONTAINS);
        chairCB.setWidth(400, Unit.PIXELS);
        chairCB.setPageLength(0);
        filterPanel.addFilterComponent("chair", chairCB);

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
        filterPanel.addFilterComponent("studentDiplomaType", diplomaTypeCB);

        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(
                STUDY_YEAR.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB = new ComboBox();
        studyYearCB.setContainerDataSource(studyYearBIC);
        studyYearCB.setImmediate(true);
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.STARTSWITH);
        studyYearCB.setWidth(200, Unit.PIXELS);
        studyYearCB.setPageLength(0);
        filterPanel.addFilterComponent("studyYear", studyYearCB);

        QueryModel<SEMESTER_PERIOD> semesterPeriodQM = new QueryModel<>(SEMESTER_PERIOD.class);
        BeanItemContainer<SEMESTER_PERIOD> semesterPeriodBIC = new BeanItemContainer<>(SEMESTER_PERIOD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterPeriodQM));

        semesterPeriodCB = new ComboBox();
        semesterPeriodCB.setContainerDataSource(semesterPeriodBIC);
        semesterPeriodCB.setNullSelectionAllowed(true);
        semesterPeriodCB.setTextInputAllowed(true);
        semesterPeriodCB.setFilteringMode(FilteringMode.STARTSWITH);
        semesterPeriodCB.setWidth(200, Unit.PIXELS);
        filterPanel.addFilterComponent("semesterPeriod", semesterPeriodCB);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        loadGW = new GridWidget(V_LOAD_TO_CHAIR_WITH_GROUPS.class);
        loadGW.addEntityListener(this);
        loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);
        Button openBtn = new Button(getUILocaleUtil().getCaption("open"));
        openBtn.setIcon(FontAwesome.SIGN_IN);
        openBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (loadGW.getSelectedEntity() != null) {
                    V_LOAD_TO_CHAIR_WITH_GROUPS loadToChair = (V_LOAD_TO_CHAIR_WITH_GROUPS) loadGW.getSelectedEntity();
                    DetailDialog detailDialog = new DetailDialog(loadToChair.getStream(), false);
                    detailDialog.getStreamGroupGW().showToolbar(false);
                } else {
                    Message.showError(getUILocaleUtil().getMessage("chooseARecord"));
                }
            }
        });
        loadGW.getToolbarPanel().addComponent(openBtn);

        DBGridModel loadGM = (DBGridModel) loadGW.getWidgetModel();
        loadGM.setTitleVisible(false);
        loadGM.setMultiSelect(false);
        loadGM.setRefreshType(ERefreshType.MANUAL);
        loadGM.setCrudEntityClass(LOAD_TO_CHAIR.class);

        subjectQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("subject")).getQueryModel();
        subjectQM.addWhere("deleted", false);

        curriculumQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("curriculum")).getQueryModel();
        curriculumQM.addWhere("deleted", false);

        groupQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("group")).getQueryModel();
        groupQM.addWhere("deleted", false);

        semesterQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("semester")).getQueryModel();
        streamQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("stream")).getQueryModel();

        countGW = new GridWidget(V_LOAD_TO_CHAIR_COUNT.class);
        countGW.showToolbar(false);

        DBGridModel countGM = (DBGridModel) countGW.getWidgetModel();
        countGM.setMultiSelect(false);
        countGM.setHeightByRows(2);
        countGM.setRefreshType(ERefreshType.MANUAL);

        totalCountGW = new GridWidget(V_LOAD_TO_CHAIR_COUNT_ALL.class);
        totalCountGW.showToolbar(false);

        DBGridModel totalCountGM = (DBGridModel) totalCountGW.getWidgetModel();
        totalCountGM.setHeightByRows(1);
        totalCountGM.setTitleResource("total");
        totalCountGM.setMultiSelect(false);
        totalCountGM.setRefreshType(ERefreshType.MANUAL);

        FChairFilter chairFilter = (FChairFilter) filterPanel.getFilterBean();
        if (chairFilter.hasFilter()) {
            doFilter(chairFilter);
        }

        getContent().addComponent(loadGW);
        getContent().setComponentAlignment(loadGW, Alignment.MIDDLE_CENTER);

        getContent().addComponent(countGW);
        getContent().setComponentAlignment(countGW, Alignment.MIDDLE_CENTER);

        getContent().addComponent(totalCountGW);
        getContent().setComponentAlignment(totalCountGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh(List<V_LOAD_TO_CHAIR_WITH_GROUPS> loads, List<V_LOAD_TO_CHAIR_COUNT> loadCounts,
                         List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts, ENTRANCE_YEAR year) {
        if(year!=null) {
            try {
                ((DBGridModel) loadGW.getWidgetModel()).setEntities(loads);
                loadGW.refresh();

                ((DBGridModel) countGW.getWidgetModel()).setEntities(loadCounts);
                countGW.refresh();

                ((DBGridModel) totalCountGW.getWidgetModel()).setEntities(loadAllCounts);
                totalCountGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", ex);
            }
        }
    }

    private List<V_LOAD_TO_CHAIR_WITH_GROUPS> getLoads(DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                                       STUDY_YEAR studyYear, SEMESTER_PERIOD semesterPeriod, ENTRANCE_YEAR year) {
        QueryModel<V_LOAD_TO_CHAIR_WITH_GROUPS> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_WITH_GROUPS.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        if (semesterPeriod != null) {
            FromItem semFI = loadQM.addJoin(EJoin.INNER_JOIN, "semester", SEMESTER.class, "id");
            loadQM.addWhere(semFI, "semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
        }
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere("createdYear", ECriteria.EQUAL, year.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        loadQM.addOrder("semester");
        loadQM.addOrder("subject");
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", e);
        }
        return new ArrayList<>();
    }

    private List<V_LOAD_TO_CHAIR_COUNT> getLoadCount(DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                                     STUDY_YEAR studyYear, SEMESTER_PERIOD semesterPeriod, ENTRANCE_YEAR year) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        if (semesterPeriod != null) {
            FromItem semFI = loadQM.addJoin(EJoin.INNER_JOIN, "semester", SEMESTER.class, "id");
            loadQM.addWhere(semFI, "semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
        }
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("createdYear", ECriteria.EQUAL, year.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh count of loading to chair", e);
        }
        return new ArrayList<>();
    }

    private List<V_LOAD_TO_CHAIR_COUNT_ALL> getLoadAllCount(DEPARTMENT chair,
                                                            STUDENT_DIPLOMA_TYPE studentDiplomaType, SEMESTER_PERIOD semesterPeriod, ENTRANCE_YEAR year) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT_ALL> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT_ALL.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        if (semesterPeriod != null) {
            FromItem semFI = loadQM.addJoin(EJoin.INNER_JOIN, "semester", SEMESTER.class, "id");
            loadQM.addWhere(semFI, "semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
        }
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("createdYear", ECriteria.EQUAL, year.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh all count of loading to chair", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FChairFilter chairFilter = (FChairFilter) abstractFilterBean;
        if (chairFilter.getChair() != null && chairFilter.getStudentDiplomaType() != null
                && chairFilter.getStudyYear() != null && yearCB.getValue() != null) {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, true);
            ENTRANCE_YEAR year =(ENTRANCE_YEAR) yearCB.getValue();
            List<V_LOAD_TO_CHAIR_WITH_GROUPS> loads = getLoads(chairFilter.getChair(), chairFilter.getStudentDiplomaType(),
                    chairFilter.getStudyYear(), chairFilter.getSemesterPeriod(),year);

            List<V_LOAD_TO_CHAIR_COUNT> loadCounts = getLoadCount(chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType(), chairFilter.getStudyYear(), chairFilter.getSemesterPeriod(),year);
            List<V_LOAD_TO_CHAIR_COUNT> loadCountSums = getJoinListByStudyYear(loadCounts,
                    chairFilter.getSemesterPeriod() != null);

            List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts = getLoadAllCount(chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType(), chairFilter.getSemesterPeriod(),year);
            List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCountSums = getJoinList(loadAllCounts);
            refresh(loads, loadCountSums, loadAllCountSums,year);
        } else {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);
        }
    }

    private List<V_LOAD_TO_CHAIR_COUNT_ALL> getJoinList(List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts) {
        V_LOAD_TO_CHAIR_COUNT_ALL loadCountSum = new V_LOAD_TO_CHAIR_COUNT_ALL();
        for (V_LOAD_TO_CHAIR_COUNT_ALL count : loadAllCounts) {

            loadCountSum.setLcCount(loadCountSum.getLcCount() + count.getLcCount());
            loadCountSum.setPrCount(loadCountSum.getPrCount() + count.getPrCount());
            loadCountSum.setLbCount(loadCountSum.getLbCount() + count.getLbCount());
            loadCountSum.setWithTeacherCount(loadCountSum.getWithTeacherCount() + count.getWithTeacherCount());
            loadCountSum.setRatingCount(loadCountSum.getRatingCount() + count.getRatingCount());
            loadCountSum.setExamCount(loadCountSum.getExamCount() + count.getExamCount());
            loadCountSum.setControlCount(loadCountSum.getControlCount() + count.getControlCount());
            loadCountSum.setCourseWorkCount(loadCountSum.getCourseWorkCount() + count.getCourseWorkCount());
            loadCountSum.setDiplomaCount(loadCountSum.getDiplomaCount() + count.getDiplomaCount());
            loadCountSum.setPracticeCount(loadCountSum.getPracticeCount() + count.getPracticeCount());
            loadCountSum.setMek(loadCountSum.getMek() + count.getMek());
            loadCountSum.setProtectDiplomaCount(loadCountSum.getProtectDiplomaCount() +
                    count.getProtectDiplomaCount());
            loadCountSum.setTotalCount(loadCountSum.getTotalCount() + count.getTotalCount());
        }
        List<V_LOAD_TO_CHAIR_COUNT_ALL> loadCountSums = new ArrayList<>(1);
        loadCountSums.add(loadCountSum);
        return loadCountSums;
    }

    private List<V_LOAD_TO_CHAIR_COUNT> getJoinListByStudyYear(List<V_LOAD_TO_CHAIR_COUNT> loadCounts,
                                                               boolean setSemester) {
        V_LOAD_TO_CHAIR_COUNT loadCountSum = new V_LOAD_TO_CHAIR_COUNT();
        for (V_LOAD_TO_CHAIR_COUNT count : loadCounts) {
            if (setSemester) {
                loadCountSum.setSemester(count.getSemester());
            }
            loadCountSum.setStudyYear(count.getStudyYear());

            loadCountSum.setLcCount(loadCountSum.getLcCount() + count.getLcCount());
            loadCountSum.setPrCount(loadCountSum.getPrCount() + count.getPrCount());
            loadCountSum.setLbCount(loadCountSum.getLbCount() + count.getLbCount());
            loadCountSum.setWithTeacherCount(loadCountSum.getWithTeacherCount() + count.getWithTeacherCount());
            loadCountSum.setRatingCount(loadCountSum.getRatingCount() + count.getRatingCount());
            loadCountSum.setExamCount(loadCountSum.getExamCount() + count.getExamCount());
            loadCountSum.setControlCount(loadCountSum.getControlCount() + count.getControlCount());
            loadCountSum.setCourseWorkCount(loadCountSum.getCourseWorkCount() + count.getCourseWorkCount());
            loadCountSum.setDiplomaCount(loadCountSum.getDiplomaCount() + count.getDiplomaCount());
            loadCountSum.setPracticeCount(loadCountSum.getPracticeCount() + count.getPracticeCount());
            loadCountSum.setMek(loadCountSum.getMek() + count.getMek());
            loadCountSum.setProtectDiplomaCount(loadCountSum.getProtectDiplomaCount() +
                    count.getProtectDiplomaCount());
            loadCountSum.setTotalCount(loadCountSum.getTotalCount() + count.getTotalCount());
        }
        List<V_LOAD_TO_CHAIR_COUNT> loadCountSums = new ArrayList<>(1);
        loadCountSums.add(loadCountSum);
        return loadCountSums;
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.REMOVED || ev.getAction() == EntityEvent.MERGED || ev.getAction() == EntityEvent.CREATED) {
            doFilter(filterPanel.getFilterBean());
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        try {
            onCreateOrEdit(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(LOAD_TO_CHAIR.class, e.getId()));
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Uanble to edit or create load_to_chair", ex);
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        onCreateOrEdit((LOAD_TO_CHAIR) e);
    }

    private void onCreateOrEdit(LOAD_TO_CHAIR loadToChair) {
        STUDY_YEAR studyYear = (STUDY_YEAR) studyYearCB.getValue();
        loadToChair.setStudyYear(studyYear);

        FromItem specFI = curriculumQM.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        curriculumQM.addWhere("diplomaType", ECriteria.EQUAL, ((STUDENT_DIPLOMA_TYPE) diplomaTypeCB.getValue()).getId());
        curriculumQM.addWhere("entranceYear", ECriteria.EQUAL, currentYear.getId());
        ID departmentId = ((DEPARTMENT) chairCB.getValue()).getId();
        curriculumQM.addWhere(specFI, "department", ECriteria.EQUAL, departmentId);

        subjectQM.addWhere("chair", ECriteria.EQUAL, departmentId);
        ID studyYearId = studyYear.getId();
        semesterQM.addWhere("studyYear", ECriteria.EQUAL, studyYearId);
        groupQM.addWhere("studyYear", ECriteria.EQUAL, studyYearId);

        FromItem streamGroupFI = streamQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "stream");
        FromItem groupFI = streamGroupFI.addJoin(EJoin.INNER_JOIN, "group", GROUPS.class, "id");
        streamQM.addWhere(groupFI, "deleted", false);
        streamQM.addWhere(groupFI, "studyYear", ECriteria.EQUAL, studyYearId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        try {
            LOAD_TO_CHAIR loadToChair = (LOAD_TO_CHAIR) e;
            if ((loadToChair.getStream() == null) == (loadToChair.getGroup() == null)) {
                Message.showError(getUILocaleUtil().getMessage("stream.or.group.should.be.filled"));
                return false;
            }
            loadToChair.setTotalCount(loadToChair.getLcCount() + loadToChair.getPrCount() + loadToChair.getLbCount()
                    + loadToChair.getWithTeacherCount() + loadToChair.getRatingCount() + loadToChair.getExamCount()
                    + loadToChair.getControlCount() + loadToChair.getCourseWorkCount() + loadToChair.getDiplomaCount()
                    + loadToChair.getPracticeCount() + loadToChair.getMek() + loadToChair.getProtectDiplomaCount());
            loadToChair.setCredit(loadToChair.getSubject().getCreditability().getCredit().doubleValue());

            GROUPS group = loadToChair.getGroup();
            if (group == null) {
                QueryModel<GROUPS> groupOfStreamQM = new QueryModel<>(GROUPS.class);
                FromItem groupStreamFI = groupOfStreamQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "group");
                groupOfStreamQM.addWhere(groupStreamFI, "stream", ECriteria.EQUAL, loadToChair.getStream().getId());
                List<GROUPS> groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        groupOfStreamQM);
                group = groups.get(0);
            }
            loadToChair.setStudentNumber(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    V_GROUP.class, group.getId()).getStudentCount());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void generate(ENTRANCE_YEAR entranceYear) {
        try {
            QueryModel<LOAD_TO_CHAIR> loadToChairQM = new QueryModel<>(LOAD_TO_CHAIR.class);
            loadToChairQM.addWhere("createdYear", ECriteria.EQUAL, entranceYear.getId());
            List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(loadToChairQM);
            for (LOAD_TO_CHAIR load : loads) {
                CommonUtils.getQuery().delete(load);
            }

            insert(entranceYear, false);
            insert(entranceYear, true);
//              insert("v_load_to_chair", false);
//            insert("v_load_to_chair_work", true);

            setSeventhSemester(entranceYear, loadToChairQM);
            setEighthSemester(loadToChairQM);
            setPractice(entranceYear, loadToChairQM);
            setStudyYear(loadToChairQM);

        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to get loads to chair", e);
        }
    }

    private void insert(ENTRANCE_YEAR entranceYear, boolean workingChair) throws Exception {
        insert(entranceYear, "v_curriculum_detail", workingChair);
        insert(entranceYear, "v_elective_subject", workingChair);
        insert(entranceYear, "v_curriculum_add_program", workingChair);
        insert(entranceYear, "v_curriculum_after_semester", workingChair);
    }

    private void insert(ENTRANCE_YEAR entranceYear, String mainTable, boolean workingChair) throws Exception {
        StringBuilder sqlSB = new StringBuilder();
        if ("v_curriculum_after_semester".equals(mainTable)) {
            sqlSB.append("SELECT DISTINCT nextval('s_v_load_to_chair') id, " +
                    "                  subj.id                                subject_id, " +
                    "                  curr.id                                curriculum_id, " +
                    "                  cast(null as bigint)                   study_year_id, " +
                    "                  0                                      stream_id, " +
                    "                  gr.id                                  group_id, " +
                    "                  sem.id                                 semester_id, " +
                    "                  gr.student_count                       student_number, " +
                    "                  curr_after_sem.credit, " +
                    "                  cast(0 as numeric(3, 0))               lc_count, " +
                    "                  0                                      pr_count, " +
                    "                  0                                      lb_count, " +
                    "                  0                                      with_teacher_count, " +
                    "                  0                                      rating_count, " +
                    "                  0                                      exam_count, " +
                    "                  0                                      control_count, " +
                    "                  0                                      course_work_count, " +
                    "                  CASE " +
                    "                    WHEN subj.id = 1388 " +
                    "                            THEN gr.student_count * " + diplomaCount +//12
                    "                    ELSE 0 END                           diploma_count, " +
                    "                  CASE " +
                    "                    WHEN subj.practice_type_id IS NOT NULL " +
                    "                            THEN subj.week_number * gr.student_count " +
                    "                    ELSE 0 END                           practice_count, " +
                    "                  CASE " +
                    "                    WHEN subj.id = 1387 " +
                    "                            THEN gr.student_count * " + mekCount +//0.5
                    "                    ELSE 0 END                           mek, " +
                    "                  CASE " +
                    "                    WHEN subj.id = 1388 " +
                    "                            THEN gr.student_count * " + protectDiplomaCount +//0.6
                    "                    ELSE 0 END                           protect_diploma_count, " +
                    entranceYear.getId().getId() + " created_year_id" +
                    "  FROM v_curriculum_after_semester curr_after_sem " +
                    "         INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id " +
                    "         INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id ");
            if (workingChair) {
                sqlSB.append(" inner join speciality spec on spec.id = curr.speciality_id ");
            }
            sqlSB.append("         LEFT JOIN semester sem ON sem.id = curr_after_sem.semester_id " +
                    "         INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id " +
                    "         INNER JOIN study_year year ON year.id = gr.study_year_id " +
                    "  WHERE subj.deleted = FALSE " +
                    "    AND curr.deleted = FALSE " +
                    "    AND sem.study_year_id = gr.study_year_id");
            if (workingChair) {
                sqlSB.append(" and spec.chair_id = subj.chair_id ");
            }
        } else {
            sqlSB.append("SELECT DISTINCT nextval('s_v_load_to_chair') id," +
                    "                  subj.id                                subject_id, " +
                    "                  curr.id                                curriculum_id, " +
                    "                  cast(null as bigint)                   study_year_id, " +
                    "                  str.id                                 stream_id, " +
                    "                  0                                      group_id, " +
                    "                  sem.id                                 semester_id, " +
                    "                  str.student_count                      student_number, " +
                    "                  main_table.credit, " +
                    "                  subj.lc_count                          lc_count, " +
                    "                  subj.pr_count * str.group_count        pr_count, " +
                    "                  subj.lb_count * str.group_count        lb_count, " +
                    "                  credit * " + withTeacherCount + "                             with_teacher_count, " +//5
                    "                  case " +
                    "                    when curr.student_diploma_type_id not in (6, 7) then str.student_count * " + ratingCount +//0.25
                    "                    else 0 end                           rating_count, " +
                    "                  case " +
                    "                    when subj.ticket then str.student_count * " + ticketExamCount +//0.25
                    "                    else str.group_count * " + notTicketExamCount + " end         exam_count, " +//2
                    "                  CASE " +
                    "                    WHEN curr.student_diploma_type_id in (6, 7) " +
                    "                            THEN str.student_count * " + controlCount +//0.2
                    "                    ELSE 0 END                           control_count, " +
                    "                  CASE " +
                    "                    WHEN subj.course_work = TRUE " +
                    "                            THEN str.student_count * " + courseWorkCount +//0.25
                    "                    ELSE 0 END                           course_work_count, " +
                    "                  0                                      diploma_count, " +
                    "                  0                                      practice_count, " +
                    "                  0                                      mek, " +
                    "                  0                                      protect_diploma_count, " +
                    entranceYear.getId().getId() + " created_year_id" +
                    "  FROM " + mainTable + " main_table " +
                    "         INNER JOIN subject subj ON subj.id = main_table.subject_id " +
                    "         INNER JOIN curriculum curr ON main_table.curriculum_id = curr.id " +
                    "         INNER JOIN speciality spec on curr.speciality_id = spec.id " +
                    "         INNER JOIN semester sem ON sem.id = main_table.semester_id " +
                    "         INNER JOIN (SELECT DISTINCT str.id, str.group_count, str.student_count, " +
                    "gr.speciality_id, gr.study_year_id, str.subject_id " +
                    "                     FROM v_stream str " +
                    "                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id " +
                    "                            INNER JOIN v_group gr ON str_gr.group_id = gr.id " +
                    "                            INNER JOIN speciality spec on spec.id = gr.speciality_id ");
            if (workingChair) {
                sqlSB.append(" where str.stream_type_id = 1) str ON str.speciality_id = curr.speciality_id" +
                        " and str.subject_id = subj.id ");
            } else {
                sqlSB.append(" where str.stream_type_id = 2) str ON str.speciality_id = curr.speciality_id");
            }
            sqlSB.append("  WHERE subj.deleted = FALSE " +
                    "    AND curr.deleted = FALSE " +
                    "    AND sem.study_year_id = str.study_year_id " +
                    "    and spec.chair_id = subj.chair_id");
        }
        if (workingChair) {
            sqlSB.append(" and spec.chair_id != 23 ");
        }
        List<LOAD_TO_CHAIR> loads = CommonUtils.getQuery().lookup(sqlSB.toString(), new HashMap<>(),
                LOAD_TO_CHAIR.class);
        for (LOAD_TO_CHAIR load : loads) {
            load.setTotalCount(load.getLcCount() + load.getPrCount() + load.getLbCount() + load.getWithTeacherCount()
                    + load.getRatingCount() + load.getExamCount() + load.getControlCount() + load.getCourseWorkCount()
                    + load.getDiplomaCount() + load.getPracticeCount() + load.getMek() + load.getProtectDiplomaCount());
        }
        CommonUtils.getQuery().create(loads);

    }

    private void insert(String table, boolean workingChair) {
        try {
            StringBuilder sqlSB = new StringBuilder("INSERT INTO load_to_chair " +
                    "  SELECT " +
                    "    nextval('s_v_load_to_chair'), " +
                    "    subject_id, " +
                    "    curriculum_id, " +
                    "    study_year_id, " +
                    "    case when stream_id=0 then null else stream_id end stream_id, " +
                    "    case when group_id=0 then null else group_id end group_id, " +
                    "    semester_id, " +
                    "    student_number, " +
                    "    credit, " +
                    "    lc_count, " +
                    "    pr_count, " +
                    "    lb_count, " +
                    "    with_teacher_count, " +
                    "    rating_count, " +
                    "    exam_count, " +
                    "    control_count, " +
                    "    course_work_count, " +
                    "    diploma_count, " +
                    "    practice_count, " +
                    "    mek, " +
                    "    protect_diploma_count, " +
                    "    total_count ," +
                    " created_year_id" +
                    "  FROM ");
            sqlSB.append(table);
            if (workingChair) {
                sqlSB.append(" inner join curriculum curr" +
                        " on curr.id = curriculum_id" +
                        " inner join speciality spec" +
                        " on spec.id = curr.speciality_id" +
                        " where spec.chair_id != 23");
            }

            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(sqlSB.toString(), new HashMap<>());
        } catch (Exception ignored) {
        }
    }

    private void setSeventhSemester(ENTRANCE_YEAR entranceYear, QueryModel<LOAD_TO_CHAIR> loadToChairQM) throws Exception {
        Set<GROUPS> groups = new HashSet<>();
        List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(loadToChairQM);
        for (LOAD_TO_CHAIR loadToChair : loads) {
            if (loadToChair.getSemester().getId().equals(SEMESTER.SEVENTH)) {
                for (GROUPS group : CommonUtils.getGroupsByStream(loadToChair.getStream())) {
                    SUBJECT subject = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class)
                            .lookup(SUBJECT.class, SUBJECT.MANAGE_DIPLOM);
                    setLoadToChair(entranceYear, groups, loadToChair, group, subject, true);
                }
            }
        }
    }

    private void setEighthSemester(QueryModel<LOAD_TO_CHAIR> loadToChairQM) throws Exception {
        Set<GROUPS> groups = new HashSet<>();
        List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(loadToChairQM);
        for (LOAD_TO_CHAIR loadToChair : loads) {
            if (loadToChair.getSemester().getId().equals(SEMESTER.EIGHTH)) {
                if (loadToChair.getSubject().getPracticeType() != null) {
                    int studentNumber = (int) (loadToChair.getStudentNumber() * 0.4);
                    loadToChair.setStudentNumber(studentNumber);
                    double practiceCount = (double)
                            studentNumber * loadToChair.getSubject().getWeekNumber();
                    loadToChair.setPracticeCount(practiceCount);
                    loadToChair.setTotalCount(practiceCount);

                } else if (loadToChair.getSubject().getId().getId().longValue() == SUBJECT.DIPLOM) {
                    loadToChair.setDiplomaCount(0.0);
                    double mek = loadToChair.getStudentNumber() * 0.6;
                    loadToChair.setMek(mek);
                    double protectDiplomaCount = loadToChair.getStudentNumber() * 0.6 * 0.4;
                    loadToChair.setProtectDiplomaCount(protectDiplomaCount);
                    loadToChair.setTotalCount(mek + protectDiplomaCount);
                }
            }
        }
        CommonUtils.getQuery().merge(loads);
    }

    private void setPractice(ENTRANCE_YEAR entranceYear, QueryModel<LOAD_TO_CHAIR> loadToChairQM) throws Exception {
        Set<GROUPS> groups = new HashSet<>();
        List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(loadToChairQM);
        for (LOAD_TO_CHAIR loadToChair : loads) {
            if (loadToChair.getSemester().getId().equals(SEMESTER.EIGHTH)) {
                QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
                subjectQM.addWhere("deleted", false);
                subjectQM.addWhere("weekNumber", ECriteria.EQUAL, 10);
                subjectQM.addWhereNotNull("practiceType");
                subjectQM.addWhere("chair", ECriteria.EQUAL, loadToChair.getCurriculum().getSpeciality().
                        getDepartment().getId());
                SUBJECT subject;
                try {
                    subject = CommonUtils.getQuery().lookupSingle(subjectQM);
                } catch (NoResultException e) {
                    subject = null;
                }
                if (subject != null) {
                    GROUPS group = loadToChair.getGroup();
                    setLoadToChair(entranceYear, groups, loadToChair, group, subject, false);
                }
            }
        }

    }

    private void setLoadToChair(ENTRANCE_YEAR entranceYear, Set<GROUPS> groups, LOAD_TO_CHAIR loadToChair, GROUPS group, SUBJECT subject,
                                boolean isDiploma) throws Exception {
        if (!groups.contains(group)) {
            V_GROUP groupView = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(V_GROUP.class, group.getId());

            LOAD_TO_CHAIR newLoadToChair = new LOAD_TO_CHAIR();
            newLoadToChair.setId(SessionFacadeFactory.getSessionFacade(
                    CommonIDFacadeBean.class).getID("s_v_load_to_chair"));
            newLoadToChair.setCreatedYear(entranceYear);
            newLoadToChair.setSubject(subject);
            newLoadToChair.setCurriculum(loadToChair.getCurriculum());
            newLoadToChair.setGroup(group);
            newLoadToChair.setSemester(loadToChair.getSemester());
            newLoadToChair.setCredit(subject.getCreditability().getCredit().
                    doubleValue());
            newLoadToChair.setStudyYear(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class,
                    STUDY_YEAR.FOURTH_STUDY_YEAR));

            if (isDiploma) {
                Integer studentNumber = (int) Math.round(groupView.getStudentCount() * 0.4);
                newLoadToChair.setStudentNumber(studentNumber);
                double diplomaCount = studentNumber.doubleValue() * 12;
                newLoadToChair.setDiplomaCount(diplomaCount);
                newLoadToChair.setTotalCount(diplomaCount);

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(
                        newLoadToChair);

                newLoadToChair.setId(SessionFacadeFactory.getSessionFacade(
                        CommonIDFacadeBean.class).getID("s_v_load_to_chair"));
                newLoadToChair.setSemester(CommonUtils.getQuery().lookup(SEMESTER.class,
                        SEMESTER.EIGHTH));
            } else {
                Integer studentNumber = (int) Math.round(groupView.getStudentCount() * 0.6);
                newLoadToChair.setStudentNumber(studentNumber);
                double practiceCount = studentNumber.doubleValue() * subject.getWeekNumber();
                newLoadToChair.setPracticeCount(practiceCount);
                newLoadToChair.setTotalCount(practiceCount);
            }
            CommonUtils.getQuery().createNoID(newLoadToChair);
            groups.add(group);
        }
    }

    private void setStudyYear(QueryModel<LOAD_TO_CHAIR> loadToChairQM) throws Exception {
        List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(loadToChairQM);
        for (LOAD_TO_CHAIR loadToChair : loads) {
            loadToChair.setStudyYear(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class,
                    ID.valueOf(CommonUtils.getStudyYearByEntranceYear(
                            loadToChair.getCurriculum().getEntranceYear()))));
        }
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(loads);
    }
}
