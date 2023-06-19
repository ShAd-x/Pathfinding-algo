package fr.alexis;

import java.util.List;

// Classe représentant une route avec le chemin et le coût de déplacement
public class Route {

    private List<int[]> path;
    private int cost;

    public Route(List<int[]> path, int cost) {
        this.path = path;
        this.cost = cost;
    }

    public List<int[]> getPath() {
        return path;
    }

    public int getCost() {
        return cost;
    }
}
