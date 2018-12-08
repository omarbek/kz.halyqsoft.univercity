package kz.halyqsoft.univercity.modules.workflowforemp;

import org.apache.poi.ss.formula.functions.T;
import org.r3a.common.entity.Entity;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

/**
 * @author Assylkhan
 * on 08.12.2018
 * @project kz.halyqsoft.univercity
 */
public class GridWidgetDialog extends AbstractDialog{
    private String title;
    private GridWidget genericGW;
    private DBGridModel genericGM;
    public GridWidgetDialog(String title, Class <? extends Entity> tClass){
        this.title = title;
        setWidth("70%");
        setHeight("70%");

        genericGW = new GridWidget(tClass);
        genericGM = (DBGridModel) genericGW.getWidgetModel();

        getContent().addComponent(genericGW);

    }

    public GridWidget getGenericGW() {
        return genericGW;
    }

    public DBGridModel getGenericGM() {
        return genericGM;
    }

    @Override
    protected String createTitle() {
        return title;
    }
}
