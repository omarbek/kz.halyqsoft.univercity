package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.CustomMessageDialog;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class IndividualEducationPlanView extends AbstractTaskView {

    private boolean opened=false;
    private STUDENT user = null;

    public IndividualEducationPlanView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        if (CommonUtils.isCurrentUserHasAdminPrivileges()) {
            TextField userTF = new TextField();
            userTF.setCaption(getUILocaleUtil().getCaption("student.id"));
            getContent().addComponent(userTF);
            getContent().setComponentAlignment(userTF, Alignment.MIDDLE_CENTER);

            Button openButton = new Button(getUILocaleUtil().getCaption("open"));
            openButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if(userTF.getValue().trim().length()==0){
                        Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));
                        return;
                    }

                    QueryModel<STUDENT> userQM = new QueryModel<>(STUDENT.class);
                    FromItem fi = userQM.addJoin(EJoin.INNER_JOIN, "id" , V_STUDENT.class ,"id");
                    userQM.addWhere(fi ,"userCode", ECriteria.EQUAL, userTF.getValue());

                    try {
                        user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userQM);
                    } catch (Exception ignored) {
                    }

                    if(user!=null){
                        if(IndividualPlanDialog.checkIfStudentHasPLanOrDelete(user,false)){
                            CustomMessageDialog customMessageDialog = new CustomMessageDialog(getUILocaleUtil().getCaption("check.data") ,getUILocaleUtil().getCaption("student.already.has.a.plan") + "!  " + getUILocaleUtil().getCaption("student.edit"));
                            Button yesButton = new Button(getUILocaleUtil().getCaption("yes"));
                            yesButton.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    new IndividualPlanDialog(user ,true);
                                    customMessageDialog.close();
                                }
                            });
                            Button noButton = new Button(getUILocaleUtil().getCaption("no.just.open"));
                            noButton.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    new IndividualPlanDialog(user ,false);
                                    customMessageDialog.close();
                                }
                            });

                            Button cancelButton = new Button(getUILocaleUtil().getCaption("cancel"));
                            cancelButton.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    customMessageDialog.close();
                                }
                            });
                            customMessageDialog.getButtons().add(yesButton);
                            customMessageDialog.getButtons().add(noButton);
                            customMessageDialog.getButtons().add(cancelButton);
                            customMessageDialog.init();
                        }else {
                            new IndividualPlanDialog(user ,true);
                        }
                    }else{
                        Message.showError(getUILocaleUtil().getMessage("not.found"));
                    }

                }
            });
            getContent().addComponent(openButton);
            getContent().setComponentAlignment(openButton, Alignment.MIDDLE_CENTER);
        } else {
            initIndividualPlan(CommonUtils.getCurrentUser());
        }
    }

    private void initIndividualPlan(USERS user) throws Exception {
        if (user != null) {
            IndividualPlanTabs ts = new IndividualPlanTabs(user,false);
            getContent().addComponent(ts);
            getContent().setComponentAlignment(ts, Alignment.MIDDLE_CENTER);
            getContent().setExpandRatio(ts, 1);
        } else {
            Label noSuchStudentLabel = new Label("no such student");
            getContent().addComponent(noSuchStudentLabel);//TODO resource
            getContent().setComponentAlignment(noSuchStudentLabel, Alignment.MIDDLE_CENTER);
        }
    }
}