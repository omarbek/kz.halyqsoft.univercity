package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.AbstractCommonPanel;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.*;

public class SemesterPanel extends AbstractCommonPanel {

    private IndividualEducationPlanView registrationView;
    private STUDENT_EDUCATION studentEducation;
    private SEMESTER semester;
    private List<SEMESTER_DATA> semesterData;
    private HorizontalLayout layout;
    private TreeTable chairsAndSubjectsTT;
    private Grid mainSubjectGrid;
    private List<SUBJECT> chosenMainlist;

    public SemesterPanel(IndividualEducationPlanView registrationView, SEMESTER semester) throws Exception {
        this.semester = semester;
        chosenMainlist = new ArrayList<>();

        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);
    }

    public void initPanel() throws Exception {
        ArrayList<STUDENT_SUBJECT> studentSubjects = getStudentSubjects();

        HorizontalLayout allSubjectsHL = CommonUtils.createButtonPanel();

        List<SUBJECT> mainSubjects = getMainSubjects();

        VerticalLayout mainSubjectsVL = new VerticalLayout();
        mainSubjectsVL.setSpacing(true);

        setMainSubjects(mainSubjects, mainSubjectsVL);
        allSubjectsHL.addComponent(mainSubjectsVL);

        addButtons(allSubjectsHL);

        addGrid(studentSubjects, mainSubjects);

        VerticalLayout electiveSubjectsVL = new VerticalLayout();
        electiveSubjectsVL.setSpacing(true);

        List<PAIR_SUBJECT> pairSubjects = getPairSubjects();

        electiveSubjectsVL.addComponent(mainSubjectGrid);

        ArrayList<OptionGroup> optionGroups = setElectiveSubjects(studentSubjects, electiveSubjectsVL, pairSubjects);
        allSubjectsHL.addComponent(electiveSubjectsVL);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(saveListener(pairSubjects, optionGroups, saveButton));

        getContent().addComponent(allSubjectsHL);

        setDisableButton(studentSubjects, saveButton);

        getContent().addComponent(saveButton);
        getContent().setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        setTeachers();
    }

    private void addGrid(ArrayList<STUDENT_SUBJECT> studentSubjects, List<SUBJECT> mainSubjects) {
        mainSubjectGrid = new Grid();
        mainSubjectGrid.setSizeFull();
        mainSubjectGrid.setCaption("chosen main subjects");
        mainSubjectGrid.setColumns("nameKZ", "chair");
        mainSubjectGrid.getColumn("nameKZ").setHeaderCaption("name");
        mainSubjectGrid.getColumn("chair").setHeaderCaption("chair");
        mainSubjectGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        mainSubjectGrid.setHeightMode(HeightMode.ROW);
        mainSubjectGrid.setHeightByRows(8);

        for (SUBJECT subject : mainSubjects) {
            for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                if (studentSubject.getSubject().getSubject().equals(subject)) {
                    if (isSubjectsInThisSemester(studentSubject)) {
                        chosenMainlist.add(studentSubject.getSubject().getSubject());
                    }
                }
            }
        }
        BeanItemContainer<SUBJECT> bic = new BeanItemContainer<>(SUBJECT.class, chosenMainlist);
        mainSubjectGrid.setContainerDataSource(bic);
    }

    private void addButtons(HorizontalLayout allSubjectsHL) {
        VerticalLayout buttonVL = new VerticalLayout();
        buttonVL.setSpacing(true);

        Button selectAll = new NativeButton();
        selectAll.setWidth(30, Unit.PIXELS);
        selectAll.setCaption(">>");
        selectAll.setDescription(getUILocaleUtil().getCaption("select.all"));
        selectAll.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent ev) {
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
        cancelAll.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent ev) {
                try {
                    cancelAll();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to cancel selected students", ex);
                }
            }
        });
        buttonVL.addComponent(cancelAll);
        buttonVL.setComponentAlignment(cancelAll, Alignment.MIDDLE_CENTER);
        allSubjectsHL.addComponent(buttonVL);
        allSubjectsHL.setComponentAlignment(buttonVL, Alignment.MIDDLE_CENTER);
    }

    private void setDisableButton(ArrayList<STUDENT_SUBJECT> studentSubjects, Button saveButton) {
        for (STUDENT_SUBJECT studentSubject : studentSubjects) {
            if (isSubjectsInThisSemester(studentSubject)) {
                saveButton.setEnabled(false);
                break;
            }
        }
    }

    private Button.ClickListener saveListener(List<PAIR_SUBJECT> pairSubjects, ArrayList<OptionGroup> optionGroups, Button saveButton) {
        return new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    setStudentSubject();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }

            private void setStudentSubject() {
                Collection<SUBJECT> mainSubjects = (Collection<SUBJECT>) mainSubjectGrid.getContainerDataSource().getItemIds();

                if (pairSubjects.isEmpty()) {
                    Message.showInfo("elective subjects are empty");//TODO resource
                    return;
                }

                if (mainSubjects.size() < 1) {
                    Message.showInfo("main subjects are empty");//TODO resource
                    return;
                }

                for (OptionGroup sub : optionGroups) {
                    if (sub.getValue() == null) {
                        Message.showError(getUILocaleUtil().getMessage("select.subject"));
                        return;
                    }
                }

                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        QueryModel<SEMESTER_SUBJECT> semesterSubjectQM = new QueryModel<>(SEMESTER_SUBJECT.class);
                        QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);

                        ArrayList<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();

                        for (OptionGroup sub : optionGroups) {
                            ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                            SEMESTER_PERIOD semesterPeriod = null;
                            for (PAIR_SUBJECT pairSubject : pairSubjects) {
                                semesterPeriod = pairSubject.getElectveBindedSubject().getSemester().getSemesterPeriod();
                            }
                            semesterDataQM.addWhere("year", ECriteria.EQUAL, entranceYear.getId());
                            semesterDataQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
                            SEMESTER_DATA semesterData = null;
                            try {
                                semesterData = getSemesterData(semesterDataQM, entranceYear, semesterPeriod);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();

                            SUBJECT subject = ((SUBJECT) sub.getValue());
                            semesterSubjectQM.addWhere("subject", ECriteria.EQUAL, subject.getId());
                            semesterSubjectQM.addWhere("semesterData", ECriteria.EQUAL, semesterData.getId());
                            SEMESTER_SUBJECT semesterSubject = null;
                            try {
                                semesterSubject = getSemesterSubject(semesterSubjectQM, semesterData, subject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            studentSubject.setSemesterData(semesterData);
                            studentSubject.setStudentEducation(studentEducation);
                            studentSubject.setRegDate(new Date());
                            studentSubject.setSubject(semesterSubject);

                            studentSubjects.add(studentSubject);
                        }

                        ArrayList<STUDENT_SUBJECT> studentSubjectAll = new ArrayList<>();

                        ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                        SEMESTER_PERIOD semesterPeriod = null;
                        for (PAIR_SUBJECT pairSubject : pairSubjects) {
                            semesterPeriod = pairSubject.getElectveBindedSubject().getSemester().getSemesterPeriod();
                        }

                        for (SUBJECT s : mainSubjects) {
                            semesterDataQM.addWhere("year", ECriteria.EQUAL, entranceYear.getId());
                            semesterDataQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterPeriod.getId());
                            SEMESTER_DATA semesterData = null;
                            try {
                                semesterData = getSemesterData(semesterDataQM, entranceYear, semesterPeriod);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            STUDENT_SUBJECT subjectOfStudent = new STUDENT_SUBJECT();

                            semesterSubjectQM.addWhere("subject", ECriteria.EQUAL, s.getId());
                            semesterSubjectQM.addWhere("semesterData", ECriteria.EQUAL, semesterData.getId());
                            SEMESTER_SUBJECT semesterSubject = null;
                            try {
                                semesterSubject = getSemesterSubject(semesterSubjectQM, semesterData, s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            subjectOfStudent.setSemesterData(semesterData);
                            subjectOfStudent.setStudentEducation(studentEducation);
                            subjectOfStudent.setRegDate(new Date());
                            subjectOfStudent.setSubject(semesterSubject);

                            studentSubjectAll.add(subjectOfStudent);
                        }

                        if (!studentSubjects.isEmpty() && !studentSubjectAll.isEmpty()) {
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(studentSubjects);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(studentSubjectAll);
                                saveButton.setEnabled(false);

                                setTeachers();
                            } catch (Exception e) {
                                CommonUtils.showMessageAndWriteLog("Unable to save subject", e);
                            }
                        }
                    }
                });
            }
        };
    }

    private List<PAIR_SUBJECT> getPairSubjects() throws Exception {
        QueryModel<PAIR_SUBJECT> pairSubjectQM = new QueryModel<>(PAIR_SUBJECT.class);
        FromItem electiveBindedSubjFI = pairSubjectQM.addJoin(EJoin.INNER_JOIN, "electiveBindedSubject",
                ELECTIVE_BINDED_SUBJECT.class, "id");
        pairSubjectQM.addWhere(electiveBindedSubjFI, "semester", ECriteria.EQUAL, semester.getId());
        FromItem specFI = electiveBindedSubjFI.addJoin(EJoin.INNER_JOIN, "catalogElectiveSubjects",
                CATALOG_ELECTIVE_SUBJECTS.class, "id");
        pairSubjectQM.addWhere(specFI, "speciality", ECriteria.EQUAL,
                studentEducation.getSpeciality().getId());
        pairSubjectQM.addWhere(specFI, "entranceYear", ECriteria.EQUAL,
                studentEducation.getStudent().getEntranceYear().getId());
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(pairSubjectQM);
    }

    private ArrayList<OptionGroup> setElectiveSubjects(ArrayList<STUDENT_SUBJECT> studentSubjects,
                                                       VerticalLayout electiveSubjectsVL, List<PAIR_SUBJECT> pairSubjects) {
        Map<Integer, ArrayList<SUBJECT>> map = new HashMap<>();

        for (PAIR_SUBJECT pairSubject : pairSubjects) {
            if (map.get(pairSubject.getPairNumber()) == null) {
                ArrayList<SUBJECT> arrayList = new ArrayList<>();
                arrayList.add(pairSubject.getSubject());
                map.put(pairSubject.getPairNumber(), arrayList);
            } else {
                map.get(pairSubject.getPairNumber()).add(pairSubject.getSubject());
            }
        }
        ArrayList<OptionGroup> optionGroups = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<SUBJECT>> e : map.entrySet()) {
            OptionGroup electiveSubjOG = new OptionGroup(getUILocaleUtil().getCaption("subjectsOG"));
            electiveSubjOG.setMultiSelect(false);
            electiveSubjOG.setNullSelectionAllowed(false);
            electiveSubjOG.setImmediate(true);
            for (SUBJECT subjectOfElective : e.getValue()) {
                electiveSubjOG.addItem(subjectOfElective);
                for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                    if (studentSubject.getSubject().getSubject().equals(subjectOfElective)) {
                        if (isSubjectsInThisSemester(studentSubject)) {
                            electiveSubjOG.select(subjectOfElective);
                        }
                    }
                }
            }
            electiveSubjectsVL.addComponent(electiveSubjOG);
            optionGroups.add(electiveSubjOG);
        }
        return optionGroups;
    }

    private void setMainSubjects(List<SUBJECT> mainSubjects,
                                 VerticalLayout mainSubjectsVL) throws Exception {
        chairsAndSubjectsTT = new TreeTable();
        HierarchicalContainer chairsAndSubjectsHC = new HierarchicalContainer();

        QueryModel<DEPARTMENT> departmentQM = new QueryModel<>(DEPARTMENT.class);
        departmentQM.addWhereNotNull("parent");
        departmentQM.addWhere("deleted", false);
        departmentQM.addWhere("fc", false);
        List<DEPARTMENT> departments = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(departmentQM);

        for (DEPARTMENT department : departments) {
            chairsAndSubjectsHC.setParent(department, null);
            chairsAndSubjectsHC.setChildrenAllowed(department, true);
            chairsAndSubjectsHC.addItem(department);

            for (SUBJECT mainSubject : mainSubjects) {
                if (department.equals(mainSubject.getChair())) {
                    chairsAndSubjectsHC.addItem(mainSubject);
                    chairsAndSubjectsHC.setParent(mainSubject, department);
                    chairsAndSubjectsHC.setChildrenAllowed(mainSubject, false);
                }
            }
        }
        chairsAndSubjectsTT.setContainerDataSource(chairsAndSubjectsHC);

        chairsAndSubjectsTT.setSizeFull();
        chairsAndSubjectsTT.addStyleName("schedule");
        chairsAndSubjectsTT.setSelectable(true);
        chairsAndSubjectsTT.setMultiSelect(true);
        chairsAndSubjectsTT.setNullSelectionAllowed(false);
        chairsAndSubjectsTT.setImmediate(true);
        chairsAndSubjectsTT.setColumnReorderingAllowed(false);
        chairsAndSubjectsTT.setPageLength(14);
        SubjectsMenuColumn menuColumn = new SubjectsMenuColumn();
        chairsAndSubjectsTT.addGeneratedColumn("subject", menuColumn);
        chairsAndSubjectsTT.setColumnHeader("subject", "choose some");//TODO resource

        mainSubjectsVL.addComponent(chairsAndSubjectsTT);
    }

    private List<SUBJECT> getMainSubjects() throws Exception {
        QueryModel<SUBJECT> mainSubjectQM = new QueryModel<>(SUBJECT.class);
        mainSubjectQM.addWhereNotNull("subjectCycle");
        mainSubjectQM.addWhere("deleted", false);
        mainSubjectQM.addWhere("mandatory", true);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(mainSubjectQM);
    }

    private ArrayList<STUDENT_SUBJECT> getStudentSubjects() throws Exception {
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        FromItem semesterDataFI = studentSubjectQM.addJoin(EJoin.INNER_JOIN, "semesterData",
                SEMESTER_DATA.class, "id");
        studentSubjectQM.addWhere(semesterDataFI, "year", ECriteria.EQUAL, studentEducation.
                getStudent().getEntranceYear().getId());
        studentSubjectQM.addWhere(semesterDataFI, "semesterPeriod", ECriteria.EQUAL, semester.
                getSemesterPeriod().getId());
        studentSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        studentSubjectQM.addOrder("id");
        ArrayList<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        try {
            studentSubjects.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(studentSubjectQM));
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
        }
        return studentSubjects;
    }

    private boolean isSubjectsInThisSemester(STUDENT_SUBJECT studentSubject) {
        return studentSubject.getSemesterData().getSemesterPeriod().equals(semester.getSemesterPeriod());
    }

    private SEMESTER_SUBJECT getSemesterSubject(QueryModel<SEMESTER_SUBJECT> semesterSubjectQM,
                                                SEMESTER_DATA semesterData, SUBJECT subject) throws Exception {
        SEMESTER_SUBJECT semesterSubject;
        try {
            semesterSubject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterSubjectQM);
        } catch (NoResultException ex) {
            semesterSubject = new SEMESTER_SUBJECT();
            semesterSubject.setSemesterData(semesterData);
            semesterSubject.setSubject(subject);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(semesterSubject);
        }
        return semesterSubject;
    }

    private SEMESTER_DATA getSemesterData(QueryModel<SEMESTER_DATA> semesterDataQM,
                                          ENTRANCE_YEAR entranceYear, SEMESTER_PERIOD semesterPeriod) throws Exception {
        SEMESTER_DATA semesterData;
        try {
            semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterDataQM);
        } catch (NoResultException ex) {
            semesterData = new SEMESTER_DATA();
            semesterData.setYear(entranceYear);
            semesterData.setSemesterPeriod(semesterPeriod);
            semesterData.setBeginDate(CommonUtils.getCurrentSemesterData().getBeginDate());
            semesterData.setEndDate(CommonUtils.getCurrentSemesterData().getEndDate());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(semesterData);
        }
        return semesterData;
    }

    private void setTeachers() throws Exception {
        ArrayList<STUDENT_SUBJECT> studentSubjects = getStudentSubjects();
        if (!studentSubjects.isEmpty()) {
            Label subjectLabel = new Label("choose a teacher");//TODO resource
            getContent().addComponent(subjectLabel);

            List<OptionGroup> optionGroups = new ArrayList<>();
            for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                if (isSubjectsInThisSemester(studentSubject)) {
                    SUBJECT subject = studentSubject.getSubject().getSubject();
                    OptionGroup teacherOG = new OptionGroup(subject.toString());
                    QueryModel<TEACHER_SUBJECT> teacherSubjectQM = new QueryModel<>(TEACHER_SUBJECT.class);
                    teacherSubjectQM.addWhere("subject", ECriteria.EQUAL, subject.getId());
                    List<TEACHER_SUBJECT> teacherSubjects = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(teacherSubjectQM);
                    teacherOG.setMultiSelect(false);
                    for (TEACHER_SUBJECT teacherSubject : teacherSubjects) {
                        EMPLOYEE employee = teacherSubject.getEmployee();
                        teacherOG.addItem(teacherSubject);
                        for (STUDENT_TEACHER_SUBJECT studentTeacherSubject : getStudentTeacherSubject()) {
                            if (studentTeacherSubject.getSemester().equals(semester)
                                    && studentTeacherSubject.getTeacherSubject().equals(teacherSubject)) {
                                teacherOG.select(teacherSubject);
                            }
                        }
                    }
                    getContent().addComponent(teacherOG);
                    optionGroups.add(teacherOG);
                }
            }
            Button saveButton = CommonUtils.createSaveButton();
            saveButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        List<STUDENT_TEACHER_SUBJECT> studentTeacherSubjects = new ArrayList<>();
                        for (OptionGroup optionGroup : optionGroups) {
                            TEACHER_SUBJECT teacherSubject = ((TEACHER_SUBJECT) optionGroup.getValue());

                            if (teacherSubject != null) {
                                STUDENT_TEACHER_SUBJECT studentTeacherSubject = new STUDENT_TEACHER_SUBJECT();
                                studentTeacherSubject.setSemester(semester);
                                studentTeacherSubject.setStudentEducation(studentEducation);
                                studentTeacherSubject.setTeacherSubject(teacherSubject);
                                studentTeacherSubjects.add(studentTeacherSubject);
                            }
                        }
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentTeacherSubjects);
                        saveButton.setEnabled(false);
                        CommonUtils.showSavedNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            for (STUDENT_TEACHER_SUBJECT studentTeacherSubject : getStudentTeacherSubject()) {
                if (studentTeacherSubject.getSemester().equals(semester)) {
                    saveButton.setEnabled(false);
                    break;
                }
            }
            getContent().addComponent(saveButton);
        }
    }

    private List<STUDENT_TEACHER_SUBJECT> getStudentTeacherSubject() throws Exception {
        QueryModel<STUDENT_TEACHER_SUBJECT> studentTeacherSubjectQM = new QueryModel<>(STUDENT_TEACHER_SUBJECT.class);
        studentTeacherSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        return SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(studentTeacherSubjectQM);
    }

    private void selectAll() {
        Set<SUBJECT> subjects = (Set<SUBJECT>) chairsAndSubjectsTT.getValue();
        if (subjects.isEmpty()) {
            Message.showInfo(getUILocaleUtil().getMessage("select.subject"));
        } else {
            chosenMainlist.addAll(subjects);
            BeanItemContainer<SUBJECT> bic = new BeanItemContainer<>(SUBJECT.class, chosenMainlist);
            mainSubjectGrid.setContainerDataSource(bic);
        }
    }

    private void cancelAll() {
        Collection<Object> selectedRows = mainSubjectGrid.getSelectedRows();
        if (selectedRows.isEmpty()) {
            Message.showInfo(getUILocaleUtil().getMessage("select.subject"));
        } else {
            for (Object o : selectedRows) {
                chosenMainlist.remove(o);
            }
            BeanItemContainer<SUBJECT> bic = new BeanItemContainer<>(SUBJECT.class, chosenMainlist);
            mainSubjectGrid.setContainerDataSource(bic);
        }
    }
}