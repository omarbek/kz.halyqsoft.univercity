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

public class SubjectsPanel extends AbstractCommonPanel {

    private STUDENT_EDUCATION studentEducation;
    private SEMESTER semester;
    private List<SEMESTER_DATA> semesterData;
    private HorizontalLayout layout;
    private TreeTable chairsAndSubjectsTT;
    private Grid mainSubjectGrid;
    private List<SUBJECT> chosenMainlist;
    private boolean isAdmin;
    private VerticalLayout teacherVL;
    private List<STUDENT_SUBJECT> studentSubjects;
    private boolean canAccess;

    SubjectsPanel(SEMESTER semester, STUDENT_EDUCATION studentEducation, boolean isAdmin) throws Exception {
        this.semester = semester;
        this.studentEducation = studentEducation;
        this.isAdmin = isAdmin;
        chosenMainlist = new ArrayList<>();
        teacherVL = new VerticalLayout();

        QueryModel<PRIVILEGES> privilegesQM = new QueryModel<>(PRIVILEGES.class);
        privilegesQM.addWhere("id", ECriteria.EQUAL, PRIVILEGES.IUPS);
        PRIVILEGES privileges = CommonUtils.getQuery().lookupSingle(privilegesQM);
        canAccess = privileges.isCanAccess();
    }

    public void initPanel() throws Exception {
        setStudentSubjects();

        HorizontalLayout allSubjectsHL = CommonUtils.createButtonPanel();

        List<SUBJECT> mainSubjects = getMainSubjects();

        VerticalLayout mainSubjectsVL = new VerticalLayout();
        mainSubjectsVL.setSpacing(true);

        setMainSubjects(mainSubjects, mainSubjectsVL);
        allSubjectsHL.addComponent(mainSubjectsVL);

        addButtons(allSubjectsHL);

        addGrid(mainSubjects);

        VerticalLayout electiveSubjectsVL = new VerticalLayout();
        electiveSubjectsVL.setSpacing(true);

        List<PAIR_SUBJECT> pairSubjects = getPairSubjects();

        electiveSubjectsVL.addComponent(mainSubjectGrid);

        List<OptionGroup> optionGroups = setElectiveSubjects(electiveSubjectsVL, pairSubjects);
        allSubjectsHL.addComponent(electiveSubjectsVL);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(saveListener(pairSubjects, optionGroups, saveButton));

        getContent().addComponent(allSubjectsHL);

        if (!isAdmin && !canAccess) {
            setDisableButton(saveButton);
        }

        getContent().addComponent(saveButton);
        getContent().setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        setTeachers();
    }

    private void addGrid(List<SUBJECT> mainSubjects) {
        mainSubjectGrid = new Grid();
        mainSubjectGrid.setSizeFull();
        mainSubjectGrid.setCaption(getUILocaleUtil().getCaption("choosen.main.subjects"));
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

    private void setDisableButton(Button saveButton) {
        for (STUDENT_SUBJECT studentSubject : studentSubjects) {
            if (isSubjectsInThisSemester(studentSubject)) {
                saveButton.setEnabled(false);
                break;
            }
        }
    }

    private Button.ClickListener saveListener(List<PAIR_SUBJECT> pairSubjects, List<OptionGroup> optionGroups,
                                              Button saveButton) {
        return new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    setStudentSubject();
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog(e.getMessage(), e);
                    e.printStackTrace();
                }
            }

            private void setStudentSubject() {
                Collection<SUBJECT> mainSubjects = (Collection<SUBJECT>) mainSubjectGrid.getContainerDataSource().getItemIds();

//                if (pairSubjects.isEmpty()) {
//                    Message.showInfo("elective subjects are empty");//TODO resource
//                    return;
//                }
//
//                if (mainSubjects.size() < 1) {
//                    Message.showInfo("main subjects are empty");//TODO resource
//                    return;
//                }

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

                        List<STUDENT_SUBJECT> electiveStudentSubjects = new ArrayList<>();

                        for (OptionGroup sub : optionGroups) {
                            ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                            SEMESTER_PERIOD semesterPeriod = semester.getSemesterPeriod();
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

                            electiveStudentSubjects.add(studentSubject);
                        }

                        List<STUDENT_SUBJECT> mainStudentSubjects = new ArrayList<>();

                        ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                        SEMESTER_PERIOD semesterPeriod = semester.getSemesterPeriod();
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

                            mainStudentSubjects.add(subjectOfStudent);
                        }


                        try {
//                            mainStudentSubjects.addAll(electiveStudentSubjects);
                            if (isAdmin || canAccess) {
                                for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                                    studentSubject.setDeleted(true);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(
                                            studentSubject);
                                }
                            } else {
                                saveButton.setEnabled(false);
                            }
                            if (!electiveStudentSubjects.isEmpty()) {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(electiveStudentSubjects);
                            }
                            if (!mainStudentSubjects.isEmpty()) {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(mainStudentSubjects);
                            }

                            setTeachers();
                        } catch (Exception e) {
                            CommonUtils.showMessageAndWriteLog("Unable to save subject", e);
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

    private List<OptionGroup> setElectiveSubjects(
            VerticalLayout electiveSubjectsVL,
            List<PAIR_SUBJECT> pairSubjects) {
        Map<Integer, List<SUBJECT>> map = new HashMap<>();

        for (PAIR_SUBJECT pairSubject : pairSubjects) {
            if (map.get(pairSubject.getPairNumber()) == null) {
                List<SUBJECT> arrayList = new ArrayList<>();
                arrayList.add(pairSubject.getSubject());
                map.put(pairSubject.getPairNumber(), arrayList);
            } else {
                map.get(pairSubject.getPairNumber()).add(pairSubject.getSubject());
            }
        }
        List<OptionGroup> optionGroups = new ArrayList<>();
        for (Map.Entry<Integer, List<SUBJECT>> e : map.entrySet()) {
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
        chairsAndSubjectsTT.setColumnHeader("subject", getUILocaleUtil().getMessage("choose.field"));

        mainSubjectsVL.addComponent(chairsAndSubjectsTT);
    }

    private List<SUBJECT> getMainSubjects() throws Exception {
        QueryModel<SUBJECT> mainSubjectQM = new QueryModel<>(SUBJECT.class);
        mainSubjectQM.addWhere("deleted", false);
        mainSubjectQM.addWhere("level", ECriteria.EQUAL, LEVEL.BACHELOR);
        mainSubjectQM.addWhere("mandatory", true);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(mainSubjectQM);
    }

    private void setStudentSubjects() throws Exception {
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        FromItem semesterDataFI = studentSubjectQM.addJoin(EJoin.INNER_JOIN, "semesterData",
                SEMESTER_DATA.class, "id");
        studentSubjectQM.addWhere(semesterDataFI, "year", ECriteria.EQUAL, CommonUtils.getCurrentSemesterData()
                .getYear().getId());
        studentSubjectQM.addWhere(semesterDataFI, "semesterPeriod", ECriteria.EQUAL, semester.
                getSemesterPeriod().getId());
        studentSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        studentSubjectQM.addWhere("deleted", ECriteria.EQUAL, false);
        studentSubjectQM.addOrder("id");
        try {
            studentSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(studentSubjectQM);
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
        }
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
        getContent().removeComponent(teacherVL);
        teacherVL.removeAllComponents();
        setStudentSubjects();

        if (!studentSubjects.isEmpty()) {
            Label subjectLabel = new Label(getUILocaleUtil().getCaption("choose.a.teacher"));
            teacherVL.addComponent(subjectLabel);

            List<OptionGroup> optionGroups = new ArrayList<>();
            for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                if (isSubjectsInThisSemester(studentSubject)) {
                    SUBJECT subject = studentSubject.getSubject().getSubject();
                    OptionGroup teacherOG = new OptionGroup(subject.toString());
                    QueryModel<TEACHER_SUBJECT> teacherSubjectQM = new QueryModel<>(TEACHER_SUBJECT.class);
                    teacherSubjectQM.addWhere("subject", ECriteria.EQUAL, subject.getId());
                    List<TEACHER_SUBJECT> teacherSubjects = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(teacherSubjectQM);
                    teacherOG.setMultiSelect(true);
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
                    teacherVL.addComponent(teacherOG);
                    optionGroups.add(teacherOG);
                }
            }
            Button saveButton = CommonUtils.createSaveButton();
            saveButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        QueryModel<STUDENT_TEACHER_SUBJECT> studentTeacherSubjectQM =
                                new QueryModel<>(STUDENT_TEACHER_SUBJECT.class);
                        studentTeacherSubjectQM.addWhere("studentEducation", ECriteria.EQUAL,
                                studentEducation.getId());
                        studentTeacherSubjectQM.addWhere("semester", ECriteria.EQUAL, semester.getId());
                        List<STUDENT_TEACHER_SUBJECT> deletingStudentTeacherSubjects = SessionFacadeFactory
                                .getSessionFacade(CommonEntityFacadeBean.class)
                                .lookup(studentTeacherSubjectQM);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(
                                deletingStudentTeacherSubjects);

                        for (OptionGroup optionGroup : optionGroups) {
                            List<STUDENT_TEACHER_SUBJECT> studentTeacherSubjects = new ArrayList<>();
                            Set<TEACHER_SUBJECT> teacherSubjects = ((Set) optionGroup.getValue());

                            for (TEACHER_SUBJECT teacherSubject : teacherSubjects) {
                                if (teacherSubject != null) {
                                    STUDENT_TEACHER_SUBJECT studentTeacherSubject = new STUDENT_TEACHER_SUBJECT();
                                    studentTeacherSubject.setSemester(semester);
                                    studentTeacherSubject.setStudentEducation(studentEducation);
                                    studentTeacherSubject.setTeacherSubject(teacherSubject);
                                    studentTeacherSubjects.add(studentTeacherSubject);
                                }
                            }
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentTeacherSubjects);
                        }

                        saveButton.setEnabled(false);
                        CommonUtils.showSavedNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (!isAdmin && !canAccess) {
                for (STUDENT_TEACHER_SUBJECT studentTeacherSubject : getStudentTeacherSubject()) {
                    if (studentTeacherSubject.getSemester().equals(semester)) {
                        saveButton.setEnabled(false);
                        break;
                    }
                }
            }
            teacherVL.addComponent(saveButton);
            getContent().addComponent(teacherVL);
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
            for (SUBJECT subject : subjects) {
                if (!chosenMainlist.contains(subject)) {
                    chosenMainlist.add(subject);
                }
            }
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