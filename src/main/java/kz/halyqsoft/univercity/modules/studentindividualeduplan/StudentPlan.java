package kz.halyqsoft.univercity.modules.studentindividualeduplan;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.modules.CourseModule;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.util.LinkedList;

public class StudentPlan extends AbstractTaskView{

    private VerticalLayout mainVL;
    private Button backBtn;
    public StudentPlan(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        backBtn = new Button(getUILocaleUtil().getCaption("back"));
        backBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        backBtn.setIcon(FontAwesome.ARROW_LEFT);
        backBtn.setVisible(false);

        mainVL = new VerticalLayout();

        getContent().addComponent(backBtn);

        CourseModule courseModule = new CourseModule(this);
        mainVL.addComponent(courseModule);
        mainVL.setSizeFull();

        getContent().addComponent(mainVL);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
