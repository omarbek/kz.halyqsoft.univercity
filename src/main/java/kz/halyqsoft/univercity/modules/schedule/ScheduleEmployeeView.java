package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VPreviousExperience;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_TEACHER_LOAD_ASSIGN_DETAIL;
import kz.halyqsoft.univercity.modules.employee.workhour.WorkHourWidget;
import kz.halyqsoft.univercity.modules.group.tab.AutoCreationTab;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.TimeUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetEvent;
import org.r3a.common.vaadin.widget.photo.PhotoWidgetListener;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;
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
public class ScheduleEmployeeView extends AbstractFormWidgetView{

    private final VerticalLayout mainVL;
    private TabSheet mainTS;
    private ScheduleEmployee employeeView;
    private final AbstractFormWidget baseDataFW;
    private GridWidget teacherGW;
    private EMPLOYEE employee;
    private boolean userPhotoChanged;
    private static WorkHourWidget whw;

    ScheduleEmployeeView(final FormModel baseDataFM, VerticalLayout mainVL, ScheduleEmployee employeeView)
            throws Exception {
        super();


        this.mainVL = mainVL;
        this.employeeView = employeeView;

        baseDataFM.setButtonsVisible(false);


        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);


        boolean readOnly = baseDataFW.getWidgetModel().isReadOnly();

        createWorkDayTab(readOnly);

    }

    private void createWorkDayTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        /* Work days */
        ID employeeId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            employeeId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        QueryModel<EMPLOYEE_WORK_HOUR> ewhQM = new QueryModel<>(EMPLOYEE_WORK_HOUR.class);
        ewhQM.addWhere("employee", ECriteria.EQUAL, employeeId);
        ewhQM.addWhere("dayHour", ECriteria.LESS_EQUAL, ID.valueOf(14));
        List<EMPLOYEE_WORK_HOUR> tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ewhQM);
        List<EMPLOYEE_WORK_HOUR> ewhList = new ArrayList<>();
        for (EMPLOYEE_WORK_HOUR ewh : tempList) {
            EMPLOYEE_WORK_HOUR ewh1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE_WORK_HOUR.class, ewh.getId());
            ewhList.add(ewh1);
        }
        whw = new WorkHourWidget(ewhList, readOnly, baseDataFW.getWidgetModel());
        whw.setCaption(getUILocaleUtil().getCaption("work.days.setting"));
        whw.setLegend1Resource("working");
        whw.setLegend2Resource("not.working");
        content.addComponent(whw);
        content.setComponentAlignment(whw, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (baseDataFW.getWidgetModel().isCreateNew()) {
                        Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                        return;
                    }
                    List<? extends AbstractWorkHourEntity> awheList = whw.getWorkHourList();
                    boolean saved = false;
                    for (AbstractWorkHourEntity awhe : awheList) {
                        if (awhe.isChanged()) {
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(awhe);
                                saved = true;
                                awhe.setChanged(false);
                            } catch (Exception ex) {
                                CommonUtils.showMessageAndWriteLog("Unable to change work day hour", ex);
                                break;
                            }
                        }
                    }

                    if (saved) {
                        showSavedNotification();
                    }
                }
            });
            buttonPanel.addComponent(save);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    whw.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("days.and.time.of.class"));
    }

    @Override
    protected AbstractCommonView getParentView() {

        try {
            employeeView.getTeacherGW().refresh();
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh teacher's grid widget", e);
        }

        mainVL.removeComponent(this);
        mainVL.addComponent(employeeView.getTeacherGW());

        return null;
    }

    @Override
    public String getViewName() {
        return null;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return null;
    }

}

