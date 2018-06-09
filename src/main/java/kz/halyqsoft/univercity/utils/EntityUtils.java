package kz.halyqsoft.univercity.utils;

import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.util.ArrayList;
import java.util.List;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

/**
 * @author Omarbek
 * @created on 24.04.2018
 */
public abstract class EntityUtils implements EntityListener {

    protected abstract void init(Object source, Entity e, boolean isNew) throws Exception;

    protected abstract GridWidget getGridWidget();

    protected abstract String getModuleName();

    protected abstract Class<? extends Entity> getEntityClass();

    protected abstract void removeChildrenEntity(List<Entity> delList) throws Exception;

    protected abstract void refresh() throws Exception;

    private GridWidget mainGW = getGridWidget();
    private String moduleName = getModuleName();
    private Class<? extends Entity> entityClass = getEntityClass();

    private boolean openEntityEdit(Object source, Entity e, boolean isNew) {
        if (source.equals(mainGW)) {
            try {
                init(source, e, isNew);
                return false;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to open " + moduleName + " ", ex);
            }
        }

        return true;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        try {
            List<Entity> delList = new ArrayList<>();
            for (Entity entity : list) {
                Entity parentEntity = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        entityClass, entity.getId());
                delList.add(parentEntity);
            }
            removeChildrenEntity(delList);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
            refresh();
        } catch (Exception ex) {
            CommonUtils.LOG.error("Unable to delete" + moduleName + ": ", ex);
            Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
        }
        return false;
    }


    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return openEntityEdit(source, e, false);
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return openEntityEdit(source, null, true);
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {
    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {
    }

    @Override
    public void deferredCreate(Object o, Entity entity) {
    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {
    }

    @Override
    public void onException(Object o, Throwable throwable) {
    }
}
