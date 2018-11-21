package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

/**
 * @author Assylkhan
 * on 06.11.2018
 * @project kz.halyqsoft.univercity
 */
public class DownloadDialog extends AbstractDialog {

    HorizontalLayout mainHL = CommonUtils.createButtonPanel();

    @Override
    protected String createTitle() {
        return "PDF/EXCEL";
    }

    public HorizontalLayout getMainHL() {
        return mainHL;
    }

    public void init(){
        setResponsive(true);
        getContent().addComponent(mainHL);
        AbstractWebUI.getInstance().addWindow(this);
    }
}
