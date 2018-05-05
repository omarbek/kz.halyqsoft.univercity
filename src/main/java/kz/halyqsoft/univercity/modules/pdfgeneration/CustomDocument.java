package kz.halyqsoft.univercity.modules.pdfgeneration;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CustomDocument {

    private Document document;
    private Font titleFont = new Font();
    private ByteArrayOutputStream byteArrayOutputStream;
    Font[] fonts = {
            new Font(),
            new Font(Font.FontFamily.COURIER,22,Font.BOLD, new BaseColor(0,0,0)),
            new Font(Font.FontFamily.HELVETICA,9,Font.ITALIC, new BaseColor( 250, 0, 13)),
            new Font(Font.FontFamily.SYMBOL,14,Font.NORMAL, new BaseColor(0,52,0)),
            new Font(Font.FontFamily.TIMES_ROMAN,25,Font.UNDERLINE, new BaseColor(0,0,20)),
    };

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }
    public void chooseFont(String family, int size, String font){

        titleFont.setFamily(family);
        titleFont.setSize(size);
        titleFont.setStyle(font);
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

    public CustomDocument(){
        byteArrayOutputStream = new ByteArrayOutputStream();
        document = new Document();
    }

    public void initialize( ArrayList<CustomField> customFieldList , String title ){
        try{

            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);


            document.open();
            Paragraph paragraph = new Paragraph();
            paragraph.add(title);
            paragraph.setSpacingBefore(50f);
            paragraph.setFont(fonts[0]);
            document.add(paragraph);

            for(CustomField cf : customFieldList) {
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                PdfContentByte cb = pdfWriter.getDirectContent();
                cb.saveState();
                cb.beginText();
                cb.moveText(Integer.parseInt(cf.getxIntegerField().getValue()), Integer.parseInt(cf.getyIntegerField().getValue()));
                cb.setFontAndSize(bf, 12);
                cb.showText(cf.getTextField().getValue());
                cb.endText();
                cb.restoreState();
            }

            document.close();
            pdfWriter.close();}
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
