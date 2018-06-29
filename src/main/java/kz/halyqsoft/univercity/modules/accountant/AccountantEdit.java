package kz.halyqsoft.univercity.modules.accountant;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.ACCOUNTANT_PRICE;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAccountants;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class AccountantEdit extends AbstractDialog {
    private CommonFormWidget accountantPriceFW;
    private ID accountantPriceId;

    private final boolean isNew;

    AccountantEdit(VAccountants accountants, boolean isNew, AccountantView accountantView) throws Exception {
        this.isNew = isNew;
        if (accountants != null) {
            accountantPriceId = accountants.getId();
        }
        setWidth(500, Unit.PIXELS);
        setHeight(500, Unit.PIXELS);
        center();

        accountantPriceFW = createAccountantPrice();
        getContent().addComponent(accountantPriceFW);
        getContent().setComponentAlignment(accountantPriceFW, Alignment.TOP_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                if (accountantPriceFW.getWidgetModel().isModified()) {
                    accountantPriceFW.save();

                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                accountantPriceFW.cancel();
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
                try {
                    accountantView.refresh();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget createAccountantPrice() throws Exception {
        CommonFormWidget accountantFW = new CommonFormWidget(ACCOUNTANT_PRICE.class);
        accountantFW.addEntityListener(new AccountantPriceListener());
        final FormModel accountantFM = accountantFW.getWidgetModel();
        accountantFM.setReadOnly(false);
        String errorMessage = getUILocaleUtil().getCaption("title.error").concat(": ").
                concat("accounts");//TODO resource
        accountantFM.setErrorMessageTitle(errorMessage);
        accountantFM.setButtonsVisible(false);

        QueryModel<ACCOUNTANT_PRICE> accountantPriceQueryModel = new QueryModel<>(ACCOUNTANT_PRICE.class);
        List<ACCOUNTANT_PRICE> accountantPrices = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(accountantPriceQueryModel);
        List<ID> ids = new ArrayList<>();
        for (ACCOUNTANT_PRICE accountantPrice : accountantPrices) {
            ids.add(accountantPrice.getId());
        }

//        QueryModel diplomaQM = ((FKFieldModel) accountantFM.getFieldModel("diploma")).getQueryModel();
//        FromItem userFI = diplomaQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "id");
//        diplomaQM.addWhereAnd(userFI, "deleted", Boolean.FALSE);
//        diplomaQM.addWhereNotInAnd("id", ids);

        if (isNew) {
            ACCOUNTANT_PRICE accountantPrice = (ACCOUNTANT_PRICE) accountantFM.createNew();

        } else {
            try {
                ACCOUNTANT_PRICE accountantPrice = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(ACCOUNTANT_PRICE.class, accountantPriceId);
                if (accountantPrice != null) {
                    accountantFM.loadEntity(accountantPrice.getId());
                }
            } catch (NoResultException ex) {
                ACCOUNTANT_PRICE accountantPrice = (ACCOUNTANT_PRICE) accountantFM.
                        createNew();
                accountantPrice.setPrice(15000);
                accountantPrice.setDeleted(false);
            }
        }
        return accountantFW;
    }

    private class AccountantPriceListener implements EntityListener {

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            ACCOUNTANT_PRICE accountantPrice = (ACCOUNTANT_PRICE) entity;
            if (isNew) {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(accountantPrice);
                    accountantPriceFW.getWidgetModel().loadEntity(accountantPrice.getId());
                    accountantPriceFW.refresh();
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create account", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(accountantPrice);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge account", ex);
                }
            }

            return false;
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

    @Override
    protected String createTitle() {
        return "Accountant";
    }
}
