package kz.halyqsoft.univercity.modules.pdf.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.AbstractWidgetItemModel;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.management.Query;
import java.util.*;

public class SubstituitionTabContainer extends AbstractCommonView implements EntityListener{

    private final String title;
    private GridWidget substitutionGW;
    public SubstituitionTabContainer() {
        this.title = getUILocaleUtil().getEntityLabel(SUBSTITUTION.class);
        substitutionGW = new GridWidget(SUBSTITUTION.class);
        substitutionGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        substitutionGW.addEntityListener(this);

        DBGridModel substitutionGM = (DBGridModel)substitutionGW.getWidgetModel();
        FKFieldModel fkEmployeeFM =(FKFieldModel) substitutionGM.getFormModel().getFieldModel("employee");
        fkEmployeeFM.getQueryModel().addJoin(EJoin.INNER_JOIN ,"id" , EMPLOYEE.class , "id");
        fkEmployeeFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);

        FKFieldModel fkSubstitutorFM =(FKFieldModel) substitutionGM.getFormModel().getFieldModel("substitutor");
        fkSubstitutorFM.getQueryModel().addJoin(EJoin.INNER_JOIN ,"id" , EMPLOYEE.class , "id");
        fkSubstitutorFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);

        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        getContent().addComponent(substitutionGW);
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
        SUBSTITUTION substitution = ((SUBSTITUTION)entity);

        try{

            if(getList((EMPLOYEE) substitution.getEmployee()).size()>0){
                Message.showError(getUILocaleUtil().getMessage("previous.substitution.error"));
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        substitution.setCreated(new Date());


        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(substitution);
        }catch (Exception e){
            e.printStackTrace();
        }
        QueryModel<DOCUMENT_SIGNER> documentSignerQM = new QueryModel(DOCUMENT_SIGNER.class);
        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.CREATED).getId());
        ids.add(WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.IN_PROCESS).getId());
        documentSignerQM.addWhereIn("documentSignerStatus" ,ids);
        documentSignerQM.addWhereAnd("employee" ,ECriteria.EQUAL, substitution.getEmployee().getId());

        List<DOCUMENT_SIGNER> documentSigners = new ArrayList<>();
        try{
            documentSigners.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerQM));


            for(DOCUMENT_SIGNER dc : documentSigners){
                dc.setEmployee((EMPLOYEE) substitution.getSubstitutor());
            }

            try{
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigners);
            }catch (Exception e){
                e.printStackTrace();
            }

            substitutionGW.refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<SUBSTITUTION> getList(EMPLOYEE employee) {

        List<SUBSTITUTION> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();

        String sql = "SELECT id FROM substitution where employee_id = "+ employee.getId().getId().longValue() +" and until_date > now();" ;

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    ID id = ID.valueOf((Long)o);
                    try {
                        SUBSTITUTION substitution = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBSTITUTION.class, id);
                        list.add(substitution);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }

        return list;
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
    public String getViewName() {
        return title;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return title;
    }

    @Override
    public void initView(boolean b) throws Exception {
    }
}
