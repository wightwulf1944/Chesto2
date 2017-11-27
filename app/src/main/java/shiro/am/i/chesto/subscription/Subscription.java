package shiro.am.i.chesto.subscription;

/**
 * Created by Shiro on 11/11/2017.
 */

public interface Subscription {

    static Subscription from(Subscription... subscriptions) {
        return () -> {
            for (Subscription subscription : subscriptions) subscription.unsubscribe();
        };
    }

    void unsubscribe();
}
