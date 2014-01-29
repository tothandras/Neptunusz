package com.neptunusz.controller;

import com.neptunusz.model.Subject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Neptun {
    private WebDriver driver;
    private boolean loginned;

    public Neptun(WebDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //Get the current time and advance it by 1 hour
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        Date expiry = calendar.getTime();

        Cookie cookie1 = new Cookie(".ASPXAUTH", "4EC2AF48BEC0F2DF33E83B61877201F7823FFF0648FEBADF680A7654172B0089F2BBB4B35352380D69A1C453B401F55D7691EDE728107116217983ACDF1A22BBEC95CB1F524900B1E79547D9F464C74F42D9B8B1580A874DB82A4FDEF0FBA83F51BF4F15B66C217F4DA4AB50199DEBDF2F5C3A8F0087CCFF87C2CF25E01F6ED2812F16265B3EAEEBAF83C5EF2F102CFD", "frame.neptun.bme.hu", "/", expiry);
        Cookie cookie2 = new Cookie("ASP.NET_SessionId", "ahp5gdelwsaprrjgfw0cskp3", "frame.neptun.bme.hu", "/", expiry);
        driver.manage().addCookie(cookie1);
        driver.manage().addCookie(cookie2);
    }

    public void login(String username, String password) {
        try {
            // Go to the Neptun home page
            driver.get("https://frame.neptun.bme.hu/hallgatoi/login.aspx");

            // Enter user name
            WebElement user = driver.findElement(By.name("user"));
            user.sendKeys(username);

            // Enter password
            WebElement pass = driver.findElement(By.name("pwd"));
            pass.sendKeys(password);

            WebElement button = driver.findElement(By.name("btnSubmit"));
            button.click();

            ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return (driver.getCurrentUrl().equals("https://frame.neptun.bme.hu/hallgatoi/main.aspx"));
                }
            };

            WebDriverWait webDriverWait = new WebDriverWait(driver, 400);
            webDriverWait.until(expectedCondition);

        } catch (Exception e) {
            e.printStackTrace();
            // try again
            login(username, password);
        }
        loginned = true;
    }

    public void register(Subject subject) {
        if (loginned) {
            try {
                driver.get("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303");
                WebDriverWait webDriverWait = new WebDriverWait(driver, 10);

                // Select 2013/14/2 semester
                Select select = new Select(driver.findElement(By.id("upFilter_cmbTerms")));
                select.selectByVisibleText("2013/14/2");

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

                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("h_addsubjects_gridSubjects_bodytable")));

                Select searchSelect = new Select(driver.findElement(By.id("h_addsubjects_gridSubjects_searchcolumn")));
                searchSelect.selectByValue("Code");

                WebElement searchField = driver.findElement(By.id("h_addsubjects_gridSubjects_searchtext"));
                searchField.sendKeys(subject.getCode());

                WebElement searchButton = driver.findElement(By.id("h_addsubjects_gridSubjects_searchsubmit"));
                searchButton.click();

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

                // OK
                webDriverWait = new WebDriverWait(driver, 3);
                WebElement okButton = driver.findElement(By.xpath("//button[@value='OK']"));
                webDriverWait.until(ExpectedConditions.elementToBeClickable(okButton));
                okButton.click();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registeredSubjects() {
        driver.get("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303");
    }
}
