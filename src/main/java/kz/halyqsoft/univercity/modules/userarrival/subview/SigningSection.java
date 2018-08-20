package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.SignDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.SampleEntityListener;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class SigningSection extends SampleEntityListener{
    private VerticalLayout mainVL;
    private GridWidget usersGW;
    private DBGridModel usersGM;
    public SigningSection(){
        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        mainVL.setSizeFull();

        usersGW = new GridWidget(USERS.class);
        usersGW.setImmediate(true);
        usersGW.addEntityListener(this);
        usersGW.showToolbar(false);

        List <ID>ids = new ArrayList();
        ids.add(ID.valueOf(1));
        ids.add(ID.valueOf(2));

        usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);
        usersGM.getQueryModel().addWhereNotInAnd("id", ids);
        usersGM.setRowNumberVisible(true);
        usersGM.setRowNumberWidth(80);

        mainVL.addComponent(usersGW);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(usersGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(usersGW.getSelectedEntity()!=null){
                    SignDialog signDialog = new SignDialog(CommonUtils.getUILocaleUtil().getCaption("manuallySign"), (USERS)usersGW.getSelectedEntity());
                }
            }
        }
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return super.preCreate(o, i);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
