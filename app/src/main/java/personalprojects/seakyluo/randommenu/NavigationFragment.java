package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.Set;

import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

import static android.app.Activity.RESULT_OK;

public class NavigationFragment extends Fragment {
    public static final String TAG = "NavigationFragment";
    private ImageButton search_button;
    private TextView title_text_view;
    private SelectTagAdapter selectTagAdapter;
    private FoodAdapter foodAdapter;
    private boolean IsLoaded = false;
    private Tag pendingTag, lastTag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        title_text_view = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.navi_fab);
        fab.setOnClickListener(v -> EditFood(null));

        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchActivity.class));
            getActivity().overridePendingTransition(R.anim.push_right_in, 0);
        });

        RecyclerView masterView = view.findViewById(R.id.masterView);
        masterView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        selectTagAdapter = new SelectTagAdapter((viewHolder, tag) -> selectTag(tag));
        selectTagAdapter.SetContext(getContext());
        selectTagAdapter.SetLongClickListener(((viewHolder, data) -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.long_click_tag_menu, getContext(), viewHolder.view);
            if (data.IsAllCategoriesTag()){
                helper.removeItem(R.id.edit_tag_item);
                helper.removeItem(R.id.delete_tag_item);
            }else{
                helper.removeItem(R.id.sort_by_default);
                helper.removeItem(R.id.sort_by_name_item);
            }
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                switch (menuItem.getItemId()){
                    case R.id.sort_by_default:
                        selectTagAdapter.SetTags(Settings.settings.Tags.Sort(Tag::compareTo).Reverse());
                        return true;
                    case R.id.sort_by_name_item:
                        selectTagAdapter.SetTags(Settings.settings.Tags.Sort((t1, t2) -> t1.Name.compareTo(t2.Name)));
                        return true;
                    case R.id.edit_tag_item:
                        InputDialog inputDialog = new InputDialog();
                        inputDialog.SetText(data.Name);
                        inputDialog.SetConfirmListener(text -> {
                            if (text.equals(data.Name)) return;
                            if (Settings.settings.Tags.Any(t -> t.Name.equals(text))){
                                Toast.makeText(getContext(), getString(R.string.tag_exists), Toast.LENGTH_SHORT).show();
                            }else{
                                Tag tag = Settings.settings.Tags.Find(t -> t.Name.equals(data.Name));
                                tag.Name = text;
                                Settings.settings.Foods.ForEach(f -> f.RenameTag(data.Name, text));
                                selectTagAdapter.Set(tag, selectTagAdapter.IndexOf(t -> t.Name.equals(data.Name)));
                            }
                        });
                        inputDialog.showNow(getChildFragmentManager(), InputDialog.TAG);
                        return true;
                    case R.id.delete_tag_item:
                        AskYesNoDialog askDialog = new AskYesNoDialog();
                        askDialog.setMessage(String.format(getString(R.string.delete_tag), data.Name));
                        askDialog.setOnYesListener(v -> {
                            if (data.equals(lastTag)) lastTag = Tag.AllCategoriesTag;
                            selectTagAdapter.Remove(data);
                            Settings.settings.Tags.Remove(data);
                            Settings.settings.Foods.ForEach(food -> food.RemoveTag(data));
                        });
                        askDialog.showNow(getChildFragmentManager(), AskYesNoDialog.TAG);
                        return true;
                }
                return false;
            });
            helper.show();
        }));
        masterView.setAdapter(selectTagAdapter);

        RecyclerView detailView = view.findViewById(R.id.detailView);
        foodAdapter = new FoodAdapter();
        foodAdapter.SetOnFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.SetFood(food);
            dialog.SetFoodEditedListener((before, after) -> SetData());
            dialog.showNow(getChildFragmentManager(), AskYesNoDialog.TAG);
        });
        foodAdapter.SetOnFoodLongClickListener((viewHolder, food) -> EditFood(food));
        detailView.setAdapter(foodAdapter);

        IsLoaded = true;
        SetData();
        if (pendingTag == null) pendingTag = Tag.AllCategoriesTag;
        selectTag(pendingTag);
        selectTagAdapter.HighlightTag(pendingTag);
        pendingTag = null;
        return view;
    }

    public void SetData(){
        if (!IsLoaded) return;
        selectTagAdapter.SetTags(Settings.settings.Tags);
        foodAdapter.SetData(Settings.settings.Foods);
    }

    public void SelectTag(Tag tag){
        pendingTag = tag;
        if (!IsLoaded) return;
        selectTag(tag);
        selectTagAdapter.HighlightTag(tag);
    }

    private void selectTag(Tag tag){
        lastTag = Settings.settings.Tags.Find(tag);
        if (lastTag.IsAllCategoriesTag()){
            title_text_view.setText(Tag.Format(getContext(), R.string.all_categories, Settings.settings.Foods.Count()));
            foodAdapter.Reset();
        }else{
            title_text_view.setText(Tag.Format(getContext(), lastTag));
            foodAdapter.Filter(lastTag);
        }
    }

    private void EditFood(Food food){
        Intent intent = new Intent(getContext(), EditFoodActivity.class);
        if (food != null) intent.putExtra(EditFoodActivity.FOOD, food);
        startActivityForResult(intent, EditFoodActivity.FOOD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            SetData();
        if (!lastTag.IsAllCategoriesTag() && !Settings.settings.Tags.Contains(lastTag)) lastTag = Tag.AllCategoriesTag;
        selectTag(lastTag);
        selectTagAdapter.HighlightTag(lastTag);
    }
}