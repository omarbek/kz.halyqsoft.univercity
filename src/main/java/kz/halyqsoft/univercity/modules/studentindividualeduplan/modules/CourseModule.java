package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;

public class CourseModule extends BaseModule{

    public CourseModule(StudentPlan studentPlan) {
        super(STUDY_YEAR.class, null , studentPlan);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(getMainGW())){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                studentPlan.getMainVL().removeAllComponents();
                studentPlan.getMainVL().addComponent(new FacultyModule(this, studentPlan));
            }
        }
    }
}
