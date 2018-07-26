package kz.halyqsoft.univercity.modules.workflow.views.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

public class EmployeePdfCreator {

    public static StreamResource createResourceStudent(DOCUMENT document) {
        String fileName = document.getPdfDocument().getFileName()+"_" + Calendar.getInstance().getTimeInMillis() + ".pdf";

        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                Document docum = new Document();

                QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
                FromItem doc = propertyQM.addJoin(EJoin.INNER_JOIN, "pdfDocument", PDF_DOCUMENT.class, "id");

                propertyQM.addWhere(doc, "id", ECriteria.EQUAL, document.getPdfDocument().getId());
                propertyQM.addOrder("orderNumber");
                List<PDF_PROPERTY> properties = null;
                try {
                    properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(propertyQM);
                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                    PdfWriter pdfWriter = PdfWriter.getInstance(docum, byteArrayOutputStream1);

                    docum.open();

                    Paragraph title = new Paragraph(document.getPdfDocument().getTitle(),
                            getFont(12, Font.BOLD));
                    title.setAlignment(Element.ALIGN_CENTER);

                    docum.add(title);

                    for (PDF_PROPERTY property : properties) {

                        String text = setReplaced(property.getText(), document.getCreatorEmployee());
                        Paragraph paragraph = new Paragraph(text,
                                getFont(Integer.parseInt(property.getSize().toString()), CommonUtils.getFontMap(property.getFont().toString())));

                        if (property.isCenter() == true) {
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                        }
                        paragraph.setSpacingBefore(property.getY());
                        paragraph.setIndentationLeft(property.getX());

                        docum.add(paragraph);
                    }

                    pdfWriter.close();
                    docum.close();
                    document.setFileByte(byteArrayOutputStream1.toByteArray());
                    return new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, fileName);
    }

    private static String setReplaced(String text, USERS employee) {
        String result = text.replaceAll("\\$fio", employee.getFirstName() +" " + employee.getLastName())
            .replaceAll("\\$phone", "+7" + employee.getPhoneMobile())
            .replaceAll("\\$aboutMe", "-")
            .replaceAll("\\$country", employee.getCitizenship().toString())
            .replaceAll("\\$status", employee.getMaritalStatus().toString())
            .replaceAll("\\$gender", employee.getSex().toString())
            .replaceAll("\\$nationality", employee.getNationality().toString());
        return result;
    }


    private static Font getFont(int fontSize, int font) {
        String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
        BaseFont timesNewRoman = null;
        try {
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return new Font(timesNewRoman, fontSize, font);
    }

    public static StreamResource getStreamResourceFromByte(byte[] file, String fileName){
         return new StreamResource(new StreamResource.StreamSource() {
             @Override
             public InputStream getStream() {
                 return new ByteArrayInputStream(file);
             }
         }, fileName);
    }

}