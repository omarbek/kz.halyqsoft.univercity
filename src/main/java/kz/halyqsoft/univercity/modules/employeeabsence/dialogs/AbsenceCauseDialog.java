package kz.halyqsoft.univercity.modules.employeeabsence.dialogs;

import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

/**
 * @author Assylkhan
 * on 21.11.2018
 * @project kz.halyqsoft.univercity
 */
public class AbsenceCauseDialog  extends AbstractDialog{
    private VEmployee employee;
    public AbsenceCauseDialog(VEmployee employee) {
        this.employee = employee;


        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("absence.cause") + ": " + employee.getFio();
    }
}
