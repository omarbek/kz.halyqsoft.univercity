package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.where.ECriteria;

public class FacultyModule extends BaseModule{

    public FacultyModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(DEPARTMENT.class, baseModule, studentPlan);
        getMainGM().getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);
        getMainGM().getQueryModel().addWhereNullAnd("parent" );
        getMainGM().getQueryModel().addWhereAnd("fc" , ECriteria.EQUAL, false );
        getMainGM().getFormModel().getFieldModel("parent").setInView(false);
        getMainGM().getFormModel().getFieldModel("parent").setInEdit(false);

    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(getMainGW())){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                studentPlan.getMainVL().removeAllComponents();
                studentPlan.getMainVL().addComponent(new SecondLevelFaculty(this, studentPlan));
            }
        }
    }
}
