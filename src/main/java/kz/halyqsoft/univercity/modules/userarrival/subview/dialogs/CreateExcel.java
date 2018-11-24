package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateExcel {

    private List<String> tableHeader;
    private List<List<String>> tableBody;
    private String title;
    private String fileName = "Writesheet.xlsx";
    private File mainFile;
    private String header = "Writesheet.xlsx";
    private byte[] mainByte;
    public CreateExcel(List<String> tableHeader, List<List<String>> tableBody,String title){
        this.tableBody = tableBody;
        this.tableHeader = tableHeader;
        this.title = title;
    }
    public void createEXCEL() throws Exception{

        XSSFWorkbook workbook = new XSSFWorkbook();

        if(title!=null){
            fileName = title + ".xlsx";
        }
        mainFile = new File(fileName);

        FileOutputStream out = new FileOutputStream( mainFile);
        XSSFSheet sheet = workbook.createSheet();
        for(int i = 0;i<=tableBody.size();i++){
            XSSFRow row = sheet.createRow(i);
            for(int j=0;j<tableHeader.size();j++)
                row.createCell(j);
        }
        workbook.write(out);
        out.close();//end creation of Excel file

        // I open Writesheet.xlsx file and write the data on it
        InputStream inp = new FileInputStream( new File(fileName));
        workbook = new XSSFWorkbook(inp);

        getList(workbook);
        out = new FileOutputStream( new File(fileName));
        workbook.write(out);

        out.close();
        inp.close();

        mainByte  = Files.readAllBytes(mainFile.toPath());
        mainFile.delete();
        System.out.println(fileName + " written successfully" );
    }

    private void getList(XSSFWorkbook workbook){
        Map<Integer, List<String>> hashmap = new HashMap<Integer , List<String>>();
        int i = 0;
        hashmap.put(i, tableHeader);
        i++;

        for( int j = 0 ; j < (tableBody.size()); j++ ){
            hashmap.put(j + i, tableBody.get(j));
        }
        System.out.println("hashmap : "+hashmap);
        Set<Integer> keyset = hashmap.keySet();
        int rownum = 0;
        XSSFSheet sheet = workbook.getSheetAt(0);
        rownum = 0;
        for(Integer key : keyset){
            List<String> nameList = hashmap.get(key);

            XSSFRow row = sheet.getRow(rownum);
            int cellnum = 0;
            for(String s : nameList){
                Cell cell = row.getCell(cellnum);
                if(null!=cell){
                    cell.setCellValue(s);
                }
                cellnum++;
            }
            rownum++;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getMainByte() {
        return mainByte;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTableHeader() {
        return tableHeader;
    }

    public List<List<String>> getTableBody() {
        return tableBody;
    }
}
