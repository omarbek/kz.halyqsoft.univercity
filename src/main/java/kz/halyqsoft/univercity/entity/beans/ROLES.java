package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.beans.AbstractRole;
import org.r3a.common.entity.tree.CommonTree;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Author Rahimjan A. Kenjegul
 * Created 25.10.2008 17:22:10
 */
@Entity
public class ROLES extends AbstractRole implements CommonTree<ROLES> {

    @OneToMany(mappedBy = "role")
    private List<USER_ROLES> userRoles;

    @OneToMany(mappedBy = "role")
    private List<ROLE_TASKS> roleTasks;

    public ROLES() {
    }

    public List<USER_ROLES> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<USER_ROLES> userRoles) {
        this.userRoles = userRoles;
    }

    public List<ROLE_TASKS> getRoleTasks() {
        return roleTasks;
    }

    public void setRoleTasks(List<ROLE_TASKS> roleTasks) {
        this.roleTasks = roleTasks;
    }

    @Override
    public List<ROLES> getChildren() {
        return null;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public ROLES getParent() {
        return null;
    }

    @Override
    public void setParent(ROLES parent) {
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void setDeleted(boolean b) {
    }
}
