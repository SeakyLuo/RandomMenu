package personalprojects.seakyluo.randommenu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Comparator;
import java.util.List;

import personalprojects.seakyluo.randommenu.Interfaces.BooleanLambda;
import personalprojects.seakyluo.randommenu.Models.AList;

public abstract class CustomAdapter<T> extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    AList<T> data = new AList<>();
    AList<CustomViewHolder> viewHolders = new AList<>();
    Activity activity;
    public CustomAdapter() {}
    public CustomAdapter(AList<T> data) { this.data = data; }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        holder.SetData(holder.data = data.Get(position));
        if (viewHolders.Count() >= position) viewHolders.Add(holder);
        else viewHolders.Set(holder, position);
    }

    public AList<CustomViewHolder> GetViewHolders() { return viewHolders; }

    public void SetActivity(Activity activity) { this.activity = activity; }

    public void SetData(AList<T> list){
        this.data.CopyFrom(list);
        notifyDataSetChanged();
    }
    public void SetData(List<T> list){
        this.data = new AList<>(list);
        notifyDataSetChanged();
    }
    private void addOne(T object){
        data.Add(object);
    }
    private void addOne(T object, int index) { data.Add(object, index); }
    public void Add(List<T> list){
        int count = data.Count();
        for (int i = 0; i < list.size(); i++) addOne(list.get(i));
        notifyItemRangeInserted(count, list.size());
    }
    public void Add(AList<T> list){
        int count = data.Count();
        for (int i = 0; i < list.Count(); i++) addOne(list.Get(i));
        notifyItemRangeInserted(count, list.Count());
    }
    public void Add(AList<T> list, int index){
        data.AddAll(list, index);
        notifyItemRangeInserted(index, getItemCount());
    }
    public void Add(T object){
        addOne(object);
        notifyItemInserted(data.Count() - 1);
    }
    public void Add(T object, int index){
        addOne(object, index);
        notifyItemInserted(index);
    }
    public void Pop(int index){
        data.Pop(index);
        notifyItemRemoved(index);
    }
    public void Remove(T object){
        int index = data.IndexOf(object);
        if (data.Remove(object)){
            notifyItemRemoved(index);
        }
    }
    public void Sort(Comparator<? super T> comparator) {
        data.Sort(comparator);
        notifyDataSetChanged();
    }
    public void Set(T element, int index){
        data.Set(element, index);
        notifyItemChanged(index);
    }
    public int IndexOf(T element) { return data.IndexOf(element); }
    public int IndexOf(BooleanLambda<T> lambda) { return data.IndexOf(lambda); }
    public AList<T> GetData(){ return data; }
    public boolean IsEmpty(){ return data.Count() == 0; }
    public boolean Contains(T element) { return data.Contains(element); }
    public void Clear(){
        final int size = data.Count();
        data.Clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() { return data.Count(); }

    public abstract class CustomViewHolder extends RecyclerView.ViewHolder{
        protected View view;
        protected T data;
        CustomViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
        }
        abstract void SetData(T data);
    }
}