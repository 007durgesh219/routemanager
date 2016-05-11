package com.BRP.routemanager.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.BRP.routemanager.utils.DbHelper;
import com.BRP.routemanager.R;
import com.BRP.routemanager.app.rmApp;

import java.io.File;
import java.util.ArrayList;

public class RouteCreatorHome extends Activity	
	implements OnItemSelectedListener
{
    /** Called when the activity is first created. */

    public static String Route,City,Corp;
    private ArrayList<String> spinnerText, corpSpinText;

    private static final String ROUTE = "ROUTE";
    private static final String SPINTEXT = "SPINTEXT";
 
    private EditText route,city,corp,dest,src;
    private Spinner spinner, corpSpinner;
    private TextView cityLabel,corpLabel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creatorhome);

	route = (EditText) findViewById(R.id.route_no);
   	city = (EditText) findViewById(R.id.addCity);
	corp = (EditText) findViewById(R.id.addCorp);
	src = (EditText) findViewById(R.id.src);
	dest = (EditText) findViewById(R.id.dest);
	cityLabel = (TextView) findViewById(R.id.addCityLabel);
	corpLabel = (TextView) findViewById(R.id.addCorpLabel);

	if(savedInstanceState != null) {
		route.setText(savedInstanceState.getString(ROUTE));
		city.setText(savedInstanceState.getString("CITY"));
		corp.setText(savedInstanceState.getString("CORP"));
		src.setText(savedInstanceState.getString("SRC"));
		dest.setText(savedInstanceState.getString("DEST"));
		spinnerText = savedInstanceState.getStringArrayList(SPINTEXT);
	}
	else {
		route.setText("");
		city.setText("");
		corp.setText("");
		src.setText("");
		dest.setText("");

		File dir = new File(DbHelper.DATABASE_PATH);
		dir.mkdirs();
		File files[] = dir.listFiles();
		int l;

		if (files != null) l = files.length;
		else l=0;

		spinnerText = new ArrayList<String>();
		spinnerText.add(getString(R.string.selectCity));
		spinnerText.add(getString(R.string.newCity));

		corpSpinText = new ArrayList<String>();
		corpSpinText.add("Please select city first");
		corpSpinText.add(getString(R.string.newCorp));

		for ( int i = 0; i<l; i++) {
			String name = files[i].getName();

			String temp = name.substring(0,name.indexOf("_Corp_"));

			temp.replaceAll("_"," ");

			if(name.contains("journal")==false) {
				spinnerText.add(temp);
			}
		}
	}

	spinner = (Spinner) findViewById(R.id.city_spinner);	
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerText);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

		corpSpinner = (Spinner) findViewById(R.id.corp_spinner);
		ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,corpSpinText);

		corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		corpSpinner.setAdapter(corpAdapter);
		corpSpinner.setOnItemSelectedListener(corpListen);
    }

    @Override
    public void onBackPressed() {
	Intent i = new Intent(this,RouteManager.class);
	startActivity(i);
	finish();
    }

	public OnItemSelectedListener corpListen = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			if (corpSpinText.size() == 1) {
				corp.setVisibility(View.VISIBLE);
				corpLabel.setVisibility(View.VISIBLE);
			} else {
				switch (i) {
					case 0: Corp = "";
						corp.setVisibility(View.GONE);
						corpLabel.setVisibility(View.GONE);
						break;
					case 1: Corp = "";
						corp.setVisibility(View.VISIBLE);
						corpLabel.setVisibility(View.VISIBLE);
						break;
					default:
						Corp =  corpSpinText.get(i);
						corp.setVisibility(View.GONE);
						corpLabel.setVisibility(View.GONE);
						break;
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
			corp.setVisibility(View.GONE);
			corpLabel.setVisibility(View.GONE);
			Corp = "";
		}
	};

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

		corpSpinText.clear();
		corpSpinText.trimToSize();

	switch(position) {
		case 0: City = "";
			corpSpinText.add("Please select city first");
			city.setVisibility(View.GONE);
			cityLabel.setVisibility(View.GONE);
			break;
		case 1: City = "";
			city.setVisibility(View.VISIBLE);
			cityLabel.setVisibility(View.VISIBLE);
			break;
		default: City = spinnerText.get(position);
			corpSpinText.add(getString(R.string.selectCorp));
			city.setVisibility(View.GONE);
			cityLabel.setVisibility(View.GONE);
			break;
	}

		corpSpinText.add(getString(R.string.newCorp));

		if (position > 1) {
			File dir = new File(DbHelper.DATABASE_PATH);
			dir.mkdirs();
			File files[] = dir.listFiles();
			int l;

			if (files != null) l = files.length;
			else l=0;

			for ( int i = 0; i<l; i++) {
				String name = files[i].getName();

				String temp = name.substring(0,name.indexOf("_Corp_"));

				temp.replaceAll("_"," ");

				if (temp.equalsIgnoreCase(City) && name.contains("journal") == false) {
					corpSpinText.add(name.substring(name.indexOf("_Corp_")+6).replaceAll("_"," "));
				}
			}
		}

		ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(RouteCreatorHome.this,android.R.layout.simple_spinner_item, corpSpinText);
		corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		corpSpinner.setAdapter(corpAdapter);
    }

    public void onNothingSelected(AdapterView<?> parent) {
	city.setVisibility(View.GONE);
	cityLabel.setVisibility(View.GONE);
	City = "";

		corpSpinText.clear();
		corpSpinText.trimToSize();
		corpSpinText.add("Please select city first!");
		corpSpinText.add(getString(R.string.newCorp));

		ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(RouteCreatorHome.this,android.R.layout.simple_spinner_item, corpSpinText);
		corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		corpSpinner.setAdapter(corpAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu items for use in the action bar
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_activity_actions, menu);
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putString(ROUTE, route.getText().toString());
	outState.putString("CITY", city.getText().toString());
	outState.putString("SRC",src.getText().toString());
	outState.putString("DEST",dest.getText().toString());
   	outState.putStringArrayList(SPINTEXT, spinnerText);
    }

    protected void openSettings() {}

    private void reset() {
	route.setText("");
	city.setText("");
	corp.setText("");
	src.setText("");
	dest.setText("");
	spinner.setSelection(0);
    }

    public void reset(View view) {
	reset();
    }

    public void create(View view) {
	Route = "Route_" + route.getText().toString().trim() + "_From_" + src.getText().toString().trim() + "_Towards_" + dest.getText().toString().trim();
	
	if (valid()) {
		City = City + "_Corp_" + Corp;
		RVnC();
	}
    }

    private boolean valid()  {
	Route =Route.trim();
	City = City.trim();
	Corp = Corp.trim();

	if (spinner.getSelectedItemPosition() == 0 ) {
		Toast.makeText(this, getString(R.string.cityEmpty), Toast.LENGTH_SHORT).show();
		return false;
	}

	if (spinner.getSelectedItemPosition() == 1) {
		City = city.getText().toString().trim();
		if (City.length() == 0) {
			Toast.makeText(this, getString(R.string.addCityEmpty), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	if (corpSpinText.get(corpSpinner.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.selectCorp)) ||
			corpSpinText.get(corpSpinner.getSelectedItemPosition()).contains("please select")) {
		Toast.makeText(this, getString(R.string.corpEmpty), Toast.LENGTH_SHORT).show();
	}

	if (corpSpinText.get(corpSpinner.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.newCorp))) {
		Corp = corp.getText().toString().trim();
		if (Corp.length() == 0) {
			Toast.makeText(this, getString(R.string.addCorpEmpty), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	if (route.getText().toString().trim().length() == 0) {
		Toast.makeText(this, getString(R.string.routeEmpty), Toast.LENGTH_SHORT).show();
		return false;
	}


	if (src.getText().toString().trim().length() == 0) {
		Toast.makeText(this, getString(R.string.srcEmpty), Toast.LENGTH_SHORT).show();
		return false;
	}

	if (dest.getText().toString().trim().length() == 0) {
		Toast.makeText(this, getString(R.string.destEmpty), Toast.LENGTH_SHORT).show();
		return false;
	}

	Route = Route.trim().replaceAll(" ","_");
	City = City.trim().replaceAll(" ","_");
	Corp = Corp.trim().replaceAll(" ","_");

	for ( int i = 0; i<City.length(); i++ ) {
		char ch = City.charAt(i);
		if ( !(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_' ) {
			Toast.makeText(this, getString(R.string.citySplCh), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	for ( int i = 0; i<Corp.length(); i++ ) {
		char ch = Corp.charAt(i);
		if (!(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_') {
			Toast.makeText(this, getString(R.string.corpSplCh), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	for ( int i = 0; i<Route.length(); i++ ) {
		char ch = Route.charAt(i);
		if ( !(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_' ) {
			if ( i < Route.indexOf("_From_") )
				Toast.makeText(this, getString(R.string.routeSplCh), Toast.LENGTH_SHORT).show();
			else if ( i < Route.indexOf("_Towards_") ) 
				Toast.makeText(this, getString(R.string.srcSplCh), Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, getString(R.string.destSplCh), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	if (City.charAt(City.length()-1) == '_') {
		Toast.makeText(this, getString(R.string.cityEndSplCh), Toast.LENGTH_SHORT).show();
		return false;
	}

	if (Corp.charAt(Corp.length()-1) == '_') {
		Toast.makeText(this, getString(R.string.corpEndSplCh), Toast.LENGTH_SHORT).show();
		return false;
	}

	if (Route.charAt(Route.indexOf("_From_")-1) == '_') {
		Toast.makeText(this, getString(R.string.routeEndSplCh), Toast.LENGTH_SHORT).show();
		return false;
	}

	if (Route.charAt(Route.indexOf("_Towards_")-1) == '_') {
		Toast.makeText(this, getString(R.string.srcEndSplCh), Toast.LENGTH_SHORT).show();
		return false;
	}
	
	if (Route.charAt(Route.length()-1) == '_') {
		Toast.makeText(this, getString(R.string.destEndSplCh), Toast.LENGTH_SHORT).show();
		return false;
	}

	return true;
    }

    private void RVnC() {
	ArrayList<String> Routes = new ArrayList<String>();
	DbHelper db = new DbHelper (rmApp.getAppContext(), City);
	Cursor c = db.getTables();
	if ( c != null && c.getCount()>0) {
		c.moveToFirst();
		while (!c.isAfterLast()) {
			Routes.add(c.getString(0));
			c.moveToNext();
		}
	}
	else {
		Routes.add("");
	}
	db.closeDB();

	boolean flag = true;
	for ( String temp : Routes) {
		if (Route.toLowerCase().equals(temp.toLowerCase())) {
			routeAlert();
			flag = false;
			break;
		}
	}
	
	if (flag) {
		Intent i = new Intent( this, RouteCreator.class);
		i.putExtra(getString(R.string.routeKey),Route);
		i.putExtra(getString(R.string.cityKey),City);
		i.putExtra(getString(R.string.parentKey),"create");
		startActivity(i);
		finish();
	}
    }

    private void routeAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("Route already exists!");
  
        // Setting Dialog Message
        alertDialog.setMessage("The route you are trying to create, already exists.\n Do you wish to edit this route, override existing route or create another route?");

        alertDialog.setPositiveButton("Override", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
		DbHelper db = new DbHelper(rmApp.getAppContext(), City, Route);
		db.delTable();
		db.closeDB();
		
		Intent i = new Intent( RouteCreatorHome.this, RouteCreator.class);
		i.putExtra(getString(R.string.routeKey),Route);
		i.putExtra(getString(R.string.cityKey),City);
		i.putExtra(getString(R.string.parentKey),"create");
		startActivity(i);
		finish();		
            }
        });
  
        alertDialog.setNegativeButton("Create Another", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
}