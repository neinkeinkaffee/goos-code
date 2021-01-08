package auctionsniper;

import auctionsniper.ui.UserRequestListener;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector sniperCollector) {
        this.auctionHouse = auctionHouse;
        this.collector = sniperCollector;
    }

    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(auction, itemId);
        collector.addSniper(sniper);
        auction.addAuctionEventListeners(sniper);
        auction.join();
    }
}
