package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ADDRESS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ADDRESS_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.utils.changelisteners.CityChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.CountryChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.RegionChangeListener;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Omarbek
 * @created 17.05.2017.
 */
@SuppressWarnings("serial")
public class AddressesTab extends VerticalLayout {

    private final StudentEdit.StudentEditHelper studentEditHelper;

    private final boolean readOnly;

    private CommonFormWidget registrationAddressFW;

    private CommonFormWidget residentialAddressFW;

    public AddressesTab(StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) throws Exception {
        this.studentEditHelper = studentEditHelper;
        this.readOnly = readOnly;

        setSpacing(true);
        setSizeFull();

        HorizontalLayout formsHL = new HorizontalLayout();
        formsHL.setSpacing(true);

        registrationAddressFW = createRegistrationAddress();
        formsHL.addComponent(registrationAddressFW);

        residentialAddressFW = createResidentialAddress();
        formsHL.addComponent(residentialAddressFW);

        addComponent(formsHL);
        setComponentAlignment(formsHL, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            Button save = studentEditHelper.createSaveButton();
            save.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (registrationAddressFW.getWidgetModel().isModified()) {
                        registrationAddressFW.save();
                    }

                    if (residentialAddressFW.getWidgetModel().isModified()) {
                        residentialAddressFW.save();
                    }
                }
            });

            Button cancel = studentEditHelper.createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    registrationAddressFW.cancel();
                    residentialAddressFW.cancel();
                }
            });

            HorizontalLayout buttonPanel = studentEditHelper.createButtonPanel();
            buttonPanel.addComponents(save, cancel);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
            addComponent(buttonPanel);
            setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }
    }

    private CommonFormWidget createRegistrationAddress() throws Exception {
        CommonFormWidget addressRegFW = new CommonFormWidget(USER_ADDRESS.class);
        addressRegFW.addEntityListener(new RegistrationAddressListener());
        final FormModel addressRegFM = addressRegFW.getWidgetModel();
        addressRegFM.setReadOnly(readOnly);
        addressRegFM.setTitleResource("address.registration");
        String errorString = studentEditHelper.getUiLocaleUtil().getCaption("title.error") + ": " + studentEditHelper.getUiLocaleUtil().getCaption("address.registration");
        addressRegFM.setErrorMessageTitle(errorString);
        addressRegFM.setButtonsVisible(false);

        FKFieldModel countryRegFM = (FKFieldModel) addressRegFM.getFieldModel("country");
        QueryModel countryRegQM = countryRegFM.getQueryModel();
        countryRegQM.addWhereNull("parent");
        countryRegQM.addOrder("countryName");

        FKFieldModel regionRegFM = (FKFieldModel) addressRegFM.getFieldModel("region");
        QueryModel regionRegQM = regionRegFM.getQueryModel();
        regionRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        regionRegQM.addOrder("countryName");

        FKFieldModel cityRegFM = (FKFieldModel) addressRegFM.getFieldModel("city");
        QueryModel cityRegQM = cityRegFM.getQueryModel();
        cityRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        cityRegQM.addOrder("countryName");

        FKFieldModel villageRegFM = (FKFieldModel) addressRegFM.getFieldModel("village");
        QueryModel villageRegQM = villageRegFM.getQueryModel();
        villageRegQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        villageRegQM.addOrder("countryName");

        COUNTRY regionReg = null;
        COUNTRY cityReg = null;
//		T_DISTRICT districtReg = null;
        COUNTRY villageReg = null;
        if (studentEditHelper.isStudentNew()) {
            addressRegFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressRegQM = new QueryModel<>(USER_ADDRESS.class);
            addressRegQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());
            addressRegQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(1));
            try {
                USER_ADDRESS addressReg = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressRegQM);

                if (addressReg != null) {
                    addressRegFM.loadEntity(addressReg.getId());
                    addressReg = (USER_ADDRESS) addressRegFM.getEntity();
                    regionReg = addressReg.getRegion();
                    cityReg = addressReg.getCity();
//					districtReg = addressReg.getDistrict();
                    villageReg = addressReg.getVillage();
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    addressRegFM.loadEntity(ID.valueOf(-1));
                } else {
                    addressRegFM.createNew();
                }
            }
        }

        countryRegFM.getListeners().add(new CountryChangeListener(regionReg, regionRegFM));
        regionRegFM.getListeners().add(new RegionChangeListener(cityReg, cityRegFM));
//		cityRegFM.getListeners().add(new DistrictChangeListener(districtReg, districtRegFM));
        cityRegFM.getListeners().add(new CityChangeListener(villageReg, villageRegFM));

        return addressRegFW;
    }

    private CommonFormWidget createResidentialAddress() throws Exception {
        CommonFormWidget residentialAddressFW = new CommonFormWidget(USER_ADDRESS.class);
        residentialAddressFW.addEntityListener(new ResidentialAddressListener());
        final FormModel addressFactFM = residentialAddressFW.getWidgetModel();
        addressFactFM.setReadOnly(readOnly);
        addressFactFM.setTitleResource("address.residential");
        String errorMessage = studentEditHelper.getUiLocaleUtil().getCaption("title.error") + ": " + studentEditHelper.getUiLocaleUtil().getCaption("address.residential");
        addressFactFM.setErrorMessageTitle(errorMessage);
        addressFactFM.setButtonsVisible(false);

        FKFieldModel countryFactFM = (FKFieldModel) addressFactFM.getFieldModel("country");
        QueryModel countryFactQM = countryFactFM.getQueryModel();
        countryFactQM.addWhereNull("parent");
        countryFactQM.addOrder("countryName");

        FKFieldModel regionFactFM = (FKFieldModel) addressFactFM.getFieldModel("region");
        QueryModel regionFactQM = regionFactFM.getQueryModel();
        regionFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        regionFactQM.addOrder("countryName");

        FKFieldModel cityFactFM = (FKFieldModel) addressFactFM.getFieldModel("city");
        QueryModel cityFactQM = cityFactFM.getQueryModel();
        cityFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        cityFactQM.addOrder("countryName");

        FKFieldModel villageFactFM = (FKFieldModel) addressFactFM.getFieldModel("village");
        QueryModel villageFactQM = villageFactFM.getQueryModel();
        villageFactQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        villageFactQM.addOrder("countryName");

        COUNTRY regionFact = null;
        COUNTRY cityFact = null;
//		T_DISTRICT districtFact = null;
        COUNTRY villageFact = null;
        if (studentEditHelper.isStudentNew()) {
            addressFactFM.createNew();
        } else {
            QueryModel<USER_ADDRESS> addressFactQM = new QueryModel<>(USER_ADDRESS.class);
            addressFactQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());
            addressFactQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(2));
            try {
                USER_ADDRESS addressFact = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressFactQM);

                if (addressFact != null) {
                    addressFactFM.loadEntity(addressFact.getId());
                    addressFact = (USER_ADDRESS) addressFactFM.getEntity();
                    regionFact = addressFact.getRegion();
                    cityFact = addressFact.getCity();
//					districtFact = addressFact.getDistrict();
                    villageFact = addressFact.getVillage();
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    addressFactFM.loadEntity(ID.valueOf(-1));
                } else {
                    addressFactFM.createNew();
                }
            }
        }

        countryFactFM.getListeners().add(new CountryChangeListener(regionFact, regionFactFM));
        regionFactFM.getListeners().add(new RegionChangeListener(cityFact, cityFactFM));
        cityFactFM.getListeners().add(new CityChangeListener(villageFact, villageFactFM));
//		cityFactFM.getListeners().add(new DistrictChangeListener(districtFact, districtFactFM));
        return residentialAddressFW;
    }

    private class RegistrationAddressListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            if (studentEditHelper.isStudentNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            USER_ADDRESS userAddress = (USER_ADDRESS) entity;
            if (isNew) {
                try {
                    userAddress.setUser(studentEditHelper.getStudent());
                    userAddress.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(1)));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userAddress);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to createCertificate a registration address: ", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(userAddress);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to merge a registration address: ", ex);
                }
            }

            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }

    private class ResidentialAddressListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            if (studentEditHelper.isStudentNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            USER_ADDRESS userAddress = (USER_ADDRESS) entity;
            if (isNew) {
                try {
                    userAddress.setUser(studentEditHelper.getStudent());
                    userAddress.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ADDRESS_TYPE.class, ID.valueOf(2)));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userAddress);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to createCertificate a residential address: ", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(userAddress);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to merge a residential address: ", ex);
                }
            }

            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }

}
