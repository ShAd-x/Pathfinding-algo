package fr.alexis;

import java.io.*;
import java.util.*;

public class Main {

    static Integer[][] map = new Integer[20][20];
    static ArrayList<int[][]> strategiques = new ArrayList<>();
    static ArrayList<int[]> interets = new ArrayList<>();


    public static void main(String[] args) {
        getMap();
        displayMap();
    }

    /**
     * Affiche la map
     */
    public static void displayMap() {
        Arrays.stream(map).map(Arrays::toString).forEach(System.out::println);
    }

    /**
     * Récupère la map depuis le fichier CSV
     */
    public static void getMap() {
        File csvFile = new File("src/map/Map.csv");
        List<String[]> dataList = null;
        try {
            dataList = readCsv(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Parcourir les données
        int i = 0;
        for (String[] data : dataList) {
            // Si c'est la première ligne, on passe
            if (i == 0) {
                i++;
                continue;
            }
            for (int j = 1; j < 21; j++) {
                map[i - 1][j - 1] = Integer.valueOf(dataList.get(i)[j]);
            }
            i++;
        }
    }

    /**
     * Récupère les points spéciaux
     */
    public static void getSpeciaux() {
        File csvFile = new File("src/map/Map.csv");
        List<String[]> dataList = null;
        try {
            dataList = readCsv(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Parcourir les données
        int i = 0;
        for (String[] data : dataList) {
            // Si c'est la première ligne, on passe
            if (i <= 0) {
                i++;
                continue;
            }
            for (int j = 1; j < 21; j++) {
                map[i - 1][j - 1] = Integer.valueOf(dataList.get(i)[j]);
            }
            i++;
        }
    }


    /**
     * Récupère les lignes depuis le fichier CSV
     *
     * @param csvFile
     * @return List<String[]>
     */
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