package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class IndividualPlanDialog extends WindowUtils {

    private String userCode;
    STUDENT user = null;
    private boolean edit;
    IndividualPlanDialog(STUDENT user,boolean edit) {
        this.user = user;
        this.edit = edit;
        init(null, null);
        this.setModal(true);
    }

    @Override
    protected String createTitle() {
        return "individual plan";//TODO
    }

    @Override
    protected void refresh() throws Exception {
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        try {
            if(edit){
                checkIfStudentHasPLanOrDelete(user,edit);
            }
            IndividualPlanTabs ts = new IndividualPlanTabs(user, edit);
            mainVL.addComponent(ts);
            mainVL.setExpandRatio(ts, 1);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to run individual plan tabs",e);
        }

        return mainVL;
    }


    public static boolean checkIfStudentHasPLanOrDelete(STUDENT student, boolean needToDelete){

        QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
        semesterDataQM.addWhere("year" , ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getYear().getId());
        List<SEMESTER_DATA> semesterDatas = null;
        try{
            semesterDatas = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterDataQM);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog(e.getMessage(),e);
            e.printStackTrace();
        }
        List<ID> ids = new ArrayList<>();
        if(semesterDatas != null){
            for(SEMESTER_DATA ss : semesterDatas){
                ids.add(ss.getId());
            }
        }else{
            ids.add(CommonUtils.getCurrentSemesterData().getId());
        }
        QueryModel<STUDENT_SUBJECT> studentSubjectQM = new QueryModel<>(STUDENT_SUBJECT.class);
        studentSubjectQM.addWhere("studentEducation" , ECriteria.EQUAL
                , student.getLastEducation().getId());
        studentSubjectQM.addWhereAnd("deleted" , ECriteria.EQUAL, false);
        studentSubjectQM.addWhereInAnd("semesterData" , ids);
        ArrayList<STUDENT_SUBJECT> studentSubjects = new ArrayList<>();
        try{
            studentSubjects.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentSubjectQM));
        }catch (Exception e){
            Message.showError(e.getMessage());
            e.printStackTrace();
        }

        for(STUDENT_SUBJECT ss : studentSubjects){
            QueryModel<STUDENT_TEACHER_SUBJECT> studentTeacherSubjectQM = new QueryModel<>(STUDENT_TEACHER_SUBJECT.class);
            studentTeacherSubjectQM.addWhere("studentEducation", ECriteria.EQUAL, ss.getStudentEducation().getId());
            FromItem fi = studentTeacherSubjectQM.addJoin(EJoin.INNER_JOIN ,"teacherSubject" , TEACHER_SUBJECT.class, "id");
            studentTeacherSubjectQM.addWhere(fi ,"subject" , ECriteria.EQUAL , ss.getSubject().getSubject().getId() );

            try{
                List<STUDENT_TEACHER_SUBJECT> stsList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentTeacherSubjectQM);
                if(stsList.size()>0){
                    if(needToDelete){
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(stsList);
                    }
                    if(!needToDelete){
                        return true;
                    }
                }
            }catch (Exception e){
                CommonUtils.showMessageAndWriteLog(e.getMessage() , e);
                e.printStackTrace();
            }

            if(needToDelete){
                ss.setDeleted(true);
            }
        }

        if(needToDelete){
            if(studentSubjects.size()>0){
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentSubjects);
                }catch (Exception e){
                    CommonUtils.showMessageAndWriteLog(e.getMessage() , e);
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
