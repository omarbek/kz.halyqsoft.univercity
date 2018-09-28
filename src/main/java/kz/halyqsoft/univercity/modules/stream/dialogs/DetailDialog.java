package kz.halyqsoft.univercity.modules.stream.dialogs;

import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.List;

public class DetailDialog extends AbstractDialog implements EntityListener{

    private STREAM stream;
    private GridWidget streamGroupGW;
    private DBGridModel streamGroupGM;
    public DetailDialog(STREAM stream){
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setWidth(80 , Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
        getContent().setSizeFull();

        this.stream = stream;

        streamGroupGW = new GridWidget(STREAM_GROUP.class);
        streamGroupGW.setButtonVisible(IconToolbar.EDIT_BUTTON,false);
        streamGroupGW.addEntityListener(this);
        streamGroupGW.setImmediate(true);
        streamGroupGW.setSizeFull();
        streamGroupGW.setMultiSelect(true);

        streamGroupGM = (DBGridModel) streamGroupGW.getWidgetModel();
        streamGroupGM.getFormModel().getFieldModel("stream").setInEdit(false);

        FKFieldModel groupFM = (FKFieldModel) streamGroupGM.getFormModel().getFieldModel("group");
        FromItem fi =groupFM.getQueryModel().addJoin(EJoin.LEFT_JOIN,"id" , STREAM_GROUP.class, "group_id");
        groupFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL , false);
        groupFM.getQueryModel().addWhereNullAnd(fi , "id" );

        getContent().addComponent(streamGroupGW);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return "Details";
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

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
        if(b){
            ((STREAM_GROUP)entity).setStream(stream);
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
