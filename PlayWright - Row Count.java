package Alert_Mgmt;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HRRAlert_Rowcount {

    @Test
    void loginToAssetMonitor() {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to site
            page.navigate("https://url.com/alerts");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            String username = System.getenv("_USERNAME");
            String password = System.getenv("_PASSWORD");

            if (username == null || password == null) {
                throw new RuntimeException("Set _USERNAME and _PASSWORD environment variables.");
            }

            // -------- Step 1: Enter Email --------
            page.waitForSelector("input[name='loginfmt']");
            page.fill("input[name='loginfmt']", username);
            page.click("input[type='submit']");

            // -------- Step 2: Enter Password --------
            page.waitForSelector("input[name='passwd']");
            page.fill("input[name='passwd']", password);
            page.click("input[type='submit']");

            page.waitForLoadState(LoadState.NETWORKIDLE);
            System.out.println("Login completed.");

            // Click HRR tab
            page.click("text=Host Railroad");

            // Wait for grid to load
            page.waitForSelector(".e-gridcontent");

            // -------------------------------
            // ✅ LOCATORS (FIXED)
            // -------------------------------
            Locator activePage = page.locator(".e-numericitem.e-currentitem");
            Locator nextButton = page.locator(".e-pagercontainer .e-next");

            // ✅ IMPORTANT: Only real data rows
            Locator tableRows = page.locator(".e-gridcontent tbody tr.e-row");

            // Wait until rows are actually loaded
            page.waitForFunction(
                    "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
            );

            // -------------------------------
            // ✅ SINGLE PAGE HANDLING
            // -------------------------------
            if (!nextButton.isVisible()) {

                int rowCount = tableRows.count();

                System.out.println("\n📄 Single Page Table");
                System.out.println("🔢 Row count = " + rowCount);

                Assertions.assertTrue(rowCount > 0,
                        "No rows found in single-page table");

                browser.close();
                return;
            }

            // -------------------------------
            // ✅ MULTI-PAGE LOGIC
            // -------------------------------
            int maxAllowedRows = tableRows.count();
            int totalRowCount = 0;

            System.out.println("Baseline rows per page = " + maxAllowedRows);

            while (true) {

                int currentPage = Integer.parseInt(activePage.innerText().trim());
                System.out.println("\n📄 Page: " + currentPage);

                // Wait for rows before counting
                page.waitForFunction(
                        "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
                );

                int rowCount = tableRows.count();
                System.out.println("🔢 Rows on page = " + rowCount);

                totalRowCount += rowCount;

                // Assertions
                Assertions.assertTrue(rowCount > 0,
                        "No rows found on Page " + currentPage);

                Assertions.assertTrue(rowCount <= maxAllowedRows,
                        "Too many rows on Page " + currentPage +
                                ". Expected ≤ " + maxAllowedRows + " but found " + rowCount);

                // Check if Next button is disabled
                String nextClass = nextButton.getAttribute("class");
                boolean isNextDisabled = nextClass != null && nextClass.contains("e-disable");

                if (isNextDisabled) {
                    System.out.println("\n✅ Last page reached");
                    System.out.println("📊 TOTAL ROW COUNT = " + totalRowCount);
                    break;
                }

                // Click Next
                nextButton.click();

                // Wait for page number to change
                page.waitForFunction(
                        "oldPage => document.querySelector('.e-currentitem').innerText.trim() !== oldPage",
                        String.valueOf(currentPage)
                );

                // Wait for new rows to load
                page.waitForFunction(
                        "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
                );
            }

            browser.close();
        }
    }
}
