package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class
WorkflowView extends AbstractTaskView {
    VerticalLayout tab1MainVL;
    VerticalLayout tab2MainVL;
    HorizontalSplitPanel mainHSP;
    HorizontalLayout mainHL;
    public WorkflowView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        TabSheet tabSheet = new TabSheet();

        tab1MainVL = new VerticalLayout();
        initTab1();
        tab2MainVL = new VerticalLayout();
        initTab2();

        tabSheet.addTab(tab1MainVL, "Создать");
        tabSheet.addTab(tab2MainVL, "Поиск");

        getContent().addComponent(tabSheet);
    }

    public void initTab1(){
        mainHSP = new HorizontalSplitPanel();
        mainHL = new HorizontalLayout();

        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(20);

        TreeTable sideBarTT= new TreeTable();
        HierarchicalContainer optionHC = new HierarchicalContainer();
        String myDocs = "Мои документы";
        optionHC.addItem(myDocs);
        optionHC.addItem("Мои замещение");
        optionHC.addItem("Мои сохраненные");
        optionHC.addItem("Общие папки");
        optionHC.addItem("Общие справочники");

        sideBarTT.setContainerDataSource(optionHC);
        sideBarTT.setColumnReorderingAllowed(false);
        sideBarTT.setMultiSelect(false);
        sideBarTT.setNullSelectionAllowed(false);
        sideBarTT.setSizeFull();
        sideBarTT.setImmediate(true);
        sideBarTT.setSelectable(true);

        MenuColumn firstColumn = new MenuColumn();
        sideBarTT.addGeneratedColumn("first" , firstColumn);
        sideBarTT.setColumnHeader("first" , "Данные");
        sideBarTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                try {
                    if (valueChangeEvent != null && valueChangeEvent.getProperty() != null && valueChangeEvent.getProperty().getValue() != null) {

                        if(mainHSP.getSecondComponent()!=null){
                            mainHSP.removeComponent(mainHL);
                        }

                        mainHL.removeAllComponents();
                        mainHL.addComponent(new Button(valueChangeEvent.getProperty().getValue().toString()));
                        mainHSP.addComponent(mainHL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mainHSP.addComponent(sideBarTT);
        tab1MainVL.addComponent(mainHSP);
    }

    public void initTab2(){

    }


}
