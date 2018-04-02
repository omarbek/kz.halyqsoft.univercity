package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LOCK_REASON;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;

/**
 * @author Omarbek
 * @created Oct 7, 2016 2:47:13 PM
 */
@SuppressWarnings("serial")
final class LockDialog extends AbstractDialog {

    private final ComboBox comboBox = new ComboBox();

    public LockDialog(AbstractYesButtonListener yesListener) {
        super(yesListener);

        QueryModel<LOCK_REASON> lrQM = new QueryModel<>(LOCK_REASON.class);
        lrQM.addWhere("lockType", ECriteria.EQUAL, 1);
        lrQM.addOrder("reason");

        try {
            BeanItemContainer<LOCK_REASON> bic = new BeanItemContainer<>(LOCK_REASON.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(lrQM));
            comboBox.setNewItemsAllowed(false);
            comboBox.setFilteringMode(FilteringMode.OFF);
            comboBox.setTextInputAllowed(false);
            comboBox.setImmediate(true);
            comboBox.setWidth(250, Unit.PIXELS);
            comboBox.setContainerDataSource(bic);
            getContent().addComponent(comboBox);
        } catch (Exception ex) {
            LOG.error("Unable to load lock reasons: ", ex);
        }
    }

    public LOCK_REASON getLockReason() {
        return (LOCK_REASON) comboBox.getValue();
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("specify.lock.reason");
    }
}
