package kz.halyqsoft.univercity.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
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

    private Button closeButton;

    public WindowUtils() {
        center();
    }

    private Unit defaultUnit = Unit.PIXELS;

    public void setDefaultUnit(Unit defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public void init(Integer width, Integer height) {
        if (width != null && height != null) {
            setWidth(width, defaultUnit);
            setHeight(height, defaultUnit);
        }else{
            setSizeFull();//1300-500
        }

        VerticalLayout mainVL = getVerticalLayout();
        getContent().addComponent(mainVL);
        getContent().setComponentAlignment(mainVL, Alignment.MIDDLE_CENTER);

        closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.setImmediate(true);
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
        getContent().setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    public Button getCloseButton() {
        return closeButton;
    }
}
