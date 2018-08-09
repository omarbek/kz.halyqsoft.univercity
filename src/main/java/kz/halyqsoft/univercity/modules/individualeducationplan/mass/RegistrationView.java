package kz.halyqsoft.univercity.modules.individualeducationplan.mass;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT_SELECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.*;

/**
 * @author Omarbek Dinassil
 * @created Nov 3, 2016 11:44:52 AM
 */
@SuppressWarnings({"serial"})
public class RegistrationView extends AbstractTaskView {

    private ComboBox studyYearCB;
    private ComboBox semesterPeriodCB;
    private TextField subjectNameTF;
    private Grid subjectGrid;
    private TextField studentCodeTF;
    private TextField studentLastNameTF;
    private TextField studentFirstNameTF;
    private ComboBox facultyCB;
    private ComboBox specialityCB;
    private ComboBox studentStudyYearCB;
    private ComboBox educationTypeCB;
    private ComboBox schoolEducationLanguage;
    private Grid foundStudentGrid;
    private Grid subjectStudentGrid;

    public RegistrationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        HorizontalLayout subjectFilterHL = new HorizontalLayout();
        subjectFilterHL.setSpacing(true);
        subjectFilterHL.setCaption(getUILocaleUtil().getCaption("search.subject"));

        Label l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("study.year.1"));
        subjectFilterHL.addComponent(l);

        SEMESTER_DATA sd = CommonUtils.getCurrentSemesterData();
        QueryModel<ENTRANCE_YEAR> studyYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
        studyYearQM.addWhere("beginYear", ECriteria.LESS_EQUAL, sd.getYear().getBeginYear() + 1);
        studyYearQM.addWhereAnd("endYear", ECriteria.LESS_EQUAL, sd.getYear().getEndYear() + 1);
        studyYearQM.addOrderDesc("beginYear");
        BeanItemContainer<ENTRANCE_YEAR> studyYearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB = new ComboBox();
        studyYearCB.setWidth(100, Unit.PIXELS);
        studyYearCB.setContainerDataSource(studyYearBIC);
        studyYearCB.setImmediate(true);
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(false);
        studyYearCB.setFilteringMode(FilteringMode.OFF);
        subjectFilterHL.addComponent(studyYearCB);

        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("semester"));
        subjectFilterHL.addComponent(l);

        QueryModel<SEMESTER_PERIOD> semesterPeriodQM = new QueryModel<>(SEMESTER_PERIOD.class);
        semesterPeriodQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(2));
        semesterPeriodQM.addOrder("id");
        BeanItemContainer<SEMESTER_PERIOD> semesterPeriodBIC = new BeanItemContainer<>(SEMESTER_PERIOD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterPeriodQM));
        semesterPeriodCB = new ComboBox();
        semesterPeriodCB.setWidth(100, Unit.PIXELS);
        semesterPeriodCB.setContainerDataSource(semesterPeriodBIC);
        semesterPeriodCB.setImmediate(true);
        semesterPeriodCB.setNullSelectionAllowed(true);
        semesterPeriodCB.setTextInputAllowed(false);
        semesterPeriodCB.setFilteringMode(FilteringMode.OFF);
        subjectFilterHL.addComponent(semesterPeriodCB);

        l = new Label();
        l.setSizeUndefined();
        l.addStyleName("bold");
        l.setValue(getUILocaleUtil().getCaption("course.name"));
        subjectFilterHL.addComponent(l);

        subjectNameTF = new TextField();
        subjectNameTF.setWidth(300, Unit.PIXELS);
        subjectNameTF.setNullSettingAllowed(true);
        subjectNameTF.setNullRepresentation("");
        subjectFilterHL.addComponent(subjectNameTF);

        getContent().addComponent(subjectFilterHL);
        getContent().setComponentAlignment(subjectFilterHL, Alignment.TOP_CENTER);

        HorizontalLayout subjectFilterButtonHL = new HorizontalLayout();
        subjectFilterButtonHL.setSpacing(true);

        Button subjectSearch = new Button();
        subjectSearch.setCaption(getUILocaleUtil().getCaption("search"));
        subjectSearch.setIcon(new ThemeResource("img/button/search.png"));
        subjectSearch.addStyleName("search");
        subjectSearch.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                refreshSubjectGrid();
            }
        });

        subjectFilterButtonHL.addComponent(subjectSearch);

        Button subjectFilterClear = new Button();
        subjectFilterClear.setCaption(getUILocaleUtil().getCaption("clear"));
        subjectFilterClear.setIcon(new ThemeResource("img/button/erase.png"));
        subjectFilterClear.addStyleName("clear");
        subjectFilterClear.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                BeanItemContainer<V_SEMESTER_SUBJECT> bic = new BeanItemContainer<>(V_SEMESTER_SUBJECT.class,
                        new ArrayList<>());
                subjectGrid.setContainerDataSource(bic);
                subjectNameTF.setValue(null);
                studyYearCB.setValue(null);
                semesterPeriodCB.setValue(null);
            }
        });
        subjectFilterButtonHL.addComponent(subjectFilterClear);

        getContent().addComponent(subjectFilterButtonHL);
        getContent().setComponentAlignment(subjectFilterButtonHL, Alignment.TOP_CENTER);

        subjectGrid = new Grid();
        subjectGrid.setCaption(getUILocaleUtil().getCaption("found.subjects"));
        subjectGrid.setWidth(100, Unit.PERCENTAGE);
        subjectGrid.setColumns("subjectName", "chairName", "levelName", "cycleShortName", "credit",
                "formula", "controlTypeName");
        subjectGrid.getColumn("subjectName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_SUBJECT_SELECT.class,
                "nameRU")).setWidthUndefined();
        subjectGrid.getColumn("chairName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_SUBJECT_SELECT.class,
                "chair")).setWidthUndefined();
        subjectGrid.getColumn("levelName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_SUBJECT_SELECT.class,
                "level")).setWidth(110);
        subjectGrid.getColumn("cycleShortName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_SUBJECT_SELECT.class,
                "subjectCycle")).setWidth(80);
        subjectGrid.getColumn("credit").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_SUBJECT_SELECT.class,
                "credit")).setWidth(80);
        subjectGrid.getColumn("formula").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(SUBJECT.class,
                "academicFormula")).setWidth(80);
        subjectGrid.getColumn("controlTypeName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                V_SUBJECT_SELECT.class, "controlTypeName")).setWidth(80);
        subjectGrid.setSelectionMode(SelectionMode.SINGLE);
        subjectGrid.setHeightMode(HeightMode.ROW);
        subjectGrid.setHeightByRows(8);
        subjectGrid.addSelectionListener(new SubjectSelectListener());
        getContent().addComponent(subjectGrid);
        getContent().setComponentAlignment(subjectGrid, Alignment.TOP_CENTER);

        GridLayout studentFilterGL = new GridLayout();
        studentFilterGL.setSpacing(true);
        studentFilterGL.setCaption(getUILocaleUtil().getCaption("search.student"));
        studentFilterGL.setColumns(4);
        studentFilterGL.setRows(2);

        studentCodeTF = new TextField();
        studentCodeTF.setCaption("ID");
        studentCodeTF.setNullSettingAllowed(true);
        studentCodeTF.setNullRepresentation("");
        studentFilterGL.addComponent(studentCodeTF);

        studentLastNameTF = new TextField();
        studentLastNameTF.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class, "lastName"));
        studentLastNameTF.setNullSettingAllowed(true);
        studentLastNameTF.setNullRepresentation("");
        studentFilterGL.addComponent(studentLastNameTF);

        studentFirstNameTF = new TextField();
        studentFirstNameTF.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class, "firstName"));
        studentFirstNameTF.setNullSettingAllowed(true);
        studentFirstNameTF.setNullRepresentation("");
        studentFilterGL.addComponent(studentFirstNameTF);

        //		QueryModel<STUDENT_STATUS> studentStatusQM = new QueryModel<STUDENT_STATUS>(STUDENT_STATUS.class);
        //		List<ID> idList = new ArrayList<ID>();
        //		idList.add(ID.valueOf(1));
        //		idList.add(ID.valueOf(2));
        //		idList.add(ID.valueOf(3));
        //		idList.add(ID.valueOf(5));
        //		studentStatusQM.addWhereIn("id", idList);
        //		BeanItemContainer<STUDENT_STATUS> studentStatusBIC = new BeanItemContainer<STUDENT_STATUS>(STUDENT_STATUS.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentStatusQM));
        //		studentStatusCB = new ComboBox();
        //		studentStatusCB.setCaption(getUILocaleUtil().getEntityFieldLabel(VTStudent.class, "status"));
        //		studentStatusCB.setContainerDataSource(studentStatusBIC);
        //		studentStatusCB.setImmediate(true);
        //		studentStatusCB.setNullSelectionAllowed(true);
        //		studentStatusCB.setTextInputAllowed(false);
        //		studentStatusCB.setFilteringMode(FilteringMode.OFF);
        //		studentFilterGL.addComponent(studentStatusCB);

        QueryModel<LANGUAGE> languageQM = new QueryModel<>(LANGUAGE.class);
        BeanItemContainer<LANGUAGE> languageBIC = new BeanItemContainer<>(LANGUAGE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(languageQM));
        schoolEducationLanguage = new ComboBox();
        schoolEducationLanguage.setCaption(getUILocaleUtil().getCaption("student.register.school.lang"));
        schoolEducationLanguage.setContainerDataSource(languageBIC);
        schoolEducationLanguage.setImmediate(true);
        schoolEducationLanguage.setNullSelectionAllowed(true);
        schoolEducationLanguage.setTextInputAllowed(false);
        schoolEducationLanguage.setFilteringMode(FilteringMode.OFF);
        studentFilterGL.addComponent(schoolEducationLanguage);

        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
//        facultyQM.addWhere("type", ECriteria.EQUAL, DEPARTMENT_TYPE.FACULTY_ID);//TODO
        facultyQM.addWhereAnd("deleted", Boolean.FALSE);
        facultyQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM));
        facultyCB = new ComboBox();
        facultyCB.setCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class, "faculty"));
        facultyCB.setContainerDataSource(facultyBIC);
        facultyCB.setImmediate(true);
        facultyCB.setNullSelectionAllowed(true);
        facultyCB.setTextInputAllowed(false);
        facultyCB.setFilteringMode(FilteringMode.OFF);
        facultyCB.setPageLength(0);
        studentFilterGL.addComponent(facultyCB);

        specialityCB = new ComboBox();
        specialityCB.setCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class, "specialty"));
        specialityCB.setImmediate(true);
        specialityCB.setNullSelectionAllowed(true);
        specialityCB.setTextInputAllowed(false);
        specialityCB.setFilteringMode(FilteringMode.OFF);
        specialityCB.setPageLength(0);
        studentFilterGL.addComponent(specialityCB);

        facultyCB.addValueChangeListener(new FacultyChangeListener(specialityCB));

        QueryModel<STUDY_YEAR> studentStudyYearQM = new QueryModel<>(STUDY_YEAR.class);
        studentStudyYearQM.addOrder("studyYear");
        BeanItemContainer<STUDY_YEAR> studentStudyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentStudyYearQM));
        studentStudyYearCB = new ComboBox();
        studentStudyYearCB.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "studyYear"));
        studentStudyYearCB.setContainerDataSource(studentStudyYearBIC);
        studentStudyYearCB.setImmediate(true);
        studentStudyYearCB.setNullSelectionAllowed(true);
        studentStudyYearCB.setTextInputAllowed(false);
        studentStudyYearCB.setFilteringMode(FilteringMode.OFF);
        studentStudyYearCB.setPageLength(0);
        studentFilterGL.addComponent(studentStudyYearCB);

        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(
                STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(
                STUDENT_EDUCATION_TYPE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(educationTypeQM));
        educationTypeCB = new ComboBox();
        educationTypeCB.setCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "educationType"));
        educationTypeCB.setContainerDataSource(educationTypeBIC);
        educationTypeCB.setImmediate(true);
        educationTypeCB.setNullSelectionAllowed(true);
        educationTypeCB.setTextInputAllowed(false);
        educationTypeCB.setFilteringMode(FilteringMode.OFF);
        studentFilterGL.addComponent(educationTypeCB);

        getContent().addComponent(studentFilterGL);
        getContent().setComponentAlignment(studentFilterGL, Alignment.MIDDLE_CENTER);

        HorizontalLayout studentFilterButtonHL = new HorizontalLayout();
        studentFilterButtonHL.setSpacing(true);

        Button studentSearch = new Button();
        studentSearch.setCaption(getUILocaleUtil().getCaption("search"));
        studentSearch.setIcon(new ThemeResource("img/button/search.png"));
        studentSearch.addStyleName("search");
        studentSearch.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                refreshFoundStudentGrid();
            }
        });
        studentFilterButtonHL.addComponent(studentSearch);

        Button studentFilterClear = new Button();
        studentFilterClear.setCaption(getUILocaleUtil().getCaption("clear"));
        studentFilterClear.setIcon(new ThemeResource("img/button/erase.png"));
        studentFilterClear.addStyleName("clear");
        studentFilterClear.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                BeanItemContainer<V_STUDENT_SELECT> bic = new BeanItemContainer<>(V_STUDENT_SELECT.class,
                        new ArrayList<>());
                foundStudentGrid.setContainerDataSource(bic);
                studentCodeTF.setValue(null);
                studentLastNameTF.setValue(null);
                studentFirstNameTF.setValue(null);
                facultyCB.setValue(null);
                specialityCB.setValue(null);
                studentStudyYearCB.setValue(null);
                educationTypeCB.setValue(null);
                schoolEducationLanguage.setValue(null);
            }
        });
        studentFilterButtonHL.addComponent(studentFilterClear);

        getContent().addComponent(studentFilterButtonHL);
        getContent().setComponentAlignment(studentFilterButtonHL, Alignment.MIDDLE_CENTER);

        GridLayout studentGL = new GridLayout();
        studentGL.setMargin(new MarginInfo(true, false, false, false));
        studentGL.setRows(1);
        studentGL.setColumns(3);
        studentGL.setSizeFull();
        studentGL.setColumnExpandRatio(0, (float) .45);
        studentGL.setColumnExpandRatio(1, (float) .1);
        studentGL.setColumnExpandRatio(2, (float) .45);

        foundStudentGrid = new Grid();
        foundStudentGrid.setSizeFull();
        foundStudentGrid.setCaption(getUILocaleUtil().getCaption("found.students"));
        foundStudentGrid.setColumns("studentCode", "lastName", "firstName", "facultyName", "specialityName");
        foundStudentGrid.getColumn("studentCode").setHeaderCaption("ID").setWidth(110);
        foundStudentGrid.getColumn("lastName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class,
                "lastName"));
        foundStudentGrid.getColumn("firstName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class,
                "firstName"));
        foundStudentGrid.getColumn("facultyName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                STUDENT_EDUCATION.class, "faculty"));
        foundStudentGrid.getColumn("specialityName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(
                STUDENT_EDUCATION.class, "speciality"));
        foundStudentGrid.setSelectionMode(SelectionMode.MULTI);
        foundStudentGrid.setHeightMode(HeightMode.ROW);
        foundStudentGrid.setHeightByRows(8);
        studentGL.addComponent(foundStudentGrid);

        VerticalLayout buttonVL = new VerticalLayout();
        buttonVL.setSpacing(true);

        Button selectAll = new NativeButton();
        selectAll.setWidth(30, Unit.PIXELS);
        selectAll.setCaption(">>");
        selectAll.setDescription(getUILocaleUtil().getCaption("select.all"));
        selectAll.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    selectAll();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to select students", ex);
                }
            }
        });
        buttonVL.addComponent(selectAll);
        buttonVL.setComponentAlignment(selectAll, Alignment.MIDDLE_CENTER);

        Button cancelAll = new NativeButton();
        cancelAll.setWidth(30, Unit.PIXELS);
        cancelAll.setCaption("<<");
        cancelAll.setDescription(getUILocaleUtil().getCaption("cancel.all"));
        cancelAll.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    cancelAll();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to cancel selected students", ex);
                }
            }
        });
        buttonVL.addComponent(cancelAll);
        buttonVL.setComponentAlignment(cancelAll, Alignment.MIDDLE_CENTER);
        studentGL.addComponent(buttonVL);
        studentGL.setComponentAlignment(buttonVL, Alignment.MIDDLE_CENTER);

        subjectStudentGrid = new Grid();
        subjectStudentGrid.setSizeFull();
        subjectStudentGrid.setCaption(getUILocaleUtil().getCaption("student.subject"));
        subjectStudentGrid.setColumns("studentCode", "lastName", "firstName", "facultyName", "specialityName");
        subjectStudentGrid.getColumn("studentCode").setHeaderCaption("ID").setWidth(110);
        subjectStudentGrid.getColumn("lastName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class, "lastName"));
        subjectStudentGrid.getColumn("firstName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT.class, "firstName"));
        subjectStudentGrid.getColumn("facultyName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "faculty"));
        subjectStudentGrid.getColumn("specialityName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "speciality"));
        subjectStudentGrid.setSelectionMode(SelectionMode.MULTI);
        subjectStudentGrid.setHeightMode(HeightMode.ROW);
        subjectStudentGrid.setHeightByRows(8);
        studentGL.addComponent(subjectStudentGrid);
        getContent().addComponent(studentGL);
        getContent().setComponentAlignment(studentGL, Alignment.BOTTOM_CENTER);
    }

    private void refreshSubjectGrid() {
        if (studyYearCB.getValue() == null) {
            Message.showInfo(getUILocaleUtil().getMessage("select.study.year"));
            return;
        }

        if (semesterPeriodCB.getValue() == null) {
            Message.showInfo(getUILocaleUtil().getMessage("select.semester"));
            return;
        }

        Map<Integer, Object> params = new HashMap<>(2);
        params.put(1, ((ENTRANCE_YEAR) studyYearCB.getValue()).getId().getId());
        params.put(2, ((SEMESTER_PERIOD) semesterPeriodCB.getValue()).getId().getId());
        String sql = "SELECT " +
                "  a.ID, " +
                "  b.NAME_RU   SUBJECT_NAME, " +
                "  h.DEPT_NAME CHAIR_NAME, " +
                "  c.LEVEL_NAME, " +
                "  d.CYCLE_SHORT_NAME, " +
                "  e.CREDIT, " +
                "  f.FORMULA, " +
                "  g.TYPE_NAME CONTROL_TYPE_NAME " +
                "FROM SEMESTER_SUBJECT a INNER JOIN SUBJECT b " +
                "    ON a.SUBJECT_ID = b.ID " +
                "  INNER JOIN LEVEL c ON b.LEVEL_ID = c.ID " +
                "  INNER JOIN SUBJECT_CYCLE d " +
                "    ON b.SUBJECT_CYCLE_ID = d.ID " +
                "  INNER JOIN CREDITABILITY e ON b.CREDITABILITY_ID = e.ID " +
                "  INNER JOIN ACADEMIC_FORMULA f ON b.ACADEMIC_FORMULA_ID = f.ID " +
                "  INNER JOIN CONTROL_TYPE g " +
                "    ON b.CONTROL_TYPE_ID = g.ID " +
                "  INNER JOIN DEPARTMENT h ON b.CHAIR_ID = h.ID " +
                "WHERE exists " +
                "      (SELECT 1 " +
                "       FROM SEMESTER_DATA h " +
                "       WHERE a.SEMESTER_DATA_ID = h.ID AND h.YEAR_ID = ?1 AND " +
                "             h.SEMESTER_PERIOD_ID = ?2) " +
                "      AND b.deleted = FALSE AND b.mandatory = FALSE";

        String subjectName = subjectNameTF.getValue();

        if (subjectName != null && subjectName.trim().length() >= 3) {
            sql = sql + " and b.NAME_RU ilike '%";
            sql = sql + subjectName.trim().toLowerCase();
            sql = sql + "%'";
        }

        try {
            List<V_SEMESTER_SUBJECT> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    sql, params, V_SEMESTER_SUBJECT.class);
            BeanItemContainer<V_SEMESTER_SUBJECT> bic = new BeanItemContainer<>(V_SEMESTER_SUBJECT.class, list);
            subjectGrid.setContainerDataSource(bic);
        } catch (Exception ex) {
            LOG.error("Unable to find semester subjects: ", ex);
            Message.showError(ex.toString());
        }
    }

    private void refreshFoundStudentGrid() {
        USERS currentUser = CommonUtils.getCurrentUser();
        if (currentUser != null) {
            //TODO
        }
        boolean filterSet = false;
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, currentUser.getId().getId());
        String sql = "SELECT " +
                "  a.ID, " +
                "  b.CODE            STUDENT_CODE, " +
                "  b.LAST_NAME, " +
                "  b.FIRST_NAME, " +
                "  d.DEPT_SHORT_NAME FACULTY_NAME, " +
                "  e.SPEC_NAME " +
                "FROM STUDENT a INNER JOIN USERS b ON a.ID = b.ID " +
                "  INNER JOIN STUDENT_EDUCATION c ON a.ID = c.STUDENT_ID AND c.CHILD_ID IS NULL " +
                "  INNER JOIN DEPARTMENT d ON c.FACULTY_ID = d.ID " +
                "  INNER JOIN SPECIALITY e ON c.SPECIALITY_ID = e.ID ";
        int i = 2;
        StringBuilder sb = new StringBuilder();
        if (schoolEducationLanguage.getValue() != null) {
            sql = sql + " inner join USER_DOCUMENT f on a.ID = f.USER_ID inner join EDUCATION_DOC g on f.ID = g.ID";
            sb.append("f.DOCUMENT_TYPE_ID = ?");
            sb.append(i);
            params.put(i++, 3);

            sb.append(" and g.LANGUAGE_ID = ?");
            sb.append(i);
            params.put(i++, ((LANGUAGE) schoolEducationLanguage.getValue()).getId().getId());
            filterSet = true;
        }
        if (facultyCB.getValue() != null) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("c.FACULTY_ID = ?");
            sb.append(i);
            params.put(i++, ((DEPARTMENT) facultyCB.getValue()).getId().getId());
            filterSet = true;
        }
        if (specialityCB.getValue() != null) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("c.SPECIALITY_ID = ?");
            sb.append(i);
            params.put(i++, ((SPECIALITY) specialityCB.getValue()).getId().getId());
            filterSet = true;
        }
        if (studentStudyYearCB.getValue() != null) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("c.STUDY_YEAR_ID = ?");
            sb.append(i);
            params.put(i++, ((STUDY_YEAR) studentStudyYearCB.getValue()).getId().getId());
            filterSet = true;
        }
        if (educationTypeCB.getValue() != null) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("c.EDUCATION_TYPE_ID = ?");
            sb.append(i);
            params.put(i++, ((STUDENT_EDUCATION_TYPE) educationTypeCB.getValue()).getId().getId());
            filterSet = true;
        }
        //		if (studentStatusCB.getValue() != null) {
        //			if (i > 1) {
        //				sql = sql + " and c.STUDENT_STATUS_ID = ?" + i;
        //			} else {
        //				sql = sql + "c.STUDENT_STATUS_ID = ?" + i;
        //			}
        //			params.put(i++, ((STUDENT_STATUS)studentStatusCB.getValue()).getId().getId());
        //			filterSet = true;
        //		}

        String studentCode = studentCodeTF.getValue();
        if (studentCode != null && studentCode.trim().length() >= 2) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("lower(b.CODE) like '");
            sb.append(studentCode.trim().toLowerCase());
            sb.append("%'");
            filterSet = true;
        }

        String lastName = studentLastNameTF.getValue();
        if (lastName != null && lastName.trim().length() >= 3) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("b.LAST_NAME ilike '%");
            sb.append(lastName.trim().toLowerCase());
            sb.append("%'");
            filterSet = true;
        }

        String firstName = studentFirstNameTF.getValue();
        if (firstName != null && firstName.trim().length() >= 3) {
            if (filterSet) {
                sb.append(" and ");
            }
            sb.append("b.FIRST_NAME ilike '%");
            sb.append(firstName.trim().toLowerCase());
            sb.append("%'");
            filterSet = true;
        }

        if (!filterSet) {
            Message.showInfo(getUILocaleUtil().getMessage("select.something"));
            return;
        }

        try {
            sb.append(" and c.STUDENT_STATUS_ID = ?");
            sb.append(i);
            params.put(i, 1);//the last param
            sb.insert(0, " where a.advisor_id = ?1 and ");
            sql = sql + sb.toString() + " order by b.LAST_NAME, b.FIRST_NAME";
            List<V_STUDENT_SELECT> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql,
                    params, V_STUDENT_SELECT.class);
            Container.Indexed ds = subjectStudentGrid.getContainerDataSource();
            for (Object item : ds.getItemIds()) {
                V_STUDENT_SELECT studentSelect = (V_STUDENT_SELECT) item;
                list.remove(studentSelect);
            }
            BeanItemContainer<V_STUDENT_SELECT> bic = new BeanItemContainer<>(V_STUDENT_SELECT.class, list);
            foundStudentGrid.setContainerDataSource(bic);
        } catch (Exception ex) {
            LOG.error("Unable to find a student: ", ex);
            Message.showError(ex.toString());
        }
    }

    private void refreshSubjectStudentGrid() {
        Object o = subjectGrid.getSelectedRow();
        if (o != null) {
            Map<Integer, Object> params = new HashMap<>();
            String sql = "select a.ID, b.CODE STUDENT_CODE, b.LAST_NAME, b.FIRST_NAME, d.DEPT_SHORT_NAME FACULTY_NAME, " +
                    "e.SPEC_NAME from STUDENT a inner join USERS b on a.ID = b.ID inner join STUDENT_EDUCATION c on " +
                    "a.ID = c.STUDENT_ID and c.CHILD_ID is null inner join DEPARTMENT d on c.FACULTY_ID = d.ID " +
                    "inner join SPECIALITY e on c.SPECIALITY_ID = e.ID where exists (select 1 from STUDENT_SUBJECT f " +
                    "where f.STUDENT_ID = c.ID and f.SUBJECT_ID = ?1 and f.DELETED = ?2)";
            params.put(1, ((V_SEMESTER_SUBJECT) o).getId().getId());
            params.put(2, Boolean.FALSE);
            try {
                List<V_STUDENT_SELECT> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        sql, params, V_STUDENT_SELECT.class);
                BeanItemContainer<V_STUDENT_SELECT> bic = new BeanItemContainer<>(V_STUDENT_SELECT.class, list);
                subjectStudentGrid.setContainerDataSource(bic);
            } catch (Exception ex) {
                LOG.error("Unable to find students of the subject: ", ex);
                Message.showError(ex.toString());
            }
        } else {
            BeanItemContainer<V_STUDENT_SELECT> bic = new BeanItemContainer<>(V_STUDENT_SELECT.class,
                    new ArrayList<>());
            subjectStudentGrid.setContainerDataSource(bic);
        }
    }

    private void selectAll() throws Exception {
        if (subjectGrid.getSelectedRow() == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.subject"));
        }

        if (foundStudentGrid.getSelectedRows().isEmpty()) {
            throw new Exception(getUILocaleUtil().getMessage("select.student"));
        }

        ID subjectId = ((V_SEMESTER_SUBJECT) subjectGrid.getSelectedRow()).getId();
        List<V_STUDENT_SELECT> list = new ArrayList<>();
        for (Object o : foundStudentGrid.getSelectedRows()) {
            list.add((V_STUDENT_SELECT) o);
        }

        Message.showConfirm(getUILocaleUtil().getMessage("student.register.subject.confirm"),
                new SelectYesListener(subjectId, list));
    }

    private void cancelAll() throws Exception {
        if (subjectStudentGrid.getSelectedRows().isEmpty()) {
            throw new Exception(getUILocaleUtil().getMessage("select.student"));
        }

        ID subjectId = ((V_SEMESTER_SUBJECT) subjectGrid.getSelectedRow()).getId();
        List<V_STUDENT_SELECT> list = new ArrayList<>();
        for (Object o : subjectStudentGrid.getSelectedRows()) {
            list.add((V_STUDENT_SELECT) o);
        }

        Message.showConfirm(getUILocaleUtil().getMessage("student.unregister.subject.confirm"),
                new CancelYesListener(subjectId, list));
    }

    private class FacultyChangeListener implements ValueChangeListener {

        private final ComboBox specialityCB;

        FacultyChangeListener(ComboBox specialityCB) {
            super();
            this.specialityCB = specialityCB;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            DEPARTMENT faculty = (DEPARTMENT) ev.getProperty().getValue();
            if (faculty != null) {
                QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
                FromItem chairFI = specialityQM.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class, "id");
                specialityQM.addWhere("deleted", Boolean.FALSE);
                specialityQM.addWhereAnd(chairFI, "parent", ECriteria.EQUAL, faculty.getId());
                try {
                    BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class,
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
                    specialityCB.setContainerDataSource(specialityBIC);
                } catch (Exception ex) {
                    LOG.error("Unable to find specialities: ", ex);
                    Message.showError(ex.toString());
                }
            } else {
                specialityCB.removeAllItems();
            }
        }
    }

    private class SubjectSelectListener implements SelectionListener {

        @Override
        public void select(SelectionEvent ev) {
            refreshSubjectStudentGrid();
            refreshFoundStudentGrid();
        }
    }

    private class SelectYesListener extends AbstractYesButtonListener {

        private final ID semesterSubjectId;
        private final List<V_STUDENT_SELECT> studentList;

        SelectYesListener(ID semesterSubjectId, List<V_STUDENT_SELECT> studentList) {
            this.semesterSubjectId = semesterSubjectId;
            this.studentList = studentList;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            QueryModel<SEMESTER_DATA> sdQM = new QueryModel<>(SEMESTER_DATA.class);
            sdQM.addWhere("year", ECriteria.EQUAL, ((ENTRANCE_YEAR) studyYearCB.getValue()).getId());
            sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, ((SEMESTER_PERIOD) semesterPeriodCB.getValue()).getId());
            Date regDate = new Date();
            List<STUDENT_SUBJECT> newList = new ArrayList<>();
            try {
                SEMESTER_DATA sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
                SEMESTER_SUBJECT ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        SEMESTER_SUBJECT.class, semesterSubjectId);
                QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                seQM.addWhereNull("child");
                for (V_STUDENT_SELECT vss : studentList) {
                    seQM.addWhere("student", ECriteria.EQUAL, vss.getId());
                    STUDENT_EDUCATION se = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookupSingle(seQM);
                    STUDENT_SUBJECT ss1 = new STUDENT_SUBJECT();
                    ss1.setSemesterData(sd);
                    ss1.setStudentEducation(se);
                    ss1.setSubject(ss);
                    ss1.setRegDate(regDate);
                    newList.add(ss1);
                }

                if (!newList.isEmpty()) {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
                    refreshSubjectStudentGrid();
                    refreshFoundStudentGrid();
                }
            } catch (Exception ex) {
                LOG.error("Unable to register the students on the subject: ", ex);
                Message.showError(ex.toString());
            }
        }
    }

    private class CancelYesListener extends AbstractYesButtonListener {

        private final ID semesterSubjectId;
        private final List<V_STUDENT_SELECT> studentList;

        CancelYesListener(ID semesterSubjectId, List<V_STUDENT_SELECT> studentList) {
            this.semesterSubjectId = semesterSubjectId;
            this.studentList = studentList;
        }

        @Override
        public void buttonClick(ClickEvent ev) {
            QueryModel<SEMESTER_DATA> sdQM = new QueryModel<>(SEMESTER_DATA.class);
            sdQM.addWhere("year", ECriteria.EQUAL, ((ENTRANCE_YEAR) studyYearCB.getValue()).getId());
            sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, ((SEMESTER_PERIOD) semesterPeriodCB.getValue()).getId());
            List<STUDENT_SUBJECT> delList = new ArrayList<>();
            try {
                SEMESTER_DATA sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
                SEMESTER_SUBJECT ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        SEMESTER_SUBJECT.class, semesterSubjectId);
                QueryModel<STUDENT_SUBJECT> ssQM = new QueryModel<>(STUDENT_SUBJECT.class);
                FromItem ssFI = ssQM.addJoin(EJoin.INNER_JOIN, "studentEducation", STUDENT_EDUCATION.class, "id");
                ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                ssQM.addWhere("subject", ECriteria.EQUAL, ss.getId());
                ssQM.addWhereNull(ssFI, "child");
                for (V_STUDENT_SELECT vss : studentList) {
                    ssQM.addWhere(ssFI, "student", ECriteria.EQUAL, vss.getId());
                    List<STUDENT_SUBJECT> ssList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(ssQM);
                    delList.addAll(ssList);
                }

                if (!delList.isEmpty()) {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                    refreshSubjectStudentGrid();
                }
            } catch (Exception ex) {
                LOG.error("Unable to unregister the students from the subject: ", ex);
                Message.showError(ex.toString());
            }
        }
    }
}
