package club.psarda.dynamicinstantcircumengine.UI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.GlobalStatsFramgnet;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.ResultsFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.RollFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.SettingsFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.SaveStatsDialogFragmnet;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.StatsFocusFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.KeyStatsFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsSelectionFragment;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.SettingsData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage;
import club.psarda.dynamicinstantcircumengine.database.FireBase.FireBaseHelper;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 9001;

	private class ViewHolder{
		public ListView navList;
		public FrameLayout fragZone;
		public DrawerLayout drawerLayout;
	}

	private ViewHolder _viewHolder = new ViewHolder();
	private ActionBarDrawerToggle _drawerToggle;
	private FirebaseAuth _auth;
	private GoogleSignInOptions _gso;
	private GoogleApiClient _googleApiClient;
	private boolean _googleSignedIn;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		initialiseViewHolder();
		initialiseActionBar();
		
		_gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		_auth = FirebaseAuth.getInstance();

		_googleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, _gso)
				.build();
		
		 initialiseFrags();
	}
	
	@Override
	public void onStart() {
		super.onStart();

		initialiseUserLogin();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return _drawerToggle.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		_drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		_drawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(this, getString(R.string.could_not_connect_to_google), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		FragmentManager fragmentManager = getFragmentManager();

		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
		} else {
			finish();
		}
	}
	
	public void switchToStatsFocus(Bundle bundle){
		Fragment fragment = StatsFocusFragment.newInstance();
		fragment.setArguments(bundle);
		
		changeFragment(fragment);
	}
	
	public void switchToStats(Bundle bundle){
		Fragment fragment = KeyStatsFragment.newInstance();
		fragment.setArguments(bundle);
		
		changeFragment(fragment);
	}
	
	public void switchToSettings(){
		Fragment fragment = SettingsFragment.Companion.newInstance();
		
		changeFragment(fragment);
	}
	
	public void switchToGlobalStats(){
		Fragment fragment = GlobalStatsFramgnet.Companion.newInstance();
		
		changeFragment(fragment);
	}
	
	public void switchToRoll(Bundle bundle){
		Fragment fragment = RollFragment.newInstance();

		if(bundle != null){
			SettingsData defualtSettings = new SettingsData();

			if(bundle.getString("sides") == null){
				bundle.putString("sides", Long.toString(defualtSettings.getDefualtSides()));
			}

			if(bundle.getString("NumberOfDice") == null){
				bundle.putString("NumberOfDice", Long.toString(defualtSettings.getDefualtNumberOfDice()));
			}

			if(bundle.getStringArrayList("targets") == null){
				bundle.putStringArrayList("targets", defualtSettings.getTargetsAsStrings());
			}
		}

		fragment.setArguments(bundle);
		changeFragment(fragment);
	}
	
	public void showSaveDialog(StatsData statsData) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		// Create and show the dialog.
		DialogFragment newFragment = SaveStatsDialogFragmnet.newInstance(statsData);
		newFragment.show(ft, "dialog");
	}
	
	public void googleSignOut(){
		FirebaseAuth.getInstance().signOut();
		Auth.GoogleSignInApi.signOut(_googleApiClient).setResultCallback(
			new ResultCallback<Status>() {
				@Override
				public void onResult(Status status) {
					_auth.signInAnonymously();
				}
			}
		);

		_googleSignedIn = false;
	}
	
	public boolean getGoogleSignedIn(){
		return _googleSignedIn;
	}

	private boolean fuckyou = true;

	public void soogleSignIn(){

		if(fuckyou){
			Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
			startActivityForResult(signInIntent, RC_SIGN_IN);
			fuckyou = false;
		}else{
			fuckyou = true;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}


	public void initialiseUserLogin(){
		OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(_googleApiClient);
		if (opr.isDone()) {
			GoogleSignInResult result = opr.get();
			handleSignInResult(result);
		} else {
			handleSignInResult(null);
		}
	}
	
	private void handleSignInResult(GoogleSignInResult result) {
		if (result != null && result.isSuccess()) {
			GoogleSignInAccount acct = result.getSignInAccount();
			Toast.makeText(this, getString(R.string.signed_in_fmt, acct.getDisplayName()), Toast.LENGTH_LONG).show();
			firebaseAuthWithGoogle(acct);
			DataStorage.Companion.getINSTANCE().loadSettingsData();
			_googleSignedIn = true;
		} else {
			Toast.makeText(this, getString(R.string.annon_sign_in), Toast.LENGTH_LONG).show();
			signInAsAnno();
			_googleSignedIn = false;
		}
	}
	
	private void signInAsAnno(){
		_auth.signInAnonymously()
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						final String TAG = "FIREBASE";
						
						if (task.isSuccessful()) {
							FireBaseHelper.Companion.getInstance();
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInAnonymously:failure", task.getException());
							Toast.makeText(getApplicationContext(), "Authentication failed.",
									Toast.LENGTH_LONG).show();
						}
					}
				});
		
		_auth.signInAnonymously();
	}
	
	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		_auth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
						}else {
							Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void initialiseActionBar(){
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		initialiseNavigationBar();
		
		ActionBar actionBar = getSupportActionBar();
		
 		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		setupDrawer();
	}
	
	private void initialiseNavigationBar(){
		addNavigationItems(_viewHolder.navList);
		setNavigationClickListner(_viewHolder.navList);
	}

	private void addNavigationItems(ListView listView) {
		String[] entires = getResources().getStringArray(R.array.fragments_selector);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entires);
		listView.setAdapter(adapter);
	}

	private void setNavigationClickListner(ListView listView){
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Fragment fragment = null;

				switch (position){
					case 0:
						switchToRoll(null);
						break;

					case 1:
						fragment = StatsSelectionFragment.Companion.newInstance();
						break;
						
					case 2:
						switchToGlobalStats();
						break;
						
					case 3:
						switchToSettings();
						break;
				}

				if(fragment != null){
					changeFragment(fragment);
				}

				_viewHolder.drawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
	}

	private void setupDrawer(){
		_drawerToggle = new ActionBarDrawerToggle(this, _viewHolder.drawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close){
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				
			}
		};
		
		_drawerToggle.setDrawerIndicatorEnabled(true);
		_viewHolder.drawerLayout.addDrawerListener(_drawerToggle);
		
	}
	
	private void initialiseFrags(){
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.base_fragment, RollFragment.newInstance());
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		updateActionTitle(RollFragment.class);
	}
	
	private void changeFragment(Fragment fragment){
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.base_fragment, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		updateActionTitle(fragment.getClass());
	}
	
	private void updateActionTitle(Class fragmentClass){

		// This did work a diffrenet way but then i changed it but
		// im not changing each if statement
		Class<?> curFragClass = fragmentClass;
		Integer newTitle = R.string.roll_fragment_title;
		
		if(curFragClass == RollFragment.class){
			//Default
		} else if(curFragClass == ResultsFragment.class){
			newTitle = R.string.result_fragmnet_title;
		} else if(curFragClass == KeyStatsFragment.class){
			newTitle = R.string.stats_fragmnet_title;
		} else if(curFragClass == StatsSelectionFragment.class){
			newTitle = R.string.stats_fragmnet_selection_title;
		} else if(curFragClass == StatsFocusFragment.class){
			newTitle = R.string.stats_focus_fragmnet_title;
		} else if(curFragClass == SettingsFragment.class){
			newTitle = R.string.settings_title;
		}else if (curFragClass == GlobalStatsFramgnet.class){
			newTitle = R.string.global_stats_fragment_title;
		}
		
		getSupportActionBar().setTitle(getString(newTitle));
	}
	
	private void initialiseViewHolder(){
		_viewHolder.navList = (ListView)findViewById(R.id.navList);
		_viewHolder.fragZone = (FrameLayout)findViewById(R.id.base_fragment);
		_viewHolder.drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
	}
}
