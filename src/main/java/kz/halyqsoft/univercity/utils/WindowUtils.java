package kz.halyqsoft.univercity.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

/**
 * @author Omarbek
 * @created on 05.05.2018
 */
public abstract class WindowUtils extends AbstractDialog {

    @Override
    protected abstract String createTitle();

    protected abstract void refresh() throws Exception;

    protected abstract VerticalLayout getVerticalLayout();

    public WindowUtils() {
        center();
    }

    protected void init() {
        setSizeFull();//1300-500

        VerticalLayout mainVL = getVerticalLayout();
        getContent().addComponent(mainVL);
        getContent().setComponentAlignment(mainVL, Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }
}
