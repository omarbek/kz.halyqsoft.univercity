package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.validator.EmailValidator;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_RELATIVE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.RELATIVE_TYPE;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.utils.AddressUtils;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import kz.halyqsoft.univercity.utils.changelisteners.CityChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.CountryChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.RegionChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Parent {

    private GridFormWidget fatherGFW;
    private GridFormWidget motherGFW;
    private AbstractFormWidget dataAFW;
    private ApplicantsForm applicantsForm;
    private FormModel motherFM;
    private FormModel fatherFM;

    private static final int FATHER = 1;

    public GridFormWidget getFatherGFW() {
        return fatherGFW;
    }

    public GridFormWidget getMotherGFW() {
        return motherGFW;
    }

    public Parent(AbstractFormWidget dataAFW, ApplicantsForm applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public boolean preSave(Entity e, boolean isNew, int parentNumber) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(ErrorUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        STUDENT_RELATIVE sr = (STUDENT_RELATIVE) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                sr.setStudent((STUDENT) fm.getEntity());
                sr.setRelativeType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(RELATIVE_TYPE.class, ID.valueOf(parentNumber)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sr);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to createCertificate a fathers data: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(sr);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to merge a fathers address: ", ex);
            }
        }
        return false;
    }

    public void create(String caption, int parent_number) throws Exception {
        StringBuilder parentSB = new StringBuilder();
        parentSB.append(ErrorUtils.getUILocaleUtil().getCaption("title.error"));
        parentSB.append(": ");
        parentSB.append(ErrorUtils.getUILocaleUtil().getCaption(caption));
        FormModel parentFM;
        if (parent_number == FATHER) {
            fatherGFW = new GridFormWidget(STUDENT_RELATIVE.class);
            fatherGFW.addEntityListener(applicantsForm);
            fatherFM = createFormModel(fatherGFW, parentSB, caption);
            parentFM = fatherFM;
        } else {
            motherGFW = new GridFormWidget(STUDENT_RELATIVE.class);
            motherGFW.addEntityListener(applicantsForm);
            motherFM = createFormModel(motherGFW, parentSB, caption);
            parentFM = motherFM;
        }
        setFormModel(parent_number, parentFM);
    }

    private void setFormModel(int parent_number, FormModel parentFM) throws Exception {
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
    }

    private FormModel createFormModel(GridFormWidget widget, StringBuilder sb, String caption) {
        FormModel formModel = widget.getWidgetModel();
        formModel.setTitleResource(caption);
        formModel.setErrorMessageTitle(sb.toString());
        formModel.setButtonsVisible(false);
        formModel.getFieldModel("email").getValidators().add(new EmailValidator("Введён некорректный E-mail"));
        return formModel;
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
    public void save(int parentNumber){
        if (parentNumber == FATHER) {
            if (fatherFM.isModified()) {
                fatherGFW.save();
            }
        } else {
            if (motherFM.isModified()) {
                motherGFW.save();
            }
        }
    }
}
