package kz.halyqsoft.univercity.modules.subject;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VSubjectRequisite;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.filter.FSubjectFilter;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.FieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Omarbek Dinassil
 * @created Dec 25, 2015 1:56:38 PM
 */

@SuppressWarnings("serial")
public final class SubjectEdit extends AbstractFormWidgetView {

    private final FSubjectFilter filter;
    private final AbstractFormWidget baseDataFW;
    private Grid prerequisiteGrid;
    private Grid postrequisiteGrid;
    private GridWidget teacherGW;
    private CustomGridSelectDialog subjectSelectDlg;

    public SubjectEdit(FormModel baseDataFM, FSubjectFilter filter) throws Exception {
        super();
        setBackButtonVisible(false);
        this.filter = filter;

        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);

        init();
    }

    private void init() throws Exception {

        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        content.setWidthUndefined();
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setSpacing(true);
        leftPanel.setWidthUndefined();
        FormModel baseDataFM = baseDataFW.getWidgetModel();
        baseDataFM.setButtonsVisible(false);

        FieldModel codeFM = baseDataFM.getFieldModel("code");
        codeFM.setReadOnlyFixed(false);
        codeFM.setRequired(true);

        FKFieldModel studyDirectFM = (FKFieldModel) baseDataFM.getFieldModel("studyDirect");
        studyDirectFM.setDialogHeight(400);
        studyDirectFM.setDialogWidth(500);

        FKFieldModel chairFM = (FKFieldModel) baseDataFM.getFieldModel("chair");
        chairFM.setDialogHeight(400);
        chairFM.setDialogWidth(400);
        QueryModel chairQM = chairFM.getQueryModel();
        chairQM.addWhereNotNull("parent");
        chairQM.addWhereAnd("deleted", Boolean.FALSE);

        QueryModel subjectCycleQM = ((FKFieldModel) baseDataFM.getFieldModel("subjectCycle")).getQueryModel();
        subjectCycleQM.addOrder("cycleShortName");

        FKFieldModel creditabilityFM = (FKFieldModel) baseDataFM.getFieldModel("creditability");

        FKFieldModel academicFormulaFM = (FKFieldModel) baseDataFM.getFieldModel("academicFormula");
        QueryModel academicFormulaQM = academicFormulaFM.getQueryModel();
        academicFormulaQM.addWhere("creditability", ECriteria.EQUAL, ID.valueOf(-1));

        ACADEMIC_FORMULA academicFormula = null;
        if (!baseDataFM.isCreateNew()) {
            try {
                SUBJECT subject = (SUBJECT) baseDataFM.getEntity();
                academicFormula = subject.getAcademicFormula();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to find chair or academic formula", ex);
            }
        }

        creditabilityFM.getListeners().add(new CreditabilityChangeListener(academicFormula, academicFormulaFM));

        leftPanel.addComponent(baseDataFW);
        leftPanel.setComponentAlignment(baseDataFW, Alignment.MIDDLE_CENTER);

        if (!baseDataFM.isReadOnly()) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    baseDataFW.save();
                }
            });

            buttonPanel.addComponent(save);
            buttonPanel.setComponentAlignment(save, Alignment.BOTTOM_CENTER);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    baseDataFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            buttonPanel.setComponentAlignment(cancel, Alignment.BOTTOM_CENTER);
            leftPanel.addComponent(buttonPanel);
            leftPanel.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        content.addComponent(leftPanel);
        content.setExpandRatio(leftPanel, (float) .4);

        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.setWidth("609px");

        Label l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("prerequisites"));
        rightPanel.addComponent(l);
        rightPanel.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        Button b = new Button();
        b.setCaption(getUILocaleUtil().getCaption("new"));
        b.setIcon(new ThemeResource("img/button/new.png"));
        b.addClickListener(new RequisiteAddListener(true));
        hl.addComponent(b);

        b = new Button();
        b.setCaption(getUILocaleUtil().getCaption("delete"));
        b.setIcon(new ThemeResource("img/button/delete.png"));
        b.addClickListener(new RequisiteDeleteListener(true));
        hl.addComponent(b);

        rightPanel.addComponent(hl);
        rightPanel.setComponentAlignment(hl, Alignment.MIDDLE_LEFT);

        prerequisiteGrid = new Grid();
        prerequisiteGrid.setSizeFull();
        prerequisiteGrid.addColumn("code").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "code")).setWidth(130);
        prerequisiteGrid.addColumn("subjectName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "subjectName")).setWidthUndefined();
        prerequisiteGrid.addColumn("credit", Integer.class).setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "credit")).setWidth(80);
        prerequisiteGrid.addColumn("formula").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "formula")).setWidth(120);
        prerequisiteGrid.addColumn("controlTypeName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "controlTypeName")).setWidth(130);
        prerequisiteGrid.setSelectionMode(SelectionMode.SINGLE);
        prerequisiteGrid.setHeightMode(HeightMode.ROW);
        prerequisiteGrid.setHeightByRows(6);
        rightPanel.addComponent(prerequisiteGrid);
        rightPanel.setComponentAlignment(prerequisiteGrid, Alignment.MIDDLE_CENTER);

        l = new Label();
        l.setWidthUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("postrequisites"));
        rightPanel.addComponent(l);
        rightPanel.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

        hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidthUndefined();

        b = new Button();
        b.setCaption(getUILocaleUtil().getCaption("new"));
        b.setIcon(new ThemeResource("img/button/new.png"));
        b.addClickListener(new RequisiteAddListener(false));
        hl.addComponent(b);

        b = new Button();
        b.setCaption(getUILocaleUtil().getCaption("delete"));
        b.setIcon(new ThemeResource("img/button/delete.png"));
        b.addClickListener(new RequisiteDeleteListener(false));
        hl.addComponent(b);

        rightPanel.addComponent(hl);
        rightPanel.setComponentAlignment(hl, Alignment.MIDDLE_LEFT);

        postrequisiteGrid = new Grid();
        postrequisiteGrid.setSizeFull();
        postrequisiteGrid.addColumn("code").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "code")).setWidth(130);
        postrequisiteGrid.addColumn("subjectName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "subjectName")).setWidthUndefined();
        postrequisiteGrid.addColumn("credit", Integer.class).setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "credit")).setWidth(80);
        postrequisiteGrid.addColumn("formula").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "formula")).setWidth(120);
        postrequisiteGrid.addColumn("controlTypeName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VSubjectRequisite.class, "controlTypeName")).setWidth(130);
        postrequisiteGrid.setSelectionMode(SelectionMode.SINGLE);
        postrequisiteGrid.setHeightMode(HeightMode.ROW);
        postrequisiteGrid.setHeightByRows(6);
        rightPanel.addComponent(postrequisiteGrid);
        rightPanel.setComponentAlignment(postrequisiteGrid, Alignment.MIDDLE_CENTER);

        teacherGW = new GridWidget(VEmployee.class);
        teacherGW.showToolbar(false);
        DBGridModel teacherGM = (DBGridModel) teacherGW.getWidgetModel();
        teacherGM.setReadOnly(true);
        teacherGM.getColumnModel("code").setInGrid(false);
        teacherGM.getColumnModel("lecture").setInGrid(true);
        teacherGM.getColumnModel("laboratory").setInGrid(true);
        teacherGM.getColumnModel("practice").setInGrid(true);
        teacherGM.getColumnModel("fall").setInGrid(true);
        teacherGM.getColumnModel("spring").setInGrid(true);
        teacherGM.setRefreshType(ERefreshType.MANUAL);
        teacherGM.setHeightByRows(4);
        teacherGM.setTitleResource("teachers");
        rightPanel.addComponent(teacherGW);
        rightPanel.setComponentAlignment(teacherGW, Alignment.MIDDLE_CENTER);

        content.addComponent(rightPanel);
        content.setExpandRatio(rightPanel, (float) .5);

        if (!baseDataFM.isCreateNew()) {
            refreshRequisite(true);
            refreshRequisite(false);
            refreshTeacher();
        }

        getTabSheet().addTab(content, getMasterTabTitle());
    }

    private void refreshRequisite(boolean preRequisite) throws Exception {
        String sql = "SELECT " +
                "  subj_requis.ID, " +
                "  subj.NAME_KZ, " +
                "  subj.NAME_EN, " +
                "  subj.NAME_RU, " +
                "  subj.CODE, " +
                "  cred.CREDIT, " +
                "  acad_formula.FORMULA, " +
                "  control_type.TYPE_NAME CONTROL_TYPE_NAME " +
                "FROM SUBJECT_REQUISITE subj_requis INNER JOIN SUBJECT subj ON subj_requis.REQUISITE_ID = subj.ID " +
                "  INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID " +
                "  INNER JOIN ACADEMIC_FORMULA acad_formula ON subj.ACADEMIC_FORMULA_ID = acad_formula.ID " +
                "  INNER JOIN CONTROL_TYPE control_type ON subj.CONTROL_TYPE_ID = control_type.ID " +
                "WHERE subj_requis.SUBJECT_ID = ?1 AND subj_requis.PRE_REQUISITE = ?2";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, baseDataFW.getWidgetModel().getEntity().getId().getId());
        params.put(2, preRequisite);
        try {
            List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            List<VSubjectRequisite> list = new ArrayList<VSubjectRequisite>(tempList.size());
            Locale locale = getUILocaleUtil().getCurrentLocale();
            for (Object o : tempList) {
                Object[] oo = (Object[]) o;
                VSubjectRequisite vsr = new VSubjectRequisite(locale);
                vsr.setId(ID.valueOf((long) oo[0]));
                vsr.setNameKZ((String) oo[1]);
                vsr.setNameEN((String) oo[2]);
                vsr.setNameRU((String) oo[3]);
                vsr.setCode((String) oo[4]);
                vsr.setCredit(((BigDecimal) oo[5]).intValue());
                vsr.setFormula((String) oo[6]);
                vsr.setControlTypeName((String) oo[7]);
                list.add(vsr);
            }

            BeanItemContainer<VSubjectRequisite> bic = new BeanItemContainer<VSubjectRequisite>(VSubjectRequisite.class, list);
            if (preRequisite) {
                prerequisiteGrid.setContainerDataSource(bic);
            } else {
                postrequisiteGrid.setContainerDataSource(bic);
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh pre/postrequisites of the subject", ex);
        }
    }

    private void refreshTeacher() throws Exception {
        String sql = "SELECT " +
                "  empl.ID, " +
                "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) TEACHER_FIO, " +
                "  dept.DEPT_NAME, " +
                "  post.POST_NAME, " +
                "  CASE WHEN teach_subj.GROUP_LEC_COUNT = 0 " +
                "    THEN 0 " +
                "  ELSE 1 END                                                                           LEC, " +
                "  CASE WHEN teach_subj.GROUP_LAB_COUNT = 0 " +
                "    THEN 0 " +
                "  ELSE 1 END                                                                           LAB, " +
                "  CASE WHEN teach_subj.GROUP_PRAC_COUNT = 0 " +
                "    THEN 0 " +
                "  ELSE 1 END                                                                           PRAC, " +
                "  teach_subj.FALL, " +
                "  teach_subj.SPRING " +
                "FROM EMPLOYEE empl INNER JOIN USERS usr ON empl.ID = usr.ID " +
                "  INNER JOIN EMPLOYEE_DEPT empl_dept ON empl.ID = empl_dept.EMPLOYEE_ID " +
                "  INNER JOIN DEPARTMENT dept ON empl_dept.DEPT_ID = dept.ID " +
                "  INNER JOIN POST post ON empl_dept.POST_ID = post.ID " +
                "  INNER JOIN TEACHER_SUBJECT teach_subj ON empl.ID = teach_subj.EMPLOYEE_ID " +
                "WHERE teach_subj.SUBJECT_ID = ?1 AND teach_subj.LOAD_PER_HOURS = ?2";
        Map<Integer, Object> params = new HashMap<Integer, Object>(2);
        params.put(1, baseDataFW.getWidgetModel().getEntity().getId().getId());
        params.put(2, Boolean.FALSE);
        try {
            List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            List<VEmployee> list = new ArrayList<VEmployee>(tempList.size());
            for (Object o : tempList) {
                Object[] oo = (Object[]) o;
                VEmployee emp = new VEmployee();
                emp.setId(ID.valueOf((long) oo[0]));
                emp.setFio((String) oo[1]);
                emp.setDeptName((String) oo[2]);
                emp.setPostName((String) oo[3]);
                emp.setLecture((int) oo[4] == 1);
                emp.setLaboratory((int) oo[5] == 1);
                emp.setPractice((int) oo[6] == 1);
                emp.setFall(((BigDecimal) oo[7]).intValue() == 1);
                emp.setSpring(((BigDecimal) oo[8]).intValue() == 1);
                list.add(emp);
            }

            ((DBGridModel) teacherGW.getWidgetModel()).setEntities(list);
            teacherGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh teacher list of the subject", ex);
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "subjectEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        FormModel fm = baseDataFW.getWidgetModel();
        if (fm.isCreateNew()) {
            return getUILocaleUtil().getCaption("subject.new");
        } else {
            StringBuilder sb = new StringBuilder();
            if (!fm.isReadOnly()) {
                sb.append(getUILocaleUtil().getCaption("subject.edit"));
            } else {
                sb.append(getUILocaleUtil().getCaption("subject.view"));
            }

            sb.append(": ");
            try {
                SUBJECT s = (SUBJECT) baseDataFW.getWidgetModel().getEntity();
                sb.append(s.getCode());
                sb.append(" - ");
                sb.append(s.getNameRU());
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create view title", ex);
            }

            return sb.toString();
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        if (source.equals(baseDataFW)) {
            return preSaveBaseData(source, e, isNew, buttonId);
        }

        return super.preSave(source, e, isNew, buttonId);
    }

    private boolean preSaveBaseData(Object source, Entity e, boolean isNew, int buttonId) {
        SUBJECT s = (SUBJECT) e;
        if (isNew) {
            if (s.getCode() == null) {
                QueryModel<SUBJECT> qm = new QueryModel<SUBJECT>(SUBJECT.class);
                qm.addWhere("studyDirect", ECriteria.EQUAL, s.getStudyDirect().getId());
                qm.addSelect("id", EAggregate.COUNT);
                try {
                    Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
                    count++;
                    String no = String.valueOf(count);
                    if (count < 10) {
                        no = "000" + no;
                    } else if (count < 100) {
                        no = "00" + no;
                    } else if (count < 1000) {
                        no = "0" + no;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(s.getStudyDirect().getCode());
                    sb.append(s.getLevel().getId().toString());
                    sb.append(s.getSubjectCycle().getId().toString());
                    if (s.isMandatory()) {
                        sb.append('1');
                    } else {
                        sb.append('0');
                    }
                    sb.append(no);
                    s.setCode(sb.toString());
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create a subject", ex);
                }
            }
        } else {
            if (!s.getCode().startsWith(s.getStudyDirect().getCode())) {
                QueryModel<SUBJECT> qm = new QueryModel<SUBJECT>(SUBJECT.class);
                qm.addWhere("studyDirect", ECriteria.EQUAL, s.getStudyDirect().getId());
                qm.addSelect("id", EAggregate.COUNT);
                try {
                    Long count = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
                    count++;
                    String no = String.valueOf(count);
                    if (count < 10) {
                        no = "000" + no;
                    } else if (count < 100) {
                        no = "00" + no;
                    } else if (count < 1000) {
                        no = "0" + no;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(s.getStudyDirect().getCode());
                    sb.append(s.getLevel().getId().toString());
                    sb.append(s.getSubjectCycle().getId().toString());
                    if (s.isMandatory()) {
                        sb.append('1');
                    } else {
                        sb.append('0');
                    }
                    sb.append(no);
                    s.setCode(sb.toString());
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to update a subject", ex);
                }
            }
        }

        return true;
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED) {
            if (ev.getSource().equals(baseDataFW)) {
                try {
                    baseDataFW.refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh the subject after creating", ex);
                }
            }
        }

        super.handleEntityEvent(ev);
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

    private class CreditabilityChangeListener implements Property.ValueChangeListener {

        private final ACADEMIC_FORMULA academicFormula;
        private final FKFieldModel academicFormulaFM;

        public CreditabilityChangeListener(ACADEMIC_FORMULA academicFormula, FKFieldModel academicFormulaFM) {
            this.academicFormula = academicFormula;
            this.academicFormulaFM = academicFormulaFM;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            Object value = ev.getProperty().getValue();
            QueryModel qm = academicFormulaFM.getQueryModel();
            if (value != null) {
                qm.addWhere("creditability", ECriteria.EQUAL, ((CREDITABILITY) value).getId());
            } else {
                qm.addWhere("creditability", ECriteria.EQUAL, ID.valueOf(-1));
            }
            try {
                academicFormulaFM.refresh(academicFormula);
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
    }

    private class RequisiteAddListener implements ClickListener {

        private final boolean prerequisite;

        public RequisiteAddListener(boolean prerequisite) {
            this.prerequisite = prerequisite;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            baseDataFW.getWidgetModel().setShowFormSaveError(false);
            if (!baseDataFW.save()) {
                baseDataFW.getWidgetModel().setShowFormSaveError(true);
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return;
            }
            baseDataFW.getWidgetModel().setShowFormSaveError(true);

            subjectSelectDlg = new CustomGridSelectDialog(new RequisiteAddYesListener(prerequisite), V_SUBJECT_SELECT.class);
            subjectSelectDlg.getSelectModel().setMultiSelect(false);
            subjectSelectDlg.setFilterRequired(true);
            subjectSelectDlg.setDialogHeight(400);
            subjectSelectDlg.setDialogWidth(600);
            QueryModel requisiteQM = ((DBSelectModel) subjectSelectDlg.getSelectModel()).getQueryModel();
            requisiteQM.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));

            try {
                QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
                chairQM.addWhereNotNull("parent");
                chairQM.addWhereAnd("deleted", Boolean.FALSE);
                chairQM.addOrder("deptName");
                BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
                ComboBox chairCB = new ComboBox();
                chairCB.setContainerDataSource(chairBIC);
                chairCB.setImmediate(true);
                chairCB.setNullSelectionAllowed(true);
                chairCB.setTextInputAllowed(true);
                chairCB.setFilteringMode(FilteringMode.CONTAINS);
                chairCB.setWidth(400, Unit.PIXELS);
                chairCB.setPageLength(0);

                QueryModel<LEVEL> levelQM = new QueryModel<>(LEVEL.class);
                levelQM.addOrder("levelName");
                BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<>(LEVEL.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
                ComboBox levelCB = new ComboBox();
                levelCB.setContainerDataSource(levelBIC);
                levelCB.setImmediate(true);
                levelCB.setNullSelectionAllowed(true);
                levelCB.setTextInputAllowed(false);
                levelCB.setFilteringMode(FilteringMode.OFF);

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

                subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
                subjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                subjectSelectDlg.initFilter();
                subjectSelectDlg.open();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
            }
        }
    }

    private class RequisiteDeleteListener implements ClickListener {

        private final boolean prerequisite;

        public RequisiteDeleteListener(boolean prerequisite) {
            this.prerequisite = prerequisite;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            List<VSubjectRequisite> list = new ArrayList<VSubjectRequisite>(1);
            if (prerequisite) {
                Object o = prerequisiteGrid.getSelectedRow();
                if (o != null) {
                    list.add((VSubjectRequisite) o);
                }
            } else {
                Object o = postrequisiteGrid.getSelectedRow();
                if (o != null) {
                    list.add((VSubjectRequisite) o);
                }
            }

            if (!list.isEmpty()) {
                Message.showConfirm(getUILocaleUtil().getMessage("confirm.deleterecords"), new RequisiteDelYesListener(list, prerequisite));
            }
        }
    }

    private class RequisiteAddYesListener extends AbstractYesButtonListener {

        private final boolean prerequisite;

        public RequisiteAddYesListener(boolean prerequisite) {
            this.prerequisite = prerequisite;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            List<Entity> list = subjectSelectDlg.getSelectedEntities();
            try {
                SUBJECT subject = (SUBJECT) baseDataFW.getWidgetModel().getEntity();
                for (Entity e : list) {
                    SUBJECT_REQUISITE sr = new SUBJECT_REQUISITE();
                    sr.setSubject(subject);
                    sr.setRequisite(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBJECT.class, e.getId()));
                    sr.setPreRequisite(prerequisite);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sr);
                }

                refreshRequisite(prerequisite);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to add pre/postrequisite", ex);
            }
        }

        @Override
        protected boolean canClose() {
            return !subjectSelectDlg.getSelectedEntities().isEmpty();
        }

        @Override
        protected boolean canProcess() {
            return !subjectSelectDlg.getSelectedEntities().isEmpty();
        }
    }

    private class RequisiteDelYesListener extends AbstractYesButtonListener {

        private final List<VSubjectRequisite> list;
        private final boolean prerequisite;

        public RequisiteDelYesListener(List<VSubjectRequisite> list, boolean prerequisite) {
            this.list = list;
            this.prerequisite = prerequisite;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            List<ID> idList = new ArrayList<>(list.size());
            for (VSubjectRequisite vsr : list) {
                idList.add(vsr.getId());
            }

            QueryModel<SUBJECT_REQUISITE> srQM = new QueryModel<>(SUBJECT_REQUISITE.class);
            srQM.addWhereIn("id", idList);
            try {
                List<SUBJECT_REQUISITE> delList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(srQM);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                refreshRequisite(prerequisite);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete pre/posrequisites", ex);
            }
        }
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }
}
