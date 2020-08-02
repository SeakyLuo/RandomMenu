package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;

public interface TagFilterListener {
    void Filter(AList<Tag> preferred, AList<Tag> excluded);
}
