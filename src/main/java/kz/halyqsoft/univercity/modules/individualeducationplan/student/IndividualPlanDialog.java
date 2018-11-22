package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

import javax.persistence.NoResultException;

public class IndividualPlanDialog extends WindowUtils {

    private String userCode;

    IndividualPlanDialog(String userCode) {
        this.userCode = userCode;
        init(null, null);
    }

    @Override
    protected String createTitle() {
        return "individual plan";//TODO
    }

    @Override
    protected void refresh() throws Exception {
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();

        try {
            QueryModel<USERS> userQM = new QueryModel<>(USERS.class);
            userQM.addWhere("code", ECriteria.EQUAL, userCode);
            USERS user = null;
            try {
                user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userQM);
            } catch (NoResultException ignored) {
            }
            IndividualPlanTabs ts = new IndividualPlanTabs(user, true);

            mainVL.addComponent(ts);
            mainVL.setComponentAlignment(ts, Alignment.MIDDLE_CENTER);
            mainVL.setExpandRatio(ts, 1);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }

        return mainVL;
    }
}
