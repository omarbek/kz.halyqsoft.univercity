package kz.halyqsoft.univercity.modules;

import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.admin.UsersView;
import org.r3a.common.vaadin.widget.form.FormModel;

/**
 * @author Omarbek
 * Created 21.10.2014 10:25:37
 */
public class UnivercityUsersView extends UsersView {

    public UnivercityUsersView(AbstractTask task) throws Exception {//
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
        FormModel fm = getUsersForm().getWidgetModel();
        fm.getFieldModel("task").setInEdit(false);
        fm.getFieldModel("task").setInView(false);
    }
}
