package kz.halyqsoft.univercity.modules.employee;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.modules.employee.workhour.WorkHourWidget;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.*;
import org.postgresql.util.PGInterval;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.FieldModel;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.footer.EColumnFooterType;
import org.r3a.common.vaadin.widget.grid.footer.IntegerColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.footer.StringColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetEvent;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetListener;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadinaddon.IntegerField;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 12.04.2018
 */
public class EmployeeEdit extends AbstractFormWidgetView implements PhotoWidgetListener {

    private final AbstractFormWidget baseDataFW;
    private USER_PHOTO userPhoto;
    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;
    private CommonFormWidget userPassportFW;
    private CommonFormWidget militaryDocFW;
    private CommonFormWidget disabilityDocFW;
    private CommonFormWidget repatriateDocFW;
    private TableWidget educationTW;
    private FromItem educationUDFI;
    private TableWidget languageTW;
    private TableWidget medicalCheckupTW;
    private CommonFormWidget addressRegFW;
    private CommonFormWidget addressFactFW;
    private TableWidget scientificDegreeTW;
    private TableWidget publicationTW;
    private TableWidget scientificActivityTW;
    private TableWidget scientificManagementTW;
    private TableWidget experienceTW;
    private TableWidget careerTW;
    private TableWidget employeeSkillTW;
    private TableWidget employeeAwardTW;
    private TableWidget employeeQualificationTW;
    private TableWidget childTW;
    private TableWidget masterTW;
    private static WorkHourWidget whw;
    private TableWidget subjectPPSTW;
    private TableWidget loadByHourTW;
    private GridWidget graduateStudentLoadGW;
    private Label graduateStudentLoadCreditSumLabel;
    private TableWidget roomTW;
    private CustomGridSelectDialog roomSelectDlg;
    private VerticalLayout mainVL;
    private EmployeeView employeeView;
    private Label experienceL;

    private static final int DOES_NOT_WORK = 2;

    EmployeeEdit(final FormModel baseDataFM, VerticalLayout mainVL, EmployeeView employeeView)
            throws Exception {
        super();
        this.mainVL = mainVL;
        this.employeeView = employeeView;

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        //		hl.setSizeFull();

        baseDataFM.setButtonsVisible(false);

        FKFieldModel nationalityFM = (FKFieldModel) baseDataFM.getFieldModel("nationality");
        QueryModel nationalityQM = nationalityFM.getQueryModel();
        nationalityQM.addOrder("nationName");

        FKFieldModel citizenshipFM = (FKFieldModel) baseDataFM.getFieldModel("citizenship");
        QueryModel citizenshipQM = citizenshipFM.getQueryModel();
        citizenshipQM.addWhereNull("parent");
        citizenshipQM.addOrder("countryName");

        CommonUtils.setCards(baseDataFM);

        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);
        hl.addComponent(baseDataFW);
        hl.setComponentAlignment(baseDataFW, Alignment.TOP_LEFT);

        if (!baseDataFM.isCreateNew()) {
            QueryModel<USER_PHOTO> qmUserPhoto = new QueryModel<>(USER_PHOTO.class);
            qmUserPhoto.addWhere("user", ECriteria.EQUAL, baseDataFM.getEntity().getId());
            try {
                userPhoto = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmUserPhoto);
            } catch (NoResultException e) {
                userPhoto = null;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load user photo", ex);
            }
            if (userPhoto != null) {
                userPhotoBytes = userPhoto.getPhoto();
                userPhotoFilename = userPhoto.getFileName();
            }
        } else {
            baseDataFM.getFieldModel("login").setInEdit(false);
            baseDataFM.getFieldModel("login").setInView(false);
        }
        PhotoWidget userPW = new PhotoWidget(userPhotoBytes, baseDataFM.isReadOnly());
        userPW.setPhotoHeight(290, Unit.PIXELS);
        userPW.setPhotoWidth(230, Unit.PIXELS);
        userPW.setSaveButtonVisible(false);
        userPW.addListener(this);
        hl.addComponent(userPW);
        hl.setComponentAlignment(userPW, Alignment.TOP_RIGHT);
        content.addComponent(hl);
        content.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        content.setExpandRatio(hl, 1);

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

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    baseDataFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getMasterTabTitle());


        boolean readOnly = baseDataFW.getWidgetModel().isReadOnly();
        createDocumentsTab(readOnly);
        createEducationTab(readOnly);
        createMedicalCheckupTab(readOnly);
        createAddressesTab(readOnly);
        createScientificDegreeTab(readOnly);
        createScienceTab(readOnly);
        createExperienceTab(readOnly);
        createCareerTab(readOnly);
        createWorkDayTab(readOnly);
        createSubjectPPSTab(readOnly);
        createRoomTab(readOnly);
        createSkillAndAwardTab(readOnly);
        createChildTab(readOnly);
        createMasterTab(readOnly);
    }

    @Override
    protected AbstractCommonView getParentView() {
        try {
            employeeView.getTeacherGW().refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        mainVL.removeComponent(this);
        mainVL.addComponent(employeeView.getFilterPanel());
        mainVL.addComponent(employeeView.getTeacherGW());
        return employeeView;
    }

    @Override
    public String getViewName() {
        return "employeeEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        FormModel fm = baseDataFW.getWidgetModel();
        if (fm.isCreateNew()) {
            return getUILocaleUtil().getCaption("teacher.new");
        } else {
            StringBuilder sb = new StringBuilder();
            if (!fm.isReadOnly()) {
                sb.append(getUILocaleUtil().getCaption("teacher.edit"));
            } else {
                sb.append(getUILocaleUtil().getCaption("teacher.view"));
            }

            sb.append(": ");
            try {
                sb.append(baseDataFW.getWidgetModel().getEntity().toString());
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create view title", ex);
            }

            return sb.toString();
        }
    }


    private void createChildTab(boolean readOnly) throws Exception {

        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        childTW = new TableWidget(VChild.class);
        childTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        childTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        childTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        childTW.addEntityListener(this);

        DBTableModel childTM = (DBTableModel) childTW.getWidgetModel();
        childTM.setCrudEntityClass(CHILD.class);
        childTM.setRefreshType(ERefreshType.MANUAL);
        //childTM.setEntities(getChildList());

        refreshChild();
        getTabSheet().addTab(childTW, getUILocaleUtil().getEntityLabel(CHILD.class));
    }
    public List<VChild> getChildList() throws Exception{

        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        List<VChild> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT " +
                " child.id,\n" +
                "  sex.sex_name,\n" +
                "  date_part('year',age(child.birth_date))\n" +
                "from child\n" +
                "INNER JOIN sex  on child.sex_id = sex.id\n" +
                "  WHERE child.employee_id = " + employeeId +
                " GROUP BY child.id, sex.sex_name,child.birth_date";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VChild child = new VChild();
                    child.setId(ID.valueOf((long) oo[0]));
                    child.setSex((String) oo[1]);
                    child.setChildAge((double) oo[2]);
                    list.add(child);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load experience list", ex);
        }
        refreshChild(list);
        return list;
    }

    private void refreshChild(List<VChild> list) {
        ((DBTableModel) childTW.getWidgetModel()).setEntities(list);
        try {
            childTW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh experience list", ex);
        }
    }

    private void createMasterTab(boolean readOnly) throws Exception {
        masterTW = new TableWidget(MASTER.class);
        masterTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        masterTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        masterTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        masterTW.addEntityListener(this);
        DBTableModel masterTM = (DBTableModel) masterTW.getWidgetModel();
        masterTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        QueryModel masterQM = masterTM.getQueryModel();
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        masterQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        getTabSheet().addTab(masterTW, getUILocaleUtil().getEntityLabel(MASTER.class));
    }

    private void createSkillAndAwardTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setImmediate(true);
        content.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        employeeSkillTW = new TableWidget(EMPLOYEE_SKILL.class);
        employeeSkillTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        employeeSkillTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        employeeSkillTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        employeeSkillTW.addEntityListener(this);
        DBTableModel employeeSkillTM = (DBTableModel) employeeSkillTW.getWidgetModel();
        employeeSkillTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        QueryModel employeeSkillQM = employeeSkillTM.getQueryModel();
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        employeeSkillQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        employeeAwardTW = new TableWidget(EMPLOYEE_AWARD.class);
        employeeAwardTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        employeeAwardTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        employeeAwardTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        employeeAwardTW.addEntityListener(this);
        DBTableModel employeeAwardTM = (DBTableModel) employeeAwardTW.getWidgetModel();
        employeeAwardTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        QueryModel employeeAwardQM = employeeAwardTM.getQueryModel();

        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        employeeAwardQM.addWhere("employee", ECriteria.EQUAL, employeeId);


        employeeQualificationTW = new TableWidget(EMPLOYEE_QUALIFICATION.class);
        employeeQualificationTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        employeeQualificationTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        employeeQualificationTW.addEntityListener(this);
        DBTableModel employeeQualificationTM = (DBTableModel) employeeQualificationTW.getWidgetModel();
        employeeQualificationTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        QueryModel employeeQualificationQM = employeeQualificationTM.getQueryModel();

        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        employeeQualificationQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        content.addComponent(employeeSkillTW);
        content.addComponent(employeeAwardTW);
        content.addComponent(employeeQualificationTW);

        getTabSheet().addTab(content, getUILocaleUtil().getEntityLabel(EMPLOYEE_SKILL.class));
    }

    private void createRoomTab(boolean readOnly) throws Exception {
        roomTW = new TableWidget(V_TEACHER_ROOM.class);
        roomTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        roomTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        roomTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        roomTW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        roomTW.addEntityListener(this);
        DBTableModel roomTM = (DBTableModel) roomTW.getWidgetModel();
        roomTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        QueryModel roomQM = roomTM.getQueryModel();
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        roomQM.addWhere("teacher", ECriteria.EQUAL, employeeId);
        getTabSheet().addTab(roomTW, getUILocaleUtil().getEntityLabel(V_TEACHER_ROOM.class));
    }

    private void createSubjectPPSTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Subject PPS */
        subjectPPSTW = new TableWidget(V_TEACHER_SUBJECT.class);
        subjectPPSTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        subjectPPSTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        subjectPPSTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, true);
        subjectPPSTW.addEntityListener(this);
        DBTableModel subjectPPSTM = (DBTableModel) subjectPPSTW.getWidgetModel();
        subjectPPSTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        subjectPPSTM.setCrudEntityClass(TEACHER_SUBJECT.class);

        FormModel subjectPPSFM = ((DBTableModel) subjectPPSTW.getWidgetModel()).getFormModel();
        FKFieldModel subjectFM = (FKFieldModel) subjectPPSFM.getFieldModel("subject");
        subjectFM.setSelectType(ESelectType.CUSTOM_GRID);
        subjectFM.setDialogHeight(360);
        subjectFM.setDialogWidth(800);
        QueryModel subjectQM = subjectFM.getQueryModel();

        QueryModel subjectPPSQM = subjectPPSTM.getQueryModel();
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        subjectPPSQM.addWhere("employee", ECriteria.EQUAL, employeeId);
        subjectPPSQM.addWhere("loadPerHours", Boolean.FALSE);

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
            BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<>(LEVEL.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
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

            CustomGridSelectDialog cgsd = subjectFM.getCustomGridSelectDialog();
            QueryModel qm = ((DBGridModel) cgsd.getSelectModel()).getQueryModel();
            qm.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
            cgsd.getSelectModel().setMultiSelect(false);
            cgsd.getFilterModel().addFilter("chair", chairCB);
            cgsd.getFilterModel().addFilter("level", levelCB);
            cgsd.getFilterModel().addFilter("creditability", creditabilityCB);
            cgsd.setFilterRequired(true);
            cgsd.initFilter();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
        }

        content.addComponent(subjectPPSTW);
        content.setComponentAlignment(subjectPPSTW, Alignment.MIDDLE_CENTER);

        /* Load By Hours */
        loadByHourTW = new TableWidget(V_TEACHER_SUBJECT.class);
        loadByHourTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        loadByHourTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        loadByHourTW.addEntityListener(this);
        DBTableModel loadByHourTM = (DBTableModel) loadByHourTW.getWidgetModel();
        loadByHourTM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        loadByHourTM.setCrudEntityClass(TEACHER_SUBJECT.class);
        loadByHourTM.setTitleResource("load.by.hour");

        FormModel loadByHourFM = ((DBTableModel) loadByHourTW.getWidgetModel()).getFormModel();
        loadByHourFM.setTitleResource("load.by.hour");
        FKFieldModel subjectFM2 = (FKFieldModel) loadByHourFM.getFieldModel("subject");
        subjectFM2.setSelectType(ESelectType.CUSTOM_GRID);
        subjectFM2.setDialogHeight(360);
        subjectFM2.setDialogWidth(800);
        QueryModel subjectQM2 = subjectFM2.getQueryModel();

        QueryModel loadByHourQM = loadByHourTM.getQueryModel();
        loadByHourQM.addWhere("employee", ECriteria.EQUAL, employeeId);
        loadByHourQM.addWhere("loadPerHours", Boolean.TRUE);

        try {
            QueryModel<DEPARTMENT> chairQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
            chairQM.addWhereNotNull("parent");
            chairQM.addWhereAnd("deleted", Boolean.FALSE);
            chairQM.addOrder("deptName");
            BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
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

            CustomGridSelectDialog cgsd = subjectFM2.getCustomGridSelectDialog();
            QueryModel qm = ((DBGridModel) cgsd.getSelectModel()).getQueryModel();
            qm.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
            cgsd.getSelectModel().setMultiSelect(false);
            cgsd.getFilterModel().addFilter("code", subjectCodeTF);
            cgsd.getFilterModel().addFilter("chair", chairCB);
            cgsd.getFilterModel().addFilter("level", levelCB);
            cgsd.getFilterModel().addFilter("creditability", creditabilityCB);
            cgsd.setFilterRequired(true);
            cgsd.initFilter();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
        }

        content.addComponent(loadByHourTW);
        content.setComponentAlignment(loadByHourTW, Alignment.MIDDLE_CENTER);

        /* Graduate Student Load */
        graduateStudentLoadGW = new GridWidget(VGraduateStudentLoad.class);
        graduateStudentLoadGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        graduateStudentLoadGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        graduateStudentLoadGW.addEntityListener(this);
        DBGridModel graduateStudentLoadGM = (DBGridModel) graduateStudentLoadGW.getWidgetModel();
        graduateStudentLoadGM.setReadOnly(baseDataFW.getWidgetModel().isReadOnly());
        graduateStudentLoadGM.setCrudEntityClass(GRADUATE_STUDENT_LOAD.class);
        graduateStudentLoadGM.setHeightByRows(4);
        graduateStudentLoadGM.setRefreshType(ERefreshType.MANUAL);
        graduateStudentLoadGM.setFooterVisible(true);

        FormModel graduateStudentLoadFM = graduateStudentLoadGM.getFormModel();
        FKFieldModel levelFM = (FKFieldModel) graduateStudentLoadFM.getFieldModel("level");

        StringColumnFooterModel nameFooter = new StringColumnFooterModel();
        nameFooter.setFooterType(EColumnFooterType.CAPTION);
        nameFooter.setColumnName("levelName");
        nameFooter.setValue(getUILocaleUtil().getCaption("total"));
        graduateStudentLoadGM.addFooterModel(nameFooter);

        IntegerColumnFooterModel creditFooter = new IntegerColumnFooterModel();
        creditFooter.setFooterType(EColumnFooterType.SUM);
        creditFooter.setColumnName("studentCount");
        graduateStudentLoadGM.addFooterModel(creditFooter);

        content.addComponent(graduateStudentLoadGW);
        content.setComponentAlignment(graduateStudentLoadGW, Alignment.MIDDLE_CENTER);

        graduateStudentLoadCreditSumLabel = new Label();
        graduateStudentLoadCreditSumLabel.addStyleName("bold");
        graduateStudentLoadCreditSumLabel.setWidthUndefined();
        graduateStudentLoadCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), 0));

        content.addComponent(graduateStudentLoadCreditSumLabel);
        content.setComponentAlignment(graduateStudentLoadCreditSumLabel, Alignment.MIDDLE_LEFT);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("subject.pps"));
    }

    private boolean preSaveSubjectPPS(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        TEACHER_SUBJECT sp = (TEACHER_SUBJECT) e;
//        if ((sp.getGroupLecCount() > 0 && sp.getGroupSizeLecture() == null) || (sp.getGroupLabCount() > 0 && sp.getGroupSizeLab() == null) || (sp.getGroupPracCount() > 0 && sp.getGroupSizePrac() == null)) {
//            throw new Exception(getUILocaleUtil().getMessage("error.incorrect.group.size"));
//        }
//
//        if ((sp.getGroupLecCount() == 0 && sp.getGroupSizeLecture() != null) || (sp.getGroupLabCount() == 0 && sp.getGroupSizeLab() != null) || (sp.getGroupPracCount() == 0 && sp.getGroupSizePrac() != null)) {
//            throw new Exception(getUILocaleUtil().getMessage("error.incorrect.group.count"));
//        }

        if (isNew) {
            sp.setEmployee((EMPLOYEE) baseDataFW.getWidgetModel().getEntity());
            sp.setLoadPerHours(false);
        }

        return true;
    }

    private boolean preSaveLoadByHour(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        TEACHER_SUBJECT sp = (TEACHER_SUBJECT) e;
//        if ((sp.getGroupLecCount() > 0 && sp.getGroupSizeLecture() == null) || (sp.getGroupLabCount() > 0 && sp.getGroupSizeLab() == null) || (sp.getGroupPracCount() > 0 && sp.getGroupSizePrac() == null)) {
//            throw new Exception(getUILocaleUtil().getMessage("error.incorrect.group.size"));
//        }
//
//        if ((sp.getGroupLecCount() == 0 && sp.getGroupSizeLecture() != null) || (sp.getGroupLabCount() == 0 && sp.getGroupSizeLab() != null) || (sp.getGroupPracCount() == 0 && sp.getGroupSizePrac() != null)) {
//            throw new Exception(getUILocaleUtil().getMessage("error.incorrect.group.count"));
//        }

        if (isNew) {
            sp.setEmployee((EMPLOYEE) baseDataFW.getWidgetModel().getEntity());
            sp.setLoadPerHours(true);
        }

        return true;
    }

    private boolean preSaveGraduateStudentLoad(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        int maxCredit = 3;
        GRADUATE_STUDENT_LOAD gsl = (GRADUATE_STUDENT_LOAD) e;
        int studentCount = gsl.getStudentCount();
        if (studentCount <= 0) {
            throw new Exception(getUILocaleUtil().getMessage("enter.student.count"));
        }

        String sql = "select LEVEL_ID, STUDENT_COUNT from GRADUATE_STUDENT_LOAD " +
                "where EMPLOYEE_ID = ?1 and LEVEL_ID != ?2";
        Map<Integer, Object> params = new HashMap<>(2);
        params.put(1, baseDataFW.getWidgetModel().getEntity().getId().getId());
        params.put(2, gsl.getLevel().getId().getId());

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            double b = .0;
            double m = .0;
            double d = .0;

            long levelId = gsl.getLevel().getId().getId().longValue();

            if (levelId == 1) {
                b = .25 * studentCount;
            } else if (levelId == 2) {
                m = .5 * studentCount;
            } else {
                d = studentCount;
            }
            for (Object o : tmpList) {
                Object[] oo = (Object[]) o;
                levelId = (long) oo[0];
                studentCount = ((BigDecimal) oo[1]).intValue();
                if (levelId == 1) {
                    b = .25 * studentCount;
                } else if (levelId == 2) {
                    m = .5 * studentCount;
                } else {
                    d = studentCount;
                }
            }

            if ((b + m + d) > maxCredit) {
                throw new Exception(String.format(getUILocaleUtil().getMessage("total.credit.cant.be.more"), maxCredit));
            }

            if (isNew) {
                gsl.setTeacher((EMPLOYEE) baseDataFW.getWidgetModel().getEntity());
            }
        } catch (Exception ex) {
            throw ex;
        }

        return true;
    }
        private void createDocumentsTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        GridLayout forms = new GridLayout(2, 2);
        forms.setSizeFull();
        forms.setSpacing(true);

        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<USER_DOCUMENT_FILE>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("userDocument", ECriteria.EQUAL, null);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);

        /* Passport */
        StringBuilder sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("identity.document"));
        userPassportFW = new CommonFormWidget(USER_PASSPORT.class);
        userPassportFW.addEntityListener(this);
        final FormModel userPassportFM = userPassportFW.getWidgetModel();
        userPassportFM.setReadOnly(readOnly);
        userPassportFM.setTitleResource("identity.document");
        userPassportFM.setErrorMessageTitle(sb.toString());
        userPassportFM.setButtonsVisible(false);
        userPassportFM.getFieldModel("expireDate").setRequired(true);

        FKFieldModel birthCountryFieldModel = (FKFieldModel) userPassportFM.getFieldModel("birthCountry");
        QueryModel birthCountryQM = birthCountryFieldModel.getQueryModel();
        birthCountryQM.addWhereNull("parent");
        birthCountryQM.addOrder("countryName");

        FKFieldModel birthRegionFieldModel = (FKFieldModel) userPassportFM.getFieldModel("birthRegion");
        QueryModel birthRegionQM = birthRegionFieldModel.getQueryModel();
        birthRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FileListFieldModel passportFLFM = (FileListFieldModel) userPassportFM.getFieldModel("fileList");
        passportFLFM.permitMimeType(FileListFieldModel.JPEG);

        COUNTRY birthRegion = null;
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            userPassportFM.createNew();
        } else {
            QueryModel<USER_PASSPORT> userPassportQM = new QueryModel<USER_PASSPORT>(USER_PASSPORT.class);
            FromItem fi = userPassportQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            userPassportQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            userPassportQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                USER_PASSPORT userPassport = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userPassportQM);
                if (userPassport != null) {
                    userPassportFM.loadEntity(userPassport.getId());
                    birthRegion = ((USER_PASSPORT) userPassportFM.getEntity()).getBirthRegion();
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, userPassport.getId());
                    List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                    if (!udfList.isEmpty()) {
                        for (Object o : udfList) {
                            Object[] oo = (Object[]) o;
                            FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                            fe.setId(ID.valueOf((Long) oo[0]));
                            fe.setFileName((String) oo[1]);
                            fe.setNewFile(false);
                            passportFLFM.getFileList().add(fe);
                        }
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    userPassportFM.loadEntity(ID.valueOf(-1));
                } else {
                    userPassportFM.createNew();
                }
            }
        }

        birthCountryFieldModel.getListeners().add(new BirthCountryChangeListener(birthRegionFieldModel, birthRegion));
        forms.addComponent(userPassportFW);

        /* Military doc */
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("military.document"));
        militaryDocFW = new CommonFormWidget(MILITARY_DOC.class);
        militaryDocFW.addEntityListener(this);
        final FormModel militaryDocFM = militaryDocFW.getWidgetModel();
        militaryDocFM.setReadOnly(readOnly);
        militaryDocFM.setTitleResource("military.document");
        militaryDocFM.setErrorMessageTitle(sb.toString());
        militaryDocFM.setButtonsVisible(false);

        FileListFieldModel militaryFLFM = (FileListFieldModel) militaryDocFM.getFieldModel("fileList");
        militaryFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (baseDataFW.getWidgetModel().isCreateNew()) {
            militaryDocFM.createNew();
        } else {
            QueryModel<MILITARY_DOC> militaryDocQM = new QueryModel<MILITARY_DOC>(MILITARY_DOC.class);
            FromItem fi = militaryDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            militaryDocQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            militaryDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                MILITARY_DOC militaryDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(militaryDocQM);
                militaryDocFM.loadEntity(militaryDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, militaryDoc.getId());
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        militaryFLFM.getFileList().add(fe);
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    militaryDocFM.loadEntity(ID.valueOf(-1));
                } else {
                    militaryDocFM.createNew();
                }
            }
        }
        forms.addComponent(militaryDocFW);

        /* Disability doc */
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("disability.document"));
        disabilityDocFW = new CommonFormWidget(DISABILITY_DOC.class);
        disabilityDocFW.addEntityListener(this);
        final FormModel disabilityDocFM = disabilityDocFW.getWidgetModel();
        disabilityDocFM.setReadOnly(readOnly);
        disabilityDocFM.setTitleResource("disability.document");
        disabilityDocFM.setErrorMessageTitle(sb.toString());
        disabilityDocFM.setButtonsVisible(false);

        FileListFieldModel disabilityFLFM = (FileListFieldModel) disabilityDocFM.getFieldModel("fileList");
        disabilityFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (baseDataFW.getWidgetModel().isCreateNew()) {
            disabilityDocFM.createNew();
        } else {
            QueryModel<DISABILITY_DOC> disabilityDocQM = new QueryModel<DISABILITY_DOC>(DISABILITY_DOC.class);
            FromItem fi = disabilityDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            disabilityDocQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            disabilityDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                DISABILITY_DOC disabilityDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(disabilityDocQM);
                disabilityDocFM.loadEntity(disabilityDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, disabilityDoc.getId());
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        disabilityFLFM.getFileList().add(fe);
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    disabilityDocFM.loadEntity(ID.valueOf(-1));
                } else {
                    disabilityDocFM.createNew();
                }
            }
        }
        forms.addComponent(disabilityDocFW);

        /* Repatriate doc */
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("repatriate.document"));
        repatriateDocFW = new CommonFormWidget(REPATRIATE_DOC.class);
        repatriateDocFW.addEntityListener(this);
        final FormModel repatriateDocFM = repatriateDocFW.getWidgetModel();
        repatriateDocFM.setReadOnly(readOnly);
        repatriateDocFM.setTitleResource("repatriate.document");
        repatriateDocFM.setErrorMessageTitle(sb.toString());
        repatriateDocFM.setButtonsVisible(false);

        FileListFieldModel repatriateFLFM = (FileListFieldModel) repatriateDocFM.getFieldModel("fileList");
        repatriateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (baseDataFW.getWidgetModel().isCreateNew()) {
            repatriateDocFM.createNew();
        } else {
            QueryModel<REPATRIATE_DOC> repatriateDocQM = new QueryModel<REPATRIATE_DOC>(REPATRIATE_DOC.class);
            FromItem fi = repatriateDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            repatriateDocQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            repatriateDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                REPATRIATE_DOC repatriateDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(repatriateDocQM);
                repatriateDocFM.loadEntity(repatriateDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, repatriateDoc.getId());
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        repatriateFLFM.getFileList().add(fe);
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    repatriateDocFM.loadEntity(ID.valueOf(-1));
                } else {
                    repatriateDocFM.createNew();
                }
            }
        }
        forms.addComponent(repatriateDocFW);

        content.addComponent(forms);
        content.setComponentAlignment(forms, Alignment.MIDDLE_CENTER);
        content.setExpandRatio(forms, 1);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (userPassportFM.isModified()) {
                        userPassportFW.save();
                    }

                    if (militaryDocFM.isModified()) {
                        militaryDocFW.save();
                    }

                    if (disabilityDocFM.isModified()) {
                        disabilityDocFW.save();
                    }

                    if (repatriateDocFM.isModified()) {
                        repatriateDocFW.save();
                    }
                }
            });
            buttonPanel.addComponent(save);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    userPassportFW.cancel();
                    militaryDocFW.cancel();
                    disabilityDocFW.cancel();
                    repatriateDocFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("documents"));
    }

    private void createEducationTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Education doc */
        educationTW = new TableWidget(EDUCATION_DOC.class);
        educationTW.addEntityListener(this);
        DBTableModel educationTM = (DBTableModel) educationTW.getWidgetModel();
        educationTM.setReadOnly(readOnly);

        educationTM.getColumnModel("entryYear").setAlignment(Table.Align.CENTER);
        educationTM.getColumnModel("endYear").setAlignment(Table.Align.CENTER);
        QueryModel educationQM = educationTM.getQueryModel();
        educationUDFI = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
        educationQM.addWhere(educationUDFI, "deleted", Boolean.FALSE);
        ID userId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            userId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        educationQM.addWhereAnd(educationUDFI, "user", ECriteria.EQUAL, userId);
        content.addComponent(educationTW);
        content.setComponentAlignment(educationTW, Alignment.TOP_CENTER);

        /* Languages */
        languageTW = new TableWidget(V_USER_LANGUAGE.class);
        languageTW.addEntityListener(this);
        DBTableModel languageTM = (DBTableModel) languageTW.getWidgetModel();
        languageTM.setReadOnly(readOnly);

        QueryModel languageQM = languageTM.getQueryModel();
        languageQM.addWhere("user", ECriteria.EQUAL, userId);

        FormModel languageFM = languageTM.getFormModel();
        FKFieldModel languageFM1 = (FKFieldModel) languageFM.getFieldModel("language");
        QueryModel languageQM1 = languageFM1.getQueryModel();
        languageQM1.addOrder("langName");

        FKFieldModel languageLevelFM = (FKFieldModel) languageFM.getFieldModel("languageLevel");
        QueryModel languageLevelQM = languageLevelFM.getQueryModel();
        languageLevelQM.addOrder("levelName");

        content.addComponent(languageTW);
        content.setComponentAlignment(languageTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("education"));
    }

    private void createMedicalCheckupTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Medical checkup */
        medicalCheckupTW = new TableWidget(V_MEDICAL_CHECKUP.class);
        medicalCheckupTW.addEntityListener(this);
        DBTableModel medicalCheckupTM = (DBTableModel) medicalCheckupTW.getWidgetModel();
        medicalCheckupTM.setReadOnly(readOnly);
        medicalCheckupTM.getColumnModel("allowStudy").setInTable(false);
        QueryModel medicalCheckupQM = medicalCheckupTM.getQueryModel();
        medicalCheckupQM.addWhere("deleted", Boolean.FALSE);
        ID userId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            userId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        medicalCheckupQM.addWhereAnd("user", ECriteria.EQUAL, userId);

        FormModel medicalCheckupFM = medicalCheckupTM.getFormModel();
        medicalCheckupFM.getFieldModel("allowStudy").setInEdit(false);
        medicalCheckupFM.getFieldModel("allowStudy").setInView(false);

        content.addComponent(medicalCheckupTW);
        content.setComponentAlignment(medicalCheckupTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("medical.checkup"));
    }

    private void createAddressesTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        GridLayout formsGL = new GridLayout(2, 1);
        formsGL.setSizeFull();
        formsGL.setSpacing(true);

        /* Address registration */
        StringBuilder sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("address.registration"));
        addressRegFW = new CommonFormWidget(USER_ADDRESS.class);
        addressRegFW.addEntityListener(this);
        final FormModel addressRegFM = addressRegFW.getWidgetModel();
        addressRegFM.setReadOnly(readOnly);
        addressRegFM.setTitleResource("address.registration");
        addressRegFM.setErrorMessageTitle(sb.toString());
        addressRegFM.setButtonsVisible(false);

        FKFieldModel countryRegFM = (FKFieldModel) addressRegFM.getFieldModel("country");
        QueryModel countryRegQM = countryRegFM.getQueryModel();
        countryRegQM.addWhereNull("parent");
        countryRegQM.addOrder("countryName");

        FKFieldModel regionRegFM = (FKFieldModel) addressRegFM.getFieldModel("region");
        QueryModel regionRegQM = regionRegFM.getQueryModel();
        regionRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        regionRegQM.addOrder("countryName");

        FKFieldModel cityRegFM = (FKFieldModel) addressRegFM.getFieldModel("city");
        QueryModel cityRegQM = cityRegFM.getQueryModel();
        cityRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        cityRegQM.addOrder("countryName");

        FKFieldModel villageRegFM = (FKFieldModel) addressRegFM.getFieldModel("village");
        QueryModel villageRegQM = villageRegFM.getQueryModel();
        villageRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        villageRegQM.addOrder("countryName");

        COUNTRY regionReg = null;
        COUNTRY cityReg = null;
        COUNTRY villageReg = null;
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            addressRegFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressRegQM = new QueryModel<USER_ADDRESS>(USER_ADDRESS.class);
            addressRegQM.addWhere("user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            addressRegQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(1));
            try {
                USER_ADDRESS addressReg = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressRegQM);
                if (addressReg != null) {
                    addressRegFM.loadEntity(addressReg.getId());
                    addressReg = (USER_ADDRESS) addressRegFM.getEntity();
                    regionReg = addressReg.getRegion();
                    cityReg = addressReg.getCity();
                    villageReg = addressReg.getVillage();
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    addressRegFM.loadEntity(ID.valueOf(-1));
                } else {
                    addressRegFM.createNew();
                }
            }
        }

        countryRegFM.getListeners().add(new CountryChangeListener(regionReg, regionRegFM));
        regionRegFM.getListeners().add(new RegionChangeListener(cityReg, cityRegFM));
        cityRegFM.getListeners().add(new CityChangeListener(villageReg, villageRegFM));

        formsGL.addComponent(addressRegFW);

        /* Address residential */
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("address.residential"));
        addressFactFW = new CommonFormWidget(USER_ADDRESS.class);
        addressFactFW.addEntityListener(this);
        final FormModel addressFactFM = addressFactFW.getWidgetModel();
        addressFactFM.setReadOnly(readOnly);
        addressFactFM.setTitleResource("address.residential");
        addressFactFM.setErrorMessageTitle(sb.toString());
        addressFactFM.setButtonsVisible(false);

        FKFieldModel countryFactFM = (FKFieldModel) addressFactFM.getFieldModel("country");
        QueryModel countryFactQM = countryFactFM.getQueryModel();
        countryFactQM.addWhereNull("parent");
        countryFactQM.addOrder("countryName");

        FKFieldModel regionFactFM = (FKFieldModel) addressFactFM.getFieldModel("region");
        QueryModel regionFactQM = regionFactFM.getQueryModel();
        regionFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        regionFactQM.addOrder("countryName");

        FKFieldModel cityFactFM = (FKFieldModel) addressFactFM.getFieldModel("city");
        QueryModel cityFactQM = cityFactFM.getQueryModel();
        cityFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        cityFactQM.addOrder("countryName");

        FKFieldModel villageFactFM = (FKFieldModel) addressFactFM.getFieldModel("village");
        QueryModel villageFactQM = villageFactFM.getQueryModel();
        villageFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        villageFactQM.addOrder("countryName");

        COUNTRY regionFact = null;
        COUNTRY cityFact = null;
        COUNTRY villageFact = null;
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            addressFactFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressFactQM = new QueryModel<USER_ADDRESS>(USER_ADDRESS.class);
            addressFactQM.addWhere("user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            addressFactQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(2));
            try {
                USER_ADDRESS addressFact = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressFactQM);
                if (addressFact != null) {
                    addressFactFM.loadEntity(addressFact.getId());
                    addressFact = (USER_ADDRESS) addressFactFM.getEntity();
                    regionFact = addressFact.getRegion();
                    cityFact = addressFact.getCity();
                    villageFact = addressFact.getVillage();
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    addressFactFM.loadEntity(ID.valueOf(-1));
                } else {
                    addressFactFM.createNew();
                }
            }
        }

        countryFactFM.getListeners().add(new CountryChangeListener(regionFact, regionFactFM));
        regionFactFM.getListeners().add(new RegionChangeListener(cityFact, cityFactFM));
        cityFactFM.getListeners().add(new CityChangeListener(villageFact, villageFactFM));

        formsGL.addComponent(addressFactFW);

        content.addComponent(formsGL);
        content.setComponentAlignment(formsGL, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (addressRegFM.isModified()) {
                        addressRegFW.save();
                    }

                    if (addressFactFM.isModified()) {
                        addressFactFW.save();
                    }
                }
            });
            buttonPanel.addComponent(save);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    addressRegFW.cancel();
                    addressFactFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("addresses"));
    }

    private void createScientificDegreeTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Scientific degree */
        scientificDegreeTW = new TableWidget(V_EMPLOYEE_DEGREE.class);
        scientificDegreeTW.addEntityListener(this);
        DBTableModel scientificDegreeTM = (DBTableModel) scientificDegreeTW.getWidgetModel();
        scientificDegreeTM.setReadOnly(readOnly);
        QueryModel scientificDegreeQM = scientificDegreeTM.getQueryModel();
        scientificDegreeQM.addWhere("deleted", Boolean.FALSE);
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        scientificDegreeQM.addWhereAnd("employee", ECriteria.EQUAL, employeeId);

        content.addComponent(scientificDegreeTW);
        content.setComponentAlignment(scientificDegreeTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("scientific.degrees"));
    }

    private void createScienceTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        /* Publications */
        publicationTW = new TableWidget(V_PUBLICATION.class);
        publicationTW.addEntityListener(this);
        DBTableModel publicationTM = (DBTableModel) publicationTW.getWidgetModel();
        publicationTM.setReadOnly(readOnly);
        QueryModel publicationQM = publicationTM.getQueryModel();
        publicationQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        FormModel publicationFM = ((DBTableModel) publicationTW.getWidgetModel()).getFormModel();

        content.addComponent(publicationTW);
        content.setComponentAlignment(publicationTW, Alignment.TOP_CENTER);

        /* Scientific activity */
        scientificActivityTW = new TableWidget(V_SCIENTIFIC_ACTIVITY.class);
        scientificActivityTW.addEntityListener(this);
        DBTableModel scientificActivityTM = (DBTableModel) scientificActivityTW.getWidgetModel();
        scientificActivityTM.setReadOnly(readOnly);
        QueryModel scientificActivityQM = scientificActivityTM.getQueryModel();
        scientificActivityQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        FormModel scientificActivityFM = scientificActivityTM.getFormModel();

        content.addComponent(scientificActivityTW);
        content.setComponentAlignment(scientificActivityTW, Alignment.MIDDLE_CENTER);

        /* Scientific management */
        scientificManagementTW = new TableWidget(V_SCIENTIFIC_MANAGEMENT.class);
        scientificManagementTW.addEntityListener(this);
        DBTableModel scientificManagementTM = (DBTableModel) scientificManagementTW.getWidgetModel();
        scientificManagementTM.setReadOnly(readOnly);
        QueryModel scientificManagementQM = scientificManagementTM.getQueryModel();
        scientificManagementQM.addWhere("employee", ECriteria.EQUAL, employeeId);
        content.addComponent(scientificManagementTW);
        content.setComponentAlignment(scientificManagementTW, Alignment.BOTTOM_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("science"));
    }

    private void createExperienceTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        /* Experiences */
        experienceTW = new TableWidget(VPreviousExperience.class);
        experienceTW.addEntityListener(this);

        DBTableModel experienceTM = (DBTableModel) experienceTW.getWidgetModel();
        experienceTM.setCrudEntityClass(PREVIOUS_EXPERIENCE.class);
        experienceTM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        experienceL = new Label();
        experienceL.setCaptionAsHtml(true);
        experienceL.setWidth(800, Unit.PIXELS);

        updateExperienceL();

        content.addComponent(experienceTW);
        content.setComponentAlignment(experienceTW, Alignment.MIDDLE_CENTER);
        content.addComponent(experienceL);
        content.setComponentAlignment(experienceL, Alignment.TOP_RIGHT);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("experience"));
    }

    private void updateExperienceL() {
        try{
            if(getSum()!=null&&getList()!=null) {
            experienceL.setCaption("<html><b>" +
                    getUILocaleUtil().getCaption("experienceL") + "</b>" + " " +
                    String.valueOf((getSum().getYears())) +" "+ getUILocaleUtil().getCaption("experienceL.year")
                    + " " + String.valueOf(getSum().getMonths()) +" "+ getUILocaleUtil().getCaption("experienceL.month")
                    + " " + String.valueOf(getSum().getDays()) +" "+ getUILocaleUtil().getCaption("experienceL.day")
            );
            }else{
                experienceL.setCaption("<html><b>" +
                        getUILocaleUtil().getCaption("experienceL") + "</b>");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED || ev.getAction() == EntityEvent.MERGED
                || ev.getAction() == EntityEvent.REMOVED) {
            refresh();
            refreshChild();

            if(ev.getSource().equals(experienceTW)){
                updateExperienceL();
            }
        }


    }

    private void refresh() {
        try {
            List<VPreviousExperience> previousExperiences = getList();
            ((DBTableModel) experienceTW.getWidgetModel()).setEntities(previousExperiences);

            experienceTW.refresh();

        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh experience grid", ex);
        }
    }

    private void refreshChild() {
        try {
            List<VChild> child = getChildList();
            ((DBTableModel) childTW.getWidgetModel()).setEntities(child);

            childTW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh experience grid", ex);
        }
    }


    public PGInterval getSum() throws Exception {
        PGInterval sum = null;
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        Map<Integer, Object> params = new HashMap<>();
        String sql = "select justify_days( sum(age(case when dismiss_date isnull then now() when dismiss_date NOTNULL THEN dismiss_date END ,hire_date) ) )\n" +
                "from previous_experience\n" +
                "where employee_id =" + employeeId;

        try {
            Object o = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
            sum = (PGInterval) o;
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load sum", ex);
        }
        return sum;
    }

    public List<VPreviousExperience> getList() throws Exception {
        PGInterval sum = null;
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        List<VPreviousExperience> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "select " +
                "  id," +
                "  organization_name,\n" +
                "  post_name,\n" +
                "  hire_date,\n" +
                "  dismiss_date,\n" +
                "  age(case when dismiss_date isnull then now() when dismiss_date NOTNULL THEN dismiss_date END ,hire_date) \n" +
                "from previous_experience" +
                " where employee_id = " + employeeId;

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VPreviousExperience vPreviousExperience = new VPreviousExperience();
                    vPreviousExperience.setId(ID.valueOf((long) oo[0]));
                    vPreviousExperience.setOrganizationName((String) oo[1]);
                    vPreviousExperience.setPostName((String) oo[2]);
                    vPreviousExperience.setHireDate((Date) oo[3]);
                    vPreviousExperience.setDismissDate((Date) oo[4]);
                    sum = (PGInterval) oo[5];
                    if (sum != null) {
                        vPreviousExperience.setWorkPeriod(String.valueOf(sum.getYears())
                                + " " + getUILocaleUtil().getCaption("experienceL.year") + " "
                                + String.valueOf(sum.getMonths()) + " " + getUILocaleUtil().getCaption("experienceL.month"));
                        list.add(vPreviousExperience);
                    }

                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load experience list", ex);
        }
        refreshExperience(list);
        return list;
    }

    private void refreshExperience(List<VPreviousExperience> list) {
        ((DBTableModel) experienceTW.getWidgetModel()).setEntities(list);
        try {
            experienceTW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh experience list", ex);
        }
    }

    private void createCareerTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        if (careerTW != null) {
            getTabSheet().removeComponent(careerTW);
        }
        careerTW = new TableWidget(V_EMPLOYEE_DEPT.class);
        careerTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        careerTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        careerTW.addEntityListener(this);
        DBTableModel careerTM = (DBTableModel) careerTW.getWidgetModel();
        careerTM.setReadOnly(readOnly);
        careerTM.setCrudEntityClass(EMPLOYEE_DEPT.class);
        QueryModel careerQM = careerTM.getQueryModel();
        ID employeeId = ID.valueOf(-1);


        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            EMPLOYEE emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            if (emp.getStatus().getId().equals(ID.valueOf(5))) {
                careerTM.getColumnModel("liveLoad").setInTable(false);
                careerTM.getColumnModel("wageRate").setInTable(false);
                careerTM.getColumnModel("rateLoad").setInTable(false);
                careerTM.getColumnModel("hourCount").setInTable(true);
            } else {
                careerTM.getColumnModel("liveLoad").setInTable(true);
                careerTM.getColumnModel("wageRate").setInTable(true);
                careerTM.getColumnModel("rateLoad").setInTable(true);
                careerTM.getColumnModel("hourCount").setInTable(false);
            }
            employeeId = emp.getId();
        }
        careerQM.addWhere("employee", ECriteria.EQUAL, employeeId);

        FormModel careerFM = careerTM.getFormModel();

        FKFieldModel departmentFM = (FKFieldModel) careerFM.getFieldModel("department");
        departmentFM.setDialogWidth(400);
        departmentFM.setDialogHeight(400);
        QueryModel departmentQM = departmentFM.getQueryModel();
        departmentQM.addWhere("deleted", Boolean.FALSE);
        departmentQM.addWhereNotNull("parent");

        FKFieldModel postFM = (FKFieldModel) careerFM.getFieldModel("post");

        FieldModel liveLoadFM = careerFM.getFieldModel("liveLoad");
        FieldModel wageRateFM = careerFM.getFieldModel("wageRate");
        FieldModel rateLoadFM = careerFM.getFieldModel("rateLoad");

        postFM.getListeners().add(new CareerPostChangeListener(liveLoadFM, wageRateFM, rateLoadFM));
        liveLoadFM.getListeners().add(new LiveLoadChangeListener(rateLoadFM, wageRateFM));
        wageRateFM.getListeners().add(new WageRateChangeListener(liveLoadFM, rateLoadFM));


        content.addComponent(careerTW);
        content.setComponentAlignment(careerTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(careerTW, getUILocaleUtil().getCaption("career"), null, 8);
    }

    private void createWorkDayTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Work days */
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        QueryModel<EMPLOYEE_WORK_HOUR> ewhQM = new QueryModel<>(EMPLOYEE_WORK_HOUR.class);
        ewhQM.addWhere("employee", ECriteria.EQUAL, employeeId);
        ewhQM.addWhere("dayHour", ECriteria.LESS_EQUAL, ID.valueOf(14));
        List<EMPLOYEE_WORK_HOUR> tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ewhQM);
        List<EMPLOYEE_WORK_HOUR> ewhList = new ArrayList<>();
        for (EMPLOYEE_WORK_HOUR ewh : tempList) {
            EMPLOYEE_WORK_HOUR ewh1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_WORK_HOUR.class, ewh.getId());
            ewhList.add(ewh1);
        }
        whw = new WorkHourWidget(ewhList, readOnly, baseDataFW.getWidgetModel());
        whw.setCaption(getUILocaleUtil().getCaption("work.days.setting"));
        whw.setLegend1Resource("working");
        whw.setLegend2Resource("not.working");
        content.addComponent(whw);
        content.setComponentAlignment(whw, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (baseDataFW.getWidgetModel().isCreateNew()) {
                        //������� ��������� ������
                        Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                        return;
                    }
                    List<? extends AbstractWorkHourEntity> awheList = whw.getWorkHourList();
                    boolean saved = false;
                    for (AbstractWorkHourEntity awhe : awheList) {
                        if (awhe.isChanged()) {
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(awhe);
                                saved = true;
                                awhe.setChanged(false);
                            } catch (Exception ex) {
                                CommonUtils.showMessageAndWriteLog("Unable to change work day hour", ex);
                                break;
                            }
                        }
                    }

                    if (saved) {
                        showSavedNotification();
                    }
                }
            });
            buttonPanel.addComponent(save);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    whw.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("days.and.time.of.class"));
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
        if (source.equals(graduateStudentLoadGW)) {
            ID employeeId = ID.valueOf(-1);
            if (!baseDataFW.getWidgetModel().isCreateNew()) {
                try {
                    employeeId = baseDataFW.getWidgetModel().getEntity().getId();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
            String sql = "select gr_st_load.ID, lvl.LEVEL_NAME, gr_st_load.STUDENT_COUNT, gr_st_load.LEVEL_ID " +
                    "from GRADUATE_STUDENT_LOAD gr_st_load " +
                    "inner join LEVEL lvl on gr_st_load.LEVEL_ID = lvl.ID " +
                    "where gr_st_load.EMPLOYEE_ID = ?1 " +
                    "order by gr_st_load.LEVEL_ID";
            Map<Integer, Object> params = new HashMap<Integer, Object>(1);
            params.put(1, employeeId.getId());

            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                List<VGraduateStudentLoad> entities = new ArrayList<VGraduateStudentLoad>(tmpList.size());
                double b = .0;
                double m = .0;
                double d = .0;
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGraduateStudentLoad vgsl = new VGraduateStudentLoad();
                    vgsl.setId(ID.valueOf((long) oo[0]));
                    vgsl.setLevelName((String) oo[1]);
                    vgsl.setStudentCount(((BigDecimal) oo[2]).intValue());
                    entities.add(vgsl);

                    long levelId = (long) oo[3];
                    if (levelId == 1) {
                        b = .25 * vgsl.getStudentCount();
                    } else if (levelId == 2) {
                        m = .5 * vgsl.getStudentCount();
                    } else {
                        d = 1 * vgsl.getStudentCount();
                    }
                }
                ((DBSelectModel) graduateStudentLoadGW.getWidgetModel()).setEntities(entities);
                graduateStudentLoadCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), b + m + d));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh graduate student loads", ex);
            }
        }
    }

    @Override
    public void handlePhotoWidgetEvent(PhotoWidgetEvent ev) {
        if (ev.getEvent() == PhotoWidgetEvent.CHANGED) {
            userPhotoBytes = ev.getBytes();
            userPhotoFilename = ev.getFilename();
            userPhotoChanged = true;
        }
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(educationTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel educationFM = ((DBTableModel) educationTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");
            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));


            FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
            educationFLFM.permitMimeType(FileListFieldModel.JPEG);

            educationFM.getFieldModel("specialityName").setRequired(true);
            educationFM.getFieldModel("qualification").setRequired(true);
            educationFM.getFieldModel("entryYear").setRequired(true);

            educationFLFM.getFileList().clear();
            educationFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(languageTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(subjectPPSTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return true;
        } else if (source.equals(graduateStudentLoadGW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            String sql = "select LEVEL_ID from GRADUATE_STUDENT_LOAD where EMPLOYEE_ID = ?1";
            Map<Integer, Object> params = new HashMap<>(1);
            try {
                params.put(1, baseDataFW.getWidgetModel().getEntity().getId().getId());
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }

            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                List<ID> levelIDs = new ArrayList<ID>(tmpList.size());

                for (Object o : tmpList) {
                    levelIDs.add(ID.valueOf((long) o));
                }

                FormModel graduateStudentLoadFM = ((DBSelectModel) graduateStudentLoadGW.getWidgetModel()).getFormModel();
                FKFieldModel levelFM = (FKFieldModel) graduateStudentLoadFM.getFieldModel("level");
                levelFM.setReadOnlyFixed(false);
                levelFM.getQueryModel().addWhereNotIn("id", levelIDs);

                return true;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to get level id", ex);
            }
        } else if (source.equals(medicalCheckupTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
            medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

            medicalCheckupFLFM.getFileList().clear();
            medicalCheckupFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(scientificDegreeTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel scientificDegreeFM = ((DBTableModel) scientificDegreeTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificDegreeFLFM = (FileListFieldModel) scientificDegreeFM.getFieldModel("fileList");
            scientificDegreeFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificDegreeFLFM.getFileList().clear();
            scientificDegreeFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(publicationTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(scientificActivityTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(scientificManagementTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel scientificManagementFM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificManagementFLFM = (FileListFieldModel) scientificManagementFM.getFieldModel("fileList");
            scientificManagementFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificManagementFLFM.getFileList().clear();
            scientificManagementFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(experienceTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        } else if (source.equals(careerTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
            FormModel careerFM = ((DBSelectModel) careerTW.getWidgetModel()).getFormModel();
            FieldModel liveLoadFM = careerFM.getFieldModel("liveLoad");
            FieldModel wageRateFM = careerFM.getFieldModel("wageRate");
            FieldModel rateLoadFM = careerFM.getFieldModel("rateLoad");
            FieldModel hourCountFM = careerFM.getFieldModel("hourCount");
            if (emp.getStatus().getId().equals(ID.valueOf(5))) {
                liveLoadFM.setInEdit(false);
                wageRateFM.setInEdit(false);
                rateLoadFM.setInEdit(false);
                hourCountFM.setInEdit(true);

            } else {
                liveLoadFM.setInEdit(true);
                wageRateFM.setInEdit(true);
                rateLoadFM.setInEdit(true);
                hourCountFM.setInEdit(false);
            }

            return true;
        } else if (source.equals(roomTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            try {
                QueryModel<CORPUS> corpusQM = new QueryModel<CORPUS>(CORPUS.class);
                BeanItemContainer<CORPUS> corpusBIC = new BeanItemContainer<CORPUS>(CORPUS.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(corpusQM));
                ComboBox corpusCB = new ComboBox();
                corpusCB.setContainerDataSource(corpusBIC);
                corpusCB.setImmediate(true);
                corpusCB.setNullSelectionAllowed(true);
                corpusCB.setTextInputAllowed(false);
                corpusCB.setFilteringMode(FilteringMode.OFF);

                TextField roomNoTF = new TextField();
                roomNoTF.setWidth(100, Unit.PIXELS);
                roomNoTF.setNullRepresentation("");
                roomNoTF.setNullSettingAllowed(true);
                roomNoTF.setImmediate(true);

                QueryModel<ROOM_TYPE> roomTypeQM = new QueryModel<ROOM_TYPE>(ROOM_TYPE.class);
                roomTypeQM.addWhere("id", ECriteria.LESS, 5);
                BeanItemContainer<ROOM_TYPE> roomTypeBIC = new BeanItemContainer<ROOM_TYPE>(ROOM_TYPE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomTypeQM));
                ComboBox roomTypeCB = new ComboBox();
                roomTypeCB.setContainerDataSource(roomTypeBIC);
                roomTypeCB.setImmediate(true);
                roomTypeCB.setNullSelectionAllowed(true);
                roomTypeCB.setTextInputAllowed(false);
                roomTypeCB.setFilteringMode(FilteringMode.OFF);

                IntegerField capacityIF = new IntegerField();
                capacityIF.setWidth(60, Unit.PIXELS);
                capacityIF.setNullRepresentation("");
                capacityIF.setNullSettingAllowed(true);
                capacityIF.setImmediate(true);

                roomSelectDlg = new CustomGridSelectDialog(new AddNewRoomListener(), V_ROOM.class);
                QueryModel qm = ((DBGridModel) roomSelectDlg.getSelectModel()).getQueryModel();
                roomSelectDlg.setDialogWidth(600);
                roomSelectDlg.setDialogHeight(400);
                roomSelectDlg.getFilterModel().addFilter("corpus", corpusCB);
                roomSelectDlg.getFilterModel().addFilter("roomNo", roomNoTF);
                roomSelectDlg.getFilterModel().addFilter("roomType", roomTypeCB);
                roomSelectDlg.getFilterModel().addFilter("capacity", capacityIF);
                roomSelectDlg.setFilterRequired(true);
                roomSelectDlg.initFilter();
                roomSelectDlg.open();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to add room for teacher", ex);
            }

            return false;
        } else if (source.equals(employeeSkillTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        } else if (source.equals(employeeAwardTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        } else if (source.equals(employeeQualificationTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        } else if (source.equals(childTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        } else if (source.equals(masterTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
        }

        return super.preCreate(source, buttonId);
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        if (source.equals(careerTW)) {
            EMPLOYEE_DEPT ed = (EMPLOYEE_DEPT) e;
            try {
                ed.setEmployeeType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(EMPLOYEE_TYPE.class, ID.valueOf(2)));//TODO
                ed.setLiveLoad(0);
                ed.setWageRate(1.0);
                ed.setRateLoad(0.0);
                ed.setHourCount(0.0);
                LOG.info(ed.getHourCount() + "");
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to set employee type", ex);
            }
        } else if (source.equals(employeeSkillTW)) {
            EMPLOYEE_SKILL employeeSkill = (EMPLOYEE_SKILL) e;
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            employeeSkill.setEmployee(emp);
        } else if (source.equals(employeeAwardTW)) {
            EMPLOYEE_AWARD employeeAward = (EMPLOYEE_AWARD) e;
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            employeeAward.setEmployee(emp);
        } else if (source.equals(employeeQualificationTW)) {
            EMPLOYEE_QUALIFICATION employeeQualification = (EMPLOYEE_QUALIFICATION) e;
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            employeeQualification.setEmployee(emp);
            employeeQualification.setCreated(new Date());
        } else if (source.equals(childTW)) {
            CHILD child = (CHILD) e;
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            child.setEmployee(emp);
        } else if (source.equals(masterTW)) {
            MASTER master = (MASTER) e;
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            master.setEmployee(emp);
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("deleted", Boolean.FALSE);

        if (source.equals(educationTW)) {
            FormModel educationFM = ((DBTableModel) educationTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");

            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));

            FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
            educationFLFM.permitMimeType(FileListFieldModel.JPEG);

            educationFM.getFieldModel("specialityName").setRequired(true);
            educationFM.getFieldModel("qualification").setRequired(true);
            educationFM.getFieldModel("entryYear").setRequired(true);

            educationFLFM.getFileList().clear();
            educationFLFM.getDeleteList().clear();

            try {
                udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        educationFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load education document copies", ex);
            }

            return true;
        } else if (source.equals(medicalCheckupTW)) {
            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
            medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

            medicalCheckupFLFM.getFileList().clear();
            medicalCheckupFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());
            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        medicalCheckupFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load medical checkup copies", ex);
            }

            return true;
        } else if (source.equals(scientificDegreeTW)) {
            FormModel scientificDegreeFM = ((DBTableModel) scientificDegreeTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificDegreeFLFM = (FileListFieldModel) scientificDegreeFM.getFieldModel("fileList");
            scientificDegreeFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificDegreeFLFM.getFileList().clear();
            scientificDegreeFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());
            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        scientificDegreeFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load scientific degree copies", ex);
            }

            return true;
        } else if (source.equals(scientificManagementTW)) {
            FormModel scientificManagementFM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificManagementFLFM = (FileListFieldModel) scientificManagementFM.getFieldModel("fileList");
            scientificManagementFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificManagementFLFM.getFileList().clear();
            scientificManagementFLFM.getDeleteList().clear();

            QueryModel<SCIENTIFIC_MANAGEMENT_FILE> smfQM = new QueryModel<SCIENTIFIC_MANAGEMENT_FILE>(SCIENTIFIC_MANAGEMENT_FILE.class);
            smfQM.addSelect("id");
            smfQM.addSelect("fileName");
            smfQM.addWhere("scientificManagement", ECriteria.EQUAL, e.getId());

            try {
                List smfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(smfQM);
                if (!smfList.isEmpty()) {
                    for (Object o : smfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(SCIENTIFIC_MANAGEMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        scientificManagementFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load scientific management file copies", ex);
            }

            return true;
        } else if (source.equals(graduateStudentLoadGW)) {
            FormModel graduateStudentLoadFM = ((DBSelectModel) graduateStudentLoadGW.getWidgetModel()).getFormModel();
            FKFieldModel levelFM = (FKFieldModel) graduateStudentLoadFM.getFieldModel("level");
            levelFM.setReadOnlyFixed(true);

            return true;
        } else if (source.equals(careerTW)) {
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();//TODO catch
            }
            FormModel careerFM = ((DBSelectModel) careerTW.getWidgetModel()).getFormModel();
            FieldModel liveLoadFM = careerFM.getFieldModel("liveLoad");
            FieldModel wageRateFM = careerFM.getFieldModel("wageRate");
            FieldModel rateLoadFM = careerFM.getFieldModel("rateLoad");
            FieldModel hourCountFM = careerFM.getFieldModel("hourCount");
            if (emp.getStatus().getId().equals(ID.valueOf(5))) {
                liveLoadFM.setInEdit(false);
                wageRateFM.setInEdit(false);
                rateLoadFM.setInEdit(false);
                hourCountFM.setInEdit(true);
            } else {
                liveLoadFM.setInEdit(true);
                wageRateFM.setInEdit(true);
                rateLoadFM.setInEdit(true);
                hourCountFM.setInEdit(false);
            }

            return true;
        } else if (source.equals(employeeQualificationTW)) {
            EMPLOYEE_QUALIFICATION employeeQualification = (EMPLOYEE_QUALIFICATION) e;
            employeeQualification.setUpdated(new Date());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(employeeQualification);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        } else if (source.equals(experienceTW)) {
            EMPLOYEE emp = null;
            try {
                emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
            } catch (Exception ex) {
                ex.printStackTrace();//TODO catch
            }
            FormModel careerFM = ((DBSelectModel) experienceTW.getWidgetModel()).getFormModel();
            return true;
        }

        return super.onEdit(source, e, buttonId);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<USER_DOCUMENT_FILE>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("deleted", Boolean.FALSE);

        if (source.equals(subjectPPSTW)) {
            FormModel subjectPPSFM = ((DBTableModel) subjectPPSTW.getWidgetModel()).getFormModel();
            return true;
        } else if (source.equals(educationTW)) {
            FormModel educationFM = ((DBTableModel) educationTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");

            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));

            FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
            educationFLFM.permitMimeType(FileListFieldModel.JPEG);

            educationFLFM.getFileList().clear();
            educationFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());

            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        educationFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load education document copies", ex);
            }

            return true;
        } else if (source.equals(medicalCheckupTW)) {
            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
            medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

            medicalCheckupFLFM.getFileList().clear();
            medicalCheckupFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());

            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        medicalCheckupFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load education document copies", ex);
            }

            return true;
        } else if (source.equals(scientificDegreeTW)) {
            FormModel scientificDegreeFM = ((DBTableModel) scientificDegreeTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificDegreeFLFM = (FileListFieldModel) scientificDegreeFM.getFieldModel("fileList");
            scientificDegreeFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificDegreeFLFM.getFileList().clear();
            scientificDegreeFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());
            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        scientificDegreeFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load medical checkup copies", ex);
            }

            return true;
        } else if (source.equals(scientificManagementTW)) {
            FormModel scientificManagementFM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificManagementFLFM = (FileListFieldModel) scientificManagementFM.getFieldModel("fileList");
            scientificManagementFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificManagementFLFM.getFileList().clear();
            scientificManagementFLFM.getDeleteList().clear();

            QueryModel<SCIENTIFIC_MANAGEMENT_FILE> smfQM = new QueryModel<SCIENTIFIC_MANAGEMENT_FILE>(SCIENTIFIC_MANAGEMENT_FILE.class);
            smfQM.addSelect("id");
            smfQM.addSelect("fileName");
            smfQM.addWhere("scientificManagement", ECriteria.EQUAL, e.getId());

            try {
                List smfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(smfQM);
                if (!smfList.isEmpty()) {
                    for (Object o : smfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(SCIENTIFIC_MANAGEMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        scientificManagementFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load scientific management file copies", ex);
            }

            return true;
        }
        return super.onPreview(source, e, buttonId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        if (source.equals(baseDataFW)) {
            return preSaveBaseData(source, e, isNew, buttonId);
        } else if (source.equals(userPassportFW)) {
            return preSavePassport(source, e, isNew, buttonId);
        } else if (source.equals(militaryDocFW)) {
            return preSaveMilitaryDoc(source, e, isNew, buttonId);
        } else if (source.equals(disabilityDocFW)) {
            return preSaveDisabilityDoc(source, e, isNew, buttonId);
        } else if (source.equals(repatriateDocFW)) {
            return preSaveRepatriateDoc(source, e, isNew, buttonId);
        } else if (source.equals(educationTW)) {
            return preSaveEducationDoc(source, e, isNew, buttonId);
        } else if (source.equals(languageTW)) {
            return preSaveUserLanguage(source, e, isNew, buttonId);
        } else if (source.equals(medicalCheckupTW)) {
            return preSaveMedicalCheckup(source, e, isNew, buttonId);
        } else if (source.equals(addressRegFW)) {
            return preSaveAddressReg(source, e, isNew, buttonId);
        } else if (source.equals(addressFactFW)) {
            return preSaveAddressFact(source, e, isNew, buttonId);
        } else if (source.equals(scientificDegreeTW)) {
            return preSaveScientificDegree(source, e, isNew, buttonId);
        } else if (source.equals(publicationTW)) {
            return preSavePublication(source, e, isNew, buttonId);
        } else if (source.equals(scientificActivityTW)) {
            return preSaveScientificActivity(source, e, isNew, buttonId);
        } else if (source.equals(scientificManagementTW)) {
            return preSaveScientificManagement(source, e, isNew, buttonId);
        } else if (source.equals(experienceTW)) {
            return preSaveExperience(source, e, isNew, buttonId);
        } else if (source.equals(careerTW)) {
            return preSaveCareer(source, e, isNew, buttonId);
        } else if (source.equals(subjectPPSTW)) {
            return preSaveSubjectPPS(source, e, isNew, buttonId);
        } else if (source.equals(loadByHourTW)) {
            return preSaveLoadByHour(source, e, isNew, buttonId);
        } else if (source.equals(graduateStudentLoadGW)) {
            return preSaveGraduateStudentLoad(source, e, isNew, buttonId);
        } else if (source.equals(masterTW)) {
            return preSaveMaster(source, e, isNew, buttonId);
        }

        return super.preSave(source, e, isNew, buttonId);
    }

    private boolean preSaveBaseData(Object source, Entity e, boolean isNew, int buttonId) {
        EMPLOYEE emp = (EMPLOYEE) e;
        if (isNew) {
            emp.setCreated(new Date());
            emp.setCreatedBy(CommonUtils.getCurrentUserLogin());
            try {
                Calendar calendar = Calendar.getInstance();
                QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
                entranceYearQM.addWhere("beginYear", ECriteria.EQUAL, calendar.get(Calendar.YEAR));
                ENTRANCE_YEAR entranceYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(entranceYearQM);
                Integer beginYear = entranceYear.getBeginYear();
                emp.setCode(CommonUtils.getCode(beginYear.toString().substring(2, 4)));
                emp.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS"));
                emp.setFirstName(emp.getFirstName().trim());
                emp.setLastName(emp.getLastName().trim());
                if (emp.getMiddleName() != null) {
                    emp.setMiddleName(emp.getMiddleName().trim());
                }
                emp.setFirstNameEN(emp.getFirstNameEN().trim());
                emp.setLastNameEN(emp.getLastNameEN().trim());
                if (emp.getMiddleNameEN() != null) {
                    emp.setMiddleNameEN(emp.getMiddleNameEN().trim());
                }
                emp.setLogin(CommonUtils.getLogin(emp.getFirstNameEN().toLowerCase().substring(0, 1) + "_" +
                        emp.getLastNameEN().toLowerCase()));
                emp.setPasswd("12345678");
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(emp);
                if (userPhotoChanged) {
                    userPhoto = new USER_PHOTO();
                    userPhoto.setUser(emp);
                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                }
                baseDataFW.getWidgetModel().loadEntity(emp.getId());
                baseDataFW.refresh();

                saveEmployeeWorkHour(emp, true);
                createCareerTab(false);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a teacher", ex);
            }
        } else {
            emp.setUpdated(new Date());
            emp.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            emp.setFirstName(emp.getFirstName().trim());
            emp.setLastName(emp.getLastName().trim());
            if (emp.getMiddleName() != null) {
                emp.setMiddleName(emp.getMiddleName().trim());
            }
            emp.setFirstNameEN(emp.getFirstNameEN().trim());
            emp.setLastNameEN(emp.getLastNameEN().trim());
            if (emp.getMiddleNameEN() != null) {
                emp.setMiddleNameEN(emp.getMiddleNameEN().trim());
            }
            try {
                CARD oldCard = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        USERS.class, emp.getId()).getCard();
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(emp);
                if (oldCard != null) {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(oldCard);
                }
                if (userPhotoChanged) {
                    if (userPhoto == null) {
                        userPhoto = new USER_PHOTO();
                        userPhoto.setUser(emp);
                    }

                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);

                    if (userPhoto.getId() == null) {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                    } else {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(userPhoto);
                    }
                }
                saveEmployeeWorkHour(emp, true);
                createCareerTab(false);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a teacher", ex);
            }
        }

        return false;
    }

    public static void saveEmployeeWorkHour(EMPLOYEE employee, boolean isEdit) throws Exception {
        QueryModel<EMPLOYEE_WORK_HOUR> ewhQM = new QueryModel<EMPLOYEE_WORK_HOUR>(EMPLOYEE_WORK_HOUR.class);
        ewhQM.addSelect("employee", EAggregate.COUNT);
        ewhQM.addWhere("employee", ECriteria.EQUAL, employee.getId());
        ewhQM.addWhere("dayHour", ECriteria.LESS_EQUAL, ID.valueOf(14));

        Integer count = ((Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(ewhQM)).
                intValue();
        if (count <= 0) {
            QueryModel<WEEK_DAY> wdQM = new QueryModel<WEEK_DAY>(WEEK_DAY.class);
            List<WEEK_DAY> wdList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(wdQM);

            QueryModel<DAY_HOUR> dhQM = new QueryModel<DAY_HOUR>(DAY_HOUR.class);
            dhQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(14));

            List<DAY_HOUR> dhList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dhQM);

            WORK_HOUR_STATUS whs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(WORK_HOUR_STATUS.class, ID.valueOf(DOES_NOT_WORK));

            List<EMPLOYEE_WORK_HOUR> ewhList = new ArrayList<>();
            for (WEEK_DAY wd : wdList) {
                for (DAY_HOUR dh : dhList) {
                    EMPLOYEE_WORK_HOUR ewh = new EMPLOYEE_WORK_HOUR();
                    ewh.setEmployee(employee);
                    ewh.setWeekDay(wd);
                    ewh.setDayHour(dh);
                    ewh.setWorkHourStatus(whs);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ewh);
                    ewhList.add(ewh);
                }
            }
            if (isEdit) {
                whw.setWorkHourList(ewhList);
                whw.refresh();
            }
        }
    }

    private boolean preSavePassport(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_PASSPORT p = (USER_PASSPORT) e;

        if (p.getIssueDate() != null && p.getExpireDate() != null) {
            if (p.getExpireDate().before(p.getIssueDate()) || p.getExpireDate().equals(p.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(USER_PASSPORT.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(USER_PASSPORT.class, "expireDate")));
                return false;
            }
        }

        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                p.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                p.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(p);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a passport", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(p);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a passport", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) userPassportFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(p);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save passport copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete passport copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveMilitaryDoc(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        MILITARY_DOC md = (MILITARY_DOC) e;

        if (md.getIssueDate() != null && md.getExpireDate() != null) {
            if (md.getExpireDate().before(md.getIssueDate()) || md.getExpireDate().equals(md.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(MILITARY_DOC.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(MILITARY_DOC.class, "expireDate")));
                return false;
            }
        }

        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a military doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a military doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) militaryDocFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save military doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete military doc copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveDisabilityDoc(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        DISABILITY_DOC dd = (DISABILITY_DOC) e;

        if (dd.getIssueDate() != null && dd.getExpireDate() != null) {
            if (dd.getExpireDate().before(dd.getIssueDate()) || dd.getExpireDate().equals(dd.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(DISABILITY_DOC.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(DISABILITY_DOC.class, "expireDate")));
                return false;
            }
        }

        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                dd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                dd.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(dd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a disability doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a disability doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) disabilityDocFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(dd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save disability doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete disability doc copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveRepatriateDoc(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        REPATRIATE_DOC rd = (REPATRIATE_DOC) e;

        if (rd.getIssueDate() != null && rd.getExpireDate() != null) {
            if (rd.getExpireDate().before(rd.getIssueDate()) || rd.getExpireDate().equals(rd.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(REPATRIATE_DOC.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(REPATRIATE_DOC.class, "expireDate")));
                return false;
            }
        }

        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                rd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                rd.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(rd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a repatriate doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(rd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a repatriate doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) repatriateDocFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(rd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save repatriate doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete repatriate doc copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveEducationDoc(Object source, Entity e, boolean isNew, int buttonId) {
        EDUCATION_DOC ed = (EDUCATION_DOC) e;

        if (ed.getIssueDate() != null && ed.getExpireDate() != null) {
            if (ed.getExpireDate().before(ed.getIssueDate()) || ed.getExpireDate().equals(ed.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(EDUCATION_DOC.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(EDUCATION_DOC.class, "expireDate")));
                return false;
            }
        }

        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                ed.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                ed.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(ed);

                QueryModel educationQM = ((DBSelectModel) educationTW.getWidgetModel()).getQueryModel();
                educationQM.addWhere(educationUDFI, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());


                FKFieldModel qualificationFM = (FKFieldModel) fm.getFieldModel("qualification");
                qualificationFM.setRequired(true);

                FKFieldModel specialityNameFM = (FKFieldModel) fm.getFieldModel("specialityName");
                specialityNameFM.setRequired(true);

                FKFieldModel entryYearFM = (FKFieldModel) fm.getFieldModel("entryYear");
                entryYearFM.setRequired(true);

                educationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a education doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                educationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a education doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) educationTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(ed);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save education doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete repatriate doc copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveUserLanguage(Object source, Entity e, boolean isNew, int buttonId) {
        V_USER_LANGUAGE vul = (V_USER_LANGUAGE) e;
        if (isNew) {
            try {
                EMPLOYEE emp = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
                USER_LANGUAGE ul = new USER_LANGUAGE();
                ul.setUser(emp);
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ul);

                QueryModel languageQM = ((DBTableModel) languageTW.getWidgetModel()).getQueryModel();
                languageQM.addWhere("user", ECriteria.EQUAL, emp.getId());

                languageTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a language", ex);
            }
        } else {
            try {
                USER_LANGUAGE ul = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_LANGUAGE.class, vul.getId());
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ul);
                languageTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a language", ex);
            }
        }

        return false;
    }

    private boolean preSaveMedicalCheckup(Object source, Entity e, boolean isNew, int buttonId) {
        V_MEDICAL_CHECKUP vmc = (V_MEDICAL_CHECKUP) e;

        if (vmc.getIssueDate() != null && vmc.getExpireDate() != null) {
            if (vmc.getExpireDate().before(vmc.getIssueDate()) || vmc.getExpireDate().equals(vmc.getIssueDate())) {
                Message.showInfo(String.format(getUILocaleUtil().getMessage("date1.must.be.less.date2"), getUILocaleUtil().getEntityFieldLabel(V_MEDICAL_CHECKUP.class, "issueDate"), getUILocaleUtil().getEntityFieldLabel(V_MEDICAL_CHECKUP.class, "expireDate")));
                return false;
            }
        }

        MEDICAL_CHECKUP mc = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            mc = new MEDICAL_CHECKUP();
            try {
                mc.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                mc.setUser(emp);
                mc.setDocumentNo(vmc.getDocumentNo());
                mc.setIssueDate(vmc.getIssueDate());
                mc.setExpireDate(vmc.getExpireDate());
                mc.setCheckupType(vmc.getCheckupType());
                mc.setIssuerName(vmc.getIssuerName());
                mc.setAllowDorm(vmc.isAllowDorm());
                mc.setAllowWork(vmc.isAllowWork());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(mc);

                QueryModel medicalCheckupQM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getQueryModel();
                medicalCheckupQM.addWhere("user", ECriteria.EQUAL, emp.getId());

                medicalCheckupTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a medical checkup", ex);
            }
        } else {
            try {
                mc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MEDICAL_CHECKUP.class, vmc.getId());
                mc.setDocumentNo(vmc.getDocumentNo());
                mc.setIssueDate(vmc.getIssueDate());
                mc.setExpireDate(vmc.getExpireDate());
                mc.setCheckupType(vmc.getCheckupType());
                mc.setIssuerName(vmc.getIssuerName());
                mc.setAllowDorm(vmc.isAllowDorm());
                mc.setAllowWork(vmc.isAllowWork());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mc);
                medicalCheckupTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a medical checkup", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(mc);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save medical checkup copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete medical checkup copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveAddressReg(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_ADDRESS ua = (USER_ADDRESS) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                ua.setUser((EMPLOYEE) fm.getEntity());
                ua.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(1)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a registration address", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a registration address", ex);
            }
        }

        return false;
    }

    private boolean preSaveAddressFact(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_ADDRESS ua = (USER_ADDRESS) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                ua.setUser((EMPLOYEE) fm.getEntity());
                ua.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(2)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a residential address", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a residential address", ex);
            }
        }

        return false;
    }

    private boolean preSaveScientificDegree(Object source, Entity e, boolean isNew, int buttonId) {
        V_EMPLOYEE_DEGREE ved = (V_EMPLOYEE_DEGREE) e;
        EMPLOYEE_DEGREE ed = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            ed = new EMPLOYEE_DEGREE();
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                ed.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                ed.setUser(emp);
                ed.setDocumentNo(ved.getDocumentNo());
                ed.setIssueDate(ved.getIssueDate());
                ed.setExpireDate(ved.getExpireDate());
                ed.setDegree(ved.getDegree());
                ed.setPlaceOfIssue(ved.getPlaceOfIssue());
                ed.setDissertationTopic(ved.getDissertationTopic());
                ed.setCandidate(ved.getCandidate());
                ed.setSpeciality(ved.getSpeciality());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(ed);

                QueryModel scientificDegreeQM = ((DBTableModel) scientificDegreeTW.getWidgetModel()).getQueryModel();
                scientificDegreeQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                scientificDegreeTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a scientific degree", ex);
            }
        } else {
            try {
                ed = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEGREE.class, ved.getId());
                ed.setDocumentNo(ved.getDocumentNo());
                ed.setIssueDate(ved.getIssueDate());
                ed.setExpireDate(ved.getExpireDate());
                ed.setDegree(ved.getDegree());
                ed.setPlaceOfIssue(ved.getPlaceOfIssue());
                ed.setDissertationTopic(ved.getDissertationTopic());
                ed.setCandidate(ved.getCandidate());
                ed.setSpeciality(ved.getSpeciality());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                scientificDegreeTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a scientific degree", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) scientificDegreeTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(ed);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save scientific degree copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific degree copy", ex);
            }
        }

        return false;
    }

    private boolean preSavePublication(Object source, Entity e, boolean isNew, int buttonId) {
        V_PUBLICATION vp = (V_PUBLICATION) e;
        PUBLICATION p = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            p = new PUBLICATION();
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                p.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                p.setEmployee(emp);
                p.setPublicationType(vp.getPublicationType());
                p.setTopic(vp.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(p);

                QueryModel publicationQM = ((DBTableModel) publicationTW.getWidgetModel()).getQueryModel();
                publicationQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                publicationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a publication", ex);
            }
        } else {
            try {
                p = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PUBLICATION.class, vp.getId());
                p.setPublicationType(vp.getPublicationType());
                p.setTopic(vp.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(p);
                publicationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a publication", ex);
            }
        }

        return false;
    }

    private boolean preSaveScientificActivity(Object source, Entity e, boolean isNew, int buttonId) {
        V_SCIENTIFIC_ACTIVITY vsa = (V_SCIENTIFIC_ACTIVITY) e;
        SCIENTIFIC_ACTIVITY sa = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            sa = new SCIENTIFIC_ACTIVITY();
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                sa.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                sa.setEmployee(emp);
                sa.setScientificActivityType(vsa.getScientificActivityType());
                sa.setBeginDate(vsa.getBeginDate());
                sa.setEndDate(vsa.getEndDate());
                sa.setTopic(vsa.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(sa);

                QueryModel scientificActivityQM = ((DBTableModel) scientificActivityTW.getWidgetModel()).getQueryModel();
                scientificActivityQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                scientificActivityTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a scientific activity", ex);
            }
        } else {
            try {
                sa = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_ACTIVITY.class, vsa.getId());
                sa.setScientificActivityType(vsa.getScientificActivityType());
                sa.setBeginDate(vsa.getBeginDate());
                sa.setEndDate(vsa.getEndDate());
                sa.setTopic(vsa.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sa);
                scientificActivityTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a scientific activity", ex);
            }
        }

        return false;
    }

    private boolean preSaveScientificManagement(Object source, Entity e, boolean isNew, int buttonId) {
        V_SCIENTIFIC_MANAGEMENT vsm = (V_SCIENTIFIC_MANAGEMENT) e;
        SCIENTIFIC_MANAGEMENT sm = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            sm = new SCIENTIFIC_MANAGEMENT();
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                sm.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                sm.setEmployee(emp);
                sm.setAchievement(vsm.getAchievement());
                sm.setProjectName(vsm.getProjectName());
                sm.setResult(vsm.getResult());
                sm.setScientificManagementType(vsm.getScientificManagementType());
                sm.setStudentsCount(vsm.getStudentsCount());
                sm.setStudentsFIO(vsm.getStudentsFIO());
                sm.setTopic(vsm.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(sm);

                QueryModel scientificManagementQM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getQueryModel();
                scientificManagementQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                scientificManagementTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a scientific management", ex);
            }
        } else {
            try {
                sm = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT.class, vsm.getId());
                sm.setAchievement(vsm.getAchievement());
                sm.setProjectName(vsm.getProjectName());
                sm.setResult(vsm.getResult());
                sm.setScientificManagementType(vsm.getScientificManagementType());
                sm.setStudentsCount(vsm.getStudentsCount());
                sm.setStudentsFIO(vsm.getStudentsFIO());
                sm.setTopic(vsm.getTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sm);
                scientificManagementTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a scientific management", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                SCIENTIFIC_MANAGEMENT_FILE smf = new SCIENTIFIC_MANAGEMENT_FILE();
                smf.setScientificManagement(sm);
                smf.setFileName(fe.getFileName());
                smf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(smf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save scientific management file copy", ex);
                }
            }
        }

        List<SCIENTIFIC_MANAGEMENT_FILE> delList = new ArrayList<SCIENTIFIC_MANAGEMENT_FILE>();
        for (FileBean fe : flfm.getDeleteList()) {
            try {
                delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT_FILE.class, fe.getId()));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific management file copy", ex);
            }
        }

        if (!delList.isEmpty()) {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific management file copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveExperience(Object source, Entity e, boolean isNew, int buttonId) {
        PREVIOUS_EXPERIENCE pe = (PREVIOUS_EXPERIENCE) e;
        if (isNew) {
            try {
                pe.setEmployee((EMPLOYEE) baseDataFW.getWidgetModel().getEntity());
                experienceTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a experience", ex);
                Message.showError("Unable to create a experience");
            }
        }

        return true;
    }

    private boolean preSaveCareer(Object source, Entity e, boolean isNew, int buttonId) {
        EMPLOYEE_DEPT ed = (EMPLOYEE_DEPT) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                ed.setEmployee(emp);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ed);

                QueryModel careerQM = ((DBTableModel) careerTW.getWidgetModel()).getQueryModel();
                careerQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                careerTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a career", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                careerTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a career", ex);
            }
        }

        return false;
    }

    private boolean preSaveMaster(Object source, Entity e, boolean isNew, int buttonId) {
        MASTER ed = (MASTER) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                EMPLOYEE emp = (EMPLOYEE) fm.getEntity();
                ed.setEmployee(emp);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ed);

                QueryModel masterQM = ((DBTableModel) masterTW.getWidgetModel()).getQueryModel();
                masterQM.addWhere("employee", ECriteria.EQUAL, emp.getId());

                masterTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a master", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                masterTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a master", ex);
            }
        }

        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(educationTW)) {
            for (Entity e : entities) {
                ((EDUCATION_DOC) e).setDeleted(true);
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(entities);
                educationTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete education docs", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(languageTW)) {
            List<USER_LANGUAGE> delList = new ArrayList<USER_LANGUAGE>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_LANGUAGE.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete user languages", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                languageTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete user languages", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(medicalCheckupTW)) {
            List<MEDICAL_CHECKUP> delList = new ArrayList<MEDICAL_CHECKUP>();
            for (Entity e : entities) {
                try {
                    MEDICAL_CHECKUP mc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MEDICAL_CHECKUP.class, e.getId());
                    mc.setDeleted(true);
                    delList.add(mc);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete medical checkup", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(delList);
                medicalCheckupTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete medical checkup", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(scientificDegreeTW)) {
            List<EMPLOYEE_DEGREE> delList = new ArrayList<EMPLOYEE_DEGREE>();
            for (Entity e : entities) {
                try {
                    EMPLOYEE_DEGREE ed = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEGREE.class, e.getId());
                    ed.setDeleted(true);
                    delList.add(ed);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete scientific degree", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(delList);
                scientificDegreeTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific degree", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(publicationTW)) {
            List<PUBLICATION> delList = new ArrayList<PUBLICATION>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PUBLICATION.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete publication", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                publicationTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete publication", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(scientificActivityTW)) {
            List<SCIENTIFIC_ACTIVITY> delList = new ArrayList<SCIENTIFIC_ACTIVITY>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_ACTIVITY.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete scientific activity", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                scientificActivityTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific activity", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(scientificManagementTW)) {
            List<SCIENTIFIC_MANAGEMENT> delList = new ArrayList<SCIENTIFIC_MANAGEMENT>();
            List<SCIENTIFIC_MANAGEMENT_FILE> delFileList = new ArrayList<SCIENTIFIC_MANAGEMENT_FILE>();

            QueryModel<SCIENTIFIC_MANAGEMENT_FILE> smfQM = new QueryModel<SCIENTIFIC_MANAGEMENT_FILE>(SCIENTIFIC_MANAGEMENT_FILE.class);
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT.class, e.getId()));
                    smfQM.addWhere("scientificManagement", ECriteria.EQUAL, e.getId());
                    delFileList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(smfQM));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete scientific management", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delFileList);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                scientificManagementTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific management", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(careerTW)) {
            List<EMPLOYEE_DEPT> delList = new ArrayList<EMPLOYEE_DEPT>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEPT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete career", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                careerTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete career", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(roomTW)) {
            List<TEACHER_ROOM> delList = new ArrayList<TEACHER_ROOM>();
            try {
                for (Entity e : entities) {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(TEACHER_ROOM.class, e.getId()));
                }

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                roomTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete teacher room", ex);
                Message.showError(ex.toString());
            }
        } else if (source.equals(masterTW)) {
            List<MASTER> delList = new ArrayList<MASTER>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MASTER.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete master", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                masterTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete master", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        return super.preDelete(source, entities, buttonId);
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
        Message.showError(ex.toString());
    }


    private class AddNewRoomListener extends AbstractYesButtonListener {

        private boolean can = false;

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            List<Entity> selectedList = roomSelectDlg.getSelectedEntities();
            if (!selectedList.isEmpty()) {
                List<TEACHER_ROOM> newList = new ArrayList<TEACHER_ROOM>();
                try {
                    EMPLOYEE teacher = (EMPLOYEE) baseDataFW.getWidgetModel().getEntity();
                    for (Entity e : selectedList) {
                        TEACHER_ROOM cd = new TEACHER_ROOM();
                        cd.setTeacher(teacher);
                        cd.setRoom(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM.class, e.getId()));
                        newList.add(cd);
                    }

                    if (!newList.isEmpty()) {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
                        roomTW.refresh();
                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to add rooms to teacher", ex);
                    Message.showError(ex.toString());
                }
            }
        }

        @Override
        protected boolean canClose() {
            return can;
        }

        @Override
        protected boolean canProcess() {
            can = !roomSelectDlg.getSelectedEntities().isEmpty();

            return can;
        }


    }


}
