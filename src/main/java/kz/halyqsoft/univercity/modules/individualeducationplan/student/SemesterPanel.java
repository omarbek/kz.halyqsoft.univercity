package kz.halyqsoft.univercity.modules.individualeducationplan.student;

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

    public SemesterPanel(IndividualEducationPlanView registrationView, SEMESTER s) throws Exception {
        this.semester = s;

        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);
    }

    public void initPanel() throws Exception {
        ArrayList<STUDENT_SUBJECT> studentSubjects = getStudentSubjects();

        HorizontalSplitPanel allSubjectsHSP = new HorizontalSplitPanel();
        allSubjectsHSP.setSplitPosition(50);
        allSubjectsHSP.setHeight("300");

        List<SUBJECT> mainSubjects = getMainSubjects();

        VerticalLayout mainSubjectsVL = new VerticalLayout();
        mainSubjectsVL.setSpacing(true);

        OptionGroup mainSubjectsOG = setMainSubjects(studentSubjects, mainSubjects, mainSubjectsVL);
        allSubjectsHSP.addComponent(mainSubjectsVL);

        VerticalLayout electiveSubjectsVL = new VerticalLayout();
        electiveSubjectsVL.setSpacing(true);

        List<PAIR_SUBJECT> pairSubjects = getPairSubjects();

        ArrayList<OptionGroup> optionGroups = setElectiveSubjects(studentSubjects, electiveSubjectsVL, pairSubjects);
        allSubjectsHSP.addComponent(electiveSubjectsVL);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(saveListener(mainSubjectsOG, pairSubjects, optionGroups, saveButton));

        allSubjectsHSP.setFirstComponent(electiveSubjectsVL);
        allSubjectsHSP.setFirstComponent(mainSubjectsVL);

        getContent().addComponent(allSubjectsHSP);

        setDisableButton(studentSubjects, saveButton);

        getContent().addComponent(saveButton);
        getContent().setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        setTeachers();
    }

    private void setDisableButton(ArrayList<STUDENT_SUBJECT> studentSubjects, Button saveButton) {
        for (STUDENT_SUBJECT studentSubject : studentSubjects) {
            if (isSubjectsInThisSemester(studentSubject)) {
                saveButton.setEnabled(false);
                break;
            }
        }
    }

    private Button.ClickListener saveListener(OptionGroup mainSubjectsOG, List<PAIR_SUBJECT> pairSubjects, ArrayList<OptionGroup> optionGroups, Button saveButton) {
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
                if (pairSubjects.isEmpty()) {
                    Message.showInfo("elective subjects are empty");//TODO resource
                    return;
                }

                if (((Set) mainSubjectsOG.getValue()).size() < 1) {
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

                        Set<SUBJECT> subjects = (Set<SUBJECT>) mainSubjectsOG.getValue();
                        for (SUBJECT s : subjects) {
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

    private ArrayList<OptionGroup> setElectiveSubjects(ArrayList<STUDENT_SUBJECT> studentSubjects, VerticalLayout electiveSubjectsVL, List<PAIR_SUBJECT> pairSubjects) {
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
//        optionGroups.add(mainSubjectsOG);
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

    private OptionGroup setMainSubjects(ArrayList<STUDENT_SUBJECT> studentSubjects, List<SUBJECT> mainSubjects, VerticalLayout mainSubjectsVL) {
        OptionGroup mainSubjectsOG = new OptionGroup(getUILocaleUtil().getCaption("subjectOG"));
        mainSubjectsOG.setMultiSelect(true);
        mainSubjectsOG.setNullSelectionAllowed(false);
        mainSubjectsOG.setImmediate(true);

        for (SUBJECT subject : mainSubjects) {
            mainSubjectsOG.addItem(subject);
            for (STUDENT_SUBJECT studentSubject : studentSubjects) {
                if (studentSubject.getSubject().getSubject().equals(subject)) {
                    if (isSubjectsInThisSemester(studentSubject)) {
                        mainSubjectsOG.select(subject);
                    }
                }
            }
            mainSubjectsVL.addComponent(mainSubjectsOG);
        }
        return mainSubjectsOG;
    }

    private List<SUBJECT> getMainSubjects() throws Exception {
        QueryModel<SUBJECT> mainSubjectQM = new QueryModel<>(SUBJECT.class);
        FromItem specialFI = mainSubjectQM.addJoin(EJoin.INNER_JOIN, "chair", SPECIALITY.class, "department");
        mainSubjectQM.addWhereNotNull("subjectCycle");
        mainSubjectQM.addWhere(specialFI, "deleted", false);
        mainSubjectQM.addWhere(specialFI, "id", ECriteria.EQUAL, studentEducation.getSpeciality().getId());
        mainSubjectQM.addWhere("deleted", false);
        mainSubjectQM.addWhere("mandatory", true);
        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(mainSubjectQM);
    }

    private ArrayList<STUDENT_SUBJECT> getStudentSubjects() throws Exception {
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        studentSubjectQM.addWhere("semesterData", ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getId());
        studentSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, studentEducation.getId());
        studentSubjectQM.addOrder("id");
        ArrayList<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        try {
            studentSubjects.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentSubjectQM));
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

//        Map<SUBJECT, EMPLOYEE> subjectEmployeeMap = new HashMap<>();
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
//                    subjectEmployeeMap.put(subject, employee);
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
}