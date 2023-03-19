package personalprojects.seakyluo.randommenu.utils;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.models.Tag;

public class FoodTagUtils {

    public static List<Tag> guessTags(String food){
        return AutoTagMapperDaoService.selectAll().stream()
                .filter(t -> t.matches(food))
                .flatMap(t -> t.getTags().stream())
                .distinct()
                .collect(Collectors.toList());
    }

}
