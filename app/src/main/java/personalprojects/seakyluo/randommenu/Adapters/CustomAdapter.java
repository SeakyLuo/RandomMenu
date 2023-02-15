package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.models.AList;

public abstract class CustomAdapter<T> extends RecyclerView.Adapter<CustomAdapter<T>.CustomViewHolder> {
    @Getter
    protected AList<T> data = new AList<>();
    @Getter
    protected AList<CustomViewHolder> viewHolders = new AList<>();
    @Setter
    protected Context context;

    protected abstract int getLayout(int viewType);

    protected abstract void fillViewHolder(CustomViewHolder viewHolder, T data, int position);

    protected void dataSizeChanged(){}

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(getLayout(viewType), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<T>.CustomViewHolder holder, int position) {
        T item = data.get(position);
        holder.setData(item);
        fillViewHolder(holder, item, position);
        if (viewHolders.size() >= position) viewHolders.add(holder);
        else viewHolders.set(holder, position);
    }

    public void setData(List<T> list){
        this.data.copyFrom(list);
        dataSizeChanged();
        notifyDataSetChanged();
    }

    public void add(List<T> list){
        final int count = data.size();
        data.addAll(list);
        dataSizeChanged();
        notifyItemRangeInserted(count, list.size());
    }

    public void add(List<T> list, int index){
        data.addAll(index, list);
        dataSizeChanged();
        notifyItemRangeInserted(index, getItemCount());
    }

    public void add(T object){
        data.add(object);
        dataSizeChanged();
        notifyItemInserted(data.size() - 1);
    }

    public void add(T object, int index){
        data.add(index, object);
        dataSizeChanged();
        notifyItemInserted(index);
    }

    public void pop(int index){
        data.pop(index);
        dataSizeChanged();
        notifyItemRemoved(index);
    }

    public void remove(T object){
        int index = data.indexOf(object);
        removeAt(index);
    }

    public void removeAt(int index){
        data.remove(index);
        dataSizeChanged();
        notifyItemRemoved(index);
    }

    public void update(T element, int index){
        data.set(element, index);
        notifyItemChanged(index);
    }

    public void sort(Comparator<? super T> comparator) {
        data.sorted(comparator);
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

    public int indexOf(Predicate<T> lambda) { return data.indexOf(lambda); }

    public T getDataAt(int index) { return data.get(index); }

    public boolean isEmpty(){ return data.size() == 0; }

    public boolean contains(T element) { return data.contains(element); }

    public void clear(){
        final int size = data.size();
        data.clear();
        dataSizeChanged();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() { return data.size(); }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @Getter
        private View view;

        @Getter
        @Setter
        private T data;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}