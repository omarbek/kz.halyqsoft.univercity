package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.*;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.Flag;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_MEDICAL_CHECKUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_LANGUAGE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.FieldValidator;
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
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;
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

import java.util.*;

/**
 * @author Omarbek
 * @created on 16.05.2018
 */
public abstract class UsersForm extends AbstractFormWidgetView implements PhotoWidgetListener {
    StringLengthValidator lengthValidator = new StringLengthValidator("ИИН должен состоять из 12 цифр",12,13,false);
    private VerticalLayout buttonsVL;
    private VerticalLayout messForm;
    private FromItem educationUDFI;
    protected HorizontalSplitPanel registrationHSP;
    protected HorizontalLayout contentHL;
    protected QueryModel<USER_DOCUMENT_FILE> udfQM;
    protected AbstractFormWidget dataAFW;
    public  String iin;
    private TableWidget eduDocTW, languagesTW, medicalCheckupTW;

    private Button mainDataButton, regAddressButton;
    private Button militaryButton, disabilityButton, repatriateButton;
    private Button eduDocsButton, preemRightButton;
    private Button medButton;
    private Button form, idDocButton;
    protected Button factAddressButton, eduDocButton, finishButton;

    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;

    protected boolean saveData, savePass, saveEduc, saveFactAddress;
    private List<Button> buttons = new ArrayList<>();
    protected Integer beginYear;

    private PreemptiveRight preemptiveRight;
    private EducationDoc educationDoc;
    protected Address address;
    protected Disability disability;
    protected Military military;
    protected Repatriate repatriate;
    protected Passport passport;
    protected Flag flag;

    private TextField iinTF;
    private boolean f=true;

    private static final int ADDRESS_REG = 1;
    private static final int ADDRESS_FACT = 2;
    private static String NEXT_BUTTON_CAPTION = "next";
    protected static String FORM_BUTTON_WIDTH = "260px";

    protected UsersForm(final FormModel dataFM, ENTRANCE_YEAR entranceYear) throws Exception {
        super();

        beginYear = entranceYear.getBeginYear();
        setBackButtonVisible(false);
        registrationHSP = new HorizontalSplitPanel();
        registrationHSP.setSplitPosition(22);
        registrationHSP.setSizeFull();

        buttonsVL = new VerticalLayout();
        buttonsVL.addStyleName("blue");
        buttonsVL.setSpacing(true);
        buttonsVL.setSizeFull();

        contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();
        contentHL.addStyleName("blue");

        dataFM.setButtonsVisible(false);
        dataAFW = new GridFormWidget(dataFM);
        dataAFW.addEntityListener(this);
        dataFM.getFieldModel("email").getValidators().add(new EmailValidator("Введён некорректный E-mail"));
        dataAFW.setCaption(getUILocaleUtil().getCaption("regapplicant.main.data"));
        dataFM.getFieldModel("email").setRequired(true);
        dataFM.getFieldModel("email").setReadOnly(false);
        dataFM.getFieldModel("phoneMobile").setRequired(true);
        dataFM.getFieldModel("code").setInEdit(false);
        dataFM.getFieldModel("card").setInEdit(false);
        dataFM.getFieldModel("login").setInEdit(false);
        dataFM.getFieldModel("login").setInView(false);

        udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
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

        address = new Address(dataAFW, this);
        address.create("address.registration", ADDRESS_REG);
        address.create("address.residential", ADDRESS_FACT);

        educationDoc = new EducationDoc(dataAFW, this, eduDocTW, educationUDFI);
        educationDoc.create(udfQM);

        createFormButtons(dataFM);

        form.click();
    }

    private void createFormButtons(FormModel dataFM) {
        form = createFormButton("regapplicant.main.data", true);
        form.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getContent().removeAllComponents();
                setInactive();
                mainDataButton.setIcon(new ThemeResource("img/button/active.png"));
                flag = Flag.MAIN_DATA;
                saveData = false;
                savePass = false;

                initSpec(dataFM);

                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(dataAFW);

                VerticalLayout photoAndButtonVL = getPhotoVL();
                contentHL.addComponent(photoAndButtonVL);

                registrationHSP.addComponent(contentHL);
                getContent().addComponent(registrationHSP);


            }


        });

        mainDataButton = createFormButton("regapplicant.main.data", true);
        mainDataButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setActive(event);
                flag = Flag.MAIN_DATA;
                registrationHSP.removeComponent(contentHL);
                contentHL = new HorizontalLayout();
                contentHL.addComponent(dataAFW);

                VerticalLayout photoAndButtonVL = getPhotoVL();
                contentHL.addComponent(photoAndButtonVL);
                registrationHSP.addComponent(contentHL);
            }
        });


        factAddressButton = createFormButton("address.residential", true);

        factAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if((flagSave(flag, dataFM) && Flag.MAIN_DATA.equals(flag))
                        || !Flag.MAIN_DATA.equals(flag)) {
                        addToLayout(Flag.FACT_ADDRESS, address.getAddressFactGFW(), regAddressButton, event);
                       f=true;
                }
            }

        });

        regAddressButton = createFormButton("address.registration", false);
        regAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.FACT_ADDRESS.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.FACT_ADDRESS.equals(flag))) {
                        addToLayout(Flag.REG_ADDRESS, address.getAddressRegGFW(), idDocButton, event);
                    f=true;
                }
            }
        });

 idDocButton = createFormButton("identity.document", true);
        idDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else  if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.REG_ADDRESS.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.REG_ADDRESS.equals(flag))) {

                        addToLayout(Flag.ID_DOC, passport.getMainGFW(), militaryButton, event);

                        FormModel mainFM = passport.getMainGFW().getWidgetModel();
                        mainFM.getFieldModel("iin").setRequired(true);
                        if(iin!=null) {
                            mainFM.getFieldModel("iin").getField().setValue(iin);
                            mainFM.getFieldModel("iin").getField().setReadOnly(true);
                            f=true;
                    }
                }

            }
        });

        militaryButton = createFormButton("military.document", false);
        militaryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.ID_DOC.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.ID_DOC.equals(flag))) {
                        addToLayout(Flag.MILITARY, military.getMainGFW(), disabilityButton, event);
                        f=true;

                }
            }
        });

        disabilityButton = createFormButton("disability.document", false);
        disabilityButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else   if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.MILITARY.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.MILITARY.equals(flag))) {

                        addToLayout(Flag.DISABILITY, disability.getMainGFW(), repatriateButton, event);
                        f=true;

                }
            }
        });

        repatriateButton = createFormButton("repatriate.document", false);
        repatriateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.DISABILITY.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.DISABILITY.equals(flag))) {

                        addToLayout(Flag.REPATRIATE, repatriate.getMainGFW(), eduDocButton, event);
                        f=true;
                    }

            }
        });

        eduDocButton = createFormButton("education.document", true);
        eduDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.REPATRIATE.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.REPATRIATE.equals(flag))) {

                        addToLayout(Flag.EDU_DOC, educationDoc.getMainGFW(), eduDocsButton, event);
                        f=true;
                    }

            }
        });

        eduDocsButton = createFormButton("education.documents", false);
        eduDocsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else  if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.EDU_DOC.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.EDU_DOC.equals(flag))) {

                        flag = Flag.EDU_DOCS;
                        eduDocTW = new TableWidget(EDUCATION_DOC.class);
                        eduDocTW.addEntityListener(UsersForm.this);
                        eduDocTW.setWidth("667px");
                        eduDocTW.addStyleName("toTop");
                        FormModel docFM = new FormModel(EDUCATION_DOC.class, true);
                        docFM.getFieldModel("schoolCountry").setRequired(true);
                        docFM.getFieldModel("language").setRequired(true);
                        docFM.getFieldModel("schoolRegion").setRequired(true);

                        DBTableModel educationTM = (DBTableModel) eduDocTW.getWidgetModel();
                        educationTM.getColumnModel("entryYear").setAlignment(Table.Align.CENTER);
                        educationTM.getColumnModel("endYear").setAlignment(Table.Align.CENTER);
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

                        addToLayout(eduDocTW, preemRightButton, event);
                        f=true;
                }
            }
        });

        preemRightButton = createFormButton("preemptive.right", false);
        preemRightButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else if ((flagSave(flag, dataFM) && Flag.MAIN_DATA.equals(flag))
                        || !Flag.MAIN_DATA.equals(flag)) {

                        flag = Flag.PREEM_RIGHT;

                        languagesTW = getTableWidget(V_USER_LANGUAGE.class, "user", null);

                        VerticalLayout preemLang = getPreemptiveRightVL(preemptiveRight.getMainGFW(), languagesTW);

                        setActive(event);
                        contentHL.removeAllComponents();
                        contentHL.addComponent(preemLang);
                        Button nextButton = createNextButton(medButton, NEXT_BUTTON_CAPTION);
                        contentHL.addComponent(nextButton);
                        contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
                        f=true;
                }
            }
        });

        medButton = createFormButton("medical.checkup", false);
        medButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if( iinTF.getValue().equals("") && iin==null) {
                    Message.showError(getUILocaleUtil().getMessage("error.iin"));
                    f=false;
                }else if(iinTF.getValue().toString().length()<12){
                    Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    f=false;
                }else  if ((flagSave(flag, dataFM) && (Flag.MAIN_DATA.equals(flag) || Flag.PREEM_RIGHT.equals(flag)))
                        || !(Flag.MAIN_DATA.equals(flag) || Flag.PREEM_RIGHT.equals(flag))) {

                        flag = Flag.MED;

                        medicalCheckupTW = new TableWidget(V_MEDICAL_CHECKUP.class);
                        medicalCheckupTW.addEntityListener(UsersForm.this);
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

                        addToLayout(medicalCheckupTW, getAfterMedButton(), event);
                        f=true;

                }
            }
        });

        initOwnButtons(dataFM);

        finishButton = new Button();
        finishButton.setHtmlContentAllowed(true);
        finishButton.setCaption("<b>" + getUILocaleUtil().getCaption("exit") + "</b>");
        finishButton.setIcon(new ThemeResource("img/button/ok.png"));
        finishButton.addStyleName("left");
        finishButton.setWidth(FORM_BUTTON_WIDTH);

        setOpeners();

        finishButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                Map<String, Boolean> conditionsMap = getConditionsMap();
                for (Map.Entry<String, Boolean> entry : conditionsMap.entrySet()) {
                    if (entry.getValue()) {
                        Message.showInfo(entry.getKey());
                        return;
                    }
                }
                setInactive();
                mainDataButton.setEnabled(false);
                factAddressButton.setEnabled(false);
                regAddressButton.setEnabled(false);
                idDocButton.setEnabled(false);
                militaryButton.setEnabled(false);
                disabilityButton.setEnabled(false);
                repatriateButton.setEnabled(false);
                eduDocButton.setEnabled(false);
                eduDocsButton.setEnabled(false);
                preemRightButton.setEnabled(false);
                medButton.setEnabled(false);
                finishButton.setEnabled(false);

                Label mess = new Label("<br><br><br><center><strong>Вы успешно прошли регистрацию!!!</strong><br>");
                mess.setContentMode(ContentMode.HTML);
                messForm = getMessForm(dataFM, mess);

                registrationHSP.removeAllComponents();
                contentHL.removeAllComponents();
                contentHL.addComponent(messForm);
                contentHL.setComponentAlignment(messForm, Alignment.BOTTOM_CENTER);
                registrationHSP.addComponent(buttonsVL);
                registrationHSP.addComponent(contentHL);
            }

        });

        buttonsVL.addComponent(mainDataButton);
        buttonsVL.addComponent(factAddressButton);
        buttonsVL.addComponent(regAddressButton);
        buttonsVL.addComponent(idDocButton);
        buttonsVL.addComponent(militaryButton);
        buttonsVL.addComponent(disabilityButton);
        buttonsVL.addComponent(repatriateButton);
        buttonsVL.addComponent(eduDocButton);
        buttonsVL.addComponent(eduDocsButton);
        buttonsVL.addComponent(preemRightButton);
        buttonsVL.addComponent(medButton);

        List<Button> childButtons = getButtons();
        for (Button button : childButtons) {
            buttonsVL.addComponent(button);
        }

        buttonsVL.addComponent(finishButton);
        buttonsVL.setSpacing(false);
        registrationHSP.addComponent(buttonsVL);

        buttons.add(mainDataButton);
        buttons.add(factAddressButton);
        buttons.add(regAddressButton);
        buttons.add(idDocButton);
        buttons.add(militaryButton);
        buttons.add(disabilityButton);
        buttons.add(repatriateButton);
        buttons.add(eduDocButton);
        buttons.add(eduDocsButton);
        buttons.add(preemRightButton);
        buttons.add(medButton);
        buttons.addAll(childButtons);
    }

    protected abstract VerticalLayout getPreemptiveRightVL(GridFormWidget mainGFW, TableWidget languagesTW);

    protected abstract void setOpeners();

    protected void setActive(Button.ClickEvent event) {
        setInactive();
        event.getButton().setIcon(new ThemeResource("img/button/active.png"));
    }

    private void setInactive() {
        for (Button button : buttons) {
            button.setIcon(new ThemeResource("img/button/inactive.png"));
        }
    }

    protected void addToLayout(TableWidget currentTW, Button currentButton, Button.ClickEvent event) {
        setActive(event);
        contentHL.removeAllComponents();
        contentHL.addComponent(currentTW);
        Button nextButton = createNextButton(currentButton, NEXT_BUTTON_CAPTION);
        contentHL.addComponent(nextButton);
        contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
    }

    protected abstract List<Button> getButtons();

    protected abstract VerticalLayout getMessForm(FormModel dataFM, Label mess);

    protected abstract Map<String, Boolean> getConditionsMap();

    protected abstract Button getAfterMedButton();

    protected abstract void initOwnButtons(FormModel dataFM);

    protected abstract void initSpec(FormModel dataFM);

    protected void addToLayout(Flag currentFlag, GridFormWidget currentGFW, Button currentButton, Button.ClickEvent event) {
        setActive(event);
        flag = currentFlag;
        contentHL.removeAllComponents();
        contentHL.addComponent(currentGFW);

        Button nextButton = createNextButton(currentButton, NEXT_BUTTON_CAPTION);

        Button cancelButton = createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                currentGFW.cancel();
            }
        });

        HorizontalLayout buttonsHL = createButtonPanel();
        buttonsHL.addComponent(nextButton);
        buttonsHL.addComponent(cancelButton);

        contentHL.addComponent(buttonsHL);
        contentHL.setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);
    }

    private VerticalLayout getPhotoVL() {
        VerticalLayout photoAndButtonsVL = new VerticalLayout();
        photoAndButtonsVL.setSpacing(true);
        iinTF=new TextField();
        iinTF.setWidth(200, Unit.PIXELS);
        iinTF.setMaxLength(12);
        iinTF.setImmediate(true);
        iinTF.setRequired(true);

        iinTF.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

                if(iinTF.getValue()!=null && !iinTF.getValue().trim().equals("")) {

                        QueryModel<USER_DOCUMENT> userDocumentQM = new QueryModel<>(USER_DOCUMENT.class);
                        FromItem udItem = userDocumentQM.addJoin(EJoin.INNER_JOIN, "id", USER_PASSPORT.class, "id");
                        userDocumentQM.addWhere(udItem, "iin", ECriteria.EQUAL, iinTF.getValue());
                        userDocumentQM.addWhere("deleted",ECriteria.EQUAL,false);


                        try {
                            USER_DOCUMENT userDocument = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userDocumentQM);
                            if (userDocument != null) {
                                Message.showInfo("ИИН уже существует");
                            }
                        } catch (Exception e) {
                        }
                        boolean flag = true;
                        for(Validator v: iinTF.getValidators()) {
                            if(v.equals(lengthValidator)){
                                flag = false;
                            }
                        }

                         if(flag){
                            iinTF.addValidator(lengthValidator);
                             if (FieldValidator.isNumber(iinTF.getValue())) {
                                 iin = iinTF.getValue();
                             } else {
                                 iinTF.setValue("");
                                 iin = "";
                             }
                        }


                    }else {
                    iinTF.removeValidator(lengthValidator);
                }

            }
        });

        PhotoWidget userPW = new PhotoWidget(userPhotoBytes);
        userPW.setPhotoHeight(290, Unit.PIXELS);
        userPW.setPhotoWidth(230, Unit.PIXELS);
        userPW.setSaveButtonVisible(false);
        userPW.addListener(UsersForm.this);
        photoAndButtonsVL.addComponent(userPW);
        photoAndButtonsVL.addComponent(iinTF);
        iinTF.setCaption(getUILocaleUtil().getCaption("iin"));

        photoAndButtonsVL.setComponentAlignment(userPW, Alignment.TOP_CENTER);
        photoAndButtonsVL.setComponentAlignment(iinTF, Alignment.TOP_CENTER);


        Button nextButton = createNextButton(factAddressButton, NEXT_BUTTON_CAPTION);


        Button cancelButton = createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                dataAFW.cancel();
                iinTF.clear();
            }
        });

        HorizontalLayout buttonsHL = createButtonPanel();

        buttonsHL.addComponent(nextButton);
        buttonsHL.addComponent(cancelButton);

        photoAndButtonsVL.addComponent(buttonsHL);
        photoAndButtonsVL.setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        return photoAndButtonsVL;
    }

    protected boolean flagSave(Flag flag, FormModel dataFM) {
        Boolean saved;
        switch (flag) {
            case MAIN_DATA:
                return saveIfModify(dataFM);
            case FACT_ADDRESS:
                saved = address.save(ADDRESS_FACT);
                if (saved == null) {
                    return true;
                }
                if (saved || saveFactAddress) {
                    saveFactAddress = true;
                    return true;
                }
                break;
            case REG_ADDRESS:
                saved = address.save(ADDRESS_REG);
                if (saved == null || saved) {
                    return true;
                }
                break;
            case ID_DOC:
                saved = passport.save();
                if (saved == null) {
                    return true;
                }
                if (saved || savePass) {
                    savePass = true;
                    return true;
                }
                break;
            case MILITARY:
                saved = military.save();
                if (saved == null || saved) {
                    return true;
                }
                break;
            case DISABILITY:
                saved = disability.save();
                if (saved == null || saved) {
                    return true;
                }
                break;
            case REPATRIATE:
                saved = repatriate.save();
                if (saved == null || saved) {
                    return true;
                }
                break;
            case EDU_DOC:
                saved = educationDoc.save();
                if (saved == null) {
                    return true;
                }
                if (saved || saveEduc) {
                    saveEduc = true;
                    return true;
                }
                break;
            case PREEM_RIGHT:
                saved = preemptiveRight.save();
                if (saved == null || saved) {
                    return true;
                }
                break;
            default:
                return checkFlag(flag);
        }
        return false;
    }

    protected abstract boolean checkFlag(Flag flag);

    private boolean saveIfModify(FormModel dataFM) {
        if (dataFM.isModified()) {
            if (dataAFW.save()) {
                saveData = true;
                return true;
            }
        } else if (saveData) {
            return true;
        } else {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
        }
        return false;
    }

    private Button createNextButton(Button clickButton, String caption) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        if (NEXT_BUTTON_CAPTION.equals(caption)) {
            temp.setIcon(new ThemeResource("img/button/arrow_right.png"));
        } else {
            temp.setIcon(new ThemeResource("img/button/ok.png"));
        }
        temp.setWidth(120.0F, Unit.PIXELS);
        temp.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                        if( iinTF.getValue().equals("") && iin==null){
                            Message.showError(getUILocaleUtil().getMessage("error.iin"));
                            return;
                        }else if(iinTF.getValue().toString().length()<12){
                             Message.showError(getUILocaleUtil().getMessage("incorrect.iin"));
                    return;
                }

                clickButton.click();
            }
        });
        return temp;
    }

    protected Button createFormButton(String caption, boolean main) {
        Button temp = new Button();
        if (main) {
            temp.setHtmlContentAllowed(true);
            temp.setCaption("<b>" + getUILocaleUtil().getCaption(caption) + "</b>");
        } else {
            temp.setCaption(getUILocaleUtil().getCaption(caption));

        }
        temp.setWidth(FORM_BUTTON_WIDTH);
        temp.addStyleName("left");
        return temp;
    }


    private boolean preSaveData(Entity e, boolean isNew) {
        USERS user = (USERS) e;

        if (isNew) {
            user.setCreated(new Date());
            user.setCreatedBy(CommonUtils.getCurrentUserLogin());
            try {
                user.setPasswd("12345678");
                user.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS"));

                String year = beginYear.toString().substring(2, 4);
                String code = CommonUtils.getCode(year);
                String login = getLogin(user.getFirstNameEN().toLowerCase().substring(0, 1) + "_" +
                        user.getLastNameEN().toLowerCase());
                user.setLogin(login == null ? code : login);
                user.setCode(code);
                if (e instanceof STUDENT) {
                    ((STUDENT) user).setCategory(SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, STUDENT_CATEGORY.ENROLLEE_ID));
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(user);
                dataAFW.getWidgetModel().loadEntity(user.getId());

                if (userPhotoChanged) {
                    USER_PHOTO userPhoto = new USER_PHOTO();
                    userPhoto.setUser(user);
                    userPhoto.setFileName(userPhotoFilename);
                    userPhoto.setPhoto(userPhotoBytes);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userPhoto);
                }

                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to save new applicant", ex);
            }
        } else {
            user.setUpdated(new Date());
            user.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(user);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an entrant", ex);
            }
        }
        return false;
    }

    protected abstract String getLogin(String s) throws Exception;

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
                USERS user = (USERS) fm.getEntity();
                md.setUser(user);
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
                USER_LANGUAGE ul = new USER_LANGUAGE();
                USERS user = (USERS) dataAFW.getWidgetModel().getEntity();
                ul.setUser(user);
                ul.setLanguage(vul.getLanguage());
                ul.setLanguageLevel(vul.getLanguageLevel());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ul);

                QueryModel languageQM = ((DBTableModel) languagesTW.getWidgetModel()).getQueryModel();
                languageQM.addWhere("user", ECriteria.EQUAL, user.getId());

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
                USERS s = (USERS) fm.getEntity();
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


    protected TableWidget getTableWidget(Class<? extends Entity> entityClass, String fieldName, Boolean isMed) {
        TableWidget currentTW = new TableWidget(entityClass);
        currentTW.addEntityListener(this);
        currentTW.setWidth("667px");
        DBTableModel currentTM = (DBTableModel) currentTW.getWidgetModel();
        QueryModel currentQM = currentTM.getQueryModel();

        ID userId = ID.valueOf(-1);
        if (!dataAFW.getWidgetModel().isCreateNew()) {
            try {
                userId = dataAFW.getWidgetModel().getEntity().getId();
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
        currentQM.addWhere(fieldName, ECriteria.EQUAL, userId);
        if (isMed != null) {
            if (isMed) {
                currentTM.getColumnModel("allowDorm").setInTable(false);
                currentTM.getColumnModel("allowStudy").setInTable(false);
                FormModel medicalCheckupFM = currentTM.getFormModel();
                medicalCheckupFM.getFieldModel("allowDorm").setInEdit(false);
                medicalCheckupFM.getFieldModel("allowStudy").setInEdit(false);
                medicalCheckupFM.getFieldModel("allowDorm").setInView(false);
                medicalCheckupFM.getFieldModel("allowStudy").setInView(false);
            }
            currentQM.addWhere("deleted", Boolean.FALSE);
        }
        return currentTW;
    }

    private boolean addFiles(Object source, Entity e) {
        QueryModel<USER_DOCUMENT_FILE> udfQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        udfQM.addSelect("id");
        udfQM.addSelect("fileName");
        udfQM.addWhere("deleted", Boolean.FALSE);

        if (source.equals(eduDocTW)) {
            FormModel educationFM = ((DBTableModel) eduDocTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");

            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));

            return refreshFiles(e, udfQM, educationFM);
        } else if (source.equals(medicalCheckupTW)) {
            FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();

            return refreshFiles(e, udfQM, medicalCheckupFM);
        } else return source.equals(languagesTW);
    }

    protected boolean refreshFiles(Entity e, QueryModel<USER_DOCUMENT_FILE> udfQM, FormModel educationFM) {
        FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
        educationFLFM.permitMimeType(FileListFieldModel.JPEG);

        educationFLFM.getFileList().clear();
        educationFLFM.getDeleteList().clear();

        udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());

        CommonUtils.addFiles(udfQM, educationFLFM);

        return true;
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(eduDocTW)) {
            if (dataAFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            FormModel educationFM = ((DBTableModel) eduDocTW.getWidgetModel()).getFormModel();
            FKFieldModel schoolCountryFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolCountry");
            QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
            schoolCountryQM.addWhereNull("parent");
            schoolCountryQM.addOrder("countryName");

            FKFieldModel schoolRegionFieldModel = (FKFieldModel) educationFM.getFieldModel("schoolRegion");
            QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
            schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

            schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));


            educationFM.getFieldModel("language").setRequired(true);
            educationFM.getFieldModel("schoolRegion").setRequired(true);
            educationFM.getFieldModel("entryYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));
            educationFM.getFieldModel("endYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));

            FileListFieldModel educationFLFM = (FileListFieldModel) educationFM.getFieldModel("fileList");
            educationFLFM.permitMimeType(FileListFieldModel.JPEG);

            educationFLFM.getFileList().clear();
            educationFLFM.getDeleteList().clear();

            return true;
        } else if (source.equals(languagesTW)) {
            return canSave();
        } else if (source.equals(medicalCheckupTW)) {
            return preCreateTW(medicalCheckupTW);
        }

        return super.preCreate(source, buttonId);
    }

    protected boolean preCreateTW(TableWidget currentTW) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }

        FormModel medicalCheckupFM = ((DBTableModel) currentTW.getWidgetModel()).getFormModel();

        FileListFieldModel medicalCheckupFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
        medicalCheckupFLFM.permitMimeType(FileListFieldModel.JPEG);

        medicalCheckupFLFM.getFileList().clear();
        medicalCheckupFLFM.getDeleteList().clear();

        return true;
    }

    protected boolean canSave() {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        return true;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return addFiles(source, e) || super.onEdit(source, e, buttonId);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return addFiles(source, e) || super.onPreview(source, e, buttonId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        if (source.equals(dataAFW)) {
            return preSaveData(e, isNew);
        } else if (source.equals(eduDocTW)) {
            return educationDoc.preSave(e, isNew);
        } else if (source.equals(educationDoc.getMainGFW())) {
            return preSaveDoc(e, isNew);
        } else if (source.equals(passport.getMainGFW())) {
            return passport.preSave(e, isNew);
        } else if (source.equals(languagesTW)) {
            return preSaveLanguage(e, isNew);
        } else if (source.equals(medicalCheckupTW)) {
            return preSaveMedicalCheckup(e, isNew);
        } else if (source.equals(military.getMainGFW())) {
            return military.preSave(e, isNew);
        } else if (source.equals(disability.getMainGFW())) {
            return disability.preSave(e, isNew);
        } else if (source.equals(repatriate.getMainGFW())) {
            return repatriate.preSave(e, isNew);
        } else if (source.equals(preemptiveRight.getMainGFW())) {
            return preemptiveRight.preSave(e, isNew);
        } else if (source.equals(address.getAddressRegGFW())) {
            return address.preSave(e, isNew, ADDRESS_REG);
        } else if (source.equals(address.getAddressFactGFW())) {
            return address.preSave(e, isNew, ADDRESS_FACT);
        }
        return super.preSave(source, e, isNew, buttonId);
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(eduDocTW)) {
            for (Entity e : entities) {
                ((EDUCATION_DOC) e).setDeleted(true);
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(entities);
                eduDocTW.refresh();
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
    protected AbstractCommonView getParentView() {
        return null;
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

    public VerticalLayout getButtonsVL() {
        return buttonsVL;
    }

    public Button getPreemRightButton() {
        return preemRightButton;
    }

    public Button getEduDocsButton() {
        return eduDocsButton;
    }

    public TableWidget getEduDocTW() {
        return eduDocTW;
    }

    public Button getMedButton() {
        return medButton;
    }
}
