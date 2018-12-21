package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CARD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.filter.FEmployeeFilter;
import kz.halyqsoft.univercity.filter.panel.EmployeeFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;


public class ScheduleEmployee extends AbstractCommonView implements EntityListener, FilterPanelListener {

    private VerticalLayout tablesVL = new VerticalLayout();
    private VerticalLayout mainVL;
    private String title;
    private GridWidget teacherGW;
    private final EmployeeFilterPanel filterPanel;
    private ComboBox cb;

    public ScheduleEmployee(String task) {
        this.title = task;
        mainVL = new VerticalLayout();
        filterPanel = new EmployeeFilterPanel(new FEmployeeFilter());
    }

    @Override
    public String getViewName() {
        return null;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return null;
    }


    public VerticalLayout getMainVL() {
        return mainVL;
    }


    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("lastname", tf);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.OFF);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        FromItem userFI = cardQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQM.addWhere(userFI, "typeIndex", ECriteria.EQUAL, 1);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cb.setContainerDataSource(cardBIC);
        filterPanel.addFilterComponent("card", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setPageLength(0);
        cb.setWidth(300, Unit.PIXELS);
        QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
        chairQM.addWhereNotNull("parent");
        chairQM.addWhereAnd("deleted", Boolean.FALSE);
        chairQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
        cb.setContainerDataSource(chairBIC);
        filterPanel.addFilterComponent("department", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(220, Unit.PIXELS);
        QueryModel<POST> postQM = new QueryModel<>(POST.class);
        BeanItemContainer<POST> postBIC = new BeanItemContainer<>(POST.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(postQM));
        cb.setContainerDataSource(postBIC);
        filterPanel.addFilterComponent("post", cb);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.OFF);
        for (int i = 1; i < 19; i++) {
            cb.addItem(i);
        }
        filterPanel.addFilterComponent("childAge", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(220, Unit.PIXELS);
        QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
        BeanItemContainer<SUBJECT> subjectBIC = new BeanItemContainer<>(SUBJECT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectQM));
        cb.setContainerDataSource(subjectBIC);
        filterPanel.addFilterComponent("subject", cb);

        mainVL.addComponent(filterPanel);
        mainVL.setComponentAlignment(filterPanel, Alignment.TOP_CENTER);



        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        mainVL.addComponent(buttonsHL);
        mainVL.setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        teacherGW = new GridWidget(V_EMPLOYEE.class);
        teacherGW.showToolbar(true);
        teacherGW.addEntityListener(this);
        teacherGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        DBGridModel teacherGM = (DBGridModel) teacherGW.getWidgetModel();
        teacherGM.setTitleVisible(false);
        teacherGM.setMultiSelect(false);
        teacherGM.setRefreshType(ERefreshType.MANUAL);

        doFilter(filterPanel.getFilterBean());
        mainVL.addComponent(teacherGW);
        mainVL.addComponent(tablesVL);
        mainVL.setComponentAlignment(tablesVL, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(teacherGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                List<Entity> selectedList = ev.getEntities();
                if (!selectedList.isEmpty()) {
                    onEdit(ev.getSource(), selectedList.get(0), 2);
                }

            }
        }
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FEmployeeFilter ef = (FEmployeeFilter) abstractFilterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (ef.getCode() != null && ef.getCode().trim().length() >= 2) {
            sb.append("lower(usr.CODE) like '");
            sb.append(ef.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (ef.getFirstname() != null && ef.getFirstname().trim().length() >= 3) {
            sb.append("usr.FIRST_NAME ilike '");
            sb.append(ef.getFirstname().trim());
            sb.append("%'");
        }
        if (ef.getLastname() != null && ef.getLastname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("usr.LAST_NAME ilike '");
            sb.append(ef.getLastname().trim());
            sb.append("%'");
        }
        if (ef.getCard() != null) {
            params.put(i, ef.getCard().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("usr.card_id = ?");
            sb.append(i++);
        }
        if (ef.getDepartment() != null) {
            params.put(i, ef.getDepartment().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("empl_dept.DEPT_ID = ?");
            sb.append(i++);
        }
        if (ef.getPost() != null) {
            params.put(i, ef.getPost().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("empl_dept.POST_ID = ?");
            sb.append(i++);
        }
        if (ef.getChildAge() != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append(" date_part('year',age(c2.birth_date)) <= " + ef.getChildAge() + " ");
            i++;
        }
        if(ef.getSubject()!=null){
            params.put(i, ef.getSubject().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("subj.subject_id = ?");
            sb.append(i++);
        }

        List<V_EMPLOYEE> list = new ArrayList<>();

        if (sb.length() > 0) {
            sb.append(" and ");
        }
        sb.insert(0, " where ");
        String sql = "SELECT  empl.ID,  usr.CODE,\n" +
                "  usr.LAST_NAME,usr.FIRST_NAME, usr.MIDDLE_NAME,\n" +
                "  dep.DEPT_NAME,\n" +
                "  post.post_name \n" +
                "FROM EMPLOYEE empl INNER JOIN USERS usr ON empl.ID = usr.ID\n" +
                "  LEFT JOIN EMPLOYEE_DEPT empl_dept ON empl_dept.EMPLOYEE_ID = empl.ID AND empl_dept.DISMISS_DATE IS NULL\n" +
                "  LEFT JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID\n" +
                "  LEFT JOIN POST post ON empl_dept.POST_ID = post.id\n" +
                "  LEFT JOIN teacher_subject subj ON empl.id = subj.employee_id" +
                "  LEFT JOIN child c2 on empl.id = c2.employee_id\n" + sb.toString() +
                "   usr.id not in (1,2) and usr.deleted = FALSE  \n" +
                "GROUP BY empl.ID,  usr.CODE,\n" +
                "   usr.LAST_NAME,usr.FIRST_NAME, usr.MIDDLE_NAME,\n" +
                "  dep.DEPT_NAME,post.post_name,post.post_name,empl_dept.priority\n" +
                "  HAVING count(empl_dept.priority)>=0" +
                " ORDER by empl_dept.priority DESC\n";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    V_EMPLOYEE ve = new V_EMPLOYEE();
                    ve.setId(ID.valueOf((long) oo[0]));
                    ve.setCode((String) oo[1]);
                    ve.setLastName((String)oo[2]);
                    ve.setFirstName((String)oo[2]);
                    ve.setMiddleName((String)oo[2]);
                    ve.setDeptName((String) oo[3]);
                    ve.setPostName((String) oo[4]);
                    list.add(ve);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }

        ((DBGridModel) teacherGW.getWidgetModel()).setEntities(list);
        try {
            teacherGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh teacher grid", ex);
        }
    }

    public EmployeeFilterPanel getFilterPanel() {
        return filterPanel;
    }

    @Override
    public void clearFilter() {
        doFilter(filterPanel.getFilterBean());
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return openEmployeeEdit(source, e, false);
    }
    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return openEmployeeEdit(source, e, true);
    }

    private boolean openEmployeeEdit(Object source, Entity e, boolean readOnly) {
        if (source.equals(teacherGW)) {
            FormModel fm = new FormModel(EMPLOYEE.class);
            fm.setReadOnly(readOnly);
            fm.setTitleVisible(false);
            try {
                if (e != null) {
                    fm.loadEntity(e.getId());
                } else {
                    fm.createNew();
                }
                mainVL.removeComponent(teacherGW);

                ScheduleEmployeeView employeeView = new ScheduleEmployeeView(fm, mainVL, this);
                mainVL.addComponent(employeeView);

                return false;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to open employeeEdit", ex);
            }
        }

        return true;
    }

    public GridWidget getTeacherGW() {
        return teacherGW;
    }

   // public boolean onPreview(Object source, Entity e, int buttonId) {
//        return openEmployeeEdit(source, e, true);
//    }

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
