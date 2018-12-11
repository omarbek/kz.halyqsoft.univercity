package kz.halyqsoft.univercity.modules.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
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
        pdfPTable = new PdfPTable(new float[]{2, 1, 2});
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

            Document doc = new Document();
            document.open();
            Paragraph paragraph = new Paragraph(title, EmployeePdfCreator.getFont(12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            for (CustomField cf : customFieldList) {
                cf.getxComboBox().setEnabled(true);

                Paragraph paragraph1 = new Paragraph(cf.getTextField().getValue(),
                        getFont(Integer.parseInt(cf.getTextSizeComboBox().getValue().toString()),
                                fontMap.get(cf.getFontComboBox().getValue().toString())));
                float x = (float) Integer.parseInt(cf.getxComboBox().getValue().toString());
                float y = (float) Integer.parseInt(cf.getyComboBox().getValue().toString());

                if (cf.getCenterCheckBox().getValue()) {
                    paragraph1.setAlignment(Element.ALIGN_CENTER);
                } else if (cf.getRightCheckBox().getValue()) {
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


//            insertCell(table, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sql1 = "SELECT  stu_subj.id,  'code'\n" +
//                "  code,   module.module_short_name moduleType,   subj.name_kz\n" +
//                "  subjectName,   credit.credit,   ects.ects,   sem.semester_name        semester,\n" +
//                "  'tutor'                  tutor,\n" +
//                "  control.type_name        examType\n" +
//                "FROM student_subject stu_subj\n" +
//                "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                "  INNER JOIN users usr ON student.id = usr.id\n" +
//                "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                "WHERE   sem_data.semester_period_id=1\n" +
//                "      AND usr.deleted = FALSE AND\n" +
//                "      subj.subject_cycle_id IS NOT NULL\n" +
//                "  AND subj.mandatory=FALSE AND " +
//                    "  subj.module_id  != 3 AND \n" +
//                "      usr.locked = FALSE AND usr.id = 1775";
//        Map<Integer, Object> param = new HashMap<>();
//        try {
//            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql1, param);
//            if (!tmpList.isEmpty()) {
//                for (Object o : tmpList) {
//                    Object[] oo = (Object[]) o;
//
//                    insertCell(table, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, ((String)oo[3]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, (String.valueOf((BigDecimal)(oo[5]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                    insertCell(table, ((String)oo[7]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                }
//            }
//        } catch (Exception ex) {
//            CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//        }
//
//        insertCell(table, "Студенттің қосымша пәндері:\t", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sql2 = "SELECT  stu_subj.id,  'code'\n" +
//                    "  code,   module.module_short_name moduleType,   subj.name_kz\n" +
//                    "  subjectName,   credit.credit,   ects.ects,   sem.semester_name        semester,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType,\n" +
//                    "  stu_subj.student_id,usr.id\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE  usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL\n" +
//                    "AND subj.module_id=3\n" +
//                    "  AND usr.locked = FALSE AND usr.id = 1775;";
//            Map<Integer, Object> para = new HashMap<>();
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql2, para);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[3]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[5]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[7]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            String sql3 = "SELECT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.id = 1775 AND\n" +
//                    "  sem_data.semester_period_id=1";
//
//            insertCell(table, ("Семестрде барлығы"), Element.ALIGN_LEFT, 3,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//            Map<Integer, Object> par = new HashMap<>();
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql3, par);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                       insertCell(table, (String.valueOf((BigDecimal)(oo[0]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[1]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//            insertCell(table, "СЕМЕСТР 2", Element.ALIGN_CENTER, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            insertCell(table, "Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            String sqlSem2 = "SELECT  stu_subj.id,  'code'\n" +
//                    "  code,   module.module_short_name moduleType,   subj.name_kz\n" +
//                    "  subjectName,   credit.credit,   ects.ects,   sem.semester_name        semester,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE   sem_data.semester_period_id=2\n" +
//                    "      AND usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL " +
//                    "  AND subj.module_id  != 3\n " +
//                    "  AND subj.mandatory=TRUE AND\n" +
//                    "      usr.locked = FALSE AND usr.id = 1775";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlSem2, params);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[3]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[5]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[7]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//            insertCell(table, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlAdd = "SELECT  stu_subj.id,  'code'\n" +
//                    "  code,   module.module_short_name moduleType,   subj.name_kz\n" +
//                    "  subjectName,   credit.credit,   ects.ects,   sem.semester_name        semester,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE   sem_data.semester_period_id=2\n" +
//                    "      AND usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL\n" +
//                    "  AND subj.mandatory=FALSE AND " +
//                    "  subj.module_id  != 3 AND \n" +
//                    "      usr.locked = FALSE AND usr.id = 1775";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlAdd, param);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[3]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[5]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[7]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table, "Студенттің қосымша пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlStud = "SELECT  stu_subj.id,  'code'\n" +
//                    "  code,   module.module_short_name moduleType,   subj.name_kz\n" +
//                    "  subjectName,   credit.credit,   ects.ects,   sem.semester_name        semester,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType,\n" +
//                    "  stu_subj.student_id,usr.id\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE  usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL\n" +
//                    "AND subj.module_id=3\n" +
//                    "  AND usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlStud, para);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[3]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[5]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, ((String)oo[7]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            String sqlSum = "SELECT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.id = 1775 AND\n" +
//                    "  sem_data.semester_period_id=2";
//
//            insertCell(table, ("Семестрде барлығы"), Element.ALIGN_LEFT, 3,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlSum, par);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[0]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table, (String.valueOf((BigDecimal)(oo[1]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//
//
//
//
//
//
//
//
//
//
//
//            PdfPTable table1 = new PdfPTable(6);
//            insertCell(table1, "Пәннің коды", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "Пәннің толық атауы", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "Кредит саны", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "ECTS", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "Оқытушының аты-жөні", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "Емтихан", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "СЕМЕСТР 1", Element.ALIGN_CENTER, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            table1.setWidthPercentage(100);
//
//            String sqlTeacherSem1 = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  sem_data.semester_period_id=1 AND\n" +
//                    "       usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL   AND subj.module_id  != 3\n" +
//                    "      AND subj.mandatory=TRUE AND\n" +
//                    "      usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeacherSem1, params);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//            insertCell(table1, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlTeaAdd = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  sem_data.semester_period_id=1\n" +
//                    "  AND usr.deleted = FALSE AND\n" +
//                    "  subj.subject_cycle_id IS NOT NULL\n" +
//                    "  AND subj.mandatory=FALSE AND\n" +
//                    "  subj.module_id  != 3 AND\n" +
//                    "  usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeaAdd, param);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table1, "Студенттің қосымша пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlTeachAdd = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.deleted = FALSE AND\n" +
//                    "  subj.subject_cycle_id IS NOT NULL AND subj.module_id=3\n" +
//                    "AND\n" +
//                    "  usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachAdd, para);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                       insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            String sqlTeachSum = "SELECT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.id = 1775 AND\n" +
//                    "  sem_data.semester_period_id=1";
//
//            insertCell(table1, ("Семестрде барлығы"), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachSum, par);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[0]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[1]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table1, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//
//
//
//
//
//
//
//
//
//
//            insertCell(table1, "СЕМЕСТР 2", Element.ALIGN_CENTER, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
//            insertCell(table1, "           Міндетті түрде оқытылатын пәндер:", Element.ALIGN_LEFT, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//
//            String sqlTeacherSem2 = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  sem_data.semester_period_id=2 AND\n" +
//                    "       usr.deleted = FALSE AND\n" +
//                    "      subj.subject_cycle_id IS NOT NULL   AND subj.module_id  != 3\n" +
//                    "      AND subj.mandatory=TRUE AND\n" +
//                    "      usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeacherSem2, params);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//            insertCell(table1, "Студенттің таңдаған пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlTeaAdd2 = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  sem_data.semester_period_id=2\n" +
//                    "  AND usr.deleted = FALSE AND\n" +
//                    "  subj.subject_cycle_id IS NOT NULL\n" +
//                    "  AND subj.mandatory=FALSE AND\n" +
//                    "  subj.module_id  != 3 AND\n" +
//                    "  usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeaAdd2, param);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table1, "Студенттің қосымша пәндері:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
//
//            String sqlTeachAdd2 = "SELECT  stu_subj.id,  'code'\n" +
//                    "                           code,    subj.name_kz subjectName,\n" +
//                    "                              credit.credit,   ects.ects,\n" +
//                    "  'tutor'                  tutor,\n" +
//                    "  control.type_name        examType\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.deleted = FALSE AND\n" +
//                    "  subj.subject_cycle_id IS NOT NULL AND subj.module_id=3\n" +
//                    "AND\n" +
//                    "  usr.locked = FALSE AND usr.id = 1775;";
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachAdd2, para);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, ((String)oo[1]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[2]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[3]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[4]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[5]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, ((String)oo[6]), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            String sqlTeachSum2 = "SELECT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
//                    "FROM student_subject stu_subj\n" +
//                    "  INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
//                    "  INNER JOIN student ON stu_edu.student_id = student.id AND stu_edu.child_id IS NULL\n" +
//                    "  INNER JOIN users usr ON student.id = usr.id\n" +
//                    "  INNER JOIN semester_subject sem_subj ON stu_subj.subject_id = sem_subj.id\n" +
//                    "  INNER JOIN semester_data sem_data ON sem_subj.semester_data_id = sem_data.id\n" +
//                    "  INNER JOIN semester sem     ON sem.study_year_id = stu_edu.study_year_id AND sem.semester_period_id = sem_data.semester_period_id\n" +
//                    "  INNER JOIN subject subj ON sem_subj.subject_id = subj.id\n" +
//                    "  INNER JOIN creditability credit ON subj.creditability_id = credit.id\n" +
//                    "  INNER JOIN ects ON subj.ects_id = ects.id\n" +
//                    "  INNER JOIN subject_module module ON subj.module_id = module.id\n" +
//                    "  INNER JOIN control_type control ON subj.control_type_id = control.id\n" +
//                    "WHERE\n" +
//                    "  usr.id = 1775 AND\n" +
//                    "  sem_data.semester_period_id=2";
//
//            insertCell(table1, ("Семестрде барлығы"), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//            try {
//                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachSum2, par);
//                if (!tmpList.isEmpty()) {
//                    for (Object o : tmpList) {
//                        Object[] oo = (Object[]) o;
//
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[0]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//                        insertCell(table1, (String.valueOf((BigDecimal)(oo[1]))), Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//                    }
//                }
//            } catch (Exception ex) {
//                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
//            }
//
//            insertCell(table1, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
//
//
//        document.add(paragraph);
//        document.add(table);
//        document.add(new Paragraph("\n"));
//        document.add(new Paragraph("\n"));
//        document.add(new Paragraph("Студенттің қолы .............................................       Күні   «.......»........................... 20......ж.\n", EmployeePdfCreator.getFont(12, Font.NORMAL)));
//        document.add(new Paragraph("\n"));
//        document.add(new Paragraph("\n"));
//
//        document.add(table1);
//        document.add(new Paragraph("\n"));
//        document.add(new Paragraph("\n"));
//        document.add(new Paragraph("Студент    __________  _______________________\n" +
//                "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));
//
//        document.add(new Paragraph("Эдвайзер    __________  _______________________\n" +
//                "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));
//
//        document.add(new Paragraph("Тіркеу офисі     __________  __________________\n" +
//                    "                        (қолы)            (Т.А.Ә.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));


            document.close();
            pdfWriter.close();
        } catch (Exception e) {
            CommonUtils.LOG.error("Unable to create pdf property", e);
            Message.showError(e.toString());
            e.printStackTrace();
        }


    }


    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(2f);
        }
        //add the call to the table
        table.addCell(cell);

    }

    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        PdfPTable table = new PdfPTable(8);
        for (int aw = 0; aw < 16; aw++) {
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
