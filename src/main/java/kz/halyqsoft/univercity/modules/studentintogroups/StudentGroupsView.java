package kz.halyqsoft.univercity.modules.studentintogroups;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_GROUP;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.grid.GridWidget;

public class StudentGroupsView extends AbstractTaskView implements EntityListener{
    private GridWidget studentGroupsGW;
    public StudentGroupsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        studentGroupsGW = new GridWidget(STUDENT_GROUP.class);
        studentGroupsGW.setImmediate(true);
        studentGroupsGW.setSizeFull();
        studentGroupsGW.showToolbar(true);
        studentGroupsGW.addEntityListener(this);

        Button button = new Button("Generate");
        getContent().addComponent(button);
        getContent().setComponentAlignment(button , Alignment.MIDDLE_CENTER);

        getContent().addComponent(studentGroupsGW);
    }
}
