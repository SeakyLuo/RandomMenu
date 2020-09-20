package personalprojects.seakyluo.randommenu.models;

public class MatchFood {
    public Food food;
    public int points;
    public int bonus;
    public int namePoints;
    public int tagPoints;
    public int notePoints;
    public MatchFood(Food food, int points){
        this.food = food;
        this.points = points;
    }

    public MatchFood(Food food, int points, int bonus, int namePoints, int tagPoints, int notePoints){
        this.food = food;
        this.points = points;
        this.bonus = bonus;
        this.namePoints = namePoints;
        this.tagPoints = tagPoints;
        this.notePoints = notePoints;
    }
}
