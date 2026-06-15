package Alert_Mgmt;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HRRAlert_First_Last_Page {

    @Test
    void loginToAssetMonitor() {

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(
                     new BrowserType.LaunchOptions().setHeadless(false));
             BrowserContext context = browser.newContext()) {

            Page page = context.newPage();

            // =============================
            // Navigate
            // =============================
            page.navigate("https:url.com/alerts");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            String username = System.getenv("_USERNAME");
            String password = System.getenv("_PASSWORD");

            if (username == null || password == null) {
                throw new RuntimeException("Set _USERNAME and _PASSWORD environment variables.");
            }

            // =============================
            // Login
            // =============================
            page.fill("input[name='loginfmt']", username);
            page.click("input[type='submit']");

            page.fill("input[name='passwd']", password);
            page.click("input[type='submit']");

            page.waitForLoadState(LoadState.NETWORKIDLE);

            // =============================
            // Navigate to HRR
            // =============================

            page.click("text=Host Railroad");

            // Wait for grid container
            page.waitForSelector(".e-gridcontent");

            // =============================
            // Locators (FIXED)
            // =============================
            Locator firstButton = page.locator(".e-pagercontainer .e-first");
            Locator prevButton  = page.locator(".e-pagercontainer .e-prev");
            Locator nextButton  = page.locator(".e-pagercontainer .e-next");
            Locator lastButton  = page.locator(".e-pagercontainer .e-last");
            Locator activePage  = page.locator(".e-pagercontainer .e-numericitem.e-currentitem");

            // ✅ Correct row locator (IMPORTANT)
            Locator tableRows = page.locator(".e-gridcontent tbody tr.e-row");

            // Wait until rows are loaded
            page.waitForFunction(
                    "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
            );

            System.out.println("✅ Grid loaded.");

            // =============================
            // SINGLE PAGE CHECK
            // =============================
            String nextClassInit = nextButton.getAttribute("class");
            boolean isSinglePage = nextClassInit != null && nextClassInit.contains("e-disable");

            if (isSinglePage) {
                System.out.println("📄 Only one page present. Skipping pagination checks.");

                int rowCount = tableRows.count();
                System.out.println("Row count = " + rowCount);

                Assertions.assertTrue(rowCount > 0, "No rows found");

                return;
            }

            // =====================================================
            // ✅ GO TO LAST PAGE
            // =====================================================
            System.out.println("\n➡️ Clicking LAST page...");

            String oldPage = activePage.innerText().trim();
            lastButton.click();

            // Wait for page change
            page.waitForFunction(
                    "(oldPage) => document.querySelector('.e-currentitem').innerText.trim() !== oldPage",
                    oldPage
            );

            // Wait for rows to reload
            page.waitForFunction(
                    "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
            );

            String lastPageNumber = activePage.innerText().trim();
            System.out.println("📄 Now on Last Page: " + lastPageNumber);

            // ✅ Validate buttons disabled
            String lastClass = lastButton.getAttribute("class");
            String nextClass = nextButton.getAttribute("class");

            Assertions.assertTrue(
                    lastClass != null && lastClass.contains("e-disable"),
                    "Last button should be disabled on last page"
            );

            Assertions.assertTrue(
                    nextClass != null && nextClass.contains("e-disable"),
                    "Next button should be disabled on last page"
            );

            System.out.println("✅ Last & Next buttons correctly disabled.");

            // =====================================================
            // ✅ GO TO FIRST PAGE
            // =====================================================
            System.out.println("\n⬅️ Clicking FIRST page...");

            oldPage = activePage.innerText().trim();
            firstButton.click();

            // Wait for page change
            page.waitForFunction(
                    "(oldPage) => document.querySelector('.e-currentitem').innerText.trim() !== oldPage",
                    oldPage
            );

            // Wait for rows reload
            page.waitForFunction(
                    "() => document.querySelectorAll('.e-gridcontent tbody tr.e-row').length > 0"
            );

            String firstPageNumber = activePage.innerText().trim();
            System.out.println("📄 Now on First Page: " + firstPageNumber);

            // ✅ Validate buttons disabled
            String firstClass = firstButton.getAttribute("class");
            String prevClass  = prevButton.getAttribute("class");

            Assertions.assertTrue(
                    firstClass != null && firstClass.contains("e-disable"),
                    "First button should be disabled on first page"
            );

            Assertions.assertTrue(
                    prevClass != null && prevClass.contains("e-disable"),
                    "Previous button should be disabled on first page"
            );

            System.out.println("✅ First & Previous buttons correctly disabled.");

            System.out.println("\n🎉 Test completed successfully.");
        }
    }
}
