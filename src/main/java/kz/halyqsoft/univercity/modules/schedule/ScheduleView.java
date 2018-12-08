package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.modules.group.tab.AutoCreationTab;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.TimeUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Calendar;

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

        ScheduleAuto scheduleAuto =  new ScheduleAuto(autoGenStr);
        TabSheet.Tab autoTab = mainTS.addTab(scheduleAuto.getMainVL() , getUILocaleUtil().getCaption("schedule.auto") );
        scheduleAuto.initView(true);

        ScheduleManual scheduleManual   = new ScheduleManual(manualGenStr);
        TabSheet.Tab manualTab = mainTS.addTab(scheduleManual.getMainVL() , getUILocaleUtil().getCaption("schedule.manual") );
        scheduleManual.initView(true);

        ScheduleEmployee scheduleEmployee = new ScheduleEmployee(manualGenStr);
        TabSheet.Tab emplTab = mainTS.addTab(scheduleEmployee.getMainVL() , getUILocaleUtil().getCaption("employeeByDepartmentGW") );
        scheduleEmployee.initView(true);

        getContent().addComponent(mainTS);


    }
}
