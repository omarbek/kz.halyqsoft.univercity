package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.COMPLAINT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.Date;
import java.util.Locale;

/**
 * @author Dinassil Omarbek
 * @created 26.05.2017.
 */
@SuppressWarnings("serial")
public class ComplaintsEdit extends AbstractFormWidgetView {

    private final static int MAX_SHORT_DESCRIPTION = 50;

    private final static int MAX_DESCRIPTION = 2048;

    private final static String VALIDATOR_MESSAGE_TEMPLATE = getUILocaleUtil().getMessage("limit.text.field");

    private final USERS currentUser = CommonUtils.getCurrentUser();

    private TextField shortDescription;

    private RichTextArea description;

    ComplaintsEdit() {
        setBackButtonVisible(false);
        buildComplaintsEditTab();
        buildMyComplaintsEditTab();//TODO
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
    }

    private FormLayout buildForm() {
        TextField userName = new TextField(getUILocaleUtil().getCaption("name"));
        userName.setValue(currentUser.toString());
        userName.setWidth(100.0f, Unit.PERCENTAGE);
        userName.setReadOnly(true);

        shortDescription = new TextField(getUILocaleUtil().getCaption("short.description"));
        shortDescription.setRequired(true);
        shortDescription.setWidth(100.0f, Unit.PERCENTAGE);
        String validatorMessage = String.format(VALIDATOR_MESSAGE_TEMPLATE, MAX_SHORT_DESCRIPTION);
        shortDescription.addValidator(new StringLengthValidator(validatorMessage, 0, MAX_SHORT_DESCRIPTION, false));

        description = new RichTextArea(getUILocaleUtil().getCaption("description"));
        description.setRequired(true);
        description.setWidth(100.0f, Unit.PERCENTAGE);
        validatorMessage = String.format(VALIDATOR_MESSAGE_TEMPLATE, MAX_DESCRIPTION);
        description.addValidator(new StringLengthValidator(validatorMessage, 0, MAX_DESCRIPTION, false));

        FormLayout layout = new FormLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        layout.addComponents(userName, shortDescription, description);

        return layout;
    }

    private HorizontalLayout buildButtonPanel() {
        Button saveButton = createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });

        Button cancelButton = createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                clear();
            }
        });

        HorizontalLayout buttonPanel = createButtonPanel();
        buttonPanel.addComponents(saveButton, cancelButton);
        return buttonPanel;
    }

    private void buildComplaintsEditTab() {
        VerticalLayout complaintsEditTab = new VerticalLayout();
        complaintsEditTab.setSizeFull();
        complaintsEditTab.setSpacing(true);
        complaintsEditTab.addComponent(buildForm());
        HorizontalLayout buttonPanel = buildButtonPanel();
        complaintsEditTab.addComponent(buttonPanel);
        complaintsEditTab.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        getTabSheet().addTab(complaintsEditTab, getUILocaleUtil().getCaption("complaint"));
    }

    private void buildMyComplaintsEditTab() {
        VerticalLayout myComplaintsEditTab = new VerticalLayout();
        myComplaintsEditTab.setSizeFull();
        myComplaintsEditTab.setSpacing(true);

        GridWidget myComplaintsGW = new GridWidget(COMPLAINT.class);
        myComplaintsGW.addEntityListener(this);
        myComplaintsGW.showToolbar(false);

        DBGridModel myComplaintsGM = (DBGridModel) myComplaintsGW.getWidgetModel();
        myComplaintsGM.setTitleVisible(false);
        myComplaintsGM.setMultiSelect(false);

        QueryModel myComplaintsQM = myComplaintsGM.getQueryModel();
        myComplaintsQM.addWhere("user", ECriteria.EQUAL, currentUser.getId());
        myComplaintsQM.addOrderDesc("createDate");

        myComplaintsEditTab.addComponent(myComplaintsGW);
        myComplaintsEditTab.setComponentAlignment(myComplaintsGW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(myComplaintsEditTab, getUILocaleUtil().getCaption("my.complaint"));
    }

    private void save() {
        try {
            if (!validateForEmpty()) {
                Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
            }
            shortDescription.setValidationVisible(false);
            description.setValidationVisible(false);
            shortDescription.validate();
            description.validate();
            COMPLAINT complaint = new COMPLAINT();
            complaint.setUser(currentUser);
            complaint.setShortDescription(shortDescription.getValue());
            complaint.setDescription(description.getValue());
            complaint.setCreateDate(new Date());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(complaint);
                clear();
                showSavedNotification();
            } catch (Exception e) {
                LOG.error("Ошибка при сохранении жалобы", e);
            }
        } catch (Validator.InvalidValueException exception) {
            shortDescription.setValidationVisible(true);
            description.setValidationVisible(true);
        }
    }

    private boolean validateForEmpty() {
        return !(shortDescription.getValue().isEmpty() || description.getValue().isEmpty());
    }

    private void clear() {
        shortDescription.clear();
        description.clear();
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "ComplaintsEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("complaint.and.offer");
    }
}
