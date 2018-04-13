package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class CustomDocument {
    private Document document;
    private ByteArrayOutputStream byteArrayOutputStream;
    private HashMap<Integer, String> styleHash = new HashMap<Integer, String>();
    private List<PDF_PROPERTY> pdfProperties = new ArrayList<>();
    public static final String FONTBOLD = "resources/fonts/FreeSansBold.ttf";


    Font pFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD);
    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public CustomDocument() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        document = new Document();
    }


    public void initialize(ArrayList<CustomField> customFieldList, String title) {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);


            document.open();
            Paragraph paragraph = new Paragraph(title, pFont);
            paragraph.setSpacingBefore(35f);
            paragraph.setIndentationLeft(200f);
            document.add(paragraph);

            for (CustomField cf : customFieldList) {
                Font font = new Font(Font.FontFamily.valueOf(cf.getStyleComboBox().getValue().toString().toUpperCase()),
                        Integer.parseInt(cf.getTextSize().getValue()), cf.getFontComboBox().getTabIndex());
//                BaseFont bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//                PdfContentByte cb = pdfWriter.getDirectContent();
//                cb.saveState();
//                cb.beginText();
//                cb.moveText(Integer.parseInt(cf.getxIntegerField().getValue()), Integer.parseInt(cf.getyIntegerField().getValue()));
//                cb.setFontAndSize(bf,Integer.parseInt(cf.getTextSize().getValue()));
//                for(int i = 0; i<bfArray.length;i++){
//                    while(iterator.hasNext()) {
//                        Map.Entry mentry = (Map.Entry)iterator.next();
//                        if(cf.getStyleComboBox().getValue().equals(mentry.getValue()))
//                            cb.setFontAndSize(bfArray[(Integer)mentry.getKey()],Integer.parseInt(cf.getTextSize().getValue()));
//                    }}
//                    cb.showText(cf.getTextField().getValue());
//
//                cb.endText();
//                cb.restoreState();
                Paragraph paragraph1 = new Paragraph(cf.getTextField().getValue(), font);
                float x = (float) Integer.parseInt(cf.getxIntegerField().getValue());
                float y = (float) Integer.parseInt(cf.getyIntegerField().getValue());

                paragraph1.setSpacingBefore(x);
                paragraph1.setIndentationLeft(y);

                PDF_PROPERTY pdfProperty=new PDF_PROPERTY();
                pdfProperty.setText(cf.getTextField().getValue());
                pdfProperty.setX(x);
                pdfProperty.setY(y);
                pdfProperty.setFont(cf.getFontComboBox().getValue().toString());
                pdfProperty.setStyle(cf.getStyleComboBox().getValue().toString());
                pdfProperty.setSize(Integer.parseInt(cf.getTextSize().getValue()));
                pdfProperties.add(pdfProperty);
//                pdfProperty.setPdfDocument(pdfDocument);
//                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(pdfProperty);

                document.add(paragraph1);
            }

            document.close();
            pdfWriter.close();
        } catch (Exception e) {
          ErrorUtils.LOG.error("Unable to create pdf property",e);
            Message.showError(e.toString());
            e.printStackTrace();
        }
    }

    public List<PDF_PROPERTY> getPdfProperties() {
        return pdfProperties;
    }

    public void setPdfProperties(List<PDF_PROPERTY> pdfProperties) {
        this.pdfProperties = pdfProperties;
    }
}
