package personalprojects.seakyluo.randommenu.models;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class TagMapEntry {

    private long id;
    private String keyword;
    private List<Tag> tags;

    public boolean matches(String foodName){
        return foodName.contains(keyword) || foodName.matches(keyword);
    }

    public void setTagsFromString(List<String> stringList){
        tags = stringList.stream().map(Tag::new).collect(Collectors.toList());
    }

}
