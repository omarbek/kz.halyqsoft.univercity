package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

public class GroupModule extends BaseModule{

    public GroupModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(GROUPS.class, baseModule, studentPlan);
        QueryModel qm = getMainGM().getQueryModel();
        qm.addWhere("deleted", ECriteria.EQUAL, false);
        qm.addWhereAnd("studyYear", ECriteria.EQUAL, baseModule.getPrevPage().getPrevPage().getPrevPage().getMainGW().getSelectedEntity().getId());
        qm.addWhereAnd("speciality", ECriteria.EQUAL, baseModule.getMainGW().getSelectedEntity().getId());
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(getMainGW())){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                studentPlan.getMainVL().removeAllComponents();
                studentPlan.getMainVL().addComponent(new StudentModule(this, studentPlan));
            }
        }
    }
}
