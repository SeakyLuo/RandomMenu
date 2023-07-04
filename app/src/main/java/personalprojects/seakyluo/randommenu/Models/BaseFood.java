package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.constants.EmojiConstant;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

@NoArgsConstructor
@Data
public class BaseFood implements Parcelable {

    private long id;
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
        dst.setId(src.getId());
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
        dst.setId(src.getId());
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

    protected BaseFood(Parcel in) {
        id = in.readLong();
        name = in.readString();
        name = in.readString();
        cover = in.readString();
        images = in.createStringArrayList();
        tags = in.createTypedArrayList(Tag.CREATOR);
        note = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<BaseFood> CREATOR = new Creator<BaseFood>() {
        @Override
        public BaseFood createFromParcel(Parcel in) {
            return new BaseFood(in);
        }

        @Override
        public BaseFood[] newArray(int size) {
            return new BaseFood[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeStringList(images);
        dest.writeTypedList(tags);
        dest.writeString(note);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
