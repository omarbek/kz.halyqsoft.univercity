package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class CreationTab {

    private VerticalLayout mainVL;
    HorizontalSplitPanel mainHSP;
    public CreationTab(){
        initTab();
    }

    public void initTab(){
        TabSheet tabSheet = new TabSheet();
        mainVL = new VerticalLayout();
        mainHSP = new HorizontalSplitPanel();




    }

}
