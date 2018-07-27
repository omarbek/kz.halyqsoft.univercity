package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;

/**
 * @author Omarbek
 * @created on 26.07.2018
 */
public class DormDialog extends WindowUtils {

    private AbstractFormWidgetView view;

    DormDialog(AbstractFormWidgetView view) {
        this.view = view;
        init(null, null);
    }

    @Override
    protected String createTitle() {
        return "DormEdit";
    }

    @Override
    protected void refresh() throws Exception {
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.addComponent(view);
        return mainVL;
    }
}
