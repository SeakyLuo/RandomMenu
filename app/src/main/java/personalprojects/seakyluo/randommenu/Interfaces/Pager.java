package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.database.dao.PagedData;
import personalprojects.seakyluo.randommenu.models.PagerFilter;

public interface Pager<T> {
    PagedData<T> selectByPage(int pageNum, int pageSize, PagerFilter<T> filter);
}
