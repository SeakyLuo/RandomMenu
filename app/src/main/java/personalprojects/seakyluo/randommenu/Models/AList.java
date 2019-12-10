package personalprojects.seakyluo.randommenu.Models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.UnaryOperator;

import personalprojects.seakyluo.randommenu.Interfaces.BooleanLambda;
import personalprojects.seakyluo.randommenu.Interfaces.ForLambda;
import personalprojects.seakyluo.randommenu.Interfaces.ObjectLambda;
import personalprojects.seakyluo.randommenu.Interfaces.ReduceLambda;
import personalprojects.seakyluo.randommenu.Interfaces.VoidLambda;
import personalprojects.seakyluo.randommenu.Interfaces.ZipVoidLambda;

public class AList<T> extends IList<T> {
    public AList(){}
    public AList(Collection<T> collection) { for (T element: collection) Add(element); }
    public AList(AList<T> collection) { AddAll(collection); }
    public AList(T[] collection) { for (T element: collection) Add(element); }
    public AList(T element) { Add(element); }

    public int Count() { return list.size(); }
    public boolean IsEmpty() { return list.size() == 0; }
    public int Count(BooleanLambda<T> lambda){
        int count = 0;
        for (T element: list) if (lambda.operate(element)) count++;
        return count;
    }
    public boolean Contains(T element) { return list.contains(element); }
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
    public boolean SameCollection(Collection<T> collection){  return SameCollection(new AList<>(collection)); }
    public T Add(T element) { list.add(element); return element; }
    public T Add(T element, int index) {
        index = ModIndex(index);
        list.add(index, element);
        return element;
    }
    public AList<T> With(T element) { Add(element); return this; }
    public AList<T> With(T element, int index) { Add(element, index); return this; }
    public AList<T> AddAll(Collection<T> collection) { list.addAll(collection); return this; }
    public AList<T> AddAll(AList<T> collection) { list.addAll(collection.list); return this; }
    public AList<T> AddAll(Collection<T> collection, int index) {
        index = ModIndex(index);
        list.addAll(index, collection);
        return this;
    }
    public AList<T> AddAll(AList<T> collection, int index) { return AddAll(collection.list, index); }
    public boolean Remove(T element) { return list.remove(element); }
    public boolean Remove(BooleanLambda<T> lambda){ return list.removeIf(lambda::operate); }
    public T Pop() { return Pop(-1); }
    public T Pop(int index){
        index = ModIndex(index);
        T element = list.get(index);
        list.remove(index);
        return element;
    }
    public AList<T> Pop(int start, int end){
        AList<T> collection = new AList<>();
        start = ModIndex(start);
        end = ModIndex(end);
        for (int i = start; i < end; i++)
            collection.Add(list.remove(start));
        return collection;
    }
    public AList<T> Without(T element){ Remove(element); return this; }
    public AList<T> RemoveAll(AList<T> collection) { list.removeAll(collection.list); return this; }
    public AList<T> RemoveAll(Collection<T> collection) { list.removeAll(collection); return this; }
    public AList<T> Clear() { list.clear(); return this; }
    public AList<T> Clear(int index) { while (list.size() > index) list.remove(index); return this; }
    public AList<T> Copy(){ return new AList<>(list); }
    public AList<T> CopyFrom(AList<T> collection){ return CopyFrom(collection.list); }
    public AList<T> CopyFrom(Collection<T> collection){
        Clear();
        AddAll(collection);
        return this;
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
        AList<T> collection = new AList<>();
        start = ModIndex(start);
        end = ModIndex(end);
        for (int i = start; i < end; i = i + 1)
            collection.Add(list.get(i));
        return collection;
    }
    public AList<T> Sub(int start, int end, int step){
        AList<T> collection = new AList<>();
        start = ModIndex(start);
        end = ModIndex(end);
        for (int i = start; i < end; i = i + step)
            collection.Add(list.get(i));
        return collection;
    }
    public T Get(int index){ return list.get(ModIndex(index)); }
    public T Set(T element, int index) { list.set(ModIndex(index), element); return element; }
    public AList<T> Set(UnaryOperator<T> lambda) { list.replaceAll(lambda); return this; }
    public T Find(T target){
        for (T element: list)
            if (element.equals(target))
                return element;
        return null;
    }
    public T Find(BooleanLambda<T> lambda){
        for (T element: list)
            if (lambda.operate(element))
                return element;
        return null;
    }
    public T FindLast(BooleanLambda<T> lambda){
        return Reverse().Find(lambda);
    }
    public AList<T> For(ForLambda lambda) {
        for (int i = 0; i < Count(); i++) lambda.operate(i);
        return this;
    }
    public AList<T> ForEach(VoidLambda<T> lambda){
        for (T element: list) lambda.operate(element);
        return this;
    }
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
        item1 = ModIndex(item1);
        item2 = ModIndex(item2);
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
        list.sort(comparator);
        return this;
    }
    public <A> HashMap<A, AList<T>> GroupBy(ObjectLambda<T, A> lambda){
        HashMap<A, AList<T>> hashMap = new HashMap<>();
        for (T element: list){
            A key = lambda.operate(element);
            AList<T> group = hashMap.getOrDefault(key, new AList<>());
            group.Add(element);
            hashMap.put(key, group);
        }
        return hashMap;
    }
    public HashMap<T, Integer> ToHashMap(){
        HashMap<T, Integer> hashMap = new HashMap<>();
        for (T element: list)
            hashMap.put(element, hashMap.getOrDefault(element, 0) + 1);
        return hashMap;
    }
    public <A> ZipList<T, A> Zip(Collection<A> zip){
        return new ZipList(this, zip);
    }
    public <A> ZipList<T, A> Zip(IList<A> zip){
        return new ZipList(this, zip);
    }
    public AList<T> Enumerate(ZipVoidLambda<Integer, T> lambda) {
        for (int i = 0; i < Count(); i++)
            lambda.operate(i, Get(i));
        return this;
    }
    public T Reduce(ReduceLambda<T> lambda){
        if (Count() == 0) return null;
        T start = list.get(0);
        for (T element: list.subList(1, Count()))
            start = lambda.operate(start, element);
        return start;
    }
    private int ModIndex(int index){
        int count = Count();
        if (count > 0) index = index % count;
        return index < 0 ? index + count : index;
    }
    public AList<T> Shuffle(){
        Collections.shuffle(list);
        return this;
    }
}