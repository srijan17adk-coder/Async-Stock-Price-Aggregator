import java.util.*;
import java.util.concurrent.*;

public class StockAggregator {

    public static void main(String[] args) {

        List<String> symbols = List.of("AAPL", "GOOGL", "MSFT", "AMZN");

        System.out.println("Starting async stock fetch...");

        // Create futures using loop instead of stream
        List<CompletableFuture<String>> taskList = new ArrayList<>();

        for (String symbol : symbols) {
            CompletableFuture<String> task =
                    CompletableFuture.supplyAsync(() -> getStockValue(symbol));
            taskList.add(task);
        }

        // Wait for all tasks
        CompletableFuture<Void> combinedFuture =
                CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0]));

        // Print results after completion
        combinedFuture.thenAccept(v -> displayResults(taskList)).join();
    }

    // Separate method to display results
    private static void displayResults(List<CompletableFuture<String>> tasks) {
        System.out.println("\n=== Stock Price Summary ===");
        for (CompletableFuture<String> f : tasks) {
            System.out.println(f.join());
        }
    }

    // Renamed method and slightly changed logic structure
    private static String getStockValue(String symbol) {
        try {
            // simulate delay
            TimeUnit.SECONDS.sleep(1);

            double value = ThreadLocalRandom.current().nextDouble(100, 1000);

            return symbol + " -> $" + String.format("%.2f", value);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return symbol + " -> Failed to fetch";
        }
    }
}
