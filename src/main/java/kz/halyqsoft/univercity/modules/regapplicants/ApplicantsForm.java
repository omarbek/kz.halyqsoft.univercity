package kz.halyqsoft.univercity.modules.regapplicants;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.Flag;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.SchoolCountryChangeListener;
import kz.halyqsoft.univercity.utils.register.*;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

    private Map<String, Integer> fontMap;

    private TableWidget specTW, documentsTW, languagesTW, medicalCheckupTW;
    private TableWidget awardsTW, socialCategoriesTW;

    private Button form;
    private Button mainDataButton, factAddressButton, regAddressButton, specButton;
    private Button idDocButton, militaryButton, disabilityButton, repatriateButton;
    private Button eduDocButton, eduDocsButton, preemRightButton;
    private Button medButton;
    private Button untButton, grantDocButton;
    private Button motherButton, fatherButton;
    private Button contractButton, moreButton;
    private Button finishButton;
    private Button downloadButton;

    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;

    private boolean saveData, saveSpec, savePass, saveEduc, saveUNT, saveFactAddress;
    private Integer beginYear;

    private Flag flag;
    private PreemptiveRight preemptiveRight;
    private EducationDoc educationDoc;
    private Parent parent;
    private Address address;
    private Unt unt;
    private Disability disability;
    private Military military;
    private Grant grant;
    private Contract contract;
    private Repatriate repatriate;
    private Passport passport;

    private static final int FATHER = 1;
    private static final int MOTHER = 2;
    private static final int ADDRESS_REG = 1;
    private static final int ADDRESS_FACT = 2;

    ApplicantsForm(final FormModel dataFM, ENTRANCE_YEAR entranceYear) throws Exception {
        super();

        beginYear = entranceYear.getBeginYear();
        setBackButtonVisible(false);
        registrationHSP = new HorizontalSplitPanel();
        registrationHSP.setSplitPosition(20);
        registrationHSP.setSizeFull();

        fontMap = new HashMap<>();
        fontMap.put("Bold", Font.BOLD);
        fontMap.put("Normal", Font.NORMAL);
        fontMap.put("Italic", Font.ITALIC);
        fontMap.put("Underline", Font.UNDERLINE);

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

        setAdviser(dataFM, "advisor");
        setAdviser(dataFM, "coordinator");

        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("userDocument", ECriteria.EQUAL, null);
        udfQM.addWhereAnd("deleted", Boolean.FALSE);

        preemptiveRight = new PreemptiveRight(dataAFW, this);
        preemptiveRight.create(udfQM);

        passport = new Passport(dataAFW, this);
        passport.create(udfQM);

        military = new Military(dataAFW, this);
        military.create(udfQM);

        disability = new Disability(dataAFW, this);
        disability.create(udfQM);

        repatriate = new Repatriate(dataAFW, this);
        repatriate.create(udfQM);

        grant = new Grant(dataAFW, this);
        grant.create(udfQM);

        unt = new Unt(dataAFW, this);
        unt.create(udfQM);

        address = new Address(dataAFW, this);
        address.create("address.registration", ADDRESS_REG);
        address.create("address.residential", ADDRESS_FACT);

        parent = new Parent(dataAFW, this);
        parent.create("parents.data.father", FATHER);
        parent.create("parents.data.mother", MOTHER);

        contract = new Contract(dataAFW, this);
        contract.create(udfQM, military.getMilitaryFLFM());

        educationDoc = new EducationDoc(dataAFW, this, documentsTW, educationUDFI);
        educationDoc.create(udfQM);

        createFormButtons(dataFM);

        addFormButtons();

        form.click();
    }

    private void setAdviser(FormModel dataFM, String field) {
        FKFieldModel advisorFM = (FKFieldModel) dataFM.getFieldModel(field);
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
            CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
        }
    }

    private void createFormButtons(FormModel dataFM) {
        form = createFormButton("regapplicant.main.data");
        form.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getContent().removeAllComponents();
                flag = Flag.MAIN_DATA;
                saveData = false;
                saveSpec = false;
                saveEduc = false;
                savePass = false;
                saveUNT = false;
                saveFactAddress = false;
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

        mainDataButton = createFormButton("regapplicant.main.data");
        mainDataButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.MAIN_DATA;
                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(dataAFW);

                VerticalLayout photoAndButtonVL = getPhotoVL();
                contentHL.addComponent(photoAndButtonVL);

                registrationHSP.addComponent(contentHL);
            }
        });

        factAddressButton = createFormButton("address.residential");
        factAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.FACT_ADDRESS, address.getAddressFactGFW(), regAddressButton);
            }
        });

        regAddressButton = createFormButton("address.registration");
        regAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.REG_ADDRESS, address.getAddressRegGFW(), specButton);
            }
        });

        specButton = createFormButton("speciality");
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

                flagSave(flag, dataFM);

                flag = Flag.SPECIALITY;
                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(specTW);
                Button nextButton = createNextButton(idDocButton, "next");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
                registrationHSP.addComponent(contentHL);
            }
        });

        idDocButton = createFormButton("identity.document");
        idDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.ID_DOC, passport.getMainGFW(), militaryButton);
            }
        });

        militaryButton = createFormButton("military.document");
        militaryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.MILITARY, military.getMainGFW(), disabilityButton);
            }
        });

        disabilityButton = createFormButton("disability.document");
        disabilityButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.DISABILITY, disability.getMainGFW(), repatriateButton);
            }
        });

        repatriateButton = createFormButton("repatriate.document");
        repatriateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.REPATRIATE, repatriate.getMainGFW(), eduDocButton);
            }
        });

        eduDocButton = createFormButton("education.document");
        eduDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.EDU_DOC, educationDoc.getMainGFW(), eduDocsButton);
            }
        });

        eduDocsButton = createFormButton("education.documents");
        eduDocsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.EDU_DOCS;
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
                Button nextButton = createNextButton(preemRightButton, "next");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
            }
        });

        preemRightButton = createFormButton("preemptive.right");
        preemRightButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.PREEM_RIGHT;
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
                preemLang.addComponent(preemptiveRight.getMainGFW());
                preemLang.addComponent(languagesTW);

                contentHL.removeAllComponents();
                contentHL.addComponent(preemLang);
                Button nextButton = createNextButton(medButton, "next");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
            }
        });

        medButton = createFormButton("medical.checkup");
        medButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.MED;

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
                Button nextButton = createNextButton(untButton, "next");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
            }
        });

        untButton = createFormButton("unt");
        untButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.UNT;

                Button saveButton = unt.createRates(createSaveButton());

                VerticalLayout dataUNT = new VerticalLayout();
                dataUNT.addComponent(unt.getCertificateGFW());
                dataUNT.addComponent(saveButton);
                dataUNT.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
                dataUNT.addComponent(unt.getUntRatesTW());

                contentHL.removeAllComponents();
                contentHL.addComponent(dataUNT);
                Button nextButton = createNextButton(grantDocButton, "next");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
            }
        });

        grantDocButton = createFormButton("grant.document");
        grantDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.GRANT_DOC, grant.getMainGFW(), motherButton);
            }
        });

        motherButton = createFormButton("parents.data.mother");
        motherButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.MOTHER, parent.getMotherGFW(), fatherButton);
            }
        });

        fatherButton = createFormButton("parents.data.father");
        fatherButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.FATHER, parent.getFatherGFW(), contractButton);
            }
        });

        contractButton = createFormButton("contract.data");
        contractButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.CONTRACT, contract.getMainGFW(), moreButton);
            }
        });

        moreButton = createFormButton("inform.more");
        moreButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.DEFAULT_FLAG;

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
                Button nextButton = createNextButton(finishButton, "exit");
                contentHL.addComponent(nextButton);
                contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
            }
        });

        finishButton = new Button();
        finishButton.setCaption(getUILocaleUtil().getCaption("exit"));
        finishButton.setWidth("230px");


        finishButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flagSave(flag, dataFM);
                if (!saveData) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                } else if (!saveFactAddress) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.address"));
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

                    downloadButton = new Button();
                    downloadButton.setCaption(getUILocaleUtil().getCaption("download"));

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
                        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);


                        USER_ADDRESS userAddress = new USER_ADDRESS();
                        QueryModel<USER_ADDRESS> userAddressQueryModel = new QueryModel<>(USER_ADDRESS.class);
                        userAddressQueryModel.addWhere("user", ECriteria.EQUAL, student.getId());
                        userAddressQueryModel.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(ADDRESS_FACT));
                        userAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userAddressQueryModel);

                        StreamResource myResource = createResource(student.toString(), studentEducation.getFaculty().toString(),
                                student.getDiplomaType().toString(), student.getPhoneMobile(), userAddress.getStreet(), userAddress.getPostalCode());
                        FileDownloader fileDownloader = new FileDownloader(myResource);
                        myResource.setMIMEType("application/pdf");
                        myResource.setCacheTime(0);
                        fileDownloader.extend(downloadButton);
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

                    messForm.addComponent(downloadButton);
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


    private void addToLayout(Flag currentFlag, GridFormWidget currentGFW, Button currentButton) {
        flag = currentFlag;
        contentHL.removeAllComponents();
        contentHL.addComponent(currentGFW);
        Button nextButton = createNextButton(currentButton, "next");
        contentHL.addComponent(nextButton);
        contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
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

        Button nextButton = createNextButton(factAddressButton, "next");
        photoAndButtonVL.addComponent(nextButton);
        photoAndButtonVL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
        return photoAndButtonVL;
    }

    private StreamResource createResource(String fio, String faculty, String formaobuch, String phone, String address, String index) {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {

                String ochnii = "";
                Document docum = new Document();
                QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
                FromItem doc = propertyQM.addJoin(EJoin.INNER_JOIN, "pdfDocument", PDF_DOCUMENT.class, "id");
                propertyQM.addWhere(doc, "id", ECriteria.EQUAL, "85");
                propertyQM.addOrder("orderNumber");
                List<PDF_PROPERTY> properties = null;
                try {
                    properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(propertyQM);
                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                    PdfWriter pdfWriter = PdfWriter.getInstance(docum, byteArrayOutputStream1);
                    docum.open();
                    Paragraph title = new Paragraph("Договор", getFont(12, Font.BOLD));
                    title.setSpacingBefore(35f);
                    title.setIndentationLeft(220f);
                    docum.add(title);
                    Date date = Calendar.getInstance().getTime();
                    for (PDF_PROPERTY property : properties) {

                        String text = new String(property.getText());
                        DateFormat formatter = new SimpleDateFormat("\"dd\".MM.yyyy");
                        String today = formatter.format(date);
                        if (formaobuch.equals("Очный")) {
                            ochnii = "очной";
                        } else if (formaobuch.equals("Заочный")) {
                            ochnii = "заочной";
                        }
                        String replaced = text.replaceAll("\\$fio", fio)
                                .replaceAll("\\$money", "17000")
                                .replaceAll("\\$faculty", faculty)
                                .replaceAll("\\$DataMonthYear", today + " года")
                                .replaceAll("\\$formaobuch", ochnii)
                                .replaceAll("\\$data\\$month\\$year", today + "г.")
                                .replaceAll("\\$email", index)
                                .replaceAll("\\$rekvizit", address)
                                .replaceAll("\\$phone", "+7" + phone)
                                .replaceAll("\\$InLetters", "Сто пятьдесять тысяча");
                        ;

                        Paragraph paragraph = new Paragraph(replaced,
                                getFont(Integer.parseInt(property.getSize().toString()), fontMap.get(property.getFont().toString())));

                        paragraph.setSpacingBefore(property.getX());
                        paragraph.setIndentationLeft(property.getY());


                        docum.add(paragraph);
                    }
                    pdfWriter.close();
                    docum.close();
                    return new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, "default.pdf");
    }

    private Font getFont(int fontSize, int font) {
        String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
        BaseFont timesNewRoman = null;
        try {
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Font(timesNewRoman, fontSize, font);
    }

    private void flagSave(Flag flag, FormModel dataFM) {

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
                passport.save();
                savePass = passport.isSavePass();
                break;
            case MILITARY:
                military.save();
                break;
            case DISABILITY:
                disability.save();
                break;
            case REPATRIATE:
                repatriate.save();
                break;
            case EDU_DOC:
                educationDoc.save();
                saveEduc = educationDoc.isSaveEduc();
                break;

            case PREEM_RIGHT:
                preemptiveRight.save();
                break;
            case UNT:
                if (unt.getUntRatesTW().getEntityCount() > 1) {
                    saveUNT = true;
                }
                break;
            case GRANT_DOC:
                grant.save();
                break;
            case MOTHER:
                parent.save(MOTHER);
                break;
            case FATHER:
                parent.save(FATHER);
                break;
            case FACT_ADDRESS:
                address.save(ADDRESS_FACT);
                saveFactAddress = address.isSaveEduc();
                break;
            case REG_ADDRESS:
                address.save(ADDRESS_REG);
                break;
            case CONTRACT:
                contract.save();
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
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        temp.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                clickButton.click();
            }
        });
        return temp;
    }

    private Button createFormButton(String caption) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        temp.setWidth("230px");
        return temp;
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
                            CommonUtils.showMessageAndWriteLog("Unable to generate code for entrant", ex);
                        }
                    }
                }

                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create an entrant speciality", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to merge an entrant speciality", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to save new applicant", ex);
            }
        } else {
            s.setUpdated(new Date());
            s.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(s);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an entrant", ex);
            }
        }
        return false;
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
                CommonUtils.showMessageAndWriteLog("Unable to create an education document", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an education document", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) educationDoc.getMainGFW().getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save education document copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete education document copy", ex);
            }
        }

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
                CommonUtils.showMessageAndWriteLog("Unable to create a language", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to merge a language", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to create an award", ex);
            }
        } else {
            try {
                ua = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_AWARD.class, vua.getId());
                ua.setAward(vua.getAward());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                awardsTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an award", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to create a social category", ex);
            }
        } else {
            try {
                usc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, vusc.getId());
                usc.setSocialCategory(vusc.getSocialCategory());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(usc);
                socialCategoriesTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a social category", ex);
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
                CommonUtils.addFiles(udfQM, educationFLFM);
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

            CommonUtils.addFiles(udfQM, medicalCheckupFLFM);

            return true;
        } else if (source.equals(languagesTW)) {
            return true;
        } else if (source.equals(unt.getUntRatesTW())) {
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
            educationFM.getFieldModel("entryYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));
            educationFM.getFieldModel("endYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));

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
        } else if (source.equals(unt.getUntRatesTW())) {
            try {
                unt.getCertificateGFW().post();
            } catch (Exception e) {
                CommonUtils.showMessageAndWriteLog("Unable to pre create unt rates", e);
            }

            if (unt.getCertificateGFW().getWidgetModel().isCreateNew()) {
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
            return educationDoc.preSave(e, isNew);
        } else if (source.equals(educationDoc.getMainGFW())) {
            return preSaveDoc(e, isNew);
        } else if (source.equals(passport.getMainGFW())) {
            return passport.preSave(e, isNew);
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
        } else if (source.equals(military.getMainGFW())) {
            return military.preSave(e, isNew);
        } else if (source.equals(disability.getMainGFW())) {
            return disability.preSave(e, isNew);
        } else if (source.equals(repatriate.getMainGFW())) {
            return repatriate.preSave(e, isNew);
        } else if (source.equals(grant.getMainGFW())) {
            return grant.preSave(e, isNew);
        } else if (source.equals(preemptiveRight.getMainGFW())) {
            return preemptiveRight.preSave(e, isNew);
        } else if (source.equals(unt.getCertificateGFW())) {
            return unt.preSaveCertificate(e, isNew);
        } else if (source.equals(unt.getUntRatesTW())) {
            boolean saved = unt.preSaveRates(e, isNew);
            saveUNT = unt.isSaveUNT();
            return saved;
        } else if (source.equals(address.getAddressRegGFW())) {
            return address.preSave(e, isNew, ADDRESS_REG);
        } else if (source.equals(address.getAddressFactGFW())) {
            return address.preSave(e, isNew, ADDRESS_FACT);
        } else if (source.equals(parent.getFatherGFW())) {
            return parent.preSave(e, isNew, FATHER);
        } else if (source.equals(parent.getMotherGFW())) {
            return parent.preSave(e, isNew, MOTHER);
        } else if (source.equals(contract.getMainGFW())) {
            return contract.preSave(e, isNew);
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
                LOG.error("Unable to delete education docs", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(languagesTW)) {
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
                languagesTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user languages", ex);
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
                LOG.error("Unable to delete medical checkup", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(unt.getUntRatesTW())) {
            List<UNT_CERT_SUBJECT> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete user Unt rates", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                unt.getUntRatesTW().refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user Unt rates", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(specTW)) {
            List<ENTRANT_SPECIALITY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANT_SPECIALITY.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete entrant specialities", ex);
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
                    CommonUtils.showMessageAndWriteLog("Unable to delete user awards", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                awardsTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user awards", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(socialCategoriesTW)) {
            List<USER_SOCIAL_CATEGORY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete user social categories", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                socialCategoriesTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete user social categories", ex);
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
                CommonUtils.showMessageAndWriteLog("Unable to create a social category", ex);
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

    @Override
    public void handlePhotoWidgetEvent(PhotoWidgetEvent photoWidgetEvent) {
        if (photoWidgetEvent.getEvent() == PhotoWidgetEvent.CHANGED) {
            userPhotoBytes = photoWidgetEvent.getBytes();
            userPhotoFilename = photoWidgetEvent.getFilename();
            userPhotoChanged = true;
        }
    }

}
