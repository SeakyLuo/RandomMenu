package personalprojects.seakyluo.randommenu.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import personalprojects.seakyluo.randommenu.interfaces.ZipVoidLambda;

public class ZipList<T1, T2> extends IList<T1>{
    private List<T2> zip_list;
    public ZipList(IList<T1> list1, Collection<T2> list2){
        list = list1.list;
        zip_list = new ArrayList<>(list2);
    }
    public ZipList(IList<T1> list1, IList<T2> list2){
        list = list1.list;
        zip_list = list2.list;
    }
    public ZipList(Collection<T1> list1, Collection<T2> list2){
        list = new ArrayList<>(list1);
        zip_list = new ArrayList<>(list2);
    }
    public ZipList(Collection<T1> list1, IList<T2> list2){
        list = new ArrayList<>(list1);
        zip_list = list2.list;
    }

    public ZipList<T1, T2> ForEach(ZipVoidLambda<T1, T2> lambda){
        int min = Math.min(list.size(), zip_list.size());
        for (int i = 0; i < min; i++)
            lambda.operate(list.get(i), zip_list.get(i));
        return this;
    }
    
    public AList<ZipObject> ToAList(){
        AList<ZipObject> collection = new AList<>();
        ForEach(((object1, object2) -> collection.add(new ZipObject(object1, object2))));
        return collection;
    }

    public ZipList<T1, T2> Enumerate(ZipVoidLambda<Integer, ZipObject> lambda){
        int min = Math.min(list.size(), zip_list.size());
        for(int i = 0; i < min; i++)
            lambda.operate(i, new ZipObject(list.get(i), zip_list.get(i)));
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
