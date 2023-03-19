package personalprojects.seakyluo.randommenu.models;

import lombok.Data;

@Data
public class MatchFood {
    private SelfFood food;
    private int points;
    private int bonus;
    private int namePoints;
    private int tagPoints;
    private int notePoints;

    public MatchFood(SelfFood food, int points){
        this.food = food;
        this.points = points;
    }

    public MatchFood(SelfFood food, int points, int bonus, int namePoints, int tagPoints, int notePoints){
        this.food = food;
        this.points = points;
        this.bonus = bonus;
        this.namePoints = namePoints;
        this.tagPoints = tagPoints;
        this.notePoints = notePoints;
    }

    public int getNamePointsWithBonus(){
        return namePoints + bonus;
    }

    public int getTagPointsWithBonus(){
        return tagPoints + bonus;
    }

    public int getNotePointsWithBonus(){
        return notePoints + bonus;
    }

    public int getPointsWithBonus(){
        return points + bonus;
    }
}
