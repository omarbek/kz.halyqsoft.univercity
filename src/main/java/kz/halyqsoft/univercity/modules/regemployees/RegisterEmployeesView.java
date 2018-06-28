package kz.halyqsoft.univercity.modules.regemployees;

import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class RegisterEmployeesView extends AbstractTaskView {

    static Button regButton;
    private VerticalLayout registerVL;
    private boolean removeAll = false;

    public RegisterEmployeesView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        registerVL = new VerticalLayout();
        regButton = new Button();
        regButton.setCaption(getUILocaleUtil().getCaption("sign.up"));
        regButton.setWidth(150.0F, Unit.PIXELS);
        regButton.setIcon(new ThemeResource("img/button/add.png"));
        regButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createEmployees();
                removeAll = true;
            }
        });
        registerVL.addComponent(regButton);
        registerVL.setComponentAlignment(regButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(registerVL);
    }

    private void createEmployees() {
        if (removeAll) {
            registerVL.removeAllComponents();
        }
        FormModel employeeFM = new FormModel(EMPLOYEE.class, true);
        employeeFM.setReadOnly(false);
        employeeFM.setTitleVisible(false);
        QueryModel cityQM = ((FKFieldModel) employeeFM.getFieldModel("citizenship")).getQueryModel();
        cityQM.addWhereNull("parent");
        cityQM.addOrder("countryName");
        QueryModel nationalityQM = ((FKFieldModel) employeeFM.getFieldModel("nationality")).getQueryModel();
        nationalityQM.addOrder("nationName");

        employeeFM.getFieldModel("firstName").setWidth(300);
        employeeFM.getFieldModel("lastName").setWidth(300);
        employeeFM.getFieldModel("middleName").setWidth(300);
        employeeFM.getFieldModel("firstNameEN").setWidth(300);
        employeeFM.getFieldModel("lastNameEN").setWidth(300);
        employeeFM.getFieldModel("middleNameEN").setWidth(300);
        employeeFM.getFieldModel("birthDate").setWidth(300);
        employeeFM.getFieldModel("sex").setWidth(300);
        employeeFM.getFieldModel("maritalStatus").setWidth(300);
        employeeFM.getFieldModel("nationality").setWidth(300);
        employeeFM.getFieldModel("citizenship").setWidth(300);
        employeeFM.getFieldModel("phoneMobile").setWidth(300);
        employeeFM.getFieldModel("status").setWidth(300);
        employeeFM.getFieldModel("bachelor").setWidth(300);
        employeeFM.getFieldModel("master").setWidth(300);

        try {
            employeeFM.createNew();

            Calendar c = Calendar.getInstance();
            QueryModel<ENTRANCE_YEAR> qmEntranceYear = new QueryModel<>(ENTRANCE_YEAR.class);
            qmEntranceYear.addWhere("beginYear", ECriteria.EQUAL, c.get(Calendar.YEAR));
            ENTRANCE_YEAR ey = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(
                    qmEntranceYear);
            ((EMPLOYEE) employeeFM.getEntity()).setStatus(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(EMPLOYEE_STATUS.class, ID.valueOf(1)));

//            String qewe = "qewe";
//            ((EMPLOYEE) employeeFM.getEntity()).setFirstName(qewe);
//            ((EMPLOYEE) employeeFM.getEntity()).setFirstNameEN(qewe);
//            ((EMPLOYEE) employeeFM.getEntity()).setLastName(qewe);
//            ((EMPLOYEE) employeeFM.getEntity()).setLastNameEN(qewe);
//            ((EMPLOYEE) employeeFM.getEntity()).setBirthDate(new Date());
//            ((EMPLOYEE) employeeFM.getEntity()).setSex(SessionFacadeFactory.getSessionFacade(
//                    CommonEntityFacadeBean.class).lookup(SEX.class, ID.valueOf(1)));
//            ((EMPLOYEE) employeeFM.getEntity()).setMaritalStatus(SessionFacadeFactory.getSessionFacade(
//                    CommonEntityFacadeBean.class).lookup(MARITAL_STATUS.class, ID.valueOf(1)));
//            ((EMPLOYEE) employeeFM.getEntity()).setNationality(SessionFacadeFactory.getSessionFacade(
//                    CommonEntityFacadeBean.class).lookup(NATIONALITY.class, ID.valueOf(1)));
//            ((EMPLOYEE) employeeFM.getEntity()).setCitizenship(SessionFacadeFactory.getSessionFacade(
//                    CommonEntityFacadeBean.class).lookup(COUNTRY.class, ID.valueOf(1)));
//            ((EMPLOYEE) employeeFM.getEntity()).setEmail(qewe + "@mail.ru");
//            ((EMPLOYEE) employeeFM.getEntity()).setPhoneMobile("707");

            registerVL.addComponent(new EmployeesForm(employeeFM, ey));
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to create new employee", ex);
        }
    }
}
