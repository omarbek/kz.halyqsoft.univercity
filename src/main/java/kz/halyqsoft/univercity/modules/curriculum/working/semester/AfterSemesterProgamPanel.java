package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_AFTER_SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_AFTER_SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.modules.curriculum.working.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.CurriculumView;
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
    private GridWidget grid;
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
        grid = new GridWidget(V_CURRICULUM_AFTER_SEMESTER.class);
        grid.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        grid.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        grid.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        grid.addEntityListener(this);
        DBGridModel gm = (DBGridModel) grid.getWidgetModel();
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

        FormModel fm = ((DBGridModel) grid.getWidgetModel()).getFormModel();

        FKFieldModel semesterFM = (FKFieldModel) fm.getFieldModel("semester");
        QueryModel semesterQM = semesterFM.getQueryModel();
        semesterQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(8));

        FKFieldModel subjectFM = (FKFieldModel) fm.getFieldModel("subject");

        FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");

        subjectFM.getListeners().add(new SubjectSelectListener( creditabilityFM));

        getContent().addComponent(grid);
    }

    @Override
    public void refresh() throws Exception {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = ((DBGridModel) grid.getWidgetModel()).getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhere("deleted", Boolean.FALSE);

        grid.getWidgetModel().setReadOnly(curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
        grid.refresh();
        getParentView().setTotalCreditSum();
    }

    public final void approve() throws Exception {
        List<SEMESTER_SUBJECT> newList = new ArrayList<SEMESTER_SUBJECT>();
        CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
        // Approve only unsaved subjects
        String sql = "SELECT subj.* " +
                "FROM SUBJECT subj INNER JOIN curriculum_after_semester curr_after_sem " +
                "    ON subj.ID = curr_after_sem.SUBJECT_ID AND curr_after_sem.CURRICULUM_ID = ?1 AND curr_after_sem.SEMESTER_ID = ?2 AND " +
                "       curr_after_sem.DELETED = ?3 " +
                "WHERE NOT exists(SELECT 1 " +
                "                 FROM SEMESTER_SUBJECT c " +
                "                 WHERE subj.ID = c.SUBJECT_ID AND c.SEMESTER_DATA_ID = ?4) AND subj.ID = ?5";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculum.getId().getId());
        params.put(3, Boolean.FALSE);

        QueryModel<CURRICULUM_AFTER_SEMESTER> capQM = new QueryModel<>(CURRICULUM_AFTER_SEMESTER.class);
        capQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        capQM.addWhereAnd("deleted", Boolean.FALSE);

        List<CURRICULUM_AFTER_SEMESTER> curriculumAfterSemesterList = session.lookup(capQM);
        for (CURRICULUM_AFTER_SEMESTER cas : curriculumAfterSemesterList) {
            SEMESTER_DATA sd = getOrCreateSemesterData(cas.getSemester());
            params.put(2, cas.getSemester().getId().getId());
            params.put(4, sd.getId().getId());
            params.put(5, cas.getSubject().getId().getId());
            List<SUBJECT> subjectList = session.lookup(sql, params, SUBJECT.class);
            if (!subjectList.isEmpty()) {
                SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                ss.setSemesterData(sd);
                ss.setSubject(cas.getSubject());
                newList.add(ss);
            }
        }
        if (!newList.isEmpty()) {
            session.create(newList);
        }
    }

    private SEMESTER_DATA getOrCreateSemesterData(SEMESTER semester) throws Exception {
        SEMESTER_DATA sd = null;
        ENTRANCE_YEAR studyYear = curriculum.getEntranceYear();
        if (!semester.getId().equals(ID.valueOf(1)) && !semester.getId().equals(ID.valueOf(2))) {
            int beginYear = studyYear.getBeginYear();
            int endYear = studyYear.getEndYear();
            if (semester.getId().equals(ID.valueOf(3)) || semester.getId().equals(ID.valueOf(4))) {
                beginYear++;
                endYear++;
            } else if (semester.getId().equals(ID.valueOf(5)) || semester.getId().equals(ID.valueOf(6))) {
                beginYear += 2;
                endYear += 2;
            } else if (semester.getId().equals(ID.valueOf(7)) || semester.getId().equals(ID.valueOf(8))) {
                beginYear += 3;
                endYear += 3;
            }

            QueryModel<ENTRANCE_YEAR> syQM = new QueryModel<>(ENTRANCE_YEAR.class);
            syQM.addWhere("beginYear", ECriteria.EQUAL, beginYear);
            syQM.addWhereAnd("endYear", ECriteria.EQUAL, endYear);

            try {
                studyYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(syQM);
            } catch (NoResultException nrex) {
                studyYear = new ENTRANCE_YEAR();
                studyYear.setBeginYear(beginYear);
                studyYear.setEndYear(endYear);
                studyYear.setEntranceYear(beginYear + "-" + endYear);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studyYear);
            }
        }

        SEMESTER_PERIOD sp = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SEMESTER.class, semester.getId()).getSemesterPeriod();

        QueryModel<SEMESTER_DATA> sdQM = new QueryModel<>(SEMESTER_DATA.class);
        sdQM.addWhere("year", ECriteria.EQUAL, studyYear.getId());
        sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, sp.getId());

        try {
            sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
        } catch (NoResultException nrex) {
            sd = new SEMESTER_DATA();
            sd.setYear(studyYear);
            sd.setSemesterPeriod(sp);

            Calendar c = Calendar.getInstance();
            c.clear();
            if (sp.getId().equals(ID.valueOf(1))) {
                c.set(Calendar.DAY_OF_MONTH, 20);
                c.set(Calendar.MONTH, Calendar.AUGUST);
                c.set(Calendar.YEAR, studyYear.getBeginYear());
                sd.setBeginDate(c.getTime());

                c.clear();
                c.set(Calendar.DAY_OF_MONTH, 31);
                c.set(Calendar.MONTH, Calendar.DECEMBER);
                c.set(Calendar.YEAR, studyYear.getBeginYear());
                sd.setEndDate(c.getTime());
            } else if (sp.getId().equals(ID.valueOf(2))) {
                c.set(Calendar.DAY_OF_MONTH, 10);
                c.set(Calendar.MONTH, Calendar.JANUARY);
                c.set(Calendar.YEAR, studyYear.getEndYear());
                sd.setBeginDate(c.getTime());

                c.clear();
                c.set(Calendar.DAY_OF_MONTH, 25);
                c.set(Calendar.MONTH, Calendar.MAY);
                c.set(Calendar.YEAR, studyYear.getEndYear());
                sd.setEndDate(c.getTime());
            }

            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sd);
        }

        return sd;
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
        if (source.equals(grid)) {
            FormModel fm = ((DBGridModel) grid.getWidgetModel()).getFormModel();

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
        if (source.equals(grid)) {
            if (isNew) {
                V_CURRICULUM_AFTER_SEMESTER afterSemesterView = (V_CURRICULUM_AFTER_SEMESTER) e;

                Map<Integer, Object> params = new HashMap<Integer, Object>();
                params.put(1, curriculum.getId().getId());
                params.put(2, afterSemesterView.getSemester().getId().getId());
                params.put(3, Boolean.FALSE);
                params.put(4, afterSemesterView.getSubject().getId().getId());
                String sql = "SELECT count(curr_after_sem.SUBJECT_ID) " +
                        "FROM curriculum_after_semester curr_after_sem " +
                        "WHERE curr_after_sem.CURRICULUM_ID = ?1 AND curr_after_sem.SEMESTER_ID = ?2 AND curr_after_sem.DELETED = ?3 " +
                        "      AND curr_after_sem.SUBJECT_ID = ?4";

                try {
                    long count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(sql, params);
                    boolean can = (count == 0);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                    } else {
                        CURRICULUM_AFTER_SEMESTER afterSemester = new CURRICULUM_AFTER_SEMESTER();
                        SEMESTER_DATA sd = getOrCreateSemesterData(afterSemesterView.getSemester());
                        afterSemester.setCurriculum(curriculum);
                        afterSemester.setSemester(afterSemesterView.getSemester());
                        afterSemester.setSemesterData(sd);
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
    public final void checkForConform() throws Exception {
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
                ssQM.addWhere("semesterData", ECriteria.EQUAL, afterSemester.getSemesterData().getId());
                ssQM.addWhere("subject", ECriteria.EQUAL, afterSemester.getSubject().getId());
                SEMESTER_SUBJECT ss = null;
                try {
                    ss = session.lookupSingle(ssQM);
                } catch (NoResultException nrex) {
                    ss=null;
                }
                if (ss != null) {
                    String sql = "SELECT count(a1.ID) CNT " +
                            "FROM curriculum_after_semester a1 INNER JOIN CURRICULUM b1 ON a1.CURRICULUM_ID = b1.ID " +
                            "WHERE a1.CURRICULUM_ID != ?1 AND a1.SEMESTER_DATA_ID = ?2 AND a1.SUBJECT_ID = ?3 AND b1.STATUS_ID = ?4;";
                    Map<Integer, Object> params = new HashMap<Integer, Object>(4);
                    params.put(1, curriculum.getId().getId());
                    params.put(2, afterSemester.getSemesterData().getId().getId());
                    params.put(3, afterSemester.getSubject().getId().getId());
                    params.put(4, 3);
                    Long sum = null;
                    try {
                        sum = (Long) session.lookupSingle(sql, params);
                    } catch (NoResultException nrex) {
                        sum=null;
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
                            sum = (Long) session.lookupSingle(sql, params);
                        } catch (NoResultException nrex) {
                        }
                        if (sum != null && sum > 0) {
                            notDelList.add( afterSemester.getSubject().getNameRU());
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

    @Override
    public void save() throws Exception {
    }

    @Override
    protected void cancel() {
    }

    public Integer getTotalCredit() {
        return (Integer) ((DBGridModel) grid.getWidgetModel()).getFooterValue("credit");
    }

    private class SubjectSelectListener implements Property.ValueChangeListener {
        private final FKFieldModel creditabilityFM;

        public SubjectSelectListener( FKFieldModel creditabilityFM) {
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
