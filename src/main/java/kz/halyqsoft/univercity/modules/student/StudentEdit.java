package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_MEDICAL_CHECKUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_LANGUAGE;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.modules.student.tabs.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.BirthCountryChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.SchoolCountryChangeListener;
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
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.*;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetEvent;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetListener;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;
import java.util.*;

import static kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm.createResourceStudent;

/**
 * @author Omarbek
 * @created Apr 4, 2016 4:29:57 PM
 */
@SuppressWarnings({"serial", "unchecked"})
public final class StudentEdit extends AbstractFormWidgetView implements PhotoWidgetListener {

    private AbstractFormWidget baseDataFW;
    private USER_PHOTO userPhoto;
    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;
    private CommonFormWidget userPassportFW, militaryDocFW, disabilityDocFW, repatriateDocFW, preemptiveRightFW;
    private CommonFormWidget grantDocFW;
    private TableWidget educationTW, languageTW, medicalCheckupTW;
    private FromItem educationUDFI;
    private Label lockLabel, lockReasonLabel, createdBylabel;
    private Button lockUnlockButton;
    private LockDialog lockDialog;
    private STUDENT student;
    private USERS users;
    private VerticalLayout mainVL;
    private static Button pdfDownload, pdfDownloadDorm, pdfDownloadLetter;
    private StudentOrApplicantView studentOrApplicantView;
    private HorizontalLayout hl;
    private static FileDownloader fileDownloaderDorm, fileDownloader,
            fileDownloaderParent, fileDownloaderTitle, fileDownloaderLetter;
    private static StreamResource myResource, myResourceDorm, resourceParents,
            myResourceTitle, myResourceLetter;
    private FormLayout educationFL;
    private FormModel mainBaseDataFM;

    public StudentEdit(final FormModel baseDataFM, VerticalLayout mainVL, StudentOrApplicantView studentOrApplicantView)
            throws Exception {
        super();
        this.mainVL = mainVL;
        this.studentOrApplicantView = studentOrApplicantView;
        mainBaseDataFM = baseDataFM;
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        hl = new HorizontalLayout();
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

        CommonUtils.setCards(baseDataFM);

        FKFieldModel entranceYearFM = (FKFieldModel) baseDataFM.getFieldModel("entranceYear");
        entranceYearFM.setReadOnlyFixed(true);

        FKFieldModel advisorFM = (FKFieldModel) baseDataFM.getFieldModel("advisor");
        advisorFM.setSelectType(ESelectType.CUSTOM_GRID);
        advisorFM.setDialogHeight(400);
        advisorFM.setDialogWidth(600);
        try {
            TextField fioTF = new TextField();
            fioTF.setImmediate(true);
            fioTF.setWidth(400, Unit.PIXELS);

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
            CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
        }

        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.setImmediate(true);
        baseDataFW.addEntityListener(this);
        hl.addComponent(baseDataFW);
        hl.setComponentAlignment(baseDataFW, Alignment.TOP_LEFT);

        if (!baseDataFM.isCreateNew()) {
            QueryModel<USER_PHOTO> qmUserPhoto = new QueryModel<>(USER_PHOTO.class);
            try {
                qmUserPhoto.addWhere("user", ECriteria.EQUAL, baseDataFM.getEntity().getId());
                try {
                    userPhoto = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmUserPhoto);
                } catch (NoResultException ex) {
                    userPhoto = null;
                } catch (Exception ex) {
                    ex.printStackTrace();//TODO catch
                }
                if (userPhoto != null) {
                    userPhotoBytes = userPhoto.getPhoto();
                    userPhotoFilename = userPhoto.getFileName();
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load user photo", ex);
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

        try {

            educationFL = getFormLayout();
            if(educationFL!=null)
            {
                rightContent.addComponent(educationFL);
                rightContent.setComponentAlignment(educationFL, Alignment.MIDDLE_CENTER);
                //educationFL
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load education info", ex);
        }

        hl.addComponent(rightContent);
        hl.setComponentAlignment(rightContent, Alignment.TOP_RIGHT);
        hl.setExpandRatio(rightContent, 1);
        content.addComponent(hl);
        content.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        content.setExpandRatio(hl, 1);

        if (!baseDataFM.isReadOnly()) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent ev) {
                    if (baseDataFW.save()) {
                        try {
                            studentEditPdfDownload((STUDENT) baseDataFM.getEntity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (userPhotoChanged) {
                            try {
                                if (userPhoto == null) {
                                    userPhoto = new USER_PHOTO();
                                    USERS s = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            lookup(USERS.class, student.getId());
                                    userPhoto.setUser(s);
                                }
                                userPhoto.setFileName(userPhotoFilename);
                                userPhoto.setPhoto(userPhotoBytes);

                                if (userPhoto.getId() == null) {
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            create(userPhoto);
                                } else {
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                            merge(userPhoto);
                                }
                            } catch (Exception e) {
                                CommonUtils.showMessageAndWriteLog("Unable to load user photo", e);
                            }
                        }
                        showSavedNotification();
                        try{
                            baseDataFM.loadEntity(student.getId());
                            CommonUtils.setCards(baseDataFM);
                            hl.removeComponent(baseDataFW);
                            baseDataFW.getWidgetModel().loadEntity(student.getId());
                            baseDataFW.refresh();
                            hl.addComponentAsFirst(baseDataFW);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            buttonPanel.addComponent(save);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);

            Button cancel = createCancelButton();
            cancel.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent ev) {
                    baseDataFW.cancel();
                }
            });

            buttonPanel.addComponent(cancel);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

            Button lockUnlock = createLockUnlockButton((STUDENT) baseDataFM.getEntity());
            buttonPanel.addComponent(lockUnlock);
            buttonPanel.setComponentAlignment(lockUnlock, Alignment.MIDDLE_CENTER);

            //  USERS user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class,student.getId());
            pdfDownload = createDownloadButton();
            buttonPanel.addComponent(pdfDownload);
            buttonPanel.setComponentAlignment(pdfDownload, Alignment.MIDDLE_CENTER);

            pdfDownloadDorm = createDownloadButtonDorm();
            buttonPanel.addComponent(pdfDownloadDorm);
            buttonPanel.setComponentAlignment(pdfDownloadDorm, Alignment.MIDDLE_CENTER);
            pdfDownloadDorm.setEnabled(false);

            pdfDownloadLetter = createDownloadButtonLetter();
            buttonPanel.addComponent(pdfDownloadLetter);
            buttonPanel.setComponentAlignment(pdfDownloadLetter, Alignment.MIDDLE_CENTER);


            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }
        getTabSheet().addTab(content, getMasterTabTitle());


        if(student.getLevel().getLevelName().equalsIgnoreCase("Магистратура"))
        {
            myResource = createResourceStudent("82", student);
            fileDownloader = new FileDownloader(myResource);
            myResource.setMIMEType("application/pdf");
            fileDownloader.extend(pdfDownload);
        }else {
            myResource = createResourceStudent("85", student);
            fileDownloader = new FileDownloader(myResource);
            myResource.setMIMEType("application/pdf");
            fileDownloader.extend(pdfDownload);
        }

        myResourceLetter = createResourceStudent("33", student);
        fileDownloaderLetter = new FileDownloader(myResourceLetter);
        myResourceLetter.setMIMEType("application/pdf");
        fileDownloaderLetter.extend(pdfDownloadLetter);

        myResourceTitle = createResourceStudent("32", student);
        fileDownloaderTitle = new FileDownloader(myResourceTitle);
        myResourceTitle.setMIMEType("application/pdf");
        fileDownloaderTitle.extend(pdfDownloadLetter);

        resourceParents = createResourceStudent("27", student);
        fileDownloaderParent = new FileDownloader(resourceParents);
        resourceParents.setMIMEType("application/pdf");
        fileDownloaderParent.extend(pdfDownloadLetter);

        if (student.isNeedDorm()) {
            pdfDownloadDorm.setEnabled(true);
            myResourceDorm = createResourceStudent("92", student);
            fileDownloaderDorm = new FileDownloader(myResourceDorm);
            myResourceDorm.setMIMEType("application/pdf");
            fileDownloaderDorm.extend(pdfDownloadDorm);
        }

        boolean readOnly = baseDataFW.getWidgetModel().isReadOnly();
        createDocumentsTab(readOnly);
        createEducationTab(readOnly);
        createMedicalCheckupTab(readOnly);
        createUNTDataTab(readOnly);
        createAddressesTab(readOnly);
        createParentsTab(readOnly);
        createAwardsTab(readOnly);
        createSocialCategoriesTab(readOnly);
//        createDebtAndPaymentTab(readOnly);//TODO add later
        if (student.getCategory().getId().equals(STUDENT_CATEGORY.STUDENT_ID)) {
            createDiplomaTab(readOnly);
        }
    }

    public static void studentEditPdfDownload(STUDENT student) throws Exception {

        if (pdfDownload.getExtensions().size() > 0) {

            pdfDownload.removeExtension(fileDownloader);
        }

        if (pdfDownloadLetter.getExtensions().size() > 0) {
            pdfDownloadLetter.removeExtension(fileDownloaderLetter);
            pdfDownloadLetter.removeExtension(fileDownloaderTitle);
            pdfDownloadLetter.removeExtension(fileDownloaderParent);
        }


        if (pdfDownloadDorm.getExtensions().size() > 0) {
            pdfDownloadDorm.removeExtension(fileDownloaderDorm);
        }

        pdfDownloadDorm.setEnabled(false);

        if(student.getLevel().getLevelName().equalsIgnoreCase("Магистратура"))
        {
            myResource = createResourceStudent("82", student);
            fileDownloader = new FileDownloader(myResource);
            myResource.setMIMEType("application/pdf");
            myResource.setCacheTime(0);
            fileDownloader.extend(pdfDownload);
        }else {
            myResource = createResourceStudent("85", student);
            fileDownloader = new FileDownloader(myResource);
            myResource.setMIMEType("application/pdf");
            myResource.setCacheTime(0);
            fileDownloader.extend(pdfDownload);
        }

        myResourceLetter = createResourceStudent("33", student);
        fileDownloaderLetter = new FileDownloader(myResourceLetter);
        myResourceLetter.setMIMEType("application/pdf");
        fileDownloaderLetter.extend(pdfDownloadLetter);

        myResourceTitle = createResourceStudent("32", student);
        fileDownloaderTitle = new FileDownloader(myResourceTitle);
        myResourceTitle.setMIMEType("application/pdf");
        fileDownloaderTitle.extend(pdfDownloadLetter);

        resourceParents = createResourceStudent("27", student);
        fileDownloaderParent = new FileDownloader(resourceParents);
        resourceParents.setMIMEType("application/pdf");
        fileDownloaderParent.extend(pdfDownloadLetter);

        if (student.isNeedDorm()) {
            pdfDownloadDorm.setEnabled(true);
            myResourceDorm = createResourceStudent("92", student);
            fileDownloaderDorm = new FileDownloader(myResourceDorm);
            myResourceDorm.setMIMEType("application/pdf");
            fileDownloaderDorm.extend(pdfDownloadDorm);
        }
    }


    @Override
    protected AbstractCommonView getParentView() {
        studentOrApplicantView.doFilter(new FStudentFilter());
        mainVL.removeComponent(this);
//        mainVL.addComponent(studentOrApplicantView.getButtonsHL());

        try {
            StudentFilterPanel studentFilterPanel = studentOrApplicantView.createStudentFilterPanel();
            studentOrApplicantView.setFilterPanel(studentFilterPanel);
        }catch (Exception e)
        {
            Message.showError(e.getMessage());
            e.printStackTrace();
        }

        mainVL.addComponent(studentOrApplicantView.getFilterPanel());
        mainVL.addComponent(studentOrApplicantView.getStudentGW());

        return null;
    }

    @Override
    public String getViewName() {
        return "studentEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        FormModel fm = baseDataFW.getWidgetModel();
        if (fm.isCreateNew()) {
            return getUILocaleUtil().getCaption("student.new");
        } else {
            StringBuilder sb = new StringBuilder();
            if (!fm.isReadOnly()) {
                sb.append(getUILocaleUtil().getCaption("student.edit"));
            } else {
                sb.append(getUILocaleUtil().getCaption("student.view"));
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

    private Button createLockUnlockButton(STUDENT student) {
        lockUnlockButton = new Button();
        lockUnlockButton.setData(11);
        lockUnlockButton.setWidth(140, Unit.PIXELS);
        lockUnlockButton.addStyleName("lock-unlock");
        lockUnlockButton.addClickListener(new LockUnlockListener(student));
        if (!student.isLocked()) {
            lockUnlockButton.setIcon(new ThemeResource("img/button/lock.png"));
            lockUnlockButton.setCaption(getUILocaleUtil().getCaption("lock"));
        } else {
            lockUnlockButton.setIcon(new ThemeResource("img/button/unlock.png"));
            lockUnlockButton.setCaption(getUILocaleUtil().getCaption("unlock"));
        }
        return lockUnlockButton;
    }

    private Button createDownloadButton() {

        Button downloadButton = new Button();
        downloadButton.setData(11);
        downloadButton.setCaption(getUILocaleUtil().getCaption("download.contract"));
        downloadButton.setWidth(130, Unit.PIXELS);

        return downloadButton;
    }

    private Button createDownloadButtonDorm() {

        Button dormDownloadButton = new Button();
        dormDownloadButton.setData(11);
        dormDownloadButton.setCaption(getUILocaleUtil().getCaption("download.contract.dorm"));
        dormDownloadButton.setWidth(220, Unit.PIXELS);

        return dormDownloadButton;
    }

    private Button createDownloadButtonLetter() {
        Button letterDownloadButton = new Button();
        letterDownloadButton.setData(11);
        letterDownloadButton.setCaption(getUILocaleUtil().getCaption("download.contract.register"));
        letterDownloadButton.setWidth(150, Unit.PIXELS);

        return letterDownloadButton;
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
    }

    private void createDiplomaTab(boolean readOnly) {
        DiplomaTab diplomaTab = new DiplomaTab(new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(diplomaTab, getUILocaleUtil().getCaption("graduation.project"));
    }

    private void createDocumentsTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        GridLayout forms = new GridLayout(2, 2);
        forms.setSizeFull();
        forms.setSpacing(true);

        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
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
            QueryModel<USER_PASSPORT> userPassportQM = new QueryModel<>(USER_PASSPORT.class);
            FromItem fi = userPassportQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            userPassportQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            userPassportQM.addWhereAnd(fi, "deleted", Boolean.FALSE);

            try {
                USER_PASSPORT userPassport = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userPassportQM);//TODO

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
            QueryModel<MILITARY_DOC> militaryDocQM = new QueryModel<>(MILITARY_DOC.class);
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
            QueryModel<DISABILITY_DOC> disabilityDocQM = new QueryModel<>(DISABILITY_DOC.class);
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
            QueryModel<REPATRIATE_DOC> repatriateDocQM = new QueryModel<>(REPATRIATE_DOC.class);
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
            save.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
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
            cancel.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
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
        educationTM.getColumnModel("entryYear").setAlignment(Align.CENTER);
        educationTM.getColumnModel("endYear").setAlignment(Align.CENTER);

        QueryModel educationQM = educationTM.getQueryModel();
        educationUDFI = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
        educationQM.addWhere(educationUDFI, "deleted", Boolean.FALSE);

        ID userId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            userId = baseDataFW.getWidgetModel().getEntity().getId();
        }

        // educationQM.addWhere("educationType", ECriteria.EQUAL, userId);
        educationQM.addWhereAnd(educationUDFI, "user", ECriteria.EQUAL, userId);

        content.addComponent(educationTW);
        content.setComponentAlignment(educationTW, Alignment.TOP_CENTER);

        /* Languages */
        languageTW = new TableWidget(V_USER_LANGUAGE.class);
        languageTW.addEntityListener(this);
        DBTableModel languageTM = (DBTableModel) languageTW.getWidgetModel();
        languageTM.setReadOnly(readOnly);

        FormModel languageFM = ((DBTableModel) languageTW.getWidgetModel()).getFormModel();
        FKFieldModel languageFM1 = (FKFieldModel) languageFM.getFieldModel("language");
        QueryModel languageQM1 = languageFM1.getQueryModel();
        languageQM1.addOrder("langName");

        FKFieldModel languageLevelFM = (FKFieldModel) languageFM.getFieldModel("languageLevel");
        QueryModel languageLevelQM = languageLevelFM.getQueryModel();
        languageLevelQM.addOrder("levelName");

        // List<V_USER_LANGUAGE> langs =
        // SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(languageQM1);
        QueryModel languageQM = languageTM.getQueryModel();
        languageQM.addWhere("user", ECriteria.EQUAL, userId);

        // pdfSource.setLanguageQM1(languageQM);
        content.addComponent(languageTW);

        content.setComponentAlignment(languageTW, Alignment.MIDDLE_CENTER);

        /* Other forms */
        VerticalLayout formsVL = new VerticalLayout();
        formsVL.setSpacing(true);
        formsVL.setSizeFull();

        GridLayout formsGL = new GridLayout(2, 1);
        formsGL.setSizeFull();
        formsGL.setSpacing(true);

        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("userDocument", ECriteria.EQUAL, null);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);
        final FormModel preemptiveRightFM = preemptiveRight(readOnly, formsGL, udfQM);
        StringBuilder sb;



        /* Grant doc */
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("grant.document"));
        grantDocFW = new CommonFormWidget(GRANT_DOC.class);
        grantDocFW.addEntityListener(this);
        final FormModel grantDocFM = grantDocFW.getWidgetModel();
        grantDocFM.setReadOnly(readOnly);
        grantDocFM.setTitleResource("grant.document");
        grantDocFM.setErrorMessageTitle(sb.toString());
        grantDocFM.setButtonsVisible(false);

        FileListFieldModel grantDocFLFM = (FileListFieldModel) grantDocFM.getFieldModel("fileList");
        grantDocFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (baseDataFW.getWidgetModel().isCreateNew()) {
            grantDocFM.createNew();
        } else {
            QueryModel<GRANT_DOC> grantDocQM = new QueryModel<>(GRANT_DOC.class);
            FromItem fi = grantDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            grantDocQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            grantDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                GRANT_DOC grantDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(grantDocQM);
                grantDocFM.loadEntity(grantDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, grantDoc.getId());
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        grantDocFLFM.getFileList().add(fe);
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    grantDocFM.loadEntity(ID.valueOf(-1));
                } else {
                    grantDocFM.createNew();
                }
            }
        }
        formsGL.addComponent(grantDocFW);

        formsVL.addComponent(formsGL);
        formsVL.setComponentAlignment(formsGL, Alignment.MIDDLE_CENTER);
        formsVL.setExpandRatio(formsGL, 1);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    if (preemptiveRightFM.isModified()) {
                        preemptiveRightFW.save();
                    }

                    if (grantDocFM.isModified()) {
                        grantDocFW.save();
                    }
                    try {
                        studentEditPdfDownload(student);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            buttonPanel.addComponent(save);

            Button cancel = createCancelButton();
            cancel.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    preemptiveRightFW.cancel();
                    grantDocFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            formsVL.addComponent(buttonPanel);
            formsVL.setComponentAlignment(buttonPanel, Alignment.MIDDLE_CENTER);
        }

        content.addComponent(formsVL);
        content.setComponentAlignment(formsVL, Alignment.BOTTOM_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("education"));
    }

    private FormModel preemptiveRight(boolean readOnly, GridLayout formsGL, QueryModel<USER_DOCUMENT_FILE> udfQM)
            throws Exception {
        /* Preemptive right */
        StringBuilder sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("preemptive.right"));
        preemptiveRightFW = new CommonFormWidget(PREEMPTIVE_RIGHT.class);
        preemptiveRightFW.addEntityListener(this);
        final FormModel preemptiveRightFM = preemptiveRightFW.getWidgetModel();
        preemptiveRightFM.setReadOnly(readOnly);
        preemptiveRightFM.setTitleResource("preemptive.right");
        preemptiveRightFM.setErrorMessageTitle(sb.toString());
        preemptiveRightFM.setButtonsVisible(false);

        FileListFieldModel preemptiveRightFLFM = (FileListFieldModel) preemptiveRightFM.getFieldModel("fileList");
        preemptiveRightFLFM.permitMimeType(FileListFieldModel.JPEG);
//        PREEMPTIVE_TYPE type = null;//TODO type
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            preemptiveRightFM.createNew();
        } else {
            QueryModel<PREEMPTIVE_RIGHT> preemptiveRightQM = new QueryModel<>(PREEMPTIVE_RIGHT.class);
            FromItem fi = preemptiveRightQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            preemptiveRightQM.addWhere(fi, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());
            preemptiveRightQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
//            preemptiveRightQM.addJoin(EJoin.INNER_JOIN, "type", PREEMPTIVE_TYPE.class, "id");
            try {
                PREEMPTIVE_RIGHT preemptiveRight = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(preemptiveRightQM);
                if (preemptiveRight != null) {
//                    type = preemptiveRight.getType();
                    // pdfSource.setPreemptiveRight(preemptiveRight);
                    preemptiveRightFM.loadEntity(preemptiveRight.getId());
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, preemptiveRight.getId());
                    List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                    if (!udfList.isEmpty()) {
                        for (Object o : udfList) {
                            Object[] oo = (Object[]) o;
                            FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                            fe.setId(ID.valueOf((Long) oo[0]));
                            fe.setFileName((String) oo[1]);
                            fe.setNewFile(false);
                            preemptiveRightFLFM.getFileList().add(fe);
                        }
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    preemptiveRightFM.loadEntity(ID.valueOf(-1));
                } else {
                    preemptiveRightFM.createNew();
                }
            }
        }
        //preemptiveRightFW.getListeners().add(new PreemptiveRightTypeChangeListener(type, preemptiveRightFM));
        formsGL.addComponent(preemptiveRightFW);
        return preemptiveRightFM;
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
        medicalCheckupTM.getColumnModel("allowWork").setInTable(false);
        QueryModel medicalCheckupQM = medicalCheckupTM.getQueryModel();
        medicalCheckupQM.addWhere("deleted", Boolean.FALSE);
        ID userId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            userId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        medicalCheckupQM.addWhereAnd("user", ECriteria.EQUAL, userId);

        FormModel medicalCheckupFM = medicalCheckupTM.getFormModel();
        medicalCheckupFM.getFieldModel("allowWork").setInEdit(false);
        medicalCheckupFM.getFieldModel("allowWork").setInView(false);
        content.addComponent(medicalCheckupTW);
        content.setComponentAlignment(medicalCheckupTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("medical.checkup"));
    }

    private void createUNTDataTab(boolean readOnly) throws Exception {
        UNTDataTab content = new UNTDataTab(student, new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(content, getUILocaleUtil().getCaption("unt"));
    }

    private void createAddressesTab(boolean readOnly) throws Exception {
        AddressesTab content = new AddressesTab(student, new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(content, getUILocaleUtil().getCaption("addresses"));
    }

    private void createParentsTab(boolean readOnly) throws Exception {
        ParentsTab parentsTab = new ParentsTab(student, new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(parentsTab, getUILocaleUtil().getCaption("parents.data"));
    }

    private void createAwardsTab(boolean readOnly) {
        AwardsTab awardsTab = new AwardsTab(new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(awardsTab, getUILocaleUtil().getCaption("awards"));
    }

    private void createSocialCategoriesTab(boolean readOnly) {
        SocialCategoriesTab socialCategoriesTab = new SocialCategoriesTab(new StudentEditHelperImpl(), readOnly);
        getTabSheet().addTab(socialCategoriesTab, getUILocaleUtil().getCaption("social.categories"));
    }

    private void createDebtAndPaymentTab(boolean readOnly) {
        VerticalLayout debtAndPaymentTab = new DebtAndPaymentTab(student.getId(), readOnly);
        getTabSheet().addTab(debtAndPaymentTab, getUILocaleUtil().getCaption("debt.payment"));
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(educationTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel educationFM = ((DBTableModel) educationTW.getWidgetModel()).getFormModel();
            educationFM.getFieldModel("expireDate").setInEdit(false);
            educationFM.getFieldModel("expireDate").setInView(false);

            FKFieldModel educationTypeFM = (FKFieldModel) educationFM.getFieldModel("educationType");
            QueryModel educationTypeQM = educationTypeFM.getQueryModel();
            educationTypeQM.addOrder("typeName");


            FKFieldModel schoolTypeFM = (FKFieldModel) educationFM.getFieldModel("schoolType");
            QueryModel schoolTypeQM = schoolTypeFM.getQueryModel();
            schoolTypeQM.addOrder("typeName");

            FKFieldModel preemptiveRightFM = (FKFieldModel) educationFM.getFieldModel("type");
            QueryModel preemptiveRightTypeQM = preemptiveRightFM.getQueryModel();
            preemptiveRightTypeQM.addOrder("type");

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

            return true;
        } else if (source.equals(languageTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
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
        }

        return super.preCreate(source, buttonId);
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
        }

        return super.onEdit(source, e, buttonId);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
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
        } else if (source.equals(preemptiveRightFW)) {
            return preSavePreemptiveRight(source, e, isNew, buttonId);
        } else if (source.equals(grantDocFW)) {
            return preSaveGrantDoc(source, e, isNew, buttonId);
        } else if (source.equals(medicalCheckupTW)) {
            return preSaveMedicalCheckup(source, e, isNew, buttonId);
        }

        return super.preSave(source, e, isNew, buttonId);
    }

    private boolean preSaveBaseData(Object source, Entity e, boolean isNew, int buttonId) {
        STUDENT s = (STUDENT) e;
        if (isNew) {
            s.setCreated(new Date());
            s.setCreatedBy(CommonUtils.getCurrentUserLogin());
            try {
                s.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS"));
                s.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
                s.setCode("000000000000");
                s.setFirstName(s.getFirstName().trim());
                s.setLastName(s.getLastName().trim());
                if (s.getMiddleName() != null) {
                    s.setMiddleName(s.getMiddleName().trim());
                }
                s.setFirstNameEN(s.getFirstNameEN().trim());
                s.setLastNameEN(s.getLastNameEN().trim());
                if (s.getMiddleNameEN() != null) {
                    s.setMiddleNameEN(s.getMiddleNameEN().trim());
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(s);
                if (userPhotoChanged) {
                    userPhoto = new USER_PHOTO();
                    userPhoto.setUser(s);
                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                }
                baseDataFW.getWidgetModel().loadEntity(s.getId());
                baseDataFW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a entrant", ex);
            }
        } else {
            s.setUpdated(new Date());
            s.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            s.setFirstName(s.getFirstName().trim());
            s.setLastName(s.getLastName().trim());
            if (s.getMiddleName() != null) {
                s.setMiddleName(s.getMiddleName().trim());
            }
            s.setFirstNameEN(s.getFirstNameEN().trim());
            s.setLastNameEN(s.getLastNameEN().trim());
            if (s.getMiddleNameEN() != null) {
                s.setMiddleNameEN(s.getMiddleNameEN().trim());
            }
            try {
                CARD oldCard = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        USERS.class, s.getId()).getCard();
                if (s.getCategory().getId().equals(ID.valueOf(3))) {
                    QueryModel<STUDENT_EDUCATION> qmSE = new QueryModel<>(STUDENT_EDUCATION.class);
                    qmSE.addSelect("student", EAggregate.COUNT);
                    qmSE.addWhere("student", ECriteria.EQUAL, s.getId());

                    Long count = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qmSE);
                    if (count == 0) {
                        QueryModel<ENTRANT_SPECIALITY> qmES = new QueryModel<>(ENTRANT_SPECIALITY.class);
                        qmES.addWhere("student", ECriteria.EQUAL, s.getId());
                        qmES.addWhereAnd("university", ECriteria.EQUAL, ID.valueOf(421));
                        List<ENTRANT_SPECIALITY> esList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmES);

                        if (!esList.isEmpty()) {
                            ENTRANT_SPECIALITY es = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANT_SPECIALITY.class, esList.get(0).getId());

                            FormModel fm = new FormModel(STUDENT_EDUCATION.class, true);

                            STUDENT_EDUCATION se = (STUDENT_EDUCATION) fm.getEntity();
                            se.setStudent(s);
                            se.setEntryDate(new Date());
                            se.setSpeciality(es.getSpeciality());
                            se.setChair(es.getSpeciality().getDepartment());
                            se.setFaculty(es.getSpeciality().getDepartment().getParent());
                            se.setLanguage(es.getLanguage());
                            se.setStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(2)));

                            FormWidgetDialog fwd = new FormWidgetDialog(fm);
                            fwd.getFormWidget().addEntityListener(this);
                            fwd.open();
                        } else {
                            Message.showError(getUILocaleUtil().getMessage("unable.change.applicant.to.student"));
                            throw new Exception("Unable to change the applicant to student");
                        }
                    }
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(s);
                if (oldCard != null && !oldCard.equals(s.getCard())) {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(oldCard);
                }
                if (userPhotoChanged) {
                    if (userPhoto == null) {
                        userPhoto = new USER_PHOTO();
                        userPhoto.setUser(s);
                    }

                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);

                    if (userPhoto.getId() == null) {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                    } else {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(userPhoto);
                    }
                }
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a entrant", ex);
            }
        }

        return false;
    }

    private boolean preSavePassport(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_PASSPORT p = (USER_PASSPORT) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                p.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                p.setUser((STUDENT) fm.getEntity());
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
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((STUDENT) fm.getEntity());
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
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                dd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                dd.setUser((STUDENT) fm.getEntity());
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
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                rd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                rd.setUser((STUDENT) fm.getEntity());
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
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                ed.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                ed.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(ed);

                QueryModel educationQM = ((DBSelectModel) educationTW.getWidgetModel()).getQueryModel();
                educationQM.addWhere(educationUDFI, "user", ECriteria.EQUAL, baseDataFW.getWidgetModel().getEntity().getId());

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
                STUDENT s = (STUDENT) baseDataFW.getWidgetModel().getEntity();
                USER_LANGUAGE ul = new USER_LANGUAGE();
                ul.setUser(s);
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ul);

                QueryModel languageQM = ((DBTableModel) languageTW.getWidgetModel()).getQueryModel();
                languageQM.addWhere("user", ECriteria.EQUAL, s.getId());

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

    private boolean preSavePreemptiveRight(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        PREEMPTIVE_RIGHT pr = (PREEMPTIVE_RIGHT) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                pr.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                pr.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(pr);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a preemptive right", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pr);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a preemptive right", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) preemptiveRightFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(pr);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save preemptive right copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete preemptive right copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveGrantDoc(Object source, Entity e, boolean isNew, int buttonId) {
        if (baseDataFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        GRANT_DOC gd = (GRANT_DOC) e;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            try {
                gd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                gd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(gd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a grant doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(gd);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a grant doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) grantDocFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(gd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save grant doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete grant doc copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveMedicalCheckup(Object source, Entity e, boolean isNew, int buttonId) {
        V_MEDICAL_CHECKUP vmc = (V_MEDICAL_CHECKUP) e;
        MEDICAL_CHECKUP mc = null;
        FormModel fm = baseDataFW.getWidgetModel();
        if (isNew) {
            mc = new MEDICAL_CHECKUP();
            try {
                STUDENT s = (STUDENT) fm.getEntity();
                mc.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                mc.setUser(s);
                mc.setDocumentNo(vmc.getDocumentNo());
                mc.setIssueDate(vmc.getIssueDate());
                mc.setExpireDate(vmc.getExpireDate());
                mc.setCheckupType(vmc.getCheckupType());
                mc.setIssuerName(vmc.getIssuerName());
                mc.setAllowDorm(vmc.isAllowDorm());
                mc.setAllowStudy(vmc.isAllowStudy());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(mc);

                QueryModel medicalCheckupQM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getQueryModel();
                medicalCheckupQM.addWhere("user", ECriteria.EQUAL, s.getId());

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
                mc.setAllowStudy(vmc.isAllowStudy());
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
            List<USER_LANGUAGE> delList = new ArrayList<>();
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
                LOG.error("Unable to delete user languages: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(medicalCheckupTW)) {
            List<MEDICAL_CHECKUP> delList = new ArrayList<>();
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
                LOG.error("Unable to delete medical checkup: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        return super.preDelete(source, entities, buttonId);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (!ev.getSource().equals(baseDataFW)) {
            super.handleEntityEvent(ev);
        }
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
    public void handlePhotoWidgetEvent(PhotoWidgetEvent ev) {
        if (ev.getEvent() == PhotoWidgetEvent.CHANGED) {
            userPhotoBytes = ev.getBytes();
            userPhotoFilename = ev.getFilename();
            userPhotoChanged = true;
        }
    }

    private class LockUnlockListener implements ClickListener {

        private final STUDENT student;

        public LockUnlockListener(STUDENT student) {
            this.student = student;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            if (!student.isLocked()) {
                lockDialog = new LockDialog(new LockListener(student));
                lockDialog.open();
            } else {
                Message.showConfirm(getUILocaleUtil().getMessage("student.unlock.confirm"), new UnlockListener(student));
            }
        }
    }

    private class LockListener extends AbstractYesButtonListener {

        private final STUDENT student;

        LockListener(STUDENT student) {
            this.student = student;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            student.setLocked(true);
            student.setLockReason(lockDialog.getLockReason());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student);
                lockLabel.setValue(getUILocaleUtil().getCaption("yes"));
                lockReasonLabel.setValue(student.getLockReason().toString());
                lockUnlockButton.setCaption(getUILocaleUtil().getCaption("unlock"));
                lockUnlockButton.setIcon(new ThemeResource("img/button/unlock.png"));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to lock the student", ex);
                Message.showError(ex.toString());
            }
        }

        @Override
        protected boolean canClose() {
            return canProcess();
        }

        @Override
        protected boolean canProcess() {
            return lockDialog.getLockReason() != null;
        }
    }

    private class UnlockListener extends AbstractYesButtonListener {

        private final STUDENT student;

        UnlockListener(STUDENT student) {
            this.student = student;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            student.setLocked(false);
//            student.setLockReason(null);
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student);
                lockLabel.setValue(getUILocaleUtil().getCaption("no"));
                lockReasonLabel.setValue(null);
                lockUnlockButton.setCaption(getUILocaleUtil().getCaption("lock"));
                lockUnlockButton.setIcon(new ThemeResource("img/button/lock.png"));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to unlock the student", ex);
                Message.showError(ex.toString());
            }
        }
    }

    public FormLayout getEducationFL() {
        return educationFL;
    }

    public void setEducationFL(FormLayout educationFL) {
        this.educationFL = educationFL;
    }

    public FormLayout getFormLayout() {

        try {
            student = (STUDENT) mainBaseDataFM.getEntity();
            QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
            seQM.addWhere("student", ECriteria.EQUAL, student.getId());
            seQM.addWhereNullAnd("child");

            STUDENT_EDUCATION se = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(seQM);
            STUDENT_EDUCATION studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION.class, se.getId());

            FormLayout educationFL = new FormLayout();
            educationFL.setMargin(new MarginInfo(false, false, false, true));
            educationFL.setCaption(getUILocaleUtil().getCaption("education.info"));

            Label label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "faculty"));
            label.setValue(studentEducation.getFaculty().toString());
            label.setImmediate(true);
            educationFL.addComponent(label);

            label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "speciality"));
            label.setValue(studentEducation.getSpeciality().toString());
            label.setImmediate(true);

            educationFL.addComponent(label);

            if (!mainBaseDataFM.isReadOnly()) {
                Button editButton = new NativeButton();
                editButton.setCaption(getUILocaleUtil().getCaption("edit"));
                editButton.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent ev) {
                        try {
                            StudentSpecialityEdit schedulePanelEdit = new StudentSpecialityEdit(studentEducation, StudentEdit.this);
                        } catch (Exception e) {
                            e.printStackTrace();//TODO check
                        }
                    }
                });
                educationFL.addComponent(editButton);
            }

            label = new Label();
            label.addStyleName("bold");
            label.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
            label.setImmediate(true);
            label.setValue(studentEducation.getStudyYear().toString());
            educationFL.addComponent(label);

            label = new Label();

            label.setImmediate(true);
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

            if (!mainBaseDataFM.isReadOnly() && student.getCategory().getId().equals(STUDENT_CATEGORY.STUDENT_ID)) {
                Button moreButton = new NativeButton();
                moreButton.setCaption(getUILocaleUtil().getCaption("more"));
                moreButton.addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent ev) {

                        mainVL.removeComponent(StudentEdit.this);
                        EducationDetailPanel edp = new EducationDetailPanel(studentEducation, mainVL, StudentEdit.this);
                        mainVL.addComponent(edp);

                    }
                });
                educationFL.addComponent(moreButton);
            }

            createdBylabel = new Label();
            createdBylabel.addStyleName("bold");
            createdBylabel.setCaption(getUILocaleUtil().getEntityFieldLabel(USERS.class, "createdBy"));
            String sql = "SELECT" +
                    "  * " +
                    "FROM USERS usr" +
                    " WHERE login =" +
                    "(SELECT created_by FROM USERS where id = ?1)";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, student.getId().getId());
            USERS user;
            try {
                user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params, USERS.class);
            } catch (NoResultException e) {
                user = null;
            }
            if (user != null) {
                createdBylabel.setValue(user.toString());
            }
            educationFL.addComponent(createdBylabel);

        return educationFL;

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public interface StudentEditHelper {

        boolean isStudentNew();

        STUDENT getStudent();

        UILocaleUtil getUiLocaleUtil();

        void showSavedNotification();

        HorizontalLayout createButtonPanel();

        Button createSaveButton();

        Button createCancelButton();
    }

    private class StudentEditHelperImpl implements StudentEditHelper {

        @Override
        public boolean isStudentNew() {
            return baseDataFW.getWidgetModel().isCreateNew();
        }

        @Override
        public STUDENT getStudent() {
            return student;
        }

        @Override
        public UILocaleUtil getUiLocaleUtil() {
            return getUILocaleUtil();
        }

        @Override
        public void showSavedNotification() {
            StudentEdit.this.showSavedNotification();
        }

        @Override
        public HorizontalLayout createButtonPanel() {
            return StudentEdit.this.createButtonPanel();
        }

        @Override
        public Button createSaveButton() {
            return StudentEdit.this.createSaveButton();
        }

        @Override
        public Button createCancelButton() {
            return StudentEdit.this.createCancelButton();
        }
    }
}
