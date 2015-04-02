package android.jsillanpaa.com.blehomesensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by joonas on 15.3.2015.
 */
public class SensorsListAdapter extends BaseAdapter {

    private static final String TAG = "CharacteristicsListAdapter";

    private ArrayList<BluetoothGattCharacteristic> mCharacteristics;
    private LayoutInflater mInflater;


    public SensorsListAdapter(LayoutInflater inflater) {
        super();

        mCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
        mInflater = inflater;
    }


    public void reloadFrom(List<BluetoothGattCharacteristic> monitorableSensors) {
        mCharacteristics.clear();
        mCharacteristics.addAll(monitorableSensors);
        notifyDataSetChanged();
    }

    public BluetoothGattCharacteristic getCharacteristic(int position) {
        return mCharacteristics.get(position);
    }

    public void clear() {
        mCharacteristics.clear();
    }

    @Override
    public int getCount() {
        return mCharacteristics.size();
    }

    @Override
    public Object getItem(int i) {
        return mCharacteristics.get(i);
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
            view = mInflater.inflate(R.layout.listitem_characteristic, null);
            viewHolder = new ViewHolder();
            viewHolder.characteristic_uuid = (TextView) view.findViewById(R.id.characteristic_uuid);
            viewHolder.characteristic_properties = (TextView) view.findViewById(R.id.characteristic_properties);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothGattCharacteristic c = mCharacteristics.get(i);
        viewHolder.characteristic_uuid.setText(c.getUuid().toString());
        viewHolder.characteristic_properties.setText(Integer.toString(c.getProperties()));

        return view;
    }

    static class ViewHolder {
        TextView characteristic_uuid;
        TextView characteristic_properties;
    }


}
