package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.10.2018
 */
public class MainDialog extends WindowUtils {

    public MainDialog() {
        init(null, null);
    }

    @Override
    protected String createTitle() {
        return null;//TODO
    }

    @Override
    protected void refresh() throws Exception {
        //TODO
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();

        GridWidget usersGW = new GridWidget(USER_ARRIVAL.class);
        usersGW.showToolbar(false);

        DBGridModel usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRefreshType(ERefreshType.MANUAL);

        refreshGridWidget(usersGW);
        mainVL.addComponent(usersGW);

        return mainVL;
    }

    private void refreshGridWidget(GridWidget usersGW) {
        try {
            String sql = "SELECT arriv.* " +
                    "FROM user_arrival arriv INNER JOIN v_employee empl ON empl.id = arriv.user_id " +
                    "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                    "      AND arriv.created = (SELECT min(max_arriv.created) " +
                    "                           FROM user_arrival max_arriv " +
                    "                           WHERE max_arriv.user_id = arriv.user_id " +
                    "                                 AND date_trunc('day', max_arriv.created) = date_trunc('day', now()) " +
                    "                                 AND come_in = TRUE) " +
                    "      AND come_in = TRUE AND arriv.created :: TIME > '08:40:00' " +
                    "ORDER BY created DESC;";
            List<USER_ARRIVAL> userArrivals = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql,
                    new HashMap<>(), USER_ARRIVAL.class);
            ((DBGridModel) usersGW.getWidgetModel()).setEntities(userArrivals);
            usersGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO
        }
    }
}
