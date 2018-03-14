package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractRoleTask;
import org.r3a.common.entity.beans.AbstractRoleTaskFunction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created 09.09.2011 17:26:20
 */
@Entity
public class ROLE_TASK_FUNCTIONS extends AbstractRoleTaskFunction {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROLE_TASK_ID", referencedColumnName = "ID", nullable = false)})
    private ROLE_TASKS roleTask;

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TASK_FUNCTION_ID", referencedColumnName = "ID", nullable = false)})
    private TASK_FUNCTIONS taskFunction;

    public ROLE_TASK_FUNCTIONS() {
    }

    @Override
    public ROLE_TASKS getRoleTask() {
        return roleTask;
    }

    public void setRoleTask(ROLE_TASKS roleTask) {
        this.roleTask = roleTask;
    }

    @Override
    public void setRoleTask(AbstractRoleTask roleTask) {
        setRoleTask((ROLE_TASKS) roleTask);
    }

    @Override
    public TASK_FUNCTIONS getTaskFunction() {
        return taskFunction;
    }

    public void setTaskFunction(TASK_FUNCTIONS taskFunction) {
        this.taskFunction = taskFunction;
    }

    @Override
    public String toString() {
        return roleTask.toString() + " | " + taskFunction.toString();
    }
}
