package android.jsillanpaa.com.blehomesensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by joonas on 15.3.2015.
 */
public class DeviceListAdapter extends BaseAdapter {

    private static final String TAG = "SensorsListAdapter";
    public static final int SORT_RSSI_DESCENDING = 1;
    public static final int SORT_RSSI_ASCENDING = 2;


    private int mSortCriteria;
    private ArrayList<ScanResult> mScanResults;
    private LayoutInflater mInflater;
    private Comparator<? super ScanResult> mSortFunc;



    public DeviceListAdapter(LayoutInflater inflater) {
        this(inflater, SORT_RSSI_DESCENDING);
    }

    public DeviceListAdapter(LayoutInflater inflater, int sortCriteria) {
        super();

        mScanResults = new ArrayList<ScanResult>();
        mInflater = inflater;
        setSortCriteria(sortCriteria);
    }

    public void setSortCriteria(int sortCriteria) {
        if(sortCriteria == SORT_RSSI_DESCENDING){
            mSortFunc = sortRssiDescending;
        }
        else if(sortCriteria == SORT_RSSI_ASCENDING){
            mSortFunc = sortRssiAscending;
        }
        Collections.sort(mScanResults, mSortFunc);
        notifyDataSetChanged();
    }


    public void addResult(ScanResult result) {
            Log.d(TAG, "addResult()");
            for(ScanResult r : mScanResults){
                // If there was old measurement for given mac remove it
                if(r.getDevice().getAddress().equals(result.getDevice().getAddress())){
                    Log.d(TAG, "    old result found: removing it");
                    mScanResults.remove(r);
                }
            }

            mScanResults.add(result);
            Collections.sort(mScanResults, mSortFunc);
            notifyDataSetChanged();

        }

        public ScanResult getResult(int position) {
            return mScanResults.get(position);
        }

        public void clear() {
            mScanResults.clear();
        }

        @Override
        public int getCount() {
            return mScanResults.size();
        }

        @Override
        public Object getItem(int i) {
            return mScanResults.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ScanResult result = mScanResults.get(i);
            BluetoothDevice device = result.getDevice();
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.deviceRssi.setText(Integer.toString(result.getRssi()));

            return view;
        }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }


    private Comparator<? super ScanResult> sortRssiDescending = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            return rhs.getRssi() - lhs.getRssi();
        }
    };
    private Comparator<? super ScanResult> sortRssiAscending = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            return lhs.getRssi() - rhs.getRssi();

        }
    };

}
