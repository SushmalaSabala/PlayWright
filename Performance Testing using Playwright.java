package Sensors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class Testing1 {

    @Test
    public void measureWILDDashboardPerformance() {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to Sensors page
            page.navigate("http://localhost:4200/sensors");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            System.out.println("Navigated to: " + page.url());

            // Track GraphQL responses
            AtomicInteger graphqlCount = new AtomicInteger(0);

            page.onResponse(response -> {
                if (response.url().contains("graphql")) {

                    int count = graphqlCount.incrementAndGet();

                    System.out.println(
                            "GraphQL Response #" + count +
                            " | Status=" + response.status() +
                            " | URL=" + response.url()
                    );
                }
            });

            // Open Dashboard dropdown
            page.locator("#ej2_multiselect_11").click();

            // Select WILD - Vertical Peak
            page.locator("li")
                    .filter(new Locator.FilterOptions()
                            .setHasText("WILD - Vertical Peak"))
                    .first()
                    .click();

            System.out.println("Dashboard Selected: WILD - Vertical Peak");

            // Start timing
            long dashboardStart = System.nanoTime();

            // Click outside to apply selection
            page.mouse().click(50, 50);

            // Wait for all network activity to complete
            page.waitForLoadState(LoadState.NETWORKIDLE);

            long networkComplete = System.nanoTime();

            // Optional UI stabilization wait
            page.waitForTimeout(1000);

            long uiComplete = System.nanoTime();

            // Calculate timings
            double backendTime =
                    (networkComplete - dashboardStart) / 1_000_000.0;

            double frontendTime =
                    (uiComplete - networkComplete) / 1_000_000.0;

            double totalTime =
                    (uiComplete - dashboardStart) / 1_000_000.0;

            double backendPct =
                    totalTime > 0
                            ? (backendTime / totalTime) * 100
                            : 0;

            double frontendPct =
                    totalTime > 0
                            ? (frontendTime / totalTime) * 100
                            : 0;

            // Results
            System.out.println("\n=======================================");
            System.out.println("WILD - Vertical Peak Dashboard Analysis");
            System.out.println("=======================================");

            System.out.println("GraphQL Calls : "
                    + graphqlCount.get());

            System.out.printf(
                    "Backend/API Time : %.2f ms (%.1f%%)%n",
                    backendTime,
                    backendPct
            );

            System.out.printf(
                    "Frontend/UI Time : %.2f ms (%.1f%%)%n",
                    frontendTime,
                    frontendPct
            );

            System.out.printf(
                    "Total Load Time  : %.2f ms%n",
                    totalTime
            );

            System.out.println("=======================================");

            browser.close();
        }
    }
}
