package kz.halyqsoft.univercity.security;

import kz.halyqsoft.univercity.entity.beans.ROLE_TASKS;
import kz.halyqsoft.univercity.entity.beans.ROLE_TASK_FUNCTIONS;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import org.r3a.common.entity.beans.AbstractRoleTask;
import org.r3a.common.entity.beans.AbstractRoleTaskFunction;
import org.r3a.common.entity.beans.AbstractUser;
import org.r3a.common.entity.beans.AbstractUserRole;
import org.r3a.common.vaadin.security.AbstractDatabaseRealm;

/**
 * @author Omarbek
 * Created 19.05.2014 19:00:49
 */
public final class DatabaseRealm extends AbstractDatabaseRealm {

    @Override
    protected Class<? extends AbstractUser> getUserClass() {
        return USERS.class;
    }

    @Override
    protected Class<? extends AbstractUserRole> getUserRoleClass() {
        return USER_ROLES.class;
    }

    @Override
    protected Class<? extends AbstractRoleTask> getRoleTaskClass() {
        return ROLE_TASKS.class;
    }

    @Override
    protected Class<? extends AbstractRoleTaskFunction> getRoleTaskFunctionClass() {
        return ROLE_TASK_FUNCTIONS.class;
    }
}
