package kz.halyqsoft.univercity.modules.regapplicants;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.modules.pdf.CustomField;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;


public class TableForm {
    private Document document;
    private PdfPTable pdfPTable;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ID studentId;
    private static int fontSize = 9;

    public TableForm(Document document, ID studentID) {
        this.document = document;
        this.studentId = studentID;
        initialize();
    }

    public void initialize() {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);
            PdfPTable table = new PdfPTable(7);

            for(int i = 1 ; i <= 2; i++){
                insertCell(table, "СЕМЕСТР " + i, Element.ALIGN_CENTER, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                insertCell(table, "Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                String headers[]  = {"Пәннің коды", "Модуль түрлері", "Пәннің толық атауы", "Кредит саны","ЕСТS" ,  "Семестр" ,"Тьютордың аты-жөні"};

                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                table.setWidthPercentage(100);

                List<ArrayList<String>> myData = getData(studentId , false , IUPS_TYPE.MANDATORY,i, 1);
                int creditNum = 0;
                int ectsNum = 0;
                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }
                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = getData(studentId , false , IUPS_TYPE.CHOOSEN,i, 1);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }


                insertCell(table, "Студенттің қосымша пәндері:\t", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                for(String s : headers ){
                    insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = getData(studentId , false , IUPS_TYPE.ADDITIONAL,i ,1);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table, ("Семестрде барлығы"), Element.ALIGN_LEFT, 3, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
                insertCell(table,String.valueOf(creditNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,String.valueOf(ectsNum), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
                insertCell(table,"", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            }

            PdfPTable table1 = new PdfPTable(7);
            for(int i = 1 ; i <= 2; i++){
                insertCell(table1, "СЕМЕСТР " + i, Element.ALIGN_CENTER, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                insertCell(table1, "Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                String headers[]  = {"Пәннің коды", "Модуль түрлері", "Пәннің толық атауы", "Кредит саны","ЕСТS" ,  "Оқытушының аты-жөні", "Емтихан"};

                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                table1.setWidthPercentage(100);

                List<ArrayList<String>> myData = getData(studentId , false , IUPS_TYPE.MANDATORY,i, 2);
                int creditNum = 0;
                int ectsNum = 0;
                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }
                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table1, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));

                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = getData(studentId , false , IUPS_TYPE.CHOOSEN,i, 2);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }


                insertCell(table1, "Студенттің қосымша пәндері:\t", Element.ALIGN_LEFT, 7, EmployeePdfCreator.getFont(fontSize, Font.BOLD));


                for(String s : headers ){
                    insertCell(table1, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                }

                myData = getData(studentId , false , IUPS_TYPE.ADDITIONAL,i ,2);

                for(List<String> myRow : myData){
                    for(String value : myRow){
                        insertCell(table1, value, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(fontSize, Font.BOLD));
                    }

                    creditNum += Integer.parseInt(myRow.get(3).trim().length()>0 ? myRow.get(3) :"0");
                    ectsNum += Integer.parseInt(myRow.get(4).trim().length()>0 ? myRow.get(4) :"0");
                }

                insertCell(table1, ("Семестрде барлығы"), Element.ALIGN_LEFT, 3, EmployeePdfCreator.getFont(fontSize, Font.NORMAL));
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
                document.add(new Paragraph(" Семестрде оқылатын пәндер тізімі:", EmployeePdfCreator.getFont(12, Font.BOLD)));
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));
                document.add(table);
                document.add(new Paragraph("\n ")); document.add(new Paragraph("\n "));
                document.add(new Paragraph("Студенттің қолы                              Күні   «        »                              " + Calendar.getInstance().get(Calendar.YEAR) + "ж.", EmployeePdfCreator.getFont(12, Font.BOLD)));

                document.newPage();
                document.add(table1);
            } catch (Exception e) {
                CommonUtils.LOG.error(e.getMessage());
                document.open();
            }



            document.add(new Paragraph("\n "));
            document.add(new Paragraph("\n "));
            document.add(new Paragraph("Студент    __________  _______________________\n" +
                    "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Эдвайзер    __________  _______________________\n" +
                    "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Тіркеу офисі     __________  __________________\n" +
                    "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));
            try {
                pdfWriter.close();
            } catch (Exception e) {
                CommonUtils.LOG.error(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public static List<ArrayList<String>> getData(ID student , boolean isRussian, IUPS_TYPE iupsType , int semPeriod, int documentType){

        int variant = 0;

        List<ArrayList<String>> result = new ArrayList();
        if(student == null){
            return  result;
        }
        SEMESTER_DATA semesterData  = CommonUtils.getCurrentSemesterData();

        long semData = semesterData.getId().getId().longValue();
        long studentId = student.getId().longValue();
        Map<Integer, Object> param = new HashMap<>();

        String sql  ="SELECT\n" +
                "  DISTINCT\n" +
                "  s4.code::text,\n" +
                "  module.module_short_name                                                       moduleType,\n" ;

                if(isRussian){
                    sql = sql + "  subj.name_RU                                                                   subjectName,\n" ;
                }else{
                    sql = sql + "  subj.name_KZ                                                                   subjectName,\n" ;
                }

                sql = sql +
                "  credit.credit::text,\n" +
                "  ects.ects::text,\n" ;

                if(documentType==1){
                 sql +="  sem.semester_name,\n" +
                                 "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" ;
                }else if(documentType==2){
                    sql +="  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                        "  control.type_name                                                              examType\n";
                }

                sql +="  FROM student_subject stu_subj\n" +
                "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
                "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
                "  INNER JOIN users usr ON student.id = usr.id\n" +
                "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
                "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
                "  INNER JOIN semester sem\n" +
                "    ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
                "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
                "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
                "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
                "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
                "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
                "  LEFT JOIN teacher_subject ts ON ts.subject_id = subj.id\n" +
                "  LEFT JOIN subject sbj ON ts.subject_id = sbj.id\n" +
                "  LEFT JOIN users u ON u.id = ts.employee_id\n" +
                "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id AND s3.student_education_id = stu_edu.id \n" +
                "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" ;

                if(iupsType == IUPS_TYPE.CHOOSEN){
                sql+= "  INNER JOIN elective_binded_subject ebs ON s4.elective_binded_subject_id = ebs.id\n" +
                            "  INNER JOIN catalog_elective_subjects c2 ON ebs.catalog_elective_subjects_id = c2.id\n";
                }

                sql +="  WHERE  usr.deleted = FALSE\n" +
                "      AND sem_data.id = " + semData + " \n" +
                "      AND usr.deleted = FALSE\n" +
                "      AND subj.subject_cycle_id IS NOT NULL\n" +
                "      AND sem_data.semester_period_id = "+semPeriod+" \n" ;

                if(iupsType == IUPS_TYPE.MANDATORY) {

                    sql = sql + "      AND subj.mandatory = TRUE\n" +
                            "      AND subj.module_id != 3\n" ;

                }else if(iupsType == IUPS_TYPE.CHOOSEN){

                    sql = sql + "      AND subj.mandatory = FALSE\n" +
                                    "      AND subj.module_id != 3\n" +
                                    "      AND c2.speciality_id = stu_edu.speciality_id ";

                }else if(iupsType == IUPS_TYPE.ADDITIONAL){

                    sql += "      AND subj.module_id = 3\n " ;

                }

                sql = sql +
                "      AND usr.locked = FALSE\n" +
                "      AND usr.id = "+studentId+"\n" ;

        try {
            List<Object> tmpList = new ArrayList<>();
            tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, param));
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    ArrayList<String> valuesList = new ArrayList();
                    for(int i = 0 ; i < oo.length; i++){
                        valuesList.add(oo[i] != null ? (String)oo[i]  : "");
                    }
                    result.add(valuesList);
                }
            }else{
                ArrayList<String> valuesList = new ArrayList();
                for(int i = 0 ; i < 7; i++){
                    valuesList.add(" ");
                }
                result.add(valuesList);
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
        }
        return result;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}


