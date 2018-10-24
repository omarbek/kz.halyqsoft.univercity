package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.where.ECriteria;

public class SecondLevelFaculty extends BaseModule{
    public SecondLevelFaculty(BaseModule baseModule, StudentPlan studentPlan) {
        super(DEPARTMENT.class, baseModule, studentPlan);
        getMainGM().getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);
        getMainGM().getQueryModel().addWhereAnd("parent" , ECriteria.EQUAL, baseModule.getMainGW().getSelectedEntity().getId());
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(getMainGW())){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                studentPlan.getMainVL().removeAllComponents();
                studentPlan.getMainVL().addComponent(new SpecialityModule(this, studentPlan));
            }
        }
    }
}
