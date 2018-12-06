package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class IndividualEducationPlanView extends AbstractTaskView {

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
                    new IndividualPlanDialog(userTF.getValue());
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
            IndividualPlanTabs ts = new IndividualPlanTabs(user, false);

            getContent().addComponent(ts);
            getContent().setComponentAlignment(ts, Alignment.MIDDLE_CENTER);
            getContent().setExpandRatio(ts, 1);
        } else {
            Label noSuchStudentLabel = new Label(getUILocaleUtil().getMessage("not.found"));
            getContent().addComponent(noSuchStudentLabel);
            getContent().setComponentAlignment(noSuchStudentLabel, Alignment.MIDDLE_CENTER);
        }
    }
}