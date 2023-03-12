package personalprojects.seakyluo.randommenu.database.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Page {

    private int pageSize;
    private int pageNum;
    private long total;
    private long totalPage;

    public Page(int pageSize, int pageNum, long total){
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.total = total;
        this.totalPage = total / pageSize;
    }
}
