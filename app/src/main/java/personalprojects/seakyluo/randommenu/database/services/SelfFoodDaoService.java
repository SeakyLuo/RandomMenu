package personalprojects.seakyluo.randommenu.database.services;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodMapper;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodDaoService {

    private static final String TAG_JOINER = ",";

    public static void insert(SelfFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        Long id = selfFoodMapper.insert(convert(food));
        food.setId(id);
    }

    public static void delete(SelfFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.delete(convert(food));
    }

    public static void update(SelfFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.update(convert(food));
    }

    public static SelfFood selectById(long id){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectById(id));
    }

    public static List<SelfFood> selectByIds(List<Long> ids){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectByIds(ids).stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static SelfFood selectByName(String name){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectByName(name));
    }

    public static List<SelfFood> selectAll(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectAll().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static List<SelfFood> selectNonHidden(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectNonHidden().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static long count(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.count();
    }

    public static List<SelfFood> getFavoriteFoods(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectFavorite(true).stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static long countFavoriteFoods(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.countFavorite(true);
    }

    public static void decrementHideCount(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.decrementHideCount();
    }

    private static SelfFoodDAO convert(SelfFood src){
        if (src == null){
            return null;
        }
        SelfFoodDAO dst = new SelfFoodDAO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setNote(src.getNote());
        dst.setTags(src.getTags().stream().map(Tag::getName).collect(Collectors.joining(TAG_JOINER)));
        dst.setFavorite(src.isFavorite());
        dst.setFoodCover(src.getCover());
        dst.setDateAdded(src.getDateAdded());
        dst.setHideCount(src.getHideCount());
        return dst;
    }

    private static SelfFood convert(SelfFoodDAO src){
        if (src == null){
            return null;
        }
        SelfFood dst = new SelfFood();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setNote(src.getNote());
        String tags = src.getTags();
        if (StringUtils.isNotEmpty(tags)){
            dst.setTags(Arrays.stream(tags.split(TAG_JOINER)).map(Tag::new).collect(Collectors.toList()));
        }
        dst.setFavorite(src.isFavorite());
        dst.setCover(src.getFoodCover());
        dst.setDateAdded(src.getDateAdded());
        dst.setHideCount(src.getHideCount());
        return dst;
    }
}
