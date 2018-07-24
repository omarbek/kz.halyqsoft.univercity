package kz.halyqsoft.univercity.modules.status;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.utils.WindowUtils;

/**
 * @author Omarbek
 * @created on 13.07.2018
 */
public class StatusDialog extends WindowUtils {

    private StatusEdit statusEdit;
    private StatusView statusView;

    StatusDialog(StatusEdit statusEdit, StatusView statusView) {
        this.statusEdit = statusEdit;
        this.statusView = statusView;
        init(null,null);
    }

    @Override
    protected String createTitle() {
        return "StatusEdit";
    }

    @Override
    protected void refresh() throws Exception {
        statusView.refresh();
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.addComponent(statusEdit);
        return mainVL;
    }
}
