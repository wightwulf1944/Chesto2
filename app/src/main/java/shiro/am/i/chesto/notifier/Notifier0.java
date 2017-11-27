package shiro.am.i.chesto.notifier;

import shiro.am.i.chesto.listener.Listener0;

/**
 * Created by Shiro on 11/5/2017.
 */

public class Notifier0 extends AbstractNotifier<Listener0> {

    public void fireEvent() {
        for (Listener0 listener : listeners) {
            listener.onEvent();
        }
    }
}
