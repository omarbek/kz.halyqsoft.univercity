package kz.halyqsoft.univercity.modules.creation;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VTaskRoles;
import kz.halyqsoft.univercity.filter.FTaskRolesFilter;
import kz.halyqsoft.univercity.filter.panel.TaskRolesFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRoleBindingView extends AbstractTaskView implements EntityListener, FilterPanelListener {
    private TaskRolesFilterPanel filterPanel;
    private HorizontalLayout horizontalLayout;

    private Button bindBtn;
    private ComboBox cbTasks;
    private ComboBox cbRoles;
    private GridWidget taskRoleGW;

    public TaskRoleBindingView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new TaskRolesFilterPanel(new FTaskRolesFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        bindBtn = new Button(getUILocaleUtil().getCaption("creation.bind"));
        horizontalLayout = new HorizontalLayout();

        bindBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TASKS task = (TASKS)cbTasks.getValue();
                ROLES role = (ROLES)cbRoles.getValue();

                if(role==null || task == null)
                {
                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
                    return;
                }

                ROLE_TASKS role_tasks = new ROLE_TASKS();
                role_tasks.setRole(role);
                role_tasks.setTask(task);
                role_tasks.setAccessType(true);
                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(role_tasks);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message.showError(e.getMessage());
                        }
                        refresh();
                    }
                });
            }
        });

        cbRoles = new ComboBox();
        cbRoles.setNullSelectionAllowed(true);
        cbRoles.setTextInputAllowed(false);
        cbRoles.setFilteringMode(FilteringMode.CONTAINS);
        cbRoles.setPageLength(0);
        cbRoles.setWidth(300, Unit.PIXELS);
        QueryModel<ROLES> roleQM = new QueryModel<>(ROLES.class);
        BeanItemContainer<ROLES> roleBIC = new BeanItemContainer<>(ROLES.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roleQM));
        cbRoles.setContainerDataSource(roleBIC);
        filterPanel.addFilterComponent("roleName", cbRoles);


        cbTasks = new ComboBox();
        cbTasks.setNullSelectionAllowed(true);
        cbTasks.setTextInputAllowed(false);
        cbTasks.setFilteringMode(FilteringMode.CONTAINS);
        cbTasks.setPageLength(0);
        cbTasks.setWidth(220, Unit.PIXELS);
        QueryModel<TASKS> taskQM = new QueryModel<>(TASKS.class);
        BeanItemContainer<TASKS> taskBIC = new BeanItemContainer<>(TASKS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(taskQM));
        cbTasks.setContainerDataSource(taskBIC);

        filterPanel.addFilterComponent("taskName", cbTasks);

        horizontalLayout.addComponent(bindBtn);
        horizontalLayout.setComponentAlignment(bindBtn,Alignment.MIDDLE_CENTER);


        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        getContent().addComponent(horizontalLayout);
        getContent().setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);

        taskRoleGW = new GridWidget(VTaskRoles.class);
        taskRoleGW.addEntityListener(this);
        taskRoleGW.setButtonVisible(IconToolbar.EDIT_BUTTON , false);
        taskRoleGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        taskRoleGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        taskRoleGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        DBGridModel taskRoleGM = (DBGridModel) taskRoleGW.getWidgetModel();
        taskRoleGM.setRowNumberVisible(true);
        taskRoleGM.setTitleVisible(false);
        taskRoleGM.setMultiSelect(true);
        taskRoleGM.setRefreshType(ERefreshType.MANUAL);

        FTaskRolesFilter ef = (FTaskRolesFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }
        getContent().addComponent(taskRoleGW);
        getContent().setComponentAlignment(taskRoleGW, Alignment.MIDDLE_CENTER);

    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FTaskRolesFilter urf = (FTaskRolesFilter) abstractFilterBean;
        int i =  1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (urf.getRoleName() != null) {
            params.put(i, urf.getRoleName().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("rt.role_id = ?");
            sb.append(i++);
        }
        if (urf.getTaskName() != null) {
            params.put(i, urf.getTaskName().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("rt.task_id= ?");
            sb.append(i++);
        }

        List<VTaskRoles> list = new ArrayList<>();
        if(sb.length()>0){


            String sql = "select rt.id , " +
                    "   trim(t.NAME || ' ' || t.TITLE || ' '" +
                    "|| coalesce(t.DESCR, '')) taskName , r.role_name roleName " +
                    "   from role_tasks rt " +
                    "   INNER JOIN tasks t on t.id = rt.task_id " +
                    "   INNER JOIN roles r on r.id = rt.role_id where "
                    + sb.toString();
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql , params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VTaskRoles ve = new VTaskRoles();
                        ve.setId(ID.valueOf((long) oo[0]));
                        ve.setTaskName((String) oo[1]);
                        ve.setRoleName((String) oo[2]);
                        list.add(ve);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load task_roles list", ex);
            }
        }else {
            Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
        }

        ((DBGridModel)taskRoleGW.getWidgetModel()).setEntities(list);
        try {
            taskRoleGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh task_roles grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }

    private void refresh(List<VTaskRoles> list) {
        ((DBGridModel) taskRoleGW.getWidgetModel()).setEntities(list);
        try {
            taskRoleGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh task_role grid", ex);
        }
    }


    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if(source.equals(taskRoleGW)){
            for(Entity entity:entities){
                try{
                    ROLE_TASKS roleTasks= SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROLE_TASKS.class , entity.getId());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(roleTasks);

                }catch (Exception e){
                    Message.showError(e.getMessage());
                }
            }
            refresh();
        }
        return false;
    }

    private void refresh(){
        try {
            taskRoleGW.refresh();
            doFilter(filterPanel.getFilterBean());
        } catch (Exception e){
            Message.showError(e.getMessage());
        }
    }
}
