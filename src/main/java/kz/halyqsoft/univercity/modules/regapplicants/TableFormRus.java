package kz.halyqsoft.univercity.modules.regapplicants;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class TableFormRus {

    private Document document;
    private PdfPTable pdfPTable;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ID studentId;
    private static int fontSize = 7;

    public TableFormRus(Document document, ID studentID){
        this.document = document;
        this.studentId=studentID;
        initialize();
    }
    public void initialize() {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);
            PdfPTable table = new PdfPTable(7);

            for(int i = 1 ; i <= 2; i++){
                insertCell(table, "СЕМЕСТР " + i, Element.ALIGN_CENTER, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                insertCell(table, "Дисциплины обязательного компонента", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

                String headers[]  = {"Код дисциплины", "Модуль обучения", "Название дисциплины", "К-во кредитов","ЕСТS" ,  "Семестр" ,"Ф.И.О. тьютора"};


                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                table.setWidthPercentage(100);

                List<ArrayList<String>> myData = TableForm.getData(studentId , true , IUPS_TYPE.MANDATORY,i, 1);
                int creditNum = 0;
                int ectsNum = 0;
                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }
                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = TableForm.getData(studentId , true , IUPS_TYPE.CHOOSEN,i, 1);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }


                insertCell(table, "Дополнительные дисциплины студента:\t", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = TableForm.getData(studentId , true , IUPS_TYPE.ADDITIONAL,i ,1);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table, ("Всего в семестре:"), Element.ALIGN_LEFT, 3, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                insertCell(table,String.valueOf(creditNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,String.valueOf(ectsNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            }

            PdfPTable table1 = new PdfPTable(7);

            for(int i = 1 ; i <= 2; i++){
                insertCell(table1, "СЕМЕСТР " + i, Element.ALIGN_CENTER, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                insertCell(table1, "Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                String headers[]  = {"Код дисциплины", "Модуль обучения", "Название дисциплины", "К-во кредитов","ЕСТS" ,  "Ф.И.О. преподавателя" ,"Форма контроля"};

                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                table1.setWidthPercentage(100);

                List<ArrayList<String>> myData = TableForm.getData(studentId , true , IUPS_TYPE.MANDATORY,i, 2);
                int creditNum = 0;
                int ectsNum = 0;
                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }
                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table1, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = TableForm.getData(studentId , true , IUPS_TYPE.CHOOSEN,i, 2);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }


                insertCell(table1, "Дополнительные дисциплины студента:\t", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = TableForm.getData(studentId , true , IUPS_TYPE.ADDITIONAL,i ,2);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table1, ("Всего в семестре:"), Element.ALIGN_LEFT, 3, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                insertCell(table1,String.valueOf(creditNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table1,String.valueOf(ectsNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table1,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table1,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            }



            try {
                if(document.isOpen()){
                    document.open();
                }
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));

                document.newPage();
                document.add(new Paragraph("Дисциплины изучаемые в семестре:", EmployeePdfCreator.getFont(12, Font.BOLD)));
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));
                document.add(table);
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));
                document.add(new Paragraph("Подпись студента                              День   «        »                              " + Calendar.getInstance().get(Calendar.YEAR) + "г.", EmployeePdfCreator.getFont(12, Font.BOLD)));

                document.newPage();
                document.add(table1);
            } catch (Exception e) {
                CommonUtils.LOG.error(e.getMessage());
                document.open();
            }



            document.add(new Paragraph("\n "));
            document.add(new Paragraph("\n "));
            document.add(new Paragraph("\n "));
            document.add(new Paragraph("Студент    __________  _______________________\n" +
                    "                         (подпись)              (Ф.И.О.)         \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Эдвайзер    __________  _______________________\n" +
                    "                        (подпись)            (Ф.И.О.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Офис регистратор     __________  __________________\n" +
                    "                               (подпись)            (Ф.И.О.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            try {
                pdfWriter.close();
            } catch (Exception e) {
                CommonUtils.LOG.error(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

}
