package i.am.shiro.chesto.notifier;

import java.util.LinkedList;
import java.util.List;

import i.am.shiro.chesto.listener.Listener;
import i.am.shiro.chesto.subscription.Subscription;

/**
 * Created by Shiro on 11/5/2017.
 */

public abstract class AbstractNotifier<T extends Listener> {

    final List<T> listeners = new LinkedList<>();

    public Subscription addListener(T listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
