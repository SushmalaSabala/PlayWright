package Alert_Mgmt;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;

public class ALL_Alert_Navigation {

    @Test
    void loginToAssetMonitor() throws InterruptedException {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to site
            page.navigate("https://url.com/alerts");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            String username = System.getenv("USERNAME");
            String password = System.getenv("PASSWORD");

            if (username == null || password == null) {
                throw new RuntimeException("Set USERNAME and PASSWORD environment variables.");
            }

            // -------- Step 1: Enter Email --------
            page.waitForSelector("input[name='loginfmt']");
            page.fill("input[name='loginfmt']", username);
            page.click("input[type='submit']");

            // -------- Step 2: Enter Password --------
            page.waitForSelector("input[name='passwd']");
            page.fill("input[name='passwd']", password);
            page.click("input[type='submit']");

            // -------- Wait 30 seconds for MFA approval --------
            System.out.println("Waiting 30 seconds for MFA approval...");
            page.waitForTimeout(15000); // 30,000 ms = 30 seconds

            // Wait until redirected back to application
            page.waitForLoadState(LoadState.NETWORKIDLE);

            System.out.println("Login flow completed.");
            
            page.waitForTimeout(5000);

            System.out.println("Navigated to: " + page.url());

            browser.close();
        }
        }
    }

