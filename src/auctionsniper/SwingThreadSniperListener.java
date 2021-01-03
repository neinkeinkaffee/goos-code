package auctionsniper;

import auctionsniper.ui.SnipersTableModel;

public class SwingThreadSniperListener implements SniperListener {
    private SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStateChanged(snapshot);
    }
}
