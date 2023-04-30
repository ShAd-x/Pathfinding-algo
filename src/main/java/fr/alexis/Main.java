package fr.alexis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(Arrays.deepToString(readExcel()));
    }

    private static String[][] readExcel() {
        String[][] values = new String[20][21]; // Tableau pour stocker les valeurs des cellules
        try {
            File file = new File("src/map/Map.xlsx");
            Scanner scanner = new Scanner(file);
            int rowIndex = 0;
            System.out.println("Lecture du fichier Excel");
            System.out.println(scanner.nextLine());
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
}