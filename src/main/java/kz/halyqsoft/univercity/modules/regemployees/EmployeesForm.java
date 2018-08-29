package kz.halyqsoft.univercity.modules.regemployees;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.Flag;
import kz.halyqsoft.univercity.entity.beans.univercity.view.*;
import kz.halyqsoft.univercity.modules.employee.EmployeeEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.CareerPostChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.LiveLoadChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.WageRateChangeListener;
import kz.halyqsoft.univercity.utils.register.UsersForm;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.FieldModel;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import java.util.*;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class EmployeesForm extends UsersForm {

    private TableWidget employeeDegreeTW, publicationTW;
    private TableWidget scientificActivityTW, scientificManagementTW;
    private TableWidget experienceTW, careerTW,childTW,magisterTW;

    private Button employeeDegreeButton, publicationButton, scientActButton;
    private Button scientManagemButton, experienceButton, careerButton,magisterButton,childButton;

    private boolean saveCareer;

    EmployeesForm(final FormModel dataFM, ENTRANCE_YEAR entranceYear) throws Exception {
        super(dataFM, entranceYear);
        getButtonsVL().removeComponent(getPreemRightButton());
        getEduDocsButton().addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                addToLayout(getEduDocTW(), getMedButton(), clickEvent);
            }
        });
        dataAFW.setCaption(getUILocaleUtil().getCaption("regapplicant.main.data"));

        factAddressButton.setCaption(getUILocaleUtil().getCaption("address.residential"));
        eduDocButton.setCaption(getUILocaleUtil().getCaption("education.document"));
    }

    @Override
    protected VerticalLayout getPreemptiveRightVL(GridFormWidget mainGFW, TableWidget languagesTW) {
        VerticalLayout preemLang = new VerticalLayout();
        preemLang.addComponent(languagesTW);
        return preemLang;
    }

    @Override
    protected void setOpeners() {
    }

    @Override
    protected VerticalLayout getMessForm(FormModel dataFM, Label mess) {
        employeeDegreeButton.setEnabled(false);
        publicationButton.setEnabled(false);
        experienceButton.setEnabled(false);
        careerButton.setEnabled(false);
        scientActButton.setEnabled(false);
        scientManagemButton.setEnabled(false);
        magisterButton.setEnabled(false);
        childButton.setEnabled(false);

        try {
            EMPLOYEE employee = (EMPLOYEE) dataFM.getEntity();
            EmployeeEdit.saveEmployeeWorkHour(employee, false);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }

        VerticalLayout messForm = new VerticalLayout();
        messForm.addComponent(mess);

        Button againButton = new Button(getUILocaleUtil().getCaption("back.to.new.employee.registration"));
        againButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                RegisterEmployeesView.regButton.click();
            }
        });

        messForm.addComponent(againButton);
        return messForm;
    }

    @Override
    protected Map<String, Boolean> getConditionsMap() {
        Map<String, Boolean> conditionsMap = new HashMap<>();
        conditionsMap.put(getUILocaleUtil().getMessage("info.save.base.data.first"), !saveData);
        conditionsMap.put(getUILocaleUtil().getMessage("info.save.passport"), !savePass);
        conditionsMap.put(getUILocaleUtil().getMessage("info.save.career"), !saveCareer);
        return conditionsMap;
    }

    @Override
    protected List<Button> getButtons() {
        List<Button> buttons = new ArrayList<>();
        buttons.add(employeeDegreeButton);
        buttons.add(publicationButton);
        buttons.add(scientActButton);
        buttons.add(scientManagemButton);
        buttons.add(experienceButton);
        buttons.add(careerButton);
        buttons.add(childButton);
        buttons.add(magisterButton);
        return buttons;
    }

    @Override
    protected Button getAfterMedButton() {
        return employeeDegreeButton;
    }

    @Override
    protected void initOwnButtons(FormModel dataFM) {
        employeeDegreeButton = createFormButton("scientific.degrees", false);
        employeeDegreeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                employeeDegreeTW = getTableWidget(V_EMPLOYEE_DEGREE.class, "employee", false);
                addToLayout(employeeDegreeTW, publicationButton, event);
            }
        });

        publicationButton = createFormButton(V_PUBLICATION.class);
        publicationButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                publicationTW = getTableWidget(V_PUBLICATION.class, "employee", null);
                addToLayout(publicationTW, scientActButton, event);
            }
        });

        scientActButton = createFormButton(V_SCIENTIFIC_ACTIVITY.class);
        scientActButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                scientificActivityTW = getTableWidget(V_SCIENTIFIC_ACTIVITY.class, "employee", null);
                addToLayout(scientificActivityTW, scientManagemButton, event);
            }
        });

        scientManagemButton = createFormButton(V_SCIENTIFIC_MANAGEMENT.class);
        scientManagemButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                scientificManagementTW = getTableWidget(V_SCIENTIFIC_MANAGEMENT.class, "employee", null);
                addToLayout(scientificManagementTW, experienceButton, event);
            }
        });

        experienceButton = createFormButton("experience", false);
        experienceButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                experienceTW = getTableWidget(PREVIOUS_EXPERIENCE.class, "employee", null);
                addToLayout(experienceTW, careerButton, event);
            }
        });

        careerButton = createFormButton("career", true);
        careerButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                careerTW = getTableWidget(V_EMPLOYEE_DEPT.class, "employee", null);
                flag = Flag.CAREER;

                EMPLOYEE emp = null;
                try {
                    emp = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }

                DBTableModel careerTM = (DBTableModel) careerTW.getWidgetModel();
                careerTM.setReadOnly(false);
                careerTM.setCrudEntityClass(EMPLOYEE_DEPT.class);
                QueryModel careerQM = careerTM.getQueryModel();
                ID employeeId = ID.valueOf(-1);
                if (emp != null && emp.getStatus() != null && emp.getStatus().getId().equals(ID.valueOf(5))) {
                    employeeId = emp.getId();
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

                addToLayout(careerTW, childButton, event);
                setActive(event);
                contentHL.removeAllComponents();
                contentHL.addComponent(careerTW);
            }
        });

        childButton=createFormButton("child",true);
        childButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                childTW = getTableWidget(CHILD.class, "employee", null);
                flag = Flag.CHILD;

                EMPLOYEE emp = null;
                try {
                    emp = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }

                DBTableModel childTM = (DBTableModel) childTW.getWidgetModel();
                childTM.setReadOnly(false);
                childTM.setCrudEntityClass(CHILD.class);
                QueryModel childQM = childTM.getQueryModel();
                ID employeeId = ID.valueOf(-1);
                if (emp != null && emp.getStatus() != null && emp.getStatus().getId().equals(ID.valueOf(5))) {
                    employeeId = emp.getId();
                    childTM.getColumnModel("birthDate").setInTable(false);
                    childTM.getColumnModel("sex").setInTable(false);
                } else {
                    childTM.getColumnModel("birthDate").setInTable(true);
                    childTM.getColumnModel("sex").setInTable(true);
                }
                childQM.addWhere("employee", ECriteria.EQUAL, employeeId);

                FormModel childFM = childTM.getFormModel();
                addToLayout(childTW, magisterButton, clickEvent);
                setActive(clickEvent);
                contentHL.removeAllComponents();
                contentHL.addComponent(childTW);
            }
        });

        magisterButton=createFormButton("master",true);
        magisterButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                magisterTW = getTableWidget(MASTER.class, "employee", null);
                flag = Flag.MAGISTER;

                EMPLOYEE emp = null;
                try {
                    emp = (EMPLOYEE) dataAFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }

                DBTableModel masterTM = (DBTableModel) magisterTW.getWidgetModel();
                masterTM.setReadOnly(false);
                masterTM.setCrudEntityClass(MASTER.class);
                QueryModel masterQM = masterTM.getQueryModel();
                ID employeeId = ID.valueOf(-1);
                if (emp != null && emp.getStatus() != null && emp.getStatus().getId().equals(ID.valueOf(5))) {
                    employeeId = emp.getId();
                    masterTM.getColumnModel("entranceYear").setInTable(false);
                    masterTM.getColumnModel("graduationYear").setInTable(false);
                    masterTM.getColumnModel("university").setInTable(false);
                    masterTM.getColumnModel("speciality").setInTable(false);
                    masterTM.getColumnModel("diplomaNumber").setInTable(false);

                } else {
                    masterTM.getColumnModel("entranceYear").setInTable(true);
                    masterTM.getColumnModel("graduationYear").setInTable(true);
                    masterTM.getColumnModel("university").setInTable(true);
                    masterTM.getColumnModel("speciality").setInTable(true);
                    masterTM.getColumnModel("diplomaNumber").setInTable(true);
                }
                masterQM.addWhere("employee", ECriteria.EQUAL, employeeId);

                FormModel masterFM = masterTM.getFormModel();
                setActive(clickEvent);
                contentHL.removeAllComponents();
                contentHL.addComponent(magisterTW);

            }
        });
    }

    @Override
    protected void initSpec(FormModel dataFM) {

    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        if (source.equals(careerTW)) {
            EMPLOYEE_DEPT ed = (EMPLOYEE_DEPT) e;
            try {
                ed.setLiveLoad(0);
                ed.setWageRate(1.0);
                ed.setRateLoad(0.0);
                ed.setHourCount(0.0);
                LOG.info(ed.getHourCount() + "");
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to set employee type", ex);
            }
        }
    }

    @Override
    protected boolean checkFlag(Flag flag) {
        Boolean saved;
        switch (flag) {
            case CAREER:
                if (careerTW.getEntityCount() > 0) {
                    saveCareer = true;
                }
                break;
        }

        return false;
    }


    private Button createFormButton(Class<? extends Entity> entityClass) {
        Button temp = new Button();
        temp.setCaption(getUILocaleUtil().getEntityLabel(entityClass));
        temp.setWidth(FORM_BUTTON_WIDTH);
        temp.setStyleName("left");
        return temp;
    }

    @Override
    protected String getLogin(String login) throws Exception {

        return CommonUtils.getLogin(login);
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
                employeeDegree.setPlaceOfIssue(vEmployeeDegree.getPlaceOfIssue());
                employeeDegree.setDissertationTopic(vEmployeeDegree.getDissertationTopic());
                employeeDegree.setCandidate(vEmployeeDegree.getCandidate());
                employeeDegree.setSpeciality(vEmployeeDegree.getSpeciality());
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
                employeeDegree.setPlaceOfIssue(vEmployeeDegree.getPlaceOfIssue());
                employeeDegree.setCandidate(vEmployeeDegree.getCandidate());
                employeeDegree.setSpeciality(vEmployeeDegree.getSpeciality());
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
        EMPLOYEE_DEPT employeeDept = (EMPLOYEE_DEPT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                EMPLOYEE employee = (EMPLOYEE) fm.getEntity();
                employeeDept.setEmployee(employee);
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
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(employeeDept);
                careerTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge an employee dept", ex);
            }
        }
        return false;
    }

    private boolean preSaveChild(Entity e,boolean isNew){
        CHILD child = (CHILD) e;
        FormModel formModel = dataAFW.getWidgetModel();
        if(isNew){
            try {
                EMPLOYEE employee = (EMPLOYEE) formModel.getEntity();
                child.setEmployee(employee);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(child);

                QueryModel childQM = ((DBTableModel)childTW.getWidgetModel()).getQueryModel();
                childQM.addWhere("employee", ECriteria.EQUAL, employee.getId());

                childTW.refresh();
                showSavedNotification();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(child);
                childTW.refresh();
                showSavedNotification();
            }catch (Exception ex){
                CommonUtils.showMessageAndWriteLog("Unable to merge an employee child", ex);
            }
        }

        return false;
    }

    private boolean preSaveMagister(Entity e,boolean isNew){
        MASTER master = (MASTER) e;
        FormModel formModel = dataAFW.getWidgetModel();
        if(isNew){
            try {
                EMPLOYEE employee = (EMPLOYEE) formModel.getEntity();
                master.setEmployee(employee);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(master);

                QueryModel masterQM = ((DBTableModel)magisterTW.getWidgetModel()).getQueryModel();
                masterQM.addWhere("employee", ECriteria.EQUAL, employee.getId());

                magisterTW.refresh();
                showSavedNotification();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(master);
                magisterTW.refresh();
                showSavedNotification();
            }catch (Exception e2){
                CommonUtils.showMessageAndWriteLog("Unable to merge an employee master", e2);
            }
        }

        return false;
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

    private boolean addFiles(Object source, Entity e) {
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

            if (source.equals(employeeDegreeTW)) {
                FormModel employeeDegreeFM = ((DBTableModel) employeeDegreeTW.getWidgetModel()).getFormModel();
                return refreshFiles(e, udfQM, employeeDegreeFM);
            } else if (source.equals(publicationTW)) {
                return true;
            } else if (source.equals(experienceTW)) {
                return true;
            } else if (source.equals(careerTW)) {
                return true;
            } else return source.equals(scientificActivityTW);
        }
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(employeeDegreeTW)) {
            return preCreateTW(employeeDegreeTW);
        } else if (source.equals(scientificManagementTW)) {
            return preCreateTW(scientificManagementTW);
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
        }else if(source.equals(childTW)){
            return   canSave();
        }else if(source.equals(magisterTW)){
            return canSave();
        }

        return super.preCreate(source, buttonId);
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
        if (source.equals(employeeDegreeTW)) {
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
        }else if(source.equals(childTW)){
            return preSaveChild(e,isNew);
        }else if(source.equals(magisterTW)){
            return preSaveMagister(e,isNew);
        }
        return super.preSave(source, e, isNew, buttonId);
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(employeeDegreeTW)) {
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
        }

        return super.preDelete(source, entities, buttonId);
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
        EMPLOYEE employee = (EMPLOYEE) e;
        if (source.equals(dataAFW)) {
            employee.setCreated(new Date());
            employee.setCreatedBy(CommonUtils.getCurrentUserLogin());
            try {
                employee.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER"));
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a social category", ex);
            }
            employee.setCode("000000000000");//TODO
            employee.setFirstName(employee.getFirstName());
            employee.setLastName(employee.getLastName());
            employee.setFirstNameEN(employee.getFirstNameEN());
            employee.setLastNameEN(employee.getLastNameEN());
        }
    }

    @Override
    public String getViewName() {
        return "EmployeesForm";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("regemployee");
    }
}
