package personalprojects.seakyluo.randommenu.models;


import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import personalprojects.seakyluo.randommenu.interfaces.ForLambda;
import personalprojects.seakyluo.randommenu.interfaces.ZipVoidLambda;

public class AList<T> extends IList<T> {
    public AList(){}
    public AList(Collection<T> collection) { addAll(collection); }
    public AList(T... collection) { addAll(Arrays.asList(collection)); }
    public AList(T element) { add(element); }
    public AList(T element, int count) {
        IntStream.range(0, count + 1).forEach(i -> add(element)); }

    public int count(Predicate<T> predicate){
        return (int) stream().filter(predicate).count();
    }
    public boolean any(Predicate<T> predicate){
        return stream().anyMatch(predicate);
    }
    public boolean all(Predicate<T> predicate){
        return stream().allMatch(predicate);
    }
    @Override
    public boolean equals(Object object){
        if (!(object instanceof Collection)) return false;
        return CollectionUtils.isEqualCollection(this, (Collection<T>) object);
    }
    public AList<T> with(T element) { add(element); return this; }
    public AList<T> with(T element, int index) { add(index, element); return this; }
    public AList<T> with(Collection<T> collection) { addAll(collection); return this; }
    public AList<T> with(Collection<T> collection, int index) { addAll(index, collection); return this; }
    public T pop() { return pop(-1); }
    public T pop(int index){
        index = modIndex(index);
        T element = get(index);
        remove(index);
        return element;
    }
    public AList<T> without(T element){
        remove(element);
        return this;
    }
    public AList<T> without(Collection<T> collection) {
        if (CollectionUtils.isNotEmpty(collection)){
            removeAll(collection);
        }
        return this;
    }
    public AList<T> clear(int start) {
        return clear(start, size());
    }
    public AList<T> clear(int start, int end) {
        subList(modIndex(start), modIndex(end)).clear();
        return this;
    }
    public AList<T> copy(){ return new AList<>(this); }
    public AList<T> copyFrom(Collection<T> collection){
        clear();
        addAll(collection);
        return this;
    }
    public int indexOf(Predicate<T> lambda){
        for (int i = 0; i < size(); i++)
            if (lambda.test(get(i)))
                return i;
        return -1;
    }
    public int lastIndexOf(Predicate<T> lambda){
        for (int i = size() - 1; i >= 0; i--)
            if (lambda.test(get(i)))
                return i;
        return -1;
    }
    public AList<T> before(int index){ return sub(0, index); }
    public AList<T> after(int index) { return sub(index + 1, size()); }
    public AList<T> sub(int start, int end){
        start = modIndex(start);
        end = modIndex(end);
        return new AList<>(subList(start, end));
    }
    public AList<T> sub(int start, int end, int step){
        AList<T> collection = new AList<>();
        start = modIndex(start);
        end = modIndex(end);
        for (int i = start; i < end; i = i + step)
            collection.add(get(i));
        return collection;
    }
    public T get(int index){ return super.get(modIndex(index)); }
    public T set(T element, int index) { super.set(modIndex(index), element); return element; }
    public AList<T> set(UnaryOperator<T> lambda) { replaceAll(lambda); return this; }
    public T first() { return size() > 0 ? get(0) : null; }
    public T first(T target){
        return stream().filter(target::equals).findFirst().orElse(null);
    }
    public T first(Predicate<T> lambda){
        return stream().filter(lambda).findFirst().orElse(null);
    }
    public T last() { return size() > 0 ? get(size() - 1) : null; }
    public T last(Predicate<T> lambda){ return reverse().first(lambda); }
    public AList<T> For(ForLambda lambda) {
        for (int i = 0; i < size(); i++) lambda.operate(i);
        return this;
    }
    public AList<T> ForEach(Consumer<? super T> lambda){
        forEach(lambda);
        return this;
    }
    public <A> AList<A> convert(Function<? super T, ? extends A> mapper){
        return new AList<>(stream().map(mapper).collect(Collectors.toList()));
    }
    public AList<T> find(Predicate<T> lambda){
        return new AList<>(stream().filter(lambda).collect(Collectors.toList()));
    }
    public AList<T> reverse(){
        int count = size();
        for (int i = 0; i < count / 2; i++) swap(i, count - 1 - i);
        return this;
    }
    public AList<T> swap(int item1, int item2){
        Collections.swap(this, modIndex(item1), modIndex(item2));
        return this;
    }
    public AList<T> swap(T item1, T item2){ return swap(indexOf(item1), indexOf(item2)); }
    public AList<T> move(int from, int to){
        from = modIndex(from);
        to = modIndex(to);
        T item = get(from);
        remove(from);
        add(to, item);
        return this;
    }
    public AList<T> move(T object, int index){
        remove(object);
        add(index, object);
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
    public Set<T> toSet() { return new HashSet<>(this); }
    public AList<T> sorted(Comparator<? super T> comparator) {
        sort(comparator);
        return this;
    }
    public Map<T, Integer> toCounter(){
        Map<T, Integer> map = new HashMap<>();
        for (T element: this)
            map.put(element, map.getOrDefault(element, 0) + 1);
        return map;
    }
    public <A> ZipList<T, A> zip(Collection<A> zip){
        return new ZipList<>(this, zip);
    }
    public <A> ZipList<T, A> zip(IList<A> zip){ return new ZipList<>(this, zip); }
    public AList<T> enumerate(ZipVoidLambda<Integer, T> lambda) {
        for (int i = 0; i < size(); i++)
            lambda.operate(i, get(i));
        return this;
    }
    private int modIndex(int index){
        if (index == 0) return 0;
        int count = size();
        if (count == index) return index;
        if (count > 0) index = index % count;
        return index < 0 ? index + count : index;
    }
    public AList<T> shuffle(){
        Collections.shuffle(this);
        return this;
    }

}