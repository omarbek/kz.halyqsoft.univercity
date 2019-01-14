package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Assylkhan
 * on 15.12.2018
 * @project kz.halyqsoft.univercity
 */
public class TeacherDialog extends AbstractDialog{

    private String title;
    private boolean isNew;
    private ScheduleView prevView;
    private ComboBox teacherCB;
    public TeacherDialog(ScheduleView prevView,Object source, Entity e, boolean isNew, int buttonId){
        this.isNew = isNew;
        this.prevView = prevView;
        teacherCB = new ComboBox();
        SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) e;
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        FKFieldModel teacherFM  = (FKFieldModel)prevView.scheduleDetailGM.getFormModel().getFieldModel("teacher");
        QueryModel scheduleDetailTeacherQM = teacherFM.getQueryModel();
        scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id" , USERS.class ,"id");
        FromItem vEmployeeFI = scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id", V_EMPLOYEE.class,"id");
        scheduleDetailTeacherQM.addWhere(vEmployeeFI , "employeeType" , ECriteria.EQUAL , EMPLOYEE_TYPE.TEACHER_ID);
        FromItem tsFI = scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN, "id" , TEACHER_SUBJECT.class, "employee");
        scheduleDetailTeacherQM.addWhereAnd(tsFI , "subject",ECriteria.EQUAL , scheduleDetail.getSubject().getId());

        try{
            BeanItemContainer bic = new BeanItemContainer(EMPLOYEE.class , CommonUtils.getQuery().lookup(scheduleDetailTeacherQM));
            teacherCB.setContainerDataSource(bic);
            teacherCB.setCaption(getUILocaleUtil().getEntityFieldLabel(TEACHER_SUBJECT.class , "teacher"));
        }catch (Exception ex){
            ex.printStackTrace();
        }


        Button saveBtn = new Button(getUILocaleUtil().getCaption("save"));
        saveBtn.setIcon(new ThemeResource("img/button/add.png"));
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if(teacherCB.getValue()!=null){

                    TEACHER_SUBJECT teacherSubject = null;
                    QueryModel<TEACHER_SUBJECT> teacherSubjectQM = new QueryModel<>(TEACHER_SUBJECT.class);
                    teacherSubjectQM.addWhere("employee" , ECriteria.EQUAL , ((EMPLOYEE)teacherCB.getValue()).getId());
                    teacherSubjectQM.addWhereAnd("subject" , ECriteria.EQUAL , scheduleDetail.getSubject().getId());
                    try{
                        teacherSubject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(teacherSubjectQM);
                    }catch (Exception ex){
                        CommonUtils.showMessageAndWriteLog("Unable to find teacher subject" , ex);
                        return;
                    }
                    SEMESTER semester = getSemester(scheduleDetail.getGroup());
                    if(semester==null){
                        return;
                    }

                    if(isNew){
                        scheduleDetail.setWeekDay((WEEK_DAY) prevView.weekDayGW.getSelectedEntity());
                        scheduleDetail.setSemesterData((SEMESTER_DATA) prevView.semesterDataCB.getValue());
                        scheduleDetail.setTeacher((EMPLOYEE) teacherCB.getValue());

                        try{
                            if(prevView.isAllEmpty(scheduleDetail)){
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
                                List<STUDENT_SUBJECT> studentSubjectList = prevView.createStudentSubjects(scheduleDetail);

                                List<STUDENT_TEACHER_SUBJECT> studentTeacherSubjects = new ArrayList<>();
                                for(STUDENT_SUBJECT studentSubject : studentSubjectList){
                                    STUDENT_TEACHER_SUBJECT studentTeacherSubject = new STUDENT_TEACHER_SUBJECT();
                                    studentTeacherSubject.setStudentEducation(studentSubject.getStudentEducation());
                                    studentTeacherSubject.setTeacherSubject(teacherSubject);
                                    studentTeacherSubject.setSemester(semester);
                                    studentTeacherSubjects.add(studentTeacherSubject);
                                }
                                CommonUtils.getQuery().create(studentTeacherSubjects);
                                prevView.refresh();
                            }
                        }catch (Exception ex){
                            CommonUtils.showMessageAndWriteLog("unable to create student teacher subjects" , ex);
                        }
                    }else{
                        try{
                            SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) e;
                            prevView.deleteStudentSubjects(scheduleDetail);
                            scheduleDetail.setTeacher((EMPLOYEE) teacherCB.getValue());
                            if(prevView.isAllEmpty(scheduleDetail)){
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(scheduleDetail);
                                prevView.refresh();
                            }
                            prevView.createStudentSubjects(scheduleDetail);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    close();
                }else{
                    Message.showError(getUILocaleUtil().getMessage("choose.field"));
                }
            }
        });
        getContent().addComponent(teacherCB);
        getContent().addComponent(saveBtn);
        getContent().setSpacing(true);
        setClosable(true);
        setHeight("20%");
        setWidth("40%");
        AbstractWebUI.getInstance().addWindow(this);
    }

    public static SEMESTER getSemester(GROUPS groups){
        SEMESTER semester = null;
        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
        semesterQM.addWhere("studyYear" , ECriteria.EQUAL , groups.getStudyYear().getId());
        semesterQM.addWhereAnd("semesterPeriod" , ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getSemesterPeriod().getId());
        try{
            semester = CommonUtils.getQuery().lookupSingle(semesterQM);
        }catch (Exception e){
            CommonUtils.showMessageAndWriteLog("unable to find semester" , e);

        }
        return semester;
    }

    @Override
    protected String createTitle() {
        return title;
    }
}
