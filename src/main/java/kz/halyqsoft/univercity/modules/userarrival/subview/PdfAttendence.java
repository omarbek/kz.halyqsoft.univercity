package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MONTH;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;

import java.util.*;
import java.util.Calendar;

public class PdfAttendence implements EntityListener {

    private VerticalLayout mainVL;
    private ComboBox departmentCB;
    private Button printBtn;

    public PdfAttendence() {
        departmentCB = new ComboBox();
        departmentCB.setCaption(CommonUtils.getUILocaleUtil().getCaption("month"));
        departmentCB.setNullSelectionAllowed(true);
        departmentCB.setTextInputAllowed(true);
        departmentCB.setFilteringMode(FilteringMode.CONTAINS);
        departmentCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<MONTH> departmentQM = new QueryModel<>(MONTH.class);
        departmentQM.addOrder("id");
        BeanItemContainer<MONTH> departmentBIC = null;
        try {
            departmentBIC = new BeanItemContainer<>(MONTH.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQM));
        } catch (Exception e) {
            e.printStackTrace();
        }
        departmentCB.setContainerDataSource(departmentBIC);

        printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<List<String>> tableBody = new ArrayList<>();
                String [] headers = new String[100];

                Calendar cal = Calendar.getInstance();
                int days =0; //cal.getMinimalDaysInFirstWeek(); // 28
                for(int i=cal.getMinimalDaysInFirstWeek();i<=Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);i++) {
                    days = i;
                    System.out.println(days);

                }
                for(int i=0;i<days;i++){
                    headers = new String[]{"№ п/п", " Фамилия,имя,\nотчество", "таб.номер", "Профессия должность", String.valueOf(i)};

                }

                List<String> tableHeader = new ArrayList<>(Arrays.asList(headers));
                String fileName = "document";

                tableBody.add(Collections.singletonList("wewe"));
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        HorizontalLayout buttonPanel = CommonUtils.createButtonPanel();


        buttonPanel.addComponent(departmentCB);
        buttonPanel.addComponent(printBtn);

        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();


        mainVL.addComponent(buttonPanel);
        mainVL.setComponentAlignment(buttonPanel, Alignment.TOP_CENTER);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }
}
