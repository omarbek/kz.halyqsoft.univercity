package kz.halyqsoft.univercity.modules.regapplicants;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.Flag;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.utils.AddressUtils;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.*;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetEvent;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetListener;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import static com.vaadin.ui.Table.Align;

/**
 * @author Omarbek
 * @created 21.05.2016 11:25:18
 */
public final class ApplicantsForm extends AbstractFormWidgetView implements PhotoWidgetListener {

    private AbstractFormWidget dataAFW;
    private HorizontalSplitPanel registrationHSP;
    private HorizontalLayout contentHL;
    private VerticalLayout buttonsVL;
    private VerticalLayout messForm;
    private FromItem educationUDFI;
    private FileListFieldModel militaryFLFM;

    private TableWidget specTW, documentsTW, languagesTW, medicalCheckupTW;
    private TableWidget awardsTW, socialCategoriesTW, untRatesTW;
    private GridFormWidget passportGFW, militaryGFW, disabilityGFW, repatriateGFW, grantGFW;
    private GridFormWidget preemptiveRightGFW, certificateGFW, addressRegGFW;
    private GridFormWidget addressFactGFW, fatherGFW, motherGFW, dataContractGFW, educDocGFW;

    private Button form;
    private Button mainDataButton, factAddressButton, regAddressButton, specButton;
    private Button idDocButton, militaryButton, disabilityButton, repatriateButton;
    private Button eduDocButton, eduDocsButton, preemRightButton;
    private Button medButton;
    private Button untButton, grantDocButton;
    private Button motherButton, fatherButton;
    private Button contractButton, moreButton;
    private Button finishButton;
    private Button nextFactAddressButton, nextRegAddressButton, nextSpecButton;
    private Button nextIdDocButton, nextMilitaryButton, nextDisabilityButton;
    private Button nextRepatriateButton, nextEduDocButton, nextEduDocsButton;
    private Button nextPreemRightButton, nextMedButton;
    private Button nextUntButton, nextGrantDocButton, nextMotherButton, nextFatherButton;
    private Button nextContractButton, nextMoreButton, nextFinishButton;

    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;

    private boolean saveData, saveSpec, savePass, saveEduc, saveUNT;
    private STUDENT student;
    private Flag flag;
    private Integer beginYear;
    private int currentYear;

    private static final int FATHER = 1;
    private static final int MOTHER = 2;

    ApplicantsForm(final FormModel dataFM, ENTRANCE_YEAR entranceYear) throws Exception {

        super();
        beginYear = entranceYear.getBeginYear();
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        setBackButtonVisible(false);
        registrationHSP = new HorizontalSplitPanel();
        registrationHSP.setSplitPosition(20);
        registrationHSP.setSizeFull();

        buttonsVL = new VerticalLayout();
        buttonsVL.setSpacing(true);
        buttonsVL.setSizeFull();

        contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();

        dataFM.setButtonsVisible(false);
        dataFM.getFieldModel("academicStatus").setInEdit(false);
        dataFM.getFieldModel("academicStatus").setInView(false);
        dataAFW = new GridFormWidget(dataFM);
        dataAFW.addEntityListener(this);
        dataFM.getFieldModel("email").getValidators().add(new EmailValidator("Введён некорректный E-mail"));
        dataAFW.setCaption(getUILocaleUtil().getCaption("regapplicant.main.data"));
        dataFM.getFieldModel("email").setRequired(true);
        dataFM.getFieldModel("email").setReadOnly(false);
        dataFM.getFieldModel("phoneMobile").setRequired(true);

        FKFieldModel advisorFM = (FKFieldModel) dataFM.getFieldModel("advisor");
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
            BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class, SessionFacadeFactory
                    .getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
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
            BeanItemContainer<POST> postBIC = new BeanItemContainer<>(POST.class, SessionFacadeFactory
                    .getSessionFacade(CommonEntityFacadeBean.class).lookup(postQM));
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

        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("userDocument", ECriteria.EQUAL, null);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);

        final FormModel preemptiveRightFM = createPreemptiveRight(udfQM);

        final FormModel userPassportFM = createIdentityDoc(udfQM);

        final FormModel militaryDocFM = createMilitaryDoc(udfQM);

        final FormModel disabilityDocFM = createDisability(udfQM);

        final FormModel repatriateDocFM = createRepatriate(udfQM);

        final FormModel grantDocFM = createGrant(udfQM);

        final FormModel untCertificateFM = createUnt(udfQM);

        final FormModel addressRegFM = createAddressReg();

        final FormModel addressFactFM = createAddressFact();

        final FormModel fatherFM = createFatherData("parents.data.father", FATHER);

        final FormModel motherFM = createFatherData("parents.data.mother", MOTHER);

        final FormModel dataContractFM = createContractData(udfQM);

        final FormModel educationFM = createEducationDoc(udfQM);

        createFormButtons(dataFM, preemptiveRightFM, userPassportFM, militaryDocFM,
                disabilityDocFM, repatriateDocFM, grantDocFM, untCertificateFM, addressRegFM,
                addressFactFM, fatherFM, motherFM, dataContractFM, educationFM);

        setNextButtons();

        addFormButtons();

        form.click();
    }

    private FormModel createPreemptiveRight(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        String sb = getUILocaleUtil().getCaption("title.error") + ": "
                + getUILocaleUtil().getCaption("preemptive.right");
        preemptiveRightGFW = new GridFormWidget(PREEMPTIVE_RIGHT.class);
        preemptiveRightGFW.addEntityListener(this);
        final FormModel preemptiveRightFM = preemptiveRightGFW.getWidgetModel();
        preemptiveRightFM.setTitleResource("preemptive.right");
        preemptiveRightFM.setErrorMessageTitle(sb);
        preemptiveRightFM.setButtonsVisible(false);

        FileListFieldModel preemptiveRightFLFM = (FileListFieldModel) preemptiveRightFM.getFieldModel("fileList");
        preemptiveRightFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            preemptiveRightFM.createNew();
        } else {
            QueryModel<PREEMPTIVE_RIGHT> preemptiveRightQM = new QueryModel<>(PREEMPTIVE_RIGHT.class);
            FromItem fi = preemptiveRightQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            preemptiveRightQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            preemptiveRightQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                PREEMPTIVE_RIGHT preemptiveRight = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(preemptiveRightQM);
                if (preemptiveRight != null) {
                    preemptiveRightFM.loadEntity(preemptiveRight.getId());
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, preemptiveRight.getId());
                    addFiles(udfQM, preemptiveRightFLFM);
                }
            } catch (NoResultException ex) {
                preemptiveRightFM.createNew();
            }
        }
        return preemptiveRightFM;
    }

    private FormModel createIdentityDoc(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("identity.document"));
        passportGFW = new GridFormWidget(USER_PASSPORT.class);
        passportGFW.addEntityListener(this);
        passportGFW.focus();
        final FormModel userPassportFM = passportGFW.getWidgetModel();
        userPassportFM.setTitleResource("identity.document");
        userPassportFM.setErrorMessageTitle(sb.toString());
        userPassportFM.setButtonsVisible(false);
        userPassportFM.getFieldModel("expireDate").setRequired(true);

        COUNTRY birthRegion = null;

        FKFieldModel birthCountryFieldModel = (FKFieldModel) userPassportFM.getFieldModel("birthCountry");
        QueryModel birthCountryQM = birthCountryFieldModel.getQueryModel();
        birthCountryQM.addWhereNull("parent");
        birthCountryQM.addOrder("countryName");

        FKFieldModel birthRegionFieldModel = (FKFieldModel) userPassportFM.getFieldModel("birthRegion");
        QueryModel birthRegionQM = birthRegionFieldModel.getQueryModel();
        birthRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        userPassportFM.getFieldModel("iin").getValidators().add(new RegexpValidator("^\\d{12}$", "ИИН должен состоять из 12 цифр"));

        FileListFieldModel passportFLFM = (FileListFieldModel) userPassportFM.getFieldModel("fileList");
        passportFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            userPassportFM.createNew();
        } else {
            QueryModel<USER_PASSPORT> userPassportQM = new QueryModel<>(USER_PASSPORT.class);
            FromItem fi = userPassportQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            userPassportQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
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
                            LOG.error("It somehow works! " + fe.toString());
                            fe.setNewFile(false);
                            passportFLFM.getFileList().add(fe);
                        }
                    }
                }
            } catch (NoResultException ex) {
                userPassportFM.createNew();
            }
        }
        birthCountryFieldModel.getListeners().add(new BirthCountryChangeListener(birthRegionFieldModel, birthRegion));
        return userPassportFM;
    }

    private FormModel createMilitaryDoc(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("military.document"));
        militaryGFW = new GridFormWidget(MILITARY_DOC.class);
        militaryGFW.addEntityListener(this);
        militaryGFW.addStyleName("toTop");
        final FormModel militaryDocFM = militaryGFW.getWidgetModel();
        militaryDocFM.setTitleResource("military.document");
        militaryDocFM.setErrorMessageTitle(sb.toString());
        militaryDocFM.setButtonsVisible(false);
        militaryDocFM.getFieldModel("militaryDocType").addStyle("toTop");

        militaryFLFM = (FileListFieldModel) militaryDocFM.getFieldModel("fileList");
        militaryFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            militaryDocFM.createNew();
        } else {
            QueryModel<MILITARY_DOC> militaryDocQM = new QueryModel<>(MILITARY_DOC.class);
            FromItem fi = militaryDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            militaryDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            militaryDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                MILITARY_DOC militaryDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(militaryDocQM);
                militaryDocFM.loadEntity(militaryDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, militaryDoc.getId());
                addFiles(udfQM, militaryFLFM);
            } catch (NoResultException ex) {
                militaryDocFM.createNew();
            }
        }
        return militaryDocFM;
    }

    private FormModel createDisability(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("disability.document"));
        disabilityGFW = new GridFormWidget(DISABILITY_DOC.class);
        disabilityGFW.addEntityListener(this);
        disabilityGFW.addStyleName("toTop");
        final FormModel disabilityDocFM = disabilityGFW.getWidgetModel();
        disabilityDocFM.setTitleResource("disability.document");
        disabilityDocFM.setErrorMessageTitle(sb.toString());
        disabilityDocFM.setButtonsVisible(false);
        disabilityDocFM.getFieldModel("expireDate").addStyle("toTop");

        FileListFieldModel disabilityFLFM = (FileListFieldModel) disabilityDocFM.getFieldModel("fileList");
        disabilityFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            disabilityDocFM.createNew();
        } else {
            QueryModel<DISABILITY_DOC> disabilityDocQM = new QueryModel<>(DISABILITY_DOC.class);
            FromItem fi = disabilityDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            disabilityDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            disabilityDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                DISABILITY_DOC disabilityDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(disabilityDocQM);
                disabilityDocFM.loadEntity(disabilityDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, disabilityDoc.getId());
                addFiles(udfQM, disabilityFLFM);
            } catch (NoResultException ex) {
                disabilityDocFM.createNew();
            }
        }
        return disabilityDocFM;
    }

    private FormModel createRepatriate(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("repatriate.document"));
        repatriateGFW = new GridFormWidget(REPATRIATE_DOC.class);
        repatriateGFW.addEntityListener(this);
        repatriateGFW.addStyleName("toTop");
        final FormModel repatriateDocFM = repatriateGFW.getWidgetModel();
        repatriateDocFM.setTitleResource("repatriate.document");
        repatriateDocFM.setErrorMessageTitle(sb.toString());
        repatriateDocFM.setButtonsVisible(false);

        FileListFieldModel repatriateFLFM = (FileListFieldModel) repatriateDocFM.getFieldModel("fileList");
        repatriateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            repatriateDocFM.createNew();
        } else {
            QueryModel<REPATRIATE_DOC> repatriateDocQM = new QueryModel<>(REPATRIATE_DOC.class);
            FromItem fi = repatriateDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            repatriateDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            repatriateDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                REPATRIATE_DOC repatriateDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(repatriateDocQM);
                repatriateDocFM.loadEntity(repatriateDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, repatriateDoc.getId());
                addFiles(udfQM, repatriateFLFM);
            } catch (NoResultException ex) {
                repatriateDocFM.createNew();
            }
        }
        return repatriateDocFM;
    }

    private FormModel createGrant(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("grant.document"));
        grantGFW = new GridFormWidget(GRANT_DOC.class);
        grantGFW.addEntityListener(this);
        final FormModel grantDocFM = grantGFW.getWidgetModel();
        grantDocFM.setTitleResource("grant.document");
        grantDocFM.setErrorMessageTitle(sb.toString());
        grantDocFM.setButtonsVisible(false);
        grantDocFM.getFieldModel("ict").getValidators().add(new RegexpValidator("^\\d{9}$", "ИКТ должен состоять из 9 цифр"));

        FileListFieldModel grantDocFLFM = (FileListFieldModel) grantDocFM.getFieldModel("fileList");
        grantDocFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            grantDocFM.createNew();
        } else {
            QueryModel<GRANT_DOC> grantDocQM = new QueryModel<>(GRANT_DOC.class);
            FromItem fi = grantDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            grantDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            grantDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                GRANT_DOC grantDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(grantDocQM);
                grantDocFM.loadEntity(grantDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, grantDoc.getId());
                addFiles(udfQM, grantDocFLFM);
            } catch (NoResultException ex) {
                grantDocFM.createNew();
            }
        }
        return grantDocFM;
    }

    private FormModel createUnt(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("unt"));
        certificateGFW = new GridFormWidget(UNT_CERTIFICATE.class);
        certificateGFW.addEntityListener(this);
        final FormModel untCertificateFM = certificateGFW.getWidgetModel();
        untCertificateFM.setTitleResource("unt.certificate");
        untCertificateFM.setErrorMessageTitle(sb.toString());
        untCertificateFM.setButtonsVisible(false);
        untCertificateFM.getFieldModel("ict").getValidators().add(new RegexpValidator("^\\d{9}$", "ИКТ должен состоять из 9 цифр"));
        untCertificateFM.getFieldModel("rate").setInEdit(true);
        FileListFieldModel untCertificateFLFM = (FileListFieldModel) untCertificateFM.getFieldModel("fileList");
        untCertificateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            UNT_CERTIFICATE cert = (UNT_CERTIFICATE) untCertificateFM.createNew();
            cert.setRate(0);
        } else {
            QueryModel<UNT_CERTIFICATE> untCertificateQM = new QueryModel<>(UNT_CERTIFICATE.class);
            FromItem fi = untCertificateQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            untCertificateQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            untCertificateQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                UNT_CERTIFICATE untCertificate = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(untCertificateQM);
                if (untCertificate != null) {
                    untCertificateFM.loadEntity(untCertificate.getId());
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, untCertificate.getId());
                    addFiles(udfQM, untCertificateFLFM);
                }
            } catch (NoResultException ex) {
                untCertificateFM.createNew();
            }
        }
        return untCertificateFM;
    }

    private FormModel createAddressReg() throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("address.registration"));
        addressRegGFW = new GridFormWidget(USER_ADDRESS.class);
        addressRegGFW.addEntityListener(this);
        final FormModel addressRegFM = addressRegGFW.getWidgetModel();
        addressRegFM.setTitleResource("address.registration");
        addressRegFM.setErrorMessageTitle(sb.toString());
        addressRegFM.setButtonsVisible(false);

        FKFieldModel countryRegFM = (FKFieldModel) addressRegFM.getFieldModel("country");
        QueryModel countryRegQM = countryRegFM.getQueryModel();
        countryRegQM.addWhereNull("parent");

        FKFieldModel regionRegFM = (FKFieldModel) addressRegFM.getFieldModel("region");
        QueryModel regionRegQM = regionRegFM.getQueryModel();
        regionRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel cityRegFM = (FKFieldModel) addressRegFM.getFieldModel("city");
        QueryModel cityRegQM = cityRegFM.getQueryModel();
        cityRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel villageRegFM = (FKFieldModel) addressRegFM.getFieldModel("village");
        QueryModel villageRegQM = villageRegFM.getQueryModel();
        villageRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        COUNTRY regionReg = null, cityReg = null, villageReg = null;
        if (dataAFW.getWidgetModel().isCreateNew()) {
            addressRegFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressRegQM = new QueryModel<>(USER_ADDRESS.class);
            addressRegQM.addWhere("user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
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
                addressRegFM.createNew();
            }
        }

        countryRegFM.getListeners().add(new CountryChangeListener(regionReg, regionRegFM));
        regionRegFM.getListeners().add(new RegionChangeListener(cityReg, cityRegFM));
        cityRegFM.getListeners().add(new CityChangeListener(villageReg, villageRegFM));
        return addressRegFM;
    }

    private FormModel createAddressFact() throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("address.residential"));
        addressFactGFW = new GridFormWidget(USER_ADDRESS.class);
        addressFactGFW.addEntityListener(this);
        final FormModel addressFactFM = addressFactGFW.getWidgetModel();
        addressFactFM.setTitleResource("address.residential");
        addressFactFM.setErrorMessageTitle(sb.toString());
        addressFactFM.setButtonsVisible(false);

        FKFieldModel countryFactFM = (FKFieldModel) addressFactFM.getFieldModel("country");
        QueryModel countryFactQM = countryFactFM.getQueryModel();
        countryFactQM.addWhereNull("parent");

        FKFieldModel regionFactFM = (FKFieldModel) addressFactFM.getFieldModel("region");
        QueryModel regionFactQM = regionFactFM.getQueryModel();
        regionFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel cityFactFM = (FKFieldModel) addressFactFM.getFieldModel("city");
        QueryModel cityFactQM = cityFactFM.getQueryModel();
        cityFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel villageFactFM = (FKFieldModel) addressFactFM.getFieldModel("village");
        QueryModel villageFactQM = villageFactFM.getQueryModel();
        villageFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        COUNTRY regionFact = null;
        COUNTRY cityFact = null;
        COUNTRY villageFact = null;
        if (dataAFW.getWidgetModel().isCreateNew()) {
            addressFactFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressFactQM = new QueryModel<USER_ADDRESS>(USER_ADDRESS.class);
            addressFactQM.addWhere("user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
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
                addressFactFM.createNew();
            }
        }

        countryFactFM.getListeners().add(new CountryChangeListener(regionFact, regionFactFM));
        regionFactFM.getListeners().add(new RegionChangeListener(cityFact, cityFactFM));
        cityFactFM.getListeners().add(new CityChangeListener(villageFact, villageFactFM));
        return addressFactFM;
    }

    private FormModel createFatherData(String caption, int parent_number) throws Exception {
        StringBuilder parentSB = new StringBuilder();
        parentSB.append(getUILocaleUtil().getCaption("title.error"));
        parentSB.append(": ");
        parentSB.append(getUILocaleUtil().getCaption(caption));
        FormModel parentFM;
        if (parent_number == FATHER) {
            fatherGFW = new GridFormWidget(STUDENT_RELATIVE.class);
            fatherGFW.addEntityListener(this);
            parentFM = createFormModel(fatherGFW, parentSB, caption);
        } else {
            motherGFW = new GridFormWidget(STUDENT_RELATIVE.class);
            motherGFW.addEntityListener(this);
            parentFM = createFormModel(motherGFW, parentSB, caption);
        }

        FKFieldModel countryFM = createFKFieldModel("country", parentFM);
        FKFieldModel regionFM = createFKFieldModel("region", parentFM);
        FKFieldModel cityFM = createFKFieldModel("city", parentFM);
        FKFieldModel villageFM = createFKFieldModel("village", parentFM);

        COUNTRY region = null;
        COUNTRY city = null;
        COUNTRY village = null;
        if (dataAFW.getWidgetModel().isCreateNew()) {
            parentFM.createNew();
        } else {
            ID studentId = dataAFW.getWidgetModel().getEntity().getId();

            AddressUtils addressUtils = new AddressUtils(parent_number, parentFM, false, studentId);
            region = addressUtils.getRegion();
            city = addressUtils.getCity();
            village = addressUtils.getVillage();
        }

        countryFM.getListeners().add(new CountryChangeListener(region, regionFM));
        regionFM.getListeners().add(new RegionChangeListener(city, cityFM));
        cityFM.getListeners().add(new CityChangeListener(village, villageFM));
        return parentFM;
    }

    private FormModel createContractData(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("contract.data"));
        dataContractGFW = new GridFormWidget(STUDENT_CONTRACT.class);
        dataContractGFW.addEntityListener(this);
        final FormModel dataContractFM = dataContractGFW.getWidgetModel();
        dataContractFM.setButtonsVisible(false);
        dataContractFM.setTitleResource("contract.data");
        dataContractFM.getFieldModel("expireDate").setInEdit(false);

        FileListFieldModel contractFLFM = (FileListFieldModel) dataContractFM.getFieldModel("fileList");
        contractFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            dataContractFM.createNew();
        } else {
            QueryModel<STUDENT_CONTRACT> dataContractQM = new QueryModel<>(STUDENT_CONTRACT.class);
            FromItem sc = dataContractQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            dataContractQM.addWhere(sc, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            dataContractQM.addWhereAnd(sc, "deleted", Boolean.FALSE);
            try {
                STUDENT_CONTRACT dataContractDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(dataContractQM);
                dataContractFM.loadEntity(dataContractDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, dataContractDoc.getId());
                addFiles(udfQM, militaryFLFM);
            } catch (NoResultException ex) {
                dataContractFM.createNew();
            }
        }
        return dataContractFM;
    }

    private FormModel createEducationDoc(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(getUILocaleUtil().getCaption("education.document"));
        educDocGFW = new GridFormWidget(EDUCATION_DOC.class);
        educDocGFW.addEntityListener(this);
        final FormModel educationFM = educDocGFW.getWidgetModel();
        educationFM.setButtonsVisible(false);
        educationFM.setTitleResource("education.document");
        FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
        QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
        schoolCountryQM.addWhereNull("parent");
        schoolCountryQM.addOrder("countryName");

        educationFM.getFieldModel("language").setRequired(true);
        educationFM.getFieldModel("schoolCertificateType").setRequired(true);
        educationFM.getFieldModel("schoolRegion").setRequired(false);

        educationFM.getFieldModel("schoolAddress").setRequired(true);
        educationFM.getFieldModel("entryYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, currentYear));
        educationFM.getFieldModel("endYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, currentYear));

        FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
        QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
        schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));


        FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
        educationFLFM.permitMimeType(FileListFieldModel.JPEG);
        educationFLFM.getFileList().clear();
        educationFLFM.getDeleteList().clear();
        if (dataAFW.getWidgetModel().isCreateNew()) {
            educationFM.createNew();
        } else {
            QueryModel<EDUCATION_DOC> educationQM = new QueryModel<EDUCATION_DOC>(EDUCATION_DOC.class);
            FromItem sc = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            educationQM.addWhere(sc, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            educationQM.addWhereAnd(sc, "deleted", Boolean.FALSE);
            try {
                EDUCATION_DOC educDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(educationQM);
                educationFM.loadEntity(educDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, educDoc.getId());
                addFiles(udfQM, educationFLFM);
            } catch (NoResultException ex) {
                educationFM.createNew();
            }
        }
        return educationFM;
    }

    private void setNextButtons() {
        String caption = "next";
        nextFactAddressButton = createNextButton(factAddressButton, caption);
        nextRegAddressButton = createNextButton(regAddressButton, caption);
        nextSpecButton = createNextButton(specButton, caption);
        nextIdDocButton = createNextButton(idDocButton, caption);
        nextMilitaryButton = createNextButton(militaryButton, caption);
        nextDisabilityButton = createNextButton(disabilityButton, caption);
        nextRepatriateButton = createNextButton(repatriateButton, caption);
        nextEduDocButton = createNextButton(eduDocButton, caption);
        nextEduDocsButton = createNextButton(eduDocsButton, caption);
        nextPreemRightButton = createNextButton(preemRightButton, caption);
        nextMedButton = createNextButton(medButton, caption);
        nextUntButton = createNextButton(untButton, caption);
        nextGrantDocButton = createNextButton(grantDocButton, caption);
        nextMotherButton = createNextButton(motherButton, caption);
        nextFatherButton = createNextButton(fatherButton, caption);
        nextContractButton = createNextButton(contractButton, caption);
        nextMoreButton = createNextButton(moreButton, caption);
        caption = "exit";
        nextFinishButton = createNextButton(finishButton, caption);
    }

    private void createFormButtons(FormModel dataFM, FormModel preemptiveRightFM, FormModel userPassportFM,
                                   FormModel militaryDocFM, FormModel disabilityDocFM, FormModel repatriateDocFM,
                                   FormModel grantDocFM, FormModel untCertificateFM, FormModel addressRegFM,
                                   FormModel addressFactFM, FormModel fatherFM, FormModel motherFM,
                                   FormModel dataContractFM, FormModel educationFM) {
        String caption = "buttonMain";
        String styleName = "buttonChild";

        form = createFormButton("regapplicant.main.data", caption);
        form.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getContent().removeAllComponents();
                mainDataButton.addStyleName("actived");
                flag = Flag.MAIN_DATA;
                saveData = false;
                saveSpec = false;
                saveEduc = false;
                savePass = false;
                saveUNT = false;
                specTW = new TableWidget(V_ENTRANT_SPECIALITY.class);
                specTW.addEntityListener(ApplicantsForm.this);
                specTW.setWidth("667px");
                DBTableModel entrantSpecialityTM = (DBTableModel) specTW.getWidgetModel();
                entrantSpecialityTM.setPageLength(5);
                QueryModel entrantSpecialityQM = ((DBTableModel) specTW.getWidgetModel()).getQueryModel();
                ID studentId1 = ID.valueOf(-1);
                if (!dataFM.isCreateNew()) {
                    try {
                        studentId1 = dataFM.getEntity().getId();
                    } catch (Exception ex) {
                        ex.printStackTrace();//TODO catch
                    }
                }
                entrantSpecialityQM.addWhere("student", ECriteria.EQUAL, studentId1);

                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(dataAFW);

                VerticalLayout photoAndButtonVL = getPhotoVL();
                contentHL.addComponent(photoAndButtonVL);

                registrationHSP.addComponent(contentHL);
                getContent().addComponent(registrationHSP);
            }


        });

        mainDataButton = createFormButton("regapplicant.main.data", caption);
        mainDataButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.MAIN_DATA;
                mainDataButton.addStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(dataAFW);

                VerticalLayout photoAndButtonVL = getPhotoVL();
                contentHL.addComponent(photoAndButtonVL);

                registrationHSP.addComponent(contentHL);
            }
        });

        factAddressButton = createFormButton("address.residential", styleName);
        factAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.FACT_ADDRESS;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.addStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.addStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(addressFactGFW);
                contentHL.addComponent(nextRegAddressButton);
                contentHL.setComponentAlignment(nextRegAddressButton, Alignment.MIDDLE_CENTER);
            }
        });

        regAddressButton = createFormButton("address.registration", styleName);
        regAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.REG_ADDRESS;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.addStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.addStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(addressRegGFW);
                contentHL.addComponent(nextSpecButton);
                contentHL.setComponentAlignment(nextSpecButton, Alignment.MIDDLE_CENTER);
            }
        });

        specButton = createFormButton("speciality", styleName);
        specButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                specTW = new TableWidget(V_ENTRANT_SPECIALITY.class);
                specTW.addEntityListener(ApplicantsForm.this);
                specTW.setWidth("667px");
                DBTableModel entrantSpecialityTM = (DBTableModel) specTW.getWidgetModel();
                entrantSpecialityTM.setPageLength(5);
                QueryModel entrantSpecialityQM = ((DBTableModel) specTW.getWidgetModel()).getQueryModel();
                ID studentId1 = ID.valueOf(-1);
                if (!dataFM.isCreateNew()) {
                    try {
                        studentId1 = dataFM.getEntity().getId();
                        if (dataAFW.save()) {
                            saveData = true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();//TODO catch
                    }
                }
                entrantSpecialityQM.addWhere("student", ECriteria.EQUAL, studentId1);

                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);

                flag = Flag.SPECIALITY;
                mainDataButton.addStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.addStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(specTW);
                contentHL.addComponent(nextIdDocButton);
                contentHL.setComponentAlignment(nextIdDocButton, Alignment.MIDDLE_CENTER);
                registrationHSP.addComponent(contentHL);
            }
        });

        idDocButton = createFormButton("identity.document", caption);
        idDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);

                flag = Flag.ID_DOC;
                mainDataButton.removeStyleName("actived");
                idDocButton.addStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(passportGFW);
                contentHL.addComponent(nextMilitaryButton);
                contentHL.setComponentAlignment(nextMilitaryButton, Alignment.MIDDLE_CENTER);
            }
        });

        militaryButton = createFormButton("military.document", styleName);
        militaryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.MILITARY;
                mainDataButton.removeStyleName("actived");
                idDocButton.addStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.addStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(militaryGFW);
                contentHL.addComponent(nextDisabilityButton);
                contentHL.setComponentAlignment(nextDisabilityButton, Alignment.MIDDLE_CENTER);
            }
        });

        disabilityButton = createFormButton("disability.document", styleName);
        disabilityButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);

                flag = Flag.DISABILITY;
                mainDataButton.removeStyleName("actived");
                idDocButton.addStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.addStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(disabilityGFW);
                contentHL.addComponent(nextRepatriateButton);
                contentHL.setComponentAlignment(nextRepatriateButton, Alignment.MIDDLE_CENTER);
            }
        });

        repatriateButton = createFormButton("repatriate.document", styleName);
        repatriateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.REPATRIATE;
                mainDataButton.removeStyleName("actived");
                idDocButton.addStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.addStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(repatriateGFW);
                contentHL.addComponent(nextEduDocButton);
                contentHL.setComponentAlignment(nextEduDocButton, Alignment.MIDDLE_CENTER);
            }
        });

        eduDocButton = createFormButton("education.document", caption);
        eduDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.EDU_DOC;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.addStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(educDocGFW);
                contentHL.addComponent(nextEduDocsButton);
                contentHL.setComponentAlignment(nextEduDocsButton, Alignment.MIDDLE_CENTER);
            }
        });

        eduDocsButton = createFormButton("education.documents", styleName);
        eduDocsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.EDU_DOCS;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.addStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.addStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                documentsTW = new TableWidget(EDUCATION_DOC.class);
                documentsTW.addEntityListener(ApplicantsForm.this);
                documentsTW.setWidth("667px");
                documentsTW.addStyleName("toTop");
                FormModel docFM = new FormModel(EDUCATION_DOC.class, true);
                docFM.getFieldModel("schoolCountry").setRequired(true);
                docFM.getFieldModel("language").setRequired(true);
                docFM.getFieldModel("schoolCertificateType").setRequired(true);
                docFM.getFieldModel("schoolRegion").setRequired(true);
                docFM.getFieldModel("schoolAddress").setRequired(true);

                DBTableModel educationTM = (DBTableModel) documentsTW.getWidgetModel();
                educationTM.getColumnModel("entryYear").setAlignment(Align.CENTER);
                educationTM.getColumnModel("endYear").setAlignment(Align.CENTER);
                QueryModel educationQM = educationTM.getQueryModel();
                educationUDFI = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
                educationQM.addWhere(educationUDFI, "deleted", Boolean.FALSE);
                ID userId1 = ID.valueOf(-1);
                if (!dataFM.isCreateNew()) {
                    try {
                        userId1 = dataFM.getEntity().getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                educationQM.addWhereAnd(educationUDFI, "user", ECriteria.EQUAL, userId1);

                contentHL.removeAllComponents();
                contentHL.addComponent(documentsTW);
                contentHL.addComponent(nextPreemRightButton);
                contentHL.setComponentAlignment(nextPreemRightButton, Alignment.MIDDLE_CENTER);
            }
        });

        preemRightButton = createFormButton("preemptive.right", styleName);
        preemRightButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.PREEM_RIGHT;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.addStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.addStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                ID userId1 = ID.valueOf(-1);
                if (!dataFM.isCreateNew()) {
                    try {
                        userId1 = dataFM.getEntity().getId();
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
                languagesTW = new TableWidget(V_USER_LANGUAGE.class);
                languagesTW.addEntityListener(ApplicantsForm.this);
                languagesTW.setWidth("667px");
                DBTableModel languageTM = (DBTableModel) languagesTW.getWidgetModel();
                QueryModel languageQM = languageTM.getQueryModel();
                languageQM.addWhere("user", ECriteria.EQUAL, userId1);

                VerticalLayout preemLang = new VerticalLayout();
                preemLang.addComponent(preemptiveRightGFW);
                preemLang.addComponent(languagesTW);

                contentHL.removeAllComponents();
                contentHL.addComponent(preemLang);
                contentHL.addComponent(nextMedButton);
                contentHL.setComponentAlignment(nextMedButton, Alignment.MIDDLE_CENTER);
            }
        });

        medButton = createFormButton("medical.checkup", caption);
        medButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.MED;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.addStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");

                medicalCheckupTW = new TableWidget(V_MEDICAL_CHECKUP.class);
                medicalCheckupTW.addEntityListener(ApplicantsForm.this);
                medicalCheckupTW.setWidth("667px");
                DBTableModel medicalCheckupTM = (DBTableModel) medicalCheckupTW.getWidgetModel();
                medicalCheckupTM.getColumnModel("allowWork").setInTable(false);
                QueryModel medicalCheckupQM = medicalCheckupTM.getQueryModel();
                medicalCheckupQM.addWhere("deleted", Boolean.FALSE);
                ID userId2 = ID.valueOf(-1);
                if (!dataAFW.getWidgetModel().isCreateNew()) {
                    try {
                        userId2 = dataAFW.getWidgetModel().getEntity().getId();
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
                medicalCheckupQM.addWhereAnd("user", ECriteria.EQUAL, userId2);

                FormModel medicalCheckupFM = medicalCheckupTM.getFormModel();
                medicalCheckupFM.getFieldModel("allowWork").setInEdit(false);
                medicalCheckupFM.getFieldModel("allowWork").setInView(false);

                contentHL.removeAllComponents();
                contentHL.addComponent(medicalCheckupTW);
                contentHL.addComponent(nextUntButton);
                contentHL.setComponentAlignment(nextUntButton, Alignment.MIDDLE_CENTER);
            }
        });

        untButton = createFormButton("unt", caption);
        untButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.UNT;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.addStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");

                ID untCertificateId = ID.valueOf(-1);
                if (!untCertificateFM.isCreateNew()) {
                    UNT_CERTIFICATE untCertificate = null;
                    try {
                        untCertificate = (UNT_CERTIFICATE) untCertificateFM.getEntity();
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                    if (untCertificate != null) {
                        untCertificateId = untCertificate.getId();
                    }
                }
                untRatesTW = new TableWidget(V_UNT_CERT_SUBJECT.class);
                untRatesTW.addEntityListener(ApplicantsForm.this);
                untRatesTW.setWidth("667px");
                DBTableModel untRatesTM = (DBTableModel) untRatesTW.getWidgetModel();
                QueryModel untRatesQM = untRatesTM.getQueryModel();
                untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, untCertificateId);

                Button saveButton = createSaveButton();
                saveButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        if (untCertificateFM.isModified()) {
                            certificateGFW.save();
                        } else {
                            Message.showError("fill main data");
                        }
                    }
                });

                VerticalLayout dataUNT = new VerticalLayout();
                dataUNT.addComponent(certificateGFW);
                dataUNT.addComponent(saveButton);
                dataUNT.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
                dataUNT.addComponent(untRatesTW);

                contentHL.removeAllComponents();
                contentHL.addComponent(dataUNT);
                contentHL.addComponent(nextGrantDocButton);
                contentHL.setComponentAlignment(nextGrantDocButton, Alignment.MIDDLE_CENTER);
            }
        });

        grantDocButton = createFormButton("grant.document", styleName);
        grantDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.GRANT_DOC;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.addStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.addStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(grantGFW);
                contentHL.addComponent(nextMotherButton);
                contentHL.setComponentAlignment(nextMotherButton, Alignment.MIDDLE_CENTER);
            }
        });

        motherButton = createFormButton("parents.data.mother", caption);
        motherButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.MOTHER;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.addStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(motherGFW);
                contentHL.addComponent(nextFatherButton);
                contentHL.setComponentAlignment(nextFatherButton, Alignment.MIDDLE_CENTER);
            }
        });

        fatherButton = createFormButton("parents.data.father", styleName);
        fatherButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.FATHER;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.addStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.addStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(fatherGFW);
                contentHL.addComponent(nextContractButton);
                contentHL.setComponentAlignment(nextContractButton, Alignment.MIDDLE_CENTER);
            }
        });

        contractButton = createFormButton("contract.data", caption);
        contractButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.CONTRACT;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.addStyleName("actived");
                moreButton.removeStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");
                contentHL.removeAllComponents();
                contentHL.addComponent(dataContractGFW);
                contentHL.addComponent(nextMoreButton);
                contentHL.setComponentAlignment(nextMoreButton, Alignment.MIDDLE_CENTER);
            }
        });

        moreButton = createFormButton("inform.more", caption);
        moreButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM, educationFM, untCertificateFM,
                        grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                flag = Flag.DEFAULT_FLAG;
                mainDataButton.removeStyleName("actived");
                idDocButton.removeStyleName("actived");
                eduDocButton.removeStyleName("actived");
                medButton.removeStyleName("actived");
                untButton.removeStyleName("actived");
                motherButton.removeStyleName("actived");
                contractButton.removeStyleName("actived");
                moreButton.addStyleName("actived");
                specButton.removeStyleName("activedCh");
                militaryButton.removeStyleName("activedCh");
                disabilityButton.removeStyleName("activedCh");
                repatriateButton.removeStyleName("activedCh");
                eduDocsButton.removeStyleName("activedCh");
                preemRightButton.removeStyleName("activedCh");
                grantDocButton.removeStyleName("activedCh");
                fatherButton.removeStyleName("activedCh");
                factAddressButton.removeStyleName("activedCh");
                regAddressButton.removeStyleName("activedCh");

                awardsTW = new TableWidget(V_USER_AWARD.class);
                awardsTW.addEntityListener(ApplicantsForm.this);
                awardsTW.setWidth("667px");
                DBTableModel awardsTM = (DBTableModel) awardsTW.getWidgetModel();
                QueryModel awardsQM = awardsTM.getQueryModel();
                ID studentId2 = ID.valueOf(-1);
                if (!dataAFW.getWidgetModel().isCreateNew()) {
                    try {
                        studentId2 = dataAFW.getWidgetModel().getEntity().getId();
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
                awardsQM.addWhere("user", ECriteria.EQUAL, studentId2);

                socialCategoriesTW = new TableWidget(V_USER_SOCIAL_CATEGORY.class);
                socialCategoriesTW.addEntityListener(ApplicantsForm.this);
                socialCategoriesTW.setWidth("667px");
                DBTableModel socialCategoriesTM = (DBTableModel) socialCategoriesTW.getWidgetModel();
                QueryModel socialCategoriesQM = socialCategoriesTM.getQueryModel();
                ID userId = ID.valueOf(-1);
                if (!dataAFW.getWidgetModel().isCreateNew()) {
                    try {
                        userId = dataAFW.getWidgetModel().getEntity().getId();
                    } catch (Exception e) {
                    }
                }
                socialCategoriesQM.addWhere("user", ECriteria.EQUAL, userId);

                VerticalLayout dopData = new VerticalLayout();
                dopData.addComponent(socialCategoriesTW);
                dopData.addComponent(awardsTW);

                contentHL.removeAllComponents();
                contentHL.addComponent(dopData);
                contentHL.addComponent(nextFinishButton);
                contentHL.setComponentAlignment(nextFinishButton, Alignment.MIDDLE_CENTER);
            }
        });

        finishButton = new Button();
        finishButton.setCaption(getUILocaleUtil().getCaption("exit"));
        finishButton.setWidth("230px");
        finishButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM, userPassportFM, militaryDocFM, disabilityDocFM, repatriateDocFM, preemptiveRightFM,
                        educationFM, untCertificateFM, grantDocFM, addressFactFM, addressRegFM, motherFM, fatherFM, dataContractFM);
                if (!saveData) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                } else if (!saveSpec) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.speciality"));
                } else if (!savePass) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.passport"));
                } else if (!saveEduc) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.educ"));
                } else if (!saveUNT) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.unt"));
                } else {
                    mainDataButton.setEnabled(false);
                    idDocButton.setEnabled(false);
                    eduDocButton.setEnabled(false);
                    medButton.setEnabled(false);
                    untButton.setEnabled(false);
                    motherButton.setEnabled(false);
                    contractButton.setEnabled(false);
                    moreButton.setEnabled(false);
                    specButton.setEnabled(false);
                    militaryButton.setEnabled(false);
                    disabilityButton.setEnabled(false);
                    repatriateButton.setEnabled(false);
                    eduDocsButton.setEnabled(false);
                    preemRightButton.setEnabled(false);
                    grantDocButton.setEnabled(false);
                    fatherButton.setEnabled(false);
                    factAddressButton.setEnabled(false);
                    regAddressButton.setEnabled(false);
                    finishButton.setEnabled(false);

                    STUDENT_EDUCATION studentEducation = new STUDENT_EDUCATION();
                    try {
                        STUDENT student = (STUDENT) dataFM.getEntity();
                        studentEducation.setStudent(student);
                        SPECIALITY speciality = (student).getEntrantSpecialities().iterator().next().getSpeciality();
                        studentEducation.setFaculty(speciality.getDepartment().getParent());
                        studentEducation.setChair(speciality.getDepartment());
                        studentEducation.setSpeciality(speciality);
                        studentEducation.setStudyYear(SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class, ID.valueOf(1)));
                        studentEducation.setLanguage(SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(LANGUAGE.class, ID.valueOf(1)));
                        studentEducation.setEducationType(SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION_TYPE.class, ID.valueOf(1)));
                        DateFormat uriDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        studentEducation.setEntryDate(uriDateFormat.parse(beginYear + "-09-01"));
                        studentEducation.setStatus(SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(1)));
                        studentEducation.setCreated(new Date());

                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentEducation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Label mess = new Label("<br><br><br><center><strong>Вы успешно прошли регистрацию!!!</strong><br>");
                    mess.setContentMode(ContentMode.HTML);
                    messForm = new VerticalLayout();
                    messForm.addComponent(mess);

                    Button againButton = new Button(getUILocaleUtil().getCaption("back.to.new.applicant.registration"));
                    againButton.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent clickEvent) {
                            RegisterApplicantsView.regButton.click();
                        }
                    });
                    messForm.addComponent(againButton);

                    registrationHSP.removeAllComponents();
                    contentHL.removeAllComponents();
                    contentHL.addComponent(messForm);
                    contentHL.setComponentAlignment(messForm, Alignment.BOTTOM_CENTER);
                    registrationHSP.addComponent(buttonsVL);
                    registrationHSP.addComponent(contentHL);
                }
            }
        });
    }

    private VerticalLayout getPhotoVL() {
        VerticalLayout photoAndButtonVL = new VerticalLayout();

        PhotoWidget userPW = new PhotoWidget(userPhotoBytes);
        userPW.setPhotoHeight(290, Unit.PIXELS);
        userPW.setPhotoWidth(230, Unit.PIXELS);
        userPW.setSaveButtonVisible(false);
        userPW.addListener(ApplicantsForm.this);
        photoAndButtonVL.addComponent(userPW);
        photoAndButtonVL.setComponentAlignment(userPW, Alignment.TOP_CENTER);

        photoAndButtonVL.addComponent(nextFactAddressButton);
        photoAndButtonVL.setComponentAlignment(nextFactAddressButton, Alignment.MIDDLE_CENTER);
        return photoAndButtonVL;
    }

    private void flagSave(Flag flag, FormModel dataFM, FormModel userPassportFM, FormModel militaryDocFM,
                          FormModel disabilityDocFM, FormModel repatriateDocFM, FormModel preemptiveRightFM,
                          FormModel educationFM, FormModel untCertificateFM, FormModel grantDocFM,
                          FormModel addressFactFM, FormModel addressRegFM, FormModel motherFM,
                          FormModel fatherFM, FormModel dataContractFM) {

        switch (flag) {
            case MAIN_DATA:
                if (dataFM.isModified() && dataAFW.save())
                    saveData = true;
                break;
            case SPECIALITY:
                if (specTW.getEntityCount() > 0) {
                    saveSpec = true;
                }
                break;
            case ID_DOC:
                if (userPassportFM.isModified() && passportGFW.save())
                    savePass = true;
                break;
            case MILITARY:
                if (militaryDocFM.isModified())
                    militaryGFW.save();
                break;
            case DISABILITY:
                if (disabilityDocFM.isModified())
                    disabilityGFW.save();
                break;
            case REPATRIATE:
                if (repatriateDocFM.isModified())
                    repatriateGFW.save();
                break;
            case EDU_DOC:
                if (educationFM.isModified() && educDocGFW.save())
                    saveEduc = true;
                break;
            case PREEM_RIGHT:
                if (preemptiveRightFM.isModified())
                    preemptiveRightGFW.save();
                break;
            case UNT:
                if (untRatesTW.getEntityCount() > 1) {
                    saveUNT = true;
                }
                break;
            case GRANT_DOC:
                if (grantDocFM.isModified())
                    grantGFW.save();
                break;
            case MOTHER:
                if (motherFM.isModified())
                    motherGFW.save();
                break;
            case FATHER:
                if (fatherFM.isModified())
                    fatherGFW.save();
                break;
            case FACT_ADDRESS:
                if (addressFactFM.isModified())
                    addressFactGFW.save();
                break;
            case REG_ADDRESS:
                if (addressRegFM.isModified())
                    addressRegGFW.save();
                break;
            case CONTRACT:
                if (dataContractFM.isModified())
                    dataContractGFW.save();
                break;
            default:
                break;
        }
    }

    private void addFormButtons() {
        buttonsVL.addComponent(mainDataButton);
        buttonsVL.addComponent(factAddressButton);
        buttonsVL.addComponent(regAddressButton);
        buttonsVL.addComponent(specButton);
        buttonsVL.addComponent(idDocButton);
        buttonsVL.addComponent(militaryButton);
        buttonsVL.addComponent(disabilityButton);
        buttonsVL.addComponent(repatriateButton);
        buttonsVL.addComponent(eduDocButton);
        buttonsVL.addComponent(eduDocsButton);
        buttonsVL.addComponent(preemRightButton);
        buttonsVL.addComponent(medButton);
        buttonsVL.addComponent(untButton);
        buttonsVL.addComponent(grantDocButton);
        buttonsVL.addComponent(motherButton);
        buttonsVL.addComponent(fatherButton);
        buttonsVL.addComponent(contractButton);
        buttonsVL.addComponent(moreButton);
        buttonsVL.addComponent(finishButton);
        buttonsVL.setSpacing(false);
        registrationHSP.addComponent(buttonsVL);
    }

    private Button createNextButton(Button clickButton, String caption) {
        String styleName = "nextStyle";
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        temp.setStyleName(styleName);
        temp.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                clickButton.click();
            }
        });
        return temp;
    }

    private Button createFormButton(String caption, String styleName) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        temp.setWidth("230px");
        temp.setStyleName(styleName);
        return temp;
    }

    private FKFieldModel createFKFieldModel(String caption, FormModel fm) {
        FKFieldModel fkFM = (FKFieldModel) fm.getFieldModel(caption);
        QueryModel fkQM = fkFM.getQueryModel();
        if (caption.equals("country"))
            fkQM.addWhereNull("parent");
        else
            fkQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        return fkFM;
    }

    private FormModel createFormModel(GridFormWidget widget, StringBuilder sb, String caption) {
        FormModel formModel = widget.getWidgetModel();
        formModel.setTitleResource(caption);
        formModel.setErrorMessageTitle(sb.toString());
        formModel.setButtonsVisible(false);
        formModel.getFieldModel("email").getValidators().add(new EmailValidator("Введён некорректный E-mail"));
        return formModel;
    }

    private boolean preSaveSpeciality(Entity e, boolean isNew) {
        V_ENTRANT_SPECIALITY ves = (V_ENTRANT_SPECIALITY) e;
        ENTRANT_SPECIALITY es;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {

            es = new ENTRANT_SPECIALITY();
            try {
                STUDENT s = (STUDENT) fm.getEntity();
                es.setStudent(s);
                es.setUniversity(ves.getUniversity());
                es.setSpeciality(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SPECIALITY.class, ves.getSpeciality().getId()));
                es.setLanguage(ves.getLanguage());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(es);

                QueryModel entrantSpecialityQM = ((DBTableModel) specTW.getWidgetModel()).getQueryModel();
                entrantSpecialityQM.addWhere("student", ECriteria.EQUAL, s.getId());

                specTW.refresh();

                if (es.getUniversity().getId().equals(ID.valueOf(421))) {
                    if ("000000000000".equals(s.getCode())) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(s.getCreated());
                        Integer year = calendar.get(Calendar.YEAR);

                        LEVEL level = s.getLevel();

                        DEPARTMENT faculty = es.getSpeciality().getDepartment().getParent();

                        QueryModel<V_KBTU_ENTRANTS> qm = new QueryModel<>(V_KBTU_ENTRANTS.class);
                        qm.addSelect("id", EAggregate.COUNT);
                        qm.addWhere("level", ECriteria.EQUAL, level.getId());
                        qm.addWhereAnd("createdYear", ECriteria.EQUAL, year);
                        qm.addWhereAnd("faculty", ECriteria.EQUAL, faculty.getId());

                        try {
                            Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
                            String code = CommonUtils.getCode(count);

                            code = faculty.getCode() + code;
                            if (level.getId().equals(ID.valueOf(2))) {
                                code = "MD" + code;
                            } else {
                                code = "BD" + code;
                            }
                            code = year.toString().substring(2) + code;
                            s.setCode(code);
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(s);
                            dataAFW.refresh();
                        } catch (Exception ex) {
                            LOG.error("Unable to generate code for entrant: ", ex);
                            Message.showError(ex.getMessage());
                        }
                    }
                }

                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a entrant speciality: ", ex);
            }
        } else {
            try {
                es = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANT_SPECIALITY.class, ves.getId());
                es.setUniversity(ves.getUniversity());
                es.setSpeciality(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SPECIALITY.class, ves.getSpeciality().getId()));
                es.setLanguage(ves.getLanguage());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(es);
                specTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a entrant speciality: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveData(Entity e, boolean isNew) {
        STUDENT s = (STUDENT) e;

        if (isNew) {
            s.setCreated(new Date());
            try {
                s.setPasswd("12345678");
                s.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS"));

                String year = beginYear.toString().substring(2, 4);
                String code = getCode(year);
                s.setLogin(code);
                s.setCode(code);
                s.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(s);
                dataAFW.getWidgetModel().loadEntity(s.getId());

                if (userPhotoChanged) {
                    USER_PHOTO userPhoto = new USER_PHOTO();
                    userPhoto.setUser(s);
                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                }

                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to save new applicant: ", ex);
                Message.showError("Ошибка сохранения основных данных");
            }
        } else {
            s.setUpdated(new Date());
            s.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(s);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a entrant: ", ex);
            }
        }
        return false;
    }

    private String getCode(String beginYear) {
        String code = null;
        try {
            Integer usersCode = SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS_CODE").getId().intValue();
            if (usersCode < 10) {
                code = beginYear + "000" + usersCode;
            } else if (usersCode < 100) {
                code = beginYear + "00" + usersCode;
            } else if (usersCode < 1000) {
                code = beginYear + "0" + usersCode;
            } else if (usersCode < 10000) {
                code = beginYear + usersCode;
            } else {
                SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS_CODE").
                        setId(BigInteger.valueOf(0));//TODO check
                code = getCode(beginYear);
            }
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return code;
    }

    private boolean preSaveContract(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        STUDENT_CONTRACT md = (STUDENT_CONTRACT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a contract doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a contract doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) dataContractGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save contract doc copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete contract doc copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSavePassport(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }

        USER_PASSPORT p = (USER_PASSPORT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                p.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                p.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(p);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a passport: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(p);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a passport: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) passportGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(p);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save passport copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete passport copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveEducationDoc(Entity e, boolean isNew) {
        EDUCATION_DOC ed = (EDUCATION_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                ed.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                ed.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(ed);

                QueryModel educationQM = ((DBSelectModel) documentsTW.getWidgetModel()).getQueryModel();
                educationQM.addWhere(educationUDFI, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());

                documentsTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a education doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                documentsTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a education doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) documentsTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(ed);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save education doc copy: ", ex);
                }
            }
        }

        deleteFiles(flfm);

        return false;
    }

    private void deleteFiles(FileListFieldModel flfm) {
        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete repatriate doc copy: ", ex);
            }
        }
    }

    private boolean preSaveDoc(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        EDUCATION_DOC md = (EDUCATION_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a Education document: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a Education document: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) educDocGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save Education document copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete Education document copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveMilitary(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        MILITARY_DOC md = (MILITARY_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a military doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a military doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) militaryGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save military doc copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete military doc copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveDisability(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        DISABILITY_DOC dd = (DISABILITY_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                dd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                dd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(dd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a disability doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a disability doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) disabilityGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(dd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save disability doc copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete disability doc copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveRepatriate(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        REPATRIATE_DOC rd = (REPATRIATE_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                rd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                rd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(rd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a repatriate doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(rd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a repatriate doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) repatriateGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(rd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save repatriate doc copy: ", ex);
                }
            }
        }

        deleteFiles(flfm);

        return false;
    }

    private boolean preSaveLanguage(Entity e, boolean isNew) {
        V_USER_LANGUAGE vul = (V_USER_LANGUAGE) e;
        if (isNew) {
            try {
                STUDENT s = (STUDENT) dataAFW.getWidgetModel().getEntity();
                USER_LANGUAGE ul = new USER_LANGUAGE();
                ul.setUser(s);
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ul);

                QueryModel languageQM = ((DBTableModel) languagesTW.getWidgetModel()).getQueryModel();
                languageQM.addWhere("user", ECriteria.EQUAL, s.getId());

                languagesTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a language: ", ex);
            }
        } else {
            try {
                USER_LANGUAGE ul = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_LANGUAGE.class, vul.getId());
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ul);
                languagesTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a language: ", ex);
            }
        }

        return false;
    }

    private boolean preSavePreemptiveRight(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        PREEMPTIVE_RIGHT pr = (PREEMPTIVE_RIGHT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                pr.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                pr.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(pr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a preemptive right: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a preemptive right: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) preemptiveRightGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(pr);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save preemptive right copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete preemptive right copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveGrant(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        GRANT_DOC gd = (GRANT_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                gd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                gd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(gd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a grant doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(gd);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a grant doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) grantGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(gd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save grant doc copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete grant doc copy: ", ex);
            }
        }

        return false;
    }

    private boolean preSaveMedicalCheckup(Entity e, boolean isNew) {
        V_MEDICAL_CHECKUP vmc = (V_MEDICAL_CHECKUP) e;
        MEDICAL_CHECKUP mc = null;
        FormModel fm = dataAFW.getWidgetModel();
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
                LOG.error("Unable to create a medical checkup: ", ex);
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
                LOG.error("Unable to merge a medical checkup: ", ex);
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
                    LOG.error("Unable to save medical checkup copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete medical checkup copy: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveUNTCertificate(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        UNT_CERTIFICATE uc = (UNT_CERTIFICATE) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                uc.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                uc.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(uc);
                certificateGFW.getWidgetModel().loadEntity(uc.getId());
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a UNT certificate: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(uc);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a UNT certificate: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) certificateGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(uc);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    LOG.error("Unable to save UNT certificate copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete UNT certificate copy: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveUNTRates(Entity e, boolean isNew) {
        V_UNT_CERT_SUBJECT vucs = (V_UNT_CERT_SUBJECT) e;
        UNT_CERT_SUBJECT ucs;
        FormModel fm = certificateGFW.getWidgetModel();
        if (isNew) {
            ucs = new UNT_CERT_SUBJECT();
            try {
                UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) fm.getEntity();
                ucs.setUntCertificate(untCertificate);
                ucs.setUntSubject(vucs.getUntSubject());
                ucs.setRate(vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ucs);
                untCertificate.setRate(untCertificate.getRate() + vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                saveUNT = true;
                QueryModel untRatesQM = ((DBTableModel) untRatesTW.getWidgetModel()).getQueryModel();
                untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, fm.getEntity().getId());

                untRatesTW.refresh();
                certificateGFW.refresh();
                showSavedNotification();

            } catch (Exception ex) {
                LOG.error("Unable to create a UNT rate: ", ex);
            }
        } else {
            try {
                UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) fm.getEntity();
                ucs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, vucs.getId());
                ucs.setUntSubject(vucs.getUntSubject());
                ucs.setRate(vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ucs);
                untCertificate.setRate(untCertificate.getRate() + vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                saveUNT = true;
                untRatesTW.refresh();
                certificateGFW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a UNT rate: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveAddressReg(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_ADDRESS ua = (USER_ADDRESS) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                ua.setUser((STUDENT) fm.getEntity());
                ua.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(1)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a registration address: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a registration address: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveAddressFact(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_ADDRESS ua = (USER_ADDRESS) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                ua.setUser((STUDENT) fm.getEntity());
                ua.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(2)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a residential address: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a residential address: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveFather(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        STUDENT_RELATIVE sr = (STUDENT_RELATIVE) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                sr.setStudent((STUDENT) fm.getEntity());
                sr.setRelativeType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(RELATIVE_TYPE.class, ID.valueOf(1)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a fathers data: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a fathers address: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveMother(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }

        STUDENT_RELATIVE sr = (STUDENT_RELATIVE) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                sr.setStudent((STUDENT) fm.getEntity());
                sr.setRelativeType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(RELATIVE_TYPE.class, ID.valueOf(2)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a mothers data: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sr);
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a mothers address: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveAwards(Entity e, boolean isNew) {
        V_USER_AWARD vua = (V_USER_AWARD) e;
        USER_AWARD ua;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            ua = new USER_AWARD();
            try {
                STUDENT s = (STUDENT) fm.getEntity();
                ua.setUser(s);
                ua.setAward(vua.getAward());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);

                QueryModel awardsQM = ((DBTableModel) awardsTW.getWidgetModel()).getQueryModel();
                awardsQM.addWhere("user", ECriteria.EQUAL, s.getId());

                awardsTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create an award: ", ex);
            }
        } else {
            try {
                ua = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_AWARD.class, vua.getId());
                ua.setAward(vua.getAward());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                awardsTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge an award: ", ex);
            }
        }
        return false;
    }

    private boolean preSaveSocialCategories(Entity e, boolean isNew) {
        V_USER_SOCIAL_CATEGORY vusc = (V_USER_SOCIAL_CATEGORY) e;
        USER_SOCIAL_CATEGORY usc;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            usc = new USER_SOCIAL_CATEGORY();
            try {
                STUDENT s = (STUDENT) fm.getEntity();
                usc.setUser(s);
                usc.setSocialCategory(vusc.getSocialCategory());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(usc);

                QueryModel socialCategoriesQM = ((DBTableModel) socialCategoriesTW.getWidgetModel()).getQueryModel();
                socialCategoriesQM.addWhere("user", ECriteria.EQUAL, s.getId());

                socialCategoriesTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to create a social category: ", ex);
            }
        } else {
            try {
                usc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, vusc.getId());
                usc.setSocialCategory(vusc.getSocialCategory());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(usc);
                socialCategoriesTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                LOG.error("Unable to merge a social category: ", ex);
            }
        }
        return false;
    }

    private boolean check(Object source, Entity e) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("deleted", Boolean.FALSE);

        if (source.equals(documentsTW)) {
            FormModel educationFM = ((DBTableModel) documentsTW.getWidgetModel()).getFormModel();
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
                addFiles(udfQM, educationFLFM);
            } catch (Exception ex) {
                LOG.error("Unable to load education document copies: ", ex);
            }

            return true;
        } else if (source.equals(medicalCheckupTW)) {
            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
            medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

            medicalCheckupFLFM.getFileList().clear();
            medicalCheckupFLFM.getDeleteList().clear();

            udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());

            addFiles(udfQM, medicalCheckupFLFM);

            return true;
        } else if (source.equals(languagesTW)) {
            return true;
        } else if (source.equals(untRatesTW)) {
            return true;
        } else if (source.equals(specTW)) {
            FormModel entrantSpecialityFM = ((DBTableModel) specTW.getWidgetModel()).getFormModel();
            FKFieldModel specialityFKFM = (FKFieldModel) entrantSpecialityFM.getFieldModel("speciality");
            specialityFKFM.setDialogWidth(600);
            specialityFKFM.setDialogHeight(600);
            QueryModel specialityQM = specialityFKFM.getQueryModel();
            specialityQM.addWhere("deleted", Boolean.FALSE);
            return true;
        } else if (source.equals(awardsTW)) {
            return true;
        } else if (source.equals(socialCategoriesTW)) {
            return true;
        }
        return false;
    }

    private void addFiles(QueryModel<USER_DOCUMENT_FILE> udfQM, FileListFieldModel medicalCheckupFLFM) {
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
            LOG.error("Unable to load education document copies: ", ex);
        }
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(documentsTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel educationFM = ((DBTableModel) documentsTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");

            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));


            educationFM.getFieldModel("language").setRequired(true);
            educationFM.getFieldModel("schoolCertificateType").setRequired(true);
            educationFM.getFieldModel("schoolRegion").setRequired(true);
            educationFM.getFieldModel("schoolAddress").setRequired(true);
            educationFM.getFieldModel("entryYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, currentYear));
            educationFM.getFieldModel("endYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, currentYear));

            FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
            educationFLFM.permitMimeType(FileListFieldModel.JPEG);

            educationFLFM.getFileList().clear();
            educationFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(languagesTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(untRatesTW)) {
            try {
                certificateGFW.post();
            } catch (Exception e) {
                LOG.error("Failed to post: ", e);// TODO catch
            }

            if (certificateGFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(medicalCheckupTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
            medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

            medicalCheckupFLFM.getFileList().clear();
            medicalCheckupFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(specTW)) {
            saveData = true;
            if (dataAFW.getWidgetModel().isCreateNew() || dataAFW.getWidgetModel().isModified()) {
                boolean success = dataAFW.save();
                if (!success) {
                    return false;
                }
            }

            int count = specTW.getEntityCount();
            if (count == 4) {
                Message.showInfo(getUILocaleUtil().getMessage("more.records.not.required"));
                return false;
            }

            FormModel entrantSpecialityFM = ((DBTableModel) specTW.getWidgetModel()).getFormModel();
            FKFieldModel specialityFKFM = (FKFieldModel) entrantSpecialityFM.getFieldModel("speciality");
            specialityFKFM.setDialogWidth(600);
            specialityFKFM.setDialogHeight(600);
            QueryModel specialityQM = specialityFKFM.getQueryModel();
            specialityQM.addWhere("deleted", Boolean.FALSE);
            try {
                specialityQM.addWhere("level", ECriteria.EQUAL, ((STUDENT) dataAFW.getWidgetModel().getEntity()).getLevel().getId());
            } catch (Exception ex) {
                ex.printStackTrace();//TODO catch
            }

            return true;
        } else if (source.equals(awardsTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
        } else if (source.equals(socialCategoriesTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return true;
        }

        return super.preCreate(source, buttonId);
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return check(source, e) || super.onEdit(source, e, buttonId);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return check(source, e) || super.onPreview(source, e, buttonId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        if (source.equals(dataAFW)) {
            return preSaveData(e, isNew);
        } else if (source.equals(documentsTW)) {
            return preSaveEducationDoc(e, isNew);
        } else if (source.equals(educDocGFW)) {
            return preSaveDoc(e, isNew);
        } else if (source.equals(passportGFW)) {
            return preSavePassport(e, isNew);
        } else if (source.equals(specTW)) {
            return preSaveSpeciality(e, isNew);
        } else if (source.equals(languagesTW)) {
            return preSaveLanguage(e, isNew);
        } else if (source.equals(medicalCheckupTW)) {
            return preSaveMedicalCheckup(e, isNew);
        } else if (source.equals(awardsTW)) {
            return preSaveAwards(e, isNew);
        } else if (source.equals(socialCategoriesTW)) {
            return preSaveSocialCategories(e, isNew);
        } else if (source.equals(militaryGFW)) {
            return preSaveMilitary(e, isNew);
        } else if (source.equals(disabilityGFW)) {
            return preSaveDisability(e, isNew);
        } else if (source.equals(repatriateGFW)) {
            return preSaveRepatriate(e, isNew);
        } else if (source.equals(grantGFW)) {
            return preSaveGrant(e, isNew);
        } else if (source.equals(preemptiveRightGFW)) {
            return preSavePreemptiveRight(e, isNew);
        } else if (source.equals(certificateGFW)) {
            return preSaveUNTCertificate(e, isNew);
        } else if (source.equals(untRatesTW)) {
            return preSaveUNTRates(e, isNew);
        } else if (source.equals(addressRegGFW)) {
            return preSaveAddressReg(e, isNew);
        } else if (source.equals(addressFactGFW)) {
            return preSaveAddressFact(e, isNew);
        } else if (source.equals(fatherGFW)) {
            return preSaveFather(e, isNew);
        } else if (source.equals(motherGFW)) {
            return preSaveMother(e, isNew);
        } else if (source.equals(dataContractGFW)) {
            return preSaveContract(e, isNew);
        }
        return super.preSave(source, e, isNew, buttonId);
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(documentsTW)) {
            for (Entity e : entities) {
                ((EDUCATION_DOC) e).setDeleted(true);
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(entities);
                documentsTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete education docs: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(languagesTW)) {
            List<USER_LANGUAGE> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_LANGUAGE.class, e.getId()));
                } catch (Exception ex) {
                    LOG.error("Unable to delete user languages: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                languagesTW.refresh();
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
                    LOG.error("Unable to delete medical checkup: ", ex);
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
        } else if (source.equals(untRatesTW)) {
            List<UNT_CERT_SUBJECT> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, e.getId()));
                } catch (Exception ex) {
                    LOG.error("Unable to delete user UNT rates: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                untRatesTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user UNT rates: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(specTW)) {
            List<ENTRANT_SPECIALITY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANT_SPECIALITY.class, e.getId()));
                } catch (Exception ex) {
                    LOG.error("Unable to delete entrant specialities: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                specTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete entrant specialities: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(awardsTW)) {
            List<USER_AWARD> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_AWARD.class, e.getId()));
                } catch (Exception ex) {
                    LOG.error("Unable to delete user awards: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                awardsTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user awards: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(socialCategoriesTW)) {
            List<USER_SOCIAL_CATEGORY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, e.getId()));
                } catch (Exception ex) {
                    LOG.error("Unable to delete user social categories: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                socialCategoriesTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user social categories: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        return super.preDelete(source, entities, buttonId);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (!ev.getSource().equals(dataAFW)) {
            super.handleEntityEvent(ev);
        }
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
        STUDENT student = (STUDENT) e;
        if (source.equals(dataAFW)) {
            student.setCreated(new Date());
            try {
                student.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER"));
                student.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
            } catch (Exception ex) {
                LOG.error("Unable to create a social category: ", ex);
            }
            student.setCode("000000000000");
            student.setFirstName(student.getFirstName());
            student.setLastName(student.getLastName());
            student.setFirstNameEN(student.getFirstNameEN());
            student.setLastNameEN(student.getLastNameEN());
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "applicantsForm";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("regapplicant");
    }

    public STUDENT getStudent() {
        return student;
    }

    public void setStudent(STUDENT student) {
        this.student = student;
    }

    @Override
    public void handlePhotoWidgetEvent(PhotoWidgetEvent photoWidgetEvent) {
        if (photoWidgetEvent.getEvent() == PhotoWidgetEvent.CHANGED) {
            userPhotoBytes = photoWidgetEvent.getBytes();
            userPhotoFilename = photoWidgetEvent.getFilename();
            userPhotoChanged = true;
        }
    }

}
