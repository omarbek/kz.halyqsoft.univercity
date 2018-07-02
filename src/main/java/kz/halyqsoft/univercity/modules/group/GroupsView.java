package kz.halyqsoft.univercity.modules.group;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.MouseEvents;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.FieldBinder;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
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
import org.r3a.common.vaadin.widget.form.field.validator.IntegerValidator;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

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
        TextField textField = new TextField("Student number");
        textField.setValue("21");
        Button generateBtn = new Button("Generate");
        generateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Integer max = new Integer(textField.getValue());
                if(max!=null && max > 0) {
                        generateNew(max);
                        refreshGridWidget();
                }else{
                    Notification.show("Enter value:");
                }
            }
        });
        mainVL.addComponent(textField);
        mainVL.setComponentAlignment(textField,Alignment.MIDDLE_CENTER);

        mainVL.addComponent(generateBtn);
        mainVL.setComponentAlignment(generateBtn,Alignment.MIDDLE_CENTER);

        mainVL.addComponent(groupsGW);
        getContent().addComponent(mainVL);
    }

    private STUDENT_EDUCATION getStudentEducationByStudentId(ID id){
        STUDENT_EDUCATION studentEducation = null;
        try{
            QueryModel<STUDENT_EDUCATION> qm = new QueryModel<>(STUDENT_EDUCATION.class);
            qm.addWhere("student", ECriteria.EQUAL , id);

            studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
        }catch (Exception e){
            Message.showError(e.getMessage());
            e.printStackTrace();
        }

        return studentEducation;
    }

    private void refreshGridWidget(){
        try{
            QueryModel<GROUPS> groupsQueryModel = new QueryModel<>(GROUPS.class);
            groupsQueryModel.addWhere("deleted" , ECriteria.EQUAL , false);
            refresh(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQueryModel));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initGridWidget(){
        groupsGW = new GridWidget(GROUPS.class);
        groupsGW.removeEntityListener(groupsGW);
        groupsGW.setSizeFull();
        groupsGW.setImmediate(true);
        groupsGW.showToolbar(true);
        groupsGW.addEntityListener(this);
        groupsGW.setMultiSelect(true);


        dbGridModel = (DBGridModel) groupsGW.getWidgetModel();
        dbGridModel.setRefreshType(ERefreshType.AUTO);
        dbGridModel.setDeferredDelete(true);
        dbGridModel.getQueryModel().addWhere("deleted", ECriteria.EQUAL,false);
        dbGridModel.setMultiSelect(true);
        dbGridModel.setRowNumberVisible(true);
        dbGridModel.setRowNumberWidth(50);
        dbGridModel.getColumnModel("deleted").setInGrid(false);
    }

    private void generateNew(int STUDENT_MAX){
        QueryModel<STUDENT> studentQueryModel = new QueryModel<>(STUDENT.class);
        FromItem fi = studentQueryModel.addJoin(EJoin.INNER_JOIN , "id" , STUDENT_EDUCATION.class , "student");
        FromItem fi2 = fi.addJoin(EJoin.INNER_JOIN, "studyYear" , STUDY_YEAR.class , "id");
        studentQueryModel.addWhere(fi2 , "studyYear" , ECriteria.EQUAL , 1);

        List students = null;

        try{
            students = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentQueryModel);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Set<GROUPS> set = new HashSet<>();
        for(STUDENT student : (List<STUDENT>)students)
        {
            if(student.getStudentEducations().iterator().next().getGroups()!=null)
            {
                set.add(student.getStudentEducations().iterator().next().getGroups());
            }
        }

        Map<SPECIALITY , ArrayList<STUDENT>> specialityMapKz = new HashMap<>();
        Map<SPECIALITY , ArrayList<STUDENT>> specialityMapRu = new HashMap<>();
        Map<SPECIALITY , ArrayList<STUDENT>> specialityMapEn = new HashMap<>();

        QueryModel<SPECIALITY> specialityQueryModel = new QueryModel<>(SPECIALITY.class);
        List<SPECIALITY> specialityList = null;
        try {
            specialityList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQueryModel);

            for(SPECIALITY specialitySingle :specialityList )
            {
                specialityMapEn.put(specialitySingle, new ArrayList<STUDENT>());
                specialityMapRu.put(specialitySingle, new ArrayList<STUDENT>());
                specialityMapKz.put(specialitySingle, new ArrayList<STUDENT>());
            }

            Map<String , Map<SPECIALITY , ArrayList<STUDENT>>> languageMap = new HashMap<>();

            languageMap.put("kz", specialityMapKz);
            languageMap.put("ru", specialityMapRu);
            languageMap.put("en", specialityMapEn);


            for(STUDENT s : (List<STUDENT>)students)  {
                String lang = s.getEntrantSpecialities().iterator().next().getLanguage().getLangName();
                if(lang.equalsIgnoreCase("Казахский")){
                    languageMap.get("kz").get(getStudentEducationByStudentId(s.getId()).getSpeciality()).add(s);
                }else if(lang.equalsIgnoreCase("Английский")){
                    languageMap.get("en").get(getStudentEducationByStudentId(s.getId()).getSpeciality()).add(s);
                }else{
                    languageMap.get("ru").get(getStudentEducationByStudentId(s.getId()).getSpeciality()).add(s);
                }
            }

            for(String langKey : languageMap.keySet())
            {
                for(SPECIALITY speciality : languageMap.get(langKey).keySet())
                {
                    GROUPS groups = null;
                    if(languageMap.get(langKey).get(speciality).size()>0) {
                        int groupCounter = 1;
                        int studentCounter = 1;

                        if (groupCounter == 1) {
                            groups = new GROUPS();
                            groups.setName(  speciality.getCode() + "-" + langKey + "-" + ((Calendar.getInstance().get(Calendar.YEAR)-2000)*100+groupCounter));
                            groups.setSpeciality(speciality);
                            groups.setOrders(new Long(groupCounter));
                            groups.setDeleted(false);
                            groups.setCreated(new Date());
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(groups);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        for (STUDENT student : languageMap.get(langKey).get(speciality)) {
                            int aasd = 0;
                            if (studentCounter > STUDENT_MAX) {
                                groupCounter++;
                                groups = new GROUPS();
                                groups.setName(speciality.getCode() + "-" + langKey + "-" + ((Calendar.getInstance().get(Calendar.YEAR)-2000)*100+groupCounter ));
                                groups.setSpeciality(speciality);
                                groups.setOrders(new Long(groupCounter));
                                groups.setDeleted(false);
                                groups.setCreated(new Date());
                                try {
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(groups);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                studentCounter = 1;
                            }
                            student.getLastEducation().setGroups(groups);
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student.getLastEducation());
                            studentCounter++;
                        }
                    }
                }
            }

            if(set.size()>0)
            {
                List<GROUPS> list = new ArrayList<>();
                for(GROUPS group : set)
                {
                    list.add(group);
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(list);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void deleteExtra(){
        try{
            Map<Integer, Object> params = new HashMap<>();
            String sql = "select * from groups where date_trunc('year',created )= date_trunc('year' , now()) and deleted = false";
            List<GROUPS> groupsList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql ,params , GROUPS.class);

            for(GROUPS group : groupsList)
            {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(group);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

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
