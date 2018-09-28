package kz.halyqsoft.univercity.modules.loadtochair;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.LOAD_TO_CHAIR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT_ALL;
import kz.halyqsoft.univercity.filter.FChairFilter;
import kz.halyqsoft.univercity.filter.panel.ChairFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private ComboBox chairCB;
    private ComboBox studyYearCB;
    private ComboBox diplomaTypeCB;

    public LoadToChairView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ChairFilterPanel(new FChairFilter());
    }


    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        if (semesterData != null) {
            currentYear = semesterData.getYear();

            HorizontalLayout loadHL = CommonUtils.createButtonPanel();

            Label yearLabel = new Label();
            yearLabel.setWidthUndefined();
            yearLabel.setValue(currentYear.toString());
            loadHL.addComponent(yearLabel);

            Button generateButton = new Button();
            generateButton.setCaption("generate");//TODO
            generateButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        QueryModel<LOAD_TO_CHAIR> loadToChairQM = new QueryModel<>(LOAD_TO_CHAIR.class);
                        List<LOAD_TO_CHAIR> loads = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(loadToChairQM);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(loads);
                        try {
                            String sql = "INSERT INTO load_to_chair " +
                                    "  SELECT " +
                                    "    nextval('s_v_load_to_chair'), " +
                                    "    subject_id, " +
                                    "    curriculum_id, " +
                                    "    study_year_id, " +
                                    "    case when stream_id=0 then null else stream_id end stream_id, " +
                                    "    case when group_id=0 then null else group_id end stream_id, " +
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
                                    "    total_count " +
                                    "  FROM v_load_to_chair;";
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookupItemsList(sql, new HashMap<>());
                        } catch (Exception ignored) {
                        }
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to get loads to chair", e);
                    }
                }
            });
            loadHL.addComponent(generateButton);

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

            getContent().addComponent(filterPanel);
            getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

            loadGW = new GridWidget(LOAD_TO_CHAIR.class);
            loadGW.addEntityListener(this);
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);

            DBGridModel loadGM = (DBGridModel) loadGW.getWidgetModel();
            loadGM.setTitleVisible(false);
            loadGM.setMultiSelect(false);
            loadGM.setRefreshType(ERefreshType.MANUAL);

            subjectQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("subject")).getQueryModel();
            subjectQM.addWhere("deleted", false);

            semesterQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("semester")).getQueryModel();

            countGW = new GridWidget(V_LOAD_TO_CHAIR_COUNT.class);
            countGW.showToolbar(false);

            DBGridModel countGM = (DBGridModel) countGW.getWidgetModel();
            countGM.setMultiSelect(false);
            countGM.setHeightByRows(1);
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
        } else {
            Label semIsNotGoingNowLabel = CommonUtils.getSemesterIsGoingNowLabel();
            getContent().addComponent(semIsNotGoingNowLabel);
            getContent().setComponentAlignment(semIsNotGoingNowLabel, Alignment.MIDDLE_CENTER);
        }
    }

    private void refresh(List<LOAD_TO_CHAIR> loads, List<V_LOAD_TO_CHAIR_COUNT> loadCounts,
                         List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts) {
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

    private List<LOAD_TO_CHAIR> getLoads(DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                         STUDY_YEAR studyYear) {
        QueryModel<LOAD_TO_CHAIR> loadQM = new QueryModel<>(LOAD_TO_CHAIR.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem subjFI = loadQM.addJoin(EJoin.INNER_JOIN, "subject", SUBJECT.class, "id");
        loadQM.addWhere(subjFI, "chair", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", e);
        }
        return new ArrayList<>();
    }

    private List<V_LOAD_TO_CHAIR_COUNT> getLoadCount(DEPARTMENT chair,
                                                     STUDENT_DIPLOMA_TYPE studentDiplomaType, STUDY_YEAR studyYear) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        loadQM.addWhere("chair", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
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
                                                            STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT_ALL> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT_ALL.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        loadQM.addWhere("chair", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
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
                && chairFilter.getStudyYear() != null) {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, true);
            List<LOAD_TO_CHAIR> loads = getLoads(chairFilter.getChair(), chairFilter.getStudentDiplomaType(),
                    chairFilter.getStudyYear());
            List<V_LOAD_TO_CHAIR_COUNT> loadCounts = getLoadCount(chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType(), chairFilter.getStudyYear());
            List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts = getLoadAllCount(chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType());
            refresh(loads, loadCounts, loadAllCounts);
        } else {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.REMOVED || ev.getAction() == EntityEvent.MERGED) {
            doFilter(filterPanel.getFilterBean());
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        subjectQM.addWhere("chair", ECriteria.EQUAL, ((DEPARTMENT) chairCB.getValue()).getId());
        semesterQM.addWhere("studyYear", ECriteria.EQUAL, ((STUDY_YEAR) studyYearCB.getValue()).getId());
        return true;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        subjectQM.addWhere("chair", ECriteria.EQUAL, ((DEPARTMENT) chairCB.getValue()).getId());
        semesterQM.addWhere("studyYear", ECriteria.EQUAL, ((STUDY_YEAR) studyYearCB.getValue()).getId());
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        try {
//            QueryModel<CURRICULUM> curriculumQM=new QueryModel<>(CURRICULUM.class);
//            FromItem specFI=curriculumQM.addJoin(EJoin.INNER_JOIN,"speciality",SPECIALITY.class,"id");
//            FromItem depFI=curriculumQM.addJoin(EJoin.INNER_JOIN,"department",DEPARTMENT.class,"id");
//            curriculumQM.addWhere("deleted",false);
//            curriculumQM.addWhere("diplomaType",ECriteria.EQUAL,((STUDENT_DIPLOMA_TYPE)diplomaTypeCB.getValue()).getId());
//            curriculumQM.addWhere("entranceYear",ECriteria.EQUAL,((ENTRANCE_YEAR)diplomaTypeCB.getValue()).getId());
            LOAD_TO_CHAIR loadToChair = (LOAD_TO_CHAIR) e;
//            loadToChair.setCurriculum();
            loadToChair.setTotalCount(loadToChair.getLcCount() + loadToChair.getPrCount() + loadToChair.getLbCount()
                    + loadToChair.getWithTeacherCount() + loadToChair.getRatingCount() + loadToChair.getExamCount()
                    + loadToChair.getControlCount() + loadToChair.getCourseWorkCount() + loadToChair.getDiplomaCount()
                    + loadToChair.getPracticeCount() + loadToChair.getMek() + loadToChair.getProtectDiplomaCount());
            loadToChair.setCredit(loadToChair.getSubject().getCreditability().getCredit().doubleValue());
            loadToChair.setStudentNumber(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    V_GROUP.class, loadToChair.getGroup().getId()).getStudentCount());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.preSave(source, e, isNew, buttonId);//TODO
    }
}
