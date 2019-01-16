package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DocumentDownloadView extends BaseView implements EntityListener {

    private GridWidget teacherGW;

    public DocumentDownloadView(String title) {
        super(title);
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

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

    @Override
    public void initView(boolean b) throws Exception {

        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout hl = new HorizontalLayout();

        Label title = new Label("Количественные и качественные показатели профессорско-преподавательского состава (возраст)");
        Label title2 = new Label("Количественные и качественные показатели профессорско-преподавательского состава ");
        Label title3 = new Label("Список  ппс Международного университета Silkway на 26.10.2018года \n ");
        Label title4 = new Label("    Количественный и качественный контингент ППс МЕЖДУНАРОДНОГО УНИВЕРСИТЕТА SILKWAY По состоянию на 26.10.2018г.");

        vl.addComponent(title);

        ComboBox departmentCB = new ComboBox();
        departmentCB.setNullSelectionAllowed(true);
        departmentCB.setTextInputAllowed(true);
        departmentCB.setFilteringMode(FilteringMode.CONTAINS);
        departmentCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<DEPARTMENT> departmentQM = new QueryModel<>(DEPARTMENT.class);
        departmentQM.addOrder("id");
        BeanItemContainer<DEPARTMENT> departmentBIC = null;
        try {
            departmentBIC = new BeanItemContainer<>(DEPARTMENT.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQM));
        } catch (Exception e) {
            e.printStackTrace();
        }
        departmentCB.setContainerDataSource(departmentBIC);
        hl.addComponent(departmentCB);

        Button download = new Button("скачать");
        vl.addComponents(download);

        vl.addComponent(title2);
        Button download2 = new Button("скачать");
        vl.addComponents(download2);

        vl.addComponent(title3);
        Button downloaDdep = new Button("скачать");
        hl.addComponents(downloaDdep);

        Button downloa3 = new Button("скачать");

        vl.addComponents(hl);
        vl.addComponent(title4);
        vl.addComponents(downloa3);
        download.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String headers[] = {"№ п/п", "Көрсеткіштер /\n Показатели", "Барлығы (адам)/\n Всего (человек)", "магистр /\n магистров", "ғылым кандидаттары /\n кандидатов наук", "PhD докторлары /\n докторов Ph.D", "ғылым докторлары /\n докторов наук", "доценттер /\n доцентов", "қауымдастырылған профессор (доцент)/\n ассоциированный профессор (доцент)", "профессорлар /\n профессоров (по новой квалификации)"};
                tableHeader.addAll(Arrays.asList(headers));

                String sql = "SELECT 1,'штаттық ПОҚ / Штатный ППС',\n" +
                        "  count(d2.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END)\n" +
                        "FROM employee_degree\n" +
                        "  INNER JOIN degree d2 ON employee_degree.degree_id = d2.id\n" +
                        "  INNER JOIN user_document u ON employee_degree.id = u.id\n" +
                        "  INNER JOIN users u2 ON u.user_id = u2.id\n" +
                        "  INNER JOIN employee e ON u2.id = e.id\n" +
                        "WHERE e.employee_status_id=1 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 2,'30 жасқа дейін/ до 30 лет ',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  date_part('year',age(birth_date))<=30 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 3,'31-40 жас/ 31-40 лет',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  date_part('year',age(birth_date)) BETWEEN 31 AND 40 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 4,'41-50 жас/ 41-50 лет',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  date_part('year',age(birth_date)) BETWEEN 41 AND 50 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 5 ,'51-63 жас/ 51- 63 лет',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  date_part('year',age(birth_date)) BETWEEN 51 AND 63 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 6,'64 жас және одан жоғары/ 64 лет и старше',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  date_part('year',age(birth_date)) >=64 and u.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT 7,'оның ішінде әйелдер / в том числе женщин',\n" +
                        "  count(e.id),\n" +
                        "  sum(CASE when d2.id=1 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=2 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=5 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=6 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8 THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9 THEN 1 ELSE 0 END) FROM employee e\n" +
                        "  INNER JOIN users u ON e.id = u.id\n" +
                        "  INNER JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE  u.sex_id=2  and u.deleted=FALSE  ORDER BY  1;";

                try {
                    List<Object> tmpList = new ArrayList<>();
                    Map<Integer, Object> param = null;
                    tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                    if (!tmpList.isEmpty()) {
                        for (Object o : tmpList) {
                            Object[] oo = (Object[]) o;
                            ArrayList<String> valuesList = new ArrayList();
                            for (int i = 0; i < oo.length; i++) {
                                valuesList.add(oo[i] != null ? String.valueOf(oo[i]) : "");

                            }
                            tableBody.add(valuesList);

                        }
                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
                }


                String fileName = "document";

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);

            }
        });
        download2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String headers[] = {"ФИО / Аты-жөні",
                        "магистр /\n магистров",
                        "ғылым кандидаттары /\n кандидатов наук",
                        "PhD докторлары /\n докторов Ph.D",
                        "ғылым докторлары /\n докторов наук",
                        "профессорлар /\n профессоров",
                        "доценттер /\n доцентов",
                        "қауымдастырылған профессор (доцент)/\n ассоциированный профессор (доцент)",
                        "профессорлар /\n профессоров (по новой квалификации)"
                };
                tableHeader.addAll(Arrays.asList(headers));

                String sql = "SELECT ' 1',2,3,4,5,6,7,8,9\n" +
                        "UNION\n" +
                        "SELECT ' Жоғары оқу орны бойынша барлығы / всего по вузу',\n" +
                        "  sum(CASE when d2.id=6  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=1  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=3  THEN 1 ELSE 0 END),\n" +
                        "  sum (CASE when d2.id=2  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=7  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=8  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=4  THEN 1 ELSE 0 END),\n" +
                        "  sum(CASE when d2.id=9  THEN 1 ELSE 0 END)\n" +
                        "FROM employee\n" +
                        "  INNER JOIN users u ON employee.id = u.id\n" +
                        "  INNER JOIN  user_document document2 ON u.id = document2.user_id\n" +
                        "  INNER JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "  INNER JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "WHERE document2.deleted=FALSE\n" +
                        "UNION\n" +
                        "SELECT\n" +
                        "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                        "  (CASE when d2.id=6  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=1  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=3  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=2  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=7  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=8  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=4  THEN 1 ELSE 0 END),\n" +
                        "  (CASE when d2.id=9  THEN 1 ELSE 0 END)\n" +
                        "FROM employee\n" +
                        "INNER JOIN users u ON employee.id = u.id\n" +
                        "LEFT JOIN  user_document document2 ON u.id = document2.user_id\n" +
                        "LEFT JOIN employee_degree ed ON document2.id = ed.id\n" +
                        "LEFT JOIN degree d2 ON ed.degree_id = d2.id\n" +
                        "  WHERE document2.deleted=FALSE\n" +
                        "GROUP BY u.first_name,u.last_name,u.middle_name,d2.id\n" +
                        "ORDER BY 1;";

                List<String> list = new ArrayList<>();

                try {
                    List<Object> tmpList = new ArrayList<>();
                    Map<Integer, Object> param = null;
                    tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                    if (!tmpList.isEmpty()) {
                        for (Object o : tmpList) {
                            Object[] oo = (Object[]) o;
                            ArrayList<String> valuesList = new ArrayList();
                            for (int i = 0; i < oo.length; i++) {
                                valuesList.add(oo[i] != null ? String.valueOf(oo[i]) : "");

                            }
                            tableBody.add(valuesList);

                        }
                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
                }


                String fileName = "document";

                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);

            }
        });

        downloaDdep.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();
                List<String> tableHeader1 = new ArrayList<>();
                List<List<String>> tableBody1 = new ArrayList<>();
                if (departmentCB.getValue() != null) {

                    String headers[] = {" ", "Ф.И.О. преподавателей", "Год  рож-\n" +
                            "дения", "Занимаемая должность", "Базовое образование (ВУЗ,год окон-\n" +
                            "чания, специальность.)", "Общ.пед. стаж/н.педстаж.", "Ученая степень,звание"
                    };
                    tableHeader.addAll(Arrays.asList(headers));

                    String sql = "\n" +
                            "SELECT  es.status_name,trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                            "  u.birth_date,p.post_name,edd.school_name,Extract(YEAR from justify_days(  sum(age(case when pe.dismiss_date isnull then now()\n" +
                            "when pe.dismiss_date NOTNULL THEN pe.dismiss_date END ,pe.hire_date)))) || ' год',d3.degree_name\n" +
                            " FROM users u\n" +
                            "INNER JOIN employee e ON u.id = e.id\n" +
                            "INNER JOIN employee_dept ed ON e.id = ed.employee_id\n" +
                            "  INNER JOIN employee_status es ON e.employee_status_id = es.id\n" +
                            "INNER JOIN department d2 ON ed.dept_id = d2.id\n" +
                            "  INNER JOIN user_document userd on u.id = userd.user_id\n" +
                            "  INNER JOIN education_doc edd on userd.id = edd.id\n" +
                            "  INNER   JOIN previous_experience pe on pe.employee_id=e.id\n" +
                            "INNER JOIN post p ON ed.post_id = p.id\n" +
                            "  LEFT JOIN employee_degree ed2 ON userd.id = ed2.id\n" +
                            " LEFT JOIN degree d3 ON ed2.degree_id = d3.id " +
                            "WHERE d2.dept_name= '" + departmentCB.getValue() + "'" +
                            " GROUP BY  u.birth_date,p.post_name,edd.school_name,FIO,u.id,es.status_name,d3.degree_name;";

                    List<String> list = new ArrayList<>();
                    ArrayList<String> dep = new ArrayList();
                    for (int i = 0; i < 8; i++) {
                        dep.add(i == 0 ? departmentCB.getValue().toString() : " ");
                    }
                    tableBody.add(dep);

                    try {
                        List<Object> tmpList = new ArrayList<>();
                        Map<Integer, Object> param = null;
                        tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                        if (!tmpList.isEmpty()) {
                            for (Object o : tmpList) {
                                Object[] oo = (Object[]) o;
                                ArrayList<String> valuesList = new ArrayList();
                                for (int i = 0; i < oo.length; i++) {
                                    valuesList.add(oo[i] != null ? String.valueOf(oo[i]) : "");
                                }

                                tableBody.add(valuesList);

                            }
                        }
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to load department list", ex);
                    }

                    String headers1[] = {"Наименование кафедр", "Всего ППС/ из них штат.", "Доктор наук, проф./ из них штат.", "Канд. наук, доцент/ из них штат.",
                            "Проц. с учен. степ. и зван./ из них штат.", "Всего совм.", "В т.ч. с учен. степ. и зван.", "Почасовики",
                            "Ведут занятия на государственном языке", "Базовое образование", "Имеют стаж прак.работы по специальности", "Стаж научно-пед. работы",
                            "Возраст"
                    };


                    String fileName = "document";

                    PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
                    //PrintDialog printDialog1 = new PrintDialog(tableHeader1, tableBody1, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);

                } else {
                    org.r3a.common.vaadin.widget.dialog.Message.showInfo(getUILocaleUtil().getMessage("more.records.not.required"));
                }
            }
        });

        downloa3.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader1 = new ArrayList<>();
                List<List<String>> tableBody1 = new ArrayList<>();

                String headers1[] = {"Наименование кафедр", "Всего ППС/ из них штат.", "Доктор наук, проф./ из них штат.", "Канд. наук, доцент/ из них штат.",
                        "Проц. с учен. степ. и зван./ из них штат.", "В т.ч. с учен. степ. и зван.", "Почасовики",
                        "Ведут занятия на государственном языке", "Базовое образование", "Имеют стаж прак.работы по специальности", "Стаж научно-пед. работы до 5 лет", "Стаж научно-пед. работы 5-15", "Стаж научно-пед. работы свыше 15",
                        "Возраст до 35 лет", "Возраст 35-50 лет", "Возраст свыше 50", "Пенсионный"
                };
                tableHeader1.addAll(Arrays.asList(headers1));

                List<String> list = new ArrayList<>();

                String sql = "SELECT d2.dept_name,\n" +
                        "  count(e.employee_status_id) || '/ ' || sum(CASE WHEN e.employee_status_id = 1 then 1 ELSE 0 end),\n" +
                        "  sum(case WHEN ed2.degree_id = 7 OR ed2.degree_id = 2 then 1 else 0 end) || '/ ' || case\n" +
                        "                                                                                     WHEN ed2.degree_id = 7 OR ed2.degree_id = 2\n" +
                        "                                                                                       then sum(CASE WHEN e.employee_status_id = 1 then 1 ELSE 0 end)\n" +
                        "                                                                                     else 0 end,\n" +
                        "  sum(case WHEN ed2.degree_id = 1 OR ed2.degree_id = 8 then 1 else 0 end) || '/ ' || case\n" +
                        "                                                                                     WHEN ed2.degree_id = 7 OR ed2.degree_id = 2\n" +
                        "                                                                                       then sum(CASE WHEN e.employee_status_id = 1 then 1 ELSE 0 end)\n" +
                        "                                                                                     else 0 end,\n" +
                        "  sum(case\n" +
                        "      WHEN ed2.degree_id = 1 OR ed2.degree_id = 2 OR ed2.degree_id = 3 OR ed2.degree_id = 4 OR\n" +
                        "           ed2.degree_id = 5 OR ed2.degree_id = 6 OR ed2.degree_id = 7 OR ed2.degree_id = 8 OR ed2.degree_id = 9\n" +
                        "        then 1\n" +
                        "      else 0 end) * 1.0 / NULLIF((sum(CASE WHEN e.employee_status_id = 1 then 1 ELSE 0 end) * 1.0) * 100.0, 0) ||\n" +
                        "  ' %',\n" +
                        "  sum(case\n" +
                        "      WHEN ed2.degree_id = 1 OR ed2.degree_id = 2 OR ed2.degree_id = 3 OR ed2.degree_id = 4 OR\n" +
                        "           ed2.degree_id = 5 OR ed2.degree_id = 6 OR ed2.degree_id = 7 OR ed2.degree_id = 8 OR ed2.degree_id = 9\n" +
                        "        then 1\n" +
                        "      else 0 end),\n" +
                        "  sum(case when e.employee_status_id = 5 then 1 else 0 end),\n" +
                        "  sum(case when d2.dep_language = TRUE then 1 else 0 end),\n" +
                        "  sum(case when edd.school_name NOTNULL then 1 else 0 end),\n" +
                        "  sum(case when pe.dismiss_date NOTNULL then 1 else 0 end),\n" +
                        "  sum(asd.qwe),\n" +
                        "  sum(asd.qwe1),\n" +
                        "  sum(asd.qwe2),\n" +
                        "  sum(case when Extract(YEAR from now())- Extract(YEAR from u.birth_date)<=35 then 1 else 0 end),\n" +
                        "  sum(case when Extract(YEAR from now())- Extract(YEAR from u.birth_date) BETWEEN 35 and 50 then 1 else 0 end),\n" +
                        "  sum(case when Extract(YEAR from now())- Extract(YEAR from u.birth_date)> 50 then 1 else 0 end),\n" +
                        "  sum(case when e.retiree = TRUE then 1 else 0 end)\n" +
                        "FROM users u\n" +
                        "  INNER JOIN\n" +
                        "  employee e\n" +
                        "    ON u.id = e.id\n" +
                        "  INNER JOIN employee_status es ON e.employee_status_id = es.id\n" +
                        "  INNER JOIN employee_dept ed ON e.id = ed.employee_id\n" +
                        "  INNER JOIN department d2 ON ed.dept_id = d2.id\n" +
                        "  inner JOIN user_document document2 ON u.id = document2.user_id\n" +
                        "  LEFT JOIN education_doc edd on document2.id = edd.id\n" +
                        "  LEFT JOIN previous_experience pe on pe.employee_id = e.id\n" +
                        "  left JOIN employee_degree ed2 ON document2.id = ed2.id\n" +
                        "  left JOIN degree d3 ON ed2.degree_id = d3.id\n" +
                        "  left join (select pe.id,\n" +
                        "               case\n" +
                        "               when Extract(YEAR from justify_days(sum(age(case\n" +
                        "                                                           when pe.dismiss_date isnull then now()\n" +
                        "                                                           when pe.dismiss_date NOTNULL\n" +
                        "                                                             THEN pe.dismiss_date END,\n" +
                        "                                                           pe.hire_date)))) <= 5\n" +
                        "                 then 1\n" +
                        "               else 0 end qwe,\n" +
                        "               case\n" +
                        "               when Extract(YEAR from justify_days(sum(age(case\n" +
                        "                                                           when pe.dismiss_date isnull then now()\n" +
                        "                                                           when pe.dismiss_date NOTNULL\n" +
                        "                                                             THEN pe.dismiss_date END,\n" +
                        "                                                           pe.hire_date)))) BETWEEN 5 and 15\n" +
                        "                 then 1\n" +
                        "               else 0 end qwe1,\n" +
                        "               case\n" +
                        "               when Extract(YEAR from justify_days(sum(age(case\n" +
                        "                                                           when pe.dismiss_date isnull then now()\n" +
                        "                                                           when pe.dismiss_date NOTNULL\n" +
                        "                                                             THEN pe.dismiss_date END,\n" +
                        "                                                           pe.hire_date))))>15\n" +
                        "                 then 1\n" +
                        "               else 0 end qwe2\n" +
                        "             from previous_experience pe\n" +
                        "             group by pe.id) asd on asd.id = pe.id\n" +
                        "WHERE d2.DELETED = false /*and faculty.deleted=FALSE*/\n" +
                        "GROUP BY d2.dept_name,ed2.degree_id,pe.id;";

                try {
                    List<Object> tmpList = new ArrayList<>();
                    Map<Integer, Object> param = null;
                    tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
                    if (!tmpList.isEmpty()) {
                        for (Object o : tmpList) {
                            Object[] oo = (Object[]) o;
                            ArrayList<String> valuesList = new ArrayList();
                            for (int i = 0; i < oo.length; i++) {
                                valuesList.add(oo[i] != null ? String.valueOf(oo[i]) : "");

                            }
                            tableBody1.add(valuesList);

                        }
                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
                }


                String fileName = "document";

//                    PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
                PrintDialog printDialog1 = new PrintDialog(tableHeader1, tableBody1, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);

            }

        });


        getContent().addComponent(vl);
        getContent().setComponentAlignment(vl, Alignment.MIDDLE_CENTER);


    }


    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return false;

    }


    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return false;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return false;
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        return false;
    }

    public GridWidget getTeacherGW() {
        return teacherGW;
    }
}
