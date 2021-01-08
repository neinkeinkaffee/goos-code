package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionHouse;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;
import static test.endtoend.auctionsniper.ApplicationRunner.SNIPER_ID;
import static test.endtoend.auctionsniper.ApplicationRunner.SNIPER_PASSWORD;
import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class XMPPAuctionTest {
    private XMPPConnection connection = new XMPPConnection(XMPP_HOSTNAME);
    private FakeAuctionServer server = new FakeAuctionServer("item-54321");

    @Before
    public void openAuction() throws Exception {
        server.startSellingItem();
    }

    @After
    public void closeAuction() {
        server.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        connection.connect();
        AuctionHouse auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
        Auction auction = auctionHouse.auctionFor(server.getItemId());
        auction.addAuctionEventListeners(auctionClosedListener(auctionWasClosed));

        auction.join();
        server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        server.announceClosed();

        assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
    }

    private AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            public void currentPrice(int price, int increment, PriceSource fromOtherBidder) {}
        };
    }
}
