package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAbsents;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class AbsentAttendance {

    private ComboBox absentDayCB;
    private GridWidget absentsGW;
    private Button printBtn;
    private VerticalLayout tableVL;

    public AbsentAttendance(VerticalLayout tableVL){
        this.tableVL = tableVL;
    }

    public VerticalLayout getAbsentsInfo() {
        absentDayCB = new ComboBox();
        absentDayCB.setNullSelectionAllowed(true);
        absentDayCB.setTextInputAllowed(true);
        absentDayCB.setFilteringMode(FilteringMode.CONTAINS);
        absentDayCB.setWidth(300, Sizeable.Unit.PIXELS);
        for(int i = 5; i<100; i++) {
            absentDayCB.addItem(i);
        }
        absentDayCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                ((DBGridModel) absentsGW.getWidgetModel()).setEntities(getAbsentList((Integer)absentDayCB.getValue()));
            }
        });

        absentsGW = new GridWidget(VAbsents.class);
        absentsGW.setCaption(getUILocaleUtil().getCaption("absentsGW"));
        absentsGW.setImmediate(true);
        absentsGW.showToolbar(false);
        absentsGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);

        DBGridModel absentsGM = (DBGridModel) absentsGW.getWidgetModel();
        absentsGM.setEntities(getAbsentList((Integer)absentDayCB.getValue()));
        absentsGM.setTitleVisible(false);
        absentsGM.setMultiSelect(false);
        absentsGM.setRefreshType(ERefreshType.MANUAL);

        printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody= new ArrayList<>();

                String fileName = "document";

                if(tableVL.getComponentIndex(absentsGW)!=-1){
                    for(GridColumnModel gcm :absentsGM.getColumnModels()){
                        tableHeader.add(gcm.getLabel());
                    }
                    for(int i = 0 ; i < absentsGW.getAllEntities().size(); i++){
                        VAbsents vAbsents = (VAbsents) absentsGW.getAllEntities().get(i);
                        if(absentsGW.getCaption()!=null){
                            fileName = absentsGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(vAbsents.getFIO());
                        list.add(vAbsents.getFaculty());
                        list.add(vAbsents.getSpeciality());
                        list.add(vAbsents.getGroup());
                        list.add(vAbsents.getAbsentSum().toString());
                        tableBody.add(list);
                    }
                }

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody , CommonUtils.getUILocaleUtil().getCaption("print"),fileName);
            }
        });


        tableVL = new VerticalLayout();

        HorizontalLayout topHL = CommonUtils.createButtonPanel();
        topHL.addComponent(new Label(getUILocaleUtil().getCaption("absentDayCB")));
        topHL.addComponent(absentDayCB);
        topHL.addComponent(printBtn);

        tableVL.addComponent(topHL);
        tableVL.setComponentAlignment(topHL, Alignment.MIDDLE_RIGHT);
        tableVL.addComponent(absentsGW);
        return (tableVL);
    }

    public List<VAbsents> getAbsentList(Integer number) {

        List<VAbsents> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT\n" +
                "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                "  vstudent.faculty_name,\n" +
                "  vstudent.speciality_name,\n" +
                "  vstudent.group_name,\n" +
                "  date_part('days', now()::date - (max(arrive.created) )\n" +
                "  ) as                                                                           absentDay,\n" +
                "  max(arrive.created)\n" +
                "FROM user_arrival arrive\n" +
                "  INNER JOIN users u on arrive.user_id = u.id\n" +
                "  INNER JOIN v_student vstudent on u.id = vstudent.id\n" +
                "GROUP BY FIO,\n" +
                "  vstudent.faculty_name,\n" +
                "  vstudent.speciality_name,\n" +
                "  vstudent.group_name\n" +
                "HAVING date_part('days', now()::date - (max(arrive.created) )\n" +
                ") > 5";
        if(number!=null){
            sql = sql + "and  date_part('days', now()::date - (max(arrive.created) )\n" +
                    ") <= " + number;
        }
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VAbsents vAbsent = new VAbsents();
                    vAbsent.setFIO((String) oo[0]);
                    vAbsent.setFaculty((String) oo[1]);
                    vAbsent.setSpeciality((String) oo[2]);
                    vAbsent.setGroup((String) oo[3]);
                    vAbsent.setAbsentSum((Double) oo[4]);
                    vAbsent.setLastVisit((Date)oo[5]);
                    list.add(vAbsent);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
        }
        refreshAbsentList(list);
        return list;
    }

    private void refreshAbsentList(List<VAbsents> list) {
        ((DBGridModel) absentsGW.getWidgetModel()).setEntities(list);
        try {
            absentsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh department list", ex);
        }
    }
}
