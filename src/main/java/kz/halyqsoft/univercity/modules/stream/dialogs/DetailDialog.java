package kz.halyqsoft.univercity.modules.stream.dialogs;

import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class DetailDialog extends AbstractDialog implements EntityListener {

    private STREAM stream;
    private GridWidget streamGroupGW;
    private DBGridModel streamGroupGM;
    private boolean isCRUD;

    public GridWidget getStreamGroupGW() {
        return streamGroupGW;
    }

    public DetailDialog(STREAM stream, boolean isCRUD) {
        this.isCRUD = isCRUD;
        init(stream, isCRUD);
    }

    public DetailDialog(STREAM stream) {
        isCRUD = true;
        init(stream, isCRUD);
    }

    public void init(STREAM stream, boolean refreshable) {
        this.stream = stream;

        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setWidth(80, Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
        getContent().setSizeFull();

        streamGroupGW = new GridWidget(STREAM_GROUP.class);
        streamGroupGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        streamGroupGW.addEntityListener(this);
        streamGroupGW.setImmediate(true);
        streamGroupGW.setSizeFull();
        streamGroupGW.setMultiSelect(true);

        streamGroupGM = (DBGridModel) streamGroupGW.getWidgetModel();
        streamGroupGM.setRefreshType(ERefreshType.MANUAL);

        refresh();


        getContent().addComponent(streamGroupGW);
        AbstractWebUI.getInstance().addWindow(this);
    }

    private void refresh() {
        if (isCRUD) {
            QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
            FromItem streamGrFI = groupsQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class, "group");
            groupsQM.addWhere("deleted", false);
            groupsQM.addWhere(streamGrFI, "stream", ECriteria.EQUAL, stream.getId());

            List<ID> groupIdsInThisStream = new ArrayList<>();

            QueryModel<STREAM> streamQM = new QueryModel<>(STREAM.class);
            streamQM.addWhere("deleted", ECriteria.EQUAL, false);
            if (stream.getSubject() != null) {
                streamQM.addWhereAnd("subject", ECriteria.EQUAL, stream.getSubject().getId());
            }
            if (stream.getSemesterPeriod() != null) {
                streamQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, stream.getSemesterPeriod().getId());
            }
            try {
                List<STREAM> streams = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        streamQM);
                for (STREAM stream : streams) {
                    QueryModel<STREAM_GROUP> streamGroupQM = new QueryModel<>(STREAM_GROUP.class);
                    streamGroupQM.addWhere("stream", ECriteria.EQUAL, stream.getId());
                    List<STREAM_GROUP> streamGroups = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(streamGroupQM);
                    for (STREAM_GROUP streamGroup : streamGroups) {
                        if (!groupIdsInThisStream.contains(streamGroup.getGroup().getId())) {
                            groupIdsInThisStream.add(streamGroup.getGroup().getId());
                        }
                    }
                }
            } catch (Exception e) {
                CommonUtils.showMessageAndWriteLog(e.getMessage(), e);
                e.printStackTrace();
            }
            try {
                List<GROUPS> groups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(groupsQM);
                for (GROUPS group : groups) {
                    if (!groupIdsInThisStream.contains(group.getId())) {
                        groupIdsInThisStream.add(group.getId());
                    }
                }
            } catch (Exception e) {
                CommonUtils.showMessageAndWriteLog(e.getMessage(), e);
                e.printStackTrace();
            }

            FKFieldModel groupFM = (FKFieldModel) streamGroupGM.getFormModel().getFieldModel("group");
            QueryModel groupQM = groupFM.getQueryModel();
            groupQM.addWhere("deleted", ECriteria.EQUAL, false);
            groupQM.addWhereNotIn("id", groupIdsInThisStream);
            groupQM.addOrder("name");
        }

        QueryModel<STREAM_GROUP> streamGroupQM = new QueryModel<>(STREAM_GROUP.class);
        streamGroupQM.addWhere("stream", ECriteria.EQUAL, stream.getId());
        FromItem fi = streamGroupQM.addJoin(EJoin.INNER_JOIN, "group", GROUPS.class, "id");
        streamGroupQM.addWhere(fi, "deleted", ECriteria.EQUAL, false);
        try {
            List<STREAM_GROUP> streamGroups = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                    .lookup(streamGroupQM);
            streamGroupGM.setEntities(streamGroups);
            streamGroupGW.refresh();
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected String createTitle() {
        return "Details";
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if (entityEvent.getAction() == EntityEvent.CREATED
                || entityEvent.getAction() == EntityEvent.REMOVED) {
            refresh();
        }
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return true;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return true;
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
        if (b) {
            ((STREAM_GROUP) entity).setStream(stream);
        }
        return true;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return true;
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
