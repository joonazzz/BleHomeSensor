package android.jsillanpaa.com.blehomesensor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;


public class SelectSensorDialogFragment extends DialogFragment {

    private static final String TAG = "SelectSensorDialog";

    private ListView mSensorsListView;
    private SensorsListAdapter mSensorsListAdapter;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList mSelectedItems;

    public interface SelectSensorDialogFragmentListener {
		public void onSensorSelected(int mSelectedItem);
	};

	public static SelectSensorDialogFragment newInstance(SelectSensorDialogFragmentListener l, List<BluetoothGattCharacteristic> sensors, int selectedItem) {
		SelectSensorDialogFragment frag = new SelectSensorDialogFragment();
		frag.setListener(l);
		
		Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>(sensors.size());
        for(BluetoothGattCharacteristic c : sensors){
            names.add(SampleGattAttributes.lookup(c));
        }

        args.putStringArrayList("names", names);
        args.putInt("selectedItem", selectedItem);

        frag.setArguments(args);

		return frag;
	}

	private SelectSensorDialogFragmentListener mListener;

	private void setListener(SelectSensorDialogFragmentListener l) {
		mListener = l;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");

        List<String> n = getArguments().getStringArrayList("names");
        int selectedItem = getArguments().getInt("selectedItem");
        CharSequence[] names = n.toArray(new CharSequence[n.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder = builder.setTitle("Sensors");
        builder.setSingleChoiceItems(names, selectedItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onSensorSelected(which);
                dismiss();
            }
        });
        return builder.create();

	}



}
