package personalprojects.seakyluo.randommenu.utils;

import android.content.ClipData;
import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.google.common.collect.Lists;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantUtils {

    public static RestaurantVO buildFromImages(Context context, ClipData clipData){
        int count = clipData.getItemCount();
        if (count == 0) return null;
        Set<AddressVO> addressSet = new HashSet<>();
        ConsumeRecordVO record = new ConsumeRecordVO();
        List<RestaurantFoodVO> foods = new ArrayList<>();
        for (int i = 0; i < count; i++){
            Uri uri = clipData.getItemAt(i).getUri();
            String fileName = ImageUtils.newImageFileName(i);
            if (!ImageUtils.saveImage(context, uri, fileName)){
                continue;
            }
            RestaurantFoodVO food = new RestaurantFoodVO();
            food.setConsumeRecordIndex(0);
            foods.add(food);
            food.setPictureUri(fileName);
            String path = FileUtils.getPath(context, uri);
            ExifInterface exifInterface;
            try {
                exifInterface = new ExifInterface(path);
            } catch (Exception e) {
                Log.w("buildRestaurantFromImages", "ExifInterface", e);
                continue;
            }
            try {
                String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                long consumeTime;
                if (dateTime == null){
                    consumeTime = Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime().toMillis();;
                } else {
                    consumeTime = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss").parse(dateTime).getTime();
                }
                if (record.getConsumeTime() == 0){
                    record.setConsumeTime(consumeTime);
                } else {
                    record.setConsumeTime(Math.min(record.getConsumeTime(), consumeTime));
                }
            } catch (Exception e){
                Log.w("buildRestaurantFromImages", "consumeTime", e);
            }
            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lonValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lonRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            AddressVO address = AddressUtils.getAddress(context, latValue, latRef, lonValue, lonRef);
            if (address != null){
                addressSet.add(address);
                record.setAddress(address);
            }
        }
        record.setFoods(foods);
        RestaurantVO vo = new RestaurantVO();
        vo.setAddressList(new ArrayList<>(addressSet));
        vo.setRecords(Lists.newArrayList(record));
        return vo;
    }
}
