package personalprojects.seakyluo.randommenu.fragments;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.impl.SearchFoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.SearchFragment;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.models.MatchResult;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.services.FoodTagService;

public class SearchFoodListFragment extends BaseFoodListFragment<SearchFoodListAdapter> implements SearchFragment<SelfMadeFood> {
    public static final String TAG = "SearchFoodListFragment";
    @Getter
    @Setter
    private String name;

    public SearchFoodListFragment(){
        adapter = new SearchFoodListAdapter();
    }

    public void setShowTags(boolean showTags) {
        adapter.setShowTags(showTags);
    }
    public void setShowNote(boolean showNote) {
        adapter.setShowNote(showNote);
    }
    public void setKeyword(String keyword) {
        adapter.setKeyword(keyword);
    }

    @Override
    public void setSearchResult(List<MatchResult<SelfMadeFood>> data) {
        setData(sortFoods(data));
    }

    private static List<SelfMadeFood> sortFoods(List<MatchResult<SelfMadeFood>> foods){
        return foods.stream().sorted(Comparator.comparing(MatchResult<SelfMadeFood>::getPoints).reversed())
                .map(MatchResult::getData)
                .peek(f -> f.setTags(FoodTagService.selectByFood(f.getId())))
                .collect(Collectors.toList());
    }
}
