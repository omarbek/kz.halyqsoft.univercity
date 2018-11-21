package kz.halyqsoft.univercity.modules.reports;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.r3a.common.vaadin.widget.table.column.AbstractTableColumn;

/**
 * @author Dinassil Omarbek
 * @created Apr 27, 2018 2:42:12 PM
 */
public final class MenuColumn extends AbstractTableColumn {

    @Override
    public Object generateCell(final Table source, final Object itemId, Object columnId) {
        String value = (String) itemId;

        Label nameLabel = new Label();
        nameLabel.setWidth(100, Unit.PERCENTAGE);
        nameLabel.setContentMode(ContentMode.HTML);
        nameLabel.addStyleName("bold");
        nameLabel.setValue(value);

        return nameLabel;
    }

}
