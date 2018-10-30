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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFormRus {

    private Document document;
    private PdfPTable pdfPTable;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ID studentId;

    public TableFormRus(Document document, ID studentID){
        this.document = document;
        this.studentId=studentID;
        initialize();
    }
    public void initialize() {
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(this.document, byteArrayOutputStream);
            // pdfWriter.open();

            PdfPTable table = new PdfPTable(7);
            document.add(new Paragraph("Дисциплины изучаемые в семестре:", EmployeePdfCreator.getFont(12, Font.BOLD)));

            insertCell(table, "СЕМЕСТР 1", Element.ALIGN_CENTER, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table, "Дисциплины обязательного компонента", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "Код дисциплины", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "Модуль обучения", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "Название дисциплины", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "К-во кредитов", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "ЕСТS", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "Семестр", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, " Ф.И.О. тьютора", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));

            table.setWidthPercentage(100);

            Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);


            String sql = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    "  WHERE   sem_data.semester_period_id=1\n" +
                    "      AND usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL " +
                    "  AND subj.module_id  != 3\n " +
                    "  AND subj.mandatory=TRUE AND \n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;

            Map<Integer, Object> params = new HashMap<>();
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
            insertCell(table, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sql1 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE   sem_data.semester_period_id=1\n" +
                    "      AND usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL\n" +
                    "  AND subj.mandatory=FALSE AND " +
                    "  subj.module_id  != 3 AND \n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;
            Map<Integer, Object> param = new HashMap<>();
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql1, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i] : String.valueOf(oo[i]) : "" , Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table, "Дополнительные дисциплины студента:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sql2 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE  usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL\n" +
                    "AND subj.module_id=3\n" +
                    "  AND usr.locked = FALSE AND usr.id = "+studentId;
            Map<Integer, Object> para = new HashMap<>();
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql2, para));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            String sql3 = "SELECT DISTINCT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
                    "FROM student_subject stu_subj\n" +
                    "   INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  usr.id =  " +studentId+
                    " AND sem_data.semester_period_id=1";

            insertCell(table, ("Семестрде барлығы"), Element.ALIGN_LEFT, 3,  EmployeePdfCreator.getFont(12, Font.NORMAL));

            Map<Integer, Object> par = new HashMap<>();
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql3, par));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 2;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));
            insertCell(table, "СЕМЕСТР 2", Element.ALIGN_CENTER, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            insertCell(table, "     Дисциплины обязательного компонента:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));
            String sqlSem2 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE   sem_data.semester_period_id=2\n" +
                    "      AND usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL " +
                    "  AND subj.module_id  != 3\n " +
                    "  AND subj.mandatory=TRUE AND\n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlSem2, params));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
            insertCell(table, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlAdd = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE   sem_data.semester_period_id=2\n" +
                    "      AND usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL\n" +
                    "  AND subj.mandatory=FALSE AND " +
                    "  subj.module_id  != 3 AND \n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlAdd, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table, "Дополнительные дисциплины студента:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlStud = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  module.module_short_name moduleType,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  sem.semester_name   ,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE  usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL\n" +
                    "AND subj.module_id=3\n" +
                    "  AND usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlStud, para));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 7;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            String sqlSum = "SELECT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  usr.id = " +studentId+
                    "  AND sem_data.semester_period_id=2";

            insertCell(table, (" Всего в семестре:"), Element.ALIGN_LEFT, 3,  EmployeePdfCreator.getFont(12, Font.NORMAL));

            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlSum, par));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 2;i++){
                            insertCell(table,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));












            PdfPTable table1 = new PdfPTable(6);
            insertCell(table1, "Название модуля", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "Полное название дисциплины", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "Кол-во кредитов", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "ECTS", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "Ф.И.О. преподавателя", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "Форма контроля", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "СЕМЕСТР 1", Element.ALIGN_CENTER, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "Дисциплины обязательного компонента:", Element.ALIGN_LEFT, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
            table1.setWidthPercentage(100);

            String sqlTeacherSem1 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  sem_data.semester_period_id=1 AND\n" +
                    "       usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL   AND subj.module_id  != 3\n" +
                    "      AND subj.mandatory=TRUE AND\n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeacherSem1, params));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
            insertCell(table1, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlTeaAdd = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  sem_data.semester_period_id=1\n" +
                    "  AND usr.deleted = FALSE AND\n" +
                    "  subj.subject_cycle_id IS NOT NULL\n" +
                    "  AND subj.mandatory=FALSE AND\n" +
                    "  subj.module_id  != 3 AND\n" +
                    "  usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeaAdd, param));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table1, "Дополнительные дисциплины студента:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlTeachAdd = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  usr.deleted = FALSE AND\n" +
                    "  subj.subject_cycle_id IS NOT NULL AND subj.module_id=3\n" +
                    "AND\n" +
                    "  usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachAdd, para));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            String sqlTeachSum = "SELECT DISTINCT  sum( credit.credit) credit, sum( ects.ects) ects\n" +
                    "FROM student_subject stu_subj\n" +
                    "   INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n" +
                    " WHERE\n" +
                    "  usr.id = " +studentId+
                    " AND sem_data.semester_period_id=1";

            insertCell(table1, ("Итого кредитов за семестр"), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));

            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachSum, par));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 2;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table1, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));











            insertCell(table1, "СЕМЕСТР 2", Element.ALIGN_CENTER, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));
            insertCell(table1, "           Дисциплины обязательного компонента:", Element.ALIGN_LEFT, 6,  EmployeePdfCreator.getFont(12, Font.BOLD));


            String sqlTeacherSem2 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n " +
                    " WHERE\n" +
                    "  sem_data.semester_period_id=2 AND\n" +
                    "       usr.deleted = FALSE AND\n" +
                    "      subj.subject_cycle_id IS NOT NULL   AND subj.module_id  != 3\n" +
                    "      AND subj.mandatory=TRUE AND\n" +
                    "      usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeacherSem2, params));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }
            insertCell(table1, "Дисциплины  компонента по выбору:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlTeaAdd2 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    "FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n " +
                    " WHERE\n" +
                    "  sem_data.semester_period_id=2\n" +
                    "  AND usr.deleted = FALSE AND\n" +
                    "  subj.subject_cycle_id IS NOT NULL\n" +
                    "  AND subj.mandatory=FALSE AND\n" +
                    "  subj.module_id  != 3 AND\n" +
                    "  usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeaAdd2, param);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table1, "Дополнительные дисциплины студента:", Element.ALIGN_LEFT, 7,  EmployeePdfCreator.getFont(12, Font.BOLD));

            String sqlTeachAdd2 = "SELECT DISTINCT\n" +
                    "  s4.code,\n" +
                    "  subj.name_RU                                                                   subjectName,\n" +
                    "  credit.credit,\n" +
                    "  ects.ects,\n" +
                    "  trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' ' || coalesce(u.MIDDLE_NAME, '')) FIO,\n" +
                    "  control.type_name                                                              examType\n" +
                    " FROM student_subject stu_subj\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id\n " +
                    " WHERE\n" +
                    "  usr.deleted = FALSE AND\n" +
                    "  subj.subject_cycle_id IS NOT NULL AND subj.module_id=3\n" +
                    "AND\n" +
                    "  usr.locked = FALSE AND usr.id = "+studentId;
            try {
                java.util.List<Object> tmpList = new ArrayList<>();
                tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachAdd2, para));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 6;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            String sqlTeachSum2 = "SELECT DISTINCT sum( credit.credit) credit, sum( ects.ects) ects\n" +
                    "FROM student_subject stu_subj\n" +
                    "   INNER JOIN student_education stu_edu ON stu_subj.student_id = stu_edu.id\n" +
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
                    "  INNER JOIN student_teacher_subject s3 ON s3.teacher_subject_id = ts.id\n" +
                    "  LEFT JOIN pair_subject s4 ON subj.id = s4.subject_id \n" +
                    "WHERE\n" +
                    "  usr.id = " +studentId+
                    "  AND sem_data.semester_period_id=2";

            insertCell(table1, ("Итого кредитов за семестр"), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));

            try {
                List<Object> tmpList = new ArrayList<>();
                        tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sqlTeachSum2, par));
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        for(int i = 0 ; i < 2;i++){
                            insertCell(table1,oo[i]!=null ? oo[i] instanceof String ? (String)oo[i]: String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1,  EmployeePdfCreator.getFont(12, Font.NORMAL));
                        }

                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
            }

            insertCell(table1, (" "), Element.ALIGN_LEFT, 2,  EmployeePdfCreator.getFont(12, Font.NORMAL));


            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));


            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));



            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));


            try{
                document.add( table );
            }catch (Exception e){
                CommonUtils.LOG.error(e.getMessage());
                document.open();
            }




            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));


            try{
                document.add(table1);
            }catch (Exception e){
                CommonUtils.LOG.error(e.getMessage());
                document.open();
            }

            document.add(new Paragraph("\n "));
            document.add(new Paragraph("\n "));
            document.add(new Paragraph("Студент    __________  _______________________\n" +
                    "                         (подпись)              (Ф.И.О.)         \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Эдвайзер    __________  _______________________\n" +
                    "                        (подпись)            (Ф.И.О.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            document.add(new Paragraph("Офис регистратор     __________  __________________\n" +
                    "                               (подпись)            (Ф.И.О.)            \n", EmployeePdfCreator.getFont(12, Font.BOLD)));

            try{
                pdfWriter.close();
            }catch (Exception e){
                CommonUtils.LOG.error(e.getMessage());
            }


        }catch (Exception e){
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
