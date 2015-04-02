package android.jsillanpaa.com.blehomesensor;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joonas on 16.3.2015.
 */
public class GraphActivity extends Activity {


    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final String TAG = "GraphActivity";

    private static final int UI_STATE_CONNECTED = 1;
    private static final int UI_STATE_CONNECTING = 2;
    private static final int UI_STATE_DISCONNECTED = 3;


    private BluetoothService mBluetoothService;
    private boolean mIsBound;
    private Button mDeviceButton;
    private BluetoothDevice mSelectedDevice;
    private List<BluetoothGattCharacteristic> mSensors = new ArrayList<>();

    private ConnectionStatusView mConnectionStatus;
    private TextView mDeviceName;
    private TextView mDeviceMac;
    private Button mSensorButton;
    private TextView mSensorName;
    private TextView mSensorUUID;
    private BluetoothGattCharacteristic mOldSensor;
    private BluetoothGattCharacteristic mSelectedSensor;
    private int mSelectedSensorIndex;
    private TextView mLogTv;
    private ScrollView mLogScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);

        doBindService();

        initComponents();

    }

    private void initComponents() {

        mDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mDeviceMac = (TextView) findViewById(R.id.tv_device_mac);

        mDeviceButton = (Button) findViewById(R.id.b_device);
        mDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GraphActivity.this, ScanActivity.class);
                startActivityForResult(i, REQUEST_SELECT_DEVICE);
            }
        });


        mSensorName = (TextView) findViewById(R.id.tv_sensor_name);
        mSensorUUID = (TextView) findViewById(R.id.tv_sensor_uuid);
        mSensorButton = (Button) findViewById(R.id.b_sensor);
        mSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                SelectSensorDialogFragment.newInstance(mySelectSensorsDialogListener, mSensors, mSelectedSensorIndex).show(getFragmentManager(), "selectSensorDialog");
            }
        });

        mConnectionStatus = (ConnectionStatusView) findViewById(R.id.connection_status);

        mLogScrollView = (ScrollView) findViewById(R.id.sv_log);
        mLogTv = (TextView) findViewById(R.id.tv_log);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        Log.d(TAG, "    requestCode = " + requestCode);
        Log.d(TAG, "    resultCode = " + resultCode);
        Log.d(TAG, "    data = " + data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SELECT_DEVICE:
                if(resultCode == RESULT_OK ){
                    Log.d(TAG, "    resultCode = OK");
                    if(data != null){
                        BluetoothDevice device = data.getParcelableExtra(ScanActivity.EXTRA_SELECTED_DEVICE);
                        setSelectedDevice(device);
                    }
                }
                break;
        }
    }

    private void setSelectedDevice(BluetoothDevice device) {
        Log.d(TAG, "setSelectedDevice");
        Log.d(TAG, "    deviceName = " + device.getName());
        Log.d(TAG, "    deviceAddress = " + device.getAddress());
        mSelectedDevice = device;
        mDeviceName.setText(device.getName());
        mDeviceMac.setText(device.getAddress());
        connectDevice(mSelectedDevice);

    }

    private void connectDevice(BluetoothDevice device) {
        Log.d(TAG, "connectDevice");
        mBluetoothService.connectDevice(device);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(GraphActivity.this, BluetoothService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBluetoothService = ((BluetoothService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(GraphActivity.this, R.string.bt_service_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBluetoothService = null;
            Toast.makeText(GraphActivity.this, R.string.bt_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "********************************************************************");
            Log.d(TAG, "*** GATT UPRATE: " + action);
            Log.d(TAG, "********************************************************************");
            if (BluetoothService.ACTION_GATT_CONNECTING.equals(action)) {
                setUiState(UI_STATE_CONNECTING);
            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                setUiState(UI_STATE_DISCONNECTED);
            } else if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                /* Let's wait until services are discovered also */
            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                setUiState(UI_STATE_CONNECTED);

                Log.d(TAG, "Available services: ");
                List<BluetoothGattService> services = mBluetoothService.getSupportedGattServices();
                mSensors.clear();
                for (BluetoothGattService s : services) {
                    Log.d(TAG, "    UUID = " + s.getUuid());
                    Log.d(TAG, "    type = " + s.getType());
                    List<BluetoothGattCharacteristic> characteristics = s.getCharacteristics();
                    Log.d(TAG, "    Characteristics:");
                    for (BluetoothGattCharacteristic c : characteristics) {
                        Log.d(TAG, "        UUID = " + c.getUuid());
                        Log.d(TAG, "        properties = " + c.getProperties());
                        Log.d(TAG, "        permissions = " + c.getPermissions());
                        if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mSensors.add(c);
                        } else if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                            mSensors.add(c);
                        }
                    }

                }

            }
            else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothService.EXTRA_DATA);
                Log.d(TAG, "");
                Log.d(TAG, "data = " + data);
                Log.d(TAG, "");
                SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy  hh:mm:ss");
                mLogTv.append("" + s.format(new Date()) + ": " + data.trim() + "\n");
                scrollToBottom();
            }

        }
    };

    private void scrollToBottom()
    {
        mLogScrollView.post(new Runnable()
        {
            public void run()
            {
                mLogScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void setUiState(int state) {
        switch (state){
            case UI_STATE_CONNECTED:
                mConnectionStatus.setState(ConnectionStatusView.STATE_CONNECTED);
                break;
            case UI_STATE_CONNECTING:
                mConnectionStatus.setState(ConnectionStatusView.STATE_CONNECTING);
                break;
            case UI_STATE_DISCONNECTED:
                mConnectionStatus.setState(ConnectionStatusView.STATE_DISCONNECTED);
                break;

        }
    }

    private SelectSensorDialogFragment.SelectSensorDialogFragmentListener mySelectSensorsDialogListener = new SelectSensorDialogFragment.SelectSensorDialogFragmentListener() {
        @Override
        public void onSensorSelected(int mSelectedItem) {
            selectSensor(mSelectedItem);

        }
    };

    private void selectSensor(int sensor) {
        BluetoothGattCharacteristic s = mSensors.get(sensor);
        mOldSensor = mSelectedSensor;
        mSelectedSensor = s;
        mSelectedSensorIndex = sensor;
        mSensorName.setText(SampleGattAttributes.lookup(s.getUuid().toString(), s.getUuid().toString()));
        mSensorUUID.setText(s.getUuid().toString());

        mBluetoothService.monitorSensor(mSelectedSensor, mOldSensor);

    }
}
