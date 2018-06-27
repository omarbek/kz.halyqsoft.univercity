package kz.halyqsoft.univercity.modules.curriculum.working.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_SCHEDULE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_SCHEDULE_SYMBOL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SchedulePanelEdit extends AbstractDialog{

    SchedulePanelEdit(List<CURRICULUM_SCHEDULE> schedule,  SchedulePanel scheduleView) throws Exception {

        Map<ID, ComboBox> comboBoxMap = new HashMap<>();

        setWidth(500, Unit.PIXELS);
        setHeight(1000, Unit.PIXELS);
        center();

        for (CURRICULUM_SCHEDULE c : schedule){

            HorizontalLayout scheduleHL=new HorizontalLayout();
            scheduleHL.setSizeFull();

            Label weekLabel=new Label();
            weekLabel.setValue(c.getWeek().toString());
            scheduleHL.addComponent(weekLabel);
            getContent().addComponent(scheduleHL);
            scheduleHL.setSpacing(true);
            weekLabel.setWidth(100, Unit.PIXELS);

            if(c.getMonth()!=null){
                Label monthLabel = new Label();
                monthLabel.setValue(c.getMonth().toString());
                scheduleHL.addComponent(monthLabel);
                getContent().addComponent(scheduleHL);
                scheduleHL.setSpacing(true);
                monthLabel.setWidth(100, Unit.PIXELS);
            }

            ComboBox scheduleCB = new ComboBox();
            QueryModel<CURRICULUM_SCHEDULE_SYMBOL> curriculumScheduleSymbolQM = new QueryModel<>(CURRICULUM_SCHEDULE_SYMBOL.class);
            BeanItemContainer<CURRICULUM_SCHEDULE_SYMBOL>  curriculumScheduleSymbolBIC = new BeanItemContainer<>(CURRICULUM_SCHEDULE_SYMBOL.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(curriculumScheduleSymbolQM));

            scheduleCB.setContainerDataSource(curriculumScheduleSymbolBIC);
            scheduleCB.setPageLength(0);
            scheduleCB.setWidth(150, Unit.PIXELS);
            scheduleCB.setNullSelectionAllowed(false);
            scheduleCB.setValue(c.getSymbol());
            scheduleHL.addComponent(scheduleCB);
            getContent().addComponent(scheduleHL);
            comboBoxMap.put(c.getId(), scheduleCB);
            scheduleHL.setComponentAlignment(scheduleCB, Alignment.TOP_RIGHT);
        }

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev)
            {
                try {
                    for (CURRICULUM_SCHEDULE entry : schedule) {
                        ComboBox comboBox = comboBoxMap.get(entry.getId());
                        if (!entry.getSymbol().equals(comboBox.getValue())) {
                            entry.setSymbol((CURRICULUM_SCHEDULE_SYMBOL) comboBox.getValue());
                             SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(entry);
                            CommonUtils.showSavedNotification();
                        }
                    }
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to save schedule symbol", e);
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                for (CURRICULUM_SCHEDULE entry : schedule) {
                    ComboBox comboBox = comboBoxMap.get(entry.getId());
                    comboBox.setValue(entry.getSymbol());
                }
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);
        Button closeButton = new Button(CommonUtils.getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    scheduleView.refresh();
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh schedule", e);
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return null;
    }

}