package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;

public class CardStackAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private FragmentManager manager;
    public AList<Food> data;

    public CardStackAdapter(Context context, FragmentManager manager, AList<Food> data) {
        this.inflater = LayoutInflater.from(context);
        this.manager = manager;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_food_card, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.SetData(data.Get(position));
        return convertView;
    }

    @Override
    public int getCount() { return data.Count(); }

    @Override
    public Object getItem(int position) { return data.Get(position); }

    @Override
    public long getItemId(int position) { return position; }

    class ViewHolder {
        View view;
        FoodCardFragment foodCardFragment = new FoodCardFragment();
        ViewHolder(View view){
            this.view = view;
            manager.beginTransaction().add(R.id.food_card_frame, foodCardFragment).commit();
        }
        void SetData(Food data){
            foodCardFragment.LoadFood(data);
        }
    }

}
