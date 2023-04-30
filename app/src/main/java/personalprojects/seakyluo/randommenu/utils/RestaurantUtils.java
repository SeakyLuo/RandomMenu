package personalprojects.seakyluo.randommenu.utils;

import android.content.ClipData;
import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantUtils {

    public static RestaurantVO buildFromImages(Context context, ClipData clipData){
        List<ConsumeRecordVO> records = buildRecords(context, clipData);
        List<ConsumeRecordVO> reducedRecords = reduceConsumeRecords(records);
        RestaurantVO vo = new RestaurantVO();
        vo.setAddressList(reducedRecords.stream()
                .map(ConsumeRecordVO::getAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        vo.setRecords(reducedRecords);
        return vo;
    }

    private static List<ConsumeRecordVO> buildRecords(Context context, ClipData clipData){
        List<ConsumeRecordVO> records = new ArrayList<>();
        int count = clipData.getItemCount();
        for (int i = 0; i < count; i++){
            Uri uri = clipData.getItemAt(i).getUri();
            String filename = ImageUtils.newImageFileName(i);
            if (!ImageUtils.saveImage(context, uri, filename)){
                continue;
            }
            ConsumeRecordVO record = new ConsumeRecordVO();
            RestaurantFoodVO food = new RestaurantFoodVO();
            food.setCover(filename);
            food.setImages(Lists.newArrayList(filename));
            record.setFoods(Lists.newArrayList(food));
            record.setEaters(new ArrayList<>());
            records.add(record);

            String path = FileUtils.getPath(context, uri);
            ExifInterface exifInterface;
            try {
                exifInterface = new ExifInterface(path);
            } catch (Exception e) {
                Log.w("buildRestaurantFromImages", "ExifInterface", e);
                exifInterface = null;
            }
            record.setConsumeTime(getConsumeTime(exifInterface, path));
            record.setAddress(getAddress(exifInterface, context));
        }
        return records;
    }

    private static long getConsumeTime(ExifInterface exifInterface, String path){
        String dateTime = exifInterface == null ? null : exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        try {
            if (dateTime == null){
                return Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime().toMillis();
            } else {
                return new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(dateTime).getTime();
            }
        } catch (Exception e){
            Log.w("buildRestaurantFromImages", "consumeTime", e);
            return 0;
        }
    }

    private static AddressVO getAddress(ExifInterface exifInterface, Context context){
        if (exifInterface == null){
            return null;
        }
        String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String lonValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String lonRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        return AddressUtils.getAddress(context, latValue, latRef, lonValue, lonRef);
    }

    private static List<ConsumeRecordVO> reduceConsumeRecords(List<ConsumeRecordVO> records){
        records.sort(Comparator.comparing(ConsumeRecordVO::getConsumeTime));
        List<ConsumeRecordVO> ret = new ArrayList<>();
        for (ConsumeRecordVO record : records){
            boolean reduced = false;
            for (int i = ret.size() - 1; i >= 0; i--){
                ConsumeRecordVO target = ret.get(i);
                if (canBeReduced(record, target)){
                    ret.set(i, merge(record, target));
                    reduced = true;
                    break;
                }
            }
            if (!reduced){
                ret.add(record);
            }
        }
        for (int i = 0; i < ret.size(); i++){
            ret.get(i).setIndex(i);
        }
        return ret;
    }

    // 同一地址，同一天或消费间隔不超过3小时
    private static boolean canBeReduced(ConsumeRecordVO source, ConsumeRecordVO target){
        if (!Objects.equals(source.getAddress(), target.getAddress())){
            return false;
        }
        long sourceConsumeTime = source.getConsumeTime();
        long targetConsumeTime = target.getConsumeTime();
        Calendar sourceCal = Calendar.getInstance(), targetCal = Calendar.getInstance();
        sourceCal.setTimeInMillis(sourceConsumeTime);
        targetCal.setTimeInMillis(targetConsumeTime);
        return DateUtils.isSameDay(sourceCal, targetCal) || Math.abs(sourceConsumeTime - targetConsumeTime) <= 10800000;
    }

    private static ConsumeRecordVO merge(ConsumeRecordVO source, ConsumeRecordVO target){
        target.getFoods().addAll(source.getFoods());
        target.setConsumeTime(Math.min(source.getConsumeTime(), target.getConsumeTime()));
        return target;
    }
}
