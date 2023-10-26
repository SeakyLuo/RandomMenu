package personalprojects.seakyluo.randommenu.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Locale;

import personalprojects.seakyluo.randommenu.models.vo.AddressVO;

public class AddressUtils {

    public static AddressVO getAddress(ExifInterface exifInterface, Context context){
        if (exifInterface == null){
            return null;
        }
        String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String lonValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String lonRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        return getAddress(context, latValue, latRef, lonValue, lonRef);
    }

    public static AddressVO getAddress(Context context, String latVal, String latRef, String lonVal, String lonRef) {
        if (latVal == null || latRef == null || lonVal == null || lonRef == null){
            return null;
        }
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        double latitude = convertRationalLatLon(latVal, latRef), longitude = convertRationalLatLon(lonVal, lonRef);
        List<Address> locationList;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (CollectionUtils.isEmpty(locationList)){
            return null;
        }
        Address address = locationList.get(0);
        return convertAddress(address);
    }

    private static AddressVO convertAddress(Address src){
        AddressVO dst = new AddressVO();
        dst.setProvince(src.getAdminArea());
        dst.setCity(src.getLocality());
        dst.setCounty(src.getSubLocality());
        dst.setAddress(src.getThoroughfare());
        return dst;
    }

    private static double convertRationalLatLon(String rationalString, String ref) {
        String[] parts = rationalString.split(",");
        String[] pair;
        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim())/ Double.parseDouble(pair[1].trim());

        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim())/ Double.parseDouble(pair[1].trim());

        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim())/ Double.parseDouble(pair[1].trim());

        double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if ((ref.equals("S") || ref.equals("W"))) {
            return -result;
        }
        return result;
    }

}
