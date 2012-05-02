package com.facebook;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class DoTask extends AsyncTask<Void, Void, Void> {	

	protected ProgressDialog progress;
	
	public DoTask(ProgressDialog progress) {
		this.progress = progress;
	}

	public void onPreExecute() {
		progress.show();
	}

	public void onPostExecute(Void unused) {
		progress.dismiss();
		}

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}