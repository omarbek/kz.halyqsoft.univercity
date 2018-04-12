package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.utility.PasswordGenerator;

import java.util.List;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Aug 8, 2016 3:39:24 PM
 */
@SuppressWarnings("serial")
public class ParentLoginForm extends FormLayout {

    private final STUDENT student;
    private final TextField loginTF;
    private final TextField passwdTF;

    public ParentLoginForm(STUDENT student) {
        this.student = student;
        setCaption(CommonUtils.getUILocaleUtil().getCaption("student.parent.login"));

        loginTF = new TextField();
        loginTF.setCaption(CommonUtils.getUILocaleUtil().getCaption("username"));
        loginTF.setImmediate(true);
        loginTF.setEnabled(false);
        loginTF.setNullSettingAllowed(true);
        loginTF.setNullRepresentation("");
        loginTF.setMaxLength(32);
        loginTF.setValue(student.getParentLogin());

        passwdTF = new TextField();
        passwdTF.setCaption(CommonUtils.getUILocaleUtil().getCaption("password"));
        passwdTF.setImmediate(true);
        passwdTF.setNullSettingAllowed(true);
        passwdTF.setNullRepresentation("");
        passwdTF.setMaxLength(10);
        passwdTF.setValue(student.getParentPasswd());

        addComponent(loginTF);
        addComponent(passwdTF);

        Button generate = new Button();
        generate.setCaption(CommonUtils.getUILocaleUtil().getCaption("generate.login.and.passwd"));
        generate.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                generate();
            }
        });
        addComponent(generate);
    }

    private void generate() {
        try {
            if (student.getParentLogin() == null) {
                String login = null;
                boolean found = false;
                while (!found) {
                    login = 'P' + PasswordGenerator.verySimple(8);
                    QueryModel<STUDENT> qm = new QueryModel<>(STUDENT.class);
                    qm.addWhere("parentLogin", ECriteria.EQUAL, login);
                    List<STUDENT> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
                    found = list.isEmpty();
                }

                if (login != null) {
                    loginTF.setValue(login);
                } else {
                    throw new Exception("Unable to generate parent login");
                }
                passwdTF.setValue(PasswordGenerator.verySimple(8));
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to generate student parent login and password", ex);
        }
    }

    public void save() throws Exception {
        String login = loginTF.getValue();
        String passwd = passwdTF.getValue();
        if (login != null && passwd != null && !passwd.equals(student.getParentPasswd())) {
            student.setParentLogin(login);
            student.setParentPasswd(passwd);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student);
        }
    }

    public void cancel() {
        loginTF.setValue(student.getParentLogin());
        passwdTF.setValue(student.getParentPasswd());
    }
}
