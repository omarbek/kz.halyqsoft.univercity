package kz.halyqsoft.univercity.modules.studentattendancelog;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_ATTENDANCE_LOG;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.List;

/**
 * @author Omarbek
 * @created on 06.09.2018
 */
public class StudentAttendanceLogView extends AbstractTaskView {

    private GridWidget studentAttendanceLogGW;

    public StudentAttendanceLogView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        studentAttendanceLogGW = new GridWidget(STUDENT_ATTENDANCE_LOG.class);
        studentAttendanceLogGW.showToolbar(false);

        refresh();

        getContent().addComponent(studentAttendanceLogGW);
    }

    private void refresh() throws Exception {
        QueryModel<STUDENT_ATTENDANCE_LOG> studentAttendanceLogQM = new QueryModel<>(STUDENT_ATTENDANCE_LOG.class);
        studentAttendanceLogQM.addOrderDesc("created");
        List<STUDENT_ATTENDANCE_LOG> studentAttendanceLogs = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(studentAttendanceLogQM);
        ((DBGridModel) studentAttendanceLogGW.getWidgetModel()).setEntities(studentAttendanceLogs);
    }
}
