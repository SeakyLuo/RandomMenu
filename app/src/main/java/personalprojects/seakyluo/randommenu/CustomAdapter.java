package personalprojects.seakyluo.randommenu;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class CustomAdapter<T> extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    List<T> data = new ArrayList<>();
    Activity activity;

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        holder.setData(holder.data = data.get(position));
    }

    public void setActivity(Activity activity) { this.activity = activity; }

    public void setData(List<T> data){
        this.data = data;
        notifyDataSetChanged();
    }
    private void addOne(T object){
        data.add(object);
    }
    private void addOne(int index, T object) { data.add(index, object); }
    public void add(List<T> list){
        int count = data.size();
        for (int i = 0; i < list.size(); i++) addOne(list.get(i));
        notifyItemRangeInserted(count, list.size());
    }
    public void add(T[] list){
        int count = data.size();
        for (int i = 0; i < list.length; i++) addOne(list[i]);
        notifyItemRangeInserted(count, list.length);
    }
    public void addAll(int index, List<T> data){
        this.data.addAll(index, data);
        notifyItemRangeInserted(index, getItemCount());
    }
    public void add(T object){
        addOne(object);
        notifyItemInserted(data.size() - 1);
    }
    public void add(int index, T object){
        addOne(index, object);
        notifyItemInserted(index);
    }
    public void remove(int index){
        data.remove(index);
        notifyItemRemoved(index);
    }
    public void remove(T object){
        int index = data.indexOf(object);
        if (index > -1){
            data.remove(object);
            notifyItemRemoved(index);
        }
    }
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(data, comparator);
        notifyDataSetChanged();
    }
    // Shouldn't be called
    public List<T> getData(){ return data; }
    public boolean hasData(){ return data.size() > 0; }
    public void clear(){
        final int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        return data.size();
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