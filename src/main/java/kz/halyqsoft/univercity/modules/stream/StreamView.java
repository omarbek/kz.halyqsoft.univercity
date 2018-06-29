package kz.halyqsoft.univercity.modules.stream;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;

public class StreamView extends AbstractTaskView {

    private GridWidget ssGW;

    public StreamView(AbstractTask task) throws Exception {
        super(task);

    }

    @Override
    public void initView(boolean b) throws Exception {

        ssGW = new GridWidget(STREAM_GROUP.class);
        ssGW.setSizeFull();
        ssGW.setMultiSelect(true);
        ssGW.setImmediate(true);

        VerticalLayout vl = new VerticalLayout();
        Button button = new Button("Kasya");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Message.showInfo("Raikhan!");
            }
        });

        vl.addComponent(button);

        getContent().addComponent(vl);
        getContent().addComponent(ssGW);

    }
}
