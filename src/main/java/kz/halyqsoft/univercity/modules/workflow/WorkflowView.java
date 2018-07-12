package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class WorkflowView extends AbstractTaskView {

    public WorkflowView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        TabSheet tabSheet = new TabSheet();
        VerticalLayout tab1MainVL = new VerticalLayout();
        VerticalLayout tab2MainVL = new VerticalLayout();

        tab1MainVL.addComponent(new Button("Asdasdfasdfasdf="));
        tab2MainVL.addComponent(new Button("asdasd"));
        tabSheet.addTab(tab1MainVL, "Kasya1");
        tabSheet.addTab(tab2MainVL , "Kasya2");

        getContent().addComponent(tabSheet);
    }
}
