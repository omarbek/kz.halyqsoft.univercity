package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.tree.CommonTree;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Author Omarbek
 * Created 12.11.2008 22:13:59
 */
@Entity
public class TASKS extends AbstractTask implements CommonTree<TASKS> {

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private TASKS parent;

    @OneToMany(mappedBy = "parent")
    private List<TASKS> children;

    @OneToMany(mappedBy = "task")
    private List<TASK_FUNCTIONS> taskFunctions = new ArrayList<>();

    public TASKS() {
    }

    @Override
    public TASKS getParent() {
        return parent;
    }

    @Override
    public void setParent(TASKS parent) {
        this.parent = parent;
    }

    @Override
    public List<TASKS> getChildren() {
        return children;
    }

    public List<TASK_FUNCTIONS> getTaskFunctions() {
        return taskFunctions;
    }

    public void setTaskFunctions(List<TASK_FUNCTIONS> taskFunctions) {
        this.taskFunctions = taskFunctions;
    }

    @Override
    public boolean hasParent() {
        return (parent != null);
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void setDeleted(boolean b) {
    }
}
