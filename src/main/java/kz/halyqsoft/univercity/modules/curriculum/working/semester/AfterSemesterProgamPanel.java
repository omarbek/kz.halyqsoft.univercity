package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_AFTER_SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.footer.EColumnFooterType;
import org.r3a.common.vaadin.widget.grid.footer.IntegerColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.footer.StringColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * @author Omarbek
 * @created on 15.06.2018
 */
public class AfterSemesterProgamPanel extends AbstractCurriculumPanel implements EntityListener {

    private CURRICULUM curriculum;
    private GridWidget afterSemGW;
    private CustomGridSelectDialog subjectSelectDlg;

    public AfterSemesterProgamPanel(CurriculumView parentView) {
        super(parentView);
    }

    @Override
    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    @Override
    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    @Override
    public void initPanel() {
        afterSemGW = new GridWidget(V_CURRICULUM_AFTER_SEMESTER.class);
        afterSemGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        afterSemGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        afterSemGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        afterSemGW.addEntityListener(this);
        DBGridModel gm = (DBGridModel) afterSemGW.getWidgetModel();
        gm.setHeightMode(HeightMode.ROW);
        gm.setHeightByRows(8);
        gm.setDeferredCreate(false);
        gm.setDeferredDelete(false);
        gm.setFooterVisible(true);

        StringColumnFooterModel nameFooter = new StringColumnFooterModel();
        nameFooter.setFooterType(EColumnFooterType.CAPTION);
        nameFooter.setColumnName("subjectNameRU");
        nameFooter.setValue(getUILocaleUtil().getCaption("total"));
        gm.addFooterModel(nameFooter);

        IntegerColumnFooterModel creditFooter = new IntegerColumnFooterModel();
        creditFooter.setFooterType(EColumnFooterType.SUM);
        creditFooter.setColumnName("credit");
        gm.addFooterModel(creditFooter);

        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = gm.getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);

        FormModel fm = ((DBGridModel) afterSemGW.getWidgetModel()).getFormModel();

        FKFieldModel subjectFM = (FKFieldModel) fm.getFieldModel("subject");

        FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");

        subjectFM.getListeners().add(new SubjectSelectListener(creditabilityFM));

        getContent().addComponent(afterSemGW);
    }

    @Override
    public void refresh() throws Exception {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = ((DBGridModel) afterSemGW.getWidgetModel()).getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhere("deleted", Boolean.FALSE);

        afterSemGW.getWidgetModel().setReadOnly(curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
        afterSemGW.refresh();
        getParentView().setTotalCreditSum();
    }

    public final void approve() throws Exception {
        List<SEMESTER_SUBJECT> newList = new ArrayList<SEMESTER_SUBJECT>();
        List<STUDENT_SUBJECT> subList = new ArrayList<>();
        CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
        // Approve only unsaved subjects
        String sql = "SELECT subj.* " +
                "FROM subject subj " +
                "  INNER JOIN curriculum_after_semester curr_after " +
                "    ON curr_after.subject_id = subj.id AND curr_after.curriculum_id = ?1 " +
                "       AND curr_after.deleted = ?2 " +
                "WHERE NOT exists(SELECT 1 " +
                "                 FROM semester_subject sem_subj " +
                "                 WHERE subj.id = sem_subj.subject_id AND sem_subj.semester_data_id = ?3) " +
                "      AND subj.id = ?4;";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculum.getId().getId());
        params.put(2, Boolean.FALSE);

        QueryModel<CURRICULUM_AFTER_SEMESTER> capQM = new QueryModel<>(CURRICULUM_AFTER_SEMESTER.class);
        capQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        capQM.addWhereAnd("deleted", Boolean.FALSE);

        List<CURRICULUM_AFTER_SEMESTER> curriculumAfterSemesters = session.lookup(capQM);
        for (CURRICULUM_AFTER_SEMESTER afterSemester : curriculumAfterSemesters) {
            SEMESTER_DATA semesterData = CommonUtils.createSemesterDataBySemester(afterSemester.getSemester(),
                    curriculum);
            SUBJECT subject = afterSemester.getSubject();
            params.put(3, semesterData.getId().getId());
            params.put(4, subject.getId().getId());
            List<SUBJECT> subjectList = session.lookup(sql, params, SUBJECT.class);
            if (!subjectList.isEmpty()) {
                SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                ss.setSemesterData(semesterData);
                ss.setSubject(subject);
                newList.add(ss);
            }
        }

        if (!newList.isEmpty()) {
            session.create(newList);
        }

        List<CURRICULUM_AFTER_SEMESTER> casList = session.lookup(capQM);
        for (CURRICULUM_AFTER_SEMESTER afterSemester : casList) {
            SEMESTER_DATA semesterData = CommonUtils.createSemesterDataBySemester(afterSemester.getSemester(),
                    curriculum);
            SUBJECT subject = afterSemester.getSubject();
            params.put(3, semesterData.getId().getId());
            params.put(4, subject.getId().getId());

            STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();
            studentSubject.setSemesterData(semesterData);
            QueryModel<SEMESTER_SUBJECT> semesterSubjectQM = new QueryModel<>(SEMESTER_SUBJECT.class);
            semesterSubjectQM.addWhere("subject", ECriteria.EQUAL, afterSemester.getSubject().getId());
            List<SEMESTER_SUBJECT> semesterSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterSubjectQM);
            for (SEMESTER_SUBJECT s : semesterSubjects) {
                studentSubject.setSubject(s);
                studentSubject.setRegDate(new Date());
                QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
                studentEducationQM.addWhere("speciality", ECriteria.EQUAL, curriculum.getSpeciality().getId());
                studentEducationQM.addWhereNull("child");
                List<STUDENT_EDUCATION> studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM);
                for (STUDENT_EDUCATION se : studentEducation) {
                    studentSubject.setStudentEducation(se);
                }
                subList.add(studentSubject);
            }
        }

        if (!subList.isEmpty()) {
            session.create(subList);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        try {
            getParentView().save();
            return true;
        } catch (Exception ex) {
            LOG.error("Unable to create additional program: ", ex);
            Message.showError(ex.getMessage());
        }

        return false;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        if (source.equals(afterSemGW)) {
            FormModel fm = ((DBGridModel) afterSemGW.getWidgetModel()).getFormModel();

            FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");
            creditabilityFM.setReadOnly(true);

            FKFieldModel subjectFM = (FKFieldModel) fm.getFieldModel("subject");
            subjectFM.setSelectType(ESelectType.CUSTOM_GRID);
            subjectFM.setDialogHeight(400);
            subjectFM.setDialogWidth(600);
            QueryModel subjectQM = subjectFM.getQueryModel();
            subjectQM.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
            subjectQM.addWhereNull("subjectCycle");
            try {
                QueryModel<DEPARTMENT> chairQM1 = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
                chairQM1.addWhereNotNull("parent");
                chairQM1.addWhereAnd("deleted", Boolean.FALSE);
                chairQM1.addOrder("deptName");
                BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM1));
                ComboBox chairCB = new ComboBox();
                chairCB.setContainerDataSource(chairBIC);
                chairCB.setImmediate(true);
                chairCB.setNullSelectionAllowed(true);
                chairCB.setTextInputAllowed(true);
                chairCB.setFilteringMode(FilteringMode.CONTAINS);
                chairCB.setWidth(400, Unit.PIXELS);
                chairCB.setPageLength(0);

                QueryModel<LEVEL> levelQM = new QueryModel<LEVEL>(LEVEL.class);
                levelQM.addOrder("levelName");
                BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<LEVEL>(LEVEL.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
                ComboBox levelCB = new ComboBox();
                levelCB.setContainerDataSource(levelBIC);
                levelCB.setImmediate(true);
                levelCB.setNullSelectionAllowed(true);
                levelCB.setTextInputAllowed(false);
                levelCB.setFilteringMode(FilteringMode.OFF);
                levelCB.setPageLength(0);

                QueryModel<CREDITABILITY> creditabilityQM = new QueryModel<CREDITABILITY>(CREDITABILITY.class);
                creditabilityQM.addOrder("credit");
                BeanItemContainer<CREDITABILITY> creditabilityBIC = new BeanItemContainer<CREDITABILITY>(CREDITABILITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(creditabilityQM));
                ComboBox creditabilityCB = new ComboBox();
                creditabilityCB.setContainerDataSource(creditabilityBIC);
                creditabilityCB.setImmediate(true);
                creditabilityCB.setNullSelectionAllowed(true);
                creditabilityCB.setTextInputAllowed(false);
                creditabilityCB.setFilteringMode(FilteringMode.OFF);
                creditabilityCB.setPageLength(0);

                subjectSelectDlg = subjectFM.getCustomGridSelectDialog();
                subjectSelectDlg.getSelectModel().setMultiSelect(false);
                subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
                subjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                subjectSelectDlg.setFilterRequired(true);
                subjectSelectDlg.initFilter();
            } catch (Exception ex) {
                LOG.error("Unable to initialize custom grid dialog: ", ex);
                Message.showError(ex.toString());
            }
        }
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
        if (source.equals(afterSemGW)) {
            if (isNew) {
                V_CURRICULUM_AFTER_SEMESTER afterSemesterView = (V_CURRICULUM_AFTER_SEMESTER) e;

                Map<Integer, Object> params = new HashMap<Integer, Object>();
                params.put(1, curriculum.getId().getId());
                params.put(2, afterSemesterView.getSemester().getId().getId());
                params.put(3, Boolean.FALSE);
                params.put(4, afterSemesterView.getSubject().getId().getId());
                String sql = "SELECT count(curr_after_sem.SUBJECT_ID) " +
                        "FROM curriculum_after_semester curr_after_sem " +
                        "WHERE curr_after_sem.CURRICULUM_ID = ?1 " +
                        "      AND curr_after_sem.semester_id = ?2 " +
                        "      AND curr_after_sem.DELETED = ?3 " +
                        "      AND curr_after_sem.SUBJECT_ID = ?4";

                try {
                    long count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(sql, params);
                    boolean can = (count == 0);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                    } else {
                        CURRICULUM_AFTER_SEMESTER afterSemester = new CURRICULUM_AFTER_SEMESTER();
                        afterSemester.setCurriculum(curriculum);
                        afterSemester.setSemester(afterSemesterView.getSemester());
                        afterSemester.setCreated(new Date());
                        afterSemester.setCode(afterSemesterView.getSubjectCode());
                        afterSemester.setEducationModuleType(afterSemesterView.getEducationModuleType());
                        try {
                            afterSemester.setSubject(SessionFacadeFactory.getSessionFacade(
                                    CommonEntityFacadeBean.class).lookup(SUBJECT.class,
                                    afterSemesterView.getSubject().getId()));
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    create(afterSemester);
                            refresh();
                        } catch (Exception ex) {
                            LOG.error("Unable to save curriculum after semester: ", ex);
                            Message.showError(ex.toString());
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to check subjects: ", ex);
                    Message.showError(ex.toString());
                }
            }
        }

        return false;
    }

    public final void checkForConfirm() throws Exception {
        if (getTotalCredit() == 0) {
            throw new Exception(getUILocaleUtil().getCaption("no.programs.after.semester"));
        }
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        List<SEMESTER_SUBJECT> ssDelList = new ArrayList<>();
        List<String> notDelList = new ArrayList<>();
        List<CURRICULUM_AFTER_SEMESTER> delList = new ArrayList<>();
        try {
            CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class);
            QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<>(SEMESTER_SUBJECT.class);
            for (Entity e : entities) {
                CURRICULUM_AFTER_SEMESTER afterSemester = session.lookup(
                        CURRICULUM_AFTER_SEMESTER.class, e.getId());
                ssQM.addWhere("semesterData", ECriteria.EQUAL, CommonUtils.createSemesterDataBySemester(
                        afterSemester.getSemester(), curriculum).getId());
                ssQM.addWhere("subject", ECriteria.EQUAL, afterSemester.getSubject().getId());
                SEMESTER_SUBJECT ss = null;
                try {
                    ss = session.lookupSingle(ssQM);
                } catch (NoResultException nrex) {
                    ss = null;
                }
                if (ss != null) {
                    String sql = "SELECT count(curr_after_sem.ID) CNT " +
                            "FROM curriculum_after_semester curr_after_sem " +
                            "  INNER JOIN CURRICULUM curr ON curr_after_sem.CURRICULUM_ID = curr.ID " +
                            "WHERE curr_after_sem.CURRICULUM_ID != ?1 " +
                            "      AND curr_after_sem.SEMESTER_ID = ?2 " +
                            "      AND curr_after_sem.SUBJECT_ID = ?3 AND curr.STATUS_ID = ?4";
                    Map<Integer, Object> params = new HashMap<Integer, Object>(4);
                    params.put(1, curriculum.getId().getId());
                    params.put(2, afterSemester.getSemester().getId().getId());
                    params.put(3, afterSemester.getSubject().getId().getId());
                    params.put(4, 3);
                    Integer sum = null;
                    try {
                        sum = (Integer) session.lookupSingle(sql, params);
                    } catch (NoResultException nrex) {
                        sum = null;
                    }
                    if (sum != null && sum > 0) {
                        delList.add(afterSemester);
                    } else {
                        sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from EXAM_SCHEDULE a1 where a1.SUBJECT_ID = ?1 and a1.DELETED = ?2 union select count(a2.ID) CNT from SCHEDULE_DETAIL a2 where a2.SUBJECT_ID = ?3 union select count(a3.SUBJECT_ID) CNT from STUDENT_SUBJECT a3 where a3.SUBJECT_ID = ?4) t1";
                        params = new HashMap<Integer, Object>(4);
                        params.put(1, ss.getId().getId());
                        params.put(2, Boolean.FALSE);
                        params.put(3, ss.getId().getId());
                        params.put(4, ss.getId().getId());
                        try {
                            sum = (Integer) session.lookupSingle(sql, params);
                        } catch (NoResultException nrex) {
                        }
                        if (sum != null && sum > 0) {
                            notDelList.add(afterSemester.getSubject().getNameRU());
                        } else {
                            ssDelList.add(ss);
                            delList.add(afterSemester);
                        }
                    }
                } else {
                    delList.add(afterSemester);
                }
            }

            if (!delList.isEmpty()) {
                if (!ssDelList.isEmpty()) {
                    session.delete(ssDelList);
                }
                session.delete(delList);
                refresh();
            }

            if (!notDelList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getUILocaleUtil().getMessage("error.recorddependencyexists"));
                sb.append(" ");
                boolean first = true;
                for (String s : notDelList) {
                    if (!first) {
                        sb.append(" ");
                    }
                    sb.append(s);
                    first = false;
                }
            }
        } catch (Exception ex) {
            LOG.error("Unable to delete curriculum add program: ", ex);
            Message.showError(ex.toString());
        }

        return false;
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

    public Integer getTotalCredit() {
        return (Integer) ((DBGridModel) afterSemGW.getWidgetModel()).getFooterValue("credit");
    }

    private class SubjectSelectListener implements Property.ValueChangeListener {
        private final FKFieldModel creditabilityFM;

        public SubjectSelectListener(FKFieldModel creditabilityFM) {
            super();
            this.creditabilityFM = creditabilityFM;
        }

        @Override
        public void valueChange(Property.ValueChangeEvent ev) {
            try {
                V_SUBJECT_SELECT subject = (V_SUBJECT_SELECT) ev.getProperty().getValue();
                if (subject != null) {
                    V_SUBJECT_SELECT vss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_SUBJECT_SELECT.class, subject.getId());
                    creditabilityFM.refresh(vss.getCreditability());
                } else {
                    creditabilityFM.refresh(null);
                }
            } catch (Exception ex) {
                LOG.error("Unable to load additional educational program: ", ex);
            }
        }
    }
}
