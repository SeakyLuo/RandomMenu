package personalprojects.seakyluo.randommenu.interfaces;

import java.util.List;

import personalprojects.seakyluo.randommenu.models.MatchResult;

public interface SearchFragment<T> {

    void setKeyword(String keyword);
    void clear();
    void setSearchResult(List<MatchResult<T>> data);
    String getName();

}
