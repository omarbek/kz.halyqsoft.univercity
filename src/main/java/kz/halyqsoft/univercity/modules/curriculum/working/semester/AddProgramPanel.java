package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.modules.curriculum.working.CurriculumView;
import kz.halyqsoft.univercity.modules.curriculum.working.AbstractCurriculumPanel;
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
import org.r3a.common.vaadin.widget.form.field.FieldModel;
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
 * @author Omarbek Dinassil
 * @created Feb 26, 2016 9:21:51 AM
 */
@SuppressWarnings({"serial", "unchecked"})
public class AddProgramPanel extends AbstractCurriculumPanel implements EntityListener {

    private CURRICULUM curriculum;
    private GridWidget grid;
    private CustomGridSelectDialog subjectSelectDlg;

    public AddProgramPanel(CurriculumView parentView) {
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
        grid = new GridWidget(V_CURRICULUM_ADD_PROGRAM.class);
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
        QueryModel subjectQM = subjectFM.getQueryModel();

        FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");

        subjectFM.getListeners().add(new SubjectSelectListener(fm.getFieldModel("subjectCode"), creditabilityFM));

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

    public void setReadOnlyDetail(boolean readOnly) {
        grid.getWidgetModel().setReadOnly(readOnly);
        try {
            refresh();
        } catch (Exception ex) {
            LOG.error("Unable to set read only status: ", ex);
            Message.showError(ex.toString());
        }
    }

    public final void checkForConform() throws Exception {
        if (getTotalCredit() == 0) {
            throw new Exception(getUILocaleUtil().getMessage("no.additional.education.program"));
        }
    }

    public final void approve() throws Exception {
        List<SEMESTER_SUBJECT> newList = new ArrayList<SEMESTER_SUBJECT>();
        List<CURRICULUM_ADD_PROGRAM> mergeList = new ArrayList<CURRICULUM_ADD_PROGRAM>();
        CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
        // Approve only unsaved subjects
        String sql = "SELECT subj.* " +
                "FROM SUBJECT subj INNER JOIN CURRICULUM_ADD_PROGRAM curr_add_pr " +
                "    ON subj.ID = curr_add_pr.SUBJECT_ID AND curr_add_pr.CURRICULUM_ID = ?1 AND curr_add_pr.SEMESTER_ID = ?2 AND " +
                "       curr_add_pr.DELETED = ?3 " +
                "WHERE NOT exists(SELECT 1 " +
                "                 FROM SEMESTER_SUBJECT c " +
                "                 WHERE subj.ID = c.SUBJECT_ID AND c.SEMESTER_DATA_ID = ?4) AND subj.ID = ?5";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculum.getId().getId());
        params.put(3, Boolean.FALSE);

        QueryModel<CURRICULUM_ADD_PROGRAM> capQM = new QueryModel<CURRICULUM_ADD_PROGRAM>(CURRICULUM_ADD_PROGRAM.class);
        capQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        capQM.addWhereAnd("deleted", Boolean.FALSE);

        List<CURRICULUM_ADD_PROGRAM> capList = session.lookup(capQM);
        for (CURRICULUM_ADD_PROGRAM cap : capList) {
            CURRICULUM_ADD_PROGRAM cap1 = session.lookup(CURRICULUM_ADD_PROGRAM.class, cap.getId());
            SEMESTER_DATA sd = getOrCreateSemesterData(cap1.getSemester());
            params.put(2, cap1.getSemester().getId().getId());
            params.put(4, sd.getId().getId());
            params.put(5, cap1.getSubject().getId().getId());
            List<SUBJECT> subjectList = session.lookup(sql, params, SUBJECT.class);
            if (!subjectList.isEmpty()) {
                SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                ss.setSemesterData(sd);
                ss.setSubject(cap1.getSubject());
                newList.add(ss);
            }

            //TODO: Потооом удалить этот метод вообще.
            if (cap1.getSemesterData() == null) {
                cap1.setSemesterData(sd);
                mergeList.add(cap1);
            }
        }

        if (!newList.isEmpty()) {
            session.create(newList);
        }

        if (!mergeList.isEmpty()) {
            session.merge(mergeList);
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

            FieldModel subjectCodeFM = fm.getFieldModel("subjectCode");
            subjectCodeFM.setReadOnly(true);

            FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");
            creditabilityFM.setReadOnly(true);

            FKFieldModel subjectFM = (FKFieldModel) fm.getFieldModel("subject");
            subjectFM.setSelectType(ESelectType.CUSTOM_GRID);
            subjectFM.setDialogHeight(400);
            subjectFM.setDialogWidth(600);
            QueryModel subjectQM = subjectFM.getQueryModel();
            subjectQM.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
            List<ID> studyDirectIDs = new ArrayList<ID>();
            studyDirectIDs.add(ID.valueOf(16));
            studyDirectIDs.add(ID.valueOf(17));
            studyDirectIDs.add(ID.valueOf(18));
            studyDirectIDs.add(ID.valueOf(19));
            subjectQM.addWhereInAnd("studyDirect", studyDirectIDs);
            try {
                QueryModel<DEPARTMENT> chairQM1 = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
//                chairQM1.addWhere("type", ECriteria.EQUAL, T_DEPARTMENT_TYPE.CHAIR_ID);//TODO
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

                TextField subjectCodeTF = new TextField();
                subjectCodeTF.setWidth(100, Unit.PIXELS);
                subjectCodeTF.setNullRepresentation("");
                subjectCodeTF.setNullSettingAllowed(true);
                subjectCodeTF.setImmediate(true);

                subjectSelectDlg = subjectFM.getCustomGridSelectDialog();
                subjectSelectDlg.getSelectModel().setMultiSelect(false);
                subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
                subjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                subjectSelectDlg.getFilterModel().addFilter("code", subjectCodeTF);
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
                V_CURRICULUM_ADD_PROGRAM vcap = (V_CURRICULUM_ADD_PROGRAM) e;

                Map<Integer, Object> params = new HashMap<Integer, Object>();
                params.put(1, curriculum.getId().getId());
                params.put(2, vcap.getSemester().getId().getId());
                params.put(3, Boolean.FALSE);
                params.put(4, vcap.getSubject().getId().getId());
                String sql = "SELECT count(curr_add_pr.SUBJECT_ID) " +
                        "FROM CURRICULUM_ADD_PROGRAM curr_add_pr " +
                        "WHERE curr_add_pr.CURRICULUM_ID = ?1 AND curr_add_pr.SEMESTER_ID = ?2 AND curr_add_pr.DELETED = ?3 " +
                        "      AND curr_add_pr.SUBJECT_ID = ?4";

                try {
                    Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
                    boolean can = (count == 0);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                    } else {
                        SEMESTER_DATA sd = getOrCreateSemesterData(vcap.getSemester());
                        CURRICULUM_ADD_PROGRAM cap = new CURRICULUM_ADD_PROGRAM();
                        cap.setCurriculum(curriculum);
                        cap.setSemester(vcap.getSemester());
                        cap.setSemesterData(sd);
                        cap.setCreated(new Date());
                        try {
                            cap.setSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBJECT.class, vcap.getSubject().getId()));
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cap);
                            refresh();
                        } catch (Exception ex) {
                            LOG.error("Unable to save curriculum add program: ", ex);
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

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        List<SEMESTER_SUBJECT> ssDelList = new ArrayList<SEMESTER_SUBJECT>();
        List<String> notDelList = new ArrayList<String>();
        List<CURRICULUM_ADD_PROGRAM> delList = new ArrayList<CURRICULUM_ADD_PROGRAM>();
        try {
            CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
            QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<SEMESTER_SUBJECT>(SEMESTER_SUBJECT.class);
            for (Entity e : entities) {
                CURRICULUM_ADD_PROGRAM cap = session.lookup(CURRICULUM_ADD_PROGRAM.class, e.getId());
                SEMESTER_DATA sd = getOrCreateSemesterData(cap.getSemester());
                ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                ssQM.addWhere("subject", ECriteria.EQUAL, cap.getSubject().getId());
                SEMESTER_SUBJECT ss = null;
                try {
                    ss = session.lookupSingle(ssQM);
                } catch (NoResultException nrex) {
                }
                if (ss != null) {
                    String sql = "select count(a1.ID) CNT from CURRICULUM_ADD_PROGRAM a1 inner join CURRICULUM b1 on a1.CURRICULUM_ID = b1.ID where a1.CURRICULUM_ID != ?1 and a1.SEMESTER_DATA_ID = ?2 and a1.SUBJECT_ID = ?3 and b1.STATUS_ID = ?4";
                    Map<Integer, Object> params = new HashMap<Integer, Object>(4);
                    params.put(1, curriculum.getId().getId());
                    params.put(2, sd.getId().getId());
                    params.put(3, cap.getSubject().getId().getId());
                    params.put(4, 3);
                    Integer sum = null;
                    try {
                        sum = (Integer) session.lookupSingle(sql, params);
                    } catch (NoResultException nrex) {
                    }
                    if (sum != null && sum > 0) {
                        delList.add(cap);
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
                            notDelList.add(cap.getSubject().getCode() + " " + cap.getSubject().getNameRU());
                        } else {
                            ssDelList.add(ss);
                            delList.add(cap);
                        }
                    }
                } else {
                    delList.add(cap);
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
                sb.append("\n");
                boolean first = true;
                for (String s : notDelList) {
                    if (!first) {
                        sb.append("\n");
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

    private class SubjectSelectListener implements ValueChangeListener {
        private final FieldModel subjectCodeFM;
        private final FKFieldModel creditabilityFM;

        public SubjectSelectListener(FieldModel subjectCodeFM, FKFieldModel creditabilityFM) {
            super();
            this.subjectCodeFM = subjectCodeFM;
            this.creditabilityFM = creditabilityFM;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            try {
                V_SUBJECT_SELECT subject = (V_SUBJECT_SELECT) ev.getProperty().getValue();
                if (subject != null) {
                    V_SUBJECT_SELECT vss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_SUBJECT_SELECT.class, subject.getId());
                    subjectCodeFM.refresh(vss.getCode());
                    creditabilityFM.refresh(vss.getCreditability());
                } else {
                    subjectCodeFM.refresh(null);
                    creditabilityFM.refresh(null);
                }
            } catch (Exception ex) {
                LOG.error("Unable to load additional educational program: ", ex);
            }
        }
    }
}
