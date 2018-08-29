package kz.halyqsoft.univercity.modules.examadmission;

import kz.halyqsoft.univercity.entity.beans.univercity.NON_ADMISSION_EXAM;
import kz.halyqsoft.univercity.entity.beans.univercity.NON_ADMISSION_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

public class ExamAdmissionView extends AbstractTaskView implements EntityListener{
    private GridWidget nonAdmissionExam;
    private GridWidget nonAdmissionSubject;
    public ExamAdmissionView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        nonAdmissionExam = new GridWidget(NON_ADMISSION_EXAM.class);
        nonAdmissionExam.setImmediate(true);
        DBGridModel nonAdmissionExamGW = (DBGridModel)nonAdmissionExam.getWidgetModel();
        FKFieldModel fkFieldModel = (FKFieldModel) nonAdmissionExamGW.getFormModel().getFieldModel("student");
        fkFieldModel.getQueryModel().addJoin(EJoin.INNER_JOIN, "id" , STUDENT.class ,"id");
        nonAdmissionSubject = new GridWidget(NON_ADMISSION_SUBJECT.class);
        getContent().addComponent(nonAdmissionExam);
        getContent().addComponent(nonAdmissionSubject);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        super.handleEntityEvent(ev);
    }
}
