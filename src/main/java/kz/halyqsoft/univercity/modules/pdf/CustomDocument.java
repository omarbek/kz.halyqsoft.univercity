package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDocument {

    private Document document;
    private ByteArrayOutputStream byteArrayOutputStream;
    private List<PDF_PROPERTY> pdfProperties = new ArrayList<>();
    private Map<String, Integer> fontMap;

    public CustomDocument() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        document = new Document();
        fontMap = new HashMap<>();
        fontMap.put(CustomField.BOLD, Font.BOLD);
        fontMap.put(CustomField.NORMAL, Font.NORMAL);
        fontMap.put(CustomField.ITALIC, Font.ITALIC);
        fontMap.put(CustomField.UNDERLINE, Font.UNDERLINE);
        fontMap.put(CustomField.BOLDITALIC, Font.BOLDITALIC);
    }

    private Font getFont(int fontSize, int font) {
        String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
        BaseFont timesNewRoman = null;
        try {
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Font(timesNewRoman, fontSize, font);
    }


    public void initialize(ArrayList<CustomField> customFieldList, String title) {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);

            document.open();
            Paragraph paragraph = new Paragraph(title, getFont(12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            for (CustomField cf : customFieldList) {
                cf.getxComboBox().setEnabled(true);

                Paragraph paragraph1 = new Paragraph(cf.getTextField().getValue(),
                        getFont(Integer.parseInt(cf.getTextSizeComboBox().getValue().toString()),
                                fontMap.get(cf.getFontComboBox().getValue().toString())));
                float x = (float) Integer.parseInt(cf.getxComboBox().getValue().toString());
                float y = (float) Integer.parseInt(cf.getyComboBox().getValue().toString());

                if(cf.getCenterCheckBox().getValue()){
                    paragraph1.setAlignment(Element.ALIGN_CENTER);
                }else if(cf.getRightCheckBox().getValue()){
                    paragraph1.setAlignment(Element.ALIGN_RIGHT);
                }

                paragraph1.setSpacingBefore(y);
                paragraph1.setIndentationLeft(x);


                PDF_PROPERTY pdfProperty = new PDF_PROPERTY();
                pdfProperty.setId(cf.getId());
                pdfProperty.setText(cf.getTextField().getValue());
                pdfProperty.setRight(cf.getRightCheckBox().getValue());
                pdfProperty.setX(x);
                pdfProperty.setY(y);
                pdfProperty.setFont(cf.getFontComboBox().getValue().toString());
                pdfProperty.setSize(Integer.parseInt(cf.getTextSizeComboBox().getValue().toString()));
                pdfProperty.setOrderNumber(Double.parseDouble(cf.getOrder().getValue().toString()));

                pdfProperty.setCenter(cf.getCenterCheckBox().getValue());
                pdfProperty.setRight(cf.getRightCheckBox().getValue());
                pdfProperty.setCustom(cf.getCustomCheckBox().getValue());

                pdfProperties.add(pdfProperty);

                document.add(paragraph1);

            }

            document.close();
            pdfWriter.close();
        } catch (Exception e) {
            CommonUtils.LOG.error("Unable to create pdf property", e);
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
}
