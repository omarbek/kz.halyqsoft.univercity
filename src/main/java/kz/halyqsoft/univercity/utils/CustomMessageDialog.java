package kz.halyqsoft.univercity.utils;

import com.vaadin.ui.*;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.util.ArrayList;

/**
 * @author Assylkhan
 * on 23.11.2018
 * @project kz.halyqsoft.univercity
 */
public class CustomMessageDialog extends AbstractDialog{

    private ArrayList<Button> buttons = new ArrayList<>();
    private String title;
    private String message;
    private VerticalLayout mainVL;
    private HorizontalLayout buttonsHL;
    public CustomMessageDialog(String title,String message){
        this.title = title;
        this.message = message;
        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setModal(true);
        setSizeUndefined();
        setWidth("30%");

    }

    @Override
    protected String createTitle() {
        return title;
    }

    public void init(){
        Label messageLbl = new Label(message);
        buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);
        buttonsHL.setSizeUndefined();
        for(Button button : buttons){
            buttonsHL.addComponent(button);
        }
        mainVL.addComponent(messageLbl);
        mainVL.addComponent(buttonsHL);
        getContent().addComponent(mainVL);
        AbstractWebUI.getInstance().addWindow(this);
    }


    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<Button> buttons) {
        this.buttons = buttons;
    }
}
