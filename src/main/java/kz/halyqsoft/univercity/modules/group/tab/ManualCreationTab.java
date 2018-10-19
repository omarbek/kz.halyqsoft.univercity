package kz.halyqsoft.univercity.modules.group.tab;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.modules.group.tab.dialogs.GroupEditDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
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
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

public class ManualCreationTab extends AbstractCommonView implements EntityListener , FilterPanelListener{

    private StudentFilterPanel filterPanel;

    private VerticalLayout mainVL;
    private GridWidget studentGroupsGW;
    private String title;
    public ManualCreationTab(String title) {
        this.title = title;

        mainVL = new VerticalLayout();

        try{
            filterPanel = createStudentFilterPanel();

            mainVL.addComponent(filterPanel);
            mainVL.setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        }catch (Exception e ){
            e.printStackTrace();
        }

        studentGroupsGW = new GridWidget(V_STUDENT.class);
        studentGroupsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        studentGroupsGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        studentGroupsGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        studentGroupsGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        studentGroupsGW.addEntityListener(this);

        DBGridModel studentGM = (DBGridModel) studentGroupsGW.getWidgetModel();
        QueryModel studentQM = studentGM.getQueryModel();
        studentQM.addWhere("category" ,ECriteria.EQUAL, STUDENT_CATEGORY.STUDENT_ID);
        studentQM.addWhere("studentStatus" ,ECriteria.EQUAL, STUDENT_STATUS.STUDYING_ID);

        studentGM.setRefreshType(ERefreshType.MANUAL);
        studentGM.setMultiSelect(false);
        studentGM.setRowNumberVisible(true);
        studentGM.setRowNumberWidth(100);
        studentGM.getColumnModel("category").setInGrid(false);

        FormModel formModel = studentGM.getFormModel();

        formModel.getFieldModel("userCode").setInEdit(false);
        formModel.getFieldModel("firstName").setInEdit(false);
        formModel.getFieldModel("lastName").setInEdit(false);
        formModel.getFieldModel("middleName").setInEdit(false);
        formModel.getFieldModel("faculty").setInEdit(false);
        formModel.getFieldModel("facultyName").setInEdit(false);
        formModel.getFieldModel("chair").setInEdit(false);
        formModel.getFieldModel("chairName").setInEdit(false);
        formModel.getFieldModel("speciality").setInEdit(false);
        formModel.getFieldModel("studyYear").setInEdit(false);
        formModel.getFieldModel("educationType").setInEdit(false);
        formModel.getFieldModel("educationTypeName").setInEdit(false);
        formModel.getFieldModel("studentStatus").setInEdit(false);
        formModel.getFieldModel("card").setInEdit(false);
        formModel.getFieldModel("groupName").setInEdit(false);

        doFilter(filterPanel.getFilterBean());

        mainVL.addComponent(studentGroupsGW);
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
    public void handleEntityEvent(EntityEvent entityEvent) {
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {

        V_STUDENT vStudent = (V_STUDENT)entity;
        QueryModel<STUDENT_EDUCATION> studentQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentQM.addWhere("student" , ECriteria.EQUAL, vStudent.getId());
        studentQM.addWhereNullAnd("child");
        STUDENT_EDUCATION studentEducation = null;
        try{
            studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentQM);

            try{
                GroupEditDialog groupEditDialog = new GroupEditDialog(getViewName(),studentEducation, this);
            } catch (Exception e){
                e.printStackTrace();
            }
            doFilter(filterPanel.getFilterBean());
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
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

    public void setFilterPanel(StudentFilterPanel filterPanel) {
        this.filterPanel = filterPanel;
    }


    public StudentFilterPanel createStudentFilterPanel() throws Exception {
        StudentFilterPanel studentFilterPanel = new StudentFilterPanel(new FStudentFilter());
        studentFilterPanel.addFilterPanelListener(this);
        studentFilterPanel.setImmediate(true);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentFilterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentFilterPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentFilterPanel.addFilterComponent("lastname", tf);

        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        FromItem userFI = cardQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQM.addWhere(userFI, "typeIndex", ECriteria.EQUAL, 2);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cb.setContainerDataSource(cardBIC);
        studentFilterPanel.addFilterComponent("card", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        List<ID> idList = new ArrayList<ID>(4);
        idList.add(ID.valueOf(1));
        idList.add(ID.valueOf(2));
        idList.add(ID.valueOf(3));
        idList.add(ID.valueOf(5));
        QueryModel<STUDENT_STATUS> ssQM = new QueryModel<>(STUDENT_STATUS.class);
        ssQM.addWhereIn("id", idList);
        ssQM.addOrder("id");
        BeanItemContainer<STUDENT_STATUS> ssBIC = new BeanItemContainer<>(STUDENT_STATUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ssQM));
        cb.setContainerDataSource(ssBIC);
        studentFilterPanel.addFilterComponent("studentStatus", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setPageLength(0);
        cb.setWidth(250, Unit.PIXELS);
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
        facultyQM.addWhereNull("parent");
        facultyQM.addWhereAnd("deleted", Boolean.FALSE);
        facultyQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM));
        cb.setContainerDataSource(facultyBIC);
        studentFilterPanel.addFilterComponent("faculty", cb);

        ComboBox specialtyCB = new ComboBox();
        specialtyCB.setNullSelectionAllowed(true);
        specialtyCB.setTextInputAllowed(true);
        specialtyCB.setFilteringMode(FilteringMode.CONTAINS);
        specialtyCB.setPageLength(0);
        specialtyCB.setWidth(250, Unit.PIXELS);
        cb.addValueChangeListener(new FacultyChangeListener(specialtyCB));
        studentFilterPanel.addFilterComponent("speciality", specialtyCB);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(70, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        studyYearQM.addWhere("studyYear", ECriteria.LESS_EQUAL, 7);
        studyYearQM.addOrder("studyYear");
        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        cb.setContainerDataSource(studyYearBIC);
        studentFilterPanel.addFilterComponent("studyYear", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        studentFilterPanel.addFilterComponent("educationType", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_DIPLOMA_TYPE> diplomaTypeQM = new QueryModel<>(STUDENT_DIPLOMA_TYPE.class);
        BeanItemContainer<STUDENT_DIPLOMA_TYPE> diplomaTypeBIC = new BeanItemContainer<>(STUDENT_DIPLOMA_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(diplomaTypeQM));
        cb.setContainerDataSource(diplomaTypeBIC);
        studentFilterPanel.addFilterComponent("studentDiplomaType", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        groupsQM.addWhere("deleted",false);
        BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        cb.setContainerDataSource(groupsBIC);
        studentFilterPanel.addFilterComponent("group", cb);


        return studentFilterPanel;
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FStudentFilter sf = (FStudentFilter) filterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        StringBuilder sb = new StringBuilder();

        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append(" and lower(stu.user_code) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            sb.append(" and stu.FIRST_NAME ilike '");
            sb.append(sf.getFirstname().trim());
            sb.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            sb.append(" and stu.LAST_NAME ilike '");
            sb.append(sf.getLastname().trim());
            sb.append("%'");
        }
        if (sf.getCard() != null) {
            params.put(i, sf.getCard().getId().getId());
            sb.append(" and stu.card_id = ?");
            sb.append(i++);
        }
        if (sf.getStudentStatus() != null) {
            params.put(i, sf.getStudentStatus().getId().getId());
            sb.append(" and stu.student_status_id = ?" + i++);
        }
        if (sf.getFaculty() != null) {
            params.put(i, sf.getFaculty().getId().getId());
            sb.append(" and stu.faculty_id = ?" + i++);
        }
        if (sf.getSpeciality() != null) {
            params.put(i, sf.getSpeciality().getId().getId());
            sb.append(" and stu.speciality_id = ?" + i++);
        }
        if (sf.getStudyYear() != null) {
            params.put(i, sf.getStudyYear().getId().getId());
            sb.append(" and stu.study_year_id = ?" + i++);
        }
        if (sf.getEducationType() != null) {
            params.put(i, sf.getEducationType().getId().getId());
            sb.append(" and stu.education_type_id = ?" + i++);
        }
        if (sf.getStudentDiplomaType() != null) {
            params.put(i, sf.getStudentDiplomaType().getId().getId());
            sb.append(" and st.diploma_type_id = ?" + i++);
        }
        if (sf.getGroup() != null) {
            params.put(i, sf.getGroup().getId().getId());
            sb.append(" and stu.groups_id = ?"+i++ );
        }


        List<V_STUDENT> list = new ArrayList<>();
        sb.insert(0, " where stu.category_id = " + STUDENT_CATEGORY.STUDENT_ID);
        String sql = "SELECT " +
                "  stu.ID, " +
                "  stu.user_code                                                                        CODE, " +
                "  trim(stu.LAST_NAME || ' ' || stu.FIRST_NAME || ' ' || coalesce(stu.MIDDLE_NAME, '')) FIO, " +
                "  stu.student_status_name                                                              STATUS_NAME, " +
                "  stu.faculty_short_name                                                               FACULTY, " +
                "  stu.speciality_name                                                                  SPEC_NAME " +
                "FROM V_STUDENT stu " +
                "  INNER JOIN student st on st.id = stu.id\n" +
                "  INNER JOIN student_diploma_type t on st.diploma_type_id = t.id" +
                sb.toString() +
                " ORDER BY FIO" +
                " limit 60";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                    sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    V_STUDENT vs = null;
                    vs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_STUDENT.class,ID.valueOf((long) oo[0]));
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }

        ((DBGridModel) studentGroupsGW.getWidgetModel()).setEntities(list);
        try {
            studentGroupsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh student grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        doFilter(filterPanel.getFilterBean());
    }

    public StudentFilterPanel getFilterPanel() {
        return filterPanel;
    }


}
