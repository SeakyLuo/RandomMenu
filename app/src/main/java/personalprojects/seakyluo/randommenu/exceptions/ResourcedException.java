package personalprojects.seakyluo.randommenu.exceptions;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourcedException extends RuntimeException {

    private int resourceId;
    // 把其他地方的异常包一层，也可以不包
    @Nullable
    private Exception original;

    public ResourcedException(int resourceId){
        this.resourceId = resourceId;
    }

}
