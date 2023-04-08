package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SearchHistoryDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SearchHistoryMapper;
import personalprojects.seakyluo.randommenu.enums.FoodClass;

public class SearchHistoryDaoService {

    public static void insert(FoodClass searchType, String keyword){
        SearchHistoryMapper mapper = AppDatabase.instance.searchHistoryMapper();
        SearchHistoryDAO dao = new SearchHistoryDAO();
        dao.setSearchType(searchType.name());
        dao.setKeyword(keyword);
        mapper.insert(dao);
    }

    public static void delete(FoodClass searchType, String keyword){
        SearchHistoryMapper mapper = AppDatabase.instance.searchHistoryMapper();
        mapper.delete(searchType.name(), keyword);
    }

    public static List<String> list(FoodClass searchType){
        SearchHistoryMapper mapper = AppDatabase.instance.searchHistoryMapper();
        List<SearchHistoryDAO> list = mapper.list(searchType.name());
        return list.stream().map(SearchHistoryDAO::getKeyword).collect(Collectors.toList());
    }

}
