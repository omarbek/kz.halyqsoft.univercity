package kz.halyqsoft.univercity.modules.changestudyyear.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.changestudyyear.view.ProblemStudentsView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChooseStatusDialog extends AbstractDialog{

    public ChooseStatusDialog(ProblemStudentsView problemStudentsView) throws Exception{

        ComboBox statusCB = new ComboBox(getUILocaleUtil().getEntityLabel(STUDENT_STATUS.class));
        statusCB.setNullSelectionAllowed(true);
        statusCB.setTextInputAllowed(true);
        statusCB.setFilteringMode(FilteringMode.CONTAINS);
        statusCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<STUDENT_STATUS> studentStatusQueryModel = new QueryModel<>(STUDENT_STATUS.class);
        BeanItemContainer<STUDENT_STATUS> studentStatusBeanItemContainer = new BeanItemContainer<>(STUDENT_STATUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentStatusQueryModel));
        statusCB.setContainerDataSource(studentStatusBeanItemContainer);

        Button saveBtn = CommonUtils.createSaveButton();
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                if(statusCB.getValue()==null){
                    Message.showError(getUILocaleUtil().getCaption("choose.status"));
                }else{
                    List<ID> ids = new ArrayList<>();
                    for(Entity entity: problemStudentsView.getStudentGW().getSelectedEntities()){
                        ids.add(entity.getId());
                    }

                    QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    studentEducationQM.addWhereNull("child");
                    studentEducationQM.addWhereInAnd("student" ,ids);
                    studentEducationQM.addWhereNotNullAnd("studyYear" );

                    List<STUDENT_EDUCATION> studentEducations = new ArrayList<>();

                    try{
                        studentEducations.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM));
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    for(STUDENT_EDUCATION se : studentEducations){
                        if(se.getStudyYear()!=null){

                            STUDENT_EDUCATION newSE = new STUDENT_EDUCATION();
                            newSE.setCreated(new Date());
                            newSE.setChair(se.getChair());
                            newSE.setEducationType(se.getEducationType());
                            newSE.setFaculty(se.getFaculty());
                            newSE.setGroups(se.getGroups());
                            newSE.setEntryDate(se.getEntryDate());
                            newSE.setLanguage(se.getLanguage());
                            newSE.setStudentSchedules(se.getStudentSchedules());
                            newSE.setStudent(se.getStudent());
                            newSE.setStudyYear(se.getStudyYear());
                            newSE.setSpeciality(se.getSpeciality());
                            newSE.setStatus((STUDENT_STATUS) statusCB.getValue());

                            try{
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newSE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            se.setChild(newSE);
                            se.setUpdated(new Date());

                        }
                    }



                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducations);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    problemStudentsView.doFilter(problemStudentsView.getChangeToNextYearGroupFilterPanel().getFilterBean());
                    CommonUtils.showSavedNotification();
                    close();
                }

            }
        });
        setWidth(30,Unit.PERCENTAGE);
        setHeight(30,Unit.PERCENTAGE);
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        getContent().addComponent(statusCB);
        getContent().addComponent(saveBtn);

        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("choose.status");
    }
}
