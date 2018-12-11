package kz.halyqsoft.univercity.modules.student;

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
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.OperType;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek
 * @created Jul 4, 2016 12:06:41 PM
 */
@SuppressWarnings("serial")
final class EducationDetailPanel extends AbstractFormWidgetView {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private TableWidget ordersTW;
    private Table orderFilesTable;
    private DateField endDateField, orderDateField;
    private TextField orderNoTF;
    private CheckBox hideTranscriptChB;
    private TextArea orderDescrTA;
    private ListSelect orderFilesLS;
    private File file;
    private Upload upload;

    private HorizontalLayout toolbarLeftHL;
    private VerticalLayout rightVL;
    private VerticalLayout mainVL;

    private ComboBox facultyCB, specCB, groupCB, studyYearCB, languageCB, educationTypeCB;
    private ComboBox statusCB, orderTypeCB, orderStudyCB;

    private Label facultyLabel, specialityLabel, groupLabel, studyYearLabel, languageLabel;
    private Label educationTypeLabel, entryDateLabel, endDateLabel, statusLabel;

    private OperType operType;
    private StudentEdit studentEdit;
    private STUDENT_EDUCATION studentEducation;

    EducationDetailPanel(STUDENT_EDUCATION studentEducation, VerticalLayout mainVL, StudentEdit studentEdit) {
        this.studentEducation = studentEducation;
        this.mainVL = mainVL;
        this.studentEdit = studentEdit;

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

        facultyLabel = new Label();
        facultyLabel.addStyleName("bold");
        facultyLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "faculty"));
        facultyLabel.setValue(studentEducation.getFaculty().toString());
        educationFL.addComponent(facultyLabel);

        specialityLabel = new Label();
        specialityLabel.addStyleName("bold");
        specialityLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "speciality"));

        if (studentEducation.getSpeciality() == null) {
            educationFL.addComponent(specialityLabel);
        } else {
            specialityLabel.setValue(studentEducation.getSpeciality().toString());
            educationFL.addComponent(specialityLabel);
        }

        groupLabel = new Label();
        groupLabel.addStyleName("bold");
        groupLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "groups"));

        if (studentEducation.getGroups() != null) {
            groupLabel.setValue(studentEducation.getGroups().toString());
        }
        educationFL.addComponent(groupLabel);

        studyYearLabel = new Label();
        studyYearLabel.addStyleName("bold");
        studyYearLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
        studyYearLabel.setValue(studentEducation.getStudyYear().toString());
        educationFL.addComponent(studyYearLabel);

        languageLabel = new Label();
        languageLabel.addStyleName("bold");
        languageLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "language"));
        languageLabel.setValue(studentEducation.getLanguage().toString());
        educationFL.addComponent(languageLabel);

        educationTypeLabel = new Label();
        educationTypeLabel.addStyleName("bold");
        educationTypeLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "educationType"));
        educationTypeLabel.setValue(studentEducation.getEducationType().toString());
        educationFL.addComponent(educationTypeLabel);

        entryDateLabel = new Label();
        entryDateLabel.addStyleName("bold");
        entryDateLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "entryDate"));
        Date entryDate = studentEducation.getEntryDate();
        if (entryDate != null) {
            entryDateLabel.setValue(dateFormat.format(entryDate));
        }
        educationFL.addComponent(entryDateLabel);

        endDateLabel = new Label();
        endDateLabel.addStyleName("bold");
        endDateLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "endDate"));
        Date endDate = studentEducation.getEndDate();
        if (endDate != null) {
            endDateLabel.setValue(dateFormat.format(endDate));
        }
        educationFL.addComponent(endDateLabel);

        statusLabel = new Label();
        statusLabel.addStyleName("bold");
        statusLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "status"));
        statusLabel.setValue(studentEducation.getStatus().toString());
        educationFL.addComponent(statusLabel);

        leftVL.addComponent(educationFL);
        leftVL.setComponentAlignment(educationFL, Alignment.TOP_LEFT);

        /* 1.2. Toolbar */
        if (!studentEducation.getStatus().getId().equals(ID.valueOf(2))) {
            toolbarLeftHL = new HorizontalLayout();
            toolbarLeftHL.setSpacing(true);

            Button transferButton = new NativeButton();
            transferButton.setCaption(getUILocaleUtil().getCaption("transfer"));
            transferButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    transfer();
                }
            });
            toolbarLeftHL.addComponent(transferButton);

            Button academicLeaveButton = new NativeButton();
            academicLeaveButton.setCaption(getUILocaleUtil().getCaption("academic.leave"));
            academicLeaveButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    academicLeave();
                }
            });
            toolbarLeftHL.addComponent(academicLeaveButton);

            Button restoreButton = new NativeButton();
            restoreButton.setCaption(getUILocaleUtil().getCaption("restore"));
            restoreButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent ev) {
                    restore();
                }
            });
            toolbarLeftHL.addComponent(restoreButton);

            Button alumnusButton = new NativeButton();
            alumnusButton.setCaption(getUILocaleUtil().getCaption("alumnus"));
            alumnusButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    alumnus();
                }
            });
            toolbarLeftHL.addComponent(alumnusButton);

            Button deductButton = new NativeButton();
            deductButton.setCaption(getUILocaleUtil().getCaption("deduct"));
            deductButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    deduct();
                }
            });
            toolbarLeftHL.addComponent(deductButton);

            Button otherButton = new NativeButton();
            otherButton.setCaption(getUILocaleUtil().getCaption("other"));
            otherButton.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    otherOrder();
                }
            });
            toolbarLeftHL.addComponent(otherButton);

            leftVL.addComponent(toolbarLeftHL);
            leftVL.setComponentAlignment(toolbarLeftHL, Alignment.TOP_CENTER);
        }

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
        orderFilesTable.setColumnHeader("fileName", getUILocaleUtil().getEntityFieldLabel(USER_DOCUMENT_FILE.class, "fileName"));
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

        refreshOrderFiles(ID.valueOf(-1));
    }

    @Override
    public String getViewName() {
        return "educationInfo";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("student.education.info");
    }

    @Override
    protected AbstractCommonView getParentView() {
        mainVL.removeComponent(this);
        mainVL.addComponent(studentEdit);
        return null;
    }

    @Override
    protected String getTopTitle() {
        STUDENT student = studentEducation.getStudent();
        StringBuilder sb = new StringBuilder();
        sb.append(student.getLastName());
        sb.append(' ');
        sb.append(student.getFirstName());
        if (student.getMiddleName() != null) {
            sb.append(' ');
            sb.append(student.getMiddleName());
        }

        return sb.toString();
    }

    @Override
    protected boolean isTabbedView() {
        return false;
    }

    @Override
    public void initView(boolean readOnly) {
    }

    private void transfer() {
        operType = OperType.TRANSFER;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("transfer"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hlG = createGroupWidget();
        rightVL.addComponent(hlG);
        rightVL.setComponentAlignment(hlG, Alignment.TOP_RIGHT);

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
        operType = OperType.ACADEMIC_LEAVE;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("academic.leave"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hlG = createGroupWidget();
        rightVL.addComponent(hlG);
        rightVL.setComponentAlignment(hlG, Alignment.TOP_RIGHT);

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
        if (!(studentEducation.getStatus().getId().equals(ID.valueOf(3)) ||
                studentEducation.getStatus().getId().equals(ID.valueOf(5)))) {
            Message.showInfo(getUILocaleUtil().getMessage("unable.to.restore"));
        } else {
            operType = OperType.RESTORE;
            rightVL.removeAllComponents();
            rightVL.setCaption(getUILocaleUtil().getCaption("restore"));

            HorizontalLayout hl1 = createFacultyWidget();
            rightVL.addComponent(hl1);
            rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

            HorizontalLayout hl2 = createSpecialityWidget();
            rightVL.addComponent(hl2);
            rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

            HorizontalLayout hlG = createGroupWidget();
            rightVL.addComponent(hlG);
            rightVL.setComponentAlignment(hlG, Alignment.TOP_RIGHT);

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
    }

    private void alumnus() {
        operType = OperType.ALUMNUS;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("alumnus"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hlG = createGroupWidget();
        rightVL.addComponent(hlG);
        rightVL.setComponentAlignment(hlG, Alignment.TOP_RIGHT);

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
        operType = OperType.DEDUCT;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("deduct"));

        HorizontalLayout hl1 = createFacultyWidget();
        rightVL.addComponent(hl1);
        rightVL.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

        HorizontalLayout hl2 = createSpecialityWidget();
        rightVL.addComponent(hl2);
        rightVL.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

        HorizontalLayout hlG = createGroupWidget();
        rightVL.addComponent(hlG);
        rightVL.setComponentAlignment(hlG, Alignment.TOP_RIGHT);

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
        operType = OperType.OTHER;
        rightVL.removeAllComponents();
        rightVL.setCaption(getUILocaleUtil().getCaption("new.order"));

        VerticalLayout orderVL = createOrderWidget(null);
        rightVL.addComponent(orderVL);
        rightVL.setComponentAlignment(orderVL, Alignment.MIDDLE_CENTER);
    }

    private void editOrder(V_ORDER_DOC vod) {
        operType = OperType.EDIT_ORDER;
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
                if (!studentEducation.getStatus().getId().equals(ID.valueOf(3)) && !studentEducation.getStatus().getId().equals(ID.valueOf(4))) {
                    editOrder(vod);
                }
            }
        } else {
            super.handleEntityEvent(ev);
        }
    }

    private void refreshOrderFiles(ID docId) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<USER_DOCUMENT_FILE>(USER_DOCUMENT_FILE.class);
        udfQM.addWhere("userDocument", ECriteria.EQUAL, docId);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);
        try {
            List<USER_DOCUMENT_FILE> udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(udfQM);
            BeanItemContainer<USER_DOCUMENT_FILE> udfBIC = new BeanItemContainer<USER_DOCUMENT_FILE>(USER_DOCUMENT_FILE.class, udfList);
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
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
        facultyQM.addWhere("deleted", Boolean.FALSE);
        facultyQM.addWhereNullAnd("parent");
        facultyQM.addOrder("deptName");
        try {
            List<DEPARTMENT> facultyList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM);
            BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, facultyList);
            facultyCB = new ComboBox();
            facultyCB.setNewItemsAllowed(false);
            facultyCB.setFilteringMode(FilteringMode.STARTSWITH);
            facultyCB.setTextInputAllowed(true);
            facultyCB.setImmediate(true);
            facultyCB.setWidth(350, Unit.PIXELS);
            facultyCB.setContainerDataSource(facultyBIC);
            facultyCB.setValue(studentEducation.getFaculty());
            facultyCB.setEnabled(operType.equals(OperType.TRANSFER));
            facultyCB.addValueChangeListener(new FacultyChangeListener());
            facultyHL.addComponent(facultyCB);
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

        specCB = new ComboBox();
        specCB.setNewItemsAllowed(false);
        specCB.setFilteringMode(FilteringMode.CONTAINS);
        specCB.setTextInputAllowed(true);
        specCB.setWidth(350, Unit.PIXELS);
        refreshSpecialities(studentEducation.getFaculty().getId());
        specCB.setValue(studentEducation.getSpeciality());
        specCB.setEnabled(operType.equals(OperType.TRANSFER));
        specCB.addValueChangeListener(new SpecialityChangeListener());

        specialityHL.addComponent(specCB);

        return specialityHL;
    }


    private HorizontalLayout createGroupWidget() {
        HorizontalLayout groupHL = new HorizontalLayout();
        Label groupsLabel = new Label();
        groupsLabel.setWidthUndefined();
        groupsLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "groups"));
        groupHL.addComponent(groupsLabel);
        groupHL.addComponent(createRequiredIndicator());
        groupCB = new ComboBox();
        groupCB.setNewItemsAllowed(false);
        groupCB.setFilteringMode(FilteringMode.CONTAINS);
        groupCB.setTextInputAllowed(true);
        groupCB.setWidth(350, Unit.PIXELS);
        refreshGroups(studentEducation.getSpeciality().getId());
        groupCB.setValue(studentEducation.getGroups());
        groupCB.setEnabled(operType.equals(OperType.TRANSFER));
        groupHL.addComponent(groupCB);

        return groupHL;
    }

    private void refreshSpecialities(ID facultyId) {
        QueryModel<SPECIALITY> specialityQM = new QueryModel<SPECIALITY>(SPECIALITY.class);
        FromItem specialityChairFM = specialityQM.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class, "id");
        specialityQM.addWhere(specialityChairFM, "parent", ECriteria.EQUAL, facultyId);
        specialityQM.addWhereAnd("deleted", Boolean.FALSE);
        specialityQM.addOrder("specName");
        try {
            List<SPECIALITY> specialityList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM);
            BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<SPECIALITY>(SPECIALITY.class, specialityList);
            specCB.setContainerDataSource(specialityBIC);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load speciality list", ex);
        }
    }

    private void generateSubjectDifference(ID studentId) {

        List<SUBJECT> randSubject = new ArrayList<>();
        List<SUBJECT> subject = new ArrayList<>();

        Long cardId;

        String randStudentSql = "\n" +
                "SELECT student_education.student_id FROM student_education\n" +
                "  INNER JOIN student s2 ON student_education.student_id = s2.id\n" +
                "  INNER JOIN speciality s3 ON student_education.speciality_id = s3.id\n" +
                "WHERE s3.id=" + studentEducation.getSpeciality().getId() +
                " GROUP BY student_education.student_id\n" +
                "having count(student_education.student_id)=1\n" +
                "ORDER BY RANDOM()\n" +
                "LIMIT 1;";

        Long stuId = null;
        try {
            stuId = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(randStudentSql,
                    new HashMap<>());

        } catch (Exception e) {
            e.printStackTrace();
        }


        String randStudentSubjectSql = "select * \n" +
                "from SUBJECT t0\n" +
                "  inner join SEMESTER_SUBJECT t1 on t0.ID = t1.SUBJECT_ID\n" +
                "  inner join STUDENT_SUBJECT t2 on t1.ID = t2.SUBJECT_ID\n" +
                "  inner join STUDENT_EDUCATION t3 on t2.STUDENT_ID = t3.ID\n" +
                "  inner join SPECIALITY t4 on t3.SPECIALITY_ID = t4.ID\n" +
                "where t3.STUDENT_ID = "+stuId;

        Map<Integer, Object> params = new HashMap<>();

        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(randStudentSubjectSql, params);

            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    SUBJECT studentSubject = new SUBJECT();
                    studentSubject.setId(ID.valueOf((Long) oo[0]));
                    studentSubject.setNameKZ((String) oo[1]);
                    studentSubject.setNameEN((String) oo[2]);
                    studentSubject.setNameRU((String) oo[3]);

                    randSubject.add(studentSubject);

                }
            } else {
                System.out.print("nothing in randStudentSubject!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String studentSubjectSql = "select *\n" +
                "from SUBJECT t0\n" +
                "  inner join SEMESTER_SUBJECT t1 on t0.ID = t1.SUBJECT_ID\n" +
                "  inner join STUDENT_SUBJECT t2 on t1.ID = t2.SUBJECT_ID\n" +
                "  inner join STUDENT_EDUCATION t3 on t2.STUDENT_ID = t3.ID\n" +
                "  inner join SPECIALITY t4 on t3.SPECIALITY_ID = t4.ID\n" +
                "where t3.STUDENT_ID =" + studentId.getId();

        Map<Integer, Object> par = new HashMap<>();


        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(studentSubjectSql, par);

            // List qwe = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SEMESTER_DATA.class, ID.valueOf());

            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    SUBJECT studentSubject = new SUBJECT();
                    studentSubject.setId(ID.valueOf((Long) oo[0]));
                    studentSubject.setNameKZ((String) oo[1]);
                    studentSubject.setNameEN((String) oo[2]);
                    studentSubject.setNameRU((String) oo[3]);
                    subject.add(studentSubject);
                }
            } else {
                System.out.print("nothing in StudentSubject!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        randSubject.removeAll(subject);

        for (SUBJECT s : randSubject) {
            STUDENT_DIFFERENCE studentDifference = new STUDENT_DIFFERENCE();
            studentDifference.setStudentEducation(studentEducation);
            studentDifference.setSubject(s);
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        create(studentDifference);
            } catch (Exception e) {
                CommonUtils.showMessageAndWriteLog("Unable to save subject", e);
            }
        }
    }

    private void refreshGroups(ID specId) {
        QueryModel<GROUPS> groupsQM = new QueryModel<GROUPS>(GROUPS.class);
        FromItem groupChairFM = groupsQM.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        groupsQM.addWhere(groupChairFM, "id", ECriteria.EQUAL, specId);
        groupsQM.addWhereAnd("deleted", Boolean.FALSE);
        groupsQM.addOrder("name");
        try {
            List<GROUPS> groupsList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM);
            BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<GROUPS>(GROUPS.class, groupsList);
            groupCB.setContainerDataSource(groupsBIC);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load groups list", ex);
        }
    }

    private HorizontalLayout createYearLangEduTypeWidget() {
        HorizontalLayout hl = new HorizontalLayout();

        Label studyYearLabel = new Label();
        studyYearLabel.setWidthUndefined();
        studyYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
        hl.addComponent(studyYearLabel);
        hl.addComponent(createRequiredIndicator());
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        studyYearQM.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
        studyYearQM.addOrder("studyYear");
        try {
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM);
            BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<STUDY_YEAR>(STUDY_YEAR.class, studyYearList);
            studyYearCB = new ComboBox();
            studyYearCB.setNewItemsAllowed(false);
            studyYearCB.setFilteringMode(FilteringMode.OFF);
            studyYearCB.setTextInputAllowed(false);
            studyYearCB.setWidth(50, Unit.PIXELS);
            studyYearCB.setContainerDataSource(studyYearBIC);
            studyYearCB.setValue(studentEducation.getStudyYear());
            studyYearCB.setEnabled(operType.equals(OperType.TRANSFER));
            hl.addComponent(studyYearCB);

            Label languageLabel = new Label();
            languageLabel.setWidthUndefined();
            languageLabel.addStyleName("margin-left-30");
            languageLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "language"));
            hl.addComponent(languageLabel);
            hl.addComponent(createRequiredIndicator());
            QueryModel<LANGUAGE> languageQM = new QueryModel<LANGUAGE>(LANGUAGE.class);
            languageQM.addOrder("langName");
            List<LANGUAGE> languageList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(languageQM);
            BeanItemContainer<LANGUAGE> languageBIC = new BeanItemContainer<LANGUAGE>(LANGUAGE.class, languageList);
            languageCB = new ComboBox();
            languageCB.setNewItemsAllowed(false);
            languageCB.setFilteringMode(FilteringMode.OFF);
            languageCB.setTextInputAllowed(false);
            languageCB.setWidth(110, Unit.PIXELS);
            languageCB.setContainerDataSource(languageBIC);
            languageCB.setValue(studentEducation.getLanguage());
            languageCB.setEnabled(operType.equals(OperType.TRANSFER));
            hl.addComponent(languageCB);

            Label educationTypeLabel = new Label();
            educationTypeLabel.setWidthUndefined();
            educationTypeLabel.addStyleName("margin-left-30");
            educationTypeLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "educationType"));
            hl.addComponent(educationTypeLabel);
            hl.addComponent(createRequiredIndicator());
            QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<STUDENT_EDUCATION_TYPE>(STUDENT_EDUCATION_TYPE.class);
            educationTypeQM.addOrder("typeName");
            List<STUDENT_EDUCATION_TYPE> educationTypeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM);
            BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<STUDENT_EDUCATION_TYPE>(STUDENT_EDUCATION_TYPE.class, educationTypeList);
            educationTypeCB = new ComboBox();
            educationTypeCB.setNewItemsAllowed(false);
            educationTypeCB.setFilteringMode(FilteringMode.OFF);
            educationTypeCB.setTextInputAllowed(false);
            educationTypeCB.setWidth(110, Unit.PIXELS);
            educationTypeCB.setContainerDataSource(educationTypeBIC);
            educationTypeCB.setValue(studentEducation.getEducationType());
            educationTypeCB.setEnabled(operType.equals(OperType.TRANSFER));
            hl.addComponent(educationTypeCB);
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
        DateField entryDateField = new DateField();
        entryDateField.setWidth(100, Unit.PIXELS);
        entryDateField.setEnabled(false);
        entryDateField.setValue(studentEducation.getEntryDate());
        hl.addComponent(entryDateField);

        Label endDateLabel = new Label();
        endDateLabel.setWidthUndefined();
        endDateLabel.addStyleName("margin-left-30");
        endDateLabel.setValue(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "endDate"));
        hl.addComponent(endDateLabel);
//        hl.addComponent(createRequiredIndicator());
        endDateField = new DateField();
        endDateField.setWidth(100, Unit.PIXELS);
        endDateField.setEnabled(operType.equals(OperType.ALUMNUS) || operType.equals(OperType.DEDUCT));
        if (operType.equals(OperType.ALUMNUS) || operType.equals(OperType.DEDUCT)) {
            endDateField.setValue(new Date());
        } else {
            endDateField.setValue(studentEducation.getEndDate());
        }
        hl.addComponent(endDateField);

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
        QueryModel<STUDENT_STATUS> statusQM = new QueryModel<STUDENT_STATUS>(STUDENT_STATUS.class);
        statusQM.addOrder("statusName");
        try {
            List<STUDENT_STATUS> statusList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(statusQM);
            BeanItemContainer<STUDENT_STATUS> statusBIC = new BeanItemContainer<STUDENT_STATUS>(STUDENT_STATUS.class, statusList);
            statusCB = new ComboBox();
            statusCB.setNewItemsAllowed(false);
            statusCB.setFilteringMode(FilteringMode.OFF);
            statusCB.setTextInputAllowed(false);
            statusCB.setWidth(130, Unit.PIXELS);
            statusCB.setContainerDataSource(statusBIC);
            statusCB.setValue(studentEducation.getStatus());
            statusCB.setEnabled(false);
            hl.addComponent(statusCB);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student status list", ex);
        }

        return hl;
    }

    private VerticalLayout createOrderWidget(V_ORDER_DOC vod) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(true);
        if (!operType.equals(OperType.OTHER) && !operType.equals(OperType.EDIT_ORDER)) {
            vl.setCaption(getUILocaleUtil().getCaption("new.order"));
        }

        HorizontalLayout hl1 = new HorizontalLayout();

        Label orderTypeLabel = new Label();
        orderTypeLabel.setWidthUndefined();
        orderTypeLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "orderType"));
        hl1.addComponent(orderTypeLabel);
        hl1.addComponent(createRequiredIndicator());
        QueryModel<ORDER_TYPE> orderTypeQM = new QueryModel<>(ORDER_TYPE.class);
        if (operType.equals(OperType.OTHER)) {
            List<ID> idList = new ArrayList<>();
            idList.add(ID.valueOf(1));
            idList.add(ID.valueOf(2));
            orderTypeQM.addWhereIn("id", idList);
        }
        orderTypeQM.addOrder("typeName");
        try {
            List<ORDER_TYPE> orderTypeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(orderTypeQM);
            BeanItemContainer<ORDER_TYPE> orderTypeBIC = new BeanItemContainer<>(ORDER_TYPE.class, orderTypeList);
            orderTypeCB = new ComboBox();
            orderTypeCB.setNewItemsAllowed(false);
            orderTypeCB.setFilteringMode(FilteringMode.OFF);
            orderTypeCB.setTextInputAllowed(false);
            orderTypeCB.setWidth(180, Unit.PIXELS);
            orderTypeCB.setContainerDataSource(orderTypeBIC);
            if (!operType.equals(OperType.OTHER) && !operType.equals(OperType.EDIT_ORDER)) {
                ORDER_TYPE orderType = null;
                for (ORDER_TYPE ot : orderTypeList) {
                    if (operType.equals(OperType.TRANSFER)) {
                        if (ot.getId().equals(ID.valueOf(3))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(OperType.ACADEMIC_LEAVE)) {
                        if (ot.getId().equals(ID.valueOf(4))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(OperType.RESTORE)) {
                        if (ot.getId().equals(ID.valueOf(5))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(OperType.ALUMNUS)) {
                        if (ot.getId().equals(ID.valueOf(7))) {
                            orderType = ot;
                            break;
                        }
                    } else if (operType.equals(OperType.DEDUCT)) {
                        if (ot.getId().equals(ID.valueOf(8))) {
                            orderType = ot;
                            break;
                        }
                    }
                }

                if (orderType != null) {
                    orderTypeCB.setValue(orderType);
                }
                orderTypeCB.setEnabled(false);
            } else if (operType.equals(OperType.EDIT_ORDER)) {
                vod = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_ORDER_DOC.class, vod.getId());
                orderTypeCB.setValue(vod.getOrderType());
            }
            hl1.addComponent(orderTypeCB);

            Label studyYearLabel = new Label();
            studyYearLabel.setWidthUndefined();
            studyYearLabel.addStyleName("margin-left-30");
            studyYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "studyYear"));
            hl1.addComponent(studyYearLabel);
            hl1.addComponent(createRequiredIndicator());
            QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
            studyYearQM.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM);
            BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<STUDY_YEAR>(STUDY_YEAR.class, studyYearList);
            orderStudyCB = new ComboBox();
            orderStudyCB.setNewItemsAllowed(false);
            orderStudyCB.setFilteringMode(FilteringMode.OFF);
            orderStudyCB.setTextInputAllowed(false);
            orderStudyCB.setWidth(50, Unit.PIXELS);
            orderStudyCB.setContainerDataSource(studyYearBIC);
            orderStudyCB.setValue(studentEducation.getStudyYear());
            orderStudyCB.setEnabled(false);
            hl1.addComponent(orderStudyCB);

            vl.addComponent(hl1);
            vl.setComponentAlignment(hl1, Alignment.TOP_RIGHT);

            HorizontalLayout hl2 = new HorizontalLayout();

            Label orderNoLabel = new Label();
            orderNoLabel.setWidthUndefined();
            orderNoLabel.setValue(getUILocaleUtil().getCaption("order.no"));
            hl2.addComponent(orderNoLabel);
            hl2.addComponent(createRequiredIndicator());
            orderNoTF = new TextField();
            orderNoTF.setWidth(120, Unit.PIXELS);
            if (operType.equals(OperType.EDIT_ORDER)) {
                orderNoTF.setValue(vod.getDocumentNo());
            }
            hl2.addComponent(orderNoTF);

            Label orderDateLabel = new Label();
            orderDateLabel.setWidthUndefined();
            orderDateLabel.addStyleName("margin-left-30");
            orderDateLabel.setValue(getUILocaleUtil().getCaption("order.date"));
            hl2.addComponent(orderDateLabel);
            hl2.addComponent(createRequiredIndicator());
            orderDateField = new DateField();
            orderDateField.setWidth(100, Unit.PIXELS);
            if (operType.equals(OperType.EDIT_ORDER)) {
                orderDateField.setValue(vod.getIssueDate());
            }
            hl2.addComponent(orderDateField);

            Label hideTranscriptLabel = new Label();
            hideTranscriptLabel.setWidthUndefined();
            hideTranscriptLabel.addStyleName("margin-left-30");
            hideTranscriptLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "hideTranscript"));
            hl2.addComponent(hideTranscriptLabel);
            hideTranscriptChB = new CheckBox();
            if (operType.equals(OperType.EDIT_ORDER)) {
                hideTranscriptChB.setValue(vod.isHideTranscript());
            } else {
                hideTranscriptChB.setValue(Boolean.FALSE);
            }
            hl2.addComponent(hideTranscriptChB);

            vl.addComponent(hl2);
            vl.setComponentAlignment(hl2, Alignment.TOP_RIGHT);

            HorizontalLayout hl3 = new HorizontalLayout();

            Label orderDescrLabel = new Label();
            orderDescrLabel.setWidthUndefined();
            orderDescrLabel.setValue(getUILocaleUtil().getEntityFieldLabel(ORDER_DOC.class, "descr"));
            hl3.addComponent(orderDescrLabel);
            hl3.addComponent(createRequiredIndicator());
            orderDescrTA = new TextArea();
            orderDescrTA.setWordwrap(true);
            orderDescrTA.setHeight(45, Unit.PIXELS);
            orderDescrTA.setWidth(350, Unit.PIXELS);
            if (operType.equals(OperType.EDIT_ORDER)) {
                orderDescrTA.setValue(vod.getDescr());
            }
            hl3.addComponent(orderDescrTA);

            vl.addComponent(hl3);
            vl.setComponentAlignment(hl3, Alignment.TOP_RIGHT);

            HorizontalLayout hl4 = new HorizontalLayout();

            Label orderFilesLabel = new Label();
            orderFilesLabel.setWidthUndefined();
            orderFilesLabel.setValue(getUILocaleUtil().getEntityLabel(USER_DOCUMENT_FILE.class));
            hl4.addComponent(orderFilesLabel);
            orderFilesLS = new ListSelect();
            orderFilesLS.setRows(4);
            orderFilesLS.setWidth(350, Unit.PIXELS);
            orderFilesLS.setNullSelectionAllowed(false);
            List<FileBean> fbList = new ArrayList<FileBean>();
            if (operType.equals(OperType.EDIT_ORDER)) {
                QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<USER_DOCUMENT_FILE>(USER_DOCUMENT_FILE.class);
                udfQM.addWhere("userDocument", ECriteria.EQUAL, vod.getId());
                udfQM.addWhereAnd("deleted", Boolean.FALSE);
                List<USER_DOCUMENT_FILE> udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(udfQM);
                for (USER_DOCUMENT_FILE udf : udfList) {
                    FileBean fb = new FileBean();
                    fb.setId(udf.getId());
                    fb.setFileName(udf.getFileName());
                    fb.setFileBytes(udf.getFileBytes());
                    fbList.add(fb);
                }
            }
            BeanItemContainer<FileBean> orderFilesBIC = new BeanItemContainer<FileBean>(FileBean.class, fbList);
            orderFilesLS.setContainerDataSource(orderFilesBIC);
            hl4.addComponent(orderFilesLS);
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
        if (operType.equals(OperType.TRANSFER)) {
            doTransfer();
        } else if (operType.equals(OperType.ACADEMIC_LEAVE)) {
            doAcademicLeave();
        } else if (operType.equals(OperType.RESTORE)) {
            doRestore();
        } else if (operType.equals(OperType.ALUMNUS)) {
            doAlumnus();
        } else if (operType.equals(OperType.DEDUCT)) {
            doDeduct();
        } else if (operType.equals(OperType.OTHER) || operType.equals(OperType.EDIT_ORDER)) {
            saveOrder();
        }
    }

    private void doTransfer() {
        try {
            checkStudentEducationInfo();
            checkOrder();
            STUDENT_STATUS ss = null;
            if (!studyYearCB.getValue().equals(studentEducation.getStudyYear())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(6));
            } else if (!facultyCB.getValue().equals(studentEducation.getFaculty()) || !specCB.getValue().equals(studentEducation.getSpeciality())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(4));
            } else if (!specCB.getValue().equals(studentEducation.getSpeciality()) || !groupCB.getValue().equals(studentEducation.getGroups())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(10));
            } else if (!educationTypeCB.getValue().equals(studentEducation.getEducationType())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(7));
            } else if (!languageCB.getValue().equals(studentEducation.getLanguage())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(8));
            } else if (!groupCB.getValue().equals(studentEducation.getGroups())) {
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(10));
            }

            if (ss == null) {
                Message.showInfo(getUILocaleUtil().getMessage("unable.to.transfer"));
            } else {
                SPECIALITY speciality = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SPECIALITY.class, ((SPECIALITY) specCB.getValue()).getId());
                GROUPS group = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(GROUPS.class, ((GROUPS) groupCB.getValue()).getId());
                STUDENT_EDUCATION newSE = new STUDENT_EDUCATION();

                newSE.setStudent(studentEducation.getStudent());
                newSE.setFaculty((DEPARTMENT) facultyCB.getValue());
                newSE.setGroups(group);
                newSE.setChair(speciality.getDepartment());
                newSE.setSpeciality(speciality);
                newSE.setStudyYear((STUDY_YEAR) studyYearCB.getValue());
                newSE.setLanguage((LANGUAGE) languageCB.getValue());
                newSE.setEducationType((STUDENT_EDUCATION_TYPE) educationTypeCB.getValue());
                newSE.setEntryDate(studentEducation.getEntryDate());
                newSE.setEndDate(endDateField.getValue());
                newSE.setStatus(studentEducation.getStatus());
                newSE.setCreated(new Date());

                generateSubjectDifference(studentEducation.getStudent().getId());
                createNewOrder();
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newSE);
                studentEducation.setStatus(ss);
                studentEducation.setChild(newSE);
                studentEducation.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                if (newSE.getStudyYear().getStudyYear() > studentEducation.getStudyYear().getStudyYear()) {//TODO check
                    int i = newSE.getStudyYear().getStudyYear() - 1;
                    ENTRANCE_YEAR ey = newSE.getStudent().getEntranceYear();
                    QueryModel<ENTRANCE_YEAR> syQM = new QueryModel<>(ENTRANCE_YEAR.class);
                    syQM.addWhere("beginYear", ECriteria.EQUAL, ey.getBeginYear() + i);
                    syQM.addWhereAnd("endYear", ECriteria.EQUAL, ey.getEndYear() + i);
                    try {
                        ENTRANCE_YEAR sy = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookupSingle(syQM);
                        QueryModel<STUDENT_SUBJECT> ssQM = new QueryModel<>(STUDENT_SUBJECT.class);
                        FromItem ssFI = ssQM.addJoin(EJoin.INNER_JOIN, "semesterData", SEMESTER_DATA.class, "id");
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
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                        }
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to transfer student subjects", ex);
                    }
                }

                studentEducation = newSE;

                facultyLabel.setValue(studentEducation.getFaculty().toString());
                specialityLabel.setValue(studentEducation.getSpeciality().toString());
                groupLabel.setValue(studentEducation.getGroups().toString());
                studyYearLabel.setValue(studentEducation.getStudyYear().toString());
                languageLabel.setValue(studentEducation.getLanguage().toString());
                educationTypeLabel.setValue(studentEducation.getEducationType().toString());

                ordersTW.refresh();
                refreshOrderFiles(ID.valueOf(-1));

                operType = null;
                rightVL.removeAllComponents();
                rightVL.setCaption("");
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to transfer the student", ex);
        }
    }

    private void doAcademicLeave() {
        try {
            checkOrder();
            STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(5));

            createNewOrder();
            studentEducation.setStatus(ss);
            studentEducation.setUpdated(new Date());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

            statusLabel.setValue(studentEducation.getStatus().toString());

            ordersTW.refresh();
            refreshOrderFiles(ID.valueOf(-1));

            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to academic leave the student", ex);
        }
    }

    private void doRestore() {
        try {
            checkOrder();
            STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(1));

            createNewOrder();
            studentEducation.setStatus(ss);
            studentEducation.setUpdated(new Date());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

            statusLabel.setValue(studentEducation.getStatus().toString());

            ordersTW.refresh();
            refreshOrderFiles(ID.valueOf(-1));

            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to restore the student", ex);
        }
    }

    private void doAlumnus() {
        try {
            checkStudentEducationInfo();
            checkOrder();
            STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(2));

            createNewOrder();
            studentEducation.setEndDate(endDateField.getValue());
            studentEducation.setStatus(ss);
            studentEducation.setUpdated(new Date());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

            endDateLabel.setValue(dateFormat.format(studentEducation.getEndDate()));
            statusLabel.setValue(studentEducation.getStatus().toString());

            ordersTW.refresh();
            refreshOrderFiles(ID.valueOf(-1));

            toolbarLeftHL.setVisible(false);
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to restore the student", ex);
        }
    }

    private void doDeduct() {
        try {
            checkStudentEducationInfo();
            checkOrder();
            STUDENT_STATUS ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(3));

            createNewOrder();
            studentEducation.setEndDate(endDateField.getValue());
            studentEducation.setStatus(ss);
            studentEducation.setUpdated(new Date());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

            endDateLabel.setValue(dateFormat.format(studentEducation.getEndDate()));
            statusLabel.setValue(studentEducation.getStatus().toString());

            ordersTW.refresh();
            refreshOrderFiles(ID.valueOf(-1));

            toolbarLeftHL.setVisible(false);
            operType = null;
            rightVL.removeAllComponents();
            rightVL.setCaption("");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to restore the student", ex);
        }
    }

    private void saveOrder() {
        if (operType.equals(OperType.OTHER) || operType.equals(OperType.EDIT_ORDER)) {
            try {
                checkOrder();
                if (operType.equals(OperType.OTHER)) {
                    createNewOrder();
                } else {
                    ORDER_DOC od = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ORDER_DOC.class, ordersTW.getSelectedEntity().getId());
                    od.setOrderType((ORDER_TYPE) orderTypeCB.getValue());
                    od.setStudyYear((STUDY_YEAR) orderStudyCB.getValue());
                    od.setDescr(orderDescrTA.getValue());
                    od.setHideTranscript(hideTranscriptChB.getValue());
                    od.setUser(studentEducation.getStudent());
                    od.setDocumentNo(orderNoTF.getValue());
                    od.setIssueDate(orderDateField.getValue());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(od);

                    List<USER_DOCUMENT_FILE> udfList = new ArrayList<USER_DOCUMENT_FILE>();
                    Collection coll = orderFilesLS.getContainerDataSource().getItemIds();
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

                operType = null;
                rightVL.removeAllComponents();
                rightVL.setCaption("");
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to save order", ex);
            }
        }
    }

    private void createNewOrder() throws Exception {
        ORDER_DOC newOrder = new ORDER_DOC();
        newOrder.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID(USER_DOCUMENT.class));
        newOrder.setOrderType((ORDER_TYPE) orderTypeCB.getValue());
        newOrder.setStudyYear((STUDY_YEAR) orderStudyCB.getValue());
        newOrder.setDescr(orderDescrTA.getValue());
        newOrder.setHideTranscript(hideTranscriptChB.getValue());
        newOrder.setUser(studentEducation.getStudent());
        newOrder.setDocumentNo(orderNoTF.getValue());
        newOrder.setIssueDate(orderDateField.getValue());
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(newOrder);

        List<USER_DOCUMENT_FILE> udfList = new ArrayList<USER_DOCUMENT_FILE>();
        Collection coll = orderFilesLS.getContainerDataSource().getItemIds();
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
        boolean ok = (orderTypeCB.getValue() != null && orderStudyCB.getValue() != null &&
                (orderNoTF.getValue() != null && !orderNoTF.getValue().trim().isEmpty()) &&
                (orderDescrTA.getValue() != null &&
                        !orderDescrTA.getValue().trim().isEmpty()));

        if (!ok) {
            throw new Exception(getUILocaleUtil().getMessage("error.emptyvalue"));
        }
    }

    private void checkStudentEducationInfo() throws Exception {
        boolean ok = (facultyCB.getValue() != null && specCB.getValue() != null
                && groupCB.getValue() != null
                && studyYearCB.getValue() != null && languageCB.getValue() != null
                && educationTypeCB.getValue() != null);
        if (ok) {
            if (operType.equals(OperType.ALUMNUS) || operType.equals(OperType.DEDUCT)) {
                ok = (endDateField.getValue() != null);
            }
        }

        if (!ok) {
            throw new Exception(getUILocaleUtil().getMessage("error.emptyvalue"));
        }
    }

    private void cancel() {
        if (operType.equals(OperType.TRANSFER)) {
            transfer();
        } else if (operType.equals(OperType.ACADEMIC_LEAVE)) {
            academicLeave();
        } else if (operType.equals(OperType.RESTORE)) {
            restore();
        } else if (operType.equals(OperType.ALUMNUS)) {
            alumnus();
        } else if (operType.equals(OperType.DEDUCT)) {
            deduct();
        } else if (operType.equals(OperType.OTHER)) {
            otherOrder();
        } else if (operType.equals(OperType.EDIT_ORDER)) {
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

            public OrderFileDownloader(StreamResource resource) {
                super(resource);
            }

            @Override
            public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
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

    private class SpecialityChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {

            SPECIALITY speciality = (SPECIALITY) ev.getProperty().getValue();
//            STUDENT student = (STUDENT) ev.getProperty().getValue();
            if (speciality != null) {
                refreshGroups(speciality.getId());
                //    generateSubjectDifference(student.getId());
            } else {
                refreshGroups(ID.valueOf(-1));
                // generateSubjectDifference(ID.valueOf(-1));
            }
        }
    }

    private class FileReceiver implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener, Upload.StartedListener {

        @Override
        public void uploadFailed(FailedEvent ev) {
            CommonUtils.LOG.error("Order files: Unable to upload the file: " + ev.getFilename());
            CommonUtils.LOG.error(String.valueOf(ev.getReason()));
        }

        @Override
        public void uploadSucceeded(SucceededEvent ev) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fis.read(bytes);

                FileBean fb = new FileBean();
                fb.setFileName(file.getName());
                fb.setFileBytes(bytes);
                fb.setNewFile(true);

                orderFilesLS.getContainerDataSource().addItem(fb);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("FileListWidget: Unable to read temp file", ex);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null;
            try {
                file = new File("/tmp/files/" + filename);
//                file = new File("C:/Users/Omarbek/IdeaProjects/univercity/tmp/files/" + filename);
                if (file.exists()) {
                    file.delete();
                }
                fos = new FileOutputStream(file);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Cannot upload file: " + filename, ex);
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
                CommonUtils.LOG.error("Trying to upload a big file: Filename = " + ev.getFilename() + ", size = " + size);
                upload.interruptUpload();
                String message = getUILocaleUtil().getMessage("error.filetoobig");
                Message.showError(String.format(message, maxSize / 1024));
            }
        }
    }
}
