package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.database.dao.PagedData;

public interface Pager<T> {
    PagedData<T> selectByPage(int pageNum, int pageSize);
}
