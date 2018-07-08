package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

public class PdfView extends AbstractTaskView implements EntityListener {

    private TabSheet mainTS;
    private Component searchComponent;
    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }
    @Override
    public void initView(boolean b) throws Exception {

        mainTS = new TabSheet();
        mainTS.addTab(initAndGetGenerationTab(), getUILocaleUtil().getCaption("generate"));

        VerticalLayout mainVL = new VerticalLayout();

        searchComponent = initAndGetSearchTab();

        mainVL.addComponent(searchComponent);
        mainTS.addTab(mainVL, getUILocaleUtil().getCaption("search"));

        mainTS.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                if(selectedTabChangeEvent.getTabSheet().getTab(mainVL)!=null){
                    mainVL.removeComponent(searchComponent);
                    try{
                        searchComponent = initAndGetSearchTab();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mainVL.addComponent(searchComponent);
                }
            }
        });

        getContent().addComponent(mainTS);

    }

    public Component initAndGetSearchTab(){
        SearchTabContainer searchTabContainer = null;
        try{
            searchTabContainer = new SearchTabContainer();
        }catch (Exception e)
        {
            LOG.error("Unable to run search tab conatiner");
            e.printStackTrace();
        }
        return searchTabContainer;
    }

    public Component initAndGetGenerationTab(){


        HorizontalSplitPanel generateTabHSP = new HorizontalSplitPanel();

        VerticalLayout leftVl = new VerticalLayout();

        generateTabHSP.setSizeFull();
        generateTabHSP.setSplitPosition(10);

        TreeTable sideBarGenerateTT = new TreeTable();
        HierarchicalContainer optionHC = new HierarchicalContainer();

        String firstOption = getUILocaleUtil().getCaption("property");
        String secondOption = getUILocaleUtil().getCaption("access");
        optionHC.addItem(firstOption);
        optionHC.setChildrenAllowed(firstOption , false);
        optionHC.addItem(secondOption);
        optionHC.setChildrenAllowed(secondOption , false);

        sideBarGenerateTT.setContainerDataSource(optionHC);
        sideBarGenerateTT.setColumnReorderingAllowed(false);
        sideBarGenerateTT.setMultiSelect(false);
        sideBarGenerateTT.setNullSelectionAllowed(false);
        sideBarGenerateTT.setSizeFull();
        sideBarGenerateTT.setImmediate(true);
        sideBarGenerateTT.setSelectable(true);

        MenuColumn firstColumn = new MenuColumn();
        sideBarGenerateTT.addGeneratedColumn("first" , firstColumn);
        sideBarGenerateTT.setColumnHeader("first" , getUILocaleUtil().getCaption("menu"));
        sideBarGenerateTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                try {
                    if (valueChangeEvent != null && valueChangeEvent.getProperty() != null && valueChangeEvent.getProperty().getValue() != null) {

                        if(generateTabHSP.getSecondComponent()!=null){
                            generateTabHSP.removeComponent(leftVl);
                        }

                        leftVl.removeAllComponents();
                        if(valueChangeEvent.getProperty().getValue().toString().equals(firstOption))
                        {
                            PDF_DOCUMENT file = new PDF_DOCUMENT();
                            PdfEdit pdfEdit = new PdfEdit(file);

                            leftVl.addComponent(pdfEdit);
                        }else{
                            PdfAccess pdfAccess = new PdfAccess();
                            leftVl.addComponent(pdfAccess);
                        }

                        generateTabHSP.addComponent(leftVl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        generateTabHSP.addComponent(sideBarGenerateTT);

        return generateTabHSP;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {

        return false;
    }
}