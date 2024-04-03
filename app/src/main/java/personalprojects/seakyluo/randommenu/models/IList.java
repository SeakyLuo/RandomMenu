package personalprojects.seakyluo.randommenu.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class IList<T> extends ArrayList<T> {
    public IList() {}
    public IList(T... collection) { addAll(Arrays.asList(collection)); }
    public IList(Collection<T> collection) { addAll(collection); }
    public IList(T element) { add(element); }
    public IList(IList<T> collection) { addAll(collection); }

}