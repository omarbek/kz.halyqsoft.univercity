package kz.halyqsoft.univercity.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

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
