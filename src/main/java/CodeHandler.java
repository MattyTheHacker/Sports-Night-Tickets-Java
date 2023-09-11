import java.util.concurrent.ConcurrentHashMap;

public class CodeHandler {

    private static final ConcurrentHashMap<String, String> codes = new ConcurrentHashMap<>();

    private static boolean isValid(String code, String societyName){
        // check neither are null
        if (societyName == null || code == null) {
            System.out.println("[ERROR] Null value passed: " + societyName + ", " + code);
            return false;
        }

        // check code is 3 letters followed by 3 numbers
        if (!code.matches("[a-z]{3}[0-9]{3}")) {
            System.out.println("[ERROR] Invalid code: " + code);
            return false;
        }

        // check code is not already in use
        if (codes.containsKey(code)) {
            System.out.println("[ERROR] Code already in use: " + code);
            return false;
        }

        return true;
    }

    public static boolean insertCode(String code, String societyName) {
        if (isValid(code, societyName)) {
            codes.put(code, societyName);
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeCode(String code) {
        if (codes.containsKey(code)) {
            codes.remove(code);
            return true;
        } else {
            return false;
        }
    }

    public static void printCodes() {
        System.out.println(codes);
    }

    public static ConcurrentHashMap<String, String> getCodes() {
        return codes;
    }
}
