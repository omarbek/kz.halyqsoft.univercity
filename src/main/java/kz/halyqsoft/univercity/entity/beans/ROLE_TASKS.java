package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractRole;
import org.r3a.common.entity.beans.AbstractRoleTask;
import org.r3a.common.entity.beans.AbstractTask;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * Author Omarbek
 * Created 12.11.2008 22:31:36
 */
@Entity
public class ROLE_TASKS extends AbstractRoleTask {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", nullable = false)})
    private ROLES role;

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", nullable = false)})
    private TASKS task;

    public ROLE_TASKS() {
    }



    @Override
    public ROLES getRole() {
        return role;
    }

    public void setRole(ROLES role) {
        this.role = role;
    }

    @Override
    public void setRole(AbstractRole role) {
        setRole((ROLES) role);
    }

    @Override
    public TASKS getTask() {
        return task;
    }

    public void setTask(TASKS task) {
        this.task = task;
    }

    @Override
    public void setTask(AbstractTask task) {
        setTask((TASKS) task);
    }

    @Override
    public String toString() {
        return role.toString() + " - " + task.toString();
    }
}
