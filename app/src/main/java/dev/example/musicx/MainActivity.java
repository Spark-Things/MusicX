package dev.example.musicx;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
     ListView listView;
     String items[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.Listview);
        runtimePermisssions();
    }

    void runtimePermisssions(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (true){
                                    DisplaySong();
                                }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                           permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong (File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        if(files != null)
        for (File singlefile : files){

            if (singlefile.isDirectory() && !singlefile.isHidden()){
                arrayList.addAll(findSong(singlefile));
            }else{
                if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")){
                   if(singlefile.length()/1024/1024 > 0) {
                       arrayList.add(singlefile);
                   }
                }
            }
        }
        return arrayList;
    }

    public void DisplaySong(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
//        Log.w("SONG", String.valueOf(mySongs));
        items = new String[mySongs.size()];
        for (int i=0;i<mySongs.size();i++){
            Log.w("SONG", String.valueOf(mySongs.get(i).getName()));
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
//            Log.w("SONG",items[i]);
        }

//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
//        listView.setAdapter(myAdapter);

         customAdpter customAdpter = new customAdpter();
        listView.setAdapter(customAdpter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), playerActivity.class)
                .putExtra("songs",mySongs)
                .putExtra("songname",songname)
                .putExtra("pos",position));
            }
        });
    }

     class customAdpter extends BaseAdapter{

         @Override
         public int getCount() {
             return items.length;
         }

         @Override
         public Object getItem(int position) {
             return null;
         }

         @Override
         public long getItemId(int position) {
             return 0;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             View myview = getLayoutInflater().inflate(R.layout.list_item,null);
             TextView textsong = myview.findViewById(R.id.textSongName);
             textsong.setSelected(true);
             textsong.setText(items[position]);

             return myview;
         }
     }
}