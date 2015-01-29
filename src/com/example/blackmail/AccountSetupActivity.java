		package com.example.blackmail;
		
		import java.util.Arrays;
		import java.security.MessageDigest;
		import java.security.NoSuchAlgorithmException;
		import java.util.List;
		
		import twitter4j.StatusUpdate;
		import twitter4j.Twitter;
		import twitter4j.TwitterException;
		import twitter4j.TwitterFactory;
		import twitter4j.auth.AccessToken;
		import twitter4j.auth.RequestToken;
		import twitter4j.conf.Configuration;
		import twitter4j.conf.ConfigurationBuilder;
		import android.app.Activity;
		import android.app.AlertDialog;
		import android.content.Intent;
		import android.content.SharedPreferences;
		import android.content.SharedPreferences.Editor;
		import android.content.pm.PackageInfo; 	 	
		import android.content.pm.PackageManager; 	 	
		import android.content.pm.PackageManager.NameNotFoundException; 	 	
		import android.content.pm.Signature; 
		import android.graphics.Bitmap;
		import android.graphics.BitmapFactory;
		import android.os.AsyncTask;
		import android.util.Base64; 
		import android.os.Bundle;
		import android.os.StrictMode;
		import android.support.v4.app.FragmentActivity;
		import android.support.v4.app.FragmentManager;
		import android.util.Log;
		import android.view.View;
		import android.view.View.OnClickListener;
		import android.view.ViewGroup;
		import android.widget.LinearLayout;
		import android.widget.RelativeLayout;
		
		import com.example.blackmail.DBObjects.API_Master;
		import com.example.blackmail.DBObjects.Blackmail;
		import com.facebook.AppEventsLogger;
		import com.facebook.*;
		import com.facebook.android.Facebook;
		import com.facebook.model.GraphObject;
		import com.facebook.model.GraphPlace;
		import com.facebook.model.GraphUser;
		import com.facebook.widget.*;
		
		/*import com.facebook.AppEventsLogger;
		 import com.facebook.FacebookRequestError;
		 import com.facebook.Request;
		 import com.facebook.Response;
		 import com.facebook.Session;
		 import com.facebook.SessionState;
		 import com.facebook.UiLifecycleHelper;
		 import com.facebook.model.GraphObject;
		 import com.facebook.model.GraphPlace;
		 import com.facebook.model.GraphUser;
		 import com.facebook.widget.FacebookDialog;
		 import com.facebook.widget.LoginButton;
		 import com.facebook.widget.ProfilePictureView;*/
		
		public class AccountSetupActivity extends FragmentActivity implements
				OnClickListener {
			/************************* facebook changes statrt ******************************/
			private static final String PERMISSION = "publish_actions";
		
			private final String PENDING_ACTION_BUNDLE_KEY = "com.example.blackmail:PendingAction";
		
			private LoginButton loginButton;
		
			private PendingAction pendingAction = PendingAction.NONE;
			private ViewGroup controlsContainer;
			private GraphUser user;
			private GraphPlace place;
			private List<GraphUser> tags;
			private boolean canPresentShareDialog;
			private boolean canPresentShareDialogWithPhotos;
			private static int var = 1;
			public int motivation=1;
			public static String fb_token;
		
			private enum PendingAction {
				NONE, POST_PHOTO, POST_STATUS_UPDATE
			}
		
			private UiLifecycleHelper uiHelper;
		
			private Session.StatusCallback callback_facebook = new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					onSessionStateChange(session, state, exception);
				}
			};
		
			private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall,
						Exception error, Bundle data) {
					Log.d("Hello BlackMail Facebook",
							String.format("Error: %s", error.toString()));
				}
		
				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall,
						Bundle data) {
					Log.d("Hello BlackMail Facebook", "Success!");
				}
			};
			/************************* facebook changes end *******************/
			protected SharedPreferences twitterAccessInfo;
		
			protected View loginLayout;
			protected View postLayout;
			protected View facebook_layout;
		
			protected Twitter twitter;
			protected RequestToken requestToken;
		
			private static final String consumerKey = "WTchQTjvGFsbswbW6FTF8kFCL";
			private static final String consumerKeySecret = "Ii1K2xzyVQCABMjIsYK2v0D6Q3S7soHYu3mYAPfPDMNMYVQEtd";
			private String callback = "http://blackmail.android.app";
		
			public static final int webCode = 100;
			
		
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				try {
			          PackageInfo info = getPackageManager().getPackageInfo(
			                  "com.example.blackmail", 
			                  PackageManager.GET_SIGNATURES);
			          for (Signature signature : info.signatures) {
			              MessageDigest md = MessageDigest.getInstance("SHA");
			              md.update(signature.toByteArray());
			              Log.d("Testing:", "Hi key ::  "+Base64.encodeToString(md.digest(), Base64.DEFAULT));
			              }
			      } catch (NameNotFoundException e) {
		
			      } catch (NoSuchAlgorithmException e) {
		
			      }
		
				/*************** facebook changes start **********************/
				uiHelper = new UiLifecycleHelper(this, callback_facebook);
				uiHelper.onCreate(savedInstanceState);
		
				if (savedInstanceState != null) {
					String name = savedInstanceState
							.getString(PENDING_ACTION_BUNDLE_KEY);
					pendingAction = PendingAction.valueOf(name);
				}
				/*************** facebook changes end **********************/
				setContentView(R.layout.account_setup);
		
				/*************** Twitter changes start **********************/
		
				// Probably this but may be needed
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
		
				loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
				postLayout = (LinearLayout) findViewById(R.id.post_layout);
				facebook_layout = (LinearLayout) findViewById(R.id.facebook_layout);
		
				findViewById(R.id.loginTwitterButton).setOnClickListener(this);
				findViewById(R.id.updateTwitterButton).setOnClickListener(this);
		
				twitterAccessInfo = getSharedPreferences("twitterAccessInfo", 0);
				boolean loggedIn = twitterAccessInfo.getBoolean("loggedIn", false);
		
				if (loggedIn) {
					loginLayout.setVisibility(View.GONE);
					postLayout.setVisibility(View.VISIBLE);
				}
				/*************** Twitter changes end **********************/
		
				/*************** facebook changes start **********************/
				loginButton = (LoginButton) findViewById(R.id.login_button);
				loginButton
						.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
							@Override
							public void onUserInfoFetched(GraphUser user) {
								AccountSetupActivity.this.user = user;
								updateUI();
								// It's possible that we were waiting for this.user to
								// be populated in order to post a
								// status update.
								
							}
						});
		
				// Can we present the share dialog for regular links?
				/*
				canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
				// Can we present the share dialog for photos?
				canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(
						this, FacebookDialog.ShareDialogFeature.PHOTOS);*/
		
				
		
				final FragmentManager fm = getSupportFragmentManager();
		
				// Listen for changes in the back stack so we know if a fragment got
				// popped off because the user
				fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
					@Override
					public void onBackStackChanged() {
						if (fm.getBackStackEntryCount() == 0) {
							// We need to re-show our UI.
							controlsContainer.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		
			@Override
			protected void onResume() {
				super.onResume();
				uiHelper.onResume();
		
				// Call the 'activateApp' method to log an app event for use in
				// analytics and advertising reporting. Do so in
				// the onResume methods of the primary Activities that an app may be
				// launched into.
				AppEventsLogger.activateApp(this);
		
				updateUI();
			}
		
			@Override
			protected void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
				uiHelper.onSaveInstanceState(outState);
		
				outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
			}
		
			@Override
			public void onPause() {
				super.onPause();
				uiHelper.onPause();
		
				// Call the 'deactivateApp' method to log an app event for use in
				// analytics and advertising
				// reporting. Do so in the onPause methods of the primary Activities
				// that an app may be launched into.
				AppEventsLogger.deactivateApp(this);
			}
		
			@Override
			public void onDestroy() {
				super.onDestroy();
				uiHelper.onDestroy();
			}
		
			private void onSessionStateChange(Session session, SessionState state,
					Exception exception) {
				Log.d("Maven", "onSessionStateChange = " + pendingAction);
				if (state.isOpened()) {
					facebook_layout.setVisibility(View.GONE);
				} else if (state.isClosed()) {
					facebook_layout.setVisibility(View.VISIBLE);
				}
		
				if (pendingAction != PendingAction.NONE
						&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
					Log.d("Maven", "onSessionStateChange -Inside If --" + pendingAction);
					new AlertDialog.Builder(AccountSetupActivity.this)
							.setTitle(R.string.cancelled)
							.setMessage(R.string.permission_not_granted)
							.setPositiveButton(R.string.ok, null).show();
					pendingAction = PendingAction.NONE;
				} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
					Log.d("Maven", "onSessionStateChange -OPEN_TOKEN_UPDATED --" + true);
					
				}
				updateUI();
			}
		
			private void updateUI() {
				Session session = Session.getActiveSession();
				Log.d("Maven", "UpdateUi -- session -- " + session
						+ " -- Session IsOpen -- " + session.isOpened());
				
				if(fb_token==null){
					API_Master apiMasterData = new DBObjects().new API_Master();
					DBWrapper dbw = new DBWrapper(this);
					fb_token =  session.getAccessToken();

					apiMasterData.AccessToken =fb_token;
					
					apiMasterData.APIName = DBWrapper.APIName_Facebook;
					apiMasterData.APITypeId = dbw
							.GetAPITypeID(DBWrapper.APIName_Facebook);
					apiMasterData.APITableName = dbw
							.Read_API_Master_Data(apiMasterData.APITypeId).APITableName;
					apiMasterData.IsActive = true;
					apiMasterData.CreatedDate = new java.sql.Date(
							new java.util.Date().getTime());

					dbw.Update_API_Master_Data_Tokens(apiMasterData);
					}
			}
		
			@SuppressWarnings("incomplete-switch")
			private void handlePendingAction(String imagepath) {
				PendingAction previouslyPendingAction = pendingAction;
				// These actions may re-set pendingAction if they are still pending, but
				// we assume they
				// will succeed.
				pendingAction = PendingAction.NONE;
		
				Log.d("Maven", "HandlePending Action = " + previouslyPendingAction);
		
				switch (previouslyPendingAction) {
				case POST_PHOTO:
					postPhoto(imagepath);
					break;
		
				case POST_STATUS_UPDATE:
					postStatusUpdate(imagepath);
					break;
		
				}
			}
		
			private interface GraphObjectWithId extends GraphObject {
				String getId();
			}
		
			private void showPublishResult(String message, GraphObject result,
					FacebookRequestError error) {
				 String title = null;
			        String alertMessage = null;
			        String temp="post";
			        if (error == null) {
			            title =  temp;//getString(R.string.success);
			            String id = result.cast(GraphObjectWithId.class).getId();
			            alertMessage = temp; //getString(R.string.successfully_posted_post, message, id);
			        } else {
			            title = temp; //getString(R.string.error);
			            alertMessage = temp;//error.getErrorMessage();
			        }

			}
		
			public void onClickPostStatusUpdate(String status) {
				Log.d("Maven", "ONCLICK STATUS UPDATE");
				performPublish(PendingAction.POST_STATUS_UPDATE, status);
			}
		
			private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
				return new FacebookDialog.ShareDialogBuilder(this);
			}
		
			private void postStatusUpdate(String Status) {
				Log.d("Maven", "INSIDE POST STATUS UPDATE");
				Log.d("Maven", "haspublishpermission" + hasPublishPermission());
				Log.d("Maven", "Active Session" + Session.getActiveSession());
				Log.d("Maven", "user " + user);
		        /*DBWrapper dbw = new DBWrapper(this);
				
				Blackmail b = dbw.Get_Blackmail_Data(motivation);
		*/
				if ( hasPublishPermission()) {
					Log.d("Maven", "user " + user);
					final String message = Status; 
					Request request = Request.newStatusUpdateRequest(
							Session.getActiveSession(), message, place, tags,
							new Request.Callback() {
								@Override
								public void onCompleted(Response response) {
									showPublishResult(message,
											response.getGraphObject(),
											response.getError());
								}
							});
					request.executeAsync();
					pendingAction = PendingAction.NONE;
				} else {
					pendingAction = PendingAction.POST_STATUS_UPDATE;
				}
			}
		
			public void onClickPostPhoto(String imagepath) {
				Log.d("Maven", "ONCLICK POST PHOTO");
				performPublish(PendingAction.POST_PHOTO,
						imagepath);
			}
		
			private FacebookDialog.PhotoShareDialogBuilder createShareDialogBuilderForPhoto(
					Bitmap... photos) {
				return new FacebookDialog.PhotoShareDialogBuilder(this)
						.addPhotos(Arrays.asList(photos));
			}
		
			private void postPhoto(String imagepath) {
				Log.d("VARUN", "postPhoto ");
				//String imagepath;
				
				Bitmap image;
				/*DBWrapper dbw = new DBWrapper(this);
				
				Blackmail b = dbw.Get_Blackmail_Data(motivation);
				imagepath=b.BlackmailImagePath;
				*/
				image = BitmapFactory.decodeFile(imagepath);
					
				
				if (hasPublishPermission()) {
					Request request = Request.newUploadPhotoRequest(
							Session.getActiveSession(), image, new Request.Callback() {
								@Override
								public void onCompleted(Response response) {
									String temp="post";
									Log.d("Maven", "postPhoto 2 - "
											+ hasPublishPermission());
									showPublishResult(temp,
											response.getGraphObject(),
											response.getError());
									pendingAction = PendingAction.NONE;
								}
							});
					request.executeAsync();
				} else {
					pendingAction = PendingAction.POST_PHOTO;
				}
			}
		
			private void showAlert(String title, String message) {
				new AlertDialog.Builder(this).setTitle(title).setMessage(message)
						.setPositiveButton(R.string.ok, null).show();
			}
		
			private boolean hasPublishPermission() {
				Session session = Session.getActiveSession();
				Log.d("Maven", "Inside hasPublishPermission () = " + session);
				return session != null
						&& session.getPermissions().contains("publish_actions");
			}
		
			private void performPublish(PendingAction action, String imagepath) {
				Session session = Session.getActiveSession();
				
				if (session != null) {
					pendingAction = action;
					if (hasPublishPermission()) {
						Log.d("Maven", "permission = " + hasPublishPermission());
						// We can do the action right away.
						handlePendingAction(imagepath);
						return;
					} else if (session.isOpened()) {
		
						// We need to get new permissions, then complete the action when
						// we get called back.
						session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
								this, PERMISSION));
						Log.d("Maven", "session is opened ");
						// ROHIT DELETE
		
						return;
					}
				}
		
				
			}
		
			/*************** facebook changes end **********************/
		
			protected void loginToTwitter() {
		
				boolean loggedIn = twitterAccessInfo.getBoolean("loggedIn", false);
		
				if (loggedIn) {
					loginLayout.setVisibility(View.GONE);
					postLayout.setVisibility(View.VISIBLE);
				} else {
					ConfigurationBuilder builder = new ConfigurationBuilder();
					builder.setOAuthConsumerKey(consumerKey);
					builder.setOAuthConsumerSecret(consumerKeySecret);
		
					Configuration configuration = builder.build();
					TwitterFactory factory = new TwitterFactory(configuration);
					twitter = factory.getInstance();
		
					try {
						requestToken = twitter.getOAuthRequestToken(callback);
						Intent intent = new Intent(this, WebViewActivity.class);
						intent.putExtra(WebViewActivity.requestURL,
								requestToken.getAuthenticationURL());
						startActivityForResult(intent, webCode);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}
			}
		
			protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
				if (resultCode == Activity.RESULT_OK) {
					String verifier = data.getExtras().getString("oauth_verifier");
					try {
						AccessToken accessToken = twitter.getOAuthAccessToken(
								requestToken, verifier);
		
						Editor editor = twitterAccessInfo.edit();
						editor.putString("accessToken", accessToken.getToken());
						editor.putString("accessTokenSecret",
								accessToken.getTokenSecret());
						editor.putBoolean("loggedIn", true);
						editor.commit();
		
						API_Master apiMasterData = new DBObjects().new API_Master();
						DBWrapper dbw = new DBWrapper(this);
		
						apiMasterData.AccessToken = accessToken.getToken();
						apiMasterData.AccessTokenSecret = accessToken.getTokenSecret();
						apiMasterData.APIName = DBWrapper.APIName_Twitter;
						apiMasterData.APITypeId = dbw
								.GetAPITypeID(DBWrapper.APIName_Twitter);
						apiMasterData.APITableName = dbw
								.Read_API_Master_Data(apiMasterData.APITypeId).APITableName;
						apiMasterData.IsActive = true;
						apiMasterData.CreatedDate = new java.sql.Date(
								new java.util.Date().getTime());
		
						dbw.Update_API_Master_Data_Tokens(apiMasterData);
		
						loginLayout.setVisibility(View.GONE);
						//postLayout.setVisibility(View.VISIBLE);
					} catch (Exception e) {
						Log.e("Twitter Login Failed", e.getMessage());
					}
				}
				super.onActivityResult(requestCode, resultCode, data);
				uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
			}
		
			class updateTwitterStatus extends AsyncTask<String, String, Void> {
		
				protected Void doInBackground(String... args) {
		
					String status = args[0];
					try {
						ConfigurationBuilder builder = new ConfigurationBuilder();
						builder.setOAuthConsumerKey(consumerKey);
						builder.setOAuthConsumerSecret(consumerKeySecret);
		
						// Access Token - Instead of preferences get from db
						String accessToken = twitterAccessInfo.getString("accessToken",
								"");
		
						// Access Token Secret - Instead of preferences get from db
						String accessTokenSecret = twitterAccessInfo.getString(
								"accessTokenSecret", "");
		
						AccessToken token = new AccessToken(accessToken,
								accessTokenSecret);
						Twitter twitter = new TwitterFactory(builder.build())
								.getInstance(token);
		
						// Update status
						StatusUpdate statusUpdate = new StatusUpdate(status);
		
						// Need string path of image you want to upload
						// File yourFile = new
						// File("/sdcard/Pictures/Messenger/reflection_mario.jpeg");
		
						// statusUpdate.setMedia(yourFile);
						twitter.updateStatus(statusUpdate);
		
					} catch (TwitterException e) {
						Log.d("No Twitter Post", e.getMessage());
					}
					return null;
				}
			}
		
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.loginTwitterButton:
					loginToTwitter();
					break;
				case R.id.updateTwitterButton:
					new updateTwitterStatus().execute("Anything");
					break;
				}
			}
		}