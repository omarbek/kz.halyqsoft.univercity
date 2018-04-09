package kz.halyqsoft.univercity.utils.register;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ADDRESS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ADDRESS_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
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

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Address {

    private GridFormWidget addressRegGFW;
    private GridFormWidget addressFactGFW;
    private AbstractFormWidget dataAFW;
    private ApplicantsForm applicantsForm;
    private FormModel addressRegFM;
    private FormModel addressFactFM;

    private static final int ADDRESS_REG = 1;

    public GridFormWidget getAddressRegGFW() {
        return addressRegGFW;
    }

    public GridFormWidget getAddressFactGFW() {
        return addressFactGFW;
    }

    public Address(AbstractFormWidget dataAFW, ApplicantsForm applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(String caption, int addressNumber) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(ErrorUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(ErrorUtils.getUILocaleUtil().getCaption(caption));
        FormModel addressFM;
        if (addressNumber == ADDRESS_REG) {
            addressRegGFW = new GridFormWidget(USER_ADDRESS.class);
            addressRegGFW.addEntityListener(applicantsForm);
            addressRegFM = addressRegGFW.getWidgetModel();
            addressFM = addressRegFM;
        } else {
            addressFactGFW = new GridFormWidget(USER_ADDRESS.class);
            addressFactGFW.addEntityListener(applicantsForm);
            addressFactFM = addressFactGFW.getWidgetModel();
            addressFM = addressFactFM;
        }
        setFormModel(addressNumber, sb, addressFM);
    }

    private void setFormModel(int addressNumber, StringBuilder sb, FormModel addressFM) throws Exception {
        addressFM.setTitleResource("address.registration");
        addressFM.setErrorMessageTitle(sb.toString());
        addressFM.setButtonsVisible(false);

        FKFieldModel countryRegFM = (FKFieldModel) addressFM.getFieldModel("country");
        QueryModel countryRegQM = countryRegFM.getQueryModel();
        countryRegQM.addWhereNull("parent");

        FKFieldModel regionRegFM = (FKFieldModel) addressFM.getFieldModel("region");
        QueryModel regionRegQM = regionRegFM.getQueryModel();
        regionRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel cityRegFM = (FKFieldModel) addressFM.getFieldModel("city");
        QueryModel cityRegQM = cityRegFM.getQueryModel();
        cityRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        FKFieldModel villageRegFM = (FKFieldModel) addressFM.getFieldModel("village");
        QueryModel villageRegQM = villageRegFM.getQueryModel();
        villageRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        COUNTRY regionReg = null, cityReg = null, villageReg = null;
        if (dataAFW.getWidgetModel().isCreateNew()) {
            addressFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressRegQM = new QueryModel<>(USER_ADDRESS.class);
            addressRegQM.addWhere("user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            addressRegQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(addressNumber));
            try {
                USER_ADDRESS addressReg = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressRegQM);
                if (addressReg != null) {
                    addressFM.loadEntity(addressReg.getId());
                    addressReg = (USER_ADDRESS) addressFM.getEntity();
                    regionReg = addressReg.getRegion();
                    cityReg = addressReg.getCity();
                    villageReg = addressReg.getVillage();
                }
            } catch (NoResultException ex) {
                addressFM.createNew();
            }
        }

        countryRegFM.getListeners().add(new CountryChangeListener(regionReg, regionRegFM));
        regionRegFM.getListeners().add(new RegionChangeListener(cityReg, cityRegFM));
        cityRegFM.getListeners().add(new CityChangeListener(villageReg, villageRegFM));
    }

    public boolean preSave(Entity e, boolean isNew, int addressNumber) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(ErrorUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        USER_ADDRESS ua = (USER_ADDRESS) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                ua.setUser((STUDENT) fm.getEntity());
                ua.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(addressNumber)));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ua);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to createCertificate a registration address: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ua);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to merge a registration address: ", ex);
            }
        }
        return false;
    }

    public void save(int addressNumber) {
        if (addressNumber == ADDRESS_REG) {
            if (addressRegFM.isModified()) {
                addressRegGFW.save();
            }
        } else {
            if (addressFactFM.isModified()) {
                addressFactGFW.save();
            }
        }
    }

}
