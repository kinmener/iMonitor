

package locks;
import sun.misc.Unsafe;
import java.lang.reflect.Field;
public class Util {
    public static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class uc = Unsafe.class;
            Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return unsafe;
    }
}
