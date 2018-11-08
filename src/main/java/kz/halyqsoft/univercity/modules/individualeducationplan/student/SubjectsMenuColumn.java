package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.vaadin.widget.table.column.AbstractTableColumn;

/**
 * @author Dinassil Omarbek
 * @created Apr 27, 2018 2:42:12 PM
 */
public final class SubjectsMenuColumn extends AbstractTableColumn {

    @Override
    public Object generateCell(final Table source, final Object itemId, Object columnId) {
        boolean isSubject = false;
        SUBJECT subject = new SUBJECT();
        DEPARTMENT department = new DEPARTMENT();
        if (itemId instanceof SUBJECT) {
            subject = (SUBJECT) itemId;
            isSubject = true;
        } else {
            department = (DEPARTMENT) itemId;
        }

        Label nameLabel = new Label();
        nameLabel.setWidth(100, Unit.PERCENTAGE);
        nameLabel.setContentMode(ContentMode.HTML);
        nameLabel.addStyleName("bold");
        nameLabel.setValue(isSubject ? subject.toString() : department.toString());

        return nameLabel;
    }

}
