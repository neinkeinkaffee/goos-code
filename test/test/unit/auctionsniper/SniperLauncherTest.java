package test.unit.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class SniperLauncherTest {
    private Mockery context = new Mockery();
    private final States auctionState = context.states("auction state").startsAs("not joined");

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() {
        final String itemId = "item 123";
        final Auction auction = context.mock(Auction.class);
        final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
        final SniperCollector sniperCollector = context.mock(SniperCollector.class);
        SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId); will(returnValue(auction));
            oneOf(auction).addAuctionEventListeners(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
            one(auction).join(); then(auctionState.is("joined"));
        }});

        launcher.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "a sniper for item with id", "was") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}
