package kz.halyqsoft.univercity.modules.loadtochair;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
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
    private QueryModel curriculumQM;
    private QueryModel streamQM;
    private QueryModel groupQM;

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
            generateButton.setCaption(getUILocaleUtil().getCaption("generate"));
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

            loadGW = new GridWidget(V_LOAD_TO_CHAIR_WITH_GROUPS.class);
            loadGW.addEntityListener(this);
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);
            Button openBtn = new Button(getUILocaleUtil().getCaption("open"));
            openBtn.setIcon(FontAwesome.SIGN_IN);
            openBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if(loadGW.getSelectedEntity()!=null){
                        V_LOAD_TO_CHAIR_WITH_GROUPS loadToChair = (V_LOAD_TO_CHAIR_WITH_GROUPS) loadGW.getSelectedEntity();
                        DetailDialog detailDialog = new DetailDialog(loadToChair.getStream(),false);
                        detailDialog.getStreamGroupGW().showToolbar(false);
                    }else{
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

    private void refresh(List<V_LOAD_TO_CHAIR_WITH_GROUPS> loads, List<V_LOAD_TO_CHAIR_COUNT> loadCounts,
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

    private List<V_LOAD_TO_CHAIR_WITH_GROUPS> getLoads(DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                         STUDY_YEAR studyYear) {
        QueryModel<V_LOAD_TO_CHAIR_WITH_GROUPS> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_WITH_GROUPS.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem subjFI = loadQM.addJoin(EJoin.INNER_JOIN, "subject", SUBJECT.class, "id");
        loadQM.addWhere(subjFI, "chair", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        loadQM.addOrder("semester");
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
            List<V_LOAD_TO_CHAIR_WITH_GROUPS> loads = getLoads(chairFilter.getChair(), chairFilter.getStudentDiplomaType(),
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
        onCreateOrEdit((LOAD_TO_CHAIR) e);
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
}
