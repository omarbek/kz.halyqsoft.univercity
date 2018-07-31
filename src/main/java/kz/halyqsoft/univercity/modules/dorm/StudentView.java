package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.modules.student.StudentOrApplicantView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.util.List;

/**
 * @author Dinassil Omarbek
 * @created May 24, 2017 3:26:17 PM
 */
public class StudentView extends StudentOrApplicantView {

    StudentView(int categoryType, HorizontalLayout buttonsHL) throws Exception {
        super(categoryType, new HorizontalLayout(),true);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(studentGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                List<Entity> selectedList = ev.getEntities();
                if (!selectedList.isEmpty()) {
                    if (ev.getSource().equals(studentGW)) {
                        FormModel fm = new FormModel(STUDENT.class);
                        fm.setReadOnly(false);
                        fm.setTitleVisible(false);
                        try {
                            fm.loadEntity(selectedList.get(0).getId());
                            new DormDialog(new StudentEdit(fm));
                        } catch (Exception ex) {
                            CommonUtils.showMessageAndWriteLog("Unable to open studentEdit", ex);
                        }
                    }
                }
            }
        }
    }
}
