package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import de.bxservice.bxpos.R;

/**
 * Default template for reports in the app
 * Created by Diego Ruiz on 30/03/16.
 */
public class ReportHtmlTemplate {

    public static final String TITLE_TAG  = "TITLE";
    public static final String ROW_TAG    = "ROW";

    private StringBuilder htmlTemplate;

    /**
     * Gets the initial html template
     * with the header
     * @return
     */
    public String getHtmlTemplate() {
        htmlTemplate = new StringBuilder();
        htmlTemplate.append("<h4 align=\"center\"><u>"+ TITLE_TAG +"</u></h4>"); //Title
        return htmlTemplate.toString();
    }

    public String getRowText() {
        return "<p>"+ ROW_TAG +"</p>";
    }

    /**
     * Get header of a table
     * @return
     */
    public String getHtmlTableHeader() {
        return "<table style=\"width:100%\" border=\"0\">"; //Table with no border
    }

    /**
     * Get closing tag of a table
     * @return
     */
    public String getHtmlTableClose() {
        return "</table>";
    }

    /**
     * Open a table row
     * @return
     */
    public String getHtmlRowOpen() {
        return "<tr>";
    }

    /**
     * Close a table row
     * @return
     */
    public String getHtmlRowClose() {
        return "</tr>";
    }

    /**
     * Get a column
     * @param alignment it must be left - center or right
     * @return
     */
    public String getHtmlColumn(String alignment) {
        return "<td align=\""+ alignment +"\">" + ROW_TAG + "</td>";
    }

    public String getTotalLine(Context ctx) {
        String totalLine = ctx.getResources().getString(R.string.total);
        return "<p><b>" + totalLine +"</b>" + ROW_TAG + "</p>";
    }

}
