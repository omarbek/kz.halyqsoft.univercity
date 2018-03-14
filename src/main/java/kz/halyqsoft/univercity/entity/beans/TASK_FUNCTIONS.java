package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractTaskFunction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created 09.09.2011 16:12:46
 */
@Entity
public class TASK_FUNCTIONS extends AbstractTaskFunction {

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")})
    private TASKS task;

    public TASK_FUNCTIONS() {
    }

    public TASKS getTask() {
        return task;
    }

    public void setTask(TASKS task) {
        this.task = task;
    }
}
