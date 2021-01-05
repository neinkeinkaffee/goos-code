package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

import static test.endtoend.auctionsniper.ApplicationRunner.SNIPER_XMPP_ID;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auction.reportPrice(1000, 98, "otherBidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperWinsAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auction.reportPrice(1000, 98, "otherBidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction, 1098);
        auction.announceClosed();
        application.showsSniperHasWonAuction(auction, 1098);
    }

    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "otherBidder");
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "otherBidder");
        auction2.hasReceivedBid(521, SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, SNIPER_XMPP_ID);

        application.hasShownSniperIsWinning(auction, 1098);
        application.hasShownSniperIsWinning(auction2, 521);

        auction.announceClosed();
        application.showsSniperHasWonAuction(auction, 1098);
        auction2.announceClosed();
        application.showsSniperHasWonAuction(auction2, 521);
    }

    @After public void stopAuction() {
        auction.stop();
    }

    @After public void stopApplication() {
        application.stop();
    }
}
