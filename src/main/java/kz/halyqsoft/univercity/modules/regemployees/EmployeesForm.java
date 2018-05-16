package kz.halyqsoft.univercity.modules.regemployees;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
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
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.DBSelectModel;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class EmployeesForm extends AbstractFormWidgetView implements PhotoWidgetListener {

    private AbstractFormWidget dataAFW;
    private HorizontalSplitPanel registrationHSP;
    private HorizontalLayout contentHL;
    private VerticalLayout buttonsVL;
    private VerticalLayout messForm;
    private FromItem educationUDFI;

    private TableWidget documentsTW, languagesTW, medicalCheckupTW;
    private TableWidget awardsTW, socialCategoriesTW, employeeDegreeTW;
    private TableWidget publicationTW, scientificActivityTW, scientificManagementTW;
    private TableWidget experienceTW, careerTW;

    private Button form;
    private Button mainDataButton, factAddressButton, regAddressButton;
    private Button idDocButton, militaryButton, disabilityButton, repatriateButton;
    private Button eduDocButton, eduDocsButton, preemRightButton, medButton;
    private Button employeeDegreeButton, publicationButton, scientActButton, scientManagemButton;
    private Button experienceButton, careerButton;
    private Button moreButton, finishButton;

    private String userPhotoFilename;
    private byte[] userPhotoBytes;
    private boolean userPhotoChanged;

    private boolean saveData, savePass;
    private Integer beginYear;
    private int sequenceForEmployee = 1;

    private Flag flag;
    private PreemptiveRight preemptiveRight;
    private EducationDoc educationDoc;
    private Address address;
    private Disability disability;
    private Military military;
    private Repatriate repatriate;
    private Passport passport;

    private static final int ADDRESS_REG = 1;
    private static final int ADDRESS_FACT = 2;
    private static final String WIDTH_OF_MENU_BUTTONS = "300px";

    EmployeesForm(final FormModel dataFM, ENTRANCE_YEAR entranceYear) throws Exception {
        super();

        beginYear = entranceYear.getBeginYear();
        setBackButtonVisible(false);
        registrationHSP = new HorizontalSplitPanel();
        registrationHSP.setSplitPosition(23);
        registrationHSP.setSizeFull();

        buttonsVL = new VerticalLayout();
        buttonsVL.setSpacing(true);
        buttonsVL.setSizeFull();

        contentHL = new HorizontalLayout();
        contentHL.setSpacing(true);
        contentHL.setSizeFull();

        dataFM.setButtonsVisible(false);
        dataAFW = new GridFormWidget(dataFM);
        dataAFW.addEntityListener(this);
        dataFM.getFieldModel("email").getValidators().add(new EmailValidator("Введён некорректный E-mail"));
        dataAFW.setCaption(getUILocaleUtil().getCaption("regapplicant.main.data"));
        dataFM.getFieldModel("email").setRequired(true);
        dataFM.getFieldModel("email").setReadOnly(false);
        dataFM.getFieldModel("phoneMobile").setRequired(true);

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

        address = new Address(dataAFW, this);
        address.create("address.registration", ADDRESS_REG);
        address.create("address.residential", ADDRESS_FACT);

        educationDoc = new EducationDoc(dataAFW, this, documentsTW, educationUDFI);
        educationDoc.create(udfQM);

        createFormButtons(dataFM);

        addFormButtons();

        form.click();
    }

    private void createFormButtons(FormModel dataFM) {
        form = createFormButton("regapplicant.main.data");
        form.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getContent().removeAllComponents();
                flag = Flag.MAIN_DATA;
                saveData = false;
                savePass = false;

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
            public void buttonClick(Button.ClickEvent event) {
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
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.FACT_ADDRESS, address.getAddressFactGFW(), regAddressButton);
            }
        });

        regAddressButton = createFormButton("address.registration");
        regAddressButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.REG_ADDRESS, address.getAddressRegGFW(), idDocButton);
            }
        });

        idDocButton = createFormButton("identity.document");
        idDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.ID_DOC, passport.getMainGFW(), militaryButton);
            }
        });

        militaryButton = createFormButton("military.document");
        militaryButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.MILITARY, military.getMainGFW(), disabilityButton);
            }
        });

        disabilityButton = createFormButton("disability.document");
        disabilityButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.DISABILITY, disability.getMainGFW(), repatriateButton);
            }
        });

        repatriateButton = createFormButton("repatriate.document");
        repatriateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.REPATRIATE, repatriate.getMainGFW(), eduDocButton);
            }
        });

        eduDocButton = createFormButton("education.document");
        eduDocButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                addToLayout(Flag.EDU_DOC, educationDoc.getMainGFW(), eduDocsButton);
            }
        });

        eduDocsButton = createFormButton("education.documents");
        eduDocsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.EDU_DOCS;
                documentsTW = new TableWidget(EDUCATION_DOC.class);
                documentsTW.addEntityListener(EmployeesForm.this);
                documentsTW.setWidth("667px");
                documentsTW.addStyleName("toTop");
                FormModel docFM = new FormModel(EDUCATION_DOC.class, true);
                docFM.getFieldModel("schoolCountry").setRequired(true);
                docFM.getFieldModel("language").setRequired(true);
                docFM.getFieldModel("schoolCertificateType").setRequired(true);
                docFM.getFieldModel("schoolRegion").setRequired(true);
                docFM.getFieldModel("schoolAddress").setRequired(true);

                DBTableModel educationTM = (DBTableModel) documentsTW.getWidgetModel();
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

                addToLayout(documentsTW, preemRightButton);
            }
        });

        preemRightButton = createFormButton("preemptive.right");
        preemRightButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.PREEM_RIGHT;

                languagesTW = getTableWidget(V_USER_LANGUAGE.class, "user", null);

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
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.MED;

                medicalCheckupTW = getTableWidget(V_MEDICAL_CHECKUP.class, "user", true);

                addToLayout(medicalCheckupTW, employeeDegreeButton);
            }
        });

        employeeDegreeButton = createFormButton("scientific.degrees");
        employeeDegreeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                employeeDegreeTW = getTableWidget(V_EMPLOYEE_DEGREE.class, "employee", false);
                addToLayout(employeeDegreeTW, publicationButton);
            }
        });

        publicationButton = createFormButton(V_PUBLICATION.class);
        publicationButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                publicationTW = getTableWidget(V_PUBLICATION.class, "employee", null);
                addToLayout(publicationTW, scientActButton);
            }
        });

        scientActButton = createFormButton(V_SCIENTIFIC_ACTIVITY.class);
        scientActButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                scientificActivityTW = getTableWidget(V_SCIENTIFIC_ACTIVITY.class, "employee", null);
                addToLayout(scientificActivityTW, scientManagemButton);
            }
        });

        scientManagemButton = createFormButton(V_SCIENTIFIC_MANAGEMENT.class);
        scientManagemButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                scientificManagementTW = getTableWidget(V_SCIENTIFIC_MANAGEMENT.class, "employee", null);
                addToLayout(scientificManagementTW, experienceButton);
            }
        });

        experienceButton = createFormButton("experience");
        experienceButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                experienceTW = getTableWidget(PREVIOUS_EXPERIENCE.class, "employee", null);
                addToLayout(experienceTW, careerButton);
            }
        });

        careerButton = createFormButton(V_EMPLOYEE_DEPT.class);
        careerButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                careerTW = getTableWidget(V_EMPLOYEE_DEPT.class, "employee", null);

                FormModel specFM = ((DBSelectModel) careerTW.getWidgetModel()).getFormModel();
                QueryModel specQM = ((FKFieldModel) specFM.getFieldModel("department")).getQueryModel();
                specQM.addWhere("deleted", Boolean.FALSE);
                specQM.addWhereNotNull("parent");

                EMPLOYEE emp = null;
                try {
                    emp = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
                DBTableModel careerTM = (DBTableModel) careerTW.getWidgetModel();
                if (emp != null && emp.getStatus() != null && emp.getStatus().getId().equals(ID.valueOf(5))) {
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

                addToLayout(careerTW, moreButton);
            }
        });

        moreButton = createFormButton("inform.more");
        moreButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                flag = Flag.DEFAULT_FLAG;

                String fieldName = "user";
                awardsTW = getTableWidget(V_USER_AWARD.class, fieldName, null);
                socialCategoriesTW = getTableWidget(V_USER_SOCIAL_CATEGORY.class, fieldName, null);

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
        finishButton.setWidth(WIDTH_OF_MENU_BUTTONS);
        finishButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                flagSave(flag, dataFM);
                if (!saveData) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                } else if (!savePass) {
                    Message.showInfo(getUILocaleUtil().getMessage("info.save.passport"));
                } else {
                    mainDataButton.setEnabled(false);
                    idDocButton.setEnabled(false);
                    eduDocButton.setEnabled(false);
                    medButton.setEnabled(false);
                    employeeDegreeButton.setEnabled(false);
                    publicationButton.setEnabled(false);
                    experienceButton.setEnabled(false);
                    careerButton.setEnabled(false);
                    scientActButton.setEnabled(false);
                    scientManagemButton.setEnabled(false);
                    moreButton.setEnabled(false);
                    militaryButton.setEnabled(false);
                    disabilityButton.setEnabled(false);
                    repatriateButton.setEnabled(false);
                    eduDocsButton.setEnabled(false);
                    preemRightButton.setEnabled(false);
                    factAddressButton.setEnabled(false);
                    regAddressButton.setEnabled(false);
                    finishButton.setEnabled(false);

                    Label mess = new Label("<br><br><br><center><strong>Вы успешно прошли регистрацию!!!</strong><br>");
                    mess.setContentMode(ContentMode.HTML);
                    messForm = new VerticalLayout();
                    messForm.addComponent(mess);

                    Button againButton = new Button(getUILocaleUtil().getCaption("back.to.new.employee.registration"));
                    againButton.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            RegisterEmployeesView.regButton.click();
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

    private TableWidget getTableWidget(Class<? extends Entity> entityClass, String fieldName, Boolean isMed) {
        TableWidget currentTW = new TableWidget(entityClass);
        currentTW.addEntityListener(EmployeesForm.this);
        currentTW.setWidth("667px");
        DBTableModel currentTM = (DBTableModel) currentTW.getWidgetModel();
        QueryModel currentQM = currentTM.getQueryModel();

        ID employeeId = ID.valueOf(-1);
        if (!dataAFW.getWidgetModel().isCreateNew()) {
            try {
                employeeId = dataAFW.getWidgetModel().getEntity().getId();
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
        currentQM.addWhere(fieldName, ECriteria.EQUAL, employeeId);
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

    private void addToLayout(TableWidget currentTW, Button currentButton) {
        contentHL.removeAllComponents();
        contentHL.addComponent(currentTW);
        Button nextButton = createNextButton(currentButton, "next");
        contentHL.addComponent(nextButton);
        contentHL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
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
        userPW.addListener(EmployeesForm.this);
        photoAndButtonVL.addComponent(userPW);
        photoAndButtonVL.setComponentAlignment(userPW, Alignment.TOP_CENTER);

        Button nextButton = createNextButton(factAddressButton, "next");
        photoAndButtonVL.addComponent(nextButton);
        photoAndButtonVL.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);
        return photoAndButtonVL;
    }

    private void flagSave(Flag flag, FormModel dataFM) {

        switch (flag) {
            case MAIN_DATA:
                if (dataFM.isModified() && dataAFW.save())
                    saveData = true;
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
                break;
            case PREEM_RIGHT:
                preemptiveRight.save();
                break;
            case FACT_ADDRESS:
                address.save(ADDRESS_FACT);
                break;
            case REG_ADDRESS:
                address.save(ADDRESS_REG);
                break;
            default:
                break;
        }
    }

    private void addFormButtons() {
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
        buttonsVL.addComponent(employeeDegreeButton);
        buttonsVL.addComponent(publicationButton);
        buttonsVL.addComponent(scientActButton);
        buttonsVL.addComponent(scientManagemButton);
        buttonsVL.addComponent(experienceButton);
        buttonsVL.addComponent(careerButton);
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
            public void buttonClick(Button.ClickEvent event) {
                clickButton.click();
            }
        });
        return temp;
    }

    private Button createFormButton(String caption) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getCaption(caption));
        temp.setWidth(WIDTH_OF_MENU_BUTTONS);
        return temp;
    }

    private Button createFormButton(Class<? extends Entity> entityClass) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getEntityLabel(entityClass));
        temp.setWidth(WIDTH_OF_MENU_BUTTONS);
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

    private boolean preSaveData(Entity e, boolean isNew) {
        EMPLOYEE s = (EMPLOYEE) e;

        if (isNew) {
            s.setCreated(new Date());
            try {
                s.setPasswd("12345678");
                s.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS"));

                String year = beginYear.toString().substring(2, 4);
                String code = getCode(year);
                s.setCode(code);
                String login = getLogin(s.getFirstNameEN().toLowerCase().substring(0, 1) + "_" +
                        s.getLastNameEN().toLowerCase());
                s.setLogin(login);
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
                CommonUtils.showMessageAndWriteLog("Unable to save new employee", ex);
            }
        } else {
            s.setUpdated(new Date());
            s.setUpdatedBy(CommonUtils.getCurrentUserLogin());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(s);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a entrant", ex);
            }
        }
        return false;
    }

    private String getLogin(String login) throws Exception {

        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
        usersQM.addWhere("login", ECriteria.EQUAL, login);
        List<USERS> users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM);
        if (!users.isEmpty()) {
            login = getLogin(login + sequenceForEmployee++);
        }
        return login;
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
                md.setUser((EMPLOYEE) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a Education document", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a Education document", ex);
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
                    CommonUtils.showMessageAndWriteLog("Unable to save Education document copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete Education document copy", ex);
            }
        }

        return false;
    }

    private boolean preSaveLanguage(Entity e, boolean isNew) {
        V_USER_LANGUAGE vul = (V_USER_LANGUAGE) e;
        if (isNew) {
            try {
                EMPLOYEE s = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
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
        MEDICAL_CHECKUP medicalCheckup = null;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            medicalCheckup = new MEDICAL_CHECKUP();
            try {
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
                medicalCheckup.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                medicalCheckup.setUser(s);
                medicalCheckup.setDocumentNo(vmc.getDocumentNo());
                medicalCheckup.setIssueDate(vmc.getIssueDate());
                medicalCheckup.setExpireDate(vmc.getExpireDate());
                medicalCheckup.setCheckupType(vmc.getCheckupType());
                medicalCheckup.setIssuerName(vmc.getIssuerName());
                medicalCheckup.setAllowWork(vmc.isAllowWork());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(medicalCheckup);

                QueryModel medicalCheckupQM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getQueryModel();
                medicalCheckupQM.addWhere("user", ECriteria.EQUAL, s.getId());

                medicalCheckupTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a medical checkup", ex);
            }
        } else {
            try {
                medicalCheckup = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MEDICAL_CHECKUP.class, vmc.getId());
                medicalCheckup.setDocumentNo(vmc.getDocumentNo());
                medicalCheckup.setIssueDate(vmc.getIssueDate());
                medicalCheckup.setExpireDate(vmc.getExpireDate());
                medicalCheckup.setCheckupType(vmc.getCheckupType());
                medicalCheckup.setIssuerName(vmc.getIssuerName());
                medicalCheckup.setAllowWork(vmc.isAllowWork());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(medicalCheckup);
                medicalCheckupTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a medical checkup", ex);
            }
        }

        modifyCopy(medicalCheckup, medicalCheckupTW);
        return false;
    }

    private void modifyCopy(USER_DOCUMENT userDocument, TableWidget currentTW) {
        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) currentTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(userDocument);
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
    }

    private boolean preSaveEmployeeDegree(Entity e, boolean isNew) {
        V_EMPLOYEE_DEGREE vEmployeeDegree = (V_EMPLOYEE_DEGREE) e;
        EMPLOYEE_DEGREE employeeDegree = null;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            employeeDegree = new EMPLOYEE_DEGREE();
            try {
                EMPLOYEE fmEntity = (EMPLOYEE) fm.getEntity();
                employeeDegree.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                employeeDegree.setUser(fmEntity);
                employeeDegree.setDocumentNo(vEmployeeDegree.getDocumentNo());
                employeeDegree.setIssueDate(vEmployeeDegree.getIssueDate());
                employeeDegree.setExpireDate(vEmployeeDegree.getExpireDate());
                employeeDegree.setDegree(vEmployeeDegree.getDegree());
                employeeDegree.setSchoolName(vEmployeeDegree.getSchoolName());
                employeeDegree.setDissertationTopic(vEmployeeDegree.getDissertationTopic());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(employeeDegree);

                QueryModel employeeDegreeQM = ((DBTableModel) employeeDegreeTW.getWidgetModel()).getQueryModel();
                employeeDegreeQM.addWhere("employee", ECriteria.EQUAL, fmEntity.getId());

                employeeDegreeTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create employee degree", ex);
            }
        } else {
            try {
                employeeDegree = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEGREE.class, vEmployeeDegree.getId());
                employeeDegree.setDocumentNo(vEmployeeDegree.getDocumentNo());
                employeeDegree.setIssueDate(vEmployeeDegree.getIssueDate());
                employeeDegree.setExpireDate(vEmployeeDegree.getExpireDate());
                employeeDegree.setDegree(vEmployeeDegree.getDegree());
                employeeDegree.setDissertationTopic(vEmployeeDegree.getDissertationTopic());
                employeeDegree.setSchoolName(vEmployeeDegree.getSchoolName());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(employeeDegree);
                employeeDegreeTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge employeeDegree", ex);
            }
        }

        modifyCopy(employeeDegree, employeeDegreeTW);
        return false;
    }

    private boolean preSaveScientificManagement(Entity e, boolean isNew) {
        V_SCIENTIFIC_MANAGEMENT vScientificManagement = (V_SCIENTIFIC_MANAGEMENT) e;
        SCIENTIFIC_MANAGEMENT scientificManagement = null;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            scientificManagement = new SCIENTIFIC_MANAGEMENT();
            try {
                EMPLOYEE fmEntity = (EMPLOYEE) fm.getEntity();
                scientificManagement.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                scientificManagement.setEmployee(fmEntity);
                scientificManagement.setTopic(vScientificManagement.getTopic());
                scientificManagement.setScientificManagementType(vScientificManagement.getScientificManagementType());
                scientificManagement.setAchievement(vScientificManagement.getAchievement());
                scientificManagement.setProjectName(vScientificManagement.getProjectName());
                scientificManagement.setResult(vScientificManagement.getResult());
                scientificManagement.setStudentsCount(vScientificManagement.getStudentsCount());
                scientificManagement.setStudentsFIO(vScientificManagement.getStudentsFIO());

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(scientificManagement);

                QueryModel employeeDegreeQM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getQueryModel();
                employeeDegreeQM.addWhere("employee", ECriteria.EQUAL, fmEntity.getId());

                scientificManagementTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create scientificManagement", ex);
            }
        } else {
            try {
                scientificManagement = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT.class, vScientificManagement.getId());
                scientificManagement.setTopic(vScientificManagement.getTopic());
                scientificManagement.setScientificManagementType(vScientificManagement.getScientificManagementType());
                scientificManagement.setAchievement(vScientificManagement.getAchievement());
                scientificManagement.setProjectName(vScientificManagement.getProjectName());
                scientificManagement.setResult(vScientificManagement.getResult());
                scientificManagement.setStudentsCount(vScientificManagement.getStudentsCount());
                scientificManagement.setStudentsFIO(vScientificManagement.getStudentsFIO());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(scientificManagement);
                scientificManagementTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge scientificManagement", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                SCIENTIFIC_MANAGEMENT_FILE scientificManagementFile = new SCIENTIFIC_MANAGEMENT_FILE();
                scientificManagementFile.setScientificManagement(scientificManagement);
                scientificManagementFile.setFileName(fe.getFileName());
                scientificManagementFile.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scientificManagementFile);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save scientific management copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                SCIENTIFIC_MANAGEMENT_FILE scientificManagementFile = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT_FILE.class, fe.getId());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(scientificManagementFile);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete scientific management copy", ex);
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
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
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

    private boolean preSavePublication(Entity e, boolean isNew) {
        V_PUBLICATION vPublication = (V_PUBLICATION) e;
        PUBLICATION publication;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            publication = new PUBLICATION();
            try {
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
                publication.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                publication.setEmployee(s);
                publication.setTopic(vPublication.getTopic());
                publication.setPublicationType(vPublication.getPublicationType());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(publication);

                QueryModel publicationQM = ((DBTableModel) publicationTW.getWidgetModel()).getQueryModel();
                publicationQM.addWhere("employee", ECriteria.EQUAL, s.getId());

                publicationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create an publication", ex);
            }
        } else {
            try {
                publication = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PUBLICATION.class, vPublication.getId());
                publication.setTopic(vPublication.getTopic());
                publication.setPublicationType(vPublication.getPublicationType());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(publication);
                publicationTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an award", ex);
            }
        }
        return false;
    }

    private boolean preSaveExperience(Entity e, boolean isNew) {
        PREVIOUS_EXPERIENCE previousExperience = (PREVIOUS_EXPERIENCE) e;
        PREVIOUS_EXPERIENCE newExperience;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            newExperience = new PREVIOUS_EXPERIENCE();
            try {
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
                newExperience.setEmployee(s);
                newExperience.setOrganizationName(previousExperience.getOrganizationName());
                newExperience.setPostName(previousExperience.getPostName());
                newExperience.setHireDate(previousExperience.getHireDate());
                newExperience.setDismissDate(previousExperience.getHireDate());
                newExperience.setDuty(previousExperience.getDuty());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newExperience);

                QueryModel expQM = ((DBTableModel) experienceTW.getWidgetModel()).getQueryModel();
                expQM.addWhere("employee", ECriteria.EQUAL, s.getId());

                experienceTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create an experience", ex);
            }
        } else {
            try {
                newExperience = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PREVIOUS_EXPERIENCE.class, previousExperience.getId());
                newExperience.setOrganizationName(previousExperience.getOrganizationName());
                newExperience.setPostName(previousExperience.getPostName());
                newExperience.setHireDate(previousExperience.getHireDate());
                newExperience.setDismissDate(previousExperience.getHireDate());
                newExperience.setDuty(previousExperience.getDuty());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(newExperience);
                experienceTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an experience", ex);
            }
        }
        return false;
    }

    private boolean preSaveCareer(Entity e, boolean isNew) {
        V_EMPLOYEE_DEPT vEmployeeDept = (V_EMPLOYEE_DEPT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                EMPLOYEE employee = (EMPLOYEE) fm.getEntity();
                EMPLOYEE_DEPT employeeDept = getEmployeeDept(vEmployeeDept, employee);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(employeeDept);

                QueryModel expQM = ((DBTableModel) careerTW.getWidgetModel()).getQueryModel();
                expQM.addWhere("employee", ECriteria.EQUAL, employee.getId());

                careerTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create an employee dept", ex);
            }
        } else {
            try {
                EMPLOYEE_DEPT employeeDept = getEmployeeDept(vEmployeeDept, null);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(employeeDept);
                careerTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an employee dept", ex);
            }
        }
        return false;
    }

    private EMPLOYEE_DEPT getEmployeeDept(V_EMPLOYEE_DEPT vEmployeeDept, EMPLOYEE employee) throws Exception {
        EMPLOYEE_DEPT employeeDept;
        if (employee != null) {
            employeeDept = new EMPLOYEE_DEPT();
            employeeDept.setEmployee(employee);
        } else {
            employeeDept = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEPT.class, vEmployeeDept.getId());
        }
        employeeDept.setHireDate(vEmployeeDept.getHireDate());
        employeeDept.setDismissDate(vEmployeeDept.getHireDate());
        employeeDept.setAdviser(vEmployeeDept.isAdviser());
        employeeDept.setDepartment(vEmployeeDept.getDepartment());
        employeeDept.setEmployeeType(vEmployeeDept.getEmployeeType());
        employeeDept.setHourCount(vEmployeeDept.getHourCount() == null ? 0.0 : vEmployeeDept.getHourCount());
        employeeDept.setLiveLoad(vEmployeeDept.getLiveLoad() == null ? 0 : vEmployeeDept.getLiveLoad());
        employeeDept.setParent(vEmployeeDept.getParent());
        employeeDept.setPost(vEmployeeDept.getPost());
        employeeDept.setWageRate(vEmployeeDept.getWageRate() == null ? 0.0 : vEmployeeDept.getWageRate());
        employeeDept.setRateLoad(employeeDept.getWageRate() == 0.0 ? 0.0 :
                (employeeDept.getLiveLoad() / employeeDept.getWageRate()));
//      employeeDept.setDescr();//TODO
        return employeeDept;
    }

    private boolean preSaveScientificActivity(Entity e, boolean isNew) {
        V_SCIENTIFIC_ACTIVITY vScientificActivity = (V_SCIENTIFIC_ACTIVITY) e;
        SCIENTIFIC_ACTIVITY scientificActivity;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            scientificActivity = new SCIENTIFIC_ACTIVITY();
            try {
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
                scientificActivity.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_EMPLOYEE_SCIENTIFIC"));
                scientificActivity.setEmployee(s);
                scientificActivity.setTopic(vScientificActivity.getTopic());
                scientificActivity.setScientificActivityType(vScientificActivity.getScientificActivityType());
                scientificActivity.setBeginDate(vScientificActivity.getBeginDate());
                scientificActivity.setEndDate(vScientificActivity.getEndDate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(scientificActivity);

                QueryModel publicationQM = ((DBTableModel) scientificActivityTW.getWidgetModel()).getQueryModel();
                publicationQM.addWhere("employee", ECriteria.EQUAL, s.getId());

                scientificActivityTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create an scientific activity", ex);
            }
        } else {
            try {
                scientificActivity = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_ACTIVITY.class, vScientificActivity.getId());
                scientificActivity.setTopic(vScientificActivity.getTopic());
                scientificActivity.setScientificActivityType(vScientificActivity.getScientificActivityType());
                scientificActivity.setBeginDate(vScientificActivity.getBeginDate());
                scientificActivity.setEndDate(vScientificActivity.getEndDate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(scientificActivity);
                scientificActivityTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an scientific activity", ex);
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
                EMPLOYEE s = (EMPLOYEE) fm.getEntity();
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
        if (source.equals(scientificManagementTW)) {
            QueryModel<SCIENTIFIC_MANAGEMENT_FILE> scientificManagementFileQM = new QueryModel<>(SCIENTIFIC_MANAGEMENT_FILE.class);
            scientificManagementFileQM.addSelect("id");
            scientificManagementFileQM.addSelect("fileName");

            FormModel scientificManagementFM = ((DBTableModel) scientificManagementTW.getWidgetModel()).getFormModel();

            FileListFieldModel scientificManagementFLFM = (FileListFieldModel) scientificManagementFM.getFieldModel("fileList");
            scientificManagementFLFM.permitMimeType(FileListFieldModel.JPEG);

            scientificManagementFLFM.getFileList().clear();
            scientificManagementFLFM.getDeleteList().clear();

            scientificManagementFileQM.addWhereAnd("scientificManagement", ECriteria.EQUAL, e.getId());

            try {
                List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(scientificManagementFileQM);
                if (!udfList.isEmpty()) {
                    for (Object o : udfList) {
                        Object[] oo = (Object[]) o;
                        FileBean fe = new FileBean(SCIENTIFIC_MANAGEMENT_FILE.class);
                        fe.setId(ID.valueOf((Long) oo[0]));
                        fe.setFileName((String) oo[1]);
                        fe.setNewFile(false);
                        scientificManagementFLFM.getFileList().add(fe);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load scientific management copies", ex);
            }

            return true;
        } else {
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

                return addFiles(e, udfQM, educationFM);
            } else if (source.equals(medicalCheckupTW)) {
                FormModel medicalCheckupFM = ((DBTableModel) medicalCheckupTW.getWidgetModel()).getFormModel();
                return addFiles(e, udfQM, medicalCheckupFM);
            } else if (source.equals(employeeDegreeTW)) {
                FormModel employeeDegreeFM = ((DBTableModel) employeeDegreeTW.getWidgetModel()).getFormModel();
                return addFiles(e, udfQM, employeeDegreeFM);
            } else if (source.equals(languagesTW)) {
                return true;
            } else if (source.equals(awardsTW)) {
                return true;
            } else if (source.equals(publicationTW)) {
                return true;
            } else if (source.equals(experienceTW)) {
                return true;
            } else if (source.equals(careerTW)) {
                return true;
            } else if (source.equals(scientificActivityTW)) {
                return true;
            } else if (source.equals(socialCategoriesTW)) {
                return true;
            }
        }
        return false;
    }

    private boolean addFiles(Entity e, QueryModel<USER_DOCUMENT_FILE> udfQM, FormModel medicalCheckupFM) {
        FileListFieldModel currentFLFM = (FileListFieldModel) medicalCheckupFM.getFieldModel("fileList");
        currentFLFM.permitMimeType(FileListFieldModel.JPEG);

        currentFLFM.getFileList().clear();
        currentFLFM.getDeleteList().clear();

        udfQM.addWhereAnd("userDocument", ECriteria.EQUAL, e.getId());

        CommonUtils.addFiles(udfQM, currentFLFM);

        return true;
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
            return canSave();
        } else if (source.equals(medicalCheckupTW)) {
            return preCreateTW(medicalCheckupTW);
        } else if (source.equals(employeeDegreeTW)) {
            return preCreateTW(employeeDegreeTW);
        } else if (source.equals(scientificManagementTW)) {
            return preCreateTW(scientificManagementTW);
        } else if (source.equals(awardsTW)) {
            return canSave();
        } else if (source.equals(publicationTW)) {
            return canSave();
        } else if (source.equals(experienceTW)) {
            return canSave();
        } else if (source.equals(careerTW)) {
            boolean canSave = canSave();
            try {
                if (canSave) {
                    DBTableModel careerTM = (DBTableModel) careerTW.getWidgetModel();
                    FormModel careerFM = careerTM.getFormModel();

                    EMPLOYEE employee = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
                    if (!employee.getStatus().getId().equals(ID.valueOf(5))) {
                        careerFM.getFieldModel("hourCount").setInEdit(false);
                    } else {
                        careerFM.getFieldModel("liveLoad").setInEdit(false);
                        careerFM.getFieldModel("wageRate").setInEdit(false);
                        careerFM.getFieldModel("rateLoad").setInEdit(false);
                    }
                    careerTW.refresh();
                }
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
            return canSave;
        } else if (source.equals(scientificActivityTW)) {
            return canSave();
        } else if (source.equals(socialCategoriesTW)) {
            return canSave();
        }

        return super.preCreate(source, buttonId);
    }

    private boolean canSave() {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        return true;
    }

    private boolean preCreateTW(TableWidget currentTW) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }

        FormModel currentFM = ((DBTableModel) currentTW.getWidgetModel()).getFormModel();

        FileListFieldModel currentFLFM = (FileListFieldModel) currentFM.getFieldModel("fileList");
        currentFLFM.permitMimeType(FileListFieldModel.JPEG);

        currentFLFM.getFileList().clear();
        currentFLFM.getDeleteList().clear();

        return true;
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
        } else if (source.equals(languagesTW)) {
            return preSaveLanguage(e, isNew);
        } else if (source.equals(medicalCheckupTW)) {
            return preSaveMedicalCheckup(e, isNew);
        } else if (source.equals(employeeDegreeTW)) {
            return preSaveEmployeeDegree(e, isNew);
        } else if (source.equals(publicationTW)) {
            return preSavePublication(e, isNew);
        } else if (source.equals(experienceTW)) {
            return preSaveExperience(e, isNew);
        } else if (source.equals(careerTW)) {
            return preSaveCareer(e, isNew);
        } else if (source.equals(scientificActivityTW)) {
            return preSaveScientificActivity(e, isNew);
        } else if (source.equals(scientificManagementTW)) {
            return preSaveScientificManagement(e, isNew);
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
                    CommonUtils.showMessageAndWriteLog("Unable to delete user languages", ex);
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
        } else if (source.equals(employeeDegreeTW)) {
            List<EMPLOYEE_DEGREE> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    EMPLOYEE_DEGREE mc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEGREE.class, e.getId());
                    mc.setDeleted(true);
                    delList.add(mc);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete employee degree", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(delList);
                employeeDegreeTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete employee degree: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(scientificManagementTW)) {
            List<SCIENTIFIC_MANAGEMENT> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    SCIENTIFIC_MANAGEMENT scientificManagement = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_MANAGEMENT.class, e.getId());
                    delList.add(scientificManagement);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete scientific management", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                scientificManagementTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete scientific management: ", ex);
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
                LOG.error("Unable to delete user awards: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(publicationTW)) {
            List<PUBLICATION> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PUBLICATION.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete publications", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                publicationTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete publications: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(experienceTW)) {
            List<PREVIOUS_EXPERIENCE> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PREVIOUS_EXPERIENCE.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete experiences", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                experienceTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete experiences: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(careerTW)) {
            List<EMPLOYEE_DEPT> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_DEPT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete careers", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                careerTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete careers: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(scientificActivityTW)) {
            List<SCIENTIFIC_ACTIVITY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SCIENTIFIC_ACTIVITY.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete scientific activities", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                scientificActivityTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete scientific activities: ", ex);
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
    public void deferredCreate(Object source, Entity e) {//TODO
        EMPLOYEE employee = (EMPLOYEE) e;
        if (source.equals(dataAFW)) {
            employee.setCreated(new Date());
            try {
                employee.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER"));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a social category", ex);
            }
            employee.setCode("000000000000");
            employee.setFirstName(employee.getFirstName());
            employee.setLastName(employee.getLastName());
            employee.setFirstNameEN(employee.getFirstNameEN());
            employee.setLastNameEN(employee.getLastNameEN());
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "EmployeesForm";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("registration");
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
