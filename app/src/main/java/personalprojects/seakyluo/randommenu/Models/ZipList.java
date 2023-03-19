package personalprojects.seakyluo.randommenu.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class ZipList<T1, T2> extends IList<T1>{
    private List<T2> zip_list;
    public ZipList(Collection<T1> list1, Collection<T2> list2){
        super(list1);

        zip_list = new ArrayList<>(list2);
    }
    public ZipList(Collection<T1> list1, IList<T2> list2){
        super(list1);
        zip_list = new ArrayList<>(list2);
    }

    public ZipList<T1, T2> ForEach(BiConsumer<T1, T2> lambda){
        int min = Math.min(size(), zip_list.size());
        for (int i = 0; i < min; i++)
            lambda.accept(get(i), zip_list.get(i));
        return this;
    }
    
    public AList<ZipObject> ToAList(){
        AList<ZipObject> collection = new AList<>();
        ForEach((object1, object2) -> collection.add(new ZipObject(object1, object2)));
        return collection;
    }

    public ZipList<T1, T2> Enumerate(BiConsumer<Integer, ZipObject> lambda){
        int min = Math.min(size(), zip_list.size());
        for(int i = 0; i < min; i++)
            lambda.accept(i, new ZipObject(get(i), zip_list.get(i)));
        return this;
    }

    public class ZipObject{
        public T1 first;
        public T2 second;
        ZipObject(T1 first, T2 second){
            this.first = first;
            this.second = second;
        }
    }
}
