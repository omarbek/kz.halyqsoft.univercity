package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.AbstractSelectWidget;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.entity.Entity;

import java.io.*;
import java.util.*;

public class PrintDialog extends AbstractDialog{

    private List<String> tableHeader;
    private List<List<String>> tableBody;

    private String title;
    private HorizontalLayout buttonPanel;
    private Button pdfBtn;
    private Button excelBtn;
    private CreateExcel createExcel = null;
    private Font currentFont;
    public PrintDialog(List<String> tableHeader, List<List<String>> tableBody,String title ,String fileName){
        currentFont = EmployeePdfCreator.getFont(12, Font.BOLD);
        print(tableHeader, tableBody,title ,fileName);
    }

    private void print(List<String> tableHeader, List<List<String>> tableBody,String title ,String fileName){
        this.tableBody = tableBody;
        this.tableHeader = tableHeader;

        setImmediate(true);
        this.title = title;
        try{
            fileName = fileName.replaceAll("\\s","_");
            createExcel = new CreateExcel(tableHeader,tableBody ,fileName);
            createExcel.createEXCEL();
        }catch (Exception e){
            e.printStackTrace();
        }

        buttonPanel = CommonUtils.createButtonPanel();

        pdfBtn = new Button("PDF");
        pdfBtn.setImmediate(true);

        FileDownloader fileDownloaderr = null;
        try{
            fileDownloaderr = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(xlsxToPdf(), createExcel.getTitle()+".pdf"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(fileDownloaderr!=null){
            fileDownloaderr.extend(pdfBtn);
        }


        excelBtn = new Button("EXCEL");
        excelBtn.setImmediate(true);

        FileDownloader fileDownloader = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(createExcel.getMainByte(), createExcel.getFileName()));
        fileDownloader.extend(excelBtn);

        buttonPanel.addComponent(pdfBtn);
        buttonPanel.addComponent(excelBtn);
        buttonPanel.setCaptionAsHtml(true);
        buttonPanel.setCaption(getUILocaleUtil().getCaption("download")+"   ");
        getContent().addComponent(buttonPanel);

        AbstractWebUI.getInstance().addWindow(this);
    }

    public PrintDialog(List<String> tableHeader, List<List<String>> tableBody,String title ,String fileName, Font font){
        setCurrentFont(font);
        print(tableHeader, tableBody,title ,fileName);
    }

    private byte[] xlsxToPdf() throws Exception{
        ByteArrayInputStream bais =new ByteArrayInputStream(createExcel.getMainByte());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        XSSFWorkbook my_xls_workbook = new XSSFWorkbook(bais);
        XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        Iterator<Row> rowIterator = my_worksheet.iterator();
        Document iText_xls_2_pdf = new Document();
            PdfWriter.getInstance(iText_xls_2_pdf, byteArrayOutputStream);
            iText_xls_2_pdf.open();
            PdfPTable my_table = new PdfPTable(createExcel.getTableHeader().size());
            PdfPCell table_cell;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {

                        case STRING:

                            Paragraph paragraph = new Paragraph(cell.getStringCellValue(), currentFont);
                            table_cell = new PdfPCell(paragraph);

                                //table_cell=new PdfPCell(new Phrase(cell.getStringCellValue()));
                                my_table.addCell(table_cell);
                                break;

                    }
                }

            }
            iText_xls_2_pdf.add(my_table);
            iText_xls_2_pdf.close();

            bais.close();

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPTable xlsxToPdfTable() throws Exception{
        ByteArrayInputStream bais =new ByteArrayInputStream(createExcel.getMainByte());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        XSSFWorkbook my_xls_workbook = new XSSFWorkbook(bais);
        XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        Iterator<Row> rowIterator = my_worksheet.iterator();
        Document iText_xls_2_pdf = new Document();
        PdfWriter.getInstance(iText_xls_2_pdf, byteArrayOutputStream);
        iText_xls_2_pdf.open();
        PdfPTable my_table = new PdfPTable(createExcel.getTableHeader().size());
        PdfPCell table_cell;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellTypeEnum()) {

                    case STRING:

                        Paragraph paragraph = new Paragraph(cell.getStringCellValue(), currentFont);
                        table_cell = new PdfPCell(paragraph);

                        //table_cell=new PdfPCell(new Phrase(cell.getStringCellValue()));
                        my_table.addCell(table_cell);
                        break;

                }
            }

        }
        iText_xls_2_pdf.add(my_table);
        iText_xls_2_pdf.close();

        bais.close();

        return my_table;
    }

    public Button getPdfBtn() {
        return pdfBtn;
    }

    public Button getExcelBtn() {
        return excelBtn;
    }

    public void setCurrentFont(Font currentFont) {
        this.currentFont = currentFont;
    }

    @Override
    protected String createTitle() {
        return null;
    }
}
