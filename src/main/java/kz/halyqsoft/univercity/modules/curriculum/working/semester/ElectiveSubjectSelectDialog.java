package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;

/**
 * @author Omarbek Dinassil
 * @created Apr 20, 2017 2:03:36 PM
 */
@SuppressWarnings("serial")
final class ElectiveSubjectSelectDialog extends CustomGridSelectDialog {

    private ComboBox subjectCycleCB;

    public ElectiveSubjectSelectDialog(AbstractYesButtonListener yesListener, Class<? extends Entity> entityClass) {
        super(yesListener, entityClass);
    }

    @Override
    protected void initAddContent() {
        FormLayout fl = new FormLayout();

        QueryModel<SUBJECT_CYCLE> subjectCycleQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
        subjectCycleQM.addWhere("id", ECriteria.LESS, ID.valueOf(4));
        subjectCycleQM.addOrder("cycleShortName");
        try {
            BeanItemContainer<SUBJECT_CYCLE> subjectCycleBIC = new BeanItemContainer<SUBJECT_CYCLE>(SUBJECT_CYCLE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectCycleQM));
            subjectCycleCB = new ComboBox();
            subjectCycleCB.setCaption(getUILocaleUtil().getCaption("curriculum.component"));
            subjectCycleCB.setContainerDataSource(subjectCycleBIC);
            subjectCycleCB.setImmediate(true);
            subjectCycleCB.setNullSelectionAllowed(true);
            subjectCycleCB.setTextInputAllowed(false);
            subjectCycleCB.setFilteringMode(FilteringMode.OFF);
            subjectCycleCB.setPageLength(0);
            fl.addComponent(subjectCycleCB);
        } catch (Exception ex) {
            LOG.error("Unable to load subject cycle list: ", ex);
        }

        getContent().addComponent(fl);
        getContent().setComponentAlignment(fl, Alignment.MIDDLE_CENTER);
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return (SUBJECT_CYCLE) subjectCycleCB.getValue();
    }
}
