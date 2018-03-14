package kz.halyqsoft.univercity.modules.regusers;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.Utils.ErrorUtil;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 13.03.2018
 */
public class CreateApplicant implements Button.ClickListener {

    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {
        FormModel applicant = new FormModel(STUDENT.class, true);
        applicant.setReadOnly(false);
        applicant.setTitleVisible(false);
//        ((FKFieldModel) applicant.getFieldModel("level")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
//        ((FKFieldModel) applicant.getFieldModel("sex")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
//        ((FKFieldModel) applicant.getFieldModel("maritalStatus")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
//        ((FKFieldModel) applicant.getFieldModel("nationality")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
//        ((FKFieldModel) applicant.getFieldModel("entranceYear")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
//        ((FKFieldModel) applicant.getFieldModel("category")).getQueryModel().getFrom().getBaseItem().setSchema("KBTU");
        QueryModel cityQM = ((FKFieldModel) applicant.getFieldModel("citizenship")).getQueryModel();
        cityQM.addWhereNull("parent");
        cityQM.addOrder("countryName");
        QueryModel nationalityQM = ((FKFieldModel) applicant.getFieldModel("nationality")).getQueryModel();
        nationalityQM.addOrder("nationName");

        applicant.getFieldModel("firstName").setWidth(300);
        applicant.getFieldModel("lastName").setWidth(300);
        applicant.getFieldModel("middleName").setWidth(300);
        applicant.getFieldModel("firstNameEN").setWidth(300);
        applicant.getFieldModel("lastNameEN").setWidth(300);
        applicant.getFieldModel("middleNameEN").setWidth(300);
        applicant.getFieldModel("birthDate").setWidth(300);
        applicant.getFieldModel("sex").setWidth(300);
        applicant.getFieldModel("maritalStatus").setWidth(300);
        applicant.getFieldModel("nationality").setWidth(300);
        applicant.getFieldModel("citizenship").setWidth(300);
        applicant.getFieldModel("phoneMobile").setWidth(300);
        applicant.getFieldModel("level").setWidth(300);
        applicant.getFieldModel("category").setWidth(300);

        try {
            applicant.createNew();

            Calendar c = Calendar.getInstance();
            QueryModel<ENTRANCE_YEAR> qmEntranceYear = new QueryModel<>(ENTRANCE_YEAR.class);
            qmEntranceYear.addWhere("beginYear", ECriteria.EQUAL, c.get(Calendar.YEAR));
            ENTRANCE_YEAR ey = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmEntranceYear);

            ((STUDENT) applicant.getEntity()).setEntranceYear(ey);
            ((STUDENT) applicant.getEntity()).setLevel(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(LEVEL.class, ID.valueOf(1)));
            ((STUDENT) applicant.getEntity()).setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
//            ApplicantsForm ee = new ApplicantsForm(applicant);//TODO
//            ApplicantsUI.getInstance().openCommonView(ee);
        } catch (Exception ex) {
            ErrorUtil.LOG.error("Unable to create new Applicant: ", ex);
            Message.showError("Unable to create new Applicant");
        }
    }
}
