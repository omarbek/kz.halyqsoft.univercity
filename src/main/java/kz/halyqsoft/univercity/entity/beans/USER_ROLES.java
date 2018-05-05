package kz.halyqsoft.univercity.entity.beans;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.beans.AbstractRole;
import org.r3a.common.entity.beans.AbstractUser;
import org.r3a.common.entity.beans.AbstractUserRole;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * Author Omarbek
 * Created 25.10.2008 17:33:31
 */
@Entity
public class USER_ROLES extends AbstractUserRole {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false )})
    private USERS user;

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", nullable = false)})
    private ROLES role;

    public USER_ROLES() {
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
    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    @Override
    public void setUser(AbstractUser user) {
        setUser((USERS) user);
    }

    @Override
    public String toString() {
        return user.toString() + " | " + role.toString();
    }
}
