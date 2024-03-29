package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import personalprojects.seakyluo.randommenu.fragments.FoodCardFragment;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.R;

public class CardStackAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private FragmentManager manager;
    public AList<SelfMadeFood> data;

    public CardStackAdapter(Context context, FragmentManager manager, AList<SelfMadeFood> data) {
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
        holder.setData(data.get(position));
        return convertView;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    class ViewHolder {
        View view;
        FoodCardFragment foodCardFragment = new FoodCardFragment();
        ViewHolder(View view){
            this.view = view;
            manager.beginTransaction().add(R.id.food_card_frame, foodCardFragment).commit();
        }

        void setData(SelfMadeFood data){
            foodCardFragment.fillFood(data);
        }
    }

}
