package kz.halyqsoft.univercity.modules.dorm;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.utility.DateUtils;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.locale.UILocaleUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Dinassil Omarbek
 * @created May 30, 2017 11:08:41 AM
 */
public class WarrantAndSkipOfStudents implements StreamSource {

    private static final long serialVersionUID = 1L;
    private ID studentId;
    private byte[] userPhotoBytes;
    private Image userPhoto = null;
    private STUDENT student;
    private Font font = null;
    private BaseFont timesNewRoman;
    private Font boldFont = null;
    private final int fontSize = 11;
    private Object[] o = null;
    private String faculty;
    private String year;

    @Override
    public InputStream getStream() {
        ByteArrayOutputStream baos = null;
        try {
            init();

            Document document = new Document();
            document.setMargins(-20, -20, 20, 20);
            baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            int tableColspan = 4;
            PdfPTable orderTable = new PdfPTable(tableColspan);
            PdfPCell orderCell;

            Image kbtuI = Image.getInstance(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/img/KBTU.gif");
            orderCell = new PdfPCell(kbtuI);
            orderCell.setBorder(PdfPCell.NO_BORDER);
            orderTable.addCell(orderCell);
            addSpaceCell(orderTable, tableColspan);
            orderCell = new PdfPCell(new Phrase(" "));
            addCell(getUILocaleUtil().getCaption("dorm.order"), orderTable, tableColspan, boldFont);

            String fio = student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() == null ? "" : student.getMiddleName());

            setTableForm(getUILocaleUtil().getCaption("student.fio"), fio, orderTable, 2, 4);
            setTableForm(getUILocaleUtil().getCaption("faculty"), faculty, orderTable, 2, 4);
            setTableForm(getUILocaleUtil().getCaption("study.year.1"), year, orderTable, 2, 4);
            setTableForm(getUILocaleUtil().getCaption("dorms.and.rooms"), (String) o[3] + ", " + getUILocaleUtil().getCaption("dorm.room") + " №" + (String) o[2], orderTable, 2, 4);
            setTableForm(getUILocaleUtil().getCaption("cost"), ((BigDecimal) o[0] == null ? 0.0 : ((BigDecimal) o[0]).doubleValue()) + "", orderTable, 2, 4);
            setTableForm(getUILocaleUtil().getCaption("date") + " " + getUILocaleUtil().getCaption("dorm.in.or.move"), new SimpleDateFormat(DateUtils.SHORT_FORMAT).format((Date) o[1]),//
                    orderTable, 2, 4);

            orderCell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("dorm.director.support") + "______________", font));
            orderCell.setColspan(4);
            orderCell.setBorder(PdfPCell.NO_BORDER);
            orderTable.addCell(orderCell);

            addSpaceCell(orderTable, tableColspan);

            tableColspan = 5;
            PdfPTable skipTable = new PdfPTable(tableColspan);
            PdfPCell cell2;

            addCell(getUILocaleUtil().getCaption("pass.to.enter"), skipTable, tableColspan, boldFont);
            addCell(fio, skipTable, tableColspan, boldFont);

            cell2 = new PdfPCell(userPhoto);
            cell2.setRowspan(7);
            cell2.setBorder(PdfPCell.NO_BORDER);
            skipTable.addCell(cell2);

            addCell(faculty + ", " + year + " " + getUILocaleUtil().getCaption("course"), skipTable, 4, font);
            addCell((String) o[3] + ", " + getUILocaleUtil().getCaption("dorm.room") + " №" + (String) o[2], skipTable, 4, font);
            addCell(getUILocaleUtil().getCaption("date") + " " + getUILocaleUtil().getCaption("dorm.in.or.move") + " " + new SimpleDateFormat(DateUtils.SHORT_FORMAT).format((Date) o[1]),//
                    skipTable, 4, font);
            addSpaceCell(skipTable, tableColspan);

            cell2 = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("dorm.pass.validity") + "______________", font));
            cell2.setColspan(4);
            cell2.setBorder(PdfPCell.NO_BORDER);
            skipTable.addCell(cell2);
            addSpaceCell(skipTable, tableColspan);

            cell2 = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("dorm.director.support") + "______________", font));
            cell2.setColspan(4);
            cell2.setBorder(PdfPCell.NO_BORDER);
            skipTable.addCell(cell2);
            addSpaceCell(skipTable, tableColspan);

            document.add(orderTable);
            document.add(orderTable);
            document.add(skipTable);
            document.add(skipTable);
            document.close();

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to download pdf file", ex);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ex) {

                }
            }
        }
        return null;
    }

    private void addCell(String a, PdfPTable table, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void addSpaceCell(PdfPTable table, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(colspan);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void setTableForm(String a, String b, PdfPTable table, int colspan1, int colspan2) {

        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan1);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(b, font));
        cell.setColspan(colspan2);

        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void init() {
        try {
            student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT.class, studentId);
            try {
                String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
                timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                font = new Font(timesNewRoman, fontSize);
                boldFont = new Font(timesNewRoman, fontSize, Font.BOLD);
                if (userPhotoBytes != null)
                    userPhoto = Image.getInstance(userPhotoBytes);
                else if (student.getSex().toString().equals("Женский"))
                    userPhoto = Image.getInstance(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/img/female.png");
                else
                    userPhoto = Image.getInstance(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/img/male.png");
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student", ex);
        }
    }

    public WarrantAndSkipOfStudents() {
    }

    public void setStudent(ID studentId) {
        this.studentId = studentId;
    }

    public void setUserPhotoBytes(byte[] userPhotoBytes) {
        this.userPhotoBytes = userPhotoBytes;
    }

    protected static UILocaleUtil getUILocaleUtil() {
        return AbstractWebUI.getInstance().getUILocaleUtil();
    }

    public void setObjectAndFacultyAndYear(Object[] o, String faculty, String year) {
        this.o = o;
        this.faculty = faculty;
        this.year = year;
    }
}
