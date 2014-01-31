package com.neptunusz.controller;

import com.neptunusz.model.Subject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 */
public class Etr {
    private WebDriver driver;
    private JavascriptExecutor jsExecutor;
    private boolean loggedIn;

    public Etr(WebDriver driver) {
        this.driver = driver;
        jsExecutor = (JavascriptExecutor) driver;
        this.loggedIn = false;
    }

    public void login(String username, String password) {
        try {
            // Go to the Etr home page
            driver.get("https://www.tr.pte.hu/ETR3/login.aspx");
            WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

            driver.findElement(By.name("uname")).sendKeys(username);
            driver.findElement(By.name("pwd")).sendKeys(password);

            jsExecutor.executeScript("dologin();");

            webDriverWait.until(ExpectedConditions.titleContains(username.toUpperCase()));

        } catch (Exception e) {
            e.printStackTrace();
            // Try again if not logged
            login(username, password);

        }

        loggedIn = true;
    }

    public void register(Subject subject) {
        if (loggedIn && subject.isRegister()) {
            try {
                // Go to the subject registration page
                driver.get("https://www.tr.pte.hu/ETR3/KFelvetel.aspx");
                WebDriverWait webDriverWait = new WebDriverWait(driver, 10);

                // Select
                Select select1 = new Select(driver.findElement(By.name("select_kepzesStatusNelkul")));
                select1.selectByIndex(1);

                //
                Select select2 = new Select(driver.findElement(By.name("select_kciklus")));
                select2.selectByValue("2013-2014-2");

                jsExecutor.executeScript("doStep(1);");

                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("kurzusok")));

                String id = driver.findElement(By.xpath("//input[@value='" + subject.getCode() + "']")).getAttribute("id");
                id = id.replace('v', 'w');
                System.out.println("myCheckBoxChange('" + id + "');");
                jsExecutor.executeScript("myCheckBoxChange('" + id + "');");

                // Save
                jsExecutor.executeScript("kfelv_ujsubmit('form');");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registeredSubjects() {
        // Go to the registered subjects page
        driver.get("https://www.tr.pte.hu/ETR3/Kurzus_lista.aspx");
    }

}
