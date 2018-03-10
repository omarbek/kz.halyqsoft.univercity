package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractUser;
import org.r3a.common.entity.tree.CommonTree;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Author Rahimjan A. Kenjegul
 * Created 25.10.2008 14:43:26
 */
@Entity
public class USERS extends AbstractUser implements CommonTree<USERS> {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")})
    private TASKS task;

    @OneToMany(mappedBy = "user")
    private List<USER_ROLES> userRoles;

    public USERS() {
    }

    @Override
    public TASKS getTask() {
        return task;
    }

    public void setTask(TASKS task) {
        this.task = task;
    }

    public List<USER_ROLES> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<USER_ROLES> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public List<USERS> getChildren() {
        return null;
    }

    @Override
    public USERS getParent() {
        return null;
    }

    @Override
    public void setParent(USERS parent) {
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public String getIconPath() {
//        if (!isStatus()) {
            return "img/user_on.png";
//        } else {
//            return "img/user_off.png";
//        }
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void setDeleted(boolean b) {
    }
}
