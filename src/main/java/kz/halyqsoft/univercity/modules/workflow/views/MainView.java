package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.addon.charts.Chart;
import com.vaadin.ui.Button;

public class MainView extends BaseView{

    public MainView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        Chart chart = new Chart();

        getContent().addComponent(chart);
    }
}
