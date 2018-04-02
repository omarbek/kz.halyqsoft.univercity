package kz.halyqsoft.univercity.utils.changelisteners;

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
public class CountryChangeListener implements Property.ValueChangeListener {

    private final COUNTRY region;
    private final FKFieldModel regionFM;

    public CountryChangeListener(COUNTRY region, FKFieldModel regionFM) {
        this.region = region;
        this.regionFM = regionFM;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        Object value = ev.getProperty().getValue();
        QueryModel qm = regionFM.getQueryModel();
        if (value != null) {
            qm.addWhere("parent", ECriteria.EQUAL, ((COUNTRY) value).getId());
        } else {
            qm.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
        }
        try {
            regionFM.refresh(region);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }
}
