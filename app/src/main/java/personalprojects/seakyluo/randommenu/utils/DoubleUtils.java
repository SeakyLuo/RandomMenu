package personalprojects.seakyluo.randommenu.utils;

public class DoubleUtils {

    public static String truncateZero(double d){
        if (d == 0) return "0";
        String num = trim(String.format("%.2f", d), '0');
        return num.endsWith(".") ? num.substring(0, num.length() - 1) : num;
    }

    private static String trim(String str, char target){
        for (int i = str.length() - 1; i >= 0; i--){
            char c = str.charAt(i);
            if (target != c){
                return str.substring(0, i + 1);
            }
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(trim("1.0", '0'));
        System.out.println(trim("1.00", '0'));
        System.out.println(trim("1.01", '0'));
        System.out.println(trim("1.10", '0'));
        System.out.println("===");
        System.out.println(truncateZero(1.0));
        System.out.println(truncateZero(1.20));
        System.out.println(truncateZero(1.25));
        System.out.println(truncateZero(1.24));
        System.out.println(truncateZero(1.255));
        System.out.println(truncateZero(1.26));
    }

}