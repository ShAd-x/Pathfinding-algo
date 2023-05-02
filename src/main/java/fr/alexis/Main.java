package fr.alexis;

import java.io.*;
import java.util.*;

public class Main {

    static Integer[][] map = new Integer[20][20];
    static Integer[][] strategiques = new Integer[10][10];
    static Integer[][] interets = new Integer[10][10];


    public static void main(String[] args) {
        getMap();
//        displayMap();
    }

    public static void displayMap() {
        Arrays.stream(map).map(Arrays::toString).forEach(System.out::println);
    }

    public static void getMap() {
        File csvFile = new File("src/map/Map.csv");
        List<String[]> dataList = null;
        try {
            dataList = readCsv(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // parcourir les données
        int i = 0;
        for (String[] data : dataList) {
            for (int j = 0; j < data.length; j++) {
                // On récupère les points
//                if (i >= 3 && j >= 22 && data[j] != null && !data[j].isEmpty()) {
//                    strategiques[i-3] = new Integer[] {Integer.valueOf(data[22]), Integer.valueOf(data[23])};
//                    interets[Integer.parseInt(data[25])][Integer.parseInt(data[26])] = Integer.valueOf(data[27]);
//                    System.out.println("Stratégique : " + Arrays.toString(strategiques[i-3]));
//                    System.out.println("Intérêt : " + Arrays.deepToString(interets));
//                }
                // On ne prend pas en compte la première ligne et la première colonne
                // On ne prend que les bonnes valeurs
                if (i == 0 || i > 21 || j == 0 || j >= 21) continue;
                map[i-1][j-1] = Integer.valueOf(data[j]);
            }
            i++;
        }
    }

    public static List<String[]> readCsv(File csvFile) throws FileNotFoundException {
        List<String[]> dataList = new ArrayList<>();

        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                dataList.add(data);
            }
        }

        return dataList;
    }
}