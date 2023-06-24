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

    /**
     * Recherche du chemin le plus court entre deux points sur la carte
     *
     * @param start
     * @param end
     * @return Liste des points du chemin le plus court
     */
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

    /**
     * Obtient les voisins valides d'un point donné.
     *
     * @param point Le point dont on souhaite obtenir les voisins.
     * @return Une liste des voisins valides du point.
     */
    public static List<int[]> getNeighbors(int[] point) {
        int row = point[0];
        int col = point[1];
        List<int[]> neighbors = new ArrayList<>();

        // Vérifie si le voisin du haut est valide
        if (row - 1 >= 0 && map[row - 1][col] != -1) {
            neighbors.add(new int[]{row - 1, col});
        }

        // Vérifie si le voisin du bas est valide
        if (row + 1 < map.length && map[row + 1][col] != -1) {
            neighbors.add(new int[]{row + 1, col});
        }

        // Vérifie si le voisin de gauche est valide
        if (col - 1 >= 0 && map[row][col - 1] != -1) {
            neighbors.add(new int[]{row, col - 1});
        }

        // Vérifie si le voisin de droite est valide
        if (col + 1 < map[0].length && map[row][col + 1] != -1) {
            neighbors.add(new int[]{row, col + 1});
        }

        return neighbors;
    }

    /**
     * Vérifie si un point donné est un point stratégique.
     *
     * @param point Le point à vérifier.
     * @return true si le point est un point stratégique, false sinon.
     */
    public static boolean isStrategicPoint(int[] point) {
        for (int[] strategicPoint : strategiques) {
            if (Arrays.equals(strategicPoint, point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcule le coût total d'un chemin en additionnant les poids de chaque déplacement.
     *
     * @param path Le chemin pour lequel calculer le coût.
     * @return Le coût total du chemin.
     */
    public static int calculateCost(List<int[]> path) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int[] current = path.get(i);
            int[] next = path.get(i + 1);
            cost += map[next[0]][next[1]]; // Ajoute le poids de la case suivante
        }
        return cost;
    }

    /**
     * Trouve le chemin rapportant le plus de points en se rendant aux points stratégiques spécifiés.
     *
     * @return Le chemin rapportant le plus de points.
     */
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

    /**
     * Génère toutes les permutations possibles des points donnés.
     *
     * @param points La liste des points à permuter.
     * @return La liste de toutes les permutations générées.
     */
    private static List<List<int[]>> generatePermutations(List<int[]> points) {
        List<List<int[]>> permutations = new ArrayList<>();
        backtrack(points, new ArrayList<>(), new boolean[points.size()], permutations);
        return permutations;
    }

    /**
     * Effectue un parcours récursif pour générer toutes les permutations des points donnés.
     *
     * @param points       La liste des points à permuter.
     * @param current      La liste courante des points sélectionnés dans la permutation en cours.
     * @param visited      Un tableau de booléens indiquant si chaque point a été visité ou non.
     * @param permutations La liste qui stocke toutes les permutations générées.
     */
    private static void backtrack(List<int[]> points, List<int[]> current, boolean[] visited, List<List<int[]>> permutations) {
        // Si tous les points ont été sélectionnés, ajoute la permutation courante à la liste des permutations.
        if (current.size() == points.size()) {
            permutations.add(new ArrayList<>(current));
            return;
        }

        // Parcours de tous les points non visités pour générer les permutations.
        for (int i = 0; i < points.size(); i++) {
            if (!visited[i]) {
                // Sélectionne le point i et marque le point comme visité.
                current.add(points.get(i));
                visited[i] = true;

                // Appel récursif pour continuer à générer les permutations.
                backtrack(points, current, visited, permutations);

                // Désélectionne le point i et le marque comme non visité avant de passer au point suivant.
                visited[i] = false;
                current.remove(current.size() - 1);
            }
        }
    }

    /**
     * Trouve un chemin reliant les points dans l'ordre spécifié.
     *
     * @param start  Le point de départ du chemin.
     * @param points Les points à visiter dans l'ordre.
     * @return Une liste des coordonnées représentant le chemin trouvé.
     */
    private static List<int[]> findPath(int[] start, List<int[]> points) {
        List<int[]> path = new ArrayList<>();
        int[] current = start;

        for (int[] point : points) {
            List<int[]> shortestPath = findShortestPath(current, point);

            // Vérifie si le chemin le plus court est vide ou s'il contient une case bloquée
            if (shortestPath.isEmpty() || hasBlockedCell(shortestPath)) {
                return new ArrayList<>(); // Retourne un chemin vide pour indiquer l'impossibilité de trouver un chemin valide
            }

            shortestPath.remove(0); // Supprime le premier élément (position actuelle)
            path.addAll(shortestPath);
            current = point;
        }

        return path;
    }


    /**
     * Vérifie si un chemin contient une case bloquée.
     *
     * @param path Le chemin à vérifier.
     * @return true si le chemin contient une case bloquée, sinon false.
     */
    private static boolean hasBlockedCell(List<int[]> path) {
        for (int[] cell : path) {
            // Vérifie si la case dans le chemin a une valeur de -1 (case bloquée)
            if (map[cell[0]][cell[1]] == -1) {
                return true; // Retourne true dès qu'une case bloquée est trouvée
            }
        }

        return false; // Retourne false si aucune case bloquée n'est trouvée
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

    /**
     * Vérifie si un point donné est un point d'intérêt.
     *
     * @param point Les coordonnées du point à vérifier.
     * @return true si le point est un point d'intérêt, sinon false.
     */
    private static boolean isInterestPoint(int[] point) {
        for (int[] interest : interets.keySet()) {
            // Vérifie si les coordonnées du point correspondent à celles d'un point d'intérêt dans la liste des clés
            if (Arrays.equals(interest, point)) {
                return true; // Retourne true si le point est un point d'intérêt
            }
        }
        return false; // Retourne false si le point n'est pas un point d'intérêt
    }

    /**
     * Récupère la valeur d'intérêt associée à un point donné.
     *
     * @param point Les coordonnées du point d'intérêt.
     * @return La valeur d'intérêt du point, ou 0 si le point n'est pas trouvé.
     */
    private static int getInterestValue(int[] point) {
        for (Map.Entry<int[], Integer> entry : interets.entrySet()) {
            // Vérifie si les coordonnées du point correspondent à celles de la clé dans l'entrée de la carte des intérêts
            if (Arrays.equals(entry.getKey(), point)) {
                return entry.getValue(); // Retourne la valeur associée à la clé (point d'intérêt)
            }
        }
        return 0; // Retourne 0 si le point n'est pas trouvé ou n'a pas de valeur d'intérêt associée
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