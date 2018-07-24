package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.ENTRANT_SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.util.HashMap;
import java.util.Map;

public class StudentSpecialityEdit extends AbstractDialog {

    StudentSpecialityEdit(STUDENT_EDUCATION mainStudentEducation, StudentEdit specialityView) throws Exception {

        specialityView.getViewName();

        setWidth(500, Unit.PIXELS);
        setHeight(200, Unit.PIXELS);
        center();
        Map<ID, ComboBox> comboBoxMap = new HashMap<>();

        HorizontalLayout scheduleHL = new HorizontalLayout();
        scheduleHL.setSizeFull();

        ComboBox specialityCB = new ComboBox();

        QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
        specialityQM.addWhere("deleted", ECriteria.EQUAL, false);
        BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        specialityCB.setContainerDataSource(specialityBIC);
        specialityCB.setPageLength(0);
        specialityCB.setWidth(400, Unit.PIXELS);
        specialityCB.setNullSelectionAllowed(false);
        specialityCB.setValue(mainStudentEducation.getSpeciality().getSpecName());
        scheduleHL.addComponent(specialityCB);
        getContent().addComponent(scheduleHL);
        comboBoxMap.put(mainStudentEducation.getId(), specialityCB);
        scheduleHL.setComponentAlignment(specialityCB, Alignment.TOP_CENTER);


        Button saveButton = CommonUtils.createSaveButton();
        ComboBox comboBox = comboBoxMap.get(mainStudentEducation.getId());
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                try {
                    if (!mainStudentEducation.getSpeciality().getSpecName().equals(comboBox.getValue())) {

                        SPECIALITY speciality = (SPECIALITY) comboBox.getValue();
                        mainStudentEducation.setFaculty((speciality).getDepartment().getParent());
                        mainStudentEducation.setChair((speciality).getDepartment());
                        mainStudentEducation.setSpeciality(speciality);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mainStudentEducation);

                        QueryModel<ENTRANT_SPECIALITY> specialityQM=new QueryModel<>(ENTRANT_SPECIALITY.class);
                        specialityQM.addWhere("student",ECriteria.EQUAL,mainStudentEducation.getStudent().getId());
                        ENTRANT_SPECIALITY entrantSpeciality=SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookupSingle(specialityQM);
                        entrantSpeciality.setSpeciality(speciality);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(entrantSpeciality);

                        CommonUtils.showSavedNotification();
                        specialityView.setImmediate(true);
                        //specialityView.getFormLayout().setImmediate(true);
                        close();
                    }
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to save speciality", e);
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                ComboBox comboBox = comboBoxMap.get(mainStudentEducation.getId());
                comboBox.setValue(mainStudentEducation.getSpeciality().getSpecName());
                close();
            }

        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);
        Button closeButton = new Button(CommonUtils.getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return null;
    }
}
