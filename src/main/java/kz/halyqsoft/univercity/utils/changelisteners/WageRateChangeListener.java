package kz.halyqsoft.univercity.utils.changelisteners;

import com.vaadin.data.Property;
import com.vaadin.ui.Field;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.widget.form.field.FieldModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * @author Omarbek
 * @created on 06.06.2018
 */
public class WageRateChangeListener implements Property.ValueChangeListener {

    private final FieldModel liveLoadFM;
    private final FieldModel rateLoadFM;

    public WageRateChangeListener(FieldModel liveLoadFM, FieldModel rateLoadFM) {
        this.liveLoadFM = liveLoadFM;
        this.rateLoadFM = rateLoadFM;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        String value = (String) ev.getProperty().getValue();
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("######.##", dfs);
        try {
            Double wageRate = df.parse(value).doubleValue();
            Field liveLoadF = liveLoadFM.getField();
            Double liveLoad = 0.0;
            if (liveLoadF != null && liveLoadF.getValue() != null) {
                liveLoad = df.parse(liveLoadF.getValue().toString()).doubleValue();
            }

            Double rateLoad = liveLoad * wageRate;
            rateLoadFM.refresh(rateLoad.toString());
        } catch (ParseException ex) {
            CommonUtils.LOG.error("Unable to parse double value");
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to parse double value", ex);
        }
    }
}