package kz.halyqsoft.univercity.modules.teacherprofessionalcompositionload;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.filter.FChairFilter;
import kz.halyqsoft.univercity.filter.panel.ChairFilterPanel;
import kz.halyqsoft.univercity.modules.stream.dialogs.DetailDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.FormWidgetDialog;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Assylkhan
 * on 24.11.2018
 * @project kz.halyqsoft.univercity
 */
public class LoadToTeacherView extends AbstractTaskView implements FilterPanelListener {

    public LoadToTeacherView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ChairFilterPanel(new FChairFilter());
    }

    private ChairFilterPanel filterPanel;
    private ENTRANCE_YEAR currentYear;

    private GridWidget loadGW;
    private FromItem teacherFI;

    private QueryModel subjectQM;
    private QueryModel semesterQM;
    private QueryModel curriculumQM;
    private QueryModel streamQM;
    private QueryModel groupQM;
    private QueryModel teacherQM;

    private ComboBox chairCB;
    private ComboBox studyYearCB;
    private ComboBox diplomaTypeCB;

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
                        QueryModel<LOAD_TO_TEACHER> teacherLoadsQM = new QueryModel<>(LOAD_TO_TEACHER.class);
                        List<LOAD_TO_TEACHER> loadToTeachers = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(teacherLoadsQM);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(loadToTeachers);
                        insert();
                        addTeachers();
                        doFilter(filterPanel.getFilterBean());
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to get loads to chair", e);
                    }
                }

                public void addTeachers(){
                    try {
                        QueryModel<LOAD_TO_TEACHER> teacherLoadsQM = new QueryModel<>(LOAD_TO_TEACHER.class);
                        List<LOAD_TO_TEACHER> loadToTeachers = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(teacherLoadsQM);

                        for(LOAD_TO_TEACHER loadToTeacher : loadToTeachers){
                            QueryModel<V_EMPLOYEE> vEmployeeQM = new QueryModel<>(V_EMPLOYEE.class);
                            FromItem fi = vEmployeeQM.addJoin(EJoin.INNER_JOIN, "id" , TEACHER_SUBJECT.class, "employee");
                            vEmployeeQM.addWhere(fi, "subject" , ECriteria.EQUAL, loadToTeacher.getSubject().getId());
                            List<V_EMPLOYEE> vEmployees = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(vEmployeeQM);
                            if(vEmployees.size()>0){
                                loadToTeacher.setTeacher(vEmployees.get(0));
                            }
                        }

                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(loadToTeachers);
                    }catch (Exception e){
                        CommonUtils.showMessageAndWriteLog("Unable to get loads to chair", e);
                    }
                }

                public void insert(){
                    String sql = "INSERT INTO load_to_teacher\n" +
                            "(id, subject_id, curriculum_id, study_year_id, stream_id, group_id, semester_id, student_number, credit, lc_count, pr_count, lb_count, with_teacher_count, rating_count, exam_count, control_count, course_work_count, diploma_count, practice_count, mek, protect_diploma_count, total_count )\n" +
                            "  SELECT\n" +
                            "    nextval('s_load_to_teacher'),\n" +
                            "    ltc.subject_id,\n" +
                            "    ltc.curriculum_id,\n" +
                            "    ltc.study_year_id,\n" +
                            "    ltc.stream_id,\n" +
                            "    ltc.group_id,\n" +
                            "    CASE WHEN ltc.semester_id = 0   THEN NULL else ltc.semester_id END ,\n" +
                            "    ltc.student_number,\n" +
                            "    ltc.credit,\n" +
                            "    ltc.lc_count,\n" +
                            "    ltc.pr_count,\n" +
                            "    ltc.lb_count,\n" +
                            "    ltc.with_teacher_count,\n" +
                            "    ltc.rating_count,\n" +
                            "    ltc.exam_count,\n" +
                            "    ltc.control_count,\n" +
                            "    ltc.course_work_count,\n" +
                            "    ltc.diploma_count,\n" +
                            "    ltc.practice_count,\n" +
                            "    ltc.mek,\n" +
                            "    ltc.protect_diploma_count,\n" +
                            "    ltc.total_count\n" +
                            "  FROM load_to_chair ltc ;";
                    Map params = new HashMap<>();
                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql,params);
                    }catch (Exception ignored){
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

            loadGW = new GridWidget(LOAD_TO_TEACHER.class);
            loadGW.addEntityListener(this);
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);

            Button openBtn = new Button(getUILocaleUtil().getCaption("open"));
            openBtn.setIcon(FontAwesome.SIGN_IN);
            openBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if (loadGW.getSelectedEntity() != null) {
                        LOAD_TO_TEACHER loadToChair = (LOAD_TO_TEACHER) loadGW.getSelectedEntity();
                        DetailDialog detailDialog = new DetailDialog(loadToChair.getStream(), false);
                        detailDialog.getStreamGroupGW().showToolbar(false);
                    } else {
                        Message.showError(getUILocaleUtil().getMessage("chooseARecord"));
                    }
                }
            });

            Button divideBtn = new Button(getUILocaleUtil().getCaption("divide"));
            divideBtn.setIcon(FontAwesome.ADJUST);
            divideBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if (loadGW.getSelectedEntity() != null) {
                        LOAD_TO_TEACHER loadToTeacher = (LOAD_TO_TEACHER) loadGW.getSelectedEntity();
                        LOAD_TO_TEACHER newLoadToTeacher = new LOAD_TO_TEACHER();
                        newLoadToTeacher.setSubject(loadToTeacher.getSubject());
                        newLoadToTeacher.setStudyYear(loadToTeacher.getStudyYear());
                        newLoadToTeacher.setStream(loadToTeacher.getStream());
                        newLoadToTeacher.setGroup(loadToTeacher.getGroup());
                        newLoadToTeacher.setSemester(loadToTeacher.getSemester());
                        newLoadToTeacher.setStudentNumber(loadToTeacher.getStudentNumber());
                        newLoadToTeacher.setCredit(loadToTeacher.getCredit());
                        newLoadToTeacher.setTeacher(loadToTeacher.getTeacher());
                        newLoadToTeacher.setCurriculum(loadToTeacher.getCurriculum());

                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newLoadToTeacher);
                            doFilter(filterPanel.getFilterBean());
                        }catch (Exception e){
                            CommonUtils.showMessageAndWriteLog("Unable to create load to teacher" , e);
                        }
                    } else {
                        Message.showError(getUILocaleUtil().getMessage("chooseARecord"));
                    }
                }
            });
            loadGW.getToolbarPanel().addComponent(divideBtn);
            loadGW.getToolbarPanel().addComponent(openBtn);
            loadGW.getToolbarPanel().setSizeUndefined();

            DBGridModel loadGM = (DBGridModel) loadGW.getWidgetModel();
            loadGM.setTitleVisible(false);
            loadGM.setMultiSelect(false);
            loadGM.setRefreshType(ERefreshType.MANUAL);
            loadGM.setCrudEntityClass(LOAD_TO_TEACHER.class);

            subjectQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("subject")).getQueryModel();
            subjectQM.addWhere("deleted", false);

            curriculumQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("curriculum")).getQueryModel();
            curriculumQM.addWhere("deleted", false);

            groupQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("group")).getQueryModel();
            groupQM.addWhere("deleted", false);

            teacherQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("teacher")).getQueryModel();
            teacherFI = teacherQM.addJoin(EJoin.INNER_JOIN, "id" ,TEACHER_SUBJECT.class, "employee" );

            semesterQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("semester")).getQueryModel();
            streamQM = ((FKFieldModel) loadGM.getFormModel().getFieldModel("stream")).getQueryModel();




            FChairFilter chairFilter = (FChairFilter) filterPanel.getFilterBean();
            if (chairFilter.hasFilter()) {
                doFilter(chairFilter);
            }

            getContent().addComponent(loadGW);
            getContent().setComponentAlignment(loadGW, Alignment.MIDDLE_CENTER);

        } else {
            Label semIsNotGoingNowLabel = CommonUtils.getSemesterIsGoingNowLabel();
            getContent().addComponent(semIsNotGoingNowLabel);
            getContent().setComponentAlignment(semIsNotGoingNowLabel, Alignment.MIDDLE_CENTER);
        }
    }

    private void refresh(List<LOAD_TO_TEACHER> loads) {
        try {
            ((DBGridModel) loadGW.getWidgetModel()).setEntities(loads);
            loadGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", ex);
        }
    }

    private List<LOAD_TO_TEACHER> getLoads(DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                                       STUDY_YEAR studyYear) {
        QueryModel<LOAD_TO_TEACHER> loadQM = new QueryModel<>(LOAD_TO_TEACHER.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem subjFI = loadQM.addJoin(EJoin.INNER_JOIN, "subject", SUBJECT.class, "id");
        loadQM.addWhere(subjFI, "chair", ECriteria.EQUAL, chair.getId());
//        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
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



    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FChairFilter chairFilter = (FChairFilter) abstractFilterBean;
        if (chairFilter.getChair() != null && chairFilter.getStudentDiplomaType() != null
                && chairFilter.getStudyYear() != null) {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, true);
            List<LOAD_TO_TEACHER> loads = getLoads(chairFilter.getChair(), chairFilter.getStudentDiplomaType(),chairFilter.getStudyYear());
            refresh(loads);
        } else {
            loadGW.setButtonEnabled(AbstractToolbar.ADD_BUTTON, false);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.REMOVED || ev.getAction() == EntityEvent.MERGED || ev.getAction() == EntityEvent.CREATED) {
            doFilter(filterPanel.getFilterBean());
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        if(source.equals(loadGW)){
//            LOAD_TO_TEACHER loadToTeacher = (LOAD_TO_TEACHER) e;
//            teacherQM.addWhere(teacherFI, "subject" , ECriteria.EQUAL, loadToTeacher.getSubject().getId());
            LoadToTeacherEditDialog loadToTeacherEditDialog = new LoadToTeacherEditDialog(this, (LOAD_TO_TEACHER) e);
        }

        return false;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {

    }


    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }

    public ChairFilterPanel getFilterPanel() {
        return filterPanel;
    }
}
