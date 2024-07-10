package br.odb;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import br.odb.knights.R;

/**
 * Created by monty on 13/08/16.
 */
class CameraSelectionAdapter extends ArrayAdapter<Bitmap> {


    private final String[] cameraModeNames;
    private final Bitmap[] cameraModes;

    public CameraSelectionAdapter(Context context, Bitmap[] cameraModes, String[] cameraModeNames) {
        super(context, R.layout.camera_item, cameraModes);

        this.cameraModes = cameraModes;
        this.cameraModeNames = cameraModeNames;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getViewForItem(parent, position);
    }

    private View getViewForItem(ViewGroup parent, int position) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.camera_item, parent, false);
        ((TextView) v.findViewById(R.id.tvCameraModeName)).setText(cameraModeNames[position]);
        ((ImageView) v.findViewById(R.id.ivCameraModeIcon)).setImageBitmap(cameraModes[position]);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getViewForItem(parent, position);
    }
}
