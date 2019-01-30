package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.ui.TabSheet;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

/**
 * @author Omarbek
 * @created on 13.07.2018
 */
public class ScheduleView extends AbstractTaskView {

    private TabSheet mainTS;

    public ScheduleView(AbstractTask task) throws Exception {
        super(task);
    }


    @Override
    public void initView(boolean b) throws Exception {
        mainTS = new TabSheet();
        mainTS.setImmediate(true);
        String autoGenStr = getUILocaleUtil().getCaption("schedule.auto");
        String manualGenStr = getUILocaleUtil().getCaption("schedule.manual");
        String employeeStr = getUILocaleUtil().getCaption("employeeByDepartmentGW");
        String roomsStr = getUILocaleUtil().getCaption("rooms");

        ScheduleAuto scheduleAuto = new ScheduleAuto(autoGenStr);
        TabSheet.Tab autoTab = mainTS.addTab(scheduleAuto.getMainVL(), getUILocaleUtil().getCaption("schedule.auto"));
        scheduleAuto.initView(true);

        ScheduleManual scheduleManual = new ScheduleManual(manualGenStr);
        TabSheet.Tab manualTab = mainTS.addTab(scheduleManual.getMainVL(), getUILocaleUtil().getCaption("schedule.manual"));
        scheduleManual.initView(true);

        ScheduleEmployee scheduleEmployee = new ScheduleEmployee(manualGenStr);
        TabSheet.Tab emplTab = mainTS.addTab(scheduleEmployee.getMainVL(), getUILocaleUtil().getCaption("employeeByDepartmentGW"));
        scheduleEmployee.initView(true);

        ScheduleRoom scheduleRoom = new ScheduleRoom(roomsStr);
        TabSheet.Tab roomTab = mainTS.addTab(scheduleRoom.getMainVL(), getUILocaleUtil().getCaption("rooms"));
        scheduleRoom.initView(true);

        getContent().addComponent(mainTS);


    }
}
