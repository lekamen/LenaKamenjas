package com.example.lekamen.kolokvij2;

import android.Manifest;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private int MY_PERM = 1;
    private DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},MY_PERM);
        }

        startDatabase();
    }

    //ja sam shvatila kao da su te dvije baze povezane, da za svaki kolac ide posebni glavni
    //sastojak, pa zato u tablici cijene imam vise puta da se pojavljuju bademi npr.
    private void startDatabase() {
        db = new DBAdapter(this);

        db.open();
        long id = db.insertCake("torta s bademom", "torta", "bademi");
        db.insertPrice(150.99, id);

        id = db.insertCake("torta bez badema", "torta", "brasno");
        db.insertPrice(7, id);

        id = db.insertCake("kolac s bademom", "kolac", "bademi");
        db.insertPrice(150.99, id);

        id = db.insertCake("kolac bez badema", "kolac", "orasi");
        db.insertPrice(60.25, id);

        id = db.insertCake("brownie", "kolac", "cokolada");
        db.insertPrice(10, id);
    }
    private void listContacts(String selection) {

        Uri allContacts = ContactsContract.Contacts.CONTENT_URI;


        Cursor c;
        if (android.os.Build.VERSION.SDK_INT <11) {
            //---before Honeycomb---
            c = managedQuery(allContacts,
                    new String[] {ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts._ID},
                    selection,null,null);

        } else {
            //---Honeycomb and later---
            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    allContacts,
                    new String[] {ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts._ID},
                    selection,
                    null,
                    null);
            c = cursorLoader.loadInBackground();
        }

        TextView tV = (TextView)findViewById(R.id.output);
        tV.setText("");

        if(c.moveToFirst()) {
            do {
                tV.setText(tV.getText() + c.getString(0) + "\n" + c.getString(1) +
                        "\n -------- \n");
            } while(c.moveToNext());
        }

    }

    public void onClickMethod(View view) {
        EditText e = (EditText) findViewById(R.id.input);
        String input = e.getText().toString();
        if(input.length() != 1) {
            Toast.makeText(this, "Unesite točno jedno slovo!", Toast.LENGTH_SHORT).show();
            return;
        }
        String selection = null;
        switch(view.getId()) {
            case R.id.startWith:
                selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE '" +
                        input + "%'";
                break;
            case R.id.dontStartWith:
                selection = ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '" +
                        input + "%'";
                break;
        }
        listContacts(selection);
    }

    //pritiskom na gumb prebacujemo se na view da radimo s bazom podataka
    public void workWithDatabase(View view) {
        findViewById(R.id.firstpart).setVisibility(View.GONE);
        findViewById(R.id.secondpart).setVisibility(View.VISIBLE);
        setAllData();
    }

    //pritiskom na gumb prebacujemo se na view da radimo s kontaktima
    public void workWithContacts(View view) {
        findViewById(R.id.secondpart).setVisibility(View.GONE);
        findViewById(R.id.firstpart).setVisibility(View.VISIBLE);
    }

    //funkcija koja u textview ispisuje obe tablice sa svim podacima
    private void setAllData() {
        TextView tV = (TextView)findViewById(R.id.database);
        tV.setText("Svi kolači: \n");
        db.open();
        Cursor[] c = db.getAllCakesAndPrices();
        if(c[0].moveToFirst()) {
            do {
                tV.append(c[0].getString(0) + "\n" + c[0].getString(1) +
                        "\n -------- \n");
            } while(c[0].moveToNext());
        }
        tV.append("Sve cijene sastojaka: \n");
        if(c[1].moveToFirst()) {
            do {
                tV.append( c[1].getString(0) + "\n" + c[1].getString(1) + "\n" + c[1].getString(2) +
                        "\n -------- \n");
            } while(c[1].moveToNext());
        }
        db.close();
    }

    //pritiskom na gumb pokusava se izbrisati podatak iz obe tablice
    public void deleteCakeAndPrice(View view) {
        db.open();

        String text = ((EditText)findViewById(R.id.deleteID)).getText().toString();
        int id = Integer.parseInt(text);

            if(db.deleteCakeAndPrice(id)) {
                Toast.makeText(this, "Uspješno izbrisano", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brisanje neuspješno", Toast.LENGTH_SHORT).show();
            }

        db.close();
        setAllData();
    }

    //metoda trazi sve one koji sadrze sastojak zadan u edittextu i ispisuje
    //u textview-u ispod
    public void allWithIngredient(View view) {
        String sastojak = ((EditText)findViewById(R.id.editInput)).getText().toString();

        TextView tV = (TextView)findViewById(R.id.editOutput);
        tV.setText("");
        db.open();
        Cursor c = db.getCakeWithIngredient(sastojak);

        if(c.moveToFirst()) {
            do {
                tV.append( c.getString(0) + "\n" + c.getString(1) +
                        "\n -------- \n");
            } while(c.moveToNext());
        }
        db.close();
    }


}
