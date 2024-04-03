package personalprojects.seakyluo.randommenu.utils;

import java.util.List;
import java.util.function.Predicate;

public class ListUtils {

    public static <T> int indexOf(List<T> list, Predicate<T> predicate){
        for (int i = 0; i < list.size(); i++){
            if (predicate.test(list.get(i))){
                return i;
            }
        }
        return -1;
    }
}
