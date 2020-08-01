package cn.qulei;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author TaoistQu
 * @data 2020-08-01 16:48
 */
public class Test {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        System.out.println("开始读取数据！！！！");
        // Use an InputStream, needs more memory
        OPCPackage pkg = OPCPackage.open(new File("E:\\test.xlsx"));
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        XSSFSheet sheet = wb.getSheetAt(0);
        int outRowNum = 0;
        //写出数据
        XSSFWorkbook out = new XSSFWorkbook();
        XSSFSheet outSheet = out.createSheet("井深度表");
        XSSFRow outRow = outSheet.createRow(outRowNum);
        XSSFCell outCell0 = outRow.createCell(0);
        XSSFCell outCell1 = outRow.createCell(1);

        outCell0.setCellValue("上界面");
        outCell1.setCellValue("下界面");
        if (null == sheet) {
            return;
        }
        double start;
        double end = 0.0;
        double value, nextVale;
        double step;
        start = sheet.getRow(0).getCell(0).getNumericCellValue();
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum() - 1; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            XSSFRow rowNext = sheet.getRow(rowNum + 1);

            if (null == row) {
                return;
            }

            XSSFCell cell = row.getCell(0);
            XSSFCell cellNext = rowNext.getCell(0);
            if (null == cell || null == cellNext) {
                return;
            }
            value = cell.getNumericCellValue();
            nextVale = cellNext.getNumericCellValue();
            step = nextVale - value;
            if (step != 0.125) {
                end = value;
                writeVale(++outRowNum, outSheet, start, end);
                start = nextVale;
            }

        }


        FileOutputStream outputStream = new FileOutputStream("E:\\井界面数据.xlsx");
        out.write(outputStream);
        out.close();
        outputStream.close();
        wb.close();
        System.out.println("读取数据完成！！！");

    }

    private static void writeVale(int outRowNum, XSSFSheet outSheet, double start, double end) {
        XSSFRow outRow = outSheet.createRow(outRowNum);
        XSSFCell outCell0 = outRow.createCell(0);
        XSSFCell outCell1 = outRow.createCell(1);
        outCell0.setCellValue(start);
        outCell1.setCellValue(end);
    }
}

