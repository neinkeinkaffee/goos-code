package test.endtoend.auctionsniper;

import auctionsniper.Main;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class FakeAuctionServer {
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";
    public static final String AUCTION_RESOURCE = "Auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private final SimpleMessageListener messageListener = new SimpleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        currentChat = chat;
                        chat.addMessageListener(messageListener);
                    }
                }
        );
    }

    public void hasReceivedJoinRequestFromSniper(String sniperId) throws Exception {
        receivesAMessageMatching(sniperId, Main.JOIN_COMMAND_FORMAT);
    }

    public void hasReceivedBid(int bid, String sniperId) throws Exception {
        receivesAMessageMatching(sniperId, String.format(Main.BID_COMMAND_FORMAT, bid));
    }

    private void receivesAMessageMatching(String sniperId, String format) throws InterruptedException {
        messageListener.receivesAMessage(equalTo(format));
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void reportPrice(int price, int increment, String bidder) throws Exception {
        currentChat.sendMessage(String.format("SOLVersion: 1.1; Event: PRICE;" +
                "CurrentPrice: %d; Increment: %d; Bidder: %s;",
                price, increment, bidder));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }
}
