package com.ederdoski.simpleble;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.Adapters.BasicList;
import com.ederdoski.simpleble.models.BluetoothLE;
import com.ederdoski.simpleble.utils.BluetoothLEHelper;
import com.ederdoski.simpleble.utils.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BluetoothLEHelper ble;
    AlertDialog dAlert;

    ListView listBle;
    Button btnScan;
    Button btnWrite;
    Button btnRead;

    private AlertDialog setDialogInfo(String title, String message, boolean btnVisible){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_standard, null);

        TextView btnNeutral = view.findViewById(R.id.btnNeutral);
        TextView txtTitle   = view.findViewById(R.id.txtTitle);
        TextView txtMessage = view.findViewById(R.id.txtMessage);

        txtTitle.setText(title);
        txtMessage.setText(message);

        if(btnVisible){
            btnNeutral.setVisibility(View.VISIBLE);
        }else{
            btnNeutral.setVisibility(View.GONE);
        }

        btnNeutral.setOnClickListener(view1 -> {
            dAlert.dismiss();
        });

        builder.setView(view);
        return builder.create();
    }

    private void setList(){

        ArrayList<BluetoothLE> aBleAvailable  = new ArrayList<>();

        if(ble.getListDevices().size() > 0){
            for (int i=0; i<ble.getListDevices().size(); i++) {
                aBleAvailable.add(new BluetoothLE(ble.getListDevices().get(i).getName(), ble.getListDevices().get(i).getMacAddress(), ble.getListDevices().get(i).getRssi(), ble.getListDevices().get(i).getDevice()));
            }

            BasicList mAdapter = new BasicList(this, R.layout.simple_row_list, aBleAvailable) {
                @Override
                public void onItem(Object item, View view, int position) {

                    TextView txtName = view.findViewById(R.id.txtText);

                    String aux = ((BluetoothLE) item).getName() + "    " + ((BluetoothLE) item).getMacAddress();
                    txtName.setText(aux);

                }
            };

            listBle.setAdapter(mAdapter);
            listBle.setOnItemClickListener((parent, view, posicion, id) -> {
                BluetoothLE  itemValue = (BluetoothLE) listBle.getItemAtPosition(posicion);
                ble.connect(itemValue.getDevice());
            });
        }else{
            dAlert = setDialogInfo("Ups", "We do not find active devices", true);
            dAlert.show();
        }
    }

    private void scanCollars(){

        if(!ble.isScanning()) {

            dAlert = setDialogInfo("Scan in progress", "Loading...", false);
            dAlert.show();

            Handler mHandler = new Handler();
            ble.scanLeDevice(true);

            mHandler.postDelayed(() -> {
                dAlert.dismiss();
                setList();
            },ble.getScanPeriod());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void listenerButtons(){

        btnScan.setOnClickListener(v -> {
            if(ble.isReadyForScan()){
                scanCollars();
            }else{
                Toast.makeText(MainActivity.this, "You must accept the bluetooth and Gps permissions", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRead.setOnClickListener(v -> {
            if(ble.isConnected()) {
                ble.read(Constants.SERVICE_COLLAR_INFO, Constants.CHARACTERISTIC_CURRENT_POSITION);
            }
        });

        btnWrite.setOnClickListener(v -> {
            if(ble.isConnected()) {
                byte[] aBytes = new byte[8];
                ble.write(Constants.SERVICE_COLLAR_INFO, Constants.CHARACTERISTIC_GEOFENCE, aBytes);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.disconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--- Initialize BLE Helper
        ble = new BluetoothLEHelper(this);

        listBle  = findViewById(R.id.listBle);
        btnScan  = findViewById(R.id.scanBle);
        btnRead  = findViewById(R.id.readBle);
        btnWrite = findViewById(R.id.writeBle);

        listenerButtons();

        //--- Delete this line to do a search of all the devices
        //ble.setFilterService(Constants.SERVICE_COLLAR_INFO);
    }
}
