package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LOCK_REASON;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
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

    private final ComboBox lockCB = new ComboBox();
    private static final int STUDENT = 2;

    LockDialog(AbstractYesButtonListener yesListener) {
        super(yesListener);

        QueryModel<LOCK_REASON> lockQM = new QueryModel<>(LOCK_REASON.class);
        lockQM.addWhere("userType", ECriteria.EQUAL, ID.valueOf(STUDENT));
        lockQM.addOrder("reason");

        try {
            BeanItemContainer<LOCK_REASON> lockBIC = new BeanItemContainer<>(LOCK_REASON.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(lockQM));
            lockCB.setNewItemsAllowed(false);
            lockCB.setFilteringMode(FilteringMode.OFF);
            lockCB.setTextInputAllowed(false);
            lockCB.setImmediate(true);
            lockCB.setWidth(250, Unit.PIXELS);
            lockCB.setContainerDataSource(lockBIC);
            getContent().addComponent(lockCB);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load lock reasons", ex);
        }
    }

    public LOCK_REASON getLockReason() {
        return (LOCK_REASON) lockCB.getValue();
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("specify.lock.reason");
    }
}
