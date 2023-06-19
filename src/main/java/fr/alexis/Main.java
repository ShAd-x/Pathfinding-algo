package fr.alexis;

import java.io.*;
import java.util.*;

public class Main {

    static Integer[][] map = new Integer[20][20];
    static ArrayList<int[]> strategiques = new ArrayList<>();
    static HashMap<int[], Integer> interets = new HashMap<>();

    // Dataset contenant les routes les plus courtes et le coût de déplacement
    static List<Route> dataset = new ArrayList<>();

    static int[] start = {18, 10}; // Point de départ du personnage (verticalement, horizontalement)

    public static void main(String[] args) {
        getMap();
        getSpeciaux();

        //displayStrategiques();
        //displayInterets();
        //displayMap();

        // Calcul des routes entre tous les points stratégiques et les points d'intérêt
        for (int[] pointStrategique : strategiques) {
            for (Map.Entry<int[], Integer> entry : interets.entrySet()) {
                int[] pointInteret = entry.getKey();
                List<int[]> shortestPath = findShortestPath(pointStrategique, pointInteret);
                int cost = calculateCost(shortestPath) - entry.getValue();
                dataset.add(new Route(shortestPath, cost));
            }
        }
        // Affichage du dataset
        //for (Route route : dataset) {
        //    System.out.println("Route : " + Arrays.deepToString(route.getPath().toArray()) + ", Coût : " + route.getCost());
        //}

        System.out.println("Pour visualiser : le départ est en [18, 10] (forme [y, x]) et donc on commence en 0 jusqu'à 19 au lieu de 1 jusqu'à 20");
        System.out.println("Dataset : Nombre de routes : " + dataset.size());
        List<int[]> highestScoringPath = findHighestScoringPath();
        System.out.println("Chemin le plus rentable : " + Arrays.deepToString(highestScoringPath.toArray()));
        System.out.println("Score : " + calculateScore(highestScoringPath));
    }

    // Recherche du chemin le plus court entre deux points sur la carte
    public static List<int[]> findShortestPath(int[] start, int[] end) {
        int[][] distances = new int[map.length][map[0].length];
        boolean[][] visited = new boolean[map.length][map[0].length];
        int[][][] previous = new int[map.length][map[0].length][2];

        // Initialisation des distances à une valeur maximale et du tableau previous à des valeurs invalides
        for (int i = 0; i < distances.length; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
            for (int j = 0; j < distances[i].length; j++) {
                previous[i][j] = new int[]{-1, -1};
            }
        }

        // Distance du point de départ à lui-même est de 0
        distances[start[0]][start[1]] = 0;

        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(o -> distances[o[0]][o[1]]));
        queue.offer(start);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            if (Arrays.equals(current, end)) {
                // Construction du chemin en remontant les valeurs précédentes
                List<int[]> path = new ArrayList<>();
                int[] temp = end;
                while (!Arrays.equals(temp, start)) {
                    path.add(temp);
                    temp = previous[temp[0]][temp[1]];
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }

            visited[current[0]][current[1]] = true;

            // Recherche des voisins non visités
            List<int[]> neighbors = getNeighbors(current);
            for (int[] neighbor : neighbors) {
                if (!visited[neighbor[0]][neighbor[1]]) {
                    int newDistance = distances[current[0]][current[1]] + map[neighbor[0]][neighbor[1]];
                    if (newDistance < distances[neighbor[0]][neighbor[1]]) {
                        distances[neighbor[0]][neighbor[1]] = newDistance;
                        previous[neighbor[0]][neighbor[1]] = current.clone();
                        queue.offer(neighbor);
                    }
                }
            }
        }
        // Si aucun chemin trouvé, retourne une liste vide
        return new ArrayList<>();
    }

    // Méthode auxiliaire pour obtenir les voisins valides d'une case
    public static List<int[]> getNeighbors(int[] point) {
        int row = point[0];
        int col = point[1];
        List<int[]> neighbors = new ArrayList<>();
        if (row - 1 >= 0 && map[row - 1][col] != -1) {
            neighbors.add(new int[]{row - 1, col});
        }
        if (row + 1 < map.length && map[row + 1][col] != -1) {
            neighbors.add(new int[]{row + 1, col});
        }
        if (col - 1 >= 0 && map[row][col - 1] != -1) {
            neighbors.add(new int[]{row, col - 1});
        }
        if (col + 1 < map[0].length && map[row][col + 1] != -1) {
            neighbors.add(new int[]{row, col + 1});
        }
        return neighbors;
    }

    // Méthode auxiliaire pour vérifier si un point est stratégique
    public static boolean isStrategicPoint(int[] point) {
        for (int[] strategicPoint : strategiques) {
            if (Arrays.equals(strategicPoint, point)) {
                return true;
            }
        }
        return false;
    }

    // Calcul du coût de déplacement pour un chemin donné
    public static int calculateCost(List<int[]> path) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int[] current = path.get(i);
            int[] next = path.get(i + 1);
            cost += map[next[0]][next[1]]; // Ajoute le poids de la case suivante
        }
        return cost;
    }

    public static List<int[]> findHighestScoringPath() {
        List<int[]> strategicPoints = new ArrayList<>();

        strategicPoints.add(strategiques.get(0)); // Point stratégique 1
        strategicPoints.add(strategiques.get(2)); // Point stratégique 3
        strategicPoints.add(strategiques.get(5)); // Point stratégique 6
        strategicPoints.add(strategiques.get(6)); // Point stratégique 7

        List<List<int[]>> permutations = generatePermutations(strategicPoints);

        List<int[]> highestScoringPath = null;
        int highestScore = Integer.MIN_VALUE;

        for (List<int[]> permutation : permutations) {
            List<int[]> path = findPath(start, permutation);

            if (path.isEmpty()) {
                continue; // Skip invalid paths
            }

            int score = calculateScore(path);

            if (score > highestScore) {
                highestScore = score;
                highestScoringPath = path;
            }
        }

        return highestScoringPath;
    }

    private static List<List<int[]>> generatePermutations(List<int[]> points) {
        List<List<int[]>> permutations = new ArrayList<>();
        backtrack(points, new ArrayList<>(), new boolean[points.size()], permutations);
        return permutations;
    }

    private static void backtrack(List<int[]> points, List<int[]> current, boolean[] visited, List<List<int[]>> permutations) {
        if (current.size() == points.size()) {
            permutations.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < points.size(); i++) {
            if (!visited[i]) {
                current.add(points.get(i));
                visited[i] = true;
                backtrack(points, current, visited, permutations);
                visited[i] = false;
                current.remove(current.size() - 1);
            }
        }
    }

    private static List<int[]> findPath(int[] start, List<int[]> points) {
        List<int[]> path = new ArrayList<>();
        int[] current = start;

        for (int[] point : points) {
            List<int[]> shortestPath = findShortestPath(current, point);

            if (shortestPath.isEmpty() || hasBlockedCell(shortestPath)) {
                return new ArrayList<>();
            }

            shortestPath.remove(0); // Remove the first element (current position)
            path.addAll(shortestPath);
            current = point;
        }

        return path;
    }

    private static boolean hasBlockedCell(List<int[]> path) {
        for (int[] cell : path) {
            if (map[cell[0]][cell[1]] == -1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calcule le score d'un chemin
     *
     * @param path Le chemin
     * @return int Le score
     */
    private static int calculateScore(List<int[]> path) {
        int score = 0;

        for (int[] point : path) {
            if (isStrategicPoint(point)) {
                score += 30;
            }

            if (isInterestPoint(point)) {
                score += getInterestValue(point);
            }

            score -= map[point[0]][point[1]];
        }

        return score;
    }

    private static boolean isInterestPoint(int[] point) {
        for (int[] interest : interets.keySet()) {
            if (Arrays.equals(interest, point)) {
                return true;
            }
        }
        return false;
    }

    private static int getInterestValue(int[] point) {
        for (Map.Entry<int[], Integer> entry : interets.entrySet()) {
            if (Arrays.equals(entry.getKey(), point)) {
                return entry.getValue();
            }
        }
        return 0;
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
            // Deux premières lignes, on passe
            if (i <= 2 || i >= 10) {
                i++;
                continue;
            }
            strategiques.add(new int[]{Integer.parseInt(dataList.get(i)[23]) - 1, Integer.parseInt(dataList.get(i)[22]) - 1});
            interets.put(new int[]{Integer.parseInt(dataList.get(i)[26]) - 1, Integer.parseInt(dataList.get(i)[25]) - 1}, Integer.parseInt(dataList.get(i)[27]));
            i++;
        }
    }


    /**
     * Affiche les points stratégiques
     */
    public static void displayStrategiques() {
        strategiques.stream()
            .forEach(tableau -> {
                for (int valeur : tableau) {
                    System.out.print(valeur + " ");
                }
                System.out.println();
            });
    }

    /**
     * Affiche les points d'intérêts
     */
    public static void displayInterets() {
        for (Map.Entry<int[], Integer> entry : interets.entrySet()) {
            int[] key = entry.getKey();
            Integer valeur = entry.getValue();

            System.out.print(Arrays.toString(key) + "=" + valeur + ", ");
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