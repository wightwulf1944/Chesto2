package shiro.am.i.chesto.notifier;

import shiro.am.i.chesto.listener.Listener1;

/**
 * Created by Shiro on 11/5/2017.
 */

public class Notifier1<T> extends AbstractNotifier<Listener1<T>> {

    public void fireEvent(T t) {
        for (Listener1<T> listener : listeners) {
            listener.onEvent(t);
        }
    }
}
