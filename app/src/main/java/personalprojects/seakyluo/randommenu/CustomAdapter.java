package personalprojects.seakyluo.randommenu;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Comparator;
import java.util.List;

import personalprojects.seakyluo.randommenu.Models.AList;

public abstract class CustomAdapter<T> extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    AList<T> data = new AList<>();
    Activity activity;

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        holder.setData(holder.data = data.Get(position));
    }

    public void setActivity(Activity activity) { this.activity = activity; }

    public void setData(AList<T> data){
        this.data.CopyFrom(data);
        notifyDataSetChanged();
    }
    public void setData(List<T> data){
        this.data = new AList<>(data);
        notifyDataSetChanged();
    }
    private void addOne(T object){
        data.Add(object);
    }
    private void addOne(int index, T object) { data.Add(object, index); }
    public void add(List<T> list){
        int count = data.Count();
        for (int i = 0; i < list.size(); i++) addOne(list.get(i));
        notifyItemRangeInserted(count, list.size());
    }
    public void add(T[] list){
        int count = data.Count();
        for (int i = 0; i < list.length; i++) addOne(list[i]);
        notifyItemRangeInserted(count, list.length);
    }
    public void addAll(int index, List<T> data){
        this.data.Add(data, index);
        notifyItemRangeInserted(index, getItemCount());
    }
    public void add(T object){
        addOne(object);
        notifyItemInserted(data.Count() - 1);
    }
    public void add(int index, T object){
        addOne(index, object);
        notifyItemInserted(index);
    }
    public void remove(int index){
        data.Pop(index);
        notifyItemRemoved(index);
    }
    public void remove(T object){
        int index = data.IndexOf(object);
        if (index > -1){
            data.Remove(object);
            notifyItemRemoved(index);
        }
    }
    public void sort(Comparator<? super T> comparator) {
        data.Sort(comparator);
        notifyDataSetChanged();
    }
    public void set(T element, int index){
        data.Set(element, index);
        notifyItemChanged(index);
    }
    public AList<T> getData(){ return data; }
    public boolean hasData(){ return data.Count() > 0; }
    public void clear(){
        final int size = data.Count();
        data.Clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        return data.Count();
    }

    abstract class CustomViewHolder extends RecyclerView.ViewHolder{
        protected View view;
        protected Context context;
        protected T data;
        CustomViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.context = view.getContext();
        }
        abstract void setData(T data);
    }
}