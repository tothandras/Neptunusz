package com.neptunusz.controller;

import com.neptunusz.model.Subject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Neptun {

    private WebDriver driver;
    private boolean loggedIn;
    private String semester;

    public Neptun(WebDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        this.semester = "2013/14/2";
    }

    public void login(String username, String password) {
        try {
            // Go to the Neptun home page
            driver.get("https://frame.neptun.bme.hu/hallgatoi/login.aspx");
            WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

            // Quick login scripts
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
            javascriptExecutor.executeScript("maxtrynumber = 1e6;");
            javascriptExecutor.executeScript("starttimer = function() {login_wait_timer = setInterval('docheck()', 1);}");

            /*
            // Cookies
            Cookie cookie1 = new Cookie(".ASPXAUTH", "here", "frame.neptun.bme.hu", "/", null);
            Cookie cookie2 = new Cookie("ASP.NET_SessionId", "and here", "frame.neptun.bme.hu", "/", null);
            driver.manage().addCookie(cookie1);
            driver.manage().addCookie(cookie2);
            driver.get("https://frame.neptun.bme.hu/hallgatoi/main.aspx");
            */

            // Enter user name
            WebElement user = driver.findElement(By.name("user"));
            user.sendKeys(username);

            // Enter password
            WebElement pass = driver.findElement(By.name("pwd"));
            pass.sendKeys(password);

            // Wait until course registration starts
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2014, Calendar.JANUARY, 30, 18, 0, 0);
            long diff = calendar.getTimeInMillis() - timeInMillis;

            if (diff > 0) {
                Thread.sleep(diff);
            }

            // Click on Login
            WebElement button = driver.findElement(By.name("btnSubmit"));
            button.click();

            // Handle alert: "A párhuzamos belépések számának korlátozása miatt korábbi munkamenetét töröltük!"
            try {
                webDriverWait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                alert.accept();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Wait until full login
            ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return (driver.getCurrentUrl().equals("https://frame.neptun.bme.hu/hallgatoi/main.aspx"));
                }
            };
            webDriverWait = new WebDriverWait(driver, 400);
            webDriverWait.until(expectedCondition);

        } catch (Exception e) {
            e.printStackTrace();
            // Try again if not logged
            login(username, password);

        }

        loggedIn = true;
    }

    public void register(Subject subject) {
        if (loggedIn) {
            try {
                // Go to the subject registration page
                driver.get("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303");
                WebDriverWait webDriverWait = new WebDriverWait(driver, 10);

                // Select semester
                Select select = new Select(driver.findElement(By.id("upFilter_cmbTerms")));
                select.selectByVisibleText(semester);

                // Select subject type
                WebElement radioButton;
                switch (subject.getType()) {
                    case CURRICULUM:
                        radioButton = driver.findElement(By.id("upFilter_rbtnSubjectType_0"));
                        radioButton.click();
                        break;
                    case OPTIONAL:
                        radioButton = driver.findElement(By.id("upFilter_rbtnSubjectType_1"));
                        radioButton.click();
                        break;
                    case ALL:
                        radioButton = driver.findElement(By.id("upFilter_rbtnSubjectType_2"));
                        radioButton.click();
                        break;
                }

                // List subjects
                WebElement listButton = driver.findElement(By.id("upFilter_expandedsearchbutton"));
                listButton.click();

                // Search for subject name
                WebElement searchIcon = driver.findElement(By.id("imgsearch"));
                searchIcon.click();

                // Wait until all subjects are listed
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("h_addsubjects_gridSubjects_bodytable")));

                // Select search by subject code
                Select searchSelect = new Select(driver.findElement(By.id("h_addsubjects_gridSubjects_searchcolumn")));
                searchSelect.selectByValue("Code");
                WebElement searchField = driver.findElement(By.id("h_addsubjects_gridSubjects_searchtext"));
                searchField.sendKeys(subject.getCode());
                WebElement searchButton = driver.findElement(By.id("h_addsubjects_gridSubjects_searchsubmit"));
                searchButton.click();

                // Wait until the right subject code appears in the table
                webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("/html/body/form[@id='form1']/fieldset/table[2]/tbody/tr/td[@class='function']/table[@class='function_table']/tbody/tr[@class='no_style'][3]/td[@id='function_table_body']/div[@id='upFunction']/div[@id='upFunction_h_addsubjects_upGrid']/div[@id='h_addsubjects_gridSubjects_gridtopdiv']/div[@id='h_addsubjects_gridSubjects_gridmaindiv']/div[@id='h_addsubjects_gridSubjects_grid_body_div']/table[@id='h_addsubjects_gridSubjects_bodytable']/tbody[@class='scrollablebody']/tr/td[3]"), subject.getCode()));

                // Subject registration
                List<WebElement> links = driver.findElements(By.className("link"));
                for (WebElement link : links) {
                    if (link.getText().equals("Felvesz")) {
                        link.click();
                        break;
                    }
                }

                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Subject_data1_ctl00")));
                for (String course : subject.getCourses()) {
                    WebElement checkbox = driver.findElement(By.xpath("//*[@id=//label[normalize-space(text()) = '" + course + "']/@for]"));
                    checkbox.click();
                }

                // Save
                WebElement finishButton = driver.findElement(By.id("function_update1"));
                finishButton.click();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registeredSubjects() {
        // Go to the registered subjects page
        driver.get("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0304");

        // Select semester
        Select select = new Select(driver.findElement(By.id("cmb_cmb")));
        select.selectByVisibleText(semester + " (aktuális félév)");

        // List subjects
        WebElement button = driver.findElement(By.id("upFilter_expandedsearchbutton"));
        button.click();
    }
}
