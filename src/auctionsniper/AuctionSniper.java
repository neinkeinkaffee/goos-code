package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private SniperSnapshot snapshot;
    private SniperListener sniperListener;

    public AuctionSniper(Auction auction, String itemId) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void auctionClosed() {
       snapshot = snapshot.closed();
       notifyChange();
    }

    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch(priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }
}
