package com.example.blackmail;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Session;

//This class is what the database calls in order to post a blackmail.
public class Posting {
	
	public class TwitterPost extends AsyncTask<String, String, Void> {
		String accessToken;
		String accessTokenSecret;
		String fpOrMsg;
		boolean isPhoto;
		
		public boolean twitterPhoto(String token, String tokenSecret, String filepath){
			accessToken = token;
			accessTokenSecret = tokenSecret;
			isPhoto = true;
			fpOrMsg = filepath;
			execute(""); // We could have passed the args here, but I didn't feel like trying it
			             // because I really wasn't sure.
			return false;
		}
		
		public boolean twitterText(String token, String tokenSecret, String message){
			accessToken = token;
			accessTokenSecret = tokenSecret;
			isPhoto = false;
			fpOrMsg = message;
			execute(""); // We could have passed the args here, but I didn't feel like trying it
						 // because I really wasn't sure.
			return false;
		}
		
		protected Void doInBackground(String... args) {
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey("WTchQTjvGFsbswbW6FTF8kFCL");
				builder.setOAuthConsumerSecret("Ii1K2xzyVQCABMjIsYK2v0D6Q3S7soHYu3mYAPfPDMNMYVQEtd");

				AccessToken token = new AccessToken(accessToken, accessTokenSecret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(token);
				StatusUpdate su;
				
				//Photo post
				if(isPhoto){
					File yourFile = new File(fpOrMsg);
					su = new StatusUpdate("Posted from Blackmail");
					su.setMedia(yourFile);
				}
				//Message post
				else {
					su = new StatusUpdate(fpOrMsg);
				}
				
				twitter.updateStatus(su);
		
			} catch (TwitterException e) {
				Log.d("No Twitter Post", e.getMessage());
			}
			return null;
		}
	}
	/*
	public static boolean facebookPhoto(){
		private void performPublish(PendingAction action, boolean allowNoSession) {
	        Session session = Session.getActiveSession();
	        Log.d("Maven","performPublish - " + allowNoSession);
	        if (session != null) {
	            pendingAction = action;
	            if (hasPublishPermission()) {
	            	  Log.d("Maven","permission = " + hasPublishPermission());
	                // We can do the action right away.
	                handlePendingAction();
	                return;
	            } else if (session.isOpened()) {
	            	 
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
	                Log.d("Maven","session is opened " );
	                // ROHIT DELETE

	                return;
	            }
	        }

	        if (allowNoSession) {
	        	 Log.d("Maven","allowNOsession " + allowNoSession);
	            pendingAction = action;
	            handlePendingAction();
	        }
	    }
		return false;
	}
	
	public static boolean facebooxText(){
		return false;
	}
	*/

}
