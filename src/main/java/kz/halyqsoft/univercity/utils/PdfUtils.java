package kz.halyqsoft.univercity.utils;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.vaadin.server.VaadinService;

import static kz.halyqsoft.univercity.utils.EmployeePdfCreator.getFont;

/**
 * @author Assylkhan
 * on 06.11.2018
 * @project kz.halyqsoft.univercity
 */
public class PdfUtils {

    private static Font mainFont = getFont(12, Font.BOLD);

    public static PdfPTable getTable(int columnNums){
        return  new PdfPTable(columnNums);
    }

    public static void insertCell(PdfPTable table, String text, int align, int colspan) {
        insertCell(table,text,align,colspan,mainFont);
    }

    public static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);
    }



    public static void setFont(int fontSize, int font) {
        String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
        BaseFont timesNewRoman = null;
        try {
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFont = new Font(timesNewRoman, fontSize, font);
    }

    public static Font getMainFont() {
        return mainFont;
    }
}
