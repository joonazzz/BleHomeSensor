package android.jsillanpaa.com.blehomesensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView mLvSensors;
    private SensorsListAdapter mListAdapter;
    private Button mButtonScan;
    private boolean mScanning;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLeScanner;
    private Spinner mSpinnerSortCriteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initBluetooth();
        initComponents();
    }

    private void initBluetooth() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    private void initComponents() {
        mSpinnerSortCriteria = (Spinner) findViewById(R.id.s_sort_criteria);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_criteria_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSortCriteria.setAdapter(adapter);
        mSpinnerSortCriteria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    mListAdapter.setSortCriteria(SensorsListAdapter.SORT_RSSI_DESCENDING);
                }
                else if(position == 1){
                    mListAdapter.setSortCriteria(SensorsListAdapter.SORT_RSSI_ASCENDING);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mScanning = false;
        mButtonScan = (Button) findViewById(R.id.b_scan);
        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SCAN: onClick()");
                if(mScanning){
                    stopLeScan();
                }
                else{
                    startLeScan();
                }
            }
        });

        mListAdapter = new SensorsListAdapter(MainActivity.this.getLayoutInflater());
        mLvSensors = (ListView) findViewById(R.id.lv_sensors);
        mLvSensors.setAdapter(mListAdapter);
    }

    private void startLeScan() {
        Log.d(TAG, "startLeScan()");
        /* TODO: if scanning is ever moved to some kind of ble service, keep scanning state only there, not on UI */
        mScanning = true;
        mButtonScan.setText(R.string.stop);

        //builder = ScanSettings.set
        //ScanSettings settings = new ScanSettings(ScanSettings.SCAN_MODE_BALANCED, ScanSettings.CALLBACK_TYPE_ALL_MATCHES, ScanSettings.SCAN_RESULT_TYPE_FULL, 0);
        mLeScanner.startScan(mLeScanCallback);



    }

    private void stopLeScan() {
        Log.d(TAG, "stopLeScan()");
        mScanning = false;
        mButtonScan.setText(R.string.scan);
        mLeScanner.stopScan(mLeScanCallback);

    }




    private ScanCallback mLeScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult");
            Log.d(TAG, "    callbackType = " + callbackType);
            Log.d(TAG, "    result = " + result);
            Log.d(TAG, "    rssi = " + result.getRssi());
            Log.d(TAG, "    name = " + result.getDevice().getName());
            Log.d(TAG, "    mac = " + result.getDevice().getAddress());
            //mListAdapter.addResult(result.getDevice());
            mListAdapter.addResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
