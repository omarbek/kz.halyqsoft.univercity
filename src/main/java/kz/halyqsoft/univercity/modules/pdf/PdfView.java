package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.SUBSTITUTION;
import kz.halyqsoft.univercity.modules.pdf.tabs.CatalogTabContainer;
import kz.halyqsoft.univercity.modules.pdf.tabs.SearchTabContainer;
import kz.halyqsoft.univercity.modules.pdf.tabs.SubstituitionTabContainer;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class PdfView extends AbstractTaskView {

    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        PdfViewContent pdfViewContent = new PdfViewContent(false);
        pdfViewContent.setSizeFull();
        getContent().addComponent(pdfViewContent);
    }
}