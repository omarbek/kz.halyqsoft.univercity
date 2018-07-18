package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
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

    public SemesterPanel(IndividualEducationPlanView registrationView, SEMESTER s) throws Exception {
        this.s = s;

        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);
    }

    public void initPanel() throws Exception {
        HorizontalLayout HL = new HorizontalLayout();
        setHeight("400");

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

        for(PAIR_SUBJECT subject : subjects){
            if(map.get(subject.getPairNumber())==null){
                ArrayList<SUBJECT> arrayList = new ArrayList<>();
                arrayList.add(subject.getSubject());
                map.put(subject.getPairNumber() , arrayList);
            }else {
                map.get(subject.getPairNumber()).add(subject.getSubject());
            }
        }
        ArrayList<OptionGroup> optionGroups = new ArrayList<>();
        for(Map.Entry<Integer,ArrayList<SUBJECT>> e: map.entrySet()) {
            OptionGroup subjectsOG = new OptionGroup(getUILocaleUtil().getCaption("subjectsOG"));
            subjectsOG.setMultiSelect(false);
            subjectsOG.setNullSelectionAllowed(false);
            subjectsOG.setImmediate(true);
            for(SUBJECT subject : e.getValue() ) {
                subjectsOG.addItem(subject);
            }
            subjectsOG.addListener(new Listener() {
                @Override
                public void componentEvent(Event event) {
                    subjectsOG.getValue();
                }
            });
            optionGroups.add(subjectsOG);
            getContent().addComponent(subjectsOG);
        }

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();
                QueryModel<SEMESTER_SUBJECT> semesterSubjectQM = new QueryModel<>(SEMESTER_SUBJECT.class);
                QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
                for(OptionGroup sub:optionGroups) {
                    ENTRANCE_YEAR entranceYear = CommonUtils.getCurrentSemesterData().getYear();
                    SEMESTER_PERIOD semesterPeriod = null;
                    for(PAIR_SUBJECT pairSubject:subjects) {
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
                    STUDENT_EDUCATION studentEdu = studentEducation;
                    studentSubject.setStudentEducation(studentEdu);
                    studentSubject.setRegDate(new Date());
                    studentSubject.setSubject(semesterSubject);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                create(studentSubject);
                    } catch (Exception e) {
                        CommonUtils.showMessageAndWriteLog("Unable to save subject", e);
                    }

                }
                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                    }
                });
            }
        });
        getContent().addComponent(saveButton);
        getContent().setComponentAlignment(saveButton,Alignment.MIDDLE_CENTER);
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