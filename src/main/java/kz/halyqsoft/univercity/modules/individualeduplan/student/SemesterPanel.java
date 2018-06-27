package kz.halyqsoft.univercity.modules.individualeduplan.student;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Mar 1, 2016 1:30:03 PMx
 */
@SuppressWarnings({"serial"})
public class SemesterPanel extends AbstractCommonPanel {

    private RegistrationView registrationView;
    private List<STUDENT_EDUCATION> studentEducation;
    int semesterId;

    public SemesterPanel(RegistrationView registrationView, int i) throws Exception {
        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM);
    }

    public void initPanel() throws Exception {

        QueryModel<ELECTIVE_BINDED_SUBJECT> subjectsQM = new QueryModel<>(ELECTIVE_BINDED_SUBJECT.class);
        for (STUDENT_EDUCATION st : studentEducation) {


            STUDY_YEAR study_year = st.getStudyYear();

            QueryModel<ELECTIVE_BINDED_SUBJECT> electiveBindedSubjectQM = new QueryModel<>(
                    ELECTIVE_BINDED_SUBJECT.class);
            FromItem catFI = electiveBindedSubjectQM.addJoin(EJoin.INNER_JOIN, "catalogElectiveSubjects",
                    CATALOG_ELECTIVE_SUBJECTS.class, "id");
            electiveBindedSubjectQM.addWhere(catFI, "speciality", ECriteria.EQUAL, st.getSpeciality().
                    getId());

//            String sql = "select * from elective_binded_subject where semester_id IN (Select id from semester where study_year_id= " + st.getStudyYear() + ")";
//            Map<Integer, Object> params = new HashMap<Integer, Object>();
//            params.put(1, st.getStudyYear().getId().getId());

            String sql = "select * from elective_binded_subject where semester_id= ?1";
            Map<Integer, Object> params = new HashMap<Integer, Object>();
            params.put(1, st.getStudyYear().getId().getId());
            STUDENT_EDUCATION sedu;

            List<ELECTIVE_BINDED_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(electiveBindedSubjectQM);
            List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

            List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
         //   sub = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params, ELECTIVE_BINDED_SUBJECT.class);

//
            List sub = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
//
//            List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
            //for (ELECTIVE_BINDED_SUBJECT subject : subjects) {
                OptionGroup subjectsOG = new OptionGroup();

//                    subjectsOG.addItem(e.getFirstSubject());
//                    subjectsOG.addItem(e.getSecondSubject());


                subjectsOG.addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();
                        //studentSubject.setStudentEducation(studentSubject);
                    }

                });
                getContent().addComponent(subjectsOG);

            //}


        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });
        getContent().addComponent(saveButton);
    }
}
}

