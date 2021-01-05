package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private SniperSnapshot snapshot;
    private final SniperListener sniperListener;

    public AuctionSniper(Auction auction, String itemId, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
        notifyChange();
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
}
