package kz.halyqsoft.univercity.modules.employeeabsence.dialogs;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE_ABSENCE_CAUSE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.Date;
import java.util.List;

/**
 * @author Assylkhan
 * on 21.11.2018
 * @project kz.halyqsoft.univercity
 */
public class AbsenceCauseDialog  extends AbstractDialog implements EntityListener{
    private VEmployee employee;
    private DBGridModel employeeAbsenceCauseGM;
    private GridWidget employeeAbsenceCauseGW;
    public AbsenceCauseDialog(VEmployee employee) {
        this.employee = employee;
        setWidth("80%");
        setHeight("80%");
        employeeAbsenceCauseGW = new GridWidget(EMPLOYEE_ABSENCE_CAUSE.class);
        employeeAbsenceCauseGM = (DBGridModel) employeeAbsenceCauseGW.getWidgetModel();
        employeeAbsenceCauseGM.getFormModel().getFieldModel("employee").setReadOnlyFixed(true);
        employeeAbsenceCauseGM.getQueryModel().addWhere("employee" , ECriteria.EQUAL, employee.getId());
        employeeAbsenceCauseGW.addEntityListener(this);
        getContent().addComponent(employeeAbsenceCauseGW);
        AbstractWebUI.getInstance().addWindow(this);
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
        if(entity instanceof EMPLOYEE_ABSENCE_CAUSE){
            EMPLOYEE_ABSENCE_CAUSE employeeAbsenceCause = (EMPLOYEE_ABSENCE_CAUSE) entity;
            try{
                employeeAbsenceCause.setEmployee(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_EMPLOYEE.class, employee.getId()));
                employeeAbsenceCause.setCreated(new Date());
            }catch (Exception e){
                e.printStackTrace();
                CommonUtils.showMessageAndWriteLog(e.getMessage(),e);
            }
        }
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

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("absence.cause") + ": " + employee.getFio();
    }
}
