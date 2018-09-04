package kz.halyqsoft.univercity.modules.practice;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.filter.FStudentPracticeFilter;
import kz.halyqsoft.univercity.filter.panel.InformationPracticeFilterPanel;
import kz.halyqsoft.univercity.filter.FInformationPracticeFilter;
import kz.halyqsoft.univercity.filter.panel.StudentPracticeFilterPanel;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.workflow.MyItem;
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
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.util.*;

public class PracticeView extends AbstractTaskView implements FilterPanelListener, EntityListener{
    private VerticalLayout mainVL;

    private InformationPracticeFilterPanel informationPracticeFP;
    private GridWidget informationPracticeGW;
    private DBGridModel informationPracticeGM;

    private StudentPracticeFilterPanel studentPracticeFP;
    private GridWidget studentPracticeGW;
    private DBGridModel studentPracticeGM;

    private HorizontalSplitPanel mainHSP;
    private static String FIRST_ROW = getUILocaleUtil().getEntityLabel(PRACTICE_INFORMATION.class);
    private static String SECOND_ROW = getUILocaleUtil().getEntityLabel(PRACTICE_STUDENT.class);

    public PracticeView(AbstractTask task) throws Exception {
        super(task);

        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setImmediate(true);
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(15);

    }

    @Override
    public void initView(boolean b) throws Exception {

        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList<MyItem> myItems = new ArrayList<>();
        myItems.add(new MyItem(optionHC.addItem(FIRST_ROW) ,FIRST_ROW, null));
        myItems.add(new MyItem(optionHC.addItem(SECOND_ROW) ,SECOND_ROW, null));

        TreeTable sideMenu = new TreeTable();
        sideMenu.setContainerDataSource(optionHC);
        sideMenu.setColumnReorderingAllowed(false);
        sideMenu.setMultiSelect(false);
        sideMenu.setNullSelectionAllowed(false);
        sideMenu.setSizeFull();
        sideMenu.setImmediate(true);
        sideMenu.setSelectable(true);

        sideMenu.setChildrenAllowed(FIRST_ROW, false);
        sideMenu.setChildrenAllowed(SECOND_ROW, false);

        MenuColumn firstColumn = new MenuColumn();
        sideMenu.addGeneratedColumn("first" , firstColumn);
        sideMenu.setColumnHeader("first" , getUILocaleUtil().getCaption("menu"));
        sideMenu.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mainVL.removeAllComponents();

                if(valueChangeEvent.getProperty().getValue().toString().equals(FIRST_ROW)){
                    mainVL.addComponent(informationPracticeFP);
                    mainVL.addComponent(informationPracticeGW);
                    doFilter(informationPracticeFP.getFilterBean());
                }else if(valueChangeEvent.getProperty().getValue().toString().equals(SECOND_ROW)){
                    mainVL.addComponent(studentPracticeFP);
                    mainVL.addComponent(studentPracticeGW);
                    doFilter(studentPracticeFP.getFilterBean());
                }
            }
        });
        initGridWidget();
        initFilter();



        mainHSP.setFirstComponent(sideMenu);
        mainHSP.setSecondComponent(mainVL);
        getContent().addComponent(mainHSP);
        sideMenu.select(FIRST_ROW);
    }

    private void initGridWidget(){
        informationPracticeGW = new GridWidget(PRACTICE_INFORMATION.class);
        informationPracticeGW.setImmediate(true);
        informationPracticeGW.setSizeFull();
        informationPracticeGW.removeEntityListener(informationPracticeGW);
        informationPracticeGW.showToolbar(true);
        informationPracticeGW.addEntityListener(this);
        informationPracticeGW.setMultiSelect(true);

        informationPracticeGM = (DBGridModel)informationPracticeGW.getWidgetModel();
        informationPracticeGM.setRefreshType(ERefreshType.MANUAL);
        informationPracticeGM.setMultiSelect(true);
        informationPracticeGM.setRowNumberVisible(true);
        informationPracticeGM.setRowNumberWidth(50);
        FKFieldModel groupsFM = (FKFieldModel) informationPracticeGM.getFormModel().getFieldModel("groups");
        groupsFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL,false);

        FKFieldModel employeeFM = (FKFieldModel) informationPracticeGM.getFormModel().getFieldModel("employee");
        employeeFM.getQueryModel().addJoin(EJoin.INNER_JOIN , "id", EMPLOYEE.class ,"id");
        employeeFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);


        studentPracticeGW = new GridWidget(PRACTICE_STUDENT.class);
        studentPracticeGW.setImmediate(true);
        studentPracticeGW.setSizeFull();
        studentPracticeGW.removeEntityListener(studentPracticeGW);
        studentPracticeGW.setMultiSelect(true);
        studentPracticeGW.addEntityListener(this);

        studentPracticeGM = (DBGridModel) studentPracticeGW.getWidgetModel();
        studentPracticeGM.setRefreshType(ERefreshType.MANUAL);
        studentPracticeGM.setMultiSelect(true);
        studentPracticeGM.setRowNumberVisible(true);
        studentPracticeGM.setRowNumberWidth(50);

        FKFieldModel studentFM = (FKFieldModel) studentPracticeGM.getFormModel().getFieldModel("student");
        FromItem fi1 = studentFM.getQueryModel().addJoin(EJoin.INNER_JOIN , "id", STUDENT.class ,"id");
        FromItem fi2 = fi1.addJoin( EJoin.INNER_JOIN,"id", STUDENT_EDUCATION.class, "student_id");
        FromItem fi3 = fi2.addJoin(EJoin.INNER_JOIN,"groups_id", GROUPS.class, "id");
        FromItem fi4 = fi3.addJoin(EJoin.INNER_JOIN, "id", PRACTICE_INFORMATION.class,"groups_id");
        studentFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);
        studentFM.getQueryModel().addWhereNullAnd(fi2, "chair");
        studentFM.getQueryModel().addWhereAnd(fi3, "deleted" , ECriteria.EQUAL ,false);
    }

    private void initFilter() throws Exception{

        informationPracticeFP = new InformationPracticeFilterPanel(new FInformationPracticeFilter());
        informationPracticeFP.addFilterPanelListener(this);
        informationPracticeFP.setImmediate(true);

        ComboBox groupsCB = new ComboBox();
        groupsCB.setNullSelectionAllowed(true);
        groupsCB.setTextInputAllowed(true);
        groupsCB.setFilteringMode(FilteringMode.CONTAINS);
        groupsCB.setWidth(200, Unit.PIXELS);
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        groupsQM.addWhere("deleted" , ECriteria.EQUAL,false);
        BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        groupsCB.setContainerDataSource(groupsBIC);
        informationPracticeFP.addFilterComponent("groups", groupsCB);

        ComboBox employeeCB = new ComboBox();
        employeeCB.setNullSelectionAllowed(true);
        employeeCB.setTextInputAllowed(true);
        employeeCB.setFilteringMode(FilteringMode.CONTAINS);
        employeeCB.setWidth(200, Unit.PIXELS);
        QueryModel<USERS> employeeQM = new QueryModel<>(USERS.class);
        employeeQM.addJoin(EJoin.INNER_JOIN, "id", EMPLOYEE.class , "id");
        employeeQM.addWhere("deleted" , ECriteria.EQUAL, false);

        BeanItemContainer<USERS> employeeBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(employeeQM));
        employeeCB.setContainerDataSource(employeeBIC);
        informationPracticeFP.addFilterComponent("employee", employeeCB);


        ComboBox studyYearCB = new ComboBox();
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.CONTAINS);
        studyYearCB.setWidth(200, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);

        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB.setContainerDataSource(studyYearBIC);
        informationPracticeFP.addFilterComponent("studyYear", studyYearCB);


        ComboBox entranceYearCB = new ComboBox();
        entranceYearCB.setNullSelectionAllowed(true);
        entranceYearCB.setTextInputAllowed(true);
        entranceYearCB.setFilteringMode(FilteringMode.CONTAINS);
        entranceYearCB.setWidth(200, Unit.PIXELS);
        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);

        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(entranceYearQM));
        entranceYearCB.setContainerDataSource(entranceYearBIC);
        informationPracticeFP.addFilterComponent("entranceYear", entranceYearCB);


        DateField createdDF = new DateField();
        informationPracticeFP.addFilterComponent("created", createdDF);


        studentPracticeFP = new StudentPracticeFilterPanel(new FStudentPracticeFilter());
        studentPracticeFP.addFilterPanelListener(this);
        studentPracticeFP.setImmediate(true);


        ComboBox studentCB = new ComboBox();
        studentCB.setNullSelectionAllowed(true);
        studentCB.setTextInputAllowed(true);
        studentCB.setFilteringMode(FilteringMode.CONTAINS);
        studentCB.setWidth(200, Unit.PIXELS);
        QueryModel<USERS> studentQM = new QueryModel<>(USERS.class);
        studentQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT.class , "id");
        studentQM.addWhere("deleted" , ECriteria.EQUAL, false);

        BeanItemContainer<USERS> studentBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentQM));
        studentCB.setContainerDataSource(studentBIC);
        studentPracticeFP.addFilterComponent("student", studentCB);

        ComboBox organizationCB = new ComboBox();
        organizationCB.setNullSelectionAllowed(true);
        organizationCB.setTextInputAllowed(true);
        organizationCB.setFilteringMode(FilteringMode.CONTAINS);
        organizationCB.setWidth(200,Unit.PIXELS);
        QueryModel<ORGANIZATION> organizationQM = new QueryModel<>(ORGANIZATION.class);

        BeanItemContainer<ORGANIZATION> organizationBIC = new BeanItemContainer<ORGANIZATION>(ORGANIZATION.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(organizationQM));
        organizationCB.setContainerDataSource(organizationBIC);
        studentPracticeFP.addFilterComponent("organization", organizationCB);

        DateField comeInDF = new DateField();
        studentPracticeFP.addFilterComponent("comeInDate", comeInDF);

        DateField comeOutDF = new DateField();
        studentPracticeFP.addFilterComponent("comeOutDate", comeOutDF);
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        if(abstractFilterBean instanceof FInformationPracticeFilter){
            FInformationPracticeFilter informationPracticeFilter = (FInformationPracticeFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (informationPracticeFilter.getEmployee() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getEmployee().getId().getId());
                sb.append("pi.teacher_id = ?" + i++);

            }

            if (informationPracticeFilter.getGroups()!=null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getGroups().getId().getId());
                sb.append("g.id = ?" + i++);

            }

            if (informationPracticeFilter.getStudyYear() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getStudyYear().getId().getId());
                sb.append(" g.study_year_id = ?" + i++);

            }

            if (informationPracticeFilter.getCreated() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  pi.created ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(informationPracticeFilter.getCreated())+ "') ");

            }

            if (informationPracticeFilter.getEntranceYear() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" pi.entrance_year_id = " + informationPracticeFilter.getEntranceYear().getId().getId().longValue()+" ");

            }

            List<PRACTICE_INFORMATION> list = new ArrayList<>();

            if(i > 1){
                sb.insert(0, " WHERE  ");
            }
            String sql = "select pi.* from practice_information pi\n" +
                    "  INNER JOIN groups g\n" +
                    "    ON pi.groups_id = g.id\n"
                    + sb.toString();
            try {

                List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        PRACTICE_INFORMATION practiceInformation = new PRACTICE_INFORMATION();
                        practiceInformation.setId(ID.valueOf((Long)oo[0]));

                        try{
                            practiceInformation.setGroups(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(GROUPS.class , ID.valueOf((Long)oo[1])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            practiceInformation.setEmployee(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class , ID.valueOf((Long)oo[2])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        practiceInformation.setCreated((Date)oo[3]);

                        try{
                            practiceInformation.setEntranceYear(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANCE_YEAR.class , ID.valueOf((Long)oo[4])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        list.add(practiceInformation);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
            }
            refresh(list);
        }else if(abstractFilterBean instanceof FStudentPracticeFilter){
            FStudentPracticeFilter fStudentPracticeFilter = (FStudentPracticeFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (fStudentPracticeFilter.getStudent() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, fStudentPracticeFilter.getStudent().getId().getId());
                sb.append("ps.student_id = ?" + i++);

            }

            if (fStudentPracticeFilter.getOrganization()!=null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, fStudentPracticeFilter.getOrganization().getId().getId());
                sb.append("ps.organization_id = ?" + i++);

            }

            if (fStudentPracticeFilter.getComeInDate() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  ps.come_in ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(fStudentPracticeFilter.getComeInDate())+ "') ");

            }

            if (fStudentPracticeFilter.getComeOutDate() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  ps.come_out ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(fStudentPracticeFilter.getComeOutDate())+ "') ");

            }

            List<PRACTICE_STUDENT> list = new ArrayList<>();

            if(i > 1){
                sb.insert(0, " WHERE  ");
            }
            String sql = " select ps.* from practice_student ps where true "
                    + sb.toString();
            try {

                List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        PRACTICE_STUDENT practiceStudent = new PRACTICE_STUDENT();
                        practiceStudent.setId(ID.valueOf((Long)oo[0]));

                        try{
                            practiceStudent.setStudent(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class , ID.valueOf((Long)oo[1])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            practiceStudent.setOrganization(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ORGANIZATION.class , ID.valueOf((Long)oo[2])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        practiceStudent.setComeInDate((Date)oo[3]);
                        practiceStudent.setComeOutDate((Date)oo[4]);

                        list.add(practiceStudent);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
            }
            refresh(list);
        }
    }

    @Override
    public void clearFilter() {

    }

    private void refresh(List list) {
        if(list.size()>0) {
            if (list.get(0) instanceof PRACTICE_INFORMATION) {
                informationPracticeGM.setEntities(list);
                try {
                    informationPracticeGW.refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh practice information list", ex);
                }
            } else if (list.get(0) instanceof PRACTICE_STUDENT) {
                studentPracticeGM.setEntities(list);
                try {
                    studentPracticeGW.refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh practice student list", ex);
                }
            }
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if(source.equals(informationPracticeGW) ){
            PRACTICE_INFORMATION pi = ((PRACTICE_INFORMATION)e);
            if(isNew){
                pi.setCreated(new Date());
            }
            QueryModel<PRACTICE_INFORMATION> practiceInformationQM = new QueryModel<>(PRACTICE_INFORMATION.class);
            practiceInformationQM.addWhere("groups", ECriteria.EQUAL,pi.getGroups().getId());
            practiceInformationQM.addWhereAnd("entranceYear", ECriteria.EQUAL,pi.getEntranceYear().getId());
            try{
                if(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(practiceInformationQM).size()>0){
                    Message.showError(getUILocaleUtil().getMessage("practice.already.exists.for.this.year"));
                    return false;
                }
            }catch (NoResultException nre){
                System.out.println("GOOD");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return super.preSave(source, e, isNew, buttonId);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        super.handleEntityEvent(ev);
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
        super.onDelete(source, entities, buttonId);
        refresh(source);
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        super.onCreate(source, e, buttonId);
        refresh(source);
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
        super.onRefresh(source, entities);
    }



    public void refresh(Object source){
        if(source.equals(informationPracticeGW)){
            doFilter(informationPracticeFP.getFilterBean());
        }else if(source.equals(studentPracticeGW)){
            doFilter(studentPracticeFP.getFilterBean());
        }
    }
}
