package kz.halyqsoft.univercity.modules.workflow.views;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

import java.util.ArrayList;
import java.util.List;

public class MainView extends BaseView{

    public MainView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        Component component = getChart();
        component.setSizeFull();
        getContent().addComponent(component);

    }


    public Component getChart() {
        PieChartConfig config = new PieChartConfig();
        config.data()
                .labels("Red", "Green", "Yellow", "Grey", "Dark Grey")
                .addDataset(new PieDataset().label("Dataset 1"))
                .and();
        config
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Chart.js Single Pie Chart")
                .and()
                .animation()
                //.animateScale(true)
                .animateRotate(true)
                .and()
                .done();

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            PieDataset lds = (PieDataset) ds;
            List<Double> data = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add((double) (Math.round(Math.random() * 100)));
                colors.add(ColorUtils.randomColor(0.7));
            }
            lds.backgroundColor(colors.toArray(new String[colors.size()]));
            lds.dataAsList(data);
        }

        ChartJs chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);
        chart.addClickListener((a,b) ->
                notification(a, b, config.data().getDatasets().get(a)));

        return chart;
    }

    public static void notification(int dataSetIdx, int dataIdx, Dataset<?, ?> dataset) {
        Notification.show("Dataset at Idx:" + dataSetIdx + "; Data at Idx: " + dataIdx + "; Value: " + dataset.getData().get(dataIdx), Notification.Type.TRAY_NOTIFICATION);
    }
}
