package kz.halyqsoft.univercity.modules.userarrival.subview.reports;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.SimpleStatistics;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.*;
import java.util.Calendar;

/**
 * @author Assylkhan
 * on 20.11.2018
 * @project kz.halyqsoft.univercity
 */
public class EmployeesArrivalReport extends VerticalLayout{

    private DateField monthField;
    private ComboBox departmentCB;

    public EmployeesArrivalReport(){
        init();
    }

    public void init(){
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        monthField = new DateField();
        departmentCB = new ComboBox();
        QueryModel<DEPARTMENT> departmentQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
        departmentQM.addWhereNotNullAnd("parent" );
        departmentQM.addWhereAnd("deleted",ECriteria.EQUAL,false );
        departmentQM.addWhereAnd("forEmployees",ECriteria.EQUAL,true );
        List<DEPARTMENT> departmentList = new ArrayList<>();
        try{
            departmentList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQM));
        }catch (Exception e){
            e.printStackTrace();
        }
        BeanItemContainer<DEPARTMENT> departmentBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, departmentList);
        departmentCB.setContainerDataSource(departmentBIC);
        setSpacing(true);
        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSpacing(true);
        mainHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainHL.addComponent(new Label(CommonUtils.getUILocaleUtil().getCaption("month")));
        mainHL.addComponent(monthField);
        mainHL.addComponent(new Label(CommonUtils.getUILocaleUtil().getEntityLabel(DEPARTMENT.class)));
        mainHL.addComponent(departmentCB);
        Button exportBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        exportBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(monthField.getValue()==null || departmentCB.getValue()==null){
                    Message.showError(CommonUtils.getUILocaleUtil().getMessage("choose.field"));
                    return;
                }
                Date dateValue = monthField.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateValue);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date startingDate = calendar.getTime();
                int maxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH,maxDate);
                Date finalDate = calendar.getTime();
                String sql ="SELECT *\n" +
                        "FROM crosstab( 'SELECT fio , day , cause from (SELECT DISTINCT\n" +
                        "                 ve.id,\n" +
                        "                 concat(ve.first_name, '' '', ve.last_name, '' '', ve.middle_name, '' '' ,ve.post_name)::TEXT fio,\n" +
                        "                 date_part( ''day'' , new_table.day::DATE) as day,\n" +
                        "\n" +
                        "                 CASE\n" +
                        "                 WHEN ua.user_id ISNULL\n" +
                        "                   THEN\n" +
                        "                     CASE\n" +
                        "                     WHEN EXTRACT(ISODOW FROM new_table.day) IN (6, 7)\n" +
                        "                       THEN ''выходной''\n" +
                        "                     ELSE\n" +
                        "                       CASE WHEN ve.employee_status_id NOT IN (1, 2, 5)\n" +
                        "                         THEN ve.employee_status_name\n" +
                        "                       ELSE\n" +
                        "                         CASE WHEN ewh.week_day_id NOTNULL\n" +
                        "                           THEN ''не пришел''\n" +
                        "                         ELSE ''не работает''\n" +
                        "                         END\n" +
                        "                       END\n" +
                        "                     END\n" +
                        "                 ELSE\n" +
                        "                   CASE\n" +
                        "                   WHEN maxua.created > minua.created\n" +
                        "                     THEN\n" +
                        "                       round((EXTRACT(EPOCH FROM maxua.created ) - EXTRACT(EPOCH FROM minua.created))/3600)::text\n" +
                        "                   ELSE ''0''\n" +
                        "                   END\n" +
                        "                 END                                                        cause\n" +
                        "\n" +
                        "\n" +
                        "               FROM\n" +
                        "                 v_employee\n" +
                        "                 ve\n" +
                        "                 INNER JOIN (SELECT\n" +
                        "                               id,\n" +
                        "                               min(post_id) post_id\n" +
                        "                             FROM v_employee\n" +
                        "                             GROUP BY id) AS emp_post ON ve.id = emp_post.id AND emp_post.post_id = ve.post_id\n" +
                        "                 CROSS JOIN (\n" +
                        "                              SELECT date_trunc(''day'', dd) :: DATE AS day\n" +
                        "                              FROM generate_series\n" +
                        "                                   (''"+CommonUtils.getFormattedDate(startingDate)+"'' :: TIMESTAMP\n" +
                        "                                  , ''"+CommonUtils.getFormattedDate(finalDate)+"'' :: TIMESTAMP\n" +
                        "                                  , ''1 day'' :: INTERVAL) dd\n" +
                        "                            ) AS new_table\n" +
                        "                 LEFT JOIN (\n" +
                        "                             SELECT\n" +
                        "                               id,\n" +
                        "                               user_id,\n" +
                        "                               max(date_trunc(''day'', created)) created\n" +
                        "                             FROM user_arrival\n" +
                        "                             GROUP BY user_id, id\n" +
                        "                           ) AS ua ON ve.id = ua.user_id AND new_table.day = ua.created\n" +
                        "                 LEFT JOIN (\n" +
                        "                             SELECT\n" +
                        "                               DISTINCT\n" +
                        "                               user_id,\n" +
                        "                               max(created) created\n" +
                        "                             FROM user_arrival\n" +
                        "                             WHERE come_in = FALSE\n" +
                        "                             GROUP BY user_id, date_trunc(''day'' , created)\n" +
                        "\n" +
                        "                           ) AS maxua ON ve.id = maxua.user_id AND date_trunc(''day'', ua.created) = date_trunc(''day'', maxua.created)\n" +
                        "                 LEFT JOIN (\n" +
                        "                             SELECT\n" +
                        "                               user_id,\n" +
                        "                               min(created) created\n" +
                        "                             FROM user_arrival\n" +
                        "                             WHERE come_in = TRUE\n" +
                        "                             GROUP BY user_id, date_trunc(''day'' , created)\n" +
                        "\n" +
                        "                           ) AS minua ON ve.id = minua.user_id AND date_trunc(''day'', ua.created) = date_trunc(''day'', minua.created)\n" +
                        "                 LEFT JOIN (\n" +
                        "                             SELECT\n" +
                        "                               max(id),\n" +
                        "                               employee_id,\n" +
                        "                               week_day_id,\n" +
                        "                               work_hour_status_id\n" +
                        "                             FROM employee_work_hour\n" +
                        "                             GROUP BY employee_id, week_day_id, work_hour_status_id\n" +
                        "                           ) AS ewh\n" +
                        "                   ON ve.id = ewh.employee_id AND EXTRACT(ISODOW FROM ua.created) = ewh.week_day_id AND ewh.work_hour_status_id = 1  WHERE VE.dept_id = "+((DEPARTMENT)departmentCB.getValue()).getId().getId().longValue()+"  " +
                        "               ORDER BY ve.id) as newtab')\n" +
                        "  AS final_result(\n" +
                        "     FIO TEXT, ";
                for(int i = 1; i <= maxDate ; i++){
                    sql += "\""+i+"\" text";
                    if(i!=maxDate){
                        sql += " , ";
                    }
                }
                sql +="     );";
                System.out.println(sql);

                List<String> tableHeader = new ArrayList<>();
                tableHeader.add("ФИО и должность");
                for(int i = 1; i <= maxDate ; i++){
                    tableHeader.add(i+"");
                }
                List<List<String>> tableBody = new ArrayList<>();
                try {
                    Map<Integer, Object> params = new HashMap<>();
                    List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                    if (!tmpList.isEmpty()) {
                        for (Object o : tmpList) {
                            Object[] oo = (Object[]) o;
                            ArrayList<String> row = new ArrayList<>();
                            for(Object in : oo){
                                row.add(in != null ? in.toString() : "");
                            }
                            tableBody.add(row);
                        }
                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load statistics list", ex);
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, "document", "document");
                printDialog.getPdfBtn().setVisible(false);
            }
        });
        mainHL.addComponent(exportBtn);
        mainHL.setSizeFull();
        Panel panel = new Panel(mainHL);
        panel.setSizeUndefined();
        addComponent(panel);
    }
}
