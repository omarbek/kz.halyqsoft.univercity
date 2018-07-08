package kz.halyqsoft.univercity.modules.workflow;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.math.BigInteger;

public class WorkflowCommonUtils {

    public static BigInteger getCurrentUserId() {
        return getCurrentUser().getId().getId();
    }

    public static USERS getCurrentUser(){
        if (AbstractWebUI.getInstance() instanceof AbstractSecureWebUI) {
            QueryModel<USERS> qm = new QueryModel<>(USERS.class);
            qm.addWhere("login" , ECriteria.EQUAL ,AbstractSecureWebUI.getInstance().getUsername());
            USERS u = null;
            try {
                u = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                return u;
            }catch (Exception e)
            {
                Message.showError(e.getMessage());
                e.printStackTrace();
            };
        }
        return null;
    }

}
