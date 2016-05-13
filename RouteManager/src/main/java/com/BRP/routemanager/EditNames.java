package com.BRP.routemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BRP.routemanager.activites.RouteManager;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.DbHelper;

import java.util.ArrayList;

public class EditNames extends Activity
        implements OnItemSelectedListener {
    private int itemPos;

    private ArrayList<String> stops;
    private String routeName, cityName, parent;
    private DbHelper db;

    private TextView currRoute, currSrc, currDest;
    private EditText routeNew, srcNew, destNew, stopNew;
    private Spinner stopList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnames);

        Intent i = getIntent();
        routeName = i.getStringExtra(getString(R.string.routeKey));
        cityName = i.getStringExtra(getString(R.string.cityKey));
        parent = i.getStringExtra(getString(R.string.parentKey));

        currRoute = (TextView) findViewById(R.id.currRoute);
        currSrc = (TextView) findViewById(R.id.currSrc);
        currDest = (TextView) findViewById(R.id.currDest);
        routeNew = (EditText) findViewById(R.id.routeNew);
        srcNew = (EditText) findViewById(R.id.srcNew);
        destNew = (EditText) findViewById(R.id.destNew);
        stopNew = (EditText) findViewById(R.id.stopNew);

        db = new DbHelper(rmApp.getAppContext(), cityName, routeName);

        stops = new ArrayList<String>();
        stops.add(getString(R.string.stopSel));

        Cursor c = db.showTable();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                stops.add(c.getString(1));
                c.moveToNext();
            }
        }

        if (savedInstanceState != null) {
            currRoute.setText(savedInstanceState.getString("currRoute"));
            currSrc.setText(savedInstanceState.getString("currSrc"));
            currDest.setText(savedInstanceState.getString("currDest"));
            routeNew.setText(savedInstanceState.getString("routeNew"));
            srcNew.setText(savedInstanceState.getString("srcNew"));
            destNew.setText(savedInstanceState.getString("destNew"));
            stopNew.setText(savedInstanceState.getString("stopNew"));
        } else {
            currRoute.setText(routeName.substring(6, routeName.indexOf("_From_")).replaceAll("_", " "));
            currSrc.setText(routeName.substring(routeName.indexOf("_From_") + 6, routeName.indexOf("_Towards_")).replaceAll("_", " "));
            currDest.setText(routeName.substring(routeName.indexOf("_Towards_") + 9).replaceAll("_", " "));
        }

        stopList = (Spinner) findViewById(R.id.stopSelectSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopList.setAdapter(adapter);
        stopList.setOnItemSelectedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
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

    protected void openSettings() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currRoute", currRoute.getText().toString());
        outState.putString("currSrc", currSrc.getText().toString());
        outState.putString("currDest", currDest.getText().toString());
        outState.putString("routeNew", routeNew.getText().toString());
        outState.putString("srcNew", srcNew.getText().toString());
        outState.putString("destNew", destNew.getText().toString());
        outState.putString("stopNew", stopNew.getText().toString());
    }

    @Override
    public void onBackPressed() {
        db.closeDB();
        Intent i;
        if (parent.equals("saved")) {
            i = new Intent(this, EditSavedHome.class);
        } else {
            i = new Intent(this, RouteManager.class);
        }
        startActivity(i);
        finish();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        itemPos = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        itemPos = 0;
    }

    private boolean checkRoute(char qualifier) {
        String Route;
        if (qualifier == 'r') {
            Route = "Route_" + routeNew.getText().toString().trim().replaceAll(" ", "_") +
                    "_From_" + currSrc.getText().toString().trim().replaceAll(" ", "_") +
                    "_Towards_" + currDest.getText().toString().trim().replaceAll(" ", "_");
        } else if (qualifier == 's') {
            Route = "Route_" + currRoute.getText().toString().trim().replaceAll(" ", "_") +
                    "_From_" + currSrc.getText().toString().trim().replaceAll(" ", "_") +
                    "_Towards_" + destNew.getText().toString().trim().replaceAll(" ", "_");
        } else if (qualifier == 'd') {
            Route = "Route_" + currRoute.getText().toString().trim().replaceAll(" ", "_") +
                    "_From_" + srcNew.getText().toString().trim().replaceAll(" ", "_") +
                    "_Towards_" + currDest.getText().toString().trim().replaceAll(" ", "_");
        } else {
            Route = "Route_From_Towards_";
        }

        if (Route.toLowerCase().equals(routeName.toLowerCase())) {
            return true;
        }

        Cursor cursor = db.getTables();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (Route.toLowerCase().equals(cursor.getString(0).toLowerCase())) {
                    showRouteAlert();
                    return false;
                }
                cursor.moveToNext();
            }
            return true;
        } else {
            return false;
        }
    }

    private void showRouteAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Route already exists!");

        // Setting Dialog Message
        alertDialog.setMessage("The route you are trying to change the name to already exists.\nPlease try another Route No. or Source or Destination!");

        alertDialog.setNegativeButton("Try Again!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void routeUpdate(View view) {
        String newRoute = routeNew.getText().toString().trim();

        if (newRoute.length() == 0) {
            Toast.makeText(this, getString(R.string.routeEmpty), Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < newRoute.length(); i++) {
            char ch = newRoute.charAt(i);
            if (!(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_' && ch != ' ') {
                Toast.makeText(this, getString(R.string.routeSplCh), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (newRoute.charAt(newRoute.length() - 1) == '_') {
            Toast.makeText(this, getString(R.string.routeEndSplCh), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkRoute('r')) {
            currRoute.setText(newRoute);
            routeNew.setText("");
        }
    }

    public void srcUpdate(View view) {
        String newSrc = srcNew.getText().toString().trim();

        if (newSrc.length() == 0) {
            Toast.makeText(this, getString(R.string.srcEmpty), Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < newSrc.length(); i++) {
            char ch = newSrc.charAt(i);
            if (!(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_' && ch != ' ') {
                Toast.makeText(this, getString(R.string.srcSplCh), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (newSrc.charAt(newSrc.length() - 1) == '_') {
            Toast.makeText(this, getString(R.string.srcEndSplCh), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkRoute('s')) {
            currSrc.setText(newSrc);
            srcNew.setText("");
        }
    }

    public void destUpdate(View view) {
        String newDest = destNew.getText().toString().trim();

        if (newDest.length() == 0) {
            Toast.makeText(this, getString(R.string.destEmpty), Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < newDest.length(); i++) {
            char ch = newDest.charAt(i);
            if (!(Character.isDigit(ch)) && !(Character.isLetter(ch)) && ch != '_' && ch != ' ') {
                Toast.makeText(this, getString(R.string.destSplCh), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (newDest.charAt(newDest.length() - 1) == '_') {
            Toast.makeText(this, getString(R.string.destEndSplCh), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkRoute('d')) {
            currDest.setText(newDest);
            destNew.setText("");
        }
    }

    public void stopUpdate(View view) {
        if (itemPos == 0) {
            Toast.makeText(this, getString(R.string.stopNotSel), Toast.LENGTH_SHORT).show();
        } else {
            if (stopNew.getText().toString().trim().length() != 0) {
                stops.set(itemPos, stopNew.getText().toString().trim());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stops);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stopList.setAdapter(adapter);
                stopNew.setText("");
            } else {
                Toast.makeText(this, getString(R.string.stopEmpty), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDB() {
        routeName = "Route_" + currRoute.getText().toString().trim().replaceAll(" ", "_") +
                "_From_" + currSrc.getText().toString().trim().replaceAll(" ", "_") +
                "_Towards_" + currDest.getText().toString().trim().replaceAll(" ", "_");
        //db.updateTableName(routeName);
        //db.updateStops(stops);
    }

    public void upload(View view) {
        updateDB();
        db.closeDB();
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra(getString(R.string.routeKey), routeName);
        i.putExtra(getString(R.string.cityKey), cityName);
        startActivity(i);
        finish();
    }

    public void save(View view) {
        updateDB();
        db.closeDB();
        Intent i = new Intent(this, ListFiles.class);
        i.putExtra(getString(R.string.routeKey), routeName);
        i.putExtra(getString(R.string.cityKey), cityName);
        if (parent.equals("saved"))
            i.putExtra(getString(R.string.parentKey), "editSave");
        else if (parent.equals("unsaved"))
            i.putExtra(getString(R.string.parentKey), "edit");
        else
            i.putExtra(getString(R.string.parentKey), "Dunno!");
        startActivity(i);
        finish();
    }

    public void cancel(View view) {
        db.closeDB();
        Intent i = new Intent(this, RouteManager.class);
        startActivity(i);
        finish();
    }
}