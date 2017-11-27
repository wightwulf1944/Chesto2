package shiro.am.i.chesto.listener;

/**
 * Created by Subaru Tashiro on 5/18/2017.
 * <p>
 * Generic listener with 1 parameter
 */

public interface Listener1<T> extends Listener {

    void onEvent(T t);
}
