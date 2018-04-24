package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FUserRolesFilter extends AbstractFilterBean {

    private ROLES roleName;
    private USERS userName;

    public FUserRolesFilter() {
    }

    public ROLES getRoleName() {
        return roleName;
    }

    public void setRoleName(ROLES roleName) {
        this.roleName = roleName;
    }

    public USERS getUserName() {
        return userName;
    }

    public void setUserName(USERS userName) {
        this.userName = userName;
    }

    @Override
    public boolean hasFilter() {
        return (!(userName == null && roleName == null));
    }
}
