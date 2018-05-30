package kz.halyqsoft.univercity;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

/**
 * Author: Omarbek Dinassil
 * Created: 11.03.2018 11:21
 */
@Push
@SuppressWarnings("serial")
public class TestUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();

        QueryModel<USERS> usersQueryModel = new QueryModel<USERS>(USERS.class);
        List<USERS> users = new ArrayList<>();
        try {
            users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQueryModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (USERS user : users) {
            vl.addComponent(new Label(user.toString()));
        }
        setContent(vl);

    }
}
