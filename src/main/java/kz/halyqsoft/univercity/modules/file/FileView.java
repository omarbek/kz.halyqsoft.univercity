package kz.halyqsoft.univercity.modules.file;


import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.modules.pdf.PdfEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.*;
import java.util.List;
import java.util.Set;

public class FileView extends AbstractTaskView {

    private Table docTable;

    private final static String FILE_NAME = "fileName";
    private final static String TITLE = "title";
//    private final static String OPEN_DOC_BUTTON = "openDocButton";
//    private final static String OPEN_BUTTON = "openButton";


    public FileView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        setButtons();
        initTable();
    }

    private void setButtons() {
        HorizontalLayout buttonPanel = CommonUtils.createButtonPanel();

        Button editButton = new Button();
        editButton.setCaption(getUILocaleUtil().getCaption("edit"));
        editButton.setWidth(120.0F, Unit.PIXELS);
        editButton.setIcon(new ThemeResource("img/button/edit.png"));
        buttonPanel.addComponent(editButton);
        editButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Set<PDF_DOCUMENT> pdfDocuments = (Set<PDF_DOCUMENT>) docTable.getValue();
                if (pdfDocuments.isEmpty()) {
                    Message.showError(getUILocaleUtil().getMessage("choose.document"));
                } else {
                    new FileEdit(pdfDocuments.iterator().next(), FileView.this);
                }
            }
        });

        Button deleteButton = new Button();
        deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
        deleteButton.setWidth(120.0F, Unit.PIXELS);
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
        docTable.setWidth(100, Unit.PERCENTAGE);
        docTable.setHeight(100, Unit.PERCENTAGE);
        docTable.setColumnReorderingAllowed(false);
        docTable.setColumnCollapsingAllowed(false);
        docTable.setNullSelectionAllowed(false);
        docTable.setMultiSelect(true);
        docTable.setSelectable(true);
        docTable.setCaption(getUILocaleUtil().getCaption("my.documents"));
        docTable.setColumnHeader(TITLE, getUILocaleUtil().getCaption("title"));
        docTable.setColumnHeader(FILE_NAME, getUILocaleUtil().getCaption("file.name"));

        setAllColumnWidth();
        setAllColumnAlign();

//        docTable.addGeneratedColumn(OPEN_DOC_BUTTON, new Table.ColumnGenerator() {
//            @Override
//            public Object generateCell(final Table table, Object o, Object o1) {
//                final Button openButton = getOpenButton(o, getUILocaleUtil().getCaption("open"));
//                File file = new File(((PDF_DOCUMENT) o).getFileName());
//                openButton.setData(file);
//                openButton.addClickListener(new Button.ClickListener() {
//                    @Override
//                    public void buttonClick(Button.ClickEvent clickEvent) {
//                        addFile(openButton);
//                    }
//                });
//                return openButton;
//            }
//        });
//
//        docTable.addGeneratedColumn(OPEN_BUTTON, new Table.ColumnGenerator() {
//            @Override
//            public Object generateCell(final Table table, Object o, Object o1) {
//                final Button openButton = getOpenButton(o, getUILocaleUtil().getCaption("open.new"));
//                File file = new File(((PDF_DOCUMENT) o).getFileName());
//                final StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
//                    @Override
//                    public InputStream getStream() {
//                        try {
//                            return reteriveByteArrayInputStream(file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                }, file.getName());
//
//                resource.setMIMEType("application/pdf");
//                final BrowserWindowOpener opener = new BrowserWindowOpener(resource);
//                opener.extend(openButton);
//
//                openButton.addClickListener(new Button.ClickListener() {
//                    @Override
//                    public void buttonClick(Button.ClickEvent clickEvent) {
//                        opener.setResource(null);
//                        opener.setResource(resource);
//
//                    }
//                });
//                return openButton;
//            }
//        });
//
//        docTable.setColumnHeader(OPEN_DOC_BUTTON, "");
//        docTable.setColumnHeader(OPEN_BUTTON, "");
        refresh();
        getContent().addComponent(docTable);
        getContent().setComponentAlignment(docTable, Alignment.TOP_CENTER);
    }

    private Button getOpenButton(Object o, String caption) {
        final Button openButton = new Button();
        openButton.setData(o);
        openButton.setCaption(caption);
        return openButton;
    }

    public void refresh() throws Exception {
        USERS user = CommonUtils.getCurrentUser();
        QueryModel<PDF_DOCUMENT> docQM = new QueryModel<>(PDF_DOCUMENT.class);
        docQM.addWhere("deleted", ECriteria.EQUAL, false);
        if (user != null) {
            docQM.addWhere("user", ECriteria.EQUAL, user.getId());
        }
        BeanItemContainer<PDF_DOCUMENT> docBIC = new BeanItemContainer<>(PDF_DOCUMENT.class, SessionFacadeFactory.
                getSessionFacade(CommonEntityFacadeBean.class).lookup(docQM));
        docTable.setContainerDataSource(docBIC);
        docTable.setVisibleColumns(TITLE, FILE_NAME/*, OPEN_DOC_BUTTON, OPEN_BUTTON*/);
    }

    private void setAllColumnAlign() {
        docTable.setColumnAlignment(TITLE, Table.Align.CENTER);
        docTable.setColumnAlignment(FILE_NAME, Table.Align.CENTER);
//        docTable.setColumnAlignment(OPEN_DOC_BUTTON, Table.Align.CENTER);
//        docTable.setColumnAlignment(OPEN_BUTTON, Table.Align.CENTER);
    }

    private void setAllColumnWidth() {//TODO
        docTable.setColumnWidth(TITLE, 805);//370
        docTable.setColumnWidth(FILE_NAME, 500);//333
//        docTable.setColumnWidth(OPEN_DOC_BUTTON, 150);
//        docTable.setColumnWidth(OPEN_BUTTON, 200);
    }

    private void addFile(Button openButton) {
        try {
            File root = (File) openButton.getData();
            Embedded pdf = new Embedded(null, new FileResource(root));
            pdf.setSizeFull();
            pdf.setMimeType("application/pdf");
            pdf.setType(2);
            pdf.setSizeFull();
            pdf.setHeight(700, Unit.PIXELS);
            getContent().removeAllComponents();
            getContent().addComponent(pdf);
        } catch (Exception ex) {
            LOG.error("Unable to load pdf: ", ex);
            Message.showError(ex.toString());
        }
    }

    private static ByteArrayInputStream reteriveByteArrayInputStream(File file) throws IOException {
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    }
}
