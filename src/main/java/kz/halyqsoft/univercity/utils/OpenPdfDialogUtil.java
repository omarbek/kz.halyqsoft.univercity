package kz.halyqsoft.univercity.utils;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpenPdfDialogUtil extends AbstractDialog{

    private VerticalLayout mainVL;
    private VerticalLayout refusedVL;

    public OpenPdfDialogUtil(DOCUMENT document, Integer widthPercentage, Integer heightPercentage){
        super();
        mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setImmediate(true);
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Embedded pdf = new Embedded(null, EmployeePdfCreator.getStreamResourceFromDocument( document));

        pdf.setImmediate(true);
        pdf.setSizeFull();
        pdf.setHeight(500, Unit.PIXELS);
        pdf.setMimeType("application/pdf");
        pdf.setType(2);

        mainVL.setHeight(100, Unit.PERCENTAGE);
        mainVL.addComponent(pdf);

        if(widthPercentage!=null && heightPercentage!=null){
            setWidth(widthPercentage+"%");
            setHeight(heightPercentage+"%");
        }else{
            setSizeFull();
        }
        getContent().setSizeFull();
        getContent().addComponent(mainVL);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return "PDF";
    }

    protected VerticalLayout getVerticalLayout() {
        return mainVL;
    }
}
