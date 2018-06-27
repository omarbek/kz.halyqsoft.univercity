package kz.halyqsoft.univercity.modules.subject;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_FORMULA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
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
import org.r3a.common.vaadin.widget.ERefreshType;
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

    private final AbstractFormWidget baseDataFW;
    private GridWidget teacherGW;

    public SubjectEdit(FormModel baseDataFM) throws Exception {
        super();
        setBackButtonVisible(false);

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
                    try{
                        baseDataFW.refresh();
                        teacherGW.refresh();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

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
        teacherGM.setHeightByRows(20);
        teacherGM.setTitleResource("teachers");
        rightPanel.addComponent(teacherGW);
        rightPanel.setComponentAlignment(teacherGW, Alignment.MIDDLE_CENTER);

        content.addComponent(rightPanel);
        content.setExpandRatio(rightPanel, (float) .5);

        if (!baseDataFM.isCreateNew()) {
            refreshTeacher();
        }

        getTabSheet().addTab(content, getMasterTabTitle());
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
//        if (isNew) {
//            if (s.getCode() == null) {
//                QueryModel<SUBJECT> qm = new QueryModel<SUBJECT>(SUBJECT.class);
//                qm.addWhere("studyDirect", ECriteria.EQUAL, s.getStudyDirect().getId());
//                qm.addSelect("id", EAggregate.COUNT);
//                try {
//                    Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
//                    count++;
//                    String no = String.valueOf(count);
//                    if (count < 10) {
//                        no = "000" + no;
//                    } else if (count < 100) {
//                        no = "00" + no;
//                    } else if (count < 1000) {
//                        no = "0" + no;
//                    }
//
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(s.getStudyDirect().getCode());
//                    sb.append(s.getLevel().getId().toString());
//                    sb.append(s.getSubjectCycle().getId().toString());
//                    if (s.isMandatory()) {
//                        sb.append('1');
//                    } else {
//                        sb.append('0');
//                    }
//                    sb.append(no);
//                    s.setCode(sb.toString());
//                } catch (Exception ex) {
//                    CommonUtils.showMessageAndWriteLog("Unable to create a subject", ex);
//                }
//            }
//        } else {
//            if (!s.getCode().startsWith(s.getStudyDirect().getCode())) {
//                QueryModel<SUBJECT> qm = new QueryModel<SUBJECT>(SUBJECT.class);
//                qm.addWhere("studyDirect", ECriteria.EQUAL, s.getStudyDirect().getId());
//                qm.addSelect("id", EAggregate.COUNT);
//                try {
//                    Long count = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
//                    count++;
//                    String no = String.valueOf(count);
//                    if (count < 10) {
//                        no = "000" + no;
//                    } else if (count < 100) {
//                        no = "00" + no;
//                    } else if (count < 1000) {
//                        no = "0" + no;
//                    }
//
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(s.getStudyDirect().getCode());
//                    sb.append(s.getLevel().getId().toString());
//                    sb.append(s.getSubjectCycle().getId().toString());
//                    if (s.isMandatory()) {
//                        sb.append('1');
//                    } else {
//                        sb.append('0');
//                    }
//                    sb.append(no);
//                    s.setCode(sb.toString());
//                } catch (Exception ex) {
//                    CommonUtils.showMessageAndWriteLog("Unable to update a subject", ex);
//                }
//            }
//        }

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

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }
}
