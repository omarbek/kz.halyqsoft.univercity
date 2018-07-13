package kz.halyqsoft.univercity.modules.status;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.*;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ORDER_DOC;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import java.io.*;
import java.util.*;

@SuppressWarnings({"serial"})
public final class StatusEdit extends AbstractFormWidgetView {

    private List<STUDENT> studentList;
    private TableWidget ordersTW;
    private Table orderFilesTable;
    private VerticalLayout rightVL;
    private EOperType operType;
    private ComboBox facultyWidget;
    private ComboBox specialityWidget;
    private ComboBox studyYearWidget;
    private ComboBox languageWidget;
    private ComboBox educationTypeWidget;
    private DateField endDateWidget;
    private ComboBox orderTypeWidget;
    private ComboBox orderStudyYearWidget;
    private TextField orderNoWidget;
    private DateField orderDateWidget;
    private CheckBox hideTranscriptWidget;
    private TextArea orderDescrWidget;
    private ListSelect orderFilesWidget;
    private File file;
    private Upload upload;
    private STUDENT_EDUCATION studentEducation;

    StatusEdit(List<STUDENT> studentList, STUDENT_STATUS status) throws Exception {
        super();
        this.studentList = studentList;

        for (STUDENT student : studentList) {
            studentEducation = student.getLastEducation();
        }

        super.initView(false);

        GridLayout mainGL = new GridLayout();
        mainGL.setSizeFull();
        mainGL.setSpacing(true);
        mainGL.setRows(1);
        mainGL.setColumns(2);

		/* 1.0. Left side */
        VerticalLayout leftVL = new VerticalLayout();
        leftVL.setSizeFull();
        leftVL.setSpacing(true);

		/* 1.1. Education detailed info */
        FormLayout educationFL = new FormLayout();
        educationFL.setCaption(getUILocaleUtil().getCaption("education.info"));

        BeanItemContainer<STUDENT> container = new BeanItemContainer<>(STUDENT.class, studentList);

        Grid studentGW = new Grid(container);
        studentGW.removeAllColumns();
        studentGW.addColumn("firstName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class,
                "firstName"));
        studentGW.addColumn("lastName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class,
                "lastName"));
        studentGW.addColumn("middleName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class,
                "middleName"));
        studentGW.setColumnOrder("firstName", "lastName", "middleName");
        leftVL.addComponent(studentGW);

        //		statusLabel = new Label();
        //		statusLabel.addStyleName("bold");
        //		statusLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "status"));
        //		statusLabel.setValue(studentEducation.getStatus().toString());
        //		educationFL.addComponent(statusLabel);
        //
        //		leftVL.addComponent(educationFL);
        //		leftVL.setComponentAlignment(educationFL, Alignment.TOP_LEFT);

		/* 1.3. Orders */
        ordersTW = new TableWidget(V_ORDER_DOC.class);
        ordersTW.showToolbar(false);
        ordersTW.addEntityListener(this);
        DBTableModel ordersTM = (DBTableModel) ordersTW.getWidgetModel();
        ordersTM.setMultiSelect(false);
        ordersTM.setWidth("hideTranscript", 150);
        ordersTM.setWidth("issueDate", 90);
        ordersTM.setWidth("studyYearYear", 80);
        ordersTM.setWidth("documentNo", 80);
        ordersTM.setPageLength(5);
        QueryModel ordersQM = ordersTM.getQueryModel();
        ordersQM.addWhere("user", ECriteria.EQUAL, studentEducation.getStudent().getId());
        ordersQM.addOrderDesc("issueDate");
        leftVL.addComponent(ordersTW);
        leftVL.setComponentAlignment(ordersTW, Alignment.MIDDLE_CENTER);

		/* 1.4. Order files */
        orderFilesTable = new Table();
        orderFilesTable.setSizeFull();
        orderFilesTable.setSelectable(false);
        orderFilesTable.setColumnReorderingAllowed(false);
        orderFilesTable.setColumnCollapsingAllowed(false);
        orderFilesTable.setEditable(false);
        orderFilesTable.setNullSelectionAllowed(false);
        orderFilesTable.setFooterVisible(false);
        orderFilesTable.setPageLength(5);
        orderFilesTable.setCaption(getUILocaleUtil().getEntityLabel(USER_DOCUMENT_FILE.class));
        orderFilesTable.setColumnHeader("fileName", getUILocaleUtil().getEntityFieldLabel(
                USER_DOCUMENT_FILE.class, "fileName"));
        orderFilesTable.addGeneratedColumn("download", new DownloadColumnGenerator());
        orderFilesTable.setColumnHeader("download", "");
        orderFilesTable.setColumnWidth("download", 80);
        orderFilesTable.setColumnExpandRatio("fileName", 1);
        leftVL.addComponent(orderFilesTable);
        leftVL.setComponentAlignment(orderFilesTable, Alignment.BOTTOM_CENTER);

        mainGL.addComponent(leftVL);

		/* Right side */
        rightVL = new VerticalLayout();
        rightVL.setSizeFull();
        rightVL.setSpacing(true);
        mainGL.addComponent(rightVL);

        getContent().addComponent(mainGL);
        if (status == null)
            otherOrder();
        else {
            switch (status.getId().toString()) {
                case "1":
                    restore();
                    break;
                case "2":
                    alumnus();
                    break;
                case "3":
                    deduct();
                    break;
                case "4":
                    transfer();
                    break;
                case "5":
                    academicLeave();
                    break;
                default:
                    otherOrder();
                    break;
            }
        }
        refreshOrderFiles(ID.valueOf(-1));
    }

    @Override
    protected AbstractCommonView getParentView() {
//		return new StatusList(filter);
        return null;
    }

    private enum EOperType {
        TRANSFER, ACADEMIC_LEAVE, RESTORE, ALUMNUS, DEDUCT, OTHER, EDIT_ORDER
    }

    @Override
    public String getViewName() {
        return "statusEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("student.status.edit");
    }

    @Override
    protected boolean isTabbedView() {
        return false;
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
    }

    private void transfer() {

        operType = EOperType.TRANSFER;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("transfer"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hl3 = createYearLangEduTypeWidget();
        rightVL.addComponent(hl3);
        rightVL.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

        HorizontalLayout hl4 = createDateWidget();
        rightVL.addComponent(hl4);
        rightVL.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

        HorizontalLayout hl5 = createStatusWidget();
        rightVL.addComponent(hl5);
        rightVL.setComponentAlignment(hl5, Alignment.TOP_RIGHT);

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);

    }

    private void academicLeave() {

        operType = EOperType.ACADEMIC_LEAVE;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("academic.leave"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hl3 = createYearLangEduTypeWidget();
        rightVL.addComponent(hl3);
        rightVL.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

        HorizontalLayout hl4 = createDateWidget();
        rightVL.addComponent(hl4);
        rightVL.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

        HorizontalLayout hl5 = createStatusWidget();
        rightVL.addComponent(hl5);
        rightVL.setComponentAlignment(hl5, Alignment.TOP_RIGHT);

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);

    }

    private void restore() {
        operType = EOperType.RESTORE;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("restore"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hl3 = createYearLangEduTypeWidget();
        rightVL.addComponent(hl3);
        rightVL.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

        HorizontalLayout hl4 = createDateWidget();
        rightVL.addComponent(hl4);
        rightVL.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

        HorizontalLayout hl5 = createStatusWidget();
        rightVL.addComponent(hl5);
        rightVL.setComponentAlignment(hl5, Alignment.TOP_RIGHT);

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    private void alumnus() {
        operType = EOperType.ALUMNUS;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("alumnus"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hl3 = createYearLangEduTypeWidget();
        rightVL.addComponent(hl3);
        rightVL.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

        HorizontalLayout hl4 = createDateWidget();
        rightVL.addComponent(hl4);
        rightVL.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

        HorizontalLayout hl5 = createStatusWidget();
        rightVL.addComponent(hl5);
        rightVL.setComponentAlignment(hl5, Alignment.TOP_RIGHT);

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    private void deduct() {
        operType = EOperType.DEDUCT;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("deduct"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hl3 = createYearLangEduTypeWidget();
        rightVL.addComponent(hl3);
        rightVL.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

        HorizontalLayout hl4 = createDateWidget();
        rightVL.addComponent(hl4);
        rightVL.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

        HorizontalLayout hl5 = createStatusWidget();
        rightVL.addComponent(hl5);
        rightVL.setComponentAlignment(hl5, Alignment.TOP_RIGHT);

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    private void otherOrder() {
        operType = EOperType.OTHER;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("new.order"));

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    private void editOrder(V_ORDER_DOC vod) {
        operType = EOperType.EDIT_ORDER;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("edit.order"));

        VerticalLayout orderVL = createOrderWidget(vod);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.SELECTED) {
            if (ev.getSource().equals(ordersTW)) {
                V_ORDER_DOC vod = (V_ORDER_DOC) ev.getEntities().get(0);
                refreshOrderFiles(vod.getId());
                if (!studentEducation.getStatus().getId().equals(ID.valueOf(3))
                        && !studentEducation.getStatus().getId().equals(ID.valueOf(4))) {
                    editOrder(vod);
                }
            }
        } else {
            super.handleEntityEvent(ev);
        }
    }

    private void refreshOrderFiles(ID docId) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addWhere("userDocument", ECriteria.EQUAL, docId);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);
        try {
            List<USER_DOCUMENT_FILE> udfList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(udfQM);
            BeanItemContainer<USER_DOCUMENT_FILE> udfBIC = new BeanItemContainer<>(USER_DOCUMENT_FILE.class,
                    udfList);
            orderFilesTable.setContainerDataSource(udfBIC);
            orderFilesTable.setVisibleColumns("fileName", "download");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh files list", ex);
        }
    }

    private static Label createRequiredIndicator() {
        Label requiredIndicator = new Label();
        requiredIndicator.setValue("*");
        requiredIndicator.setStyleName("v-required-field-indicator");
        requiredIndicator.addStyleName("required-indicator");

        return requiredIndicator;
    }

    private HorizontalLayout createFacultyWidget() {
        HorizontalLayout facultyHL = new HorizontalLayout();
        Label facultyLabel = new Label();
        facultyLabel.setWidthUndefined();
        facultyLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "faculty"));
        facultyHL.addComponent(facultyLabel);
        facultyHL.addComponent(createRequiredIndicator());
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
        facultyQM.addWhere("deleted", Boolean.FALSE);
        facultyQM.addWhereNullAnd("parent");
        facultyQM.addOrder("deptName");
        try {
            List<DEPARTMENT> facultyList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(facultyQM);
            BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class, facultyList);
            facultyWidget = new ComboBox();
            facultyWidget.setNewItemsAllowed(false);
            facultyWidget.setFilteringMode(FilteringMode.STARTSWITH);
            facultyWidget.setTextInputAllowed(true);
            facultyWidget.setImmediate(true);
            facultyWidget.setWidth(350, Unit.PIXELS);
            facultyWidget.setContainerDataSource(facultyBIC);
            facultyWidget.setValue(studentEducation.getFaculty());
            facultyWidget.setEnabled(operType.equals(EOperType.TRANSFER));
            facultyWidget.addValueChangeListener(new FacultyChangeListener());
            facultyHL.addComponent(facultyWidget);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load faculty list", ex);
        }

        return facultyHL;
    }

    private HorizontalLayout createSpecialityWidget() {
        HorizontalLayout specialityHL = new HorizontalLayout();
        Label specialityLabel = new Label();
        specialityLabel.setWidthUndefined();
        specialityLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "speciality"));
        specialityHL.addComponent(specialityLabel);
        specialityHL.addComponent(createRequiredIndicator());
        specialityWidget = new ComboBox();
        specialityWidget.setNewItemsAllowed(false);
        specialityWidget.setFilteringMode(FilteringMode.CONTAINS);
        specialityWidget.setTextInputAllowed(true);
        specialityWidget.setWidth(350, Unit.PIXELS);
        refreshSpecialities(studentEducation.getFaculty().getId());
        specialityWidget.setValue(studentEducation.getSpeciality());
        specialityWidget.setEnabled(operType.equals(EOperType.TRANSFER));
        specialityHL.addComponent(specialityWidget);

        return specialityHL;
    }

    private void refreshSpecialities(ID facultyId) {
        QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
        FromItem specialityChairFM = specialityQM.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class, "id");
        specialityQM.addWhere(specialityChairFM, "parent", ECriteria.EQUAL, facultyId);
        specialityQM.addWhereAnd("deleted", Boolean.FALSE);
        specialityQM.addOrder("specName");
        try {
            List<SPECIALITY> specialityList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(specialityQM);
            BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class,
                    specialityList);
            specialityWidget.setContainerDataSource(specialityBIC);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load speciality list", ex);
        }
    }

    private HorizontalLayout createYearLangEduTypeWidget() {
        HorizontalLayout hl = new HorizontalLayout();

        Label studyYearLabel = new Label();
        studyYearLabel.setWidthUndefined();
        studyYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
        hl.addComponent(studyYearLabel);
        hl.addComponent(createRequiredIndicator());
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        studyYearQM.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
        studyYearQM.addOrder("studyYear");
        try {
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(studyYearQM);
            BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class, studyYearList);
            studyYearWidget = new ComboBox();
            studyYearWidget.setNewItemsAllowed(false);
            studyYearWidget.setFilteringMode(FilteringMode.OFF);
            studyYearWidget.setTextInputAllowed(false);
            studyYearWidget.setWidth(50, Unit.PIXELS);
            studyYearWidget.setContainerDataSource(studyYearBIC);
            studyYearWidget.setValue(studentEducation.getStudyYear());
            studyYearWidget.setEnabled(operType.equals(EOperType.TRANSFER));
            hl.addComponent(studyYearWidget);

            Label languageLabel = new Label();
            languageLabel.setWidthUndefined();
            languageLabel.addStyleName("margin-left-30");
            languageLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "language"));
            hl.addComponent(languageLabel);
            hl.addComponent(createRequiredIndicator());
            QueryModel<LANGUAGE> languageQM = new QueryModel<>(LANGUAGE.class);
            languageQM.addOrder("langName");
            List<LANGUAGE> languageList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(languageQM);
            BeanItemContainer<LANGUAGE> languageBIC = new BeanItemContainer<>(LANGUAGE.class, languageList);
            languageWidget = new ComboBox();
            languageWidget.setNewItemsAllowed(false);
            languageWidget.setFilteringMode(FilteringMode.OFF);
            languageWidget.setTextInputAllowed(false);
            languageWidget.setWidth(110, Unit.PIXELS);
            languageWidget.setContainerDataSource(languageBIC);
            languageWidget.setValue(studentEducation.getLanguage());
            languageWidget.setEnabled(operType.equals(EOperType.TRANSFER));
            hl.addComponent(languageWidget);

            Label educationTypeLabel = new Label();
            educationTypeLabel.setWidthUndefined();
            educationTypeLabel.addStyleName("margin-left-30");
            educationTypeLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class,
                    "educationType"));
            hl.addComponent(educationTypeLabel);
            hl.addComponent(createRequiredIndicator());
            QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
            educationTypeQM.addOrder("typeName");
            List<STUDENT_EDUCATION_TYPE> educationTypeList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(educationTypeQM);
            BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(
                    STUDENT_EDUCATION_TYPE.class, educationTypeList);
            educationTypeWidget = new ComboBox();
            educationTypeWidget.setNewItemsAllowed(false);
            educationTypeWidget.setFilteringMode(FilteringMode.OFF);
            educationTypeWidget.setTextInputAllowed(false);
            educationTypeWidget.setWidth(110, Unit.PIXELS);
            educationTypeWidget.setContainerDataSource(educationTypeBIC);
            educationTypeWidget.setValue(studentEducation.getEducationType());
            educationTypeWidget.setEnabled(operType.equals(EOperType.TRANSFER));
            hl.addComponent(educationTypeWidget);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load records", ex);
        }

        return hl;
    }

    private HorizontalLayout createDateWidget() {
        HorizontalLayout hl = new HorizontalLayout();

        Label entryDateLabel = new Label();
        entryDateLabel.setWidthUndefined();
        entryDateLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "entryDate"));
        hl.addComponent(entryDateLabel);
        hl.addComponent(createRequiredIndicator());
        DateField entryDateWidget = new DateField();
        entryDateWidget.setWidth(80, Unit.PIXELS);
        entryDateWidget.setEnabled(false);
        entryDateWidget.setValue(studentEducation.getEntryDate());
        hl.addComponent(entryDateWidget);

        Label endDateLabel = new Label();
        endDateLabel.setWidthUndefined();
        endDateLabel.addStyleName("margin-left-30");
        endDateLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "endDate"));
        hl.addComponent(endDateLabel);
        hl.addComponent(createRequiredIndicator());
        endDateWidget = new DateField();
        endDateWidget.setWidth(80, Unit.PIXELS);
        endDateWidget.setEnabled(operType.equals(EOperType.ALUMNUS) || operType.equals(EOperType.DEDUCT));
        if (operType.equals(EOperType.ALUMNUS) || operType.equals(EOperType.DEDUCT)) {
            endDateWidget.setValue(new Date());
        } else {
            endDateWidget.setValue(studentEducation.getEndDate());
        }
        hl.addComponent(endDateWidget);

        return hl;
    }

    private HorizontalLayout createStatusWidget() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(new MarginInfo(false, false, true, false));

        Label statusLabel = new Label();
        statusLabel.setWidthUndefined();
        statusLabel.addStyleName("margin-left-30");
        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "status"));
        hl.addComponent(statusLabel);
        hl.addComponent(createRequiredIndicator());
        QueryModel<STUDENT_STATUS> statusQM = new QueryModel<>(STUDENT_STATUS.class);
        statusQM.addOrder("statusName");
        try {
            List<STUDENT_STATUS> statusList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(statusQM);
            BeanItemContainer<STUDENT_STATUS> statusBIC = new BeanItemContainer<>(
                    STUDENT_STATUS.class, statusList);
            ComboBox statusWidget = new ComboBox();
            statusWidget.setNewItemsAllowed(false);
            statusWidget.setFilteringMode(FilteringMode.OFF);
            statusWidget.setTextInputAllowed(false);
            statusWidget.setWidth(130, Unit.PIXELS);
            statusWidget.setContainerDataSource(statusBIC);
            statusWidget.setValue(studentEducation.getStatus());
            statusWidget.setEnabled(false);
            hl.addComponent(statusWidget);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student status list", ex);
        }

        return hl;
    }

    private VerticalLayout createOrderWidget(V_ORDER_DOC vod) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(true);
        if (!operType.equals(EOperType.OTHER) && !operType.equals(EOperType.EDIT_ORDER)) {
            vl.setCaption(getUILocaleUtil().getCaption("new.order"));
        }

        HorizontalLayout hl1 = new HorizontalLayout();

        Label orderTypeLabel = new Label();
        orderTypeLabel.setWidthUndefined();
        orderTypeLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "orderType"));
        hl1.addComponent(orderTypeLabel);
        hl1.addComponent(createRequiredIndicator());
        QueryModel<ORDER_TYPE> orderTypeQM = new QueryModel<>(ORDER_TYPE.class);
        if (operType.equals(EOperType.OTHER)) {
            List<ID> idList = new ArrayList<>();
            idList.add(ID.valueOf(1));
            idList.add(ID.valueOf(2));
            orderTypeQM.addWhereIn("id", idList);
        }
        orderTypeQM.addOrder("typeName");
        try {
            List<ORDER_TYPE> orderTypeList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(orderTypeQM);
            BeanItemContainer<ORDER_TYPE> orderTypeBIC = new BeanItemContainer<>(ORDER_TYPE.class, orderTypeList);
            orderTypeWidget = new ComboBox();
            orderTypeWidget.setNewItemsAllowed(false);
            orderTypeWidget.setFilteringMode(FilteringMode.OFF);
            orderTypeWidget.setTextInputAllowed(false);
            orderTypeWidget.setWidth(180, Unit.PIXELS);
            orderTypeWidget.setContainerDataSource(orderTypeBIC);
            if (!operType.equals(EOperType.OTHER) && !operType.equals(EOperType.EDIT_ORDER)) {
                ORDER_TYPE orderType = null;
                for (ORDER_TYPE ot : orderTypeList) {
                    if (operType.equals(EOperType.TRANSFER)) {
                        if (ot.getId().equals(ID.valueOf(3))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(EOperType.ACADEMIC_LEAVE)) {
                        if (ot.getId().equals(ID.valueOf(4))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(EOperType.RESTORE)) {
                        if (ot.getId().equals(ID.valueOf(5))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(EOperType.ALUMNUS)) {
                        if (ot.getId().equals(ID.valueOf(7))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(EOperType.DEDUCT)) {
                        if (ot.getId().equals(ID.valueOf(8))) {
                            orderType = ot;
                            break;
                        }
                    }
                }

                if (orderType != null) {
                    orderTypeWidget.setValue(orderType);
                }
                orderTypeWidget.setEnabled(false);
            } else if (operType.equals(EOperType.EDIT_ORDER)) {
                vod = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(V_ORDER_DOC.class, vod.getId());
                orderTypeWidget.setValue(vod.getOrderType());
            }
            hl1.addComponent(orderTypeWidget);

            Label studyYearLabel = new Label();
            studyYearLabel.setWidthUndefined();
            studyYearLabel.addStyleName("margin-left-30");
            studyYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "studyYear"));
            hl1.addComponent(studyYearLabel);
            hl1.addComponent(createRequiredIndicator());
            QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
            studyYearQM.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(studyYearQM);
            BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class, studyYearList);
            orderStudyYearWidget = new ComboBox();
            orderStudyYearWidget.setNewItemsAllowed(false);
            orderStudyYearWidget.setFilteringMode(FilteringMode.OFF);
            orderStudyYearWidget.setTextInputAllowed(false);
            orderStudyYearWidget.setWidth(50, Unit.PIXELS);
            orderStudyYearWidget.setContainerDataSource(studyYearBIC);
            orderStudyYearWidget.setValue(studentEducation.getStudyYear());
            orderStudyYearWidget.setEnabled(false);
            hl1.addComponent(orderStudyYearWidget);

            vl.addComponent(hl1);
            vl.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

            HorizontalLayout hl2 = new HorizontalLayout();

            Label orderNoLabel = new Label();
            orderNoLabel.setWidthUndefined();
            orderNoLabel.setValue(getUILocaleUtil().getCaption("order.no"));
            hl2.addComponent(orderNoLabel);
            hl2.addComponent(createRequiredIndicator());
            orderNoWidget = new TextField();
            orderNoWidget.setWidth(120, Unit.PIXELS);
            if (operType.equals(EOperType.EDIT_ORDER)) {
                orderNoWidget.setValue(vod.getDocumentNo());
            }
            hl2.addComponent(orderNoWidget);

            Label orderDateLabel = new Label();
            orderDateLabel.setWidthUndefined();
            orderDateLabel.addStyleName("margin-left-30");
            orderDateLabel.setValue(getUILocaleUtil().getCaption("order.date"));
            hl2.addComponent(orderDateLabel);
            hl2.addComponent(createRequiredIndicator());
            orderDateWidget = new DateField();
            orderDateWidget.setWidth(80, Unit.PIXELS);
            if (operType.equals(EOperType.EDIT_ORDER)) {
                orderDateWidget.setValue(vod.getIssueDate());
            }
            hl2.addComponent(orderDateWidget);

            Label hideTranscriptLabel = new Label();
            hideTranscriptLabel.setWidthUndefined();
            hideTranscriptLabel.addStyleName("margin-left-30");
            hideTranscriptLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "hideTranscript"));
            hl2.addComponent(hideTranscriptLabel);
            hideTranscriptWidget = new CheckBox();
            if (operType.equals(EOperType.EDIT_ORDER)) {
                hideTranscriptWidget.setValue(vod.isHideTranscript());
            } else {
                hideTranscriptWidget.setValue(Boolean.FALSE);
            }
            hl2.addComponent(hideTranscriptWidget);

            vl.addComponent(hl2);
            vl.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

            HorizontalLayout hl3 = new HorizontalLayout();

            Label orderDescrLabel = new Label();
            orderDescrLabel.setWidthUndefined();
            orderDescrLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "descr"));
            hl3.addComponent(orderDescrLabel);
            hl3.addComponent(createRequiredIndicator());
            orderDescrWidget = new TextArea();
            orderDescrWidget.setWordwrap(true);
            orderDescrWidget.setHeight(45, Unit.PIXELS);
            orderDescrWidget.setWidth(350, Unit.PIXELS);
            if (operType.equals(EOperType.EDIT_ORDER)) {
                orderDescrWidget.setValue(vod.getDescr());
            }
            hl3.addComponent(orderDescrWidget);

            vl.addComponent(hl3);
            vl.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

            HorizontalLayout hl4 = new HorizontalLayout();

            Label orderFilesLabel = new Label();
            orderFilesLabel.setWidthUndefined();
            orderFilesLabel.setValue(getUILocaleUtil().getEntityLabel(USER_DOCUMENT_FILE.class));
            hl4.addComponent(orderFilesLabel);
            orderFilesWidget = new ListSelect();
            orderFilesWidget.setRows(4);
            orderFilesWidget.setWidth(350, Unit.PIXELS);
            orderFilesWidget.setNullSelectionAllowed(false);
            List<FileBean> fbList = new ArrayList<>();
            if (operType.equals(EOperType.EDIT_ORDER)) {
                QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
                udfQM.addWhere("userDocument", ECriteria.EQUAL, vod.getId());
                udfQM.addWhereAnd("deleted", Boolean.FALSE);
                List<USER_DOCUMENT_FILE> udfList = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(udfQM);
                for (USER_DOCUMENT_FILE udf : udfList) {
                    FileBean fb = new FileBean();
                    fb.setId(udf.getId());
                    fb.setFileName(udf.getFileName());
                    fb.setFileBytes(udf.getFileBytes());
                    fbList.add(fb);
                }
            }
            BeanItemContainer<FileBean> orderFilesBIC = new BeanItemContainer<>(FileBean.class, fbList);
            orderFilesWidget.setContainerDataSource(orderFilesBIC);
            hl4.addComponent(orderFilesWidget);
            FileReceiver fr = new FileReceiver();
            upload = new Upload();
            upload.setButtonCaption(getUILocaleUtil().getCaption("upload"));
            upload.setImmediate(true);
            upload.setReceiver(fr);
            upload.addSucceededListener(fr);
            upload.addStartedListener(fr);
            hl4.addComponent(upload);

            vl.addComponent(hl4);
            vl.setComponentAlignment(hl4, Alignment.TOP_RIGHT);

            HorizontalLayout toolbarHL = new HorizontalLayout();
            toolbarHL.setSpacing(true);
            toolbarHL.setMargin(new MarginInfo(true, false, false, false));

            Button saveButton = new Button();
            saveButton.setWidth(120, Unit.PIXELS);
            saveButton.setIcon(new ThemeResource("img/button/ok.png"));
            saveButton.setCaption(getUILocaleUtil().getCaption("save"));
            saveButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    save();
                }
            });
            toolbarHL.addComponent(saveButton);

            Button cancelButton = new Button();
            cancelButton.setWidth(120, Unit.PIXELS);
            cancelButton.setIcon(new ThemeResource("img/button/cancel.png"));
            cancelButton.setCaption(getUILocaleUtil().getCaption("cancel"));
            cancelButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    cancel();
                }
            });
            toolbarHL.addComponent(cancelButton);

            vl.addComponent(toolbarHL);
            vl.setComponentAlignment(toolbarHL, Alignment.MIDDLE_CENTER);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to create order widget list", ex);
        }

        return vl;
    }

    private void save() {
        if (operType.equals(EOperType.TRANSFER)) {
            doTransfer();
        } else if (operType.equals(EOperType.ACADEMIC_LEAVE)) {
            doAcademicLeave();
        } else if (operType.equals(EOperType.RESTORE)) {
            doRestore();
        } else if (operType.equals(EOperType.ALUMNUS)) {
            doAlumnus();
        } else if (operType.equals(EOperType.DEDUCT)) {
            doDeduct();
        } else if (operType.equals(EOperType.OTHER) || operType.equals(EOperType.EDIT_ORDER)) {
            saveOrder();
        }
    }

    private void doTransfer() {
        try {
            for (STUDENT student : studentList) {
                studentEducation = null;
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                seQM.addWhereNullAnd("child");
                try {
                    STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }

                checkStudentEducationInfo();
                checkOrder();
                STUDENT_STATUS ss = null;
                if (!studyYearWidget.getValue().equals(studentEducation.getStudyYear())) {
                    ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_STATUS.class, ID.valueOf(6));
                } else if (!facultyWidget.getValue().equals(studentEducation.getFaculty())
                        || !specialityWidget.getValue().equals(studentEducation.getSpeciality())) {
                    ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_STATUS.class, ID.valueOf(4));
                } else if (!educationTypeWidget.getValue().equals(studentEducation.getEducationType())) {
                    ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_STATUS.class, ID.valueOf(7));
                } else if (!languageWidget.getValue().equals(studentEducation.getLanguage())) {
                    ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_STATUS.class, ID.valueOf(8));
                }

                if (ss == null) {
                    Message.showInfo(getUILocaleUtil().getMessage("unable.to.transfer"));
                } else {
                    SPECIALITY speciality = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(SPECIALITY.class, ((SPECIALITY) specialityWidget.getValue()).getId());
                    STUDENT_EDUCATION newSE = new STUDENT_EDUCATION();
                    newSE.setStudent(studentEducation.getStudent());
                    newSE.setFaculty((DEPARTMENT) facultyWidget.getValue());
                    newSE.setChair(speciality.getDepartment());
                    newSE.setSpeciality(speciality);
                    newSE.setStudyYear((STUDY_YEAR) studyYearWidget.getValue());
                    newSE.setLanguage((LANGUAGE) languageWidget.getValue());
                    newSE.setEducationType((STUDENT_EDUCATION_TYPE) educationTypeWidget.getValue());
                    newSE.setEntryDate(studentEducation.getEntryDate());
                    newSE.setEndDate(endDateWidget.getValue());
                    newSE.setStatus(studentEducation.getStatus());
                    newSE.setCreated(new Date());

                    createNewOrder();
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newSE);
                    studentEducation.setStatus(ss);
                    studentEducation.setChild(newSE);
                    studentEducation.setUpdated(new Date());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                    if (newSE.getStudyYear().getStudyYear() > studentEducation.getStudyYear().getStudyYear()) {
                        int i = newSE.getStudyYear().getStudyYear() - 1;
                        ENTRANCE_YEAR ey = newSE.getStudent().getEntranceYear();
                        QueryModel<ENTRANCE_YEAR> syQM = new QueryModel<>(ENTRANCE_YEAR.class);
                        syQM.addWhere("beginYear", ECriteria.EQUAL, ey.getBeginYear() + i);
                        syQM.addWhereAnd("endYear", ECriteria.EQUAL, ey.getEndYear() + i);
                        try {
                            ENTRANCE_YEAR sy = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    lookupSingle(syQM);
                            QueryModel<STUDENT_SUBJECT> ssQM = new QueryModel<>(STUDENT_SUBJECT.class);
                            FromItem ssFI = ssQM.addJoin(EJoin.INNER_JOIN, "semesterData", SEMESTER_DATA.class,
                                    "id");
                            ssQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
                            ssQM.addWhereAnd(ssFI, "year", ECriteria.EQUAL, sy.getId());
                            List<STUDENT_SUBJECT> ssList = SessionFacadeFactory.getSessionFacade(
                                    CommonEntityFacadeBean.class).lookup(ssQM);

                            List<STUDENT_SUBJECT> mergeList = new ArrayList<>();
                            for (STUDENT_SUBJECT ss1 : ssList) {
                                ss1.setStudentEducation(newSE);
                                mergeList.add(ss1);
                            }

                            if (!mergeList.isEmpty()) {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        merge(mergeList);
                            }
                        } catch (Exception ex) {
                            LOG.error("Unable to transfer student subjects: ", ex);
                        }
                    }

                    studentEducation = newSE;

                    ordersTW.refresh();
                    refreshOrderFiles(ID.valueOf(-1));
                }
            }
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");

        } catch (Exception ex) {
            LOG.error("Unable to transfer the student: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private void doAcademicLeave() {
        try {
            for (STUDENT student : studentList) {
                studentEducation = null;
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                seQM.addWhereNullAnd("child");
                try {
                    STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }

                checkOrder();
                STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(STUDENT_STATUS.class, ID.valueOf(5));

                createNewOrder();
                studentEducation.setStatus(ss);
                studentEducation.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                ordersTW.refresh();
                refreshOrderFiles(ID.valueOf(-1));

            }
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            LOG.error("Unable to academic leave the student: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private void doRestore() {
        try {
            for (STUDENT student : studentList) {
                studentEducation = null;
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                seQM.addWhereNullAnd("child");
                try {
                    STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }

                checkOrder();
                STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        STUDENT_STATUS.class, ID.valueOf(1));

                createNewOrder();
                studentEducation.setStatus(ss);
                studentEducation.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                ordersTW.refresh();
                refreshOrderFiles(ID.valueOf(-1));

            }
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            LOG.error("Unable to academic restore the student: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private void doAlumnus() {
        try {
            for (STUDENT student : studentList) {
                studentEducation = null;
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                seQM.addWhereNullAnd("child");
                try {
                    STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }

                checkStudentEducationInfo();
                checkOrder();
                STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(STUDENT_STATUS.class, ID.valueOf(2));

                createNewOrder();
                studentEducation.setEndDate(endDateWidget.getValue());
                studentEducation.setStatus(ss);
                studentEducation.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                ordersTW.refresh();
                refreshOrderFiles(ID.valueOf(-1));
            }
            //			toolbarLeftHL.setVisible(false);
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            LOG.error("Unable to restore the student: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private void doDeduct() {
        try {
            for (STUDENT student : studentList) {
                studentEducation = null;
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                seQM.addWhereNullAnd("child");
                try {
                    STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }
                checkStudentEducationInfo();
                checkOrder();
                STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(STUDENT_STATUS.class, ID.valueOf(3));

                createNewOrder();
                studentEducation.setEndDate(endDateWidget.getValue());
                studentEducation.setStatus(ss);
                studentEducation.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                ordersTW.refresh();
                refreshOrderFiles(ID.valueOf(-1));
            }
            //			toolbarLeftHL.setVisible(false);
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            LOG.error("Unable to deduct the student: ", ex);
            Message.showError(ex.getMessage());
        }
    }

    private void saveOrder() {
        if (operType.equals(EOperType.OTHER) || operType.equals(EOperType.EDIT_ORDER)) {
            try {
                for (STUDENT student : studentList) {
                    studentEducation = null;
                    QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    seQM.addWhere("student", ECriteria.EQUAL, student.getId());
                    seQM.addWhereNullAnd("child");
                    try {
                        STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookupSingle(seQM);
                        studentEducation = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se1.getId());
                    } catch (Exception e) {
                        Message.showError(e.getMessage());
                    }
                    checkOrder();
                    if (operType.equals(EOperType.OTHER)) {
                        createNewOrder();
                    } else {
                        ORDER_DOC od = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(ORDER_DOC.class, ordersTW.getSelectedEntity().getId());
                        od.setOrderType((ORDER_TYPE) orderTypeWidget.getValue());
                        od.setStudyYear((STUDY_YEAR) orderStudyYearWidget.getValue());
                        od.setDescr(orderDescrWidget.getValue());
                        od.setHideTranscript(hideTranscriptWidget.getValue());
                        od.setUser(studentEducation.getStudent());
                        od.setDocumentNo(orderNoWidget.getValue());
                        od.setIssueDate(orderDateWidget.getValue());
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(od);

                        List<USER_DOCUMENT_FILE> udfList = new ArrayList<>();
                        Collection coll = orderFilesWidget.getContainerDataSource().getItemIds();
                        for (Object o : coll) {
                            FileBean fb = (FileBean) o;
                            if (fb.isNewFile()) {
                                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                                udf.setUserDocument(od);
                                udf.setFileName(fb.getFileName());
                                udf.setFileBytes(fb.getFileBytes());
                                udfList.add(udf);
                            }
                        }
                        if (!udfList.isEmpty()) {
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udfList);
                        }
                    }

                    ordersTW.refresh();
                    refreshOrderFiles(ID.valueOf(-1));
                }

                operType = null;
                rightVL.removeAllComponents();
                rightVL.setCaption("");
            } catch (Exception ex) {
                LOG.error("Unable to save order: ", ex);
                Message.showError(ex.getMessage());
            }
        }
    }

    private void createNewOrder() throws Exception {
        ORDER_DOC newOrder = new ORDER_DOC();
        newOrder.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID(USER_DOCUMENT.class));
        newOrder.setOrderType((ORDER_TYPE) orderTypeWidget.getValue());
        newOrder.setStudyYear((STUDY_YEAR) orderStudyYearWidget.getValue());
        newOrder.setDescr(orderDescrWidget.getValue());
        newOrder.setHideTranscript(hideTranscriptWidget.getValue());
        newOrder.setUser(studentEducation.getStudent());
        newOrder.setDocumentNo(orderNoWidget.getValue());
        newOrder.setIssueDate(orderDateWidget.getValue());
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(newOrder);

        List<USER_DOCUMENT_FILE> udfList = new ArrayList<>();
        Collection coll = orderFilesWidget.getContainerDataSource().getItemIds();
        for (Object o : coll) {
            FileBean fb = (FileBean) o;
            if (fb.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(newOrder);
                udf.setFileName(fb.getFileName());
                udf.setFileBytes(fb.getFileBytes());
                udfList.add(udf);
            }
        }

        if (!udfList.isEmpty()) {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udfList);
        }
    }

    private void checkOrder() throws Exception {
        boolean ok = (orderTypeWidget.getValue() != null && orderStudyYearWidget.getValue() != null &&
                (orderNoWidget.getValue() != null && !orderNoWidget.getValue().trim().isEmpty()) &&
                orderDateWidget.getValue() != null && (orderDescrWidget.getValue() != null &&
                !orderDescrWidget.getValue().trim().isEmpty()));

        if (!ok) {
            throw new Exception(getUILocaleUtil().getMessage("error.emptyvalue"));
        }
    }

    private void checkStudentEducationInfo() throws Exception {
        boolean ok = (facultyWidget.getValue() != null && specialityWidget.getValue() != null &&
                studyYearWidget.getValue() != null && languageWidget.getValue() != null &&
                educationTypeWidget.getValue() != null);
        if (ok) {
            if (operType.equals(EOperType.ALUMNUS) || operType.equals(EOperType.DEDUCT)) {
                ok = (endDateWidget.getValue() != null);
            }
        }

        if (!ok) {
            throw new Exception(getUILocaleUtil().getMessage("error.emptyvalue"));
        }
    }

    private void cancel() {
        if (operType.equals(EOperType.TRANSFER)) {
            transfer();
        } else if (operType.equals(EOperType.ACADEMIC_LEAVE)) {
            academicLeave();
        } else if (operType.equals(EOperType.RESTORE)) {
            restore();
        } else if (operType.equals(EOperType.ALUMNUS)) {
            alumnus();
        } else if (operType.equals(EOperType.DEDUCT)) {
            deduct();
        } else if (operType.equals(EOperType.OTHER)) {
            otherOrder();
        } else if (operType.equals(EOperType.EDIT_ORDER)) {
            editOrder((V_ORDER_DOC) ordersTW.getSelectedEntity());
        }
    }

    private class DownloadColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final USER_DOCUMENT_FILE udf = (USER_DOCUMENT_FILE) itemId;
            Button b = new NativeButton();
            b.setData(itemId);
            b.setCaption(getUILocaleUtil().getCaption("download"));
            FileDownloader fd = new OrderFileDownloader(new StreamResource(new StreamSource() {

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(udf.getFileBytes());
                }
            }, udf.getFileName()));
            fd.extend(b);

            return b;
        }

        private class OrderFileDownloader extends FileDownloader {

            OrderFileDownloader(StreamResource resource) {
                super(resource);
            }

            @Override
            public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path)
                    throws IOException {
                return super.handleConnectorRequest(request, response, path);
            }
        }
    }

    private class FacultyChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            DEPARTMENT faculty = (DEPARTMENT) ev.getProperty().getValue();
            if (faculty != null) {
                refreshSpecialities(faculty.getId());
            } else {
                refreshSpecialities(ID.valueOf(-1));
            }
        }
    }

    private class FileReceiver implements Upload.Receiver, Upload.SucceededListener,
            Upload.FailedListener, Upload.StartedListener {

        @Override
        public void uploadFailed(FailedEvent ev) {
            LOG.error("Order files: Unable to upload the file: " + ev.getFilename());
            LOG.error(ev.getReason().toString());
        }

        @Override
        public void uploadSucceeded(SucceededEvent ev) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream.read(bytes);

                FileBean fb = new FileBean();
                fb.setFileName(file.getName());
                fb.setFileBytes(bytes);
                fb.setNewFile(true);

                orderFilesWidget.getContainerDataSource().addItem(fb);
            } catch (Exception ex) {
                LOG.error("FileListWidget: Unable to read temp file: ", ex);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to close fileInputStream", ex);
                    }
                }
            }
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null;
            try {
                file = new File("/tmp/" + filename);
                if (file.exists()) {
                    file.delete();
                }
                fos = new FileOutputStream(file);
            } catch (Exception ex) {
                LOG.error("Cannot upload file: " + filename, ex);
            }

            return fos;
        }

        @Override
        public void uploadStarted(StartedEvent ev) {
            if (!FileListFieldModel.JPEG.equals(ev.getMIMEType())) {
                upload.interruptUpload();
                String message = getUILocaleUtil().getMessage("error.permittedfiletype");
                Message.showError(String.format(message, FileListFieldModel.JPEG));
            }
            long size = ev.getContentLength();
            long maxSize = FileListFieldModel.MAX_FILE_SIZE;
            if (size > maxSize) {
                LOG.error("Trying to upload a big file: Filename = " + ev.getFilename() + ", size = " + size);
                upload.interruptUpload();
                String message = getUILocaleUtil().getMessage("error.filetoobig");
                Message.showError(String.format(message, maxSize / 1024));
            }
        }
    }

}