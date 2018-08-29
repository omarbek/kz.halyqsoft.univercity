package kz.halyqsoft.univercity.modules.group.tab.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.modules.group.tab.ManualCreationTab;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;


public class GroupEditDialog extends AbstractDialog{

    private String title;
    private ManualCreationTab prevView;
    public GroupEditDialog(String title, STUDENT_EDUCATION studentEducation, ManualCreationTab prevView) throws Exception{
        this.title = title;
        this.prevView = prevView;
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        groupsQM.addWhere("deleted" , ECriteria.EQUAL, false);
        groupsQM.addWhere("speciality" , ECriteria.EQUAL, studentEducation.getSpeciality().getId());
        groupsQM.addWhere("language" , ECriteria.EQUAL, studentEducation.getLanguage().getId());

        BeanItemContainer groupsBIC = new BeanItemContainer(GROUPS.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));

        ComboBox groupsCB = new ComboBox(getUILocaleUtil().getEntityFieldLabel(STUDENT_EDUCATION.class, "groups"));
        groupsCB.setContainerDataSource(groupsBIC);
        groupsCB.setSizeFull();
        groupsCB.setImmediate(true);
        if(studentEducation.getGroups()!=null){
            groupsCB.setValue(studentEducation.getGroups());
        }
        Button saveBtn = CommonUtils.createSaveButton();

        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                studentEducation.setGroups((GROUPS) groupsCB.getValue());
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducation);
                    prevView.doFilter(prevView.getFilterPanel().getFilterBean());
                }catch (Exception e){
                    e.printStackTrace();
                }
                CommonUtils.showSavedNotification();
                close();
            }
        });

        getContent().addComponent(groupsCB);

        getContent().addComponent(saveBtn);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return title;
    }
}
