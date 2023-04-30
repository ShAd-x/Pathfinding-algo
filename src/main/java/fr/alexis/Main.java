package fr.alexis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        printExcel();
        //System.out.println(Arrays.deepToString(readExcel()));
    }

    private static String[][] readExcel() {
        String[][] values = new String[20][21]; // Tableau pour stocker les valeurs des cellules
        try {
            File file = new File("src/map/Map.xlsx");
            Scanner scanner = new Scanner(file);
            int rowIndex = 0;
            System.out.println(scanner.hasNextLine());
            while (scanner.hasNextLine()) {
                System.out.println(rowIndex);
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue; // Ignorer les lignes vides
                }
                String[] cells = line.split("\t");
                if (cells.length != 21) {
                    continue; // Ignorer les lignes qui ne contiennent pas 21 cellules
                }
                System.arraycopy(cells, 0, values[rowIndex], 0, cells.length);
                rowIndex++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void printExcel() {
        File file = new File("src/map/Map.xlsx");
        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            break;
                        case NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t");
                            break;
                        case BOOLEAN:
                            System.out.print(cell.getBooleanCellValue() + "\t");
                            break;
                        default:
                            System.out.print("\t");
                            break;
                    }
                }
                System.out.println();
            }
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}