package kz.halyqsoft.univercity.modules.individualeduplan.student;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.OptionGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.modules.individualeduplan.student.RegistrationView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.AbstractCommonPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Mar 1, 2016 1:30:03 PMx
 */
@SuppressWarnings({"serial"})
public class SubjectPanel extends AbstractCommonPanel {

    private RegistrationView registrationView;

    public SubjectPanel(RegistrationView registrationView) throws Exception {
        this.registrationView = registrationView;
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        STUDENT_EDUCATION studentEducation=SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);
    }

    public void initPanel() throws Exception {
        QueryModel<ELECTIVE_BINDED_SUBJECT> subjectsQM = new QueryModel<>(ELECTIVE_BINDED_SUBJECT.class);
        List<ELECTIVE_BINDED_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(subjectsQM);
        List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        for (ELECTIVE_BINDED_SUBJECT subject : subjects) {
            OptionGroup subjectsOG = new OptionGroup();
            subjectsOG.addItem(subject.getFirstSubject().getId());
            subjectsOG.addItem(subject.getSecondSubject().getId());
            subjectsOG.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    STUDENT_SUBJECT studentSubject = new STUDENT_SUBJECT();
//                    studentSubject.set
                }
            });
            getContent().addComponent(subjectsOG);


        }
        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });
        getContent().addComponent(saveButton);
    }
}