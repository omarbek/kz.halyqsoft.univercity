package kz.halyqsoft.univercity;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Author: Rakymzhan A. Kenzhegul
 * Created: 11.03.2018 11:21
 */
public class TestUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();

        vl.addComponent(new Label("Test project"));

        setContent(vl);
    }
}
