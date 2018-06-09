package kz.halyqsoft.univercity.modules.employee.workhour;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.univercity.AbstractWorkHourEntity;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DAY_HOUR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.AbstractWidgetModel;
import org.r3a.common.vaadin.widget.AbstractWidgetPanel;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Jan 2, 2016 3:49:56 PM
 */
@SuppressWarnings("serial")
public class WorkHourWidget extends AbstractWidgetPanel {

    private final Map<AbstractWorkHourEntity, DayHourButton> buttons = new HashMap<>();
    private List<WEEK_DAY> wdList;
    private List<DAY_HOUR> dhList;
    private List<? extends AbstractWorkHourEntity> list;
    private final boolean readOnly;
    private GridLayout matrixGW;
    private String legend1Resource;
    private String legend2Resource;
    private FormModel mainFM;

    public WorkHourWidget(List<? extends AbstractWorkHourEntity> list, boolean readOnly, FormModel mainFM) {
        super();

        this.list = list;
        this.readOnly = readOnly;
        this.mainFM = mainFM;

        QueryModel<WEEK_DAY> wdQM = new QueryModel<>(WEEK_DAY.class);
        QueryModel<DAY_HOUR> dhQM = new QueryModel<>(DAY_HOUR.class);
        dhQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(14));
        try {
            wdList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(wdQM);
            dhList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dhQM);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load week days", ex);
        }
    }

    public List<? extends AbstractWorkHourEntity> getWorkHourList() {
        return list;
    }

    public void setWorkHourList(List<? extends AbstractWorkHourEntity> list) {
        this.list = list;
    }

    public boolean isEmpty() {
        return (list == null || list.isEmpty());
    }

    @Override
    public void localeChanged(Locale newLocale) {
    }

    public String getLegend1Resource() {
        return legend1Resource;
    }

    public void setLegend1Resource(String legend1Resource) {
        this.legend1Resource = legend1Resource;
    }

    public String getLegend2Resource() {
        return legend2Resource;
    }

    public void setLegend2Resource(String legend2Resource) {
        this.legend2Resource = legend2Resource;
    }

    @Override
    public void refresh() throws Exception {
        for (AbstractWorkHourEntity awhe : list) {
            DayHourButton dhb = new DayHourButton(awhe, readOnly);
            int col = awhe.getDayHour().getId().getId().intValue() + 1;
            int row = awhe.getWeekDay().getId().getId().intValue() + 1;
            matrixGW.removeComponent(col, row);
            matrixGW.addComponent(dhb, col, row);
            matrixGW.setComponentAlignment(dhb, Alignment.MIDDLE_CENTER);
            buttons.put(awhe, dhb);
        }
    }

    public void cancel() {
        for (DayHourButton dhb : buttons.values()) {
            dhb.cancel();
        }
    }

    @Override
    public AbstractWidgetModel getWidgetModel() {
        return null;
    }

    @Override
    protected void initWidget() throws Exception {
        int cols = 17;
        int rows = 10;

        matrixGW = new GridLayout();
        //		matrixGW.setSizeFull();
        matrixGW.setSpacing(true);
        matrixGW.setColumns(cols);
        matrixGW.setRows(rows);

        for (int i = 2; i < 16; i++) {
            final Button b = new Button();
            b.setData(i);
            b.setIcon(new ThemeResource("img/button/on_off_button.png"));
            String descr = getUILocaleUtil().getCaption("activate") + "/" + getUILocaleUtil().getCaption("deactivate");
            b.setDescription(descr);
            b.addStyleName("icon-only");
            matrixGW.addComponent(b, i, 0);
            matrixGW.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
            b.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    if (mainFM.isCreateNew()) {
                        Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                        return;
                    }
                    int strartColumn = (int) b.getData();
                    for (int x = 2; x <= 8; x++) {
                        Button bb = (Button) matrixGW.getComponent(strartColumn, x);
                        bb.click();
                    }
                }
            });
        }

        for (int i = 2; i <= 8; i++) {
            final Button b = new Button();
            b.setData(i);
            b.setIcon(new ThemeResource("img/button/on_off_button.png"));
            String descr = getUILocaleUtil().getCaption("activate") + "/" + getUILocaleUtil().getCaption("deactivate");
            b.setDescription(descr);
            b.addStyleName("icon-only");
            matrixGW.addComponent(b, 0, i);
            matrixGW.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
            b.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    if (mainFM.isCreateNew()) {
                        Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                        return;
                    }
                    int strartColumn = (int) b.getData();
                    for (int x = 2; x <= 15; x++) {
                        Button bb = (Button) matrixGW.getComponent(x, strartColumn);
                        bb.click();
                    }
                }
            });
        }

        int j = 2;
        for (DAY_HOUR dh : dhList) {
            Label l = new Label(dh.toString());
            l.setWidthUndefined();
            l.addStyleName("day-time");
            l.addStyleName("bold");
            matrixGW.addComponent(l, j, 1);
            matrixGW.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
            j++;
        }

        int i = 2;
        for (WEEK_DAY wd : wdList) {
            Label l = new Label(wd.toString());
            l.setWidthUndefined();
            l.addStyleName("day-time");
            l.addStyleName("bold");
            matrixGW.addComponent(l, 1, i);
            matrixGW.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
            i++;
        }

        refresh();

        getContent().addComponent(matrixGW);
        getContent().setComponentAlignment(matrixGW, Alignment.MIDDLE_CENTER);

        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(true);
        legend.addStyleName("day-time-legend");

        Label l1 = new Label();
        l1.addStyleName("day-time-legend");
        l1.setIcon(new ThemeResource("img/button/active.png"));
        l1.setCaption(" - " + getUILocaleUtil().getCaption(legend1Resource));
        legend.addComponent(l1);

        Label l2 = new Label();
        l2.addStyleName("day-time-legend");
        l2.setIcon(new ThemeResource("img/button/inactive.png"));
        l2.setCaption(" - " + getUILocaleUtil().getCaption(legend2Resource));
        legend.addComponent(l2);

        getContent().addComponent(legend);
        getContent().setComponentAlignment(legend, Alignment.BOTTOM_CENTER);
    }
}
