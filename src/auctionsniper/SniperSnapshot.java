package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public SniperState state;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(itemId)
            .append(lastPrice)
            .append(lastBid)
            .append(state)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SniperSnapshot) {
            final SniperSnapshot other = (SniperSnapshot) obj;
            return new EqualsBuilder()
                .append(itemId, other.itemId)
                .append(lastPrice, other.lastPrice)
                .append(lastBid, other.lastBid)
                .append(state, other.state)
                .isEquals();
        } else {
            return false;
        }
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    public boolean isForSameItemAs(SniperSnapshot snapshot) {
        return this.itemId.equals(snapshot.itemId);
    }
}
