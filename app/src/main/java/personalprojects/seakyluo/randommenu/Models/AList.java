package personalprojects.seakyluo.randommenu.models;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.interfaces.BooleanLambda;
import personalprojects.seakyluo.randommenu.interfaces.ForLambda;
import personalprojects.seakyluo.randommenu.interfaces.ObjectLambda;
import personalprojects.seakyluo.randommenu.interfaces.ReduceLambda;
import personalprojects.seakyluo.randommenu.interfaces.VoidLambda;
import personalprojects.seakyluo.randommenu.interfaces.ZipVoidLambda;

public class AList<T> extends IList<T> {
    public AList(){}
    public AList(Collection<T> collection) { for (T element: collection) add(element); }
    public AList(AList<T> collection) { addAll(collection); }
    public AList(T... collection) { for (T element: collection) add(element); }
    public AList(T element) { add(element); }
    public AList(T element, int count) { for (int i = 0; i < count; i++) add(element); }

    public int count() { return list.size(); }
    public boolean isEmpty() { return list.size() == 0; }
    public int count(BooleanLambda<T> lambda){
        int count = 0;
        for (T element: list) if (lambda.operate(element)) count++;
        return count;
    }
    public boolean contains(T element) { return list.contains(element); }
    public boolean any(BooleanLambda<T> lambda){
        for (T obj: list) if (lambda.operate(obj)) return true;
        return false;
    }
    public boolean all(BooleanLambda<T> lambda){
        for (T obj: list) if (!lambda.operate(obj)) return false;
        return true;
    }
    @Override
    public boolean equals(Object object){
        if (object == null) return false;
        if (object instanceof AList){
            AList<T> collection = (AList<T>) object;
            int count = count();
            if (count != collection.count()) return false;
            for (int i = 0; i < count; i++)
                if (!get(i).equals(collection.get(i)))
                    return false;
            return true;
        }else if (object instanceof Collection){
            return equals(new AList<>((Collection)object));
        }else{
            return false;
        }
    }
    public T add(T element) { list.add(element); return element; }
    public T add(T element, int index) {
        index = modIndex(index);
        list.add(index, element);
        return element;
    }
    public AList<T> with(T element) { add(element); return this; }
    public AList<T> with(T element, int index) { add(element, index); return this; }
    public AList<T> addAll(Collection<T> collection) { list.addAll(collection); return this; }
    public AList<T> addAll(AList<T> collection) { list.addAll(collection.list); return this; }
    public AList<T> addAll(Collection<T> collection, int index) {
        index = modIndex(index);
        list.addAll(index, collection);
        return this;
    }
    public AList<T> addAll(AList<T> collection, int index) { return addAll(collection.list, index); }
    public boolean remove(T element) { return list.remove(element); }
    public AList<T> remove(BooleanLambda<T> lambda){ list.removeIf(lambda::operate); return this; }
    public T pop() { return pop(-1); }
    public T pop(int index){
        index = modIndex(index);
        T element = list.get(index);
        list.remove(index);
        return element;
    }
    public AList<T> pop(int start, int end){
        AList<T> collection = new AList<>();
        start = modIndex(start);
        end = modIndex(end);
        for (int i = start; i < end; i++)
            collection.add(list.remove(start));
        return collection;
    }
    public AList<T> without(T element){ remove(element); return this; }
    public AList<T> removeAll(AList<T> collection) { if (collection != null) list.removeAll(collection.list); return this; }
    public AList<T> removeAll(Collection<T> collection) { if (collection != null) list.removeAll(collection); return this; }
    public AList<T> clear() { list.clear(); return this; }
    public AList<T> clear(int start) {
        return clear(start, count());
    }
    public AList<T> clear(int start, int end) {
        list.subList(modIndex(start), modIndex(end)).clear();
        return this;
    }
    public AList<T> copy(){ return new AList<>(list); }
    public AList<T> copyFrom(AList<T> collection){ return copyFrom(collection.list); }
    public AList<T> copyFrom(Collection<T> collection){
        list.clear();
        list.addAll(collection);
        return this;
    }
    public int indexOf(T element){ return list.indexOf(element); }
    public int indexOf(BooleanLambda<T> lambda){
        for (int i = 0; i < list.size(); i++)
            if (lambda.operate(list.get(i)))
                return i;
        return -1;
    }
    public AList<Integer> indexOfAll(BooleanLambda<T> lambda){
        AList<Integer> collection = new AList<>();
        for (int i = 0; i < list.size(); i++)
            if (lambda.operate(list.get(i)))
                collection.add(i);
        return collection;
    }
    public int lastIndexOf(T element) { return list.lastIndexOf(element); }
    public int lastIndexOf(BooleanLambda<T> lambda){
        for (int i = count(); i >= 0; i--)
            if (lambda.operate(list.get(i)))
                return i;
        return -1;
    }
    public AList<T> before(int index){ return sub(0, index); }
    public AList<T> after(int index) { return sub(index + 1, count()); }
    public AList<T> sub(int start, int end){
        AList<T> collection = new AList<>();
        start = modIndex(start);
        end = modIndex(end);
        for (int i = start; i < end; i = i + 1)
            collection.add(list.get(i));
        return collection;
    }
    public AList<T> sub(int start, int end, int step){
        AList<T> collection = new AList<>();
        start = modIndex(start);
        end = modIndex(end);
        for (int i = start; i < end; i = i + step)
            collection.add(list.get(i));
        return collection;
    }
    public T get(int index){ return list.get(modIndex(index)); }
    public T set(T element, int index) { list.set(modIndex(index), element); return element; }
    public AList<T> set(UnaryOperator<T> lambda) { list.replaceAll(lambda); return this; }
    public T first() { return count() > 0 ? list.get(0) : null; }
    public T first(T target){
        for (T element: list)
            if (element.equals(target))
                return element;
        return null;
    }
    public T first(BooleanLambda<T> lambda){
        for (T element: list)
            if (lambda.operate(element))
                return element;
        return null;
    }
    public T last() { return count() > 0 ? list.get(count() - 1) : null; }
    public T last(BooleanLambda<T> lambda){ return reverse().first(lambda); }
    public AList<T> For(ForLambda lambda) {
        for (int i = 0; i < count(); i++) lambda.operate(i);
        return this;
    }
    public AList<T> forEach(VoidLambda<T> lambda){
        for (T element: list) lambda.operate(element);
        return this;
    }
    public <A> AList<A> convert(ObjectLambda<T, A> lambda){
        AList<A> collection = new AList<A>();
        for (T element: list) collection.add(lambda.operate(element));
        return collection;
    }
    public AList<T> find(BooleanLambda<T> lambda){
        AList<T> collection = new AList<>();
        for (T element: list) if (lambda.operate(element)) collection.add(element);
        return collection;
    }
    public AList<T> reverse(){
        int count = count();
        for (int i = 0; i < count / 2; i++) swap(i, count - 1 - i);
        return this;
    }
    public AList<T> swap(int item1, int item2){
        item1 = modIndex(item1);
        item2 = modIndex(item2);
        T temp = list.get(item1);
        list.set(item1, list.get(item2));
        list.set(item2, temp);
        return this;
    }
    public AList<T> swap(T item1, T item2){ return swap(indexOf(item1), indexOf(item2)); }
    public AList<T> move(int from, int to){
        from = modIndex(from);
        to = modIndex(to);
        T item = list.get(from);
        list.remove(from);
        list.add(to, item);
        return this;
    }
    public AList<T> setDifference(AList<T> collection){ return setDifference(collection.toSet()); }
    public AList<T> setDifference(Collection<T> collection){ return setDifference((new AList<>(collection)).toSet()); }
    public AList<T> setDifference(Set<T> set2){
        Set<T> set1 = toSet();
        AList<T> diff = new AList<>();
        for (T element: set1) if (!set2.contains(element)) diff.add(element);
        return diff;
    }
    public Set<T> toSet() { return new HashSet<>(list); }
    public List<T> toList() { return toArrayList(); }
    public ArrayList<T> toArrayList() { return new ArrayList<T>(list); }
    public AList<T> sort(Comparator<? super T> comparator) {
        list.sort(comparator);
        return this;
    }
    public <A> HashMap<A, AList<T>> groupBy(ObjectLambda<T, A> lambda){
        HashMap<A, AList<T>> hashMap = new HashMap<>();
        for (T element: list){
            A key = lambda.operate(element);
            AList<T> group = hashMap.getOrDefault(key, new AList<>());
            group.add(element);
            hashMap.put(key, group);
        }
        return hashMap;
    }
    public HashMap<T, Integer> toHashMap(){
        HashMap<T, Integer> hashMap = new HashMap<>();
        for (T element: list)
            hashMap.put(element, hashMap.getOrDefault(element, 0) + 1);
        return hashMap;
    }
    public <A> ZipList<T, A> zip(Collection<A> zip){
        return new ZipList<>(this, zip);
    }
    public <A> ZipList<T, A> zip(IList<A> zip){ return new ZipList<>(this, zip); }
    public AList<T> enumerate(ZipVoidLambda<Integer, T> lambda) {
        for (int i = 0; i < count(); i++)
            lambda.operate(i, get(i));
        return this;
    }
    public T reduce(ReduceLambda<T> lambda){
        if (count() == 0) return null;
        T start = list.get(0);
        for (T element: list.subList(1, count()))
            start = lambda.operate(start, element);
        return start;
    }
    private int modIndex(int index){
        int count = count();
        if (count == index) return index;
        if (count > 0) index = index % count;
        return index < 0 ? index + count : index;
    }
    public AList<T> shuffle(){
        Collections.shuffle(list);
        return this;
    }
    public Stream<T> stream(){
        return list.stream();
    }
    public Stream<T> parallelStream(){
        return list.parallelStream();
    }

    @NonNull
    @Override
    public String toString() { return list.toString(); }
}