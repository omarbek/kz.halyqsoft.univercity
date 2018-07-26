package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class PdfView extends AbstractTaskView implements EntityListener {


    private final String FIRST_OPTION = getUILocaleUtil().getCaption("property");
    private final String SECOND_OPTION = getUILocaleUtil().getCaption("access");


    private TabSheet mainTS;
    private Component searchComponent;
    private TreeTable sideBarGenerateTT;

    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }
    @Override
    public void initView(boolean b) throws Exception {

        mainTS = new TabSheet();
        mainTS.addTab(initAndGetGenerationTab(), getUILocaleUtil().getCaption("generate"));
        mainTS.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                if(sideBarGenerateTT!=null){
                    sideBarGenerateTT.select(null);
                    sideBarGenerateTT.select(FIRST_OPTION);
                }
            }
        });
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

        sideBarGenerateTT = new TreeTable();
        HierarchicalContainer optionHC = new HierarchicalContainer();

        optionHC.addItem(FIRST_OPTION);
        optionHC.setChildrenAllowed(FIRST_OPTION, false);
        optionHC.addItem(SECOND_OPTION);
        optionHC.setChildrenAllowed(SECOND_OPTION, false);

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
                        if(valueChangeEvent.getProperty().getValue().toString().equals(FIRST_OPTION))
                        {
                            PDF_DOCUMENT file = new PDF_DOCUMENT();
                            PdfGenerationPart pdfGenerationPart = new PdfGenerationPart(file, getUILocaleUtil().getCaption("loading"));
                            leftVl.addComponent(pdfGenerationPart.getPdfHSP());
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