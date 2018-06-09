package kz.halyqsoft.univercity.modules.group;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAccountants;
import kz.halyqsoft.univercity.filter.FAccountantFilter;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.GroupFilterPanel;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.FormWidgetDialog;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class GroupsView extends AbstractTaskView implements FilterPanelListener , EntityListener {

    private GroupFilterPanel groupFilterPanel;
    private VerticalLayout mainVL;
    private GridWidget groupsGW;
    private DBGridModel dbGridModel;

    @Override
    public void clearFilter() {
        doFilter(groupFilterPanel.getFilterBean());
    }

    public GroupsView(AbstractTask task) throws Exception {
        super(task);
        mainVL = new VerticalLayout();
    }


    @Override
    public void initView(boolean b) throws Exception {
        initGridWidget();
        initFilter();
        mainVL.addComponent(groupFilterPanel);

        Button generateBtn = new Button("Generate");
        generateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                QueryModel<STUDENT_EDUCATION> qm = new QueryModel<>(STUDENT_EDUCATION.class);
                qm.addWhereNull("groups");
                FromItem fi = qm.addJoin(EJoin.INNER_JOIN, "student", STUDENT.class, "id");
                qm.addWhereAnd(fi , "category", ECriteria.EQUAL , STUDENT_CATEGORY.STUDENT_ID);

                List<STUDENT_EDUCATION> list = null;
                try{
                    list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
                    for(STUDENT_EDUCATION student_education : list){
                        student_education.getSpeciality();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        mainVL.addComponent(generateBtn);
        mainVL.setComponentAlignment(generateBtn,Alignment.MIDDLE_CENTER);

        mainVL.addComponent(groupsGW);
        getContent().addComponent(mainVL);
    }


    public void initGridWidget(){
        groupsGW = new GridWidget(GROUPS.class);
        groupsGW.removeEntityListener(groupsGW);
        groupsGW.setSizeFull();
        groupsGW.setImmediate(true);
        groupsGW.showToolbar(true);
        groupsGW.addEntityListener(this);


        dbGridModel = (DBGridModel) groupsGW.getWidgetModel();
        dbGridModel.setRefreshType(ERefreshType.AUTO);
        dbGridModel.setDeferredDelete(true);
        dbGridModel.getQueryModel().addWhere("deleted", ECriteria.EQUAL,false);
        dbGridModel.setMultiSelect(false);
        dbGridModel.setRowNumberVisible(true);
        dbGridModel.setRowNumberWidth(50);
        dbGridModel.getColumnModel("deleted").setInGrid(false);

    }



    public void initFilter() throws Exception{
        groupFilterPanel = new GroupFilterPanel(new FGroupFilter());
        groupFilterPanel.addFilterPanelListener(this);
        groupFilterPanel.setImmediate(true);

        ComboBox specialityComboBox = new ComboBox();
        specialityComboBox.setNullSelectionAllowed(true);
        specialityComboBox.setTextInputAllowed(true);
        specialityComboBox.setFilteringMode(FilteringMode.CONTAINS);
        specialityComboBox.setWidth(300, Unit.PIXELS);
        QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
        BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        specialityComboBox.setContainerDataSource(specialityBIC);
        groupFilterPanel.addFilterComponent("speciality", specialityComboBox);


        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        groupFilterPanel.addFilterComponent("name", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        groupFilterPanel.addFilterComponent("orders", tf);

    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        super.handleEntityEvent(ev);
    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FGroupFilter sf = (FGroupFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (sf.getName() != null) {

            sb.append(" and ");
            params.put(i, sf.getName());
            sb.append("name = ?" + i++);

        }

        if (sf.getSpeciality()!=null) {

            sb.append(" and ");
            params.put(i, sf.getSpeciality().getId().getId());
            sb.append("speciality_id = ?" + i++);

        }

        if (sf.getOrders() != null) {

            sb.append(" and ");
            params.put(i, sf.getOrders());
            sb.append(" orders = ?" + i++);

        }
        List<GROUPS> list = new ArrayList<>();

        sb.insert(0, " where deleted = false ");
        String sql = "SELECT * from groups "
                + sb.toString();
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;

                    GROUPS group = new GROUPS();
                    group.setId(ID.valueOf((long) oo[0]));
                    QueryModel<SPECIALITY> qm = new QueryModel<>(SPECIALITY.class);
                    qm.addWhere("id" , ECriteria.EQUAL , ID.valueOf((long) oo[1]));
                    SPECIALITY speciality = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                    group.setSpeciality(speciality);
                    group.setName((String) oo[2]);
                    group.setOrders((long)oo[3]);
                    list.add(group);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
        }


        refresh(list);
    }


    public GroupFilterPanel getGroupFilterPanel() {
        return groupFilterPanel;
    }



    private void refresh(List<GROUPS> list) {

        ((DBGridModel) groupsGW.getWidgetModel()).setEntities(list);
        try {
            groupsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh groups list", ex);
        }
    }


    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
        ArrayList<GROUPS> arrayList = new ArrayList<>();
        for(Entity entity : entities)
        {
            GROUPS g = (GROUPS) entity;
            g.setDeleted(true);
            arrayList.add(g);
        }
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(arrayList);
        }catch (Exception e)
        {
            CommonUtils.LOG.error("Unable to delete" + getModuleName() + ": ", e);
            Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
        }

    }

    protected String getModuleName() {
        return "GroupsUtils";
    }


    protected Class<? extends Entity> getEntityClass() {
        return GROUPS.class;
    }

}
