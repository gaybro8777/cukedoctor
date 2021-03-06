package com.github.cukedoctor.extension;

import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * Created by pestano on 20/07/15. adds search box to rendered html
 * documentation
 */
public class CukedoctorFooterExtension extends Postprocessor {

    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

    @Override
    public String process(Document document, String output) {
        if (document.isBasebackend("html") && System.getProperty("cukedoctor.disable.footer") == null) {
            String stopWatch = System.getProperty("cukedoctor.stopwatch");
            Double generationTimeInSeconds = new Double(0);
            String documentationDate = dateFormat.format(new Date());
            String cukedoctorVersion = "";
            try {
                ResourceBundle bundle = new PropertyResourceBundle(CukedoctorFooterExtension.class.getResourceAsStream("/cukedoctor-extension.properties"));
                cukedoctorVersion = bundle.getString("cukedoctor.version");
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).warning("Could not find bundle cukedoctor-extension");
            }

            if (stopWatch != null && !"".equals(stopWatch)) {
                long begin = 0;
                try {
                    begin = Long.parseLong(stopWatch);
                    generationTimeInSeconds = (System.currentTimeMillis() - begin) / 1000.0;
                } catch (NumberFormatException e) {
                }
            }
            org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8");
            Element contentElement = doc.getElementById("footer");
            contentElement.attr("style", "color:gray;font-size:11px");
            contentElement.text("Generated by ").append("<a href=https://github.com/rmpestano/cukedoctor target=\"_blank\"> Cukedoctor " +
                    cukedoctorVersion +
                    "</a>");
            contentElement.append(" - " + documentationDate);
            contentElement.append("<span style=\"float:right;font-size:11px\"> Execution time: " +
                    +generationTimeInSeconds + " seconds</span>");
            return doc.html();
        } else {
            return output;
        }
    }

}
