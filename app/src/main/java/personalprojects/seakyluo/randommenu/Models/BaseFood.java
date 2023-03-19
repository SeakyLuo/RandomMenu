package personalprojects.seakyluo.randommenu.models;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import personalprojects.seakyluo.randommenu.constants.EmojiConstant;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

@Data
public class BaseFood {

    private String name;
    private String cover;
    private List<String> images;
    private List<Tag> tags;
    private String note;
    private boolean isFavorite;
    private FoodClass foodClass;
    private Object source;

    public boolean hasImage() {
        return images.size() > 0;
    }

    public SelfMadeFood asSelfMadeFood(){
        return (SelfMadeFood) source;
    }

    public RestaurantFoodVO asRestaurantFood(){
        return (RestaurantFoodVO) source;
    }

    public static BaseFood from(SelfMadeFood src){
        BaseFood dst = new BaseFood();
        dst.setName(src.getName());
        dst.setCover(src.getCover());
        dst.setImages(src.getImages());
        dst.setTags(src.getTags());
        dst.setNote(src.getNote());
        dst.setFavorite(src.isFavorite());
        dst.setFoodClass(FoodClass.SELF_MADE);
        dst.setSource(src);
        return dst;
    }

    public static BaseFood from(RestaurantFoodVO src){
        BaseFood dst = new BaseFood();
        dst.setName(src.getName());
        String cover = src.getCover();
        dst.setCover(cover);
        dst.setImages(src.getImages());
        List<String> notes = new ArrayList<>();
        double price = src.getPrice();
        if (price != 0){
            notes.add(EmojiConstant.TOTAL_COST + " 价格：" + DoubleUtils.truncateZero(price));
        }
        String comment = src.getComment();
        if (StringUtils.isNotEmpty(comment)){
            notes.add(EmojiConstant.COMMENT + " 评价：" + comment);
        }
        dst.setNote(String.join("\n", notes));
        dst.setFoodClass(FoodClass.RESTAURANT);
        dst.setSource(src);
        return dst;
    }
}
