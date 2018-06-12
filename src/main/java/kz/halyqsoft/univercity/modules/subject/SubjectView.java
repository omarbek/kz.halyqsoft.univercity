package kz.halyqsoft.univercity.modules.subject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VSubject;
import kz.halyqsoft.univercity.filter.FSubjectFilter;
import kz.halyqsoft.univercity.filter.panel.SubjectFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Dec 25, 2015 11:37:43 AM
 */
@SuppressWarnings({"serial", "unchecked"})
public class SubjectView extends AbstractTaskView implements FilterPanelListener {

    private final SubjectFilterPanel filterPanel;
    private GridWidget subjectGW;

    public SubjectView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new SubjectFilterPanel(new FSubjectFilter());
    }


    @Override
    public void initView(boolean readOnly) throws Exception {
        filterPanel.addFilterPanelListener(this);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        tf.setWidth(100, Unit.PIXELS);
        filterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        tf.setWidth(200, Unit.PIXELS);
        filterPanel.addFilterComponent("subjectName", tf);

        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setPageLength(0);
        cb.setWidth(300, Unit.PIXELS);
        QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
        chairQM.addWhereNotNull("parent");
        chairQM.addWhereAnd("deleted", Boolean.FALSE);
        chairQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
        cb.setContainerDataSource(chairBIC);
        filterPanel.addFilterComponent("chair", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(80, Unit.PIXELS);
        QueryModel<CREDITABILITY> creditQM = new QueryModel<>(CREDITABILITY.class);
        creditQM.addOrder("credit");
        BeanItemContainer<CREDITABILITY> creditBIC = new BeanItemContainer<>(CREDITABILITY.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(creditQM));
        cb.setContainerDataSource(creditBIC);
        filterPanel.addFilterComponent("creditability", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(70, Unit.PIXELS);
        QueryModel<SUBJECT_CYCLE> cycleQM = new QueryModel<>(SUBJECT_CYCLE.class);
        cycleQM.addOrder("cycleShortName");
        BeanItemContainer<SUBJECT_CYCLE> cycleBIC = new BeanItemContainer<>(SUBJECT_CYCLE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cycleQM));
        cb.setContainerDataSource(cycleBIC);
        filterPanel.addFilterComponent("subjectCycle", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(130, Unit.PIXELS);
        QueryModel<LEVEL> levelQM = new QueryModel<>(LEVEL.class);
        levelQM.addOrder("levelName");
        BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<>(LEVEL.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
        cb.setContainerDataSource(levelBIC);
        filterPanel.addFilterComponent("level", cb);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        subjectGW = new GridWidget(VSubject.class);
        subjectGW.addEntityListener(new SubjectEntity());
        subjectGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        subjectGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);

        ((DBGridModel)subjectGW.getWidgetModel()).setEntities(getEntities());

        DBGridModel subjectGM = (DBGridModel) subjectGW.getWidgetModel();
        subjectGM.setCrudEntityClass(SUBJECT.class);
        subjectGM.setTitleVisible(false);
        subjectGM.setMultiSelect(false);
        subjectGM.setRefreshType(ERefreshType.MANUAL);

        FSubjectFilter sf = (FSubjectFilter) filterPanel.getFilterBean();
        if (sf.getChair() != null) {
            doFilter(sf);
        }

        getContent().addComponent(subjectGW);
        getContent().setComponentAlignment(subjectGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FSubjectFilter sf = (FSubjectFilter) filterBean;
        if (sf.getChair() == null) {
            Message.showInfo(getUILocaleUtil().getMessage("select.chair"));
            return;
        }

        int i = 1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 3) {
            params.put(i, sf.getCode().trim().toLowerCase());
            sb.append("lower(subj.CODE) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getSubjectName() != null && sf.getSubjectName().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(subj.NAME_RU) like '");
            sb.append(sf.getSubjectName().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getChair() != null) {
            params.put(i, sf.getChair().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("subj.CHAIR_ID = ?" + i++);
        }
        if (sf.getCreditability() != null) {
            params.put(i, sf.getCreditability().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("subj.CREDITABILITY_ID = ?" + i++);
        }
        if (sf.getSubjectCycle() != null) {
            params.put(i, sf.getSubjectCycle().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("subj.SUBJECT_CYCLE_ID = ?" + i++);
        }
        if (sf.getLevel() != null) {
            params.put(i, sf.getLevel().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("subj.LEVEL_ID = ?" + i++);
        }

        List<VSubject> list = new ArrayList<>();
        if (sb.length() > 0) {
            params.put(i, Boolean.FALSE);
            sb.append(" and ");
            sb.append("subj.DELETED = ?" + i++);
            sb.insert(0, " where ");
            String sql = "SELECT " +
                    "  subj.ID, " +
                    "  subj.CODE, " +
                    "  subj.NAME_RU  SUBJECT_NAME, " +
                    "  dep.DEPT_NAME CHAIR_NAME, " +
                    "  lvl.LEVEL_NAME, " +
                    "  cred.CREDIT, " +
                    "  formula.FORMULA " +
                    "FROM SUBJECT subj INNER JOIN DEPARTMENT dep ON subj.CHAIR_ID = dep.ID " +
                    "  INNER JOIN LEVEL lvl ON subj.LEVEL_ID = lvl.ID " +
                    "  INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID " +
                    "  INNER JOIN ACADEMIC_FORMULA formula ON subj.ACADEMIC_FORMULA_ID = formula.ID" +
                    sb.toString() +
                    " ORDER BY SUBJECT_NAME";
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VSubject vs = new VSubject();
                        vs.setId(ID.valueOf((long) oo[0]));
                        vs.setCode((String) oo[1]);
                        vs.setSubjectName((String) oo[2]);
                        vs.setChairName((String) oo[3]);
                        vs.setLevelName((String) oo[4]);
                        vs.setCredit(((BigDecimal) oo[5]).intValue());
                        vs.setFormula((String) oo[6]);
                        list.add(vs);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load subject list", ex);
            }
        }

        refresh(list);
    }

    public List<SUBJECT> getEntities(){
        QueryModel<SUBJECT> qm = new QueryModel<>(SUBJECT.class);
        try{
            List<SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
            return subjects;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }

    private void refresh(List<VSubject> list) {
        ((DBGridModel) subjectGW.getWidgetModel()).setEntities(list);
        try {
            subjectGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to clear subject list", ex);
        }
    }

    public void refresh() throws Exception {
        FSubjectFilter subjectFilter = (FSubjectFilter) filterPanel.getFilterBean();
        if (subjectFilter.hasFilter()) {
            doFilter(subjectFilter);
        }
    }

    private class SubjectEntity extends EntityUtils {

        @Override
        protected void init(Object source, Entity e, boolean isNew) throws Exception {
            FormModel fm = ((DBGridModel) subjectGW.getWidgetModel()).getFormModel();

            fm.setReadOnly(false);
            fm.setTitleVisible(false);
            FSubjectFilter sf = (FSubjectFilter) filterPanel.getFilterBean();

            SUBJECT newSubject = (SUBJECT) fm.createNew();

            if (sf != null) {
                newSubject.setChair(sf.getChair());
            }
            SubjectEdit se = new SubjectEdit(fm);
            new SubjectDialog(se, SubjectView.this);
        }

        @Override
        protected GridWidget getGridWidget() {
            return subjectGW;
        }

        @Override
        protected String getModuleName() {
            return "subjects";
        }

        @Override
        protected Class<? extends Entity> getEntityClass() {
            return null;
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {
        }

        @Override
        protected void refresh() throws Exception {
            SubjectView.this.refresh();
        }

        @Override
        public boolean onEdit(Object source, Entity e, int buttonId) {
            if (source.equals(subjectGW)) {
                try {
                    checkDependencies(e, false);
                    return false;
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to edit the subject", ex);
                }
            }
            return true;
        }

        @Override
        public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
            if (source.equals(subjectGW)) {
                try {
                    checkDependencies(entities.get(0), true);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete the subject", ex);
                }
                return false;
            }
            return true;
        }

        private void checkDependencies(Entity e, boolean delete) throws Exception {
            StringBuilder sb = new StringBuilder();
            boolean tagged = false;
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, e.getId().getId());
            String sql = "SELECT usr.LAST_NAME || ' ' || substr(usr.FIRST_NAME, 1, 2) || '.' || " +
                    "       (CASE WHEN usr.MIDDLE_NAME IS NULL " +
                    "         THEN '' " +
                    "        ELSE substr(usr.MIDDLE_NAME, 1, 2) || '.' END) TEACHER_FIO " +
                    "FROM EMPLOYEE empl INNER JOIN USERS usr ON empl.ID = usr.ID " +
                    "  INNER JOIN TEACHER_SUBJECT teacher_subj ON empl.ID = teacher_subj.EMPLOYEE_ID " +
                    "WHERE teacher_subj.SUBJECT_ID = ?1";
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                StringBuilder sbTmp = new StringBuilder();
                boolean first = true;
                for (Object o : tmpList) {
                    if (!first) {
                        sbTmp.append(", ");
                    }

                    sbTmp.append(o);
                    first = false;
                }

                sb.append(String.format(getUILocaleUtil().getMessage("subject.tagged.teacher"), sbTmp.toString()));
                sb.append("\n");
                tagged = true;
            }

            sql = "SELECT room.ROOM_NO " +
                    "FROM ROOM room " +
                    "WHERE exists(SELECT 1 " +
                    "             FROM ROOM_SUBJECT room_subj " +
                    "             WHERE room_subj.ROOM_ID = room.ID AND room_subj.SUBJECT_ID = ?1)";
            tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                StringBuilder sbTmp = new StringBuilder();
                boolean first = true;
                for (Object o : tmpList) {
                    if (!first) {
                        sbTmp.append(", ");
                    }

                    sbTmp.append(o);
                    first = false;
                }

                sb.append(String.format(getUILocaleUtil().getMessage("subject.tagged.room"), sbTmp.toString()));
                sb.append("\n");
                tagged = true;
            }

            sql = "SELECT DISTINCT spec.SPEC_NAME || '(' || entr_year.ENTRANCE_YEAR || ')' CURRICULUM " +
                    "FROM CURRICULUM curr INNER JOIN SPECIALITY spec ON curr.SPECIALITY_ID = spec.ID " +
                    "  INNER JOIN ENTRANCE_YEAR entr_year ON curr.ENTRANCE_YEAR_ID = entr_year.ID " +
                    "WHERE curr.DELETED = ?1 AND exists(SELECT 1 " +
                    "                                  FROM CURRICULUM_DETAIL curr_det " +
                    "                                  WHERE curr.ID = curr_det.CURRICULUM_ID AND curr_det.SUBJECT_ID = ?2 AND " +
                    "                                        curr_det.DELETED = ?3) " +
                    "UNION SELECT DISTINCT special.SPEC_NAME || '(' || cc.ENTRANCE_YEAR || ')' CURRICULUM " +
                    "      FROM CURRICULUM curric INNER JOIN SPECIALITY special ON curric.SPECIALITY_ID = special.ID " +
                    "        INNER JOIN ENTRANCE_YEAR cc ON curric.ENTRANCE_YEAR_ID = cc.ID " +
                    "      WHERE curric.DELETED = ?4 AND exists(SELECT 1 " +
                    "                                          FROM CURRICULUM_ADD_PROGRAM curr_add_progr " +
                    "                                          WHERE " +
                    "                                            curric.ID = curr_add_progr.CURRICULUM_ID AND curr_add_progr.SUBJECT_ID = ?5 " +
                    "                                            AND curr_add_progr.DELETED = ?6) " +
                    "UNION SELECT DISTINCT speciality.SPEC_NAME || '(' || entran_year.ENTRANCE_YEAR || ')' CURRICULUM " +
                    "      FROM CURRICULUM curricul INNER JOIN SPECIALITY speciality ON curricul.SPECIALITY_ID = speciality.ID " +
                    "        INNER JOIN ENTRANCE_YEAR entran_year ON curricul.ENTRANCE_YEAR_ID = entran_year.ID " +
                    "      WHERE curricul.DELETED = ?7 AND exists(SELECT 1 " +
                    "                                            FROM ELECTIVE_SUBJECT elect_subj " +
                    "                                            WHERE " +
                    "                                              curricul.ID = elect_subj.CURRICULUM_ID AND elect_subj.SUBJECT_ID = ?8 AND " +
                    "                                              elect_subj.DELETED = ?9)";
            params.put(1, Boolean.FALSE);
            params.put(2, e.getId().getId());
            params.put(3, Boolean.FALSE);
            params.put(4, Boolean.FALSE);
            params.put(5, e.getId().getId());
            params.put(6, Boolean.FALSE);
            params.put(7, Boolean.FALSE);
            params.put(8, e.getId().getId());
            params.put(9, Boolean.FALSE);
            tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                StringBuilder sbTmp = new StringBuilder();
                boolean first = true;
                for (Object o : tmpList) {
                    if (!first) {
                        sbTmp.append(",\n");
                    }

                    sbTmp.append(o);
                    first = false;
                }

                sb.append(String.format(getUILocaleUtil().getMessage("subject.used.curriculum"), sbTmp.toString()));
                sb.append("\n");
                tagged = true;
            }

            sql = "SELECT entr_year.ENTRANCE_YEAR || '(' || sem_period.PERIOD_NAME || ')' CURRICULUM " +
                    "FROM CURRICULUM_SUMMER curr_summer INNER JOIN SEMESTER_DATA sem_data ON curr_summer.SEMESTER_DATA_ID = sem_data.ID " +
                    "  INNER JOIN ENTRANCE_YEAR entr_year ON sem_data.YEAR_ID = entr_year.ID " +
                    "  INNER JOIN SEMESTER_PERIOD sem_period ON sem_data.SEMESTER_PERIOD_ID = sem_period.ID " +
                    "WHERE curr_summer.DELETED = ?1 AND exists(SELECT 1 " +
                    "                                           FROM CURRICULUM_SUMMER_DETAIL e " +
                    "                                           WHERE curr_summer.ID = e.CURRICULUM_ID AND e.SUBJECT_ID = ?2 AND " +
                    "                                                 e.DELETED = ?3)";
            params.put(1, Boolean.FALSE);
            params.put(2, e.getId().getId());
            params.put(3, Boolean.FALSE);
            tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                StringBuilder sbTmp = new StringBuilder();
                boolean first = true;
                for (Object o : tmpList) {
                    if (!first) {
                        sbTmp.append(",\n");
                    }

                    sbTmp.append(o);
                    first = false;
                }

                sb.append(String.format(getUILocaleUtil().getMessage("subject.used.curriculum"), sbTmp.toString()));
                sb.append("\n");
                tagged = true;
            }

            if (tagged) {
                sb.insert(0, "<b>");
                if (delete) {
                    Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"), sb.toString(), 450);
                } else {
                    sb.append("<b>");
                    sb.append(getUILocaleUtil().getMessage("confirm.editrecord"));
                    sb.append("</b>");
                    Message.showConfirm(sb.toString(), new SubjectEditYesListener(e), 450);
                }
            } else {
                FSubjectFilter sf = (FSubjectFilter) filterPanel.getFilterBean();
                if (delete) {
                    SUBJECT subject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBJECT.class, e.getId());
                    subject.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(subject);
                    doFilter(sf);
                } else {
                    FormModel fm = ((DBGridModel) subjectGW.getWidgetModel()).getFormModel();
                    fm.setReadOnly(false);
                    fm.setTitleVisible(false);
                    try {
                        fm.loadEntity(e.getId());
                        SubjectEdit se = new SubjectEdit(fm);
                        new SubjectDialog(se, SubjectView.this);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to edit the subject", ex);
                    }
                }
            }
        }
    }

    private class SubjectEditYesListener extends AbstractYesButtonListener {

        private final Entity e;

        SubjectEditYesListener(Entity e) {
            super();
            this.e = e;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            FormModel fm = ((DBGridModel) subjectGW.getWidgetModel()).getFormModel();
            fm.setReadOnly(false);
            fm.setTitleVisible(false);
            try {
                fm.loadEntity(e.getId());
//                SubjectEdit ee = new SubjectEdit(fm, (FSubjectFilter) filterPanel.getFilterBean());
//                SubjectUI.getInstance().openCommonView(ee);//TODO
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to edit the subject", ex);
            }
        }
    }
}
