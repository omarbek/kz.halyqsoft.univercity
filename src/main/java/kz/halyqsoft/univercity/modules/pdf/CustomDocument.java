package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.TEACHER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDocument {

    private Document document;
    private PdfPTable pdfPTable;
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
        pdfPTable = new PdfPTable(new float[] { 2, 1, 2 });
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

            Document doc=new Document();
            document.open();
            Paragraph paragraph = new Paragraph(title, getFont(12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
           // document.add(paragraph);

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
                PdfPTable table = new PdfPTable(3); // 3 columns.

            table.setWidthPercentage(100);

            Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);


//            table.addCell(createLabelCell("Label 1"));
//            table.addCell(createValueCell("Value 1"));
//            table.addCell(createLabelCell("Label 2"));
//            table.addCell(createValueCell("Value 2"));
//            table.addCell(createLabelCell("Label 3"));
//            table.addCell(createValueCell("Value 3"));

            // 2nd Row
//            table.addCell(createLabelCell("Label 4"));
//            table.addCell(createValueCell("Value 4"));
//            table.addCell(createLabelCell("Label 5"));
//            table.addCell(createValueCell("Value 5"));
//            table.addCell(createLabelCell("Label 6"));
//            table.addCell(createValueCell("Value 6"));



            QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
            FromItem ssItem = subjectQM.addJoin(EJoin.INNER_JOIN,"id", STUDENT_SUBJECT.class,"subject");
            FromItem smItem =  subjectQM.addJoin(EJoin.INNER_JOIN,"subjectModule", SUBJECT_MODULE.class,"id");
            FromItem cItem = subjectQM.addJoin(EJoin.INNER_JOIN,"creditability",CREDITABILITY.class,"id");
            FromItem eItem = subjectQM.addJoin(EJoin.INNER_JOIN,"ects", ECTS.class,"id");
            FromItem sdItem = ssItem.addJoin(EJoin.INNER_JOIN,"semesterData", SEMESTER_DATA.class,"id");
            FromItem tsItem  =subjectQM.addJoin(EJoin.INNER_JOIN,"id", TEACHER_SUBJECT.class,"subject");
            FromItem uItem  =tsItem.addJoin(EJoin.INNER_JOIN,"employee", USERS.class,"id");
           // FromItem scItem  =subjectQM.addJoin(EJoin.INNER_JOIN,"subjectCode", SUBJECT_CODE.class,"id");
          //  subjectQM.addWhere(ssItem,"studentEducation", ECriteria.EQUAL,1293);

            List<SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(subjectQM);
            PdfPCell cell = new PdfPCell(new Phrase("HMKCODE.com - iText PDFTable Example",font));
            cell.setColspan(6);

            for(SUBJECT s:subjects){

              //  table.addCell(createLabelCell(s.getSubjectCode().toString()));
                table.addCell(createValueCell(s.getCreditability().toString()));
                table.addCell(cell);
            }

            document.add(table);

            document.close();
            pdfWriter.close();
        } catch (Exception e) {
            CommonUtils.LOG.error("Unable to create pdf property", e);
            Message.showError(e.toString());
            e.printStackTrace();
        }
    }

    private static PdfPCell createLabelCell(String text){

        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.DARK_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(text,font));

        return cell;
    }

    private static PdfPCell createValueCell(String text){
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text,font));

        return cell;
    }


    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        PdfPTable table = new PdfPTable(8);
        for(int aw = 0; aw < 16; aw++){
            table.addCell("hi");
        }
        document.add(table);
        document.close();
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
