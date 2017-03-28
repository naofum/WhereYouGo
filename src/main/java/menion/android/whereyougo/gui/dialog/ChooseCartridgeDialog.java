package menion.android.whereyougo.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import cz.matejcik.openwig.formats.CartridgeFile;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.dialog.CustomDialogFragment;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;

public class ChooseCartridgeDialog extends CustomDialogFragment {

    public static final String TAG = "ChooseCartridgeDialog";
    public interface ChooseCartridgeDialogCallback {
        void onChooseCartridgeResult(CartridgeFile selectedCartridge);
    }
    private ArrayList<DataInfo> data;
    private BaseAdapter adapter;
    private Vector<CartridgeFile> cartridgeFiles;
    private ChooseCartridgeDialogCallback chooseCartridgeDialogCallback;

    public ChooseCartridgeDialog() {
        super();
    }

    @Override
    public Dialog createDialog(Bundle savedInstanceState) {
        if (cartridgeFiles == null) {
            return null;
        }
        try {
            // sort cartridges
            final Location actLoc = LocationState.getLocation();
            final Location loc1 = new Location(TAG);
            final Location loc2 = new Location(TAG);
            Collections.sort(cartridgeFiles, new Comparator<CartridgeFile>() {

                @Override
                public int compare(CartridgeFile object1, CartridgeFile object2) {
                    loc1.setLatitude(object1.latitude);
                    loc1.setLongitude(object1.longitude);
                    loc2.setLatitude(object2.latitude);
                    loc2.setLongitude(object2.longitude);
                    return (int) (actLoc.distanceTo(loc1) - actLoc.distanceTo(loc2));
                }
            });

            // prepare list
            data = new ArrayList<>();
            for (int i = 0; i < cartridgeFiles.size(); i++) {
                CartridgeFile file = cartridgeFiles.get(i);
                byte[] iconData = file.getFile(file.iconId);
                Bitmap icon;
                try {
                    icon = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                } catch (Exception e) {
                    icon = Images.getImageB(R.drawable.icon_gc_wherigo);
                }

                DataInfo di =
                        new DataInfo(file.name, file.type + ", " + file.author + ", " + file.version, icon);
                di.value01 = file.latitude;
                di.value02 = file.longitude;
                di.setDistAzi(actLoc);
                data.add(di);
            }

            // create listView
            ListView listView = UtilsGUI.createListView(getActivity(), false, data);
            // set click listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemClicked(position);
                }
            });
            // set on long click listener for file deletion
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    itemLongClicked(position);
                    return true;
                }
            });
            adapter = (BaseAdapter) listView.getAdapter();
            // construct dialog
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.choose_cartridge)
                    .setIcon(R.drawable.ic_title_logo)
                    .setView(listView)
                    .setNeutralButton(R.string.close, null)
                    .create();
        } catch (Exception e) {
            Logger.e(TAG, "createDialog()", e);
        }
        return null;
    }

    public void setArguments(Vector<CartridgeFile> cartridgeFiles, ChooseCartridgeDialogCallback chooseCartridgeDialogCallback) {
        this.cartridgeFiles = cartridgeFiles;
        this.chooseCartridgeDialogCallback = chooseCartridgeDialogCallback;
    }

    private void itemClicked(int position) {
        dismiss();
        if (chooseCartridgeDialogCallback != null)
            chooseCartridgeDialogCallback.onChooseCartridgeResult(cartridgeFiles.get(position));
    }

    private void itemLongClicked(final int position) {
        try {
            final CartridgeFile cartridgeFile = cartridgeFiles.get(position);
            final String filename = cartridgeFile.filename.substring(0,
                    cartridgeFile.filename.length() - 3);

            UtilsGUI.showDialogQuestion(getActivity(), R.string.delete_cartridge,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int btn) {
                            for (File file : FileSystem.getFiles(MainApplication.getInstance().getCartridgesDir(), filename, null)) {
                                file.delete();
                            }
                            for (File file : FileSystem.getFiles(MainApplication.getInstance().getFilesDir(), filename, null)) {
                                file.delete();
                            }
                            for (File file : FileSystem.getFiles(MainApplication.getInstance().getCacheDir(), filename, null)) {
                                file.delete();
                            }
                            cartridgeFiles.remove(position);
                            data.remove(position);
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                        }
                    }, null);
        } catch (Exception e) {
            Logger.e(TAG, "onCreate()", e);
        }
    }
}
