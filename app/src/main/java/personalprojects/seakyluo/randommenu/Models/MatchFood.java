package personalprojects.seakyluo.randommenu.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatchFood extends MatchResult<SelfMadeFood> {

    private int bonus;
    private int namePoints;
    private int tagPoints;
    private int notePoints;

    public MatchFood(SelfMadeFood data, int points){
        this.data = data;
        this.points = points;
    }

    public MatchFood(SelfMadeFood data, int points,  int bonus, int namePoints, int tagPoints, int notePoints){
        this.data = data;
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
