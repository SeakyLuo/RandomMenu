package personalprojects.seakyluo.randommenu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EaterCount {
    private String eater;
    private long count;
}