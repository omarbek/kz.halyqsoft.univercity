package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FAccountantFilter;
import kz.halyqsoft.univercity.filter.FPdfDocumentFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

public class PdfDocumentFilterPanel extends AbstractFilterPanel {


    public PdfDocumentFilterPanel(FPdfDocumentFilter filterBean) {
        super(filterBean);
    }

    @Override
    protected void initWidget() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidth("100%");
        hl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        AbstractField af = getFilterComponent("fileName");
        af.setWidth("90%");
        if (af != null) {
            hl.addComponent(af);

        }

        af = getFilterComponent("pdfDocumentType");
        af.setWidth("90%");
        if (af != null) {
            hl.addComponent(af);

        }


        getContent().addComponent(hl);
        getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
    }
}
