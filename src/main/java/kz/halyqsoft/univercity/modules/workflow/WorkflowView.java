package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;

public class WorkflowView extends AbstractTaskView {
    VerticalLayout mainVL;
    VerticalLayout tab2MainVL;
    HorizontalSplitPanel mainHSP;
    VerticalLayout secondMainVL;

    public static String MY_DOCUMENTS ;
    public static String MY_SAVES ;
    public static String COMMON_DOCUMENTS ;
    public static String MAIN ;
    public static String CREATE ;
    public static String SEARCH ;

    public static String INCOMING ;
    public static String OUTCOMING ;

    public static String O_ON_AGREE ;
    public static String O_ON_SIGN ;
    public static String I_ON_AGREE ;
    public static String I_ON_SIGN ;




    public WorkflowView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        MY_DOCUMENTS = getUILocaleUtil().getCaption("my_documents");
        MY_SAVES = getUILocaleUtil().getCaption("my_saves");
        COMMON_DOCUMENTS = getUILocaleUtil().getCaption("common_documents");
        MAIN = getUILocaleUtil().getCaption("main");
        CREATE = getUILocaleUtil().getCaption("create");
        SEARCH = getUILocaleUtil().getCaption("search");

        INCOMING = getUILocaleUtil().getCaption("incoming");
        OUTCOMING = getUILocaleUtil().getCaption("outcoming");

        O_ON_AGREE = getUILocaleUtil().getCaption("on_agree");
        O_ON_SIGN = getUILocaleUtil().getCaption("on_sign");

        I_ON_AGREE = getUILocaleUtil().getCaption("on_agree")+" ";
        I_ON_SIGN = getUILocaleUtil().getCaption("on_sign")+" ";

        mainVL = new VerticalLayout();
        initTab1();

        getContent().addComponent(mainVL);
    }

    public void initTab1(){
        mainHSP = new HorizontalSplitPanel();
        secondMainVL = new VerticalLayout();
        secondMainVL.setSizeFull();
        secondMainVL.setImmediate(true);
        secondMainVL.setResponsive(true);

        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(15);

        TreeTable sideBarTT = new TreeTable();
        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList<MyItem> myItems = new ArrayList<>();

        myItems.add(new MyItem(optionHC.addItem(MAIN) ,MAIN , null));
        myItems.add(new MyItem(optionHC.addItem(CREATE) ,CREATE, null));
        myItems.add(new MyItem(optionHC.addItem(SEARCH) ,SEARCH, null));
        myItems.add(new MyItem(optionHC.addItem(INCOMING), INCOMING , null));
        myItems.add(new MyItem(optionHC.addItem(OUTCOMING) , OUTCOMING, null));
        myItems.add(new MyItem(optionHC.addItem(MY_SAVES), MY_SAVES , null));
        myItems.add(new MyItem(optionHC.addItem(MY_DOCUMENTS),MY_DOCUMENTS , null));
        myItems.add(new MyItem(optionHC.addItem(COMMON_DOCUMENTS),COMMON_DOCUMENTS , null));

        myItems.add(new MyItem(optionHC.addItem(O_ON_AGREE), O_ON_AGREE, INCOMING));
        myItems.add(new MyItem(optionHC.addItem(O_ON_SIGN), O_ON_SIGN, INCOMING));

        myItems.add(new MyItem(optionHC.addItem(I_ON_AGREE),I_ON_AGREE , OUTCOMING));
        myItems.add(new MyItem(optionHC.addItem(I_ON_SIGN),I_ON_SIGN , OUTCOMING));

        for(MyItem myItem : myItems){
            if(myItem.getParentId()!=null){
                optionHC.setParent(myItem.getCurrentId() , myItem.getParentId());
            }
        }

        for(MyItem myItem : myItems){
            if(!optionHC.hasChildren(myItem.getCurrentId())){
                optionHC.setChildrenAllowed(myItem.getCurrentId(), false);
            }
        }

        sideBarTT.setContainerDataSource(optionHC);
        sideBarTT.setColumnReorderingAllowed(false);
        sideBarTT.setMultiSelect(false);
        sideBarTT.setNullSelectionAllowed(false);
        sideBarTT.setSizeFull();
        sideBarTT.setImmediate(true);
        sideBarTT.setSelectable(true);

        MenuColumn firstColumn = new MenuColumn();
        sideBarTT.addGeneratedColumn("first" , firstColumn);
        sideBarTT.setColumnHeader("first" , getUILocaleUtil().getCaption("menu"));
        sideBarTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                try {
                    if (valueChangeEvent != null && valueChangeEvent.getProperty() != null && valueChangeEvent.getProperty().getValue() != null) {

                        if(!optionHC.hasChildren(valueChangeEvent.getProperty().getValue())){
                            if(mainHSP.getSecondComponent()!=null){
                                mainHSP.removeComponent(secondMainVL);
                            }

                            secondMainVL.removeAllComponents();
                            ViewResolver view = new ViewResolver();
                            secondMainVL.addComponent(view.getViewByTitle((String) valueChangeEvent.getProperty().getValue()));
                            mainHSP.addComponent(secondMainVL);
                        }
                    }
                }catch (IllegalArgumentException iea){
                    Message.showError(iea.getMessage());
                    iea.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mainHSP.addComponent(sideBarTT);
        mainVL.addComponent(mainHSP);
    }

    public void initTab2(){

    }


}
