package kz.halyqsoft.univercity.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @author Assylkhan
 * on 06.11.2018
 * @project kz.halyqsoft.univercity
 */
public class PdfCreator {

    private ArrayList<Element> elements = new ArrayList();
    private Document document;

    public byte[] createAndGetPdf() throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        for(Element element : elements){
            document.add(element);
        }
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    public void add(Element element){
        elements.add(element);
    }
}
