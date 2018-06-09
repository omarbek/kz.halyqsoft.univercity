package kz.halyqsoft.univercity.utils.changelisteners;

import com.vaadin.data.Property;
import com.vaadin.ui.Field;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.widget.form.field.FieldModel;

/**
 * @author Omarbek
 * @created on 06.06.2018
 */
public class CareerPostChangeListener implements Property.ValueChangeListener {

    private final FieldModel liveLoadFM;
    private final FieldModel wageRateFM;
    private final FieldModel rateLoadFM;

    public CareerPostChangeListener(FieldModel liveLoadFM, FieldModel wageRateFM, FieldModel rateLoadFM) {
        this.liveLoadFM = liveLoadFM;
        this.wageRateFM = wageRateFM;
        this.rateLoadFM = rateLoadFM;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        POST post = (POST) ev.getProperty().getValue();
        try {
            if (post != null) {
                Integer liveLoad = post.getStudyLoad();
                liveLoadFM.refresh(liveLoad.toString());
                Field wageRateF = wageRateFM.getField();
                Double wageRate = 0.0;
                if (wageRateF != null && wageRateF.getValue() != null) {
                    String a = wageRateF.getValue().toString();
                    wageRate = Double.valueOf(a.replace(',', '.'));
                }
                Double rateLoad = liveLoad * wageRate;
                rateLoadFM.refresh(rateLoad.toString());
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh rate and live load", ex);
        }
    }
}