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

    private VerticalLayout mainVL;

    public MainDialog(VerticalLayout mainVL) {
        this.mainVL=mainVL;
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
        return mainVL;
    }
}
