package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.modules.curriculum.working.main.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.main.CurriculumView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.footer.EColumnFooterType;
import org.r3a.common.vaadin.widget.grid.footer.IntegerColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;

/**
 * @author Omarbek Dinassil
 * @created 02.11.2018
 */
@SuppressWarnings({"serial", "unchecked"})
public final class SemesterDetailPanel extends AbstractCurriculumPanel implements EntityListener {

    private final SEMESTER semester;
    private CURRICULUM curriculum;
    private ENTRANCE_YEAR entranceYear;
    private GridWidget mainSubjectsGW;
    private GridWidget electiveSubjectsGW;
    private GridWidget addingSubjectsGW;

    public SemesterDetailPanel(CurriculumView parentView, SEMESTER semester) {
        super(parentView);
        this.semester = semester;
    }

    @Override
    public void initPanel() {
        if (semester != null) {
            mainSubjectsGW = initGridWidget(V_CURRICULUM_DETAIL.class, CURRICULUM_DETAIL.class,
                    SubjectsType.MAIN_SUBJECTS);
            electiveSubjectsGW = initGridWidget(V_ELECTIVE_SUBJECT.class, ELECTIVE_SUBJECT.class,
                    SubjectsType.ELECTIVE_SUBJECTS);
        } else {
            addingSubjectsGW = initGridWidget(V_CURRICULUM_ADD_PROGRAM.class, CURRICULUM_ADD_PROGRAM.class,
                    SubjectsType.ADDING_SUBJECTS);
        }

        if (curriculum != null) {
            refreshSubjects(curriculum, semester);
        }
    }

    private GridWidget initGridWidget(Class<? extends Entity> view,
                                      Class<? extends Entity> table,
                                      SubjectsType subjectsType) {
        GridWidget currentGW = new GridWidget(view);
        currentGW.addEntityListener(this);
        currentGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        currentGW.setButtonDescription(AbstractToolbar.ADD_BUTTON, "add.from.iups");
        //currentGW.setButt(AbstractToolbar.ADD_BUTTON, "150px");
        currentGW.addButtonClickListener(AbstractToolbar.ADD_BUTTON, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        addSubjectsFromIups(curriculum, semester, subjectsType);
                    }
                });
            }
        });
        currentGW.addButtonClickListener(AbstractToolbar.REFRESH_BUTTON, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshSubjects(curriculum, semester);
            }
        });

        DBGridModel currentGM = (DBGridModel) currentGW.getWidgetModel();
        currentGM.setCrudEntityClass(table);
        currentGM.setRefreshType(ERefreshType.MANUAL);
        currentGM.setFooterVisible(true);

        IntegerColumnFooterModel currentFooterModel = new IntegerColumnFooterModel();
        currentFooterModel.setFooterType(EColumnFooterType.SUM);
        currentFooterModel.setColumnName("credit");
        currentGM.addFooterModel(currentFooterModel);

        getContent().addComponent(currentGW);
        getContent().setComponentAlignment(currentGW, Alignment.TOP_CENTER);

        return currentGW;
    }

    private void addSubjectsFromIups(CURRICULUM curriculum, SEMESTER semester,
                                     SubjectsType subjectsType) {
        boolean isMainSubjects = subjectsType.equals(SubjectsType.MAIN_SUBJECTS);
        boolean isElectiveSubjects = subjectsType.equals(SubjectsType.ELECTIVE_SUBJECTS);
        boolean isAddingSubjects = subjectsType.equals(SubjectsType.ADDING_SUBJECTS);
        try {
            StringBuilder sqlSB = new StringBuilder("SELECT DISTINCT sem_subj.subject_id");
            if (isAddingSubjects) {
                sqlSB.append(", sem_subj.semester_data_id");
            }
            sqlSB.append(" FROM student_subject stu_subj " +
                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id " +
                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id " +
                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id " +
                    "  INNER JOIN student stu ON stu_edu.student_id = stu.id " +
                    "                            AND stu_edu.child_id IS NULL " +
                    "  INNER JOIN users usr ON stu.id = usr.id " +
                    "  INNER JOIN teacher_subject teach_subj  " +
                    "    ON sem_subj.subject_id = teach_subj.subject_id " +
                    "  INNER JOIN student_teacher_subject stu_teach_subj " +
                    "    ON stu_teach_subj.teacher_subject_id = teach_subj.id " +
                    "WHERE usr.deleted = FALSE AND usr.locked = FALSE " +
                    "      AND subj.deleted = FALSE AND subj.level_id = 1 ");
            if (isMainSubjects) {
                sqlSB.append(" AND subj.mandatory = TRUE AND subj.practice_type_id IS NULL" +
                        " AND subj.subject_cycle_id < 4");
            } else if (isElectiveSubjects) {
                sqlSB.append(" AND subj.mandatory = FALSE AND subj.practice_type_id IS NULL" +
                        " AND subj.subject_cycle_id < 4");
            } else if (isAddingSubjects) {
                sqlSB.append(" AND subj.practice_type_id IS NULL" +
                        " AND subj.subject_cycle_id = 4");
            }
            sqlSB.append(" AND stu_edu.speciality_id = ?1 AND stu.diploma_type_id = ?2 " +
                    "      AND stu.entrance_year_id = ?3 ");
            if (!isAddingSubjects) {
                sqlSB.append("AND sem_subj.semester_data_id = ?4 ");
            }
            sqlSB.append("ORDER BY sem_subj.subject_id");
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, curriculum.getSpeciality().getId().getId());
            params.put(2, curriculum.getDiplomaType().getId().getId());
            params.put(3, curriculum.getEntranceYear().getId().getId());
            if (!isAddingSubjects) {
                SEMESTER_DATA semesterData = CommonUtils.getSemesterDataBySemesterAndEntranceYear(semester,
                       entranceYear);
                params.put(4, semesterData.getId().getId());
                List<Long> subjectIds = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookupItemsList(sqlSB.toString(), params);
                if (isMainSubjects) {
                    initCurriculumDetail(curriculum, semester, subjectIds, semesterData);
                } else {
                    initElectiveSubject(curriculum, semester, subjectIds, semesterData);
                }
            } else {
                List<Object> subjectsBySemester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookupItemsList(sqlSB.toString(), params);
                initAddingSubjects(curriculum, subjectsBySemester);
            }
            CommonUtils.showSavedNotification();
            refreshSubjects(curriculum, semester);
            getParentView().setTotalCreditSum();
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to add subjects from iups", e);
        }
    }

    private void initAddingSubjects(CURRICULUM curriculum, List<Object> subjectsBySemester)
            throws Exception {
        QueryModel<CURRICULUM_ADD_PROGRAM> curriculumAddProgramQM = new QueryModel<>(CURRICULUM_ADD_PROGRAM.class);
        curriculumAddProgramQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        List<CURRICULUM_ADD_PROGRAM> curriculumAddPrograms = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(curriculumAddProgramQM);
        for (CURRICULUM_ADD_PROGRAM curriculumAddProgram : curriculumAddPrograms) {
            curriculumAddProgram.setDeleted(true);
        }
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculumAddPrograms);
        for (Object object : subjectsBySemester) {
            Object[] objects = (Object[]) object;
            Long subjectId = (Long) objects[0];
            Long semesterDataId = (Long) objects[1];

            CURRICULUM_ADD_PROGRAM curriculumAddProgram = new CURRICULUM_ADD_PROGRAM();
            curriculumAddProgram.setDeleted(false);
            curriculumAddProgram.setCreated(new Date());
            curriculumAddProgram.setCurriculum(curriculum);
            curriculumAddProgram.setSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean
                    .class).lookup(SUBJECT.class, ID.valueOf(subjectId)));
            SEMESTER_DATA semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean
                    .class).lookup(SEMESTER_DATA.class, ID.valueOf(semesterDataId));
            curriculumAddProgram.setSemesterData(semesterData);
            curriculumAddProgram.setSemester(CommonUtils.getSemesterBySemesterDataAndEntranceYear(
                    semesterData, entranceYear));
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(curriculumAddProgram);
        }
    }

    private void initElectiveSubject(CURRICULUM curriculum, SEMESTER semester,
                                     List<Long> subjectIds, SEMESTER_DATA semesterData) throws Exception {
        QueryModel<ELECTIVE_SUBJECT> curriculumDetailQM = new QueryModel<>(
                ELECTIVE_SUBJECT.class);
        curriculumDetailQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        curriculumDetailQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
        List<ELECTIVE_SUBJECT> electiveSubjects = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(curriculumDetailQM);
        for (ELECTIVE_SUBJECT electiveSubject : electiveSubjects) {
            electiveSubject.setDeleted(true);
        }
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(
                electiveSubjects);
        for (Long subjectId : subjectIds) {
            ELECTIVE_SUBJECT electiveSubject = new ELECTIVE_SUBJECT();
            electiveSubject.setDeleted(false);
            electiveSubject.setConsiderCredit(false);
            electiveSubject.setCreated(new Date());
            electiveSubject.setCurriculum(curriculum);
            electiveSubject.setSemester(semester);
            electiveSubject.setSubject(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(SUBJECT.class, ID.valueOf(subjectId)));
            electiveSubject.setSemesterData(semesterData);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(
                    electiveSubject);
        }
    }

    private void initCurriculumDetail(CURRICULUM curriculum, SEMESTER semester,
                                      List<Long> subjectIds, SEMESTER_DATA semesterData) throws Exception {
        QueryModel<CURRICULUM_DETAIL> curriculumDetailQM = new QueryModel<>(
                CURRICULUM_DETAIL.class);
        curriculumDetailQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        curriculumDetailQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
        List<CURRICULUM_DETAIL> curriculumDetails = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(curriculumDetailQM);
        for (CURRICULUM_DETAIL curriculumDetail : curriculumDetails) {
            curriculumDetail.setDeleted(true);
        }
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(
                curriculumDetails);
        for (Long subjectId : subjectIds) {
            CURRICULUM_DETAIL curriculumDetail = new CURRICULUM_DETAIL();
            curriculumDetail.setDeleted(false);
            curriculumDetail.setCreated(new Date());
            curriculumDetail.setCurriculum(curriculum);
            curriculumDetail.setSemester(semester);
            curriculumDetail.setSubject(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(SUBJECT.class, ID.valueOf(subjectId)));
            curriculumDetail.setSemesterData(semesterData);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(
                    curriculumDetail);
        }
    }

    private void refreshSubjects(CURRICULUM curriculum, SEMESTER semester) {
        try {
            if (semester != null) {
                refreshMainSubjects(curriculum, semester);
                refreshElectiveSubjects(curriculum, semester);
            } else {
                refreshAddingSubjects(curriculum);
            }
            getParentView().setTotalCreditSum();
            getParentView().countSumOfCreditBySemester();
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh subjects", e);
        }
    }

    private void refreshMainSubjects(CURRICULUM curriculum, SEMESTER semester) throws Exception {
        List<V_CURRICULUM_DETAIL> curriculumDetails = new ArrayList<>();
        if (curriculum.getId() != null) {
            QueryModel<V_CURRICULUM_DETAIL> curriculumDetailQM = new QueryModel<>(
                    V_CURRICULUM_DETAIL.class);
            curriculumDetailQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
            curriculumDetailQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
            curriculumDetails = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(curriculumDetailQM);

        }
        ((DBGridModel) mainSubjectsGW.getWidgetModel()).setEntities(curriculumDetails);
        mainSubjectsGW.refresh();
    }

    private void refreshElectiveSubjects(CURRICULUM curriculum, SEMESTER semester) throws Exception {
        List<V_ELECTIVE_SUBJECT> electiveSubjects = new ArrayList<>();
        if (curriculum.getId() != null) {
            QueryModel<V_ELECTIVE_SUBJECT> electiveSubjectQM = new QueryModel<>(
                    V_ELECTIVE_SUBJECT.class);
            electiveSubjectQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
            electiveSubjectQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
            electiveSubjects = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(electiveSubjectQM);

        }
        ((DBGridModel) electiveSubjectsGW.getWidgetModel()).setEntities(electiveSubjects);
        electiveSubjectsGW.refresh();
    }

    private void refreshAddingSubjects(CURRICULUM curriculum) throws Exception {
        List<V_CURRICULUM_ADD_PROGRAM> addingSubjects = new ArrayList<>();
        if (curriculum.getId() != null) {
            QueryModel<V_CURRICULUM_ADD_PROGRAM> addingSubjectQM = new QueryModel<>(
                    V_CURRICULUM_ADD_PROGRAM.class);
            addingSubjectQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
            addingSubjects = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(addingSubjectQM);

        }
        ((DBGridModel) addingSubjectsGW.getWidgetModel()).setEntities(addingSubjects);
        addingSubjectsGW.refresh();
    }

    @Override
    public void refresh() throws Exception {
        refreshSubjects(curriculum, semester);
    }

    public Integer getTotalCredit(SubjectsType subjectsType) {
        if (SubjectsType.ADDING_SUBJECTS.equals(subjectsType)) {

            Integer addingSubjectsCredit = (Integer) ((DBGridModel) addingSubjectsGW.getWidgetModel()).
                    getFooterValue("credit");
            List<Entity> addingSubjects = addingSubjectsGW.getAllEntities();
            for (Entity item : addingSubjects) {
                V_CURRICULUM_ADD_PROGRAM curriculumAddProgram = (V_CURRICULUM_ADD_PROGRAM) item;
                if (curriculumAddProgram.getSubjectCode() == null) {
                    addingSubjectsCredit -= curriculumAddProgram.getCredit();
                }
            }
            addingSubjectsGW.setFooterValue("credit", addingSubjectsCredit);
            return addingSubjectsCredit;
        } else {
            Integer mainSubjectsCredit = (Integer) ((DBGridModel) mainSubjectsGW.getWidgetModel()).
                    getFooterValue("credit");
            List<Entity> mainSubjects = mainSubjectsGW.getAllEntities();
            for (Entity item : mainSubjects) {
                V_CURRICULUM_DETAIL curriculumDetail = (V_CURRICULUM_DETAIL) item;
                if (curriculumDetail.getSubjectCode() == null) {
                    mainSubjectsCredit -= curriculumDetail.getCredit();
                }
            }
            mainSubjectsGW.setFooterValue("credit", mainSubjectsCredit);

            Integer electiveSubjectsCredit = (Integer) ((DBGridModel) electiveSubjectsGW.getWidgetModel()).
                    getFooterValue("credit");
            List<Entity> electiveSubjects = electiveSubjectsGW.getAllEntities();
            for (Entity item : electiveSubjects) {
                V_ELECTIVE_SUBJECT electiveSubject = (V_ELECTIVE_SUBJECT) item;
                if (electiveSubject.getSubjectCode() == null
                        || !electiveSubject.isConsiderCredit()) {
                    electiveSubjectsCredit -= electiveSubject.getCredit();
                }
            }
            electiveSubjectsGW.setFooterValue("credit", electiveSubjectsCredit);

            return mainSubjectsCredit + electiveSubjectsCredit;
        }
    }

    public void checkForConform(SubjectsType subjectsType) throws Exception {
        if (isCheckSemester()) {
            if (getTotalCredit(subjectsType) == 0) {
                String message = String.format(getUILocaleUtil().getMessage(
                        "no.subject.for.semester"), getSemester().getId().toString());
                throw new Exception(message);
            }
        }
    }

    private boolean isCheckSemester() {
        return semester.getId().getId().intValue() < 8;
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if (entityEvent.getAction() == EntityEvent.CREATED
                || entityEvent.getAction() == EntityEvent.MERGED
                || entityEvent.getAction() == EntityEvent.REMOVED) {
            refreshSubjects(curriculum, semester);
        }
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {
    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return true;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        try {
            if (o.equals(mainSubjectsGW)) {
                for (Entity entity : list) {
                    CURRICULUM_DETAIL curriculumDetail = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(CURRICULUM_DETAIL.class, entity.getId());
                    curriculumDetail.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            merge(curriculumDetail);
                }
            } else if (o.equals(electiveSubjectsGW)) {
                for (Entity entity : list) {
                    ELECTIVE_SUBJECT electiveSubject = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(ELECTIVE_SUBJECT.class, entity.getId());
                    electiveSubject.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            merge(electiveSubject);
                }
            } else if (o.equals(addingSubjectsGW)) {
                for (Entity entity : list) {
                    CURRICULUM_ADD_PROGRAM curriculumAddProgram = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(CURRICULUM_ADD_PROGRAM.class, entity.getId());
                    curriculumAddProgram.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            merge(curriculumAddProgram);
                }
            }
            refreshSubjects(curriculum, semester);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to delete subjects", e);
        }
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }

    public SEMESTER getSemester() {
        return semester;
    }

    @Override
    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    public enum SubjectsType {
        MAIN_SUBJECTS,
        ELECTIVE_SUBJECTS,
        ADDING_SUBJECTS
    }
}
