package personalprojects.seakyluo.randommenu.database.services;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfMadeFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodMapper;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodDaoService {

    private static final String TAG_JOINER = ",";

    public static void insert(SelfMadeFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        Long id = selfFoodMapper.insert(convert(food));
        food.setId(id);
    }

    public static void delete(SelfMadeFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.delete(convert(food));
    }

    public static void update(SelfMadeFood food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.update(convert(food));
    }

    public static SelfMadeFood selectById(long id){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectById(id));
    }

    public static List<SelfMadeFood> selectByIds(List<Long> ids){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectByIds(ids).stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static SelfMadeFood selectByName(String name){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectByName(name));
    }

    public static List<SelfMadeFood> selectAll(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectAll().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static List<SelfMadeFood> selectNonHidden(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectNonHidden().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static long count(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.count();
    }

    public static List<SelfMadeFood> getFavoriteFoods(){
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

    private static SelfMadeFoodDAO convert(SelfMadeFood src){
        if (src == null){
            return null;
        }
        SelfMadeFoodDAO dst = new SelfMadeFoodDAO();
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

    private static SelfMadeFood convert(SelfMadeFoodDAO src){
        if (src == null){
            return null;
        }
        SelfMadeFood dst = new SelfMadeFood();
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
