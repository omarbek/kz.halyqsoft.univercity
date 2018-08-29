package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TURNSTILE_TYPE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.FieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignDialog extends AbstractDialog{
    private String title;
    private CheckBox comeInChB;
    private ComboBox turnstileCB;
    private DateField createdDF;
    public SignDialog(String title, USERS user){
        this.title = title;

        setHeight(30, Unit.PERCENTAGE);
        setWidth(40, Unit.PERCENTAGE);
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        comeInChB = new CheckBox(getUILocaleUtil().getEntityFieldLabel(USER_ARRIVAL.class, "comeIn"));
        comeInChB.setWidth(100 ,Unit.PERCENTAGE);

        createdDF = new DateField(getUILocaleUtil().getEntityFieldLabel(USER_ARRIVAL.class, "created"));
        createdDF.setDateFormat(CommonUtils.DATETIME);
        createdDF.setWidth(100 ,Unit.PERCENTAGE);
        createdDF.setInvalidAllowed(false);
        createdDF.setResolution(Resolution.SECOND);

        BeanItemContainer<TURNSTILE_TYPE> turnstileTypeBIC = null;

        try{
            turnstileTypeBIC = new BeanItemContainer<>(TURNSTILE_TYPE.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(new QueryModel<>(TURNSTILE_TYPE.class)));
        }catch (Exception e){
            e.printStackTrace();
        }

        Container container = new BeanItemContainer(TURNSTILE_TYPE.class);

        turnstileCB = new ComboBox(getUILocaleUtil().getEntityFieldLabel(USER_ARRIVAL.class,"turnstileType"));
        turnstileCB.setContainerDataSource(turnstileTypeBIC);
        turnstileCB.setWidth(100 ,Unit.PERCENTAGE);

        getContent().addComponent(createdDF);
        getContent().addComponent(comeInChB);
        getContent().addComponent(turnstileCB);

        Button saveBtn  = CommonUtils.createSaveButton();
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(turnstileCB.getValue()==null || createdDF.getValue()==null){
                    Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));
                    return;
                }

                USER_ARRIVAL userArrival = new USER_ARRIVAL();
                userArrival.setTurnstileType((TURNSTILE_TYPE) turnstileCB.getValue());
                userArrival.setCreated(createdDF.getValue());
                userArrival.setUser(user);
                userArrival.setComeIn(comeInChB.getValue());
                userArrival.setManuallySigned(true);
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userArrival);
                }catch (Exception e){
                    e.printStackTrace();
                }
                CommonUtils.showSavedNotification();
                close();
            }
        });
        getContent().addComponent(saveBtn);

        AbstractWebUI.getInstance().addWindow(this);
    }


    @Override
    protected String createTitle() {
        return title;
    }
}
