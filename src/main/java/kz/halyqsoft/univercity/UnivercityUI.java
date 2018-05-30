package kz.halyqsoft.univercity;

import com.vaadin.annotations.Theme;
import kz.halyqsoft.univercity.entity.beans.*;
import org.r3a.common.dblink.lifecycle.LifecycleManager;
import org.r3a.common.dblink.utils.DBLink;
import org.r3a.common.entity.beans.*;
import org.r3a.common.resources.Resource;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Omarbek
 * Created: 09.03.2018 22:49
 */
@Theme("univercity")
public class UnivercityUI extends AbstractSecureWebUI {

    private static final Logger LOG = LoggerFactory.getLogger(UnivercityUI.class);

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
