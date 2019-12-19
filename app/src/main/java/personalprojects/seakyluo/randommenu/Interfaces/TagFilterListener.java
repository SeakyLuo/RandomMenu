package personalprojects.seakyluo.randommenu.Interfaces;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

public interface TagFilterListener {
    void Filter(AList<Tag> preferred, AList<Tag> excluded);
}
