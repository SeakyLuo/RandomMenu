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

import java.util.Comparator;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.FoodTagDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.activities.impl.EditSelfMadeFoodActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SelfFoodAdapter;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.SearchActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SelectTagAdapter;
import personalprojects.seakyluo.randommenu.services.FoodTagService;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;

import static android.app.Activity.RESULT_OK;

public class NavigationFragment extends Fragment {
    public static final String TAG = "NavigationFragment";
    private TextView title_text_view;
    private SelectTagAdapter selectTagAdapter;
    private SelfFoodAdapter foodAdapter;
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
            IsLoaded = false;
            setData();
            selectTag(selectTagAdapter.getSelectedTag());
            IsLoaded = true;
            swipeRefreshLayout.setRefreshing(false);
        });
        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchActivity.class));
            getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        });
        RecyclerView masterView = view.findViewById(R.id.masterView);
        masterView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        selectTagAdapter = new SelectTagAdapter((viewHolder, tag) -> selectTag(tag));
        selectTagAdapter.setContext(getContext());
        selectTagAdapter.setLongClickListener(this::showTagMenu);
        masterView.setAdapter(selectTagAdapter);

        RecyclerView detailView = view.findViewById(R.id.detailView);
        foodAdapter = new SelfFoodAdapter();
        foodAdapter.setFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setSelfFoodId(food.getId());
            dialog.setFoodEditedListener(after -> foodAdapter.updateFood(after));
            dialog.setFoodLikedListener(after -> foodAdapter.setFoodLiked(after));
            dialog.showNow(getChildFragmentManager(), FoodCardDialog.TAG);
        });
        foodAdapter.setFoodLongClickListener((viewHolder, food) -> editFood(food, false));
        detailView.setAdapter(foodAdapter);
        view.findViewById(R.id.navigation_toolbar).setOnClickListener(v -> detailView.smoothScrollToPosition(0));

        setData();
        IsLoaded = true;
        if (pendingTag == null) pendingTag = Tag.AllCategoriesTag;
        selectTag(pendingTag);
        selectTagAdapter.highlightTag(pendingTag);
        pendingTag = null;
        return view;
    }

    private void showTagMenu(CustomAdapter<Tag>.CustomViewHolder viewHolder, Tag data){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.long_click_tag_menu, getContext(), viewHolder.getView());
        if (data.isAllCategoriesTag()){
            helper.removeItems(R.id.edit_tag_item);
            helper.removeItems(R.id.delete_tag_item);
        } else {
            helper.removeItems(R.id.sort_by_default);
            helper.removeItems(R.id.sort_by_name_item);
        }
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.sort_by_default:
                    selectTagAdapter.setTags(FoodTagDaoService.selectAll());
                    return true;
                case R.id.sort_by_name_item:
                    selectTagAdapter.setTags(FoodTagDaoService.selectAll().stream()
                            .sorted(Comparator.comparing(Tag::getName))
                            .collect(Collectors.toList()));
                    return true;
                case R.id.edit_tag_item:
                    InputDialog inputDialog = new InputDialog();
                    inputDialog.setText(data.getName());
                    inputDialog.setConfirmListener(text -> {
                        if (text.equals(data.getName())) return;
                        if (FoodTagDaoService.selectByName(text) != null){
                            Toast.makeText(getContext(), getString(R.string.tag_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            data.setName(text);
                            FoodTagService.update(data);
                            selectTagAdapter.set(data, selectTagAdapter.indexOf(t -> t.getId() == data.getId()));
                        }
                    });
                    inputDialog.showNow(getChildFragmentManager(), InputDialog.TAG);
                    return true;
                case R.id.delete_tag_item:
                    AskYesNoDialog askDialog = new AskYesNoDialog();
                    askDialog.setMessage(String.format(getString(R.string.delete_tag), data.getName()));
                    askDialog.setYesListener(v -> {
                        if (data.equals(lastTag)) lastTag = Tag.AllCategoriesTag;
                        selectTagAdapter.remove(data);
                        FoodTagService.delete(data);
                    });
                    askDialog.showNow(getChildFragmentManager(), AskYesNoDialog.TAG);
                    return true;
            }
            return false;
        });
        helper.show();
    }

    public void setData(){
        if (IsLoaded) return;
        selectTagAdapter.setTags(FoodTagDaoService.selectAll());
        foodAdapter.setData(SelfFoodDaoService.selectAll());
    }

    public void selectTagAndHighlight(Tag tag){
        pendingTag = tag;
        if (!IsLoaded) return;
        selectTag(tag);
        selectTagAdapter.highlightTag(tag);
    }

    private void selectTag(Tag tag){
        if (tag.isAllCategoriesTag()){
            lastTag = Tag.AllCategoriesTag;
            title_text_view.setText(Tag.format(getContext(), R.string.all_categories, SelfFoodDaoService.count()));
            foodAdapter.setData(SelfFoodDaoService.selectAll());
        } else {
            lastTag = FoodTagDaoService.selectById(tag.getId());
            title_text_view.setText(Tag.format(getContext(), lastTag));
            foodAdapter.setData(SelfMadeFoodService.selectByTag(lastTag));
        }
    }

    private void editFood(SelfMadeFood food, boolean isDraft){
        Intent intent = new Intent(getContext(), EditSelfMadeFoodActivity.class);
        intent.putExtra(EditSelfMadeFoodActivity.FOOD_ID, food.getId());
        intent.putExtra(EditSelfMadeFoodActivity.IS_DRAFT, isDraft);
        startActivityForResult(intent, ActivityCodeConstant.FOOD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) setData();
        if (!lastTag.isAllCategoriesTag() && FoodTagDaoService.selectById(lastTag.getId()) == null)
            selectTagAdapter.highlightTag(lastTag = Tag.AllCategoriesTag);
        selectTag(lastTag);
    }
}