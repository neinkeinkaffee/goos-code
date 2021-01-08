package auctionsniper;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector {
    private PortfolioListener listener;
    private List<AuctionSniper> snipers = new ArrayList<>();

    public void addPortfolioListener(PortfolioListener listener) {
        this.listener = listener;
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listener.sniperAdded(sniper);
    }
}
