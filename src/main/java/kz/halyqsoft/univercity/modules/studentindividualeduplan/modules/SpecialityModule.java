package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.where.ECriteria;

public class SpecialityModule extends BaseModule{

    public SpecialityModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(SPECIALITY.class, baseModule, studentPlan);
        getMainGM().getQueryModel().addWhere("deleted" , ECriteria.EQUAL,false);
        getMainGM().getQueryModel().addWhereAnd("department", ECriteria.EQUAL, baseModule.getMainGW().getSelectedEntity().getId());
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(getMainGW())){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                studentPlan.getMainVL().removeAllComponents();
                studentPlan.getMainVL().addComponent(new GroupModule(this, studentPlan));
            }
        }
    }
}
