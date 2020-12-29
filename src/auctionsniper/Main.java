package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_JOINING = "Joining";
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ITEM_ID = 3;
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;
    private Chat notToBeGcd;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connectTo(args[ARG_HOSTNAME],
                                              args[ARG_USERNAME],
                                              args[ARG_PASSWORD]);
        main.joinAuction(connection, auctionId(args[ITEM_ID], connection));
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
        Chat chat = connection.getChatManager().createChat(
                itemId,
                new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    ui.showStatus(STATUS_LOST);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        this.notToBeGcd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
