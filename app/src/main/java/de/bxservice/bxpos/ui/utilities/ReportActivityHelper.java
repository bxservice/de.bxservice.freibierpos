/**********************************************************************
 * This file is part of Freibier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.ui.utilities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportActivityHelper {

    public static void openPdf(Activity activity, String pdfName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(),  pdfName));
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
    }

    public static void saveToPdf(Activity activity, RecyclerView recyclerView) {
        // create a new document
        PdfDocument document = new PdfDocument();

        // draw something on the page
        int pages = 1; //reportResults.size(); If in the future we decided to create a page per report

        View content = recyclerView;
        // create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), pages).create();

        // First page
        PdfDocument.Page page = document.startPage(pageInfo);
        content.draw(page.getCanvas());
        // finish the page
        document.finishPage(page);

        //When the document has more than on one
        /*for(int i = 1; i < reportResults.size(); i++) {
            // New page
            page = document.startPage(pageInfo);
            content = recyclerView.getChildAt(i);
            content.draw(page.getCanvas());
            // finish the page
            document.finishPage(page);

        }*/

        FileOutputStream fos;
        // saving pdf document to sdcard
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pdfName = "Report"
                + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
        try {

            File f = new File(Environment.getExternalStorageDirectory().getPath(), pdfName);
            //f.createNewFile();

            fos = new FileOutputStream(f);
            // write the document content
            document.writeTo(fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();

        openPdf(activity, pdfName);
    }
}
