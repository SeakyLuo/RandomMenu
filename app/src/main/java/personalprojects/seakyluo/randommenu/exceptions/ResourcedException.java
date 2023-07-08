package personalprojects.seakyluo.randommenu.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourcedException extends RuntimeException {

    private int resourceId;

}
