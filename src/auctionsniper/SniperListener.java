package auctionsniper;

import java.lang.reflect.InvocationTargetException;

public interface SniperListener {
    void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
