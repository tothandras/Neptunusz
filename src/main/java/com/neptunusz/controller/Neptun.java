package com.neptunusz.controller;

import com.neptunusz.model.Subject;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Neptun {

    CloseableHttpClient httpClient;
    HttpContext localContext;
    RequestConfig requestConfig;
    private boolean loggedIn;

    public Neptun() {
        httpClient = HttpClients.createDefault();
        BasicCookieStore cookieStore = new BasicCookieStore();
        localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        requestConfig = RequestConfig.custom()
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
    }

    public void login(String username, String password) {
        try {
            HttpUriRequest login = RequestBuilder.post()
                    .setUri("https://frame.neptun.bme.hu/hallgatoi/Login.aspx/CheckLoginEnable")
                    .setEntity(new StringEntity("{'user':'" + username.trim() + "','pwd':'" + password + "','UserLogin':null,'GUID':null}", ContentType.APPLICATION_JSON))
                    .setConfig(requestConfig)
                    .build();

            HttpResponse response = httpClient.execute(login, localContext);
            if (EntityUtils.toString(response.getEntity()).contains("True")) {
                loggedIn = true;
                System.out.println("Logged in as: " + username);
            } else {
                System.out.println("Wrong username or password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(Subject subject) {
        if (loggedIn && subject.isRegister()) {

            HttpResponse httpResponse;
            Document document;
            String viewState;
            String eventValidation;
            String response;

            String subjectType;
            switch (subject.getType()) {
                case CURRICULUM:
                    subjectType = "MintatantervTargyai";
                    break;
                case OPTIONAL:
                    subjectType = "EgyebSzabadonValaszthato";
                    break;
                default:
                    subjectType = "MindenIntezmenyiTargy";
                    break;
            }

            try {
                // Get the subjects page
                HttpUriRequest subjectsPage = RequestBuilder.get()
                        .setUri("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303")
                        .build();

                httpResponse = httpClient.execute(subjectsPage, localContext);
                response = EntityUtils.toString(httpResponse.getEntity());

                // Get values
                document = Jsoup.parse(response);
                viewState = document.select("#__VIEWSTATE").val();
                eventValidation = document.select("#__EVENTVALIDATION").val();

                List<BasicNameValuePair> form = new ArrayList<>();
                form.add(new BasicNameValuePair("ToolkitScriptManager1", "upFilter|upFilter$expandedsearchbutton"));
                form.add(new BasicNameValuePair("ToolkitScriptManager1_HiddenField", ""));
                form.add(new BasicNameValuePair("__EVENTTARGET", ""));
                form.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                form.add(new BasicNameValuePair("__LASTFOCUS", ""));
                form.add(new BasicNameValuePair("__VIEWSTATE", viewState));
                form.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation));
                form.add(new BasicNameValuePair("ActiveModalBehaviourID", ""));
                form.add(new BasicNameValuePair("progressalerttype", "progress"));
                form.add(new BasicNameValuePair("NoMatchString", "!"));
                form.add(new BasicNameValuePair("hfCountDownTime", "1800"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTime", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkExam", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTask", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkKonzultacio", "on"));
                form.add(new BasicNameValuePair("upFilter$cmbTerms", "70607"));
                form.add(new BasicNameValuePair("upFilter$rbtnSubjectType", subjectType));
                form.add(new BasicNameValuePair("upFilter$cmbTemplates", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbSubjectGroups", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbLanguage", "0"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$validCalloutExt_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$cmbWTChooser_upFilter_WTChooserFrom", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$txbWTChooser_upFilter_WTChooserFrom", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$maskEditT_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$txtOktato", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$validCalloutExt_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$cmbWTChooser_upFilter_WTChooserTo", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$txbWTChooser_upFilter_WTChooserTo", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$maskEditT_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFunction$h_addsubjects$upFilter$searchpanel$searchpanel_state", "expanded"));
                form.add(new BasicNameValuePair("filedownload$hfDocumentId", ""));
                form.add(new BasicNameValuePair("__ASYNCPOST", "true"));
                form.add(new BasicNameValuePair("upFilter$expandedsearchbutton", ""));

                HttpUriRequest listSubjects = RequestBuilder.post()
                        .setUri("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303")
                        .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .setEntity(new StringEntity(URLEncodedUtils.format(form, StandardCharsets.UTF_8), ContentType.APPLICATION_FORM_URLENCODED))
                        .build();

                httpResponse = httpClient.execute(listSubjects, localContext);
                response = EntityUtils.toString(httpResponse.getEntity());
                viewState = response.substring(response.lastIndexOf("__VIEWSTATE|") + 12, response.lastIndexOf("__EVENTVALIDATION|") - 18);
                eventValidation = response.substring(response.lastIndexOf("__EVENTVALIDATION|") + 18, response.lastIndexOf("asyncPostBackControlIDs|") - 3);

                HttpUriRequest searchSubject = RequestBuilder.get()
                        .setUri("https://frame.neptun.bme.hu/hallgatoi/HandleRequest.ashx?RequestType=GetData&GridID=h_addsubjects_gridSubjects&pageindex=1&pagesize=500&sort1=TermNumber%20ASC&sort2=&fixedheader=false&searchcol=Code&searchtext=" + subject.getCode().trim() + "&searchexpanded=true&allsubrowsexpanded=False&selectedid=undefined&functionname=&level=")
                        .build();

                httpResponse = httpClient.execute(searchSubject, localContext);
                response = EntityUtils.toString(httpResponse.getEntity());
                document = Jsoup.parse(response);
                String subjectId = document.select("#h_addsubjects_gridSubjects_bodytable>tbody>tr").attr("id").substring(4);

                form = new ArrayList<>();
                form.add(new BasicNameValuePair("ToolkitScriptManager1", "ToolkitScriptManager1|upFunction$h_addsubjects$upGrid$gridSubjects"));
                form.add(new BasicNameValuePair("ToolkitScriptManager1_HiddenField", ""));
                form.add(new BasicNameValuePair("__EVENTTARGET", "upFunction$h_addsubjects$upGrid$gridSubjects"));
                form.add(new BasicNameValuePair("__EVENTARGUMENT", "commandname=subjectdata;commandsource=select;id=" + subjectId + ";level=1"));
                form.add(new BasicNameValuePair("__LASTFOCUS", ""));
                form.add(new BasicNameValuePair("__VIEWSTATE", viewState));
                form.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation));
                form.add(new BasicNameValuePair("ActiveModalBehaviourID", ""));
                form.add(new BasicNameValuePair("progressalerttype", "progress"));
                form.add(new BasicNameValuePair("NoMatchString", "!"));
                form.add(new BasicNameValuePair("hfCountDownTime", "1800"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTime", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkExam", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTask", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkKonzultacio", "on"));
                form.add(new BasicNameValuePair("upFilter$cmbTerms", "70607"));
                form.add(new BasicNameValuePair("upFilter$rbtnSubjectType", subjectType));
                form.add(new BasicNameValuePair("upFilter$cmbTemplates", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbSubjectGroups", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbLanguage", "0"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$validCalloutExt_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$cmbWTChooser_upFilter_WTChooserFrom", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$txbWTChooser_upFilter_WTChooserFrom", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$maskEditT_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$txtOktato", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$validCalloutExt_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$cmbWTChooser_upFilter_WTChooserTo", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$txbWTChooser_upFilter_WTChooserTo", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$maskEditT_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFunction$h_addsubjects$upFilter$searchpanel$searchpanel_state", "expanded"));
                form.add(new BasicNameValuePair("upFunction$h_addsubjects$upModal$upmodal_subjectdata$_data", "Visible:false"));
                form.add(new BasicNameValuePair("filedownload$hfDocumentId", ""));
                form.add(new BasicNameValuePair("Subject_data1_tab_ClientState", "{\"ActiveTabIndex\":0,\"TabEnabledState\":[true,true,true,true,true,true],\"TabWasLoadedOnceState\":[true,false,false,false,false,false]}"));
                form.add(new BasicNameValuePair("__ASYNCPOST", "true"));
                form.add(new BasicNameValuePair("", ""));

                HttpUriRequest openModal = RequestBuilder.post()
                        .setUri("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303")
                        .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .setEntity(new StringEntity(URLEncodedUtils.format(form, StandardCharsets.UTF_8), ContentType.APPLICATION_FORM_URLENCODED))
                        .build();

                httpResponse = httpClient.execute(openModal, localContext);
                response = EntityUtils.toString(httpResponse.getEntity());
                document = Jsoup.parse(response);
                viewState = response.substring(response.lastIndexOf("__VIEWSTATE|") + 12, response.lastIndexOf("__EVENTVALIDATION|") - 18);
                eventValidation = response.substring(response.lastIndexOf("__EVENTVALIDATION|") + 18, response.lastIndexOf("asyncPostBackControlIDs|") - 3);

                List<String> courseIds = new ArrayList<>();
                for (Element span : document.select("span")) {
                    for (String s : subject.getCourses()) {
                        if (span.text().trim().equals(s)) {
                            courseIds.add(span.parent().attr("onclick").substring(8, 17));
                        }
                    }
                }

                for (String courseId : courseIds) {
                    HttpUriRequest registerSubject = RequestBuilder.post()
                            .setUri("https://frame.neptun.bme.hu/hallgatoi/HandleRequest.ashx?RequestType=Update&GridID=Addsubject_course1_gridCourses&pageindex=1&pagesize=999999&sort1=&sort2=&fixedheader=false&searchcol=&searchtext=&searchexpanded=false&allsubrowsexpanded=False&selectedid=undefined&functionname=update&level=1")
                            .setHeader("Content-Type", "text/plain;charset=UTF-8")
                            .setHeader("User-Agent", "Mozilla/5.0")
                            .setEntity(new StringEntity("{\"Data\":[ {\"ID\":\""+ courseId +"\",\"chk\":\"#true\"} ]}"))
                            .build();

                    httpResponse = httpClient.execute(registerSubject, localContext);
                    if (EntityUtils.toString(httpResponse.getEntity()).equals("ok")) {
                        System.out.println("ok");
                    }
                }

                form = new ArrayList<>();
                form.add(new BasicNameValuePair("ToolkitScriptManager1", "ToolkitScriptManager1|upFunction$h_addsubjects$upModal$upmodal_subjectdata$ctl02$Subject_data1$upParent$tab$ctl00$upAddSubjects$Addsubject_course1$upGrid$gridCourses"));
                form.add(new BasicNameValuePair("ToolkitScriptManager1_HiddenField", ""));
                form.add(new BasicNameValuePair("__EVENTTARGET", "upFunction$h_addsubjects$upModal$upmodal_subjectdata$ctl02$Subject_data1$upParent$tab$ctl00$upAddSubjects$Addsubject_course1$upGrid$gridCourses"));
                form.add(new BasicNameValuePair("__EVENTARGUMENT", "commandname=update;commandsource=function"));
                form.add(new BasicNameValuePair("__LASTFOCUS", ""));
                form.add(new BasicNameValuePair("__VIEWSTATE", viewState));
                form.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation));
                form.add(new BasicNameValuePair("ActiveModalBehaviourID", "behaviorupFunction_h_addsubjects_upModal_modal_subjectdata"));
                form.add(new BasicNameValuePair("progressalerttype", "progress"));
                form.add(new BasicNameValuePair("NoMatchString", "!"));
                form.add(new BasicNameValuePair("hfCountDownTime", "1800"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTime", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkExam", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkTask", "on"));
                form.add(new BasicNameValuePair("upBoxes$upCalendar$gdgCalendar$ctl35$calendar$upPanel$chkKonzultacio", "on"));
                form.add(new BasicNameValuePair("upFilter$cmbTerms", "70607"));
                form.add(new BasicNameValuePair("upFilter$rbtnSubjectType", subjectType));
                form.add(new BasicNameValuePair("upFilter$cmbTemplates", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbSubjectGroups", "All"));
                form.add(new BasicNameValuePair("upFilter$cmbLanguage", "0"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$validCalloutExt_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$cmbWTChooser_upFilter_WTChooserFrom", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$txbWTChooser_upFilter_WTChooserFrom", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserFrom$maskEditT_upFilter_WTChooserFrom_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$txtOktato", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$validCalloutExt_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$cmbWTChooser_upFilter_WTChooserTo", "Hétfő"));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$txbWTChooser_upFilter_WTChooserTo", ""));
                form.add(new BasicNameValuePair("upFilter$WTChooserTo$maskEditT_upFilter_WTChooserTo_ClientState", ""));
                form.add(new BasicNameValuePair("upFunction$h_addsubjects$upFilter$searchpanel$searchpanel_state", "expanded"));
                form.add(new BasicNameValuePair("upFunction$h_addsubjects$upModal$upmodal_subjectdata$_data", "Visible:true"));
                form.add(new BasicNameValuePair("filedownload$hfDocumentId", ""));
                form.add(new BasicNameValuePair("Subject_data1_tab_ClientState", "{\"ActiveTabIndex\":0,\"TabEnabledState\":[true,true,true,true,true,true],\"TabWasLoadedOnceState\":[true,false,false,false,false,false]}"));
                form.add(new BasicNameValuePair("__ASYNCPOST", "true"));
                form.add(new BasicNameValuePair("", ""));

                HttpUriRequest finish = RequestBuilder.post()
                        .setUri("https://frame.neptun.bme.hu/hallgatoi/main.aspx?ismenuclick=true&ctrl=0303")
                        .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .setEntity(new StringEntity(URLEncodedUtils.format(form, StandardCharsets.UTF_8), ContentType.APPLICATION_FORM_URLENCODED))
                        .build();

                httpClient.execute(finish, localContext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registeredSubjects() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
