package kz.halyqsoft.univercity.modules.studentsubject;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentSubject;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 14.09.2018
 */
public class StudentSubjectView extends AbstractTaskView implements EntityListener {

    private GridWidget studentSubjectGW;

    public StudentSubjectView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        ComboBox studentCB = new ComboBox();
        studentCB.setNullSelectionAllowed(false);
        studentCB.setTextInputAllowed(true);
        studentCB.setFilteringMode(FilteringMode.CONTAINS);
        studentCB.setPageLength(10);
        QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
        BeanItemContainer<V_STUDENT> studentBIC = new BeanItemContainer<>(V_STUDENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentQM));
        studentCB.setContainerDataSource(studentBIC);
        getContent().addComponent(studentCB);
        getContent().setComponentAlignment(studentCB, Alignment.MIDDLE_CENTER);
        studentCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refresh((V_STUDENT) studentCB.getValue());
            }
        });

        studentSubjectGW = new GridWidget(VStudentSubject.class);
        studentSubjectGW.addEntityListener(this);
        studentSubjectGW.showToolbar(false);

        DBGridModel studentSubjectGM = (DBGridModel) studentSubjectGW.getWidgetModel();
        studentSubjectGM.setRowNumberVisible(true);
        studentSubjectGM.setRefreshType(ERefreshType.MANUAL);

        refresh((V_STUDENT) studentCB.getValue());

        getContent().addComponent(studentSubjectGW);
        getContent().setComponentAlignment(studentSubjectGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh(V_STUDENT student) {
        try {
            if (student != null) {
                String sql = "SELECT " +
                        "  stu_subj.id, " +
                        "  'code'                   code, " +
                        "  module.module_short_name moduleType, " +
                        "  subj.name_" + CommonUtils.getLanguage() + "             subjectName, " +
                        "  credit.credit, " +
                        "  ects.ects, " +
                        "  sem.semester_name        semester, " +
                        "  teacher.fio              tutor, " +
                        "  control.type_name        examType " +
                        "FROM student_subject stu_subj " +
                        "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id " +
                        "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL " +
                        "  INNER JOIN users usr ON student.id = usr.id " +
                        "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id " +
                        "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id " +
                        "  INNER JOIN semester sem " +
                        "    ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id " +
                        "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id " +
                        "  INNER JOIN creditability credit ON subj.creditability_id = credit.id " +
                        "  INNER JOIN ects ON subj.ects_id = ects.id " +
                        "  INNER JOIN subject_module module ON subj.module_id = module.id " +
                        "  INNER JOIN control_type control ON subj.control_type_id = control.id " +
                        "  INNER JOIN teacher_subject teach_subj ON subj.id = teach_subj.subject_id " +
                        "  INNER JOIN student_teacher_subject stu_teach_subj ON stu_teach_subj.teacher_subject_id = teach_subj.id " +
                        "  INNER JOIN (SELECT " +
                        "                id, " +
                        "                trim(LAST_NAME || ' ' || FIRST_NAME || ' ' || coalesce(MIDDLE_NAME, '')) fio " +
                        "              FROM users) teacher ON teacher.id = teach_subj.employee_id " +
                        "WHERE CURRENT_DATE BETWEEN sem_data.begin_date AND sem_data.end_date " +
                        "      AND usr.deleted = FALSE AND usr.locked = FALSE AND usr.id = ?1";
                Map<Integer, Object> params = new HashMap<>();
                params.put(1, student.getId().getId());
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                        sql, params);
                List<VStudentSubject> list = new ArrayList<>();
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VStudentSubject studentSubject = new VStudentSubject();
                        studentSubject.setId(ID.valueOf((long) oo[0]));
                        studentSubject.setCode((String) oo[1]);
                        studentSubject.setModuleType((String) oo[2]);
                        studentSubject.setSubjectName((String) oo[3]);
                        studentSubject.setCredit(((BigDecimal) oo[4]).intValue());
                        studentSubject.setEcts(((BigDecimal) oo[5]).intValue());
                        studentSubject.setSemester((String) oo[6]);
                        studentSubject.setTutor((String) oo[7]);
                        studentSubject.setExamType((String) oo[8]);
                        list.add(studentSubject);
                    }
                }
                ((DBGridModel) studentSubjectGW.getWidgetModel()).setEntities(list);
                studentSubjectGW.refresh();
            }
        } catch (Exception ex) {
            ex.printStackTrace();//TODO catch
        }
    }
}
