package personalprojects.seakyluo.randommenu.database.dao;

import java.util.List;

import lombok.Data;

@Data
public class PagedData<T> {

    private Page page;
    private List<T> data;

}
