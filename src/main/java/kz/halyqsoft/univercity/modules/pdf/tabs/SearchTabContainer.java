package kz.halyqsoft.univercity.modules.pdf.tabs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.modules.pdf.MyFileEdit;
import kz.halyqsoft.univercity.modules.pdf.PdfViewContent;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class SearchTabContainer extends AbstractCommonView {

    private Table docTable;
    private final static String FILE_NAME = "fileName";
    private final static String CREATED = "created";


    public SearchTabContainer() throws Exception{
        setButtons();
        initTable();
    }

    private void setButtons() {
        HorizontalLayout buttonPanel = CommonUtils.createButtonPanel();

        Button editButton = new Button();
        editButton.setCaption(getUILocaleUtil().getCaption("edit"));
        editButton.setWidth(120.0F, Sizeable.Unit.PIXELS);
        editButton.setIcon(new ThemeResource("img/button/edit.png"));
        buttonPanel.addComponent(editButton);
        editButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Set<PDF_DOCUMENT> pdfDocuments = (Set<PDF_DOCUMENT>) docTable.getValue();
                if (pdfDocuments.isEmpty()) {
                    Message.showError(getUILocaleUtil().getMessage("choose.document"));
                } else {
                    new MyFileEdit(pdfDocuments.iterator().next(), SearchTabContainer.this);
                }
            }
        });

        Button deleteButton = new Button();
        deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
        deleteButton.setWidth(120.0F, Sizeable.Unit.PIXELS);
        deleteButton.setIcon(new ThemeResource("img/button/delete.png"));
        buttonPanel.addComponent(deleteButton);
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    Set<PDF_DOCUMENT> pdfDocuments = (Set<PDF_DOCUMENT>) docTable.getValue();
                    if (pdfDocuments.isEmpty()) {
                        Message.showError(getUILocaleUtil().getMessage("choose.document"));
                    } else {
                        PDF_DOCUMENT pdfDocument = pdfDocuments.iterator().next();
                        QueryModel<PDF_PROPERTY> propertyFileByIdQM = new QueryModel<>(PDF_PROPERTY.class);
                        propertyFileByIdQM.addWhere("pdfDocument", ECriteria.EQUAL, pdfDocument.getId());
                        List<PDF_PROPERTY> properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(propertyFileByIdQM);
                        pdfDocument.setDeleted(true);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(properties);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pdfDocument);

                        refresh();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getContent().addComponent(buttonPanel);
    }

    private void initTable() throws Exception {
        docTable = new Table();
        docTable.setPageLength(10);
        docTable.setWidth(100, Sizeable.Unit.PERCENTAGE);
        docTable.setHeight(100, Sizeable.Unit.PERCENTAGE);
        docTable.setColumnReorderingAllowed(false);
        docTable.setColumnCollapsingAllowed(false);
        docTable.setNullSelectionAllowed(false);
        docTable.setMultiSelect(true);
        docTable.setSelectable(true);
        docTable.setCaption(getUILocaleUtil().getCaption("my.documents"));
        docTable.setColumnHeader(FILE_NAME, getUILocaleUtil().getCaption("file.name"));
        docTable.setColumnHeader(CREATED, getUILocaleUtil().getCaption("created"));

        setAllColumnWidth();
        setAllColumnAlign();

        refresh();
        getContent().addComponent(docTable);
        getContent().setComponentAlignment(docTable, Alignment.TOP_CENTER);
    }

    public void refresh() throws Exception {
        USERS user = CommonUtils.getCurrentUser();
        QueryModel<PDF_DOCUMENT> docQM = new QueryModel<>(PDF_DOCUMENT.class);
        docQM.addWhere("deleted", ECriteria.EQUAL, false);
        if(!CommonUtils.isAdmin()){
            if (user != null) {
                docQM.addWhere("user", ECriteria.EQUAL, user.getId());
            }
        }
        if(PdfViewContent.isForHRD){
            docQM.addWhere("forHumanResourceDepartment", ECriteria.EQUAL, PdfViewContent.isForHRD);
        }
        BeanItemContainer<PDF_DOCUMENT> docBIC = new BeanItemContainer<>(PDF_DOCUMENT.class, SessionFacadeFactory.
                getSessionFacade(CommonEntityFacadeBean.class).lookup(docQM));
        docTable.setContainerDataSource(docBIC);
        docTable.setVisibleColumns(FILE_NAME,CREATED /*, OPEN_DOC_BUTTON, OPEN_BUTTON*/);
    }

    private void setAllColumnAlign() {
        docTable.setColumnAlignment(FILE_NAME, Table.Align.CENTER);
        docTable.setColumnAlignment(CREATED, Table.Align.CENTER);
    }
    private void setAllColumnWidth() {//TODO
        docTable.setColumnWidth(FILE_NAME, 805);//333
        docTable.setColumnWidth(CREATED, 500);//370
    }


    private static ByteArrayInputStream reteriveByteArrayInputStream(File file) throws IOException {
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    }

    @Override
    public String getViewName() {
        return getUILocaleUtil().getCaption("search");
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("search");
    }

    @Override
    public void initView(boolean b) throws Exception {

    }

}
