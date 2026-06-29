import java.util.ArrayList;
import java.util.Scanner;

class Stock {
    String symbol; // e.g., "AAPL", "TSLA"
    String name; // e.g., "Apple", "Tesla"
    double price; // Current market price

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }
}

class PortfolioItem {
    String symbol;
    int quantity; // How many shares you own
    double averagePrice; // Price you bought them at

    public PortfolioItem(String symbol, int quantity, double averagePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }
}

class Transaction {
    String type; // "BUY" or "SELL"
    String symbol;
    int quantity;
    double pricePerShare;

    public Transaction(String type, String symbol, int quantity, double pricePerShare) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
    }
}

public class SimpleStockPlatform {
    // Market lists and user account tracking
    private static ArrayList<Stock> marketStocks = new ArrayList<>();
    private static ArrayList<PortfolioItem> myPortfolio = new ArrayList<>();
    private static ArrayList<Transaction> history = new ArrayList<>();

    private static double userBalance = 10000.0; // Everyone starts with Rs. 10,000 cash

    public static void main(String[] args) {
        // Setup default available stocks in the market
        marketStocks.add(new Stock("AAPL", "Apple Inc.", 150.0));
        marketStocks.add(new Stock("TSLA", "Tesla Motor", 200.0));
        marketStocks.add(new Stock("GOOG", "Google LLC", 100.0));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== STOCK TRADING PLATFORM ===");
            System.out.println("Your Cash Balance: Rs. " + String.format("%.2f", userBalance));
            System.out.println("1. View Market Data (Live Prices)");
            System.out.println("2. Buy a Stock");
            System.out.println("3. Sell a Stock");
            System.out.println("4. View My Portfolio Performance");
            System.out.println("5. View Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                showMarket();
            } else if (choice.equals("2")) {
                System.out.print("Enter Stock Symbol to BUY (e.g., AAPL): ");
                String symbol = scanner.nextLine().toUpperCase().trim();
                System.out.print("Enter Quantity to buy: ");
                int qty = Integer.parseInt(scanner.nextLine());
                buyStock(symbol, qty);
            } else if (choice.equals("3")) {
                System.out.print("Enter Stock Symbol to SELL: ");
                String symbol = scanner.nextLine().toUpperCase().trim();
                System.out.print("Enter Quantity to sell: ");
                int qty = Integer.parseInt(scanner.nextLine());
                sellStock(symbol, qty);
            } else if (choice.equals("4")) {
                viewPortfolio();
            } else if (choice.equals("5")) {
                viewHistory();
            } else if (choice.equals("6")) {
                System.out.println("Exiting simulator. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice! Select 1-6.");
            }
        }
        scanner.close();
    }

    private static void showMarket() {
        System.out.println("\n--- LIVE MARKET DATA ---");
        for (Stock s : marketStocks) {
            System.out.println(s.symbol + " | " + s.name + " -> Rs. " + s.price);
        }
    }

    private static void buyStock(String symbol, int qty) {
        Stock targetStock = null;
        for (Stock s : marketStocks) {
            if (s.symbol.equals(symbol)) {
                targetStock = s;
                break;
            }
        }

        if (targetStock == null) {
            System.out.println("Error: Stock symbol not found in market.");
            return;
        }

        double totalCost = targetStock.price * qty;
        if (userBalance < totalCost) {
            System.out.println("Error: Insufficient funds! You need Rs. " + totalCost);
            return;
        }

        // Deduct money from account balance
        userBalance -= totalCost;

        // Add or update share count inside portfolio
        boolean alreadyOwns = false;
        for (PortfolioItem item : myPortfolio) {
            if (item.symbol.equals(symbol)) {
                // Math update for average cost price
                item.averagePrice = ((item.averagePrice * item.quantity) + totalCost) / (item.quantity + qty);
                item.quantity += qty;
                alreadyOwns = true;
                break;
            }
        }

        if (!alreadyOwns) {
            myPortfolio.add(new PortfolioItem(symbol, qty, targetStock.price));
        }

        history.add(new Transaction("BUY", symbol, qty, targetStock.price));
        System.out.println("Success! Bought " + qty + " shares of " + symbol);
    }

    private static void sellStock(String symbol, int qty) {
        PortfolioItem ownedItem = null;
        for (PortfolioItem item : myPortfolio) {
            if (item.symbol.equals(symbol)) {
                ownedItem = item;
                break;
            }
        }

        if (ownedItem == null || ownedItem.quantity < qty) {
            System.out.println("Error: You do not own enough shares to sell.");
            return;
        }

        double livePrice = 0;
        for (Stock s : marketStocks) {
            if (s.symbol.equals(symbol))
                livePrice = s.price;
        }

        double moneyGained = livePrice * qty;
        userBalance += moneyGained; // Add money back to cash balance
        ownedItem.quantity -= qty; // Subtract shares

        if (ownedItem.quantity == 0) {
            myPortfolio.remove(ownedItem);
        }

        history.add(new Transaction("SELL", symbol, qty, livePrice));
        System.out.println("Success! Sold " + qty + " shares of " + symbol + " for Rs. " + moneyGained);
    }

    private static void viewPortfolio() {
        System.out.println("\n--- MY INVESTMENT PORTFOLIO PERFORMANCE ---");
        if (myPortfolio.isEmpty()) {
            System.out.println("You don't own any shares yet.");
            return;
        }

        double totalInvestmentValue = 0;

        for (PortfolioItem item : myPortfolio) {
            // Find what it's worth right now live
            double livePrice = 0;
            for (Stock s : marketStocks) {
                if (s.symbol.equals(item.symbol))
                    livePrice = s.price;
            }

            double currentVal = item.quantity * livePrice;
            double investedVal = item.quantity * item.averagePrice;
            double profitOrLoss = currentVal - investedVal;

            totalInvestmentValue += currentVal;

            System.out.println(item.symbol + " | Shares: " + item.quantity +
                    " | Bought At: Rs." + String.format("%.2f", item.averagePrice) +
                    " | Current Price: Rs." + livePrice +
                    " | Profit/Loss: Rs." + String.format("%.2f", profitOrLoss));
        }
        System.out.println("Total Holdings Net Value: Rs. " + String.format("%.2f", totalInvestmentValue));
    }

    private static void viewHistory() {
        System.out.println("\n--- TRANSACTION HISTORY LOG ---");
        if (history.isEmpty()) {
            System.out.println("No trades made yet.");
            return;
        }
        for (Transaction t : history) {
            System.out
                    .println("[" + t.type + "] " + t.quantity + " shares of " + t.symbol + " @ Rs. " + t.pricePerShare);
        }
    }
}