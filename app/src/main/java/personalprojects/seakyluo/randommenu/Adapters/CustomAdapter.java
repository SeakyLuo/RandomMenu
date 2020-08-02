package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Comparator;
import java.util.List;

import personalprojects.seakyluo.randommenu.interfaces.BooleanLambda;
import personalprojects.seakyluo.randommenu.models.AList;

public abstract class CustomAdapter<T> extends RecyclerView.Adapter<CustomAdapter<T>.CustomViewHolder> {
    public AList<T> data = new AList<>();
    public AList<CustomViewHolder> viewHolders = new AList<>();
    public Context context;
    public CustomAdapter() {}

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<T>.CustomViewHolder holder, int position) {
        holder.setData(holder.data = data.get(position));
        if (viewHolders.count() >= position) viewHolders.add(holder);
        else viewHolders.set(holder, position);
    }

    public void setData(AList<T> list){
        this.data.copyFrom(list);
        notifyDataSetChanged();
    }
    public void setData(List<T> list){
        this.data = new AList<>(list);
        notifyDataSetChanged();
    }
    private void addOne(T object){
        data.add(object);
    }
    private void addOne(T object, int index) { data.add(object, index); }
    public void add(List<T> list){
        int count = data.count();
        for (int i = 0; i < list.size(); i++) addOne(list.get(i));
        notifyItemRangeInserted(count, list.size());
    }
    public void add(AList<T> list){
        int count = data.count();
        for (int i = 0; i < list.count(); i++) addOne(list.get(i));
        notifyItemRangeInserted(count, list.count());
    }
    public void add(AList<T> list, int index){
        data.addAll(list, index);
        notifyItemRangeInserted(index, getItemCount());
    }
    public void add(T object){
        addOne(object);
        notifyItemInserted(data.count() - 1);
    }
    public void add(T object, int index){
        addOne(object, index);
        notifyItemInserted(index);
    }
    public void pop(int index){
        data.pop(index);
        notifyItemRemoved(index);
    }
    public void remove(T object){
        int index = data.indexOf(object);
        if (data.remove(object)){
            notifyItemRemoved(index);
        }
    }
    public void sort(Comparator<? super T> comparator) {
        data.sort(comparator);
        notifyDataSetChanged();
    }
    public void set(T element, int index){
        data.set(element, index);
        notifyItemChanged(index);
    }
    public void move(int from, int to){
        data.move(from, to);
        notifyItemMoved(from, to);
    }
    public int indexOf(T element) { return data.indexOf(element); }
    public int indexOf(BooleanLambda<T> lambda) { return data.indexOf(lambda); }
    public AList<T> getData(){ return data; }
    public boolean isEmpty(){ return data.count() == 0; }
    public boolean contains(T element) { return data.contains(element); }
    public void clear(){
        final int size = data.count();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() { return data.count(); }

    public abstract class CustomViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public T data;
        public CustomViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
        }

        abstract void setData(T data);
    }
}