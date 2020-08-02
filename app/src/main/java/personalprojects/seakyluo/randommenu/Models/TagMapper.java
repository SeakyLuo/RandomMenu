package personalprojects.seakyluo.randommenu.models;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TagMapper {
    public String key;
    public List<Tag> value;
    public TagMapper() {}

    public TagMapper(String key){
        this.key = key;
    }

    public TagMapper(String key, List<Tag> value){
        this.key = key;
        this.value = value;
    }

    public void setValue(List<String> tags){
        value = tags.stream().map(Tag::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o){
        return o instanceof TagMapper && Objects.nonNull(key) && key.equals(((TagMapper) o).key);
    }
}
