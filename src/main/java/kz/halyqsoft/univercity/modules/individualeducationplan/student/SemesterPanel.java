package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.data.Item;
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
    private SEMESTER s;
    private List<SEMESTER_DATA> semesterData;
    private   HorizontalLayout layout;

    public SemesterPanel(IndividualEducationPlanView registrationView, SEMESTER s) throws Exception {
        this.s = s;

        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);
    }

    public void initPanel() throws Exception {
        HorizontalSplitPanel HL = new HorizontalSplitPanel();
        HL.setSplitPosition(50);
        HL.setHeight("300");

        QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
        subjectQM.addWhere("chair",ECriteria.EQUAL,studentEducation.getChair().getId());
        subjectQM.addWhere("deleted",false);
        subjectQM.addWhere("mandatory",true);
        List<SUBJECT> subject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(subjectQM);

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        OptionGroup subjectOG = new OptionGroup(getUILocaleUtil().getCaption("subjectOG"));
        subjectOG.setMultiSelect(true);
        subjectOG.setNullSelectionAllowed(false);
        subjectOG.setImmediate(true);

        for(SUBJECT sub: subject) {
            Item item = subjectOG.addItem(sub);
            subjectOG.addListener(new Listener() {
                @Override
                public void componentEvent(Event event) {
                    subjectOG.getValue();
                }
            });
            vl.addComponent(subjectOG);
        }
        HL.addComponent(vl);

        VerticalLayout vl2  = new VerticalLayout();
        vl2.setSpacing(true);
        QueryModel<PAIR_SUBJECT> subjectsQM = new QueryModel<>(PAIR_SUBJECT.class);
        FromItem FI = subjectsQM.addJoin(EJoin.INNER_JOIN, "electiveBindedSubject",
                ELECTIVE_BINDED_SUBJECT.class,"id");
        subjectsQM.addWhere(FI, "semester", ECriteria.EQUAL, s.getId());
        FromItem specFI = FI.addJoin(EJoin.INNER_JOIN,"catalogElectiveSubjects",
                CATALOG_ELECTIVE_SUBJECTS.class, "id");
        subjectsQM.addWhere(specFI,"speciality",ECriteria.EQUAL,
                studentEducation.getSpeciality().getId());
        subjectsQM.addWhere(specFI,"entranceYear",ECriteria.EQUAL,
                studentEducation.getStudent().getEntranceYear().getId());
        List<PAIR_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(subjectsQM);

        Map<Integer , ArrayList<SUBJECT>> map = new HashMap<>();

        for(PAIR_SUBJECT pairSubject : subjects){
            if(map.get(pairSubject.getPairNumber())==null){
                ArrayList<SUBJECT> arrayList = new ArrayList<>();
                arrayList.add(pairSubject.getSubject());
                map.put(pairSubject.getPairNumber() , arrayList);
            }else {
                map.get(pairSubject.getPairNumber()).add(pairSubject.getSubject());
            }
        }
        ArrayList<OptionGroup> optionGroups = new ArrayList<>();
        for(Map.Entry<Integer,ArrayList<SUBJECT>> e: map.entrySet()) {
            OptionGroup subjectsOG = new OptionGroup(getUILocaleUtil().getCaption("subjectsOG"));
            subjectsOG.setMultiSelect(false);
            subjectsOG.setNullSelectionAllowed(false);
            subjectsOG.setImmediate(true);
            for(SUBJECT subjectOfElective : e.getValue() ) {
                subjectsOG.addItem(subjectOfElective);
            }
            subjectsOG.addListener(new Listener() {
                @Override
                public void componentEvent(Event event) {
                    subjectsOG.getValue();
                }
            });
            optionGroups.add(subjectsOG);
            vl2.addComponent(subjectsOG);
        }
        HL.addComponent(vl2);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
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

                        ArrayList<STUDENT_SUBJECT>studentSubjects = new ArrayList<>();

                        for (OptionGroup sub : optionGroups) {
                            ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                            SEMESTER_PERIOD semesterPeriod = null;
                            for (PAIR_SUBJECT pairSubject : subjects) {
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

                        ArrayList<STUDENT_SUBJECT>studentSubjectAll = new ArrayList<>();

                            ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                            SEMESTER_PERIOD semesterPeriod = null;
                            for(PAIR_SUBJECT pairSubject:subjects) {
                                semesterPeriod = pairSubject.getElectveBindedSubject().getSemester().getSemesterPeriod();
                            }

                        Set<SUBJECT> subjects=(Set<SUBJECT>) subjectOG.getValue();
                        for(SUBJECT s: subjects) {
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

                        if(!studentSubjects.isEmpty() && !studentSubjectAll.isEmpty()){
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(studentSubjects);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        create(studentSubjectAll);
                                saveButton.setEnabled(false);
                            } catch (Exception e) {
                                CommonUtils.showMessageAndWriteLog("Unable to save subject", e);
                            }
                        }
                    }
                });
            }
        });

        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        studentSubjectQM.addWhere("semesterData" ,ECriteria.EQUAL,CommonUtils.getCurrentSemesterData().getId());
        studentSubjectQM.addWhere("studentEducation" ,ECriteria.EQUAL,studentEducation.getId());
        ArrayList<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        try{
            studentSubjects.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentSubjectQM));

        }catch (NoResultException e) {
            System.out.println(e.getMessage());
        }

        HL.setFirstComponent(vl2);
        HL.setFirstComponent(vl);
        getContent().addComponent(HL);

        if(studentSubjects.size()==0){
            getContent().addComponent(saveButton);
            getContent().setComponentAlignment(saveButton,Alignment.MIDDLE_CENTER);
        }else{
            for(OptionGroup gr :optionGroups){
                for(Object o : gr.getItemIds()){
                    for(STUDENT_SUBJECT ss : studentSubjects){
                        if(((SUBJECT)o).getId().getId().longValue()==(ss.getSubject().getSubject().getId().getId().longValue())){
                            gr.setValue(o);
                        }
                    }
                }
            }
        }
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
}