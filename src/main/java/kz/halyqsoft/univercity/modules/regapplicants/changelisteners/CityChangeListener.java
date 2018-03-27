package kz.halyqsoft.univercity.modules.regapplicants.changelisteners;

import com.vaadin.data.Property;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

/**
 * @author Omarbek
 * @created on 27.03.2018
 */
public class CityChangeListener implements Property.ValueChangeListener {

    private final COUNTRY village;
    private final FKFieldModel villageFM;

    public CityChangeListener(COUNTRY village, FKFieldModel villageFM) {
        this.village = village;
        this.villageFM = villageFM;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        Object value = ev.getProperty().getValue();
        QueryModel qm = villageFM.getQueryModel();
        if (value != null) {
            qm.addWhere("parent", ECriteria.EQUAL, ((COUNTRY) value).getId());
        } else {
            qm.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        }
        try {
            villageFM.refresh(village);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }
}
