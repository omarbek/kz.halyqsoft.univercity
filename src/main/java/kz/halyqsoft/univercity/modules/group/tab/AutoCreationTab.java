package kz.halyqsoft.univercity.modules.group.tab;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.ENTRANT_SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.GroupFilterPanel;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.Calendar;

public class AutoCreationTab extends AbstractCommonView  implements FilterPanelListener, EntityListener {

    private GroupFilterPanel groupFilterPanel;
    private VerticalLayout mainVL;
    private GridWidget groupsGW;
    private DBGridModel dbGridModel;
    private String title;
    public AutoCreationTab(String title) {
        this.title = title;
        mainVL = new VerticalLayout();
        initGridWidget();
        try{
            initFilter();
        }catch (Exception e){
            e.printStackTrace();
        }
        mainVL.addComponent(groupFilterPanel);
        TextField textField = new TextField("Student number");
        textField.setValue("21");
        Button generateBtn = new Button(getUILocaleUtil().getCaption("generate"));
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
        mainVL.setComponentAlignment(textField, Alignment.MIDDLE_CENTER);

        mainVL.addComponent(generateBtn);
        mainVL.setComponentAlignment(generateBtn,Alignment.MIDDLE_CENTER);

        mainVL.addComponent(groupsGW);
        doFilter(getGroupFilterPanel().getFilterBean());
    }

    @Override
    public String getViewName() {
        return title;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return title;
    }

    @Override
    public void initView(boolean b) throws Exception {

    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    @Override
    public void clearFilter() {
        doFilter(groupFilterPanel.getFilterBean());
    }


    private STUDENT_EDUCATION getStudentEducationByStudentId(ID id){
        STUDENT_EDUCATION studentEducation = null;
        try{
            QueryModel<STUDENT_EDUCATION> qm = new QueryModel<>(STUDENT_EDUCATION.class);
            qm.addWhere("student", ECriteria.EQUAL , id);
            qm.addWhereNullAnd("child");
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
        dbGridModel.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);
        dbGridModel.getQueryModel().addWhereNotNullAnd("studyYear");
        dbGridModel.setRefreshType(ERefreshType.MANUAL);
        dbGridModel.setDeferredDelete(true);
        dbGridModel.setMultiSelect(true);
        dbGridModel.setRowNumberVisible(true);
        dbGridModel.setRowNumberWidth(50);
        dbGridModel.getColumnModel("deleted").setInGrid(false);
        FKFieldModel specialityFM =  (FKFieldModel) dbGridModel.getFormModel().getFieldModel("speciality");
        specialityFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL , false);
    }

    private List<LANGUAGE> getLanguageList(List<ID> ids){

        QueryModel<LANGUAGE> languageQM = new QueryModel<>(LANGUAGE.class);
        FromItem fi  =languageQM.addJoin(EJoin.INNER_JOIN , "id" , ENTRANT_SPECIALITY.class,"language");
        languageQM.addWhereIn(fi ,"student" , ids);
        Set<LANGUAGE> languages = new HashSet<>();
        try{
            languages.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(languageQM));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>(languages);
    }


    private void generateNew(int STUDENT_MAX){
        STUDY_YEAR studyYear = null;
        try{
            studyYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class, STUDY_YEAR.FIRST_STUDY_YEAR);
        }catch (Exception e){
            e.printStackTrace();
        }
        QueryModel<STUDENT> studentQueryModel = new QueryModel<>(STUDENT.class);
        FromItem fi = studentQueryModel.addJoin(EJoin.INNER_JOIN , "id" , STUDENT_EDUCATION.class , "student");
        FromItem fi2 = fi.addJoin(EJoin.INNER_JOIN, "studyYear" , STUDY_YEAR.class , "id");
        FromItem fi3 = studentQueryModel.addJoin(EJoin.INNER_JOIN, "id" , USERS.class, "id");

        studentQueryModel.addWhere(fi2 , "studyYear" , ECriteria.EQUAL , studyYear.getId());
        studentQueryModel.addWhereAnd(fi3 , "deleted" , ECriteria.EQUAL , false);
        studentQueryModel.addWhereAnd(fi , "status" , ECriteria.EQUAL , STUDENT_STATUS.STUDYING_ID);
        studentQueryModel.addWhereNullAnd(fi , "child" );
        studentQueryModel.addWhereAnd("category" , ECriteria.EQUAL , STUDENT_CATEGORY.STUDENT_ID);
        List students = new ArrayList<STUDENT>();
        deleteExtra();
        try{
            students.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentQueryModel));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Set<GROUPS> set = new HashSet<>();
        Set<ID> ids = new HashSet<>();
        for(STUDENT student : (List<STUDENT>)students)
        {
            if(student.getStudentEducations().iterator().next().getGroups()!=null)
            {
                set.add(student.getStudentEducations().iterator().next().getGroups());
            }
            ids.add(student.getId());
        }
        List<LANGUAGE> languages = getLanguageList(new ArrayList<>(ids));

        Map<LANGUAGE , Map<SPECIALITY, ArrayList<STUDENT>>> languageMapMap= new HashMap<>();

        for(LANGUAGE language :languages){
            languageMapMap.put(language , new HashMap<>());
        }

        QueryModel<SPECIALITY> specialityQueryModel = new QueryModel<>(SPECIALITY.class);
        FromItem specFI = specialityQueryModel.addJoin(EJoin.INNER_JOIN ,"id", STUDENT_EDUCATION.class,"speciality");
        FromItem specFI2 = specFI.addJoin(EJoin.INNER_JOIN ,"student", V_STUDENT.class,"id");

        specialityQueryModel.addWhere("deleted",ECriteria.EQUAL, false);
        specialityQueryModel.addWhereAnd(specFI2,"category",ECriteria.EQUAL, STUDENT_CATEGORY.STUDENT_ID);
        specialityQueryModel.addWhereNullAnd(specFI,"child");

        List<SPECIALITY> specialityList = new ArrayList<SPECIALITY>();
        try {
            specialityList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQueryModel));

            for(LANGUAGE language : languageMapMap.keySet()){
                for(SPECIALITY specialitySingle :specialityList )
                {
                    languageMapMap.get(language).put(specialitySingle , new ArrayList<>());
                }
            }

            for(STUDENT s : (List<STUDENT>)students)  {
                LANGUAGE lang = s.getEntrantSpecialities().iterator().next().getLanguage();
                languageMapMap.get(lang).get(getStudentEducationByStudentId(s.getId()).getSpeciality()).add(s);
            }

            for(LANGUAGE language : languageMapMap.keySet())
            {
                for(SPECIALITY speciality : languageMapMap.get(language).keySet())
                {
                    GROUPS groups = null;
                    if(languageMapMap.get(language).get(speciality).size()>0) {
                        int groupCounter = 1;
                        int studentCounter = 1;

                        if (groupCounter == 1) {
                            groups = new GROUPS();
                            groups.setName(  speciality.getCode() + "-" + language + "-" + ((Calendar.getInstance().get(Calendar.YEAR)-2000)*100+groupCounter));
                            groups.setSpeciality(speciality);
                            groups.setLanguage(language);
                            groups.setStudyYear(studyYear);
                            groups.setOrders(new Long(groupCounter));
                            groups.setDeleted(false);
                            groups.setCreated(new Date());
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(groups);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        for (STUDENT student : languageMapMap.get(language).get(speciality)) {
                            if (studentCounter > STUDENT_MAX) {
                                groupCounter++;
                                groups = new GROUPS();
                                groups.setName(speciality.getCode() + "-" + language+ "-" + ((Calendar.getInstance().get(Calendar.YEAR)-2000)*100+groupCounter ));
                                groups.setLanguage(language);
                                groups.setStudyYear(studyYear);
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
                    QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    studentEducationQM.addWhere("groups" , ECriteria.EQUAL , group.getId());
                    studentEducationQM.addWhereNullAnd("child" );

                    try{
                        List<STUDENT_EDUCATION> studentEducations = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM);
                        for(STUDENT_EDUCATION studentEducation :studentEducations){
                            studentEducation.setGroups(null);
                        }
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducations);
                    }catch (NoResultException e){
                        System.out.println(e.getMessage());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return true;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return true;
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
        if(entity instanceof GROUPS){
            ((GROUPS) entity).setCreated(new Date());
        }
        return true;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return true;
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

    private void deleteExtra(){
        try{
            Map<Integer, Object> params = new HashMap<>();
            String sql = "select * from groups where date_trunc('year',created ) = date_trunc('year' , now()) and deleted = false";
            List<GROUPS> groupsList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql ,params , GROUPS.class);

            for(GROUPS group : groupsList)
            {
                try{
                    group.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(group);
                }catch (Exception e){
                    e.printStackTrace();
                }
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


        ComboBox groupNameCB = new ComboBox();
        groupNameCB.setNullSelectionAllowed(true);
        groupNameCB.setTextInputAllowed(true);
        groupNameCB.setFilteringMode(FilteringMode.CONTAINS);
        groupNameCB.setWidth(300,Unit.PIXELS);
        QueryModel<V_GROUP> groupsQM = new QueryModel<>(V_GROUP.class);
        BeanItemContainer<V_GROUP> groupBIC = new BeanItemContainer<V_GROUP>(V_GROUP.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        groupNameCB.setContainerDataSource(groupBIC);
        groupFilterPanel.addFilterComponent("group", groupNameCB);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        groupFilterPanel.addFilterComponent("orders", tf);

    }



    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FGroupFilter sf = (FGroupFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (sf.getGroup() != null) {

            sb.append(" and ");
            params.put(i, sf.getGroup().getName());
            sb.append("name ilike '" + sf.getGroup().getName() +"%' ");
            i++;
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

                    GROUPS group = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(GROUPS.class,ID.valueOf((long) oo[0]));
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

            QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
            studentEducationQM.addWhere("groups" , ECriteria.EQUAL, g.getId());
            studentEducationQM.addWhereNullAnd("child");
            try{
                List<STUDENT_EDUCATION> studentEducations = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM);
                for(STUDENT_EDUCATION se : studentEducations){
                    se.setGroups(null);
                }
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducations);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(arrayList);
        }catch (Exception e)
        {
            CommonUtils.LOG.error("Unable to delete" + getModuleName() + ": ", e);
            Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
        }
        doFilter(groupFilterPanel.getFilterBean());
    }

    protected String getModuleName() {
        return "GroupsUtils";
    }


    protected Class<? extends Entity> getEntityClass() {
        return GROUPS.class;
    }



}
