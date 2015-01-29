package com.example.blackmail;

import java.io.InputStream;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends FragmentActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_help);
		TextView helpText = (TextView)findViewById(R.id.help_text);
		
		try {
			Resources rs = getResources();
			InputStream is = rs.openRawResource(R.raw.help);
			byte[] t = new byte[is.available()];
			is.read(t);
			helpText.setText(Html.fromHtml(new String(t)));			
		} catch(Exception e){
			helpText.setText("Help text does not appear to exist.");
		}
	}
}
