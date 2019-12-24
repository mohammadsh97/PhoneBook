package com.mohammadSharabati_ahmadSharabati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;

/**I added ListAdapter.java and single_list_item.xml to view the listView with text and image with
 the wanted format **/

public class MainActivity extends AppCompatActivity {

    private Button insertBtn , searchBtn;
    private EditText nameTxt , phoneTxt;
    private Intent intent;
    private ListView list;
    private SQLiteDatabase contactsDB = null;
    private Vector<String> names , phones;
    private ListAdapter lAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertBtn=findViewById(R.id.insert);
        searchBtn=findViewById(R.id.search);
        nameTxt=findViewById(R.id.nametxt);
        phoneTxt=findViewById(R.id.phonetxt);
        list=findViewById(R.id.listView);



        createDB();
        showContacts();

        nameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameTxt.getText().clear();
            }
        });

        phoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneTxt.setText("");
            }
        });


        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
                showContacts();

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {//in case of listView pressed
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!phones.elementAt(i).equalsIgnoreCase("")){
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phones.elementAt(i)));
                    startActivity(intent);
                }
            }
        });

    }

    public void createDB()
    {
        try
        {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            contactsDB = openOrCreateDatabase("contacts", MODE_PRIVATE, null);
            // Execute an SQL statement that isn't select
            String sql = "CREATE TABLE IF NOT EXISTS contacts( name VARCHAR primary key , phone VARCHAR );";
            contactsDB.execSQL(sql);
        }

        catch(Exception e){
            Log.d("debug", "Error Creating Database");
        }
    }


    public void addContact() {//to add contact to db

        // Get the contact name and phone entered
        String contactName = nameTxt.getText().toString();
        String contactPhone = phoneTxt.getText().toString();

        if (contactPhone.equalsIgnoreCase("phone"))//in case pressed contact name without phone num
            contactPhone="";
        if (contactName.equalsIgnoreCase("name"))
            contactName="";

        if (!contactName.equalsIgnoreCase("")){//if no name was entered

            int ifExist=0;//to check if the name exists already

            for (int i=0;i<names.size();i++){
                if (names.elementAt(i).equalsIgnoreCase(contactName)){
                    Toast.makeText(this, "Contact All ready exists", Toast.LENGTH_SHORT).show();
                    if (!contactPhone.equalsIgnoreCase("")) {
                        ContentValues cv = new ContentValues();
                        cv.put("name", contactName);
                        cv.put("phone", contactPhone);
                        contactsDB.update("contacts", cv, "name" + "='" + contactName
                                + "'", null);
                    }
                    ifExist = 1;
                    break;
                }
            }
            if (ifExist==0){//in case of new contact
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                // Execute SQL statement to insert new data
                String sql = "INSERT INTO contacts(name, phone) VALUES ('" + contactName + "', '" + contactPhone+ "');";
                contactsDB.execSQL(sql);
            }
        }
        else
            Toast.makeText(this, "Must enter contact name", Toast.LENGTH_SHORT).show();
    }


    public void showContacts()
    {
        // A Cursor provides read and write access to database results
        String sql = "SELECT * FROM contacts";
        Cursor cursor = contactsDB.rawQuery(sql, null);

        names=new Vector<>();// to save the names in
        phones=new Vector<>();//to save the phones nums in

        String namesArr[] , phonesArr[];// used in the adapter
        int images[];


        // Get the index for the column name provided
        int nameColumn = cursor.getColumnIndex("name");
        int phoneColumn = cursor.getColumnIndex("phone");


        // Move to the first row of results
        cursor.moveToFirst();

        // Verify that we have results
        if(cursor != null && (cursor.getCount() > 0)){

            do{
                // Get the results and store them in a String
                String name= cursor.getString(nameColumn);
                String phone= cursor.getString(phoneColumn);

                /** to add to the vectors **/
                names.add(name);
                phones.add(phone);

                // Keep getting results as long as they exist
            }while(cursor.moveToNext());


            namesArr=new String[names.size()];//to save the tasks & dates together
            phonesArr=new String[names.size()];
            images=new int[names.size()];
            for (int j=0;j<namesArr.length;j++){//to fill the arrays used in the adapter with info
                namesArr[j]=names.elementAt(j);
                phonesArr[j]=phones.elementAt(j);

                if (phonesArr[j].equalsIgnoreCase(""))
                    images[j]=R.drawable.gray;
                else
                    images[j]=R.drawable.green;


            }


            lAdapter = new ListAdapter(MainActivity.this, namesArr, phonesArr, images);

            list.setAdapter(lAdapter);


        } else {

            Toast.makeText(this, "No Results to Show", Toast.LENGTH_SHORT).show();
        }


    }


    public void search(){
        String contactName = nameTxt.getText().toString();
        String contactPhone = phoneTxt.getText().toString();

        if (contactPhone.equalsIgnoreCase("phone"))
            contactPhone="";
        if (contactName.equalsIgnoreCase("name"))
            contactName="";

        String namesArr[] , phonesArr[];
        int images[];

        boolean doesExist=false;//to check if sub is found or not

        if (!contactName.equalsIgnoreCase("") && !contactPhone.equalsIgnoreCase("")){
            for (int i=0;i<names.size();i++){
                if (names.elementAt(i).equalsIgnoreCase(contactName) && phones.elementAt(i).equalsIgnoreCase(contactPhone)){
                    namesArr=new String[1];
                    phonesArr=new String[1];
                    images=new int[1];
                    namesArr[0]=names.elementAt(i);
                    phonesArr[0]=phones.elementAt(i);

                    if (phonesArr[0].equalsIgnoreCase(""))
                        images[0]=R.drawable.gray;
                    else
                        images[0]=R.drawable.green;
                    lAdapter = new ListAdapter(MainActivity.this, namesArr, phonesArr, images);
                    list.setAdapter(lAdapter);
                    doesExist=true;
                    break;
                }

            }

        }


        else if (!contactName.equalsIgnoreCase("") || !contactPhone.equalsIgnoreCase("")){
            int subNameCount=numOfElements(names,contactName);//to know the num of subs to initialize the arrays
            int subPhoneCount=numOfElements(phones,contactPhone);//to know the num of subs to initialize the arrays

            if (!contactName.equalsIgnoreCase("") && subNameCount!=0){
                namesArr=new String[subNameCount];
                phonesArr=new String[subNameCount];
                images=new int[subNameCount];
                for (int i=0,j=0;i<names.size();i++){
                    if (names.elementAt(i).toLowerCase().contains(contactName.toLowerCase())){
                        namesArr[j]=names.elementAt(i);
                        phonesArr[j]=phones.elementAt(i);
                        if (phonesArr[j].equalsIgnoreCase(""))
                            images[j]=R.drawable.gray;
                        else
                            images[j]=R.drawable.green;
                        j++;
                        doesExist=true;
                    }
                }
                lAdapter = new ListAdapter(MainActivity.this, namesArr, phonesArr, images);
            }

            else if(!contactPhone.equalsIgnoreCase("")&&subPhoneCount!=0){
                namesArr=new String[subPhoneCount];
                phonesArr=new String[subPhoneCount];
                images=new int[subPhoneCount];
                for (int i=0,j=0;i<names.size();i++){
                    if (phones.elementAt(i).toLowerCase().contains(contactPhone.toLowerCase())){
                        namesArr[j]=names.elementAt(i);
                        phonesArr[j]=phones.elementAt(i);
                        if (phonesArr[j].equalsIgnoreCase(""))
                            images[j]=R.drawable.gray;
                        else
                            images[j]=R.drawable.green;
                        j++;
                        doesExist=true;
                    }
                }
                lAdapter = new ListAdapter(MainActivity.this, namesArr, phonesArr, images);
            }

            list.setAdapter(lAdapter);

        }

        if (doesExist==false){// if the sub does not exist
            list.setAdapter(null);
            Toast.makeText(this, "No Results Found", Toast.LENGTH_SHORT).show();
        }


    }

    private int numOfElements(Vector<String> v , String sub){// to return the number of subs in vector
        int count=0;
        if (!sub.equalsIgnoreCase("")){
            for (int i=0;i<v.size();i++){
                if (v.elementAt(i).toLowerCase().contains(sub.toLowerCase()))
                    count++;
            }
        }
        return count;
    }

    protected void onDestroy()
    {
        contactsDB.close();
        super.onDestroy();
    }
}