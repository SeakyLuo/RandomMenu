package personalprojects.seakyluo.randommenu;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class AList<T> {
    private ArrayList<T> list = new ArrayList<>();;
    public AList(){}
    public AList(Collection<T> collection) { for (T element: collection) Add(element); }
    public AList(AList<T> collection) { Add(collection); }
    public AList(T element) { Add(element); }
    public int Count() { return list.size(); }
    public boolean IsEmpty() { return list.size() == 0; }
    public int Count(BooleanLambda<T> lambda){
        int count = 0;
        for (T element: list) if (lambda.operate(element)) count++;
        return count;
    }
    public boolean Contains(T element) { return list.contains(element); }
    public boolean Contains(BooleanLambda<T> lambda){
        return Any(lambda);
    }
    public boolean Any(BooleanLambda<T> lambda){
        for (T obj: list) if (lambda.operate(obj)) return true;
        return false;
    }
    public boolean All(BooleanLambda<T> lambda){
        for (T obj: list) if (!lambda.operate(obj)) return false;
        return true;
    }
    public boolean SameCollection(AList<T> collection){
        int count = Count();
        if (count != collection.Count()) return false;
        for(int i = 0; i < count; i++)
            if (!Get(i).equals(collection.Get(i)))
                return false;
        return true;
    }
    public boolean SameCollection(Collection<T> collection){
        return SameCollection(new AList<>(collection));
    }
    public T Add(T element) { list.add(element); return element; }
    public T Add(T element, int index) { list.add(index, element); return element; }
    public AList<T> Add(Collection<T> collection) { list.addAll(collection); return this; }
    public AList<T> Add(AList<T> collection) { list.addAll(collection.list); return this; }
    public AList<T> Add(Collection<T> collection, int index) { list.addAll(index, collection); return this; }
    public AList<T> Add(AList<T> collection, int index) { list.addAll(index, collection.list); return this; }
    public boolean Remove(T element) { return list.remove(element); }
    public boolean RemoveAt(int index) {
        boolean success;
        if (success = index < Count()) list.remove(index);
        return success;
    }
    public boolean Remove(BooleanLambda<T> lambda){ return list.removeIf(lambda::operate); }
    public AList<T> Without(T element){ Remove(element); return this; }
    public void Clear() { list.clear(); }
    public AList<T> Copy(){ return new AList<>(list); }
    public void CopyFrom(AList<T> collection){
        CopyFrom(collection.list);
    }
    public void CopyFrom(Collection<T> collection){
        Clear();
        Add(collection);
    }
    public int IndexOf(T element){ return list.indexOf(element); }
    public int IndexOf(BooleanLambda<T> lambda){
        for (int i = 0; i < list.size(); i++)
            if (lambda.operate(list.get(i)))
                return i;
        return -1;
    }
    public AList<Integer> IndexOfAll(BooleanLambda<T> lambda){
        AList<Integer> collection = new AList<>();
        for (int i = 0; i < list.size(); i++)
            if (lambda.operate(list.get(i)))
                collection.Add(i);
        return collection;
    }
    public int LastIndexOf(T element) { return list.lastIndexOf(element); }
    public int LastIndexOf(BooleanLambda<T> lambda){
        for (int i = Count(); i >= 0; i--)
            if (lambda.operate(list.get(i)))
                return i;
        return -1;
    }
    public AList<T> Before(int index){ return Sub(0, index); }
    public AList<T> After(int index) { return Sub(index + 1, Count()); }
    public AList<T> Sub(int start, int end){
        AList<T> newList = new AList<>();
        for (int i = start; i < end; i = i + 1)
            newList.Add(list.get(i));
        return newList;
    }
    public AList<T> Sub(int start, int end, int step){
        AList<T> newList = new AList<>();
        for (int i = start; i < end; i = i + step)
            newList.Add(list.get(i));
        return newList;
    }
    public T Get(int index){ return list.get(index); }
    public void Set(T element, int index) { list.set(index, element); }
    public T Find(BooleanLambda<T> lambda){
        for (T element: list)
            if (lambda.operate(element))
                return element;
        return null;
    }
    public T FindLast(BooleanLambda<T> lambda){
        return Reverse().Find(lambda);
    }
    public void For(ForLambda lambda) { for (int i = 0; i < Count(); i++) lambda.operate(i); }
    public void ForEach(VoidLambda<T> lambda){ for (T element: list) lambda.operate(element); }
    public <A> AList<A> Convert(ObjectLambda<T, A> lambda){
        AList<A> collection = new AList<A>();
        for (T element: list) collection.Add(lambda.operate(element));
        return collection;
    }
    public AList<T> Filter(BooleanLambda<T> lambda){
        AList<T> collection = new AList<>();
        for (T element: list) if (lambda.operate(element)) collection.Add(element);
        return collection;
    }
    public AList<T> Reverse(){
        int count = Count();
        for (int i = 0; i < count / 2; i++) Swap(i, count - 1 - i);
        return this;
    }
    public void Swap(int item1, int item2){
        T temp = list.get(item1);
        list.set(item1, list.get(item2));
        list.set(item2, temp);
    }
    public AList<T> SetDifference(AList<T> collection){ return SetDifference(collection.ToHashSet()); }
    public AList<T> SetDifference(Collection<T> collection){ return SetDifference((new AList<>(collection)).ToHashSet()); }
    public AList<T> SetDifference(HashSet<T> set2){
        HashSet<T> set1 = ToHashSet();
        AList<T> diff = new AList<>();
        for (T element: set1) if (!set2.contains(element)) diff.Add(element);
        return diff;
    }
    public T[] ToArray() { return (T[])list.toArray(); }
    public HashSet<T> ToHashSet() { return new HashSet<>(list); }
    public ArrayList<T> ToArrayList(){ return new ArrayList<T>(list); }
    public AList<T> Sort(Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
        return this;
    }
}
interface IntLambda<T>{

}
interface doubleLambda<T>{

}
interface BooleanLambda<T>{
    boolean operate(T object);
}
interface VoidLambda<T>{
    void operate(T object);
}
interface ObjectLambda<T, A>{
    A operate(T object);
}
interface ForLambda{
    void operate(int index);
}