package personalprojects.seakyluo.randommenu;

import java.util.ArrayList;

public class Food {
    public String Name;
    public String ImagePath;
    public ArrayList<Tag> Tags = new ArrayList<>();
    public String Note;
    public Food(String name, String path, ArrayList<Tag> tags, String note){
        Name = name;
        ImagePath = path;
        Tags = tags;
        Note = note;
    }
}
