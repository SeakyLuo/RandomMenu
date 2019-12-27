package personalprojects.seakyluo.randommenu.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class IList<T> {
    protected ArrayList<T> list = new ArrayList<>();
    public IList() {}
    public IList(T... collection) { list.addAll(Arrays.asList(collection)); }
    public IList(Collection<T> collection) { list.addAll(collection); }
    public IList(T element) { list.add(element); }
    public IList(IList<T> collection) { list = collection.list; }
}
