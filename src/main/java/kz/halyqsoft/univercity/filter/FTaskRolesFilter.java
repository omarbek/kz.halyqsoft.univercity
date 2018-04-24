package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.TASKS;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FTaskRolesFilter extends AbstractFilterBean {

    private ROLES roleName;
    private TASKS taskName;

    public FTaskRolesFilter() {
    }

    public ROLES getRoleName() {
        return roleName;
    }

    public void setRoleName(ROLES roleName) {
        this.roleName = roleName;
    }

    public TASKS getTaskName() {
        return taskName;
    }

    public void setTaskName(TASKS taskName) {
        this.taskName = taskName;
    }

    @Override
    public boolean hasFilter() {
        return (!(taskName == null && roleName == null));
    }
}
