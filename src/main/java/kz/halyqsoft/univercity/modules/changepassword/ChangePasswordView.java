package kz.halyqsoft.univercity.modules.changepassword;

import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.shiro.SecurityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonLogFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.beans.AbstractUser;
import org.r3a.common.utility.Hash;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.Locale;

/**
 * Author Omarbek Dinassil
 * Created 04.05.2018 10:28:02
 */
public final class ChangePasswordView extends AbstractTaskView {

    private final PasswordField oldPF = new PasswordField();
    private final PasswordField newPF = new PasswordField();
    private final PasswordField confirmNewPF = new PasswordField();
    private final Button changeButton = new Button();

    public ChangePasswordView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeUndefined();
        vl.setMargin(true);

        FormLayout fl = new FormLayout();
        fl.setStyleName("v-formlayout-borderless");

        oldPF.setWidth(200, Unit.PIXELS);
        oldPF.setRequired(true);
        oldPF.setImmediate(true);
        oldPF.focus();
        fl.addComponent(oldPF);

        newPF.setWidth(200, Unit.PIXELS);
        newPF.setRequired(true);
        newPF.setImmediate(true);
        fl.addComponent(newPF);

        confirmNewPF.setWidth(200, Unit.PIXELS);
        confirmNewPF.setRequired(true);
        confirmNewPF.setImmediate(true);
        fl.addComponent(confirmNewPF);

        vl.addComponent(fl);
        vl.setComponentAlignment(fl, Alignment.TOP_CENTER);

        changeButton.setWidth(200, Unit.PIXELS);
        changeButton.addClickListener(new ChangeButtonListener());
        vl.addComponent(changeButton);
        vl.setComponentAlignment(changeButton, Alignment.BOTTOM_CENTER);

        getContent().addComponent(vl);
        getContent().setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
        getContent().setExpandRatio(vl, 1);
    }

    private void logChangePassword(AbstractUser user) {
        try {
            SessionFacadeFactory.getSessionFacade(CommonLogFacadeBean.class).logChangePassword(user, getTask(), user);
        } catch (Exception ex) {
            LOG.error("Unable to write to log: ", ex);
        }
    }

    @Override
    public void localeChanged(Locale newLocale) {
        super.localeChanged(newLocale);
        oldPF.setCaption(getUILocaleUtil().getCaption("oldpassword"));
        newPF.setCaption(getUILocaleUtil().getCaption("newpassword"));
        confirmNewPF.setCaption(getUILocaleUtil().getCaption("newpassword.confirm"));
        changeButton.setCaption(getUILocaleUtil().getCaption("change.password"));
    }

    private class ChangeButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            String oldPwd = oldPF.getValue();
            String newPwd1 = newPF.getValue();
            String newPwd2 = confirmNewPF.getValue();

            if (oldPwd.isEmpty()) {
                Message.showError(getUILocaleUtil().getMessage("error.enteroldpassword"));
                return;
            }

            if (newPwd1.isEmpty()) {
                Message.showError(getUILocaleUtil().getMessage("error.enternewpassword"));
                return;
            }

            if (newPwd2.isEmpty()) {
                Message.showError(getUILocaleUtil().getMessage("error.enternewpasswordconfirm"));
                return;
            }

            if (newPwd1.length() < 6) {
                Message.showError(getUILocaleUtil().getMessage("error.newpasswordtooshort"));
                return;
            }

            if (!newPwd1.equals(newPwd2)) {
                Message.showError(getUILocaleUtil().getMessage("error.incorrectnewpassword"));
                return;
            }

            AbstractUser currentUser = (AbstractUser) SecurityUtils.getSubject().getSession().getAttribute("user");
            try {
                if (!Hash.sha256(oldPwd).equals(currentUser.getPasswd())) {
                    Message.showError(getUILocaleUtil().getMessage("error.incorrectoldpassword"));
                }
            } catch (Exception ex) {
                LOG.error("Unable to change password: ", ex);
                Message.showError(ex.getMessage());
            }

            Message.showConfirm(getUILocaleUtil().getMessage("confirm.changepassword"), new ChangeListener());
        }
    }

    private class ChangeListener extends AbstractYesButtonListener {

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            String newPasswordValue = newPF.getValue();
            try {
                USERS currentUser = CommonUtils.getCurrentUser();
                if (currentUser != null) {
                    currentUser.setPasswd(newPasswordValue);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(currentUser);
                    logChangePassword(currentUser);
                    Message.showInfo(getUILocaleUtil().getMessage("info.changepasswordsuccess"), new LogOutListener());
                }
            } catch (Exception ex) {
                LOG.error("Unable to change password: ", ex);
                Message.showError(ex.getMessage());
            }
        }
    }

    private class LogOutListener extends AbstractYesButtonListener {

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            AbstractSecureWebUI.getInstance().logout();
        }
    }
}
