package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_REQUISITE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VPairSubject;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WindowUtils;
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
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.List;

/**
 * @author Omarbek
 * @created on 27.09.2018
 */
public class RequisitesDialog extends WindowUtils {

    private BindingElectiveSubjectEdit bindingElectiveSubjectEdit;
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;
    private SPECIALITY speciality;
    private VPairSubject pairSubject;
    private GridWidget requisitesGW;

    public RequisitesDialog(BindingElectiveSubjectEdit bindingElectiveSubjectEdit,
                            ELECTIVE_BINDED_SUBJECT electiveBindedSubject, SPECIALITY speciality,
                            VPairSubject pairSubject) {
        this.bindingElectiveSubjectEdit = bindingElectiveSubjectEdit;
        this.electiveBindedSubject = electiveBindedSubject;
        this.speciality = speciality;
        this.pairSubject = pairSubject;
        init(600, 450);
    }

    @Override
    protected String createTitle() {
        return "requisites";//TODO
    }

    @Override
    protected void refresh() throws Exception {
        bindingElectiveSubjectEdit.refresh(electiveBindedSubject);
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();

        requisitesGW = new GridWidget(SUBJECT_REQUISITE.class);
        requisitesGW.addEntityListener(new RequisitesListener());
        requisitesGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        requisitesGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);

        DBGridModel requisitesGM = (DBGridModel) requisitesGW.getWidgetModel();
        requisitesGM.setRowNumberVisible(true);
        requisitesGM.setTitleVisible(false);
        requisitesGM.setMultiSelect(true);
        requisitesGM.setRefreshType(ERefreshType.MANUAL);

        refreshGridModel();

        QueryModel subjectQM = ((FKFieldModel) requisitesGM.getFormModel().
                getFieldModel("subject")).getQueryModel();
        FromItem specFI = subjectQM.addJoin(EJoin.INNER_JOIN, "chair", SPECIALITY.class, "department");
        subjectQM.addWhereNotNull("subjectCycle");
        subjectQM.addWhereAnd("deleted", Boolean.FALSE);
        subjectQM.addWhereAnd("mandatory", Boolean.FALSE);
        subjectQM.addWhere(specFI, "deleted", false);
        subjectQM.addWhere(specFI, "id", ECriteria.EQUAL, speciality.getId());

        mainVL.addComponent(requisitesGW);
        mainVL.setComponentAlignment(requisitesGW, Alignment.MIDDLE_CENTER);

        return mainVL;
    }

    private void refreshGridModel() {
        try {
            QueryModel<SUBJECT_REQUISITE> subjectRequisiteQM = new QueryModel<>(SUBJECT_REQUISITE.class);
            subjectRequisiteQM.addWhere("pairSubject", ECriteria.EQUAL, pairSubject.getId());
            List<SUBJECT_REQUISITE> specialityCorpuses = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(subjectRequisiteQM);
            ((DBGridModel) requisitesGW.getWidgetModel()).setEntities(specialityCorpuses);
            requisitesGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh subject requisite grid", ex);
        }
    }

    private class RequisitesListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
            if (entityEvent.getAction() == EntityEvent.CREATED
                    || entityEvent.getAction() == EntityEvent.MERGED
                    || entityEvent.getAction() == EntityEvent.REMOVED) {
                refreshGridModel();
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
            SUBJECT_REQUISITE subjectRequisite = (SUBJECT_REQUISITE) entity;
            ((SUBJECT_REQUISITE) entity).setPairSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(PAIR_SUBJECT.class, pairSubject.getId()));
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
}
