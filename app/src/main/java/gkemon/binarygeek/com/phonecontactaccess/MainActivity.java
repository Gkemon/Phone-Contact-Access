package gkemon.binarygeek.com.phonecontactaccess;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_CONTACTS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

    }
    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d("GK", "Name: " + name);
                        Log.d("GK", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

    //persmission method.
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission

        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        //প্রতিবার দেখবো পারমিশন নেয়া নাকি নেয়া না।যদি নেয়া হয় তাহলে কন্টার রিড করব আর যদি না নেয়া হয় তাহলে কন্টাক রিড করব না বরং রানটাইম পারমিশন যাইবো।
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CONTACTS
            );
        }
        else {
            getContactList();
        }

    }


    //প্রথমবার আমি পারমিশন এলাও করলাম নাকি ডিনাই করতাম তা এইটা দিয়ে দেখা যাবে।
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //আমি পারমিশন এলাও করলে কন্টাক রিড করবে।
                    getContactList();
                } else {

                    // আমি পারমিশন ডীনাই করলে এখানে আসবে।
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
