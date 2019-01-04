package kz.halyqsoft.univercity.modules.workflowforemp.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
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

        Label title = new Label("Количественные и качественные показатели профессорско-преподавательского состава (возраст)");
        Label title2 = new Label("Количественные и качественные показатели профессорско-преподавательского состава ");
        vl.addComponent(title);

        Button download = new Button("скачать");
        vl.addComponents(download);

        vl.addComponent(title2);
        Button download2 = new Button("скачать");
        vl.addComponents(download2);

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
