package personalprojects.seakyluo.randommenu.Models;

import personalprojects.seakyluo.randommenu.Interfaces.ZipVoidLambda;

public class EnumerateList<T> extends IList<T> {
    public EnumerateList(IList<T> list) { this.list = list.list; }
    public EnumerateList<T> For(ZipVoidLambda<Integer, T> lambda){
        for (int i = 0; i < list.size(); i++)
            lambda.operate(i, list.get(i));
        return this;
    }
    public AList<T> ToAList() { return new AList<>(list); }
}