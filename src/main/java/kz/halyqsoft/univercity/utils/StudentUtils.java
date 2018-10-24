package kz.halyqsoft.univercity.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.modules.student.StudentOrApplicantView;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

/**
 * @author Omarbek
 * @created on 01.06.2018
 */
public abstract class StudentUtils extends AbstractFormWidgetView implements EntityListener, FilterPanelListener {

    private StudentFilterPanel filterPanel;
    private StudentOrApplicantView studentOrApplicantView;
    private HorizontalLayout buttonsHL;
    private int categoryType;
    private boolean forDorm;

    protected GridWidget studentGW;

    public void setFilterPanel(StudentFilterPanel filterPanel) {
        this.filterPanel = filterPanel;
    }

    public StudentFilterPanel createStudentFilterPanel() throws Exception {
        StudentFilterPanel studentFilterPanel = new StudentFilterPanel(new FStudentFilter());
        studentFilterPanel = new StudentFilterPanel(new FStudentFilter());
        studentFilterPanel.addFilterPanelListener(this);
        studentFilterPanel.setImmediate(true);

        setBackButtonVisible(false);

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
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        BeanItemContainer<GROUPS> dgroupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        cb.setContainerDataSource(dgroupsBIC);
        studentFilterPanel.addFilterComponent("group", cb);


        return studentFilterPanel;
    }

    public StudentUtils(int categoryType, boolean forDorm) throws Exception {
        super();
        this.categoryType = categoryType;
        this.forDorm = forDorm;

        filterPanel = createStudentFilterPanel();

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        Button saveToCatalog = new Button(getUILocaleUtil().getCaption("saveToCatalog"));
        saveToCatalog.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<VStudent> students = new ArrayList<>();

                for (Entity s : studentGW.getAllEntities()) {
                    students.add((VStudent) s);
                }
                if (students.size() > 0) {
                    WindowUtils saveDialog = new WindowUtils() {
                        @Override
                        protected String createTitle() {
                            return getUILocaleUtil().getEntityLabel(CATALOG.class);
                        }

                        @Override
                        protected void refresh() {

                        }

                        @Override
                        protected VerticalLayout getVerticalLayout() {

                            VerticalLayout vl = new VerticalLayout();
                            Label descriptionL = new Label(getUILocaleUtil().getEntityFieldLabel(CATALOG.class, "description"));
                            TextArea descriptionTA = new TextArea();
                            descriptionTA.setWidth(100, Unit.PERCENTAGE);
                            descriptionTA.setRequired(true);
                            Label nameL = new Label(getUILocaleUtil().getEntityFieldLabel(CATALOG.class, "name"));

                            TextField nameTF = new TextField();
                            nameTF.setWidth(100, Unit.PERCENTAGE);
                            nameTF.setRequired(true);

                            Button saveBtn = CommonUtils.createSaveButton();
                            saveBtn.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {

                                    String name = nameTF.getValue();
                                    String description = descriptionTA.getValue();
                                    String jsonInString = "";


                                    if (FieldValidator.isNotEmpty(name) && FieldValidator.isNotEmpty(description)) {

                                        if (nameTF.getValue().toCharArray()[0] != '$') {
                                            name = name.trim();
                                            name = name.replaceAll("\\s+","");
                                            name = "$" + name;
                                        }

                                        ObjectMapper mapper = new ObjectMapper();
                                        try {
                                            jsonInString = mapper.writeValueAsString(students);
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                            Message.showError(e.getMessage());
                                            return;
                                        }

                                        CATALOG catalog = new CATALOG();
                                        catalog.setCreated(new Date());
                                        catalog.setDescription(description);
                                        catalog.setName(name);
                                        if (jsonInString.toCharArray()[0] == '[' && jsonInString.toCharArray()[jsonInString.toCharArray().length - 1] == ']') {
                                            jsonInString = jsonInString.substring(1, jsonInString.length() - 1);
                                        }
                                        catalog.setValue(jsonInString);


                                        try {
                                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(catalog);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Message.showError(e.getMessage());
                                            return;
                                        }

                                        CommonUtils.showSavedNotification();
                                        close();
                                    } else {
                                        Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));
                                    }

                                }
                            });

                            vl.addComponent(nameL);
                            vl.addComponent(nameTF);
                            vl.addComponent(descriptionL);
                            vl.addComponent(descriptionTA);

                            vl.addComponent(saveBtn);
                            vl.setComponentAlignment(saveBtn, Alignment.BOTTOM_CENTER);
                            return vl;
                        }
                    };
                    saveDialog.init(400, 300);
                    saveDialog.setHeightUndefined();
                    saveDialog.getCloseButton().setVisible(false);

                } else {
                    Message.showError(getUILocaleUtil().getMessage("no.data"));
                }
            }
        });

        getContent().addComponent(saveToCatalog);

        studentGW = new GridWidget(VStudent.class);
        studentGW.addEntityListener(this);
        //studentGW.showToolbar(false);

        DBGridModel studentGM = (DBGridModel) studentGW.getWidgetModel();
        studentGM.setReadOnly(true);
        studentGM.setRefreshType(ERefreshType.MANUAL);
        studentGM.setTitleVisible(false);
        studentGM.setMultiSelect(false);
        studentGM.setRowNumberVisible(true);
        studentGM.setRowNumberWidth(50);
        studentGM.getColumnModel("category").setInGrid(false);
        studentGM.getColumnModel("lockReason").setInGrid(false);

        doFilter(filterPanel.getFilterBean());

        getContent().addComponent(studentGW);
        getContent().setComponentAlignment(studentGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "StudentUtils";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        if (categoryType == 3) {
            return getUILocaleUtil().getCaption("students");
        }
        return getUILocaleUtil().getCaption("applicants" +
                "");
    }

    @Override
    public void initView(boolean b) throws Exception {
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(studentGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                List<Entity> selectedList = ev.getEntities();
                if (!selectedList.isEmpty()) {
                    onEdit(ev.getSource(), selectedList.get(0), 2);
                }
            }
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return openStudentEdit(source, e, false);
    }

    private boolean openStudentEdit(Object source, Entity e, boolean readOnly) {
        if (source.equals(studentGW)) {
            FormModel fm = new FormModel(STUDENT.class);
            fm.setReadOnly(readOnly);
            fm.setTitleVisible(false);
            try {
                fm.loadEntity(e.getId());

//                getContent().removeComponent(buttonsHL);
                getContent().removeComponent(filterPanel);
                getContent().removeComponent(studentGW);
                getContent().addComponent(new StudentEdit(fm, getContent(), studentOrApplicantView));

                return false;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to open studentEdit", ex);
            }
        }

        return true;
    }


    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return openStudentEdit(source, e, true);
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(studentGW)) {
            List<STUDENT> mergeList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    STUDENT s = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(STUDENT.class, e.getId());
                    s.setDeleted(true);
                    mergeList.add(s);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete the student", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                studentGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete the students", ex);
            }
            return false;
        }

        return true;
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FStudentFilter sf = (FStudentFilter) filterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        StringBuilder sb = new StringBuilder();
        if (forDorm) {
            sb.append(" and stu.need_dorm = true ");
        }
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append(" and lower(stu.user_code) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append(" and stu.FIRST_NAME ilike '");
            sb.append(sf.getFirstname().trim());
            sb.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append("  ");
            }
            sb.append("  and stu.LAST_NAME ilike '");
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
            sb.append(" and stu.groups_id = ?" + i++);
        }

        List<VStudent> list = new ArrayList<>();
        sb.insert(0, " where stu.category_id = " + categoryType);
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
                " ORDER BY FIO";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                    sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudent vs = new VStudent();
                    vs.setId(ID.valueOf((long) oo[0]));
                    vs.setCode((String) oo[1]);
                    vs.setFio((String) oo[2]);
                    vs.setStatus((String) oo[3]);
                    vs.setFaculty((String) oo[4]);
                    vs.setSpecialty((String) oo[5]);
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }

        ((DBGridModel) studentGW.getWidgetModel()).setEntities(list);
        try {
            studentGW.refresh();
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

    public GridWidget getStudentGW() {
        return studentGW;
    }

    public void setStudentOrApplicantView(StudentOrApplicantView studentOrApplicantView) {
        this.studentOrApplicantView = studentOrApplicantView;
    }

    public HorizontalLayout getButtonsHL() {
        return buttonsHL;
    }

    public void setButtonsHL(HorizontalLayout buttonsHL) {
        this.buttonsHL = buttonsHL;
    }
}
