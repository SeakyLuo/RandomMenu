package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

import static android.app.Activity.RESULT_OK;

public class NavigationFragment extends Fragment {
    public static final String TAG = "NavigationFragment";
    private TextView title_text_view;
    private SelectTagAdapter selectTagAdapter;
    private FoodAdapter foodAdapter;
    private boolean IsLoaded = false;
    private Tag pendingTag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        title_text_view = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.navi_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditFoodActivity.class);
            intent.putExtra(EditFoodActivity.FOOD, Settings.settings.FoodDraft);
            startActivityForResult(intent, EditFoodActivity.FOOD_CODE);
            getActivity().overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
        });

        RecyclerView masterView = view.findViewById(R.id.masterView);
        masterView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        selectTagAdapter = new SelectTagAdapter((viewHolder, tag) -> selectTag(tag));
        masterView.setAdapter(selectTagAdapter);

        RecyclerView detailView = view.findViewById(R.id.detailView);
        foodAdapter = new FoodAdapter();
        foodAdapter.SetOnFoodClickedListener(((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.SetFood(food);
            dialog.SetFoodEditedListener((before, after) -> SetData());
            dialog.showNow(getFragmentManager(), AskYesNoDialog.WARNING);
        }));
        detailView.setAdapter(foodAdapter);

        IsLoaded = true;
        SetData();
        if (pendingTag == null){
            selectTagAdapter.HighlightTag(Tag.AllCategoriesTag);
        }else{
            selectTag(pendingTag);
            selectTagAdapter.HighlightTag(pendingTag);
            pendingTag = null;
        }
        return view;
    }

    public void SetData(){
        if (!IsLoaded) return;
        selectTagAdapter.SetData(Settings.settings.Tags);
        foodAdapter.SetData(Settings.settings.Foods);
    }

    public void SelectTag(Tag tag){
        pendingTag = tag;
        if (!IsLoaded) return;
        selectTagAdapter.HighlightTag(tag);
        selectTag(tag);
    }

    private void selectTag(Tag tag){
        if (tag.equals(Tag.AllCategoriesTag)){
            title_text_view.setText(Tag.Format(tag.Name, Settings.settings.Foods.Count()));
            foodAdapter.Reset();
        }else{
            title_text_view.setText(tag.Name);
            foodAdapter.Filter(tag);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        SetData();
    }
}
