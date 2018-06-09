package kz.halyqsoft.univercity.utils.changelisteners;

import com.vaadin.data.Property;
import org.r3a.common.vaadin.widget.form.field.FieldModel;

/**
 * @author Omarbek
 * @created on 06.06.2018
 */
public class LiveLoadChangeListener implements Property.ValueChangeListener {

    private final FieldModel rateLoadFM;
    private final FieldModel wageRateFM;

    public LiveLoadChangeListener(FieldModel rateLoadFM, FieldModel wageRateFM) {
        this.rateLoadFM = rateLoadFM;
        this.wageRateFM = wageRateFM;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        if (wageRateFM.getField() != null) {
            Object wageRate = wageRateFM.getField().getValue();
            String liveLoad = (String) ev.getProperty().getValue();
            if (wageRate != null && liveLoad != null) {
                Double rateLoad = Double.parseDouble(liveLoad) / Double.parseDouble(wageRate.toString());
                try {
                    rateLoadFM.refresh(rateLoad.toString());
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        }
    }
}