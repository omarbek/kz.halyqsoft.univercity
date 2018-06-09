package kz.halyqsoft.univercity.modules.room;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.utils.WindowUtils;

/**
 * @author Omarbek
 * @created on 05.06.2018
 */
public class RoomDialog extends WindowUtils {

    private RoomEdit roomEdit;
    private RoomView roomView;

    RoomDialog(RoomEdit roomEdit, RoomView roomView) {
        this.roomEdit = roomEdit;
        this.roomView = roomView;
        init(1300, 500);
    }

    @Override
    protected String createTitle() {
        return "RoomEdit";
    }

    @Override
    protected void refresh() throws Exception {
        roomView.refresh();
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.addComponent(roomEdit);
        return mainVL;
    }
}
