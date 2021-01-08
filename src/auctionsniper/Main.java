package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.UserRequestListener;
import auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private MainWindow mainWindow;
    private List<Auction> notToBeGcd = new ArrayList<>();
    private final SnipersTableModel snipers = new SnipersTableModel();

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse =
                XMPPAuctionHouse.connect(args[ARG_HOSTNAME],
                    args[ARG_USERNAME],
                    args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(XMPPAuctionHouse auctionHouse) {
        mainWindow.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                Auction auction = auctionHouse.auctionFor(itemId);
                notToBeGcd.add(auction);
                auction.addAuctionEventListeners(new AuctionSniper(auction, itemId, new SwingThreadSniperListener(snipers)));
                auction.join();
            }
        });
    }

    private void disconnectWhenUICloses(XMPPAuctionHouse auctionHouse) {
        mainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                auctionHouse.getConnection().disconnect();
            }
        });
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                mainWindow = new MainWindow(snipers);
            }
        });
    }

}
