package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import de.bxservice.bxpos.R;

/**
 * Default tempalte for reports in the app
 * Created by Diego Ruiz on 30/03/16.
 */
public class ReportHtmlTemplate {

    public static final String TITLE_TAG  = "TITLE";
    public static final String ROW_TAG    = "ROW";

    private StringBuilder htmlTemplate;

    public String getHtmlTemplate() {
        htmlTemplate = new StringBuilder();
        htmlTemplate.append("<h4 align=\"center\">"+ TITLE_TAG +"</h4>"); //Title
        return htmlTemplate.toString();
    }

    public String getRowText() {
        return "<p>"+ ROW_TAG +"</p>";
    }

    public String getTotalLine(Context ctx) {
        String totalLine = ctx.getResources().getString(R.string.total);
        return "<p><b>" + totalLine +"</b>" + ROW_TAG + "</p>";
    }

}
