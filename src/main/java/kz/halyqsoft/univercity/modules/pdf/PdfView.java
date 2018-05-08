package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.Document;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfView extends AbstractTaskView {


    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }
    @Override
    public void initView(boolean b) throws Exception {
        PDF_DOCUMENT file = new PDF_DOCUMENT();
        PdfEdit pdfEdit = new PdfEdit(file);
        getContent().addComponent(pdfEdit);


    }}