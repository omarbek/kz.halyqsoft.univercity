package kz.halyqsoft.univercity.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
                    Font font = getFont(12, Font.BOLD);
                    properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(propertyQM);
                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                    PdfWriter pdfWriter = PdfWriter.getInstance(docum, byteArrayOutputStream1);

                    docum.open();

                    Paragraph title = new Paragraph(document.getPdfDocument().getTitle(),
                            getFont(12, Font.BOLD));
                    title.setAlignment(Element.ALIGN_CENTER);

                    docum.add(title);

                    for (PDF_PROPERTY property : properties) {

                        Map<String,List>map = new HashMap();
                        String jsonText = "";
                        if(property.getText().toCharArray()[0]=='$'){

                            QueryModel<CATALOG> catalogQM = new QueryModel<>(CATALOG.class);
                            catalogQM.addWhere("name" , ECriteria.EQUAL, property.getText());
                            try{
                                jsonText = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(catalogQM).getValue();
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                            if(!jsonText.equals("")){
                                try {

                                    String[]json = jsonText.split(",");
                                    for(String js : json){
                                        Map<String,Object> tempMap = JsonUtils.toMap(new JSONObject(js));
                                        for(String key : tempMap.keySet()){
                                            if(map.get(key)==null){
                                                map.put(key,new ArrayList<>());
                                            }
                                            map.get(key).add(tempMap.get(key));
                                        }
                                    }

                                    System.out.println(map);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(map.keySet().size()>0){

                            PdfPTable pdfPTable = new PdfPTable(map.keySet().size());
                            for(String key : map.keySet()){

                                PdfPCell cell = new PdfPCell();
                                Paragraph paragraph = new Paragraph(key);
                                paragraph.setFont(font);
                                cell.addElement(paragraph);

                                pdfPTable.addCell(cell);


                            }
                            pdfPTable.setHeaderRows(1);

                            for(String key : map.keySet()){
                                for(Object value : map.get(key)){

                                    PdfPCell cell = new PdfPCell();
                                    Paragraph paragraph = new Paragraph((String)value);
                                    paragraph.setFont(font);
                                    cell.addElement(paragraph);

                                    pdfPTable.addCell(cell);
                                }
                            }

                            docum.add(pdfPTable);
                        }else{
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

    public static PdfPTable createFirstTable(Map<String,String> map) {

        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Cell with colspan 3"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2
        cell = new PdfPCell(new Phrase("Cell with rowspan 2"));
        cell.setRowspan(2);
        table.addCell(cell);
        // we add the four remaining cells with addCell()
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
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

    public  static StreamResource getStreamResourceFromPdfDocument(PDF_DOCUMENT pdfDocument){
        String fileName = pdfDocument.getFileName()+"_" + Calendar.getInstance().getTimeInMillis() + ".pdf";

        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                Document docum = new Document();

                QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
                FromItem doc = propertyQM.addJoin(EJoin.INNER_JOIN, "pdfDocument", PDF_DOCUMENT.class, "id");

                propertyQM.addWhere(doc, "id", ECriteria.EQUAL, pdfDocument.getId());
                propertyQM.addOrder("orderNumber");
                List<PDF_PROPERTY> properties = null;
                try {
                    properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(propertyQM);
                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                    PdfWriter pdfWriter = PdfWriter.getInstance(docum, byteArrayOutputStream1);

                    docum.open();

                    Paragraph title = new Paragraph(pdfDocument.getTitle(),
                            getFont(12, Font.BOLD));
                    title.setAlignment(Element.ALIGN_CENTER);

                    docum.add(title);

                    for (PDF_PROPERTY property : properties) {
                        Paragraph paragraph = new Paragraph(property.getText(),
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
                    return new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, fileName);
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
