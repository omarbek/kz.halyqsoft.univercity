package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_RELATIVE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.RELATIVE_TYPE;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.AddressUtils;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import kz.halyqsoft.univercity.utils.changelisteners.CityChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.CountryChangeListener;
import kz.halyqsoft.univercity.utils.changelisteners.RegionChangeListener;
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

import java.util.List;

/**
 * @author Dmitry Dobrin.
 * @created 17.05.2017.
 */
@SuppressWarnings("serial")
public class ParentsTab extends VerticalLayout {

    private final StudentEdit.StudentEditHelper studentEditHelper;
    private final boolean readOnly;

    private CommonFormWidget fatherFW;
    private CommonFormWidget motherFW;

    private static final int FATHER = 1;
    private static final int MOTHER = 2;

    public ParentsTab(StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) throws Exception {
        this.studentEditHelper = studentEditHelper;
        this.readOnly = readOnly;

        setSpacing(true);
        setSizeFull();

        HorizontalLayout formsHL = new HorizontalLayout();
        formsHL.setSpacing(true);

        fatherFW = createRelativeFormWidget("parents.data.father", FATHER);
        formsHL.addComponent(fatherFW);

        motherFW = createRelativeFormWidget("parents.data.mother", MOTHER);
        formsHL.addComponent(motherFW);

        final ParentLoginForm parentLoginForm = new ParentLoginForm(studentEditHelper.getStudent());
//        formsHL.addComponent(parentLoginForm);//TODO restore maybe

        addComponent(formsHL);
        setComponentAlignment(formsHL, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            Button save = studentEditHelper.createSaveButton();
            save.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (fatherFW.getWidgetModel().isModified()) {
                        fatherFW.save();
                    }

                    if (motherFW.getWidgetModel().isModified()) {
                        motherFW.save();
                    }

                    try {
                        parentLoginForm.save();
                        ParentsTab.this.studentEditHelper.showSavedNotification();
                    } catch (Exception ex) {
                        ErrorUtils.LOG.error("Unable to save student parent login: ", ex);
                        Message.showError(ex.toString());
                    }
                }
            });

            Button cancel = studentEditHelper.createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    fatherFW.cancel();
                    motherFW.cancel();
                    parentLoginForm.cancel();
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

    private CommonFormWidget createRelativeFormWidget(String caption,
                                                      int parent_number) throws Exception {
        CommonFormWidget parentFW = new CommonFormWidget(STUDENT_RELATIVE.class);
        parentFW.addEntityListener(new RelativeListener(parent_number));
        final FormModel parentFM = parentFW.getWidgetModel();
        parentFM.setTitleResource(caption);
        String errorMessage = studentEditHelper.getUiLocaleUtil().getCaption("title.error") + ": "
                + studentEditHelper.getUiLocaleUtil().getCaption(caption);
        parentFM.setErrorMessageTitle(errorMessage);
        parentFM.setButtonsVisible(false);

        FKFieldModel countryFM = (FKFieldModel) parentFM.getFieldModel("country");
        QueryModel countryQM = countryFM.getQueryModel();
        countryQM.addWhereNull("parent");
        countryQM.addOrder("countryName");

        FKFieldModel regionFM = (FKFieldModel) parentFM.getFieldModel("region");
        QueryModel regionQM = regionFM.getQueryModel();
        regionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        regionQM.addOrder("countryName");

        FKFieldModel cityFM = (FKFieldModel) parentFM.getFieldModel("city");
        QueryModel cityQM = cityFM.getQueryModel();
        cityQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        cityQM.addOrder("countryName");

        FKFieldModel villageFM = (FKFieldModel) parentFM.getFieldModel("village");
        QueryModel villageQM = villageFM.getQueryModel();
        villageQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        villageQM.addOrder("countryName");

        COUNTRY region = null;
        COUNTRY city = null;
        COUNTRY village = null;
        if (studentEditHelper.isStudentNew()) {
            parentFM.createNew();
        } else {
            ID studentId = studentEditHelper.getStudent().getId();

            AddressUtils addressUtils = new AddressUtils(parent_number, parentFM, readOnly, studentId);
            region = addressUtils.getRegion();
            city = addressUtils.getCity();
            village = addressUtils.getVillage();
        }
        parentFM.setReadOnly(readOnly);

        countryFM.getListeners().add(new CountryChangeListener(region, regionFM));
        regionFM.getListeners().add(new RegionChangeListener(city, cityFM));
        cityFM.getListeners().add(new CityChangeListener(village, villageFM));

        return parentFW;
    }

    private class RelativeListener implements EntityListener {

        private int parentNumber;

        RelativeListener(int parentNumber) {
            this.parentNumber = parentNumber;
        }

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
            STUDENT_RELATIVE studentRelative = (STUDENT_RELATIVE) entity;
            if (isNew) {
                try {
                    studentRelative.setStudent(studentEditHelper.getStudent());
                    studentRelative.setRelativeType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(RELATIVE_TYPE.class, ID.valueOf(parentNumber)));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentRelative);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to createCertificate a fathers data: ", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentRelative);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to merge a fathers address: ", ex);
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
