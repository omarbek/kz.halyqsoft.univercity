package kz.halyqsoft.univercity.modules.regapplicants;

import kz.halyqsoft.univercity.utils.ErrorUtils;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 13.03.2018
 */
public class RegisterApplicantsView extends AbstractTaskView implements EntityListener {

    public RegisterApplicantsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        FormModel studentFM = new FormModel(STUDENT.class, true);
        studentFM.setReadOnly(false);
        studentFM.setTitleVisible(false);
        QueryModel cityQM = ((FKFieldModel) studentFM.getFieldModel("citizenship")).getQueryModel();
        cityQM.addWhereNull("parent");
        cityQM.addOrder("countryName");
        QueryModel nationalityQM = ((FKFieldModel) studentFM.getFieldModel("nationality")).getQueryModel();
        nationalityQM.addOrder("nationName");

        studentFM.getFieldModel("firstName").setWidth(300);
        studentFM.getFieldModel("lastName").setWidth(300);
        studentFM.getFieldModel("middleName").setWidth(300);
        studentFM.getFieldModel("firstNameEN").setWidth(300);
        studentFM.getFieldModel("lastNameEN").setWidth(300);
        studentFM.getFieldModel("middleNameEN").setWidth(300);
        studentFM.getFieldModel("birthDate").setWidth(300);
        studentFM.getFieldModel("sex").setWidth(300);
        studentFM.getFieldModel("maritalStatus").setWidth(300);
        studentFM.getFieldModel("nationality").setWidth(300);
        studentFM.getFieldModel("citizenship").setWidth(300);
        studentFM.getFieldModel("phoneMobile").setWidth(300);
        studentFM.getFieldModel("level").setWidth(300);
        studentFM.getFieldModel("category").setWidth(300);

        try {
            studentFM.createNew();

            java.util.Calendar c = java.util.Calendar.getInstance();
            QueryModel<ENTRANCE_YEAR> qmEntranceYear = new QueryModel<>(ENTRANCE_YEAR.class);
            qmEntranceYear.addWhere("beginYear", ECriteria.EQUAL, c.get(Calendar.YEAR));
            ENTRANCE_YEAR ey = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmEntranceYear);

            ((STUDENT) studentFM.getEntity()).setEntranceYear(ey);
            ((STUDENT) studentFM.getEntity()).setLevel(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(LEVEL.class, ID.valueOf(1)));
            ((STUDENT) studentFM.getEntity()).setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
            getContent().addComponent(new ApplicantsForm(studentFM));
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to create new Applicant: ", ex);
            Message.showError("Unable to create new Applicant");
        }
    }
}
