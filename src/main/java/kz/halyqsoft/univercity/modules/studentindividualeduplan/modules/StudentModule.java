package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_COORDINATOR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.regapplicants.TableForm;
import kz.halyqsoft.univercity.modules.regapplicants.TableFormRus;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.dialogs.LanguageDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class StudentModule extends BaseModule{

    private static final int FATHER = 1;
    private static final int MOTHER = 2;
    private static final int ADDRESS_FACT = 2;

    private Button downloadBtn;
    private Button clearBtn;
    public StudentModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(V_STUDENT.class, baseModule, studentPlan);
        getMainGW().setMultiSelect(true);
        getMainGM().getQueryModel().addWhere("group" , ECriteria.EQUAL,baseModule.getMainGW().getSelectedEntity().getId());

        downloadBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("download"));
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE);
        downloadBtn.setIcon(FontAwesome.DOWNLOAD);
        downloadBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(getMainGW().getSelectedEntities().size() > 0){
                    List<STUDENT> studentList = new ArrayList<>();
                    for(Entity vStudent : getMainGW().getSelectedEntities()){
                        try{
                            STUDENT student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean .class)
                                    .lookup(STUDENT.class , vStudent.getId());

                            studentList.add(student);
                        }catch (Exception e){
                            e.printStackTrace();
                            return;
                        }
                    }
                    LanguageDialog languageDialog = new LanguageDialog(studentList);
                }else{
                    Message.showError(CommonUtils.getUILocaleUtil().getMessage("choose.field"));
                }
            }
        });

        clearBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("clear"));
        clearBtn.setStyleName(ValoTheme.BUTTON_LARGE);
        clearBtn.setIcon(FontAwesome.TRASH);
        clearBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(getMainGW().getSelectedEntities().size() > 0){
                    List<STUDENT> studentList = new ArrayList<>();
                    for(Entity vStudent : getMainGW().getSelectedEntities()){
                        try{
                            STUDENT student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean .class)
                                    .lookup(STUDENT.class , vStudent.getId());

                            studentList.add(student);
                        }catch (Exception e){
                            e.printStackTrace();
                            return;
                        }
                    }
                    QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
                    semesterDataQM.addWhere("year" ,ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getYear().getId());
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
                    for(STUDENT student : studentList){
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
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(stsList);
                            }catch (Exception e){
                                CommonUtils.showMessageAndWriteLog(e.getMessage() , e);
                                e.printStackTrace();
                            }

                            ss.setDeleted(true);
                        }

                        if(studentSubjects.size()>0){
                            try{
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentSubjects);
                            }catch (Exception e){
                                CommonUtils.showMessageAndWriteLog(e.getMessage() , e);
                                e.printStackTrace();
                            }
                        }
                     }

                }else{
                    Message.showError(CommonUtils.getUILocaleUtil().getMessage("choose.field"));
                }
            }
        });
        getButtonsPanel().addComponent(downloadBtn);
        getButtonsPanel().addComponent(clearBtn);

    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }
}
