package personalprojects.seakyluo.randommenu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MatchResult<T> {
    protected T data;
    protected int points;
}
