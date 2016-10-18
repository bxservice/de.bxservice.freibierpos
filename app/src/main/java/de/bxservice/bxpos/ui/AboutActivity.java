/**********************************************************************
 * This file is part of FreiBier POS                                   *
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
package de.bxservice.bxpos.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.bxservice.bxpos.BuildConfig;
import de.bxservice.bxpos.R;

public class AboutActivity extends AppCompatActivity {

    private static final String FORUMS_URL  = "https://groups.google.com/forum/#!forum/idempiere";
    private static final String HELP_URL    = "https://drive.google.com/open?id=1BPJPkE1swO3cYh9tROTBpAGdm_vdNZu98lUc-Q6Y1rw";
    private static final String GPLUS_URL   = "https://plus.google.com/BXS_PROFILE/posts";
    private static final String LICENSE_URL = "https://www.gnu.org/licenses/old-licenses/gpl-2.0.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);

        TextView versionTv = (TextView) findViewById(R.id.versionTextView);
        versionTv.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString(getString(R.string.developedBy));
        str1.setSpan(new ForegroundColorSpan(Color.GRAY), 0, str1.length(), 0);
        builder.append(str1);

        SpannableString str2= new SpannableString(" Diego Andrés Ruiz Gómez");
        str2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, str2.length(), 0);
        builder.append(str2);

        TextView tv = (TextView) findViewById(R.id.developerTextView);
        tv.setText( builder, TextView.BufferType.SPANNABLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    /**
     * On Button listener defined in the xml file
     * @param view the view were the click was performed
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {

            case R.id.forumLinear:
                openURL(FORUMS_URL);
                break;
            case R.id.gPlusLinear:
                openGPlus("107685996636371844085");
                break;
            case R.id.rateLinear:
                Toast.makeText(getBaseContext(), "Click Rate us",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.licenseLinear:
                openURL(LICENSE_URL);
                break;
            case R.id.helpLinear:
                openURL(HELP_URL);
                break;
            case R.id.developerTextView:
                openGPlus("+DiegoRuiz15");
                break;
            default:
                break;
        }
    }

    private void openGPlus(String profile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri",profile);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            openURL(GPLUS_URL.replace("BXS_PROFILE",profile));
        }
    }

    private void openURL(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)));
    }

}
