package kz.halyqsoft.univercity.modules.workflowforemp;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import javafx.scene.control.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DOCUMENT_TYPE;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.workflow.MyItem;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;

/**
 * @author Assylkhan
 * on 06.12.2018
 * @project kz.halyqsoft.univercity
 */
public class WorflowViewForEmp extends AbstractTaskView {
    private HorizontalSplitPanel mainHSP;
    private VerticalLayout mainVL;
    public static String WORK_FLOW = getUILocaleUtil().getCaption("workFlow");
    public static String JOURNAL = getUILocaleUtil().getCaption("journal");
    public static String EMPLOYEES = getUILocaleUtil().getCaption("employees");
    public static String DOCUMENT_TYPES = getUILocaleUtil().getCaption("documentTypes");
    public static String DOCUMENT_DOWNLOAD = getUILocaleUtil().getCaption("documentDownload");
    public WorflowViewForEmp(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setImmediate(true);
        mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainHSP.setSecondComponent(mainVL);

        TreeTable sideBarTT = new TreeTable();
        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList <MyItem>myItems = new <MyItem>ArrayList();

        myItems.add(new MyItem(optionHC.addItem(WORK_FLOW),WORK_FLOW , null));
        myItems.add(new MyItem(optionHC.addItem(JOURNAL),JOURNAL, null));
        myItems.add(new MyItem(optionHC.addItem(EMPLOYEES),EMPLOYEES, null));
        myItems.add(new MyItem(optionHC.addItem(DOCUMENT_TYPES),DOCUMENT_TYPES, null));
        myItems.add(new MyItem(optionHC.addItem(DOCUMENT_DOWNLOAD),DOCUMENT_DOWNLOAD, null));

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
                            mainVL.removeAllComponents();
                            ViewResolver view = new ViewResolver();
                            mainVL.addComponent(view.getViewByTitle((String) valueChangeEvent.getProperty().getValue()));
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

        mainHSP.setSplitPosition(13);
        mainHSP.setFirstComponent(sideBarTT);
        getContent().addComponent(mainHSP);
    }
}
