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
    public static String MY_DOCUMENTS ;
    public static String CREATE ;
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
        CREATE = getUILocaleUtil().getCaption("create");

        INCOMING = getUILocaleUtil().getCaption("incoming");
        OUTCOMING = getUILocaleUtil().getCaption("outcoming");

        O_ON_AGREE = getUILocaleUtil().getCaption("on_agree");
        O_ON_SIGN = getUILocaleUtil().getCaption("on_sign");

        I_ON_AGREE = getUILocaleUtil().getCaption("on_agree")+" ";
        I_ON_SIGN = getUILocaleUtil().getCaption("on_sign")+" ";

        WorkflowViewContent content = new WorkflowViewContent(false);
        getContent().addComponent(content.getMainVL());
    }
}
