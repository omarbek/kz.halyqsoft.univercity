package kz.halyqsoft.univercity.modules.employee;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CARD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.filter.FEmployeeFilter;
import kz.halyqsoft.univercity.filter.panel.EmployeeFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 12.04.2018
 */
public class EmployeeView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final EmployeeFilterPanel filterPanel;
    private GridWidget teacherGW;
    private ComboBox cb;

    public EmployeeView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new EmployeeFilterPanel(new FEmployeeFilter());
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
        for(int i = 1; i<19; i++) {
            cb.addItem(i);
        }
        filterPanel.addFilterComponent("childAge", cb);

        teacherGW = new GridWidget(VEmployee.class);
        teacherGW.addEntityListener(this);
        teacherGW.showToolbar(true);
        teacherGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        teacherGW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        DBGridModel teacherGM = (DBGridModel) teacherGW.getWidgetModel();
        teacherGM.setTitleVisible(false);
        teacherGM.setMultiSelect(false);
        teacherGM.setRefreshType(ERefreshType.MANUAL);

        doFilter(filterPanel.getFilterBean());

        getContent().addComponent(teacherGW);
        getContent().setComponentAlignment(teacherGW, Alignment.MIDDLE_CENTER);
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
            sb.append("lower(usr.FIRST_NAME) like '");
            sb.append(ef.getFirstname().trim().toLowerCase());
            sb.append("%'");
        }
        if (ef.getLastname() != null && ef.getLastname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(usr.LAST_NAME) like '");
            sb.append(ef.getLastname().trim().toLowerCase());
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
            sb.append(" date_part('year',age(c2.birth_date)) <= "+ef.getChildAge()+" ");
            i++;
        }

        List<VEmployee> list = new ArrayList<>();

        if (sb.length() > 0) {
            sb.append(" and ");
        }
        sb.insert(0, " where ");
        String sql = "SELECT " +
                "  empl.ID, " +
                "  usr.CODE, " +
                "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO, " +
                "  dep.DEPT_NAME, " +
                "  post.POST_NAME " +
                "FROM EMPLOYEE empl INNER JOIN USERS usr ON empl.ID = usr.ID " +
                "  LEFT JOIN EMPLOYEE_DEPT empl_dept ON empl_dept.EMPLOYEE_ID = empl.ID AND empl_dept.DISMISS_DATE IS NULL " +
                "  LEFT JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID " +
                "  LEFT JOIN POST post ON empl_dept.POST_ID = post.id " +
                " LEFT JOIN child c2 on empl.id = c2.employee_id " + sb.toString() +
                " usr.id not in (1,2) and usr.deleted = FALSE " +
                " order by FIO ";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VEmployee ve = new VEmployee();
                    ve.setId(ID.valueOf((long) oo[0]));
                    ve.setCode((String) oo[1]);
                    ve.setFio((String) oo[2]);
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

    @Override
    public void clearFilter() {
        doFilter(filterPanel.getFilterBean());
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
    public boolean preCreate(Object source, int buttonId) {
        return openEmployeeEdit(source, null, false);
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
                getContent().removeComponent(filterPanel);
                getContent().removeComponent(teacherGW);
//                VerticalLayout viewVL = new VerticalLayout();
//                viewVL.addComponent(filterPanel);
//                viewVL.addComponent(teacherGW);
                EmployeeEdit employeeEdit = new EmployeeEdit(fm, getContent(), this);
                getContent().addComponent(employeeEdit);

                return false;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to open employeeEdit", ex);
            }
        }

        return true;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return openEmployeeEdit(source, e, false);
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return openEmployeeEdit(source, e, true);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(teacherGW)) {
            List<EMPLOYEE> mergeList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    EMPLOYEE e1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class,
                            e.getId());
                    e1.setDeleted(true);
                    mergeList.add(e1);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete the teacher", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                teacherGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh teachers grid", ex);
            }
            return false;
        }

        return true;
    }

    public EmployeeFilterPanel getFilterPanel() {
        return filterPanel;
    }

    public GridWidget getTeacherGW() {
        return teacherGW;
    }
}
