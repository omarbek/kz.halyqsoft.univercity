package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.modules.workflow.WorkflowViewContent;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;

/**
 * @author Assylkhan
 * on 06.12.2018
 * @project kz.halyqsoft.univercity
 */
public class MainView extends BaseView{
    public MainView(String title) {
        super(title);
        WorkflowViewContent content = new WorkflowViewContent();
        getContent().addComponent(content.getMainVL());
    }
}
