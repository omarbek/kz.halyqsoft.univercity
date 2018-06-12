package kz.halyqsoft.univercity.utils.excel;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Mar 16, 2016 11:37:32 AM
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    public static Map<ExcelStyles, CellStyle> createStyles(Workbook wb) {
        Map<ExcelStyles, CellStyle> styleMap = new HashMap<>();

        Font font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.TITLE, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setWrapText(true);
        style.setFont(font);
        styleMap.put(ExcelStyles.TITLE_WITH_BORDER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setRotation((short) 90);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        styleMap.put(ExcelStyles.TITLE_VERTICAL_WITH_BORDER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setRotation((short) 90);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setWrapText(true);
        style.setFont(font);
        styleMap.put(ExcelStyles.TITLE_VERTICAL_WITH_BORDER_WRAP, style);

        setColorStyle(wb, styleMap, IndexedColors.LIGHT_GREEN.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_LESS_GREEN);
        setColorStyle(wb, styleMap, IndexedColors.PINK.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_PURPLE);
        setColorStyle(wb, styleMap, IndexedColors.AQUA.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_BLUE);
        setColorStyle(wb, styleMap, IndexedColors.BROWN.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_BROWN);
        setColorStyle(wb, styleMap, IndexedColors.ORANGE.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_ORANGE);
        setColorStyle(wb, styleMap, IndexedColors.VIOLET.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_VIOLET);
        setColorStyle(wb, styleMap, IndexedColors.LIGHT_BLUE.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_LESS_BLUE);
        setColorStyle(wb, styleMap, IndexedColors.YELLOW.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_YELLOW);
        setColorStyle(wb, styleMap, IndexedColors.GREEN.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_GREEN);
        setColorStyle(wb, styleMap, IndexedColors.LIGHT_ORANGE.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_LESS_ORANGE);
        setColorStyle(wb, styleMap, IndexedColors.LEMON_CHIFFON.getIndex(), ExcelStyles.TITLE_VERTICAL_WITH_BORDER_LEMON_CHIFFON);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setItalic(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.ITALIC, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.TITLE_RIGHT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.SUBTITLE_CENTER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.SUBTITLE_CENTER_WITH_BORDER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(font);
        styleMap.put(ExcelStyles.SUBTITLE_CENTER_WITH_BORDER_WRAP, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.SUBTITLE_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.SUBTITLE_LEFT_WITH_BORDER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(BorderStyle.THIN);
        //		style.setBorderBottom(BorderStyle.THIN);
        //		style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_BORDER_THIN, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderTop(CellStyle.BORDER_THICK);
        //		style.setBorderRight(CellStyle.BORDER_THICK);
        //		style.setBorderBottom(CellStyle.BORDER_THICK);
        //		style.setBorderLeft(CellStyle.BORDER_THICK);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_RIGHT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_CENTER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_CENTER_WITH_BORDER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderTop(CellStyle.BORDER_THICK);
        //		style.setBorderRight(CellStyle.BORDER_THICK);
        //		style.setBorderBottom(CellStyle.BORDER_THICK);
        //		style.setBorderLeft(CellStyle.BORDER_THICK);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setRotation((short) 90);
        //		style.setBorderTop(CellStyle.BORDER_THICK);
        //		style.setBorderRight(CellStyle.BORDER_THICK);
        //		style.setBorderBottom(CellStyle.BORDER_THICK);
        //		style.setBorderLeft(CellStyle.BORDER_THICK);
        style.setFont(font);
        styleMap.put(ExcelStyles.HEADER_VERTICAL, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(BorderStyle.THIN);
        //		style.setBorderBottom(BorderStyle.THIN);
        //		style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        styleMap.put(ExcelStyles.CONTENT_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(BorderStyle.THIN);
        //		style.setBorderBottom(BorderStyle.THIN);
        //		style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        styleMap.put(ExcelStyles.CONTENT_CENTER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(BorderStyle.THIN);
        //		style.setBorderBottom(BorderStyle.THIN);
        //		style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        style.setWrapText(true);
        styleMap.put(ExcelStyles.CONTENT_CENTER_WRAP, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(CellStyle.BORDER_THICK);
        //		style.setBorderBottom(CellStyle.BORDER_THICK);
        //		style.setBorderLeft(CellStyle.BORDER_THICK);
        style.setFont(font);
        styleMap.put(ExcelStyles.FOOTER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(CellStyle.BORDER_THICK);
        //		style.setBorderBottom(CellStyle.BORDER_THICK);
        //		style.setBorderLeft(CellStyle.BORDER_THICK);
        style.setFont(font);
        styleMap.put(ExcelStyles.FOOTER_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.TRANSCRIPT_STUDENT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 6);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        styleMap.put(ExcelStyles.TRANSCRIPT_HEADER_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 5);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        styleMap.put(ExcelStyles.TRANSCRIPT_BOLD_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 5);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        styleMap.put(ExcelStyles.TRANSCRIPT_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 7);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        styleMap.put(ExcelStyles.ORDER_BOLD_LEFT, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        styleMap.put(ExcelStyles.ORDER_BOLD_CENTER, style);

        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //		style.setBorderRight(BorderStyle.THIN);
        //		style.setBorderBottom(BorderStyle.THIN);
        //		style.setBorderLeft(BorderStyle.THIN);
        //		style.setBorderTop(BorderStyle.THIN);
        style.setFont(font);
        styleMap.put(ExcelStyles.ORDER_CENTER, style);

        return styleMap;
    }

    private static void setColorStyle(Workbook wb, Map<ExcelStyles, CellStyle> styleMap, short index, ExcelStyles excelStyle) {
        Font font;
        CellStyle style;
        font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setRotation((short) 90);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFillForegroundColor(index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        styleMap.put(excelStyle, style);
    }
}
