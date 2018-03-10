package kz.halyqsoft.univercity;

import com.vaadin.annotations.Theme;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.ROLE_TASKS;
import kz.halyqsoft.univercity.entity.beans.ROLE_TASK_FUNCTIONS;
import kz.halyqsoft.univercity.entity.beans.SETTINGS;
import kz.halyqsoft.univercity.entity.beans.TABLES;
import kz.halyqsoft.univercity.entity.beans.TASKS;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import org.r3a.common.dblink.lifecycle.LifecycleManager;
import org.r3a.common.dblink.utils.DBLink;
import org.r3a.common.entity.beans.AbstractLog;
import org.r3a.common.entity.beans.AbstractRole;
import org.r3a.common.entity.beans.AbstractRoleTask;
import org.r3a.common.entity.beans.AbstractRoleTaskFunction;
import org.r3a.common.entity.beans.AbstractSetting;
import org.r3a.common.entity.beans.AbstractTable;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.beans.AbstractUser;
import org.r3a.common.entity.beans.AbstractUserRole;
import org.r3a.common.resources.Resource;
import org.r3a.common.vaadin.AbstractSecureWebUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author: Rakymzhan A. Kenzhegul
 * Created: 09.03.2018 22:49
 */
@Theme("univercity")
public class UnivercityUI extends AbstractSecureWebUI {

    @Override
    public void attach() {
        super.attach();
        try {
            List<Locale> locales = new ArrayList<>(3);
            locales.add(new Locale("kk", "KZ"));
            locales.add(new Locale("ru", "RU"));
            locales.add(new Locale("en", "US"));
            Resource.INSTANCE.loadApplicationResources("kz.halyqsoft.univercity.resourses.messages", getClass().getClassLoader(), locales);
            DBLink.INSTANCE.open("UNIVER-PU");
            LifecycleManager.getInstance().start(SETTINGS.class);
        } catch (Exception ex) {
            LOG.error("Unable to start application: ", ex);
        }
    }

    @Override
    public Class<? extends AbstractTask> getTaskClass() {
        return TASKS.class;
    }

    @Override
    public Class<? extends AbstractLog> getLogClass() {
        return kz.halyqsoft.univercity.entity.beans.LOG.class;
    }

    @Override
    public Class<? extends AbstractTable> getTableClass() {
        return TABLES.class;
    }

    @Override
    public Class<? extends AbstractSetting> getSettingClass() {
        return SETTINGS.class;
    }

    @Override
    public Class<? extends AbstractRole> getRoleClass() {
        return ROLES.class;
    }

    @Override
    public Class<? extends AbstractRoleTask> getRoleTaskClass() {
        return ROLE_TASKS.class;
    }

    @Override
    public Class<? extends AbstractRoleTaskFunction> getRoleTaskFunctionClass() {
        return ROLE_TASK_FUNCTIONS.class;
    }

    @Override
    public Class<? extends AbstractUserRole> getUserRoleClass() {
        return USER_ROLES.class;
    }

    @Override
    public Class<? extends AbstractUser> getUserClass() {
        return USERS.class;
    }
}
