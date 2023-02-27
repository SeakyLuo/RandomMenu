package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.activities.impl.EditFoodActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.FoodAdapter;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.SearchActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SelectTagAdapter;

import static android.app.Activity.RESULT_OK;

public class NavigationFragment extends Fragment {
    public static final String TAG = "NavigationFragment";
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
        fab.setOnClickListener(v -> editFood(Settings.settings.FoodDraft, true));
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            setData();
            selectTag(selectTagAdapter.GetSelectedTag());
            swipeRefreshLayout.setRefreshing(false);
        });
        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchActivity.class));
            getActivity().overridePendingTransition(R.anim.push_right_in, 0);
        });
        RecyclerView masterView = view.findViewById(R.id.masterView);
        masterView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        selectTagAdapter = new SelectTagAdapter((viewHolder, tag) -> selectTag(tag));
        selectTagAdapter.setContext(getContext());
        selectTagAdapter.setLongClickListener(((viewHolder, data) -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.long_click_tag_menu, getContext(), viewHolder.getView());
            if (data.isAllCategoriesTag()){
                helper.removeItems(R.id.edit_tag_item);
                helper.removeItems(R.id.delete_tag_item);
            }else{
                helper.removeItems(R.id.sort_by_default);
                helper.removeItems(R.id.sort_by_name_item);
            }
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                switch (menuItem.getItemId()){
                    case R.id.sort_by_default:
                        selectTagAdapter.setTags(Settings.settings.Tags.sorted(Tag::compareTo).reverse());
                        return true;
                    case R.id.sort_by_name_item:
                        selectTagAdapter.setTags(Settings.settings.Tags.sorted((t1, t2) -> t1.Name.compareTo(t2.Name)));
                        return true;
                    case R.id.edit_tag_item:
                        InputDialog inputDialog = new InputDialog();
                        inputDialog.SetText(data.Name);
                        inputDialog.SetConfirmListener(text -> {
                            if (text.equals(data.Name)) return;
                            if (Settings.settings.Tags.any(t -> t.Name.equals(text))){
                                Toast.makeText(getContext(), getString(R.string.tag_exists), Toast.LENGTH_SHORT).show();
                            }else{
                                Tag tag = Settings.settings.Tags.first(t -> t.Name.equals(data.Name));
                                tag.Name = text;
                                Settings.settings.Foods.ForEach(f -> f.renameTag(data.Name, text));
                                selectTagAdapter.set(tag, selectTagAdapter.indexOf(t -> t.Name.equals(data.Name)));
                                Helper.save();
                            }
                        });
                        inputDialog.showNow(getChildFragmentManager(), InputDialog.TAG);
                        return true;
                    case R.id.delete_tag_item:
                        AskYesNoDialog askDialog = new AskYesNoDialog();
                        askDialog.setMessage(String.format(getString(R.string.delete_tag), data.Name));
                        askDialog.setYesListener(v -> {
                            if (data.equals(lastTag)) lastTag = Tag.AllCategoriesTag;
                            selectTagAdapter.remove(data);
                            Settings.settings.Tags.remove(data);
                            Settings.settings.Foods.ForEach(food -> food.removeTag(data));
                            Helper.save();
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
        foodAdapter.setFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setFood(food);
            dialog.setFoodEditedListener((before, after) -> setData());
            dialog.setFoodLikedListener(((before, after) -> foodAdapter.setFoodLiked(after)));
            dialog.showNow(getChildFragmentManager(), FoodCardDialog.TAG);
        });
        foodAdapter.setFoodLongClickListener((viewHolder, food) -> editFood(food, false));
        detailView.setAdapter(foodAdapter);
        view.findViewById(R.id.navigation_toolbar).setOnClickListener(v -> detailView.smoothScrollToPosition(0));

        IsLoaded = true;
        setData();
        if (pendingTag == null) pendingTag = Tag.AllCategoriesTag;
        selectTag(pendingTag);
        selectTagAdapter.highlightTag(pendingTag);
        pendingTag = null;
        return view;
    }

    public void setData(){
        if (!IsLoaded) return;
        selectTagAdapter.setTags(Settings.settings.Tags);
        foodAdapter.setData(Settings.settings.Foods);
    }

    public void SelectTag(Tag tag){
        pendingTag = tag;
        if (!IsLoaded) return;
        selectTag(tag);
        selectTagAdapter.highlightTag(tag);
    }

    private void selectTag(Tag tag){
        if (tag.isAllCategoriesTag()){
            lastTag = Tag.AllCategoriesTag;
            title_text_view.setText(Tag.format(getContext(), R.string.all_categories, Settings.settings.Foods.size()));
            foodAdapter.reset();
        }else{
            lastTag = Settings.settings.Tags.first(tag);
            title_text_view.setText(Tag.format(getContext(), lastTag));
            foodAdapter.filter(lastTag);
        }
    }

    private void editFood(Food food, boolean isDraft){
        Intent intent = new Intent(getContext(), EditFoodActivity.class);
        intent.putExtra(EditFoodActivity.FOOD, food);
        intent.putExtra(EditFoodActivity.IS_DRAFT, isDraft);
        startActivityForResult(intent, ActivityCodeConstant.FOOD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) setData();
        if (!lastTag.isAllCategoriesTag() && !Settings.settings.Tags.contains(lastTag))
            selectTagAdapter.highlightTag(lastTag = Tag.AllCategoriesTag);
        selectTag(lastTag);
    }
}