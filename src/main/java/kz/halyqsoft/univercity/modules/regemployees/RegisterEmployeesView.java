package kz.halyqsoft.univercity.modules.regemployees;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.Calendar;

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
        regButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createEmployees();
                removeAll = true;
            }
        });
        registerVL.addComponent(regButton);
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
            ENTRANCE_YEAR ey = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmEntranceYear);

//            ((EMPLOYEE) employeeFM.getEntity()).setEntranceYear(ey);//TODO
//            ((EMPLOYEE) employeeFM.getEntity()).setLevel(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(LEVEL.class, ID.valueOf(1)));
//            ((EMPLOYEE) employeeFM.getEntity()).setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));

//            registerVL.addComponent(new EmployeesForm(employeeFM, ey));
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to createCertificate new Applicant: ", ex);
            Message.showError("Unable to createCertificate new Applicant");
        }
    }
}
