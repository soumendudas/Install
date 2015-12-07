package com.install.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener{
	private final String TAG = RegistrationActivity.class.getCanonicalName();
	private SharedPreferences mPrefs;
	private EditText etFirstName;
	private EditText etMiddleName;
	private EditText etLastName;
	private EditText etPhoneNumber;
	private EditText etEmail;
	private EditText etLocation;
	private Button btnCancelRegist;
	private Button btnOkRegist;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		initUI();
	}

	@Override
	public void onClick(View view) {
		if(view == btnOkRegist){
			checkAndRegister();

		}else if(view == btnCancelRegist){
			this.finish();
		}
	}

	private void checkAndRegister() {
		String firstNameTxt = etFirstName.getText().toString();
		String middleNameTxt = etMiddleName.getText().toString();
		String lastNameTxt = etLastName.getText().toString();
		String phoneNumberTxt = etPhoneNumber.getText().toString();
		String emailTxt = etEmail.getText().toString();
		String locationTxt = etLocation.getText().toString();

		/**
		 * Method to validate user input and register a user over web server[Cloud]
		 */
			new RegistrationAsyncTask(Utils.getDeviceImeiNumber(RegistrationActivity.this),
					firstNameTxt,
					middleNameTxt,
					lastNameTxt,
					phoneNumberTxt,
					emailTxt,
					locationTxt).execute();
	}

	private boolean validate(String firstNameTxt, String middleNameTxt,
			String lastNameTxt, String phoneNumberTxt, String emailTxt,
			String locationTxt) {
		boolean flag = true;

		if(firstNameTxt.trim().equals("")){
			Toast.makeText(this, "First Name Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;			
		}
		if(middleNameTxt.trim().equals("")){
			Toast.makeText(this, "Middle Name Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;			
		}
		if(lastNameTxt.trim().equals("")){
			Toast.makeText(this, "Last Name Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;			
		}
		

		if(phoneNumberTxt.trim().equals("")){
			Toast.makeText(this, "Phone Number Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;
		}else{
			String match = "";
			String pattern = "^[1-9][0-9]{9}$";	
			Pattern regEx = Pattern.compile(pattern);
			String phone_value = phoneNumberTxt.trim();
			Matcher matcher = regEx.matcher(phone_value); 
			if (matcher.find()){
				match = matcher.group();
				Log.v(TAG, "Phone Match found.");

			}else if(phone_value.startsWith("0")){
				Toast.makeText(this, "Please do not enter phone number starting with 0.", Toast.LENGTH_SHORT).show();
				flag = false;
				return flag;
				//et_phoneNumber.setSelection(phone_value.length());
				//et_phoneNumber.setFocusable(true);
				//et_phoneNumber.requestFocus();
			}else{
				Toast.makeText(this, "Please enter mobile number of 10 digits.", Toast.LENGTH_SHORT).show();
				flag = false;
				return flag;
				//et_phoneNumber.setSelection(phone_value.length());
				//et_phoneNumber.setFocusable(true);
				//et_phoneNumber.requestFocus();
			}
		}
		
		if(emailTxt.trim().equals("")){
			Toast.makeText(this, "Email Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;			
		}
		if(locationTxt.trim().equals("")){
			Toast.makeText(this, "Location Shouldn't be empty.", Toast.LENGTH_SHORT).show();
			flag = false;
			return flag;			
		}


		return true;
	}


	private void initUI() {
		etFirstName = (EditText)findViewById(R.id.etFirstName);
		etMiddleName = (EditText)findViewById(R.id.etMiddleName);
		etLastName = (EditText)findViewById(R.id.etLastName);
		etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
		etEmail = (EditText)findViewById(R.id.etEmail);
		etLocation = (EditText)findViewById(R.id.etLocation);
		btnOkRegist = (Button)findViewById(R.id.btnOkRegist);
		btnCancelRegist = (Button)findViewById(R.id.btnCancelRegist);

		btnOkRegist.setOnClickListener(this);
		btnCancelRegist.setOnClickListener(this);

	}


	private class RegistrationAsyncTask extends AsyncTask<String, Integer, String> {
		private String deviceImeiNumber;
		private String firstNameTxt;
		private String middleNameTxt;
		private String lastNameTxt;
		private String phoneNumberTxt;
		private String emailTxt;
		private String locationTxt;
		
		public RegistrationAsyncTask(String deviceImeiNumber,
				String firstNameTxt, String middleNameTxt, String lastNameTxt,
				String phoneNumberTxt, String emailTxt, String locationTxt) {
			this.deviceImeiNumber = deviceImeiNumber;
			this.firstNameTxt = firstNameTxt;
			this.middleNameTxt = middleNameTxt;
			this.lastNameTxt = lastNameTxt;
			this.phoneNumberTxt = phoneNumberTxt;
			this.emailTxt = emailTxt;
			this.locationTxt = locationTxt;
		}
		private ProgressDialog pDialog;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();			
			pDialog = new ProgressDialog(RegistrationActivity.this);
			pDialog.setMessage(Html.fromHtml("<b>Wait</b><br/>Loading data..."));
			pDialog.setIndeterminate(true);
			//pDialog.setCancelable(false);
			pDialog.show(); 

		}

		@Override
		protected String doInBackground(String... arg0) {
			String result = null;
			String url = null;
			
			try{
				url = Utils.BASE_URL + Utils.USER_REGISTRATION_URL 
						+ deviceImeiNumber + "/"
						+ firstNameTxt +"/" 
						+ middleNameTxt + "/" 
						+ lastNameTxt + "/"
						+ phoneNumberTxt + "/" 
						+ emailTxt+ "/" 
						+ locationTxt;
				
				if(url!=null &&url.length()>0){
					Log.v(TAG, "URL: "+url);
					JSONParser jParser = new JSONParser();
					result = jParser.getResponseString(url);
					if(result!=null){
						Log.v(TAG, "EVENT RESPONSE: "+result);
					}
					return result;
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(pDialog.isShowing()) 
					pDialog.dismiss();

			if(result!=null && result.length()>0 ){
				mPrefs = RegistrationActivity.this.getSharedPreferences("INSTALLED_PREFS", Context.MODE_PRIVATE);
				try{
				JSONObject jObj = new JSONObject(result);
				if(jObj!=null && jObj.length()>0){
					if(jObj.has("status")){
						boolean status = jObj.getBoolean("status");
						//if(status){
							Editor editor = mPrefs.edit();
							editor.putBoolean("REGISTERED", true);
							editor.commit();
							RegistrationActivity.this.finish();
						//}
					}
				}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}

	}


}
