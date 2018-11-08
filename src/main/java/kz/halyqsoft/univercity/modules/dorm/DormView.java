package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

/**
 * @author Dinassil Omarbek
 * @created May 24, 2018 9:14:06 AM
 */
public class DormView extends AbstractTaskView {

    public DormView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        getContent().setSizeFull();

        MenuBar menu = new MenuBar();
        menu.addItem(getUILocaleUtil().getCaption("entering"), new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                try {
                    StudentView studentView = new StudentView(3, new HorizontalLayout());
                    new DormDialog(studentView);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to open dorm dialog",e);
                }
            }
        }).setIcon(new ThemeResource("img/users.png"));

        menu.addItem(getUILocaleUtil().getCaption("dorms.and.rooms"), new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                DormBuildingEdit buildingsEdit = new DormBuildingEdit();
                new DormDialog(buildingsEdit);
            }
        }).setIcon(new ThemeResource("img/button/building.png"));
        menu.addItem(getUILocaleUtil().getCaption("complaint.and.offer"), new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                ComplaintsEdit buildingsEdit = new ComplaintsEdit();
                new DormDialog(buildingsEdit);
            }
        }).setIcon(new ThemeResource("img/button/speaker.png"));

        menu.addItem(getUILocaleUtil().getCaption("requests"), new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                AcceptRequest buildingsEdit = new AcceptRequest();
                new DormDialog(buildingsEdit);
            }
        }).setIcon(new ThemeResource("img/users.png"));

        getContent().addComponent(menu);
        getContent().setComponentAlignment(menu, Alignment.TOP_CENTER);
    }

}
