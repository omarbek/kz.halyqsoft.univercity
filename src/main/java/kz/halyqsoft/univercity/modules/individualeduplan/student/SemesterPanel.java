package kz.halyqsoft.univercity.modules.individualeduplan.student;

import com.vaadin.ui.OptionGroup;
import javafx.scene.control.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
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
public class SemesterPanel extends AbstractCommonPanel implements EntityListener {

    private SEMESTER semester;
    private CURRICULUM curriculum;
    private RegistrationView registrationView;
    private List<STUDENT_EDUCATION> studentEducation;
    private ComboBox specialityCB;

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public SemesterPanel(RegistrationView registrationView, SEMESTER semester) {
        this.registrationView = registrationView;
        this.semester = semester;
         }

    public void initPanel() throws Exception {
        String titleResource = "semester." + semester.getId().toString();

        QueryModel<ELECTIVE_BINDED_SUBJECT> subjectsQM = new QueryModel<>(ELECTIVE_BINDED_SUBJECT.class);
        for (STUDENT_EDUCATION st : studentEducation) {


            STUDY_YEAR study_year = st.getStudyYear();

            QueryModel<ELECTIVE_BINDED_SUBJECT> electiveBindedSubjectQM = new QueryModel<>(
                    ELECTIVE_BINDED_SUBJECT.class);
            FromItem catFI = electiveBindedSubjectQM.addJoin(EJoin.INNER_JOIN, "catalogElectiveSubjects",
                    CATALOG_ELECTIVE_SUBJECTS.class, "id");
            electiveBindedSubjectQM.addWhere(catFI, "speciality", ECriteria.EQUAL, st.getSpeciality().
                    getId());

           String sql = "select * " +
                   "from elective_binded_subject " +
                   "where semester_id " +
                   "IN " +
                   "(Select id " +
                   "from semester " +
                   "where study_year_id= " + st.getStudyYear() + ")";
            Map<Integer, Object> params = new HashMap<Integer, Object>();
            params.put(1, st.getStudyYear().getId().getId());

            List<ELECTIVE_BINDED_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(electiveBindedSubjectQM);
            List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

            List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
            //   sub = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params, ELECTIVE_BINDED_SUBJECT.class);

//
            List sub = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
//
//            List<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
            for (ELECTIVE_BINDED_SUBJECT subject : subjects) {
            OptionGroup subjectsOG = new OptionGroup();

                    subjectsOG.addItem(subject);

            getContent().addComponent(subjectsOG);

            }
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }


    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }

    protected SemesterPanel() {
        super();
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }
}


