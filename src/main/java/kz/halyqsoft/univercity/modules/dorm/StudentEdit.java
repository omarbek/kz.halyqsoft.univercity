package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.utility.DateUtils;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetEvent;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetListener;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * @author Dinassil Omarbek
 * @created May 24, 2017 4:10:38 PM
 */
@SuppressWarnings({"serial"})
public final class StudentEdit extends AbstractFormWidgetView implements PhotoWidgetListener {

    private final DateFormat uriDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    private final AbstractFormWidget baseDataFW;
    private USER_PHOTO userPhoto;
    private byte[] userPhotoBytes;
    private Label lockLabel, lockReasonLabel;
    private Button orderAndSkipB;
    private WarrantAndSkipOfStudents orderAndSkipSource;
    private STUDENT student;
    private ComboBox violationTypeCB, outTypeCB, buildingCB, roomCB;
    private TextArea violationDescrTA, outDescrTA;
    private Label costL;
    private DateField violationDF, outDF, inOrMoveDF;
    private String faculty;
    private String year;
    private boolean isInDorm;

    public StudentEdit(final FormModel baseDataFM) throws Exception {
        super();
        setBackButtonVisible(false);

        orderAndSkipSource = new WarrantAndSkipOfStudents();

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        // hl.setSizeFull();

        baseDataFM.setButtonsVisible(false);
        baseDataFM.getFieldModel("academicStatus").setInEdit(true);
        baseDataFM.getFieldModel("academicStatus").setInView(true);

        baseDataFM.getFieldModel("code").setReadOnly(true);

        FKFieldModel citizenshipFM = (FKFieldModel) baseDataFM.getFieldModel("citizenship");
        citizenshipFM.getQueryModel().addOrder("countryName");

        baseDataFM.getFieldModel("phoneInternal").setInEdit(false);

        FKFieldModel levelFM = (FKFieldModel) baseDataFM.getFieldModel("level");
        levelFM.setReadOnlyFixed(true);

        FKFieldModel categoryFM = (FKFieldModel) baseDataFM.getFieldModel("category");
        QueryModel categoryQM = categoryFM.getQueryModel();
        categoryQM.addWhere("id", ECriteria.NOT_EQUAL, ID.valueOf(1));

        FKFieldModel entranceYearFM = (FKFieldModel) baseDataFM.getFieldModel("entranceYear");
        entranceYearFM.setReadOnlyFixed(true);

        FKFieldModel advisorFM = (FKFieldModel) baseDataFM.getFieldModel("advisor");
        advisorFM.setSelectType(ESelectType.CUSTOM_GRID);
        advisorFM.setDialogHeight(400);
        advisorFM.setDialogWidth(600);
        QueryModel advisorQM = advisorFM.getQueryModel();
        try {
            TextField fioTF = new TextField();
            fioTF.setImmediate(true);
            fioTF.setWidth(400, Unit.PIXELS);

            QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
            chairQM.addWhereNotNull("parent");
            chairQM.addWhereAnd("deleted", Boolean.FALSE);
            chairQM.addOrder("deptName");
            BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
            ComboBox chairCB = new ComboBox();
            chairCB.setContainerDataSource(chairBIC);
            chairCB.setImmediate(true);
            chairCB.setNullSelectionAllowed(true);
            chairCB.setTextInputAllowed(true);
            chairCB.setFilteringMode(FilteringMode.CONTAINS);
            chairCB.setPageLength(0);
            chairCB.setWidth(400, Unit.PIXELS);

            QueryModel<POST> postQM = new QueryModel<>(POST.class);
            postQM.addOrder("postName");
            BeanItemContainer<POST> postBIC = new BeanItemContainer<>(POST.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(postQM));
            ComboBox postCB = new ComboBox();
            postCB.setContainerDataSource(postBIC);
            postCB.setImmediate(true);
            postCB.setNullSelectionAllowed(true);
            postCB.setTextInputAllowed(true);
            postCB.setFilteringMode(FilteringMode.STARTSWITH);
            postCB.setPageLength(0);
            postCB.setWidth(400, Unit.PIXELS);

            CustomGridSelectDialog cgsd = advisorFM.getCustomGridSelectDialog();
            cgsd.getSelectModel().setMultiSelect(false);
            cgsd.getFilterModel().addFilter("department", chairCB);
            cgsd.getFilterModel().addFilter("post", postCB);
            cgsd.getFilterModel().addFilter("fio", fioTF);
            cgsd.initFilter();
        } catch (Exception ex) {
            LOG.error("Unable to initialize custom grid dialog: ", ex);
            Message.showError(ex.toString());
        }

        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);
        hl.addComponent(baseDataFW);
        hl.setComponentAlignment(baseDataFW, Alignment.TOP_LEFT);

        if (!baseDataFM.isCreateNew()) {
            QueryModel<USER_PHOTO> qmUserPhoto = new QueryModel<>(USER_PHOTO.class);
            try {
                qmUserPhoto.addWhere("user", ECriteria.EQUAL, baseDataFM.getEntity().getId());
                userPhoto = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmUserPhoto);
                if (userPhoto != null) {
                    userPhotoBytes = userPhoto.getPhoto();
                }
            } catch (Exception ex) {
                LOG.error("Unable to load user photo: ", ex);
            }
        }

        VerticalLayout rightContent = new VerticalLayout();
        rightContent.setSpacing(true);
        rightContent.setSizeFull();

        PhotoWidget userPW = new PhotoWidget(userPhotoBytes, baseDataFM.isReadOnly());
        userPW.setPhotoHeight(290, Unit.PIXELS);
        userPW.setPhotoWidth(230, Unit.PIXELS);
        userPW.setSaveButtonVisible(false);
        userPW.addListener(this);
        rightContent.addComponent(userPW);
        rightContent.setComponentAlignment(userPW, Alignment.TOP_CENTER);

		/* Education info */
        student = (STUDENT) baseDataFM.getEntity();
        QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
        seQM.addWhere("student", ECriteria.EQUAL, student.getId());
        seQM.addWhereNullAnd("child");
        try {
            STUDENT_EDUCATION se = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(seQM);
            final STUDENT_EDUCATION studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se.getId());

            FormLayout educationFL = new FormLayout();
            educationFL.setMargin(new MarginInfo(false, false, false, true));
            educationFL.setCaption(getUILocaleUtil().getCaption("education.info"));

            Label label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "faculty"));
            faculty = studentEducation.getFaculty().toString();
            label.setValue(studentEducation.getFaculty().toString());
            educationFL.addComponent(label);

            label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "speciality"));
            label.setValue(studentEducation.getSpeciality().toString());
            educationFL.addComponent(label);

            label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
            year = studentEducation.getStudyYear().toString();
            label.setValue(studentEducation.getStudyYear().toString());
            educationFL.addComponent(label);

            label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "status"));
            label.setValue(studentEducation.getStatus().toString());
            educationFL.addComponent(label);

            lockLabel = new Label();
            lockLabel.addStyleName("bold");
            lockLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class, "locked"));
            if (student.isLocked()) {
                lockLabel.setValue(getUILocaleUtil().getCaption("yes"));
            } else {
                lockLabel.setValue(getUILocaleUtil().getCaption("no"));
            }
            educationFL.addComponent(lockLabel);

            lockReasonLabel = new Label();
            lockReasonLabel.addStyleName("bold");
            lockReasonLabel.setCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class, "lockReason"));
            if (student.isLocked()) {
                LOCK_REASON lr = student.getLockReason();
                if (lr != null) {
                    lockReasonLabel.setValue(lr.toString());
                }
            }
            educationFL.addComponent(lockReasonLabel);

            rightContent.addComponent(educationFL);
            rightContent.setComponentAlignment(educationFL, Alignment.MIDDLE_CENTER);
        } catch (Exception ex) {
            LOG.error("Unable to load education info: ", ex);
            Message.showError(ex.toString());
        }

        hl.addComponent(rightContent);
        hl.setComponentAlignment(rightContent, Alignment.TOP_RIGHT);
        hl.setExpandRatio(rightContent, 1);
        content.addComponent(hl);
        content.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        content.setExpandRatio(hl, 1);
        //
        HorizontalLayout buttonsHL = createButtonPanel();

        setIsInDorm();

        if (isInDorm) {
            orderAndSkipB = createOrderAndSkipButton((STUDENT) baseDataFM.getEntity());
            buttonsHL.addComponent(orderAndSkipB);
            buttonsHL.setComponentAlignment(orderAndSkipB, Alignment.MIDDLE_RIGHT);
        }

        content.addComponent(buttonsHL);
        content.setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);
        //
        orderAndSkipSource.setUserPhotoBytes(userPhotoBytes);
        orderAndSkipSource.setStudent(student.getId());

        getTabSheet().addTab(content, getMasterTabTitle());

        createComeInOrMoveTab(isInDorm);
        if (isInDorm) {
            createComeOutTab();
            createRemarkTab();
        }
    }

    private void setIsInDorm() {
        DORM_STUDENT dormStudent = null;
        try {
            String sql = "SELECT t0.* " +
                    "FROM DORM_STUDENT t0 INNER JOIN STUDENT_EDUCATION t1 ON t0.STUDENT_ID = t1.ID " +
                    "WHERE t1.STUDENT_ID = ?1 AND t1.CHILD_ID IS NULL AND t0.CHECK_OUT_DATE IS NULL AND " +
                    "t0.CHECK_IN_DATE IS NOT NULL AND t0.request_status_id = 1 and " +
                    "      t0.DELETED = FALSE;";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, student.getId().getId());
            dormStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params,
                    DORM_STUDENT.class);
        } catch (NoResultException e) {
            dormStudent = null;
        } catch (Exception ex) {
            LOG.error("Unable to get dormStudent: ", ex);
            Message.showError(ex.toString());
        }
        isInDorm = dormStudent != null;
    }

    private Button createOrderAndSkipButton(final STUDENT student) {
        orderAndSkipB = new Button(getUILocaleUtil().getCaption("warrant.and.skip"));

        String filename = student.getFirstNameEN() + student.getLastNameEN() +
                uriDateFormat.format(Calendar.getInstance().getTime()) + ".pdf";
        final StreamResource resource = new StreamResource(orderAndSkipSource, filename);
        resource.setMIMEType("application/pdf");
        final BrowserWindowOpener opener = new BrowserWindowOpener(resource);
        opener.extend(orderAndSkipB);

        orderAndSkipB.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                opener.setResource(null);
                opener.setResource(resource);
            }
        });
        return orderAndSkipB;
    }

    @Override
    public void handlePhotoWidgetEvent(PhotoWidgetEvent ev) {
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "StudentEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        FormModel fm = baseDataFW.getWidgetModel();
        if (fm.isCreateNew()) {
            return getUILocaleUtil().getCaption("student.new");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(getUILocaleUtil().getCaption("student.view"));
            sb.append(": ");
            try {
                sb.append(baseDataFW.getWidgetModel().getEntity().toString());
            } catch (Exception ex) {
                LOG.error("Unable to create view title: ", ex);
            }

            return sb.toString();
        }
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }

    private void createComeInOrMoveTab(final boolean isInDorm) {
        HorizontalLayout contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();

        FormLayout inFL = new FormLayout();
        inFL.setSpacing(true);
        //		inFL.setWidth(30, Unit.PERCENTAGE);
        inFL.setSizeFull();

        buildingCB = new ComboBox(getUILocaleUtil().getCaption("building"));
        buildingCB.setRequired(true);
        buildingCB.setTextInputAllowed(true);
        buildingCB.setFilteringMode(FilteringMode.STARTSWITH);
        buildingCB.setWidth(245, Unit.PIXELS);
        QueryModel<DORM> buildingQM = new QueryModel<>(DORM.class);
        buildingQM.addWhere("deleted", Boolean.FALSE);
        BeanItemContainer<DORM> buildingBIC = null;
        try {
            buildingBIC = new BeanItemContainer<DORM>(DORM.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(buildingQM));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            LOG.error("Unable to load building: ", ex);
            Message.showError(ex.toString());
        }
        buildingCB.setContainerDataSource(buildingBIC);
        buildingCB.setNullSelectionAllowed(false);
        inFL.addComponent(buildingCB);

        roomCB = new ComboBox(getUILocaleUtil().getCaption("room"));
        roomCB.setRequired(true);
        roomCB.setTextInputAllowed(true);
        roomCB.setFilteringMode(FilteringMode.STARTSWITH);
        roomCB.setWidth(245, Unit.PIXELS);
        roomCB.setNullSelectionAllowed(false);
        inFL.addComponent(roomCB);

        buildingCB.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() != null) {
                    roomCB.setReadOnly(false);
                    QueryModel<DORM_ROOM> roomQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
                    roomQM.addWhere("dorm", ECriteria.EQUAL, ((DORM) event.getProperty().getValue()).getId());
                    roomQM.addWhere("deleted", Boolean.FALSE);
                    BeanItemContainer<DORM_ROOM> roomBIC = null;
                    try {
                        roomBIC = new BeanItemContainer<DORM_ROOM>(DORM_ROOM.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM));
                    } catch (IllegalArgumentException e) {
                        LOG.error("Unable to load room: IllegalArgumentException - ", e);
                    } catch (Exception ex) {
                        LOG.error("Unable to load room: ", ex);
                        Message.showError(ex.toString());
                    }
                    roomCB.setContainerDataSource(roomBIC);
                } else {
                    roomCB.setReadOnly(true);
                }
            }
        });

        costL = new Label();
        costL.setCaption(getUILocaleUtil().getCaption("cost"));
        costL.setWidth(245, Unit.PIXELS);

        roomCB.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                    QueryModel<DORM_ROOM> roomQM = new QueryModel<>(DORM_ROOM.class);
                    FromItem fi = roomQM.addJoin(EJoin.INNER_JOIN, "dorm", DORM.class, "id");
                    roomQM.addWhere("deleted", Boolean.FALSE);
                    roomQM.addWhere(fi, "deleted", Boolean.FALSE);
                    roomQM.addWhere(fi, "name", ECriteria.EQUAL, buildingCB.getValue().toString());
                    roomQM.addWhere("roomNo", ECriteria.EQUAL, event.getProperty().getValue().toString());
                    List<DORM_ROOM> dormRooms = null;
                    try {
                        dormRooms = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM);
                        if (dormRooms != null) {
                            LOG.info("dormRooms requested. List size= " + dormRooms.size());
                        } else {
                            LOG.info("dormRooms requested. List empty !!! ");
                        }
                    } catch (NoResultException e) {
                        LOG.error("Unable to load room: ", e);
                    } catch (Exception ex) {
                        LOG.error("Unable to load room: ", ex);
                        Message.showError(ex.toString());
                    }
                    if (dormRooms != null && dormRooms.size() > 0 && dormRooms.get(0) != null &&
                            (dormRooms.get(0).getBedCount() - dormRooms.get(0).getBusyBedCount()) > 0) {
                        if (dormRooms.get(0).getCost() != null) {
                            costL.setValue(dormRooms.get(0).getCost().toString());
                        }
                    } else {
                        Message.showInfo(getUILocaleUtil().getMessage("no.free.beds"));
                        clearIn();
                    }
                }
            }
        });
        inFL.addComponent(costL);

        inOrMoveDF = new DateField(getUILocaleUtil().getCaption("date") + "\n<br>" + getUILocaleUtil().getCaption("dorm.in.or.move"));
        inOrMoveDF.setCaptionAsHtml(true);
        inOrMoveDF.setRequired(true);
        inOrMoveDF.setValue(new Date());
        inOrMoveDF.setWidth(245, Unit.PIXELS);
        inFL.addComponent(inOrMoveDF);

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        Button saveB = new Button(getUILocaleUtil().getCaption("dorm.in"));
        if (isInDorm)
            saveB.setCaption(getUILocaleUtil().getCaption("dorm.move"));
        Button cancelB = new Button(getUILocaleUtil().getCaption("clear"));
        buttonsHL.addComponent(saveB);
        buttonsHL.addComponent(cancelB);
        saveB.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (roomCB.getValue() != null && costL.getValue() != null && !costL.getValue().equals("") && inOrMoveDF.getValue() != null) {
                    try {
                        QueryModel<STUDENT_EDUCATION> studentQM = new QueryModel<STUDENT_EDUCATION>(STUDENT_EDUCATION.class);
                        studentQM.addWhere("student", ECriteria.EQUAL, student.getId());
                        studentQM.addWhereNull("child");

                        QueryModel<DORM_ROOM> roomQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
                        FromItem fi = roomQM.addJoin(EJoin.INNER_JOIN, "dorm", DORM.class, "id");
                        roomQM.addWhere("deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "name", ECriteria.EQUAL, buildingCB.getValue().toString());
                        roomQM.addWhere("roomNo", ECriteria.EQUAL, roomCB.getValue().toString());

                        STUDENT_EDUCATION STUDENT_EDUCATION = null;
                        DORM_ROOM dormRoom = null;
                        try {
                            STUDENT_EDUCATION = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentQM);
                            dormRoom = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(roomQM);
                        } catch (NoResultException e) {
                            STUDENT_EDUCATION = null;
                        }
                        if (STUDENT_EDUCATION != null && dormRoom != null) {
                            DORM_STUDENT dormStudent = null;
                            if (!isInDorm) {
                                // delete previuos info
                                QueryModel<DORM_STUDENT> deleteQM = new QueryModel<DORM_STUDENT>(DORM_STUDENT.class);
                                deleteQM.addWhere("student", ECriteria.EQUAL, STUDENT_EDUCATION.getId());
                                deleteQM.addWhere("deleted", Boolean.FALSE);
                                List<DORM_STUDENT> dormStudentList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(deleteQM);
                                for (DORM_STUDENT tds : dormStudentList) {
                                    tds.setDeleted(true);
                                }
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dormStudentList);
                                //
                                dormStudent = new DORM_STUDENT();
                                dormStudent.setCheckInDate(inOrMoveDF.getValue());
                                dormStudent.setCost(Double.parseDouble(costL.getValue()));
                                dormStudent.setCreated(new Date());
                                dormStudent.setDeleted(false);
                                dormStudent.setStudent(STUDENT_EDUCATION);
                                dormStudent.setRoom(dormRoom);
                                dormStudent.setRequestStatus(1);

                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(dormStudent);
                                AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
//                                DormUI.getInstance().openCommonView(new StudentView(filter));//TODO
                            } else {
                                QueryModel<DORM_STUDENT> dormStudentQM = new QueryModel<DORM_STUDENT>(DORM_STUDENT.class);
                                dormStudentQM.addWhere("student", ECriteria.EQUAL, STUDENT_EDUCATION.getId());
                                dormStudentQM.addWhere("deleted", Boolean.FALSE);
                                try {
                                    dormStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(dormStudentQM);
                                } catch (NoResultException e) {
                                    dormStudent = null;
                                }
                                if (dormStudent != null) {
                                    dormStudent.setCheckInDate(inOrMoveDF.getValue());
                                    dormStudent.setCost(Double.parseDouble(costL.getValue()));
                                    dormStudent.setCreated(new Date());
                                    dormStudent.setRoom(dormRoom);

                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dormStudent);
                                    AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
                                    clearIn();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOG.error("Unable to create or update student dorm: ", ex);
                        Message.showError(ex.toString());
                    }
                } else {
                    Message.showError(getUILocaleUtil().getCaption("add.news.required"));
                }

            }

        });

        cancelB.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                clearIn();
            }
        });
        inFL.addComponent(buttonsHL);

        getInfoOfStudent(inFL, contentHL);

        getTabSheet().addTab(contentHL, getUILocaleUtil().getCaption("dorm.in.or.move"));
    }

    private void getInfoOfStudent(FormLayout violationFL, HorizontalLayout contentHL) {
        FormLayout infoFL = new FormLayout();
        infoFL.setSpacing(true);
        //		infoFL.setWidth(30, Unit.PERCENTAGE);
        infoFL.setSizeFull();

        QueryModel<USER_SOCIAL_CATEGORY> tuscQM = new QueryModel<USER_SOCIAL_CATEGORY>(USER_SOCIAL_CATEGORY.class);
        tuscQM.addWhere("user", ECriteria.EQUAL, student.getId());
        List<USER_SOCIAL_CATEGORY> socialCategoryList = new ArrayList<USER_SOCIAL_CATEGORY>();

        QueryModel<MEDICAL_CHECKUP_TYPE> medicalQM = new QueryModel<MEDICAL_CHECKUP_TYPE>(MEDICAL_CHECKUP_TYPE.class);
        FromItem medicalFI = medicalQM.addJoin(EJoin.INNER_JOIN, "id", MEDICAL_CHECKUP.class, "checkupType");
        FromItem userFI = medicalFI.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
        medicalQM.addWhere(userFI, "user", ECriteria.EQUAL, student.getId());
        List<MEDICAL_CHECKUP_TYPE> medicalCheckupTypeList = null;

        QueryModel<MEDICAL_CHECKUP> allowQM = new QueryModel<MEDICAL_CHECKUP>(MEDICAL_CHECKUP.class);
        FromItem allowUserFI = allowQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
        allowQM.addWhere(allowUserFI, "user", ECriteria.EQUAL, student.getId());
        List<MEDICAL_CHECKUP> allowDormList = null;

        QueryModel<DORM_RULE_VIOLATION_TYPE> dormRuleViolationQM = new QueryModel<DORM_RULE_VIOLATION_TYPE>(DORM_RULE_VIOLATION_TYPE.class);
        FromItem violationFI = dormRuleViolationQM.addJoin(EJoin.INNER_JOIN, "id", DORM_STUDENT_VIOLATION.class, "violationType");
        FromItem dormStudentFI = violationFI.addJoin(EJoin.INNER_JOIN, "dormStudent", DORM_STUDENT.class, "id");
        FromItem educationFI = dormStudentFI.addJoin(EJoin.INNER_JOIN, "student", STUDENT_EDUCATION.class, "id");
        dormRuleViolationQM.addWhere(educationFI, "student", ECriteria.EQUAL, student.getId());
        dormRuleViolationQM.addWhereNull(educationFI, "child");
        dormRuleViolationQM.addWhere(dormStudentFI, "deleted", Boolean.FALSE);
        List<DORM_RULE_VIOLATION_TYPE> violTypeList = new ArrayList<DORM_RULE_VIOLATION_TYPE>();
        try {
            socialCategoryList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(tuscQM);
            violTypeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dormRuleViolationQM);
            medicalCheckupTypeList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(medicalQM);
            allowDormList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(allowQM);
        } catch (Exception ex) {
            LOG.error("Unable to load social list: ", ex);
            Message.showError(ex.toString());
        }

        Label socL = new Label();
        socL.setCaption(getUILocaleUtil().getCaption("social.categories"));
        socL.setContentMode(ContentMode.HTML);
        String soc = "";
        for (USER_SOCIAL_CATEGORY userSocialCategory : socialCategoryList) {
            soc += (userSocialCategory.getSocialCategory().getCategoryName() + "<br>");
        }
        socL.setValue(soc);

        Label medL = new Label();
        medL.setCaption(getUILocaleUtil().getCaption("medical.checkup"));
        medL.setContentMode(ContentMode.HTML);
        String med = "";
        for (MEDICAL_CHECKUP_TYPE MEDICAL_CHECKUP_TYPE : medicalCheckupTypeList) {
            for (MEDICAL_CHECKUP MEDICAL_CHECKUP : allowDormList) {
                if (MEDICAL_CHECKUP.getCheckupType().equals(MEDICAL_CHECKUP_TYPE.getId())) {
                    med += (MEDICAL_CHECKUP_TYPE.getTypeName() + ", " + (MEDICAL_CHECKUP.isAllowDorm() ? "Допущен" : "Не допущен") + "<br>");
                }
            }
        }
        medL.setValue(med);

        Label violationL = new Label();
        violationL.setCaption(getUILocaleUtil().getCaption("violation.type"));
        violationL.setContentMode(ContentMode.HTML);
        String vio = "";
        for (DORM_RULE_VIOLATION_TYPE dormRuleViolationType : violTypeList) {
            vio += (dormRuleViolationType.getTypeName() + "<br>");
        }
        violationL.setValue(vio);

        infoFL.addComponent(medL);
        infoFL.addComponent(socL);
        infoFL.addComponent(violationL);

        contentHL.addComponent(violationFL);
        contentHL.setComponentAlignment(violationFL, Alignment.MIDDLE_LEFT);

        if (isInDorm) {
            FormLayout dormFL = new FormLayout();
            dormFL.setSpacing(true);
            dormFL.setSizeFull();

            Label facultyL = new Label();
            facultyL.setCaption(getUILocaleUtil().getCaption("faculty"));
            facultyL.setValue(faculty);
            dormFL.addComponent(facultyL);

            Label yearL = new Label();
            yearL.setCaption(getUILocaleUtil().getCaption("study.year.1"));
            yearL.setValue(year);
            dormFL.addComponent(yearL);

            Object[] o = getDormInfo(student.getId());
            orderAndSkipSource.setObjectAndFacultyAndYear(o, faculty, year);

            Label buildingAndRoomL = new Label();
            buildingAndRoomL.setCaption(getUILocaleUtil().getCaption("dorms.and.rooms"));
            buildingAndRoomL.setValue((String) o[3] + ", " + (String) o[2]);
            dormFL.addComponent(buildingAndRoomL);

            Label costL = new Label();
            costL.setCaption(getUILocaleUtil().getCaption("cost"));
            costL.setValue(((BigDecimal) o[0] == null ? 0.0 : ((BigDecimal) o[0]).doubleValue()) + "");
            dormFL.addComponent(costL);

            Label dateL = new Label();
            dateL.setCaption(getUILocaleUtil().getCaption("date") + " " + getUILocaleUtil().getCaption("dorm.in.or.move"));
            dateL.setValue(new SimpleDateFormat(DateUtils.SHORT_FORMAT).format((Date) o[1]));
            dormFL.addComponent(dateL);

            contentHL.addComponent(dormFL);
            contentHL.setComponentAlignment(dormFL, Alignment.MIDDLE_CENTER);
        }
        contentHL.addComponent(infoFL);
        contentHL.setComponentAlignment(infoFL, Alignment.MIDDLE_RIGHT);
    }

    private Object[] getDormInfo(ID studentId) {
        // @formatter:off
        String sql = "select a.cost, a.check_in_date, c.room_no, d.dorm_name"
                + " from DORM_STUDENT a inner join STUDENT_EDUCATION b on b.id=a.student_id and b.child_id is null"
                + " inner join DORM_ROOM c on c.id=a.room_id"
                + " inner join DORM d on d.id=c.dorm_id"
                + " where b.student_id=?1 and a.check_in_date is not null and a.check_out_date is null and a.deleted=?2"
                + " and c.deleted=?2 and d.deleted=?2";
        // @formatter:on
        Map<Integer, Object> params = new HashMap<Integer, Object>(1);
        params.put(1, studentId.getId());
        params.put(2, Boolean.FALSE);
        Object[] o = null;
        try {
            o = (Object[]) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
        } catch (Exception ex) {
            LOG.error("Unable to load dorm info: ", ex);
            Message.showError(ex.toString());
        }
        return o;
    }

    private void clearIn() {
        roomCB.clear();
        buildingCB.clear();
        costL.setValue("");
        inOrMoveDF.setValue(new Date());
    }

    private void createComeOutTab() {
        HorizontalLayout contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();

        outOrRemarkTab(contentHL, false);

        getTabSheet().addTab(contentHL, getUILocaleUtil().getCaption("dorm.out"));

    }

    private void createRemarkTab() {
        HorizontalLayout contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();

        outOrRemarkTab(contentHL, true);

        getTabSheet().addTab(contentHL, getUILocaleUtil().getCaption("remark"));

    }

    private void outOrRemarkTab(final HorizontalLayout contentHL, final boolean isRemark) {
        final FormLayout violationFL = new FormLayout();
        violationFL.setSpacing(true);
        //		violationFL.setWidth(30, Unit.PERCENTAGE);
        violationFL.setSizeFull();

        if (isRemark) {
            violationTypeCB = new ComboBox(getUILocaleUtil().getCaption("violation.type"));
            violationTypeCB.setRequired(true);
            violationTypeCB.setTextInputAllowed(true);
            violationTypeCB.setFilteringMode(FilteringMode.STARTSWITH);
            violationTypeCB.setWidth(245, Unit.PIXELS);
            QueryModel<DORM_RULE_VIOLATION_TYPE> typeQM = new QueryModel<DORM_RULE_VIOLATION_TYPE>(DORM_RULE_VIOLATION_TYPE.class);
            BeanItemContainer<DORM_RULE_VIOLATION_TYPE> typeBIC = null;
            try {
                typeBIC = new BeanItemContainer<DORM_RULE_VIOLATION_TYPE>(DORM_RULE_VIOLATION_TYPE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                LOG.error("Unable to load violation type: ", ex);
                Message.showError(ex.toString());
            }
            violationTypeCB.setContainerDataSource(typeBIC);
            violationTypeCB.setNullSelectionAllowed(false);
            violationFL.addComponent(violationTypeCB);

            violationDF = new DateField(getUILocaleUtil().getCaption("violation.date"));
            violationDF.setRequired(true);
            violationDF.setValue(new Date());
            violationDF.setWidth(245, Unit.PIXELS);
            violationFL.addComponent(violationDF);

            violationDescrTA = new TextArea();
            violationDescrTA.setCaption(getUILocaleUtil().getCaption("description"));
            violationDescrTA.setWidth(245, Unit.PIXELS);
            violationFL.addComponent(violationDescrTA);
        } else {
            outTypeCB = new ComboBox(getUILocaleUtil().getCaption("violation.type"));
            outTypeCB.setRequired(true);
            outTypeCB.setTextInputAllowed(true);
            outTypeCB.setFilteringMode(FilteringMode.STARTSWITH);
            outTypeCB.setWidth(245, Unit.PIXELS);
            QueryModel<DORM_RULE_VIOLATION_TYPE> typeQM = new QueryModel<DORM_RULE_VIOLATION_TYPE>(DORM_RULE_VIOLATION_TYPE.class);
            BeanItemContainer<DORM_RULE_VIOLATION_TYPE> typeBIC = null;
            try {
                typeBIC = new BeanItemContainer<DORM_RULE_VIOLATION_TYPE>(DORM_RULE_VIOLATION_TYPE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                LOG.error("Unable to load violation type: ", ex);
                Message.showError(ex.toString());
            }
            outTypeCB.setContainerDataSource(typeBIC);
            outTypeCB.setNullSelectionAllowed(false);
            violationFL.addComponent(outTypeCB);

            outDF = new DateField(getUILocaleUtil().getCaption("violation.date"));
            outDF.setRequired(true);
            outDF.setValue(new Date());
            outDF.setWidth(245, Unit.PIXELS);
            violationFL.addComponent(outDF);

            outDescrTA = new TextArea();
            outDescrTA.setCaption(getUILocaleUtil().getCaption("description"));
            outDescrTA.setWidth(245, Unit.PIXELS);
            violationFL.addComponent(outDescrTA);
        }

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        Button saveB = new Button(getUILocaleUtil().getCaption("add.news.save"));
        if (!isRemark)
            saveB.setCaption(getUILocaleUtil().getCaption("dorm.out.button"));
        Button cancelB = new Button(getUILocaleUtil().getCaption("clear"));
        buttonsHL.addComponent(saveB);
        buttonsHL.addComponent(cancelB);
        saveB.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if ((violationDF.getValue() != null && violationTypeCB.getValue() != null)
                        || (outDF.getValue() != null && outTypeCB.getValue() != null)) {
                    try {
                        DORM_STUDENT dormStudent;
                        try {
                            String sql="SELECT t0.* " +
                                    "FROM DORM_STUDENT t0 INNER JOIN STUDENT_EDUCATION t1 ON t0.STUDENT_ID = t1.ID " +
                                    "WHERE t1.STUDENT_ID = ?1 AND t1.CHILD_ID IS NULL AND t0.CHECK_OUT_DATE IS NULL " +
                                    "AND t0.DELETED = FALSE and t0.request_status_id=1;";
                            Map<Integer, Object> params=new HashMap<>();
                            params.put(1,student.getId().getId());
                            dormStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(
                                    sql,params,DORM_STUDENT.class);
                        } catch (NoResultException e) {
                            dormStudent = null;
                        }
                        if (dormStudent != null) {
                            DORM_STUDENT_VIOLATION DORM_STUDENT_VIOLATION = new DORM_STUDENT_VIOLATION();
                            DORM_STUDENT_VIOLATION.setCreated(new Date());
                            DORM_STUDENT_VIOLATION.setDormStudent(dormStudent);

                            if (isRemark) {
                                DORM_STUDENT_VIOLATION.setDescr(violationDescrTA.getValue());
                                DORM_STUDENT_VIOLATION.setViolationDate(violationDF.getValue());
                                DORM_STUDENT_VIOLATION.setEvicted(false);

                                DORM_RULE_VIOLATION_TYPE violationType = (DORM_RULE_VIOLATION_TYPE )violationTypeCB.getValue();
                                DORM_STUDENT_VIOLATION.setViolationType(violationType);

                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(DORM_STUDENT_VIOLATION);
                                AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
                                clearViolation();

                                contentHL.removeAllComponents();
                                getInfoOfStudent(violationFL, contentHL);

                            } else {
                                DORM_STUDENT_VIOLATION.setDescr(outDescrTA.getValue());
                                DORM_STUDENT_VIOLATION.setViolationDate(outDF.getValue());
                                DORM_STUDENT_VIOLATION.setEvicted(true);

                                DORM_RULE_VIOLATION_TYPE violationType = (DORM_RULE_VIOLATION_TYPE )outTypeCB.getValue();
                                DORM_STUDENT_VIOLATION.setViolationType(violationType);

                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(DORM_STUDENT_VIOLATION);

                                dormStudent.setCheckOutDate(outDF.getValue());
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dormStudent);

                                AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
//                                DormUI.getInstance().openCommonView(new StudentView(filter));//TODO
                            }

                        }
                    } catch (Exception ex) {
                        LOG.error("Unable to create or update violation: ", ex);
                        Message.showError(ex.toString());
                    }
                } else {
                    Message.showError(getUILocaleUtil().getCaption("add.news.required"));
                }

            }

        });

        cancelB.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (isRemark)
                    clearViolation();
                else
                    clearOut();
            }
        });
        violationFL.addComponent(buttonsHL);

        getInfoOfStudent(violationFL, contentHL);
    }

    private void clearViolation() {
        violationTypeCB.clear();
        violationDescrTA.clear();
        violationDF.setValue(new Date());
    }

    private void clearOut() {
        outTypeCB.clear();
        outDescrTA.clear();
        outDF.setValue(new Date());
    }
}
