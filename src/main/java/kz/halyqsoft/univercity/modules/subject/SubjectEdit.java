package kz.halyqsoft.univercity.modules.subject;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_FORMULA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
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

        FieldModel weekName = baseDataFM.getFieldModel("weekNumber");
        weekName.setReadOnly(true);

        FieldModel practiceType = baseDataFM.getFieldModel("practiceType");

        FKFieldModel practiceTypeFM = (FKFieldModel) baseDataFM.getFieldModel("practiceType");
        practiceTypeFM.getListeners().add(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent valueChangeEvent) {
                FKFieldModel creditabilityFM = (FKFieldModel) baseDataFM.getFieldModel("creditability");
                FKFieldModel academicFormulaFM = (FKFieldModel) baseDataFM.getFieldModel("academicFormula");
                if(practiceType.getValue()!=null){
                    baseDataFM.getFieldModel("weekNumber").setReadOnly(false);
                }else{
                    baseDataFM.getFieldModel("weekNumber").setReadOnly(true);
                }
                try {
                    creditabilityFM.refresh(practiceType);
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });

        FKFieldModel studyDirectFM = (FKFieldModel) baseDataFM.getFieldModel("studyDirect");
        studyDirectFM.setDialogHeight(400);
        studyDirectFM.setDialogWidth(500);

        FKFieldModel chairFM = (FKFieldModel) baseDataFM.getFieldModel("chair");
        chairFM.setDialogHeight(400);
        chairFM.setDialogWidth(400);
        QueryModel chairQM = chairFM.getQueryModel();
        chairQM.addWhereNotNull("parent");
        chairQM.addWhereAnd("deleted", Boolean.FALSE);



        QueryModel subjectCycleQM = ((FKFieldModel) baseDataFM.getFieldModel("subjectCycle")).
                getQueryModel();
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
        FieldModel withTeacherCountFM = baseDataFM.getFieldModel("withTeacherCount");
        FieldModel ownTeacherCountFM = baseDataFM.getFieldModel("ownCount");
        FieldModel totalCountFM = baseDataFM.getFieldModel("totalCount");

        creditabilityFM.getListeners().add(new CreditabilityChangeListener(academicFormula,
                academicFormulaFM, withTeacherCountFM, ownTeacherCountFM, totalCountFM, practiceTypeFM));

        FieldModel lcCountFM = baseDataFM.getFieldModel("lcCount");
        FieldModel prCountFM = baseDataFM.getFieldModel("prCount");
        FieldModel lbCountFM = baseDataFM.getFieldModel("lbCount");

        academicFormulaFM.getListeners().add(new AcademicFormulaChangeListener(lcCountFM, prCountFM,
                lbCountFM,practiceTypeFM));

        leftPanel.addComponent(baseDataFW);
        leftPanel.setComponentAlignment(baseDataFW, Alignment.MIDDLE_CENTER);

        if (!baseDataFM.isReadOnly()) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    baseDataFW.save();
                    try {
                        baseDataFW.refresh();
                        teacherGW.refresh();
                    } catch (Exception e) {
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
                emp.setFall((Boolean) oo[7]);
                emp.setSpring((Boolean) oo[8]);
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

    private class CreditabilityChangeListener implements Property.ValueChangeListener {

        private final ACADEMIC_FORMULA academicFormula;
        private final FKFieldModel academicFormulaFM;
        private final FieldModel withTeacherCountFM;
        private final FieldModel ownTeacherCountFM;
        private final FieldModel totalCountFM;
        private FKFieldModel practiceType;

        CreditabilityChangeListener(ACADEMIC_FORMULA academicFormula, FKFieldModel academicFormulaFM,
                                    FieldModel withTeacherCountFM, FieldModel ownTeacherCountFM,
                                    FieldModel totalCountFM,FKFieldModel practiceType) {
            this.academicFormula = academicFormula;
            this.academicFormulaFM = academicFormulaFM;
            this.withTeacherCountFM = withTeacherCountFM;
            this.ownTeacherCountFM = ownTeacherCountFM;
            this.totalCountFM = totalCountFM;
            this.practiceType = practiceType;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            Object value = ev.getProperty().getValue();
            QueryModel qm = academicFormulaFM.getQueryModel();
            String count;
            String totalCount;

            if (value != null &&practiceType.getValue()==null) {
                CREDITABILITY creditability = (CREDITABILITY) value;
                qm.addWhere("creditability", ECriteria.EQUAL, creditability.getId());
                count = creditability.getCredit() * 15 + "";
                totalCount = creditability.getCredit() * 15 * 3 + "";
            }else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.PRODUCTION_ID)) {
                CREDITABILITY creditability = (CREDITABILITY) value;
                qm.addWhere("creditability", ECriteria.EQUAL, creditability.getId());
                count = creditability.getCredit() * 25 + "";
                totalCount = creditability.getCredit() * 25 * 3 + "";
            } else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.EDUCATIONAL_ID)) {
                CREDITABILITY creditability = (CREDITABILITY) value;
                qm.addWhere("creditability", ECriteria.EQUAL, creditability.getId());
                count = creditability.getCredit() * 5 + "";
                totalCount = creditability.getCredit() * 5 * 3 + "";
            } else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.PEDAGOGICAL_ID)) {
                CREDITABILITY creditability = (CREDITABILITY) value;
                qm.addWhere("creditability", ECriteria.EQUAL, creditability.getId());
                count = creditability.getCredit() * 10 + "";
                totalCount = creditability.getCredit() * 10 * 3 + "";
            }  else {
                practiceType.getValue().equals(PRACTICE_TYPE.PRODUCTION_ID);
                qm.addWhere("creditability", ECriteria.EQUAL, ID.valueOf(-1));
                count = "0";
                totalCount = "0";
            }
            try {
                academicFormulaFM.refresh(academicFormula);
                withTeacherCountFM.refresh(count);
                ownTeacherCountFM.refresh(count);
                totalCountFM.refresh(totalCount);
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
    }

    private class AcademicFormulaChangeListener implements Property.ValueChangeListener {

        private final FieldModel lcCountFM;
        private final FieldModel prCountFM;
        private final FieldModel lbCountFM;
        private FKFieldModel practiceType;


        AcademicFormulaChangeListener(FieldModel lcCountFM, FieldModel prCountFM,
                                      FieldModel lbCountFM,FKFieldModel practiceType) {
            this.lcCountFM = lcCountFM;
            this.prCountFM = prCountFM;
            this.lbCountFM = lbCountFM;
            this.practiceType = practiceType;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            Object value = ev.getProperty().getValue();
            String lcCount;
            String prCount;
            String lbCount;
            if (value != null && practiceType.getValue()==null) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) value;
                lcCount = academicFormula.getLcCount() * 15 + "";
                prCount = academicFormula.getPrCount() * 15 + "";
                lbCount = academicFormula.getLbCount() * 15 + "";
            }else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.PRODUCTION_ID)) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) value;
                lcCount = academicFormula.getLcCount() * 25 + "";
                prCount = academicFormula.getPrCount() * 25 + "";
                lbCount = academicFormula.getLbCount() * 25 + "";
            } else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.EDUCATIONAL_ID)) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) value;
                lcCount = academicFormula.getLcCount() * 5 + "";
                prCount = academicFormula.getPrCount() * 5 + "";
                lbCount = academicFormula.getLbCount() * 5 + "";
            } else if (value != null && ((PRACTICE_TYPE)practiceType.getValue()).getId().equals(PRACTICE_TYPE.PEDAGOGICAL_ID)) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) value;
                lcCount = academicFormula.getLcCount() * 10 + "";
                prCount = academicFormula.getPrCount() * 10 + "";
                lbCount = academicFormula.getLbCount() * 10 + "";
            } else {
                lcCount = "0";
                prCount = "0";
                lbCount = "0";
            }
            try {
                lcCountFM.refresh(lcCount);
                prCountFM.refresh(prCount);
                lbCountFM.refresh(lbCount);
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