package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

public class StudentChangeLanguage extends AbstractDialog {
    private StudentEdit studentEdit;
    private ComboBox lessonCB;
    private STUDENT_EDUCATION studentEducation;

    StudentChangeLanguage(STUDENT_EDUCATION studentEducation,StudentEdit studentEdit) throws Exception {
        this.studentEdit = studentEdit;

        studentEdit.getViewName();

        setWidth(500, Unit.PIXELS);
        setHeight(200, Unit.PIXELS);
        center();

        HorizontalLayout scheduleHL = new HorizontalLayout();
        scheduleHL.setSizeFull();

        lessonCB = new ComboBox(getUILocaleUtil().getCaption("lessonCB"));
        QueryModel<LANGUAGE> specialityQM = new QueryModel<>(LANGUAGE.class);
        BeanItemContainer<LANGUAGE> specialityBIC = new BeanItemContainer<>(LANGUAGE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        lessonCB.setContainerDataSource(specialityBIC);
        lessonCB.setPageLength(0);
        lessonCB.setWidth(400, Unit.PIXELS);
        lessonCB.setNullSelectionAllowed(false);

        scheduleHL.addComponent(lessonCB);
        getContent().addComponent(scheduleHL);
        scheduleHL.setComponentAlignment(lessonCB, Alignment.TOP_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                try {
                        LANGUAGE language = (LANGUAGE) lessonCB.getValue();
                        studentEducation.setLanguage(language);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);

                        CommonUtils.showSavedNotification();
                        studentEdit.setImmediate(true);

                        close();
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to save speciality", e);
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                close();
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton,cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return "Graduate employment";
    }
}
