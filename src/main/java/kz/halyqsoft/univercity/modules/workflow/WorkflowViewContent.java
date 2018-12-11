package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class WorkflowViewContent {
    VerticalLayout mainVL;
    VerticalLayout tab2MainVL;
    HorizontalSplitPanel mainHSP;
    VerticalLayout secondMainVL;
    public static boolean isForHRD;
    public static String MY_DOCUMENTS = getUILocaleUtil().getCaption("my_documents");

    public static String CREATE = getUILocaleUtil().getCaption("create");

    public static String INCOMING = getUILocaleUtil().getCaption("incoming");
    public static String OUTCOMING = getUILocaleUtil().getCaption("outcoming");

    public static String O_ON_AGREE = getUILocaleUtil().getCaption("on_agree");
    public static String O_ON_SIGN = getUILocaleUtil().getCaption("on_sign");
    public static String I_ON_AGREE = getUILocaleUtil().getCaption("on_agree")+" ";
    public static String I_ON_SIGN = getUILocaleUtil().getCaption("on_sign")+" ";

    public WorkflowViewContent(boolean isForHRD) {
        initView();
        WorkflowViewContent.isForHRD = isForHRD;
    }

    public void initView(){
        mainVL = new VerticalLayout();
        initTab1();
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

        myItems.add(new MyItem(optionHC.addItem(CREATE) ,CREATE, null));
        myItems.add(new MyItem(optionHC.addItem(INCOMING), INCOMING , null));
        myItems.add(new MyItem(optionHC.addItem(OUTCOMING) , OUTCOMING, null));
        myItems.add(new MyItem(optionHC.addItem(MY_DOCUMENTS),MY_DOCUMENTS , null));

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

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
