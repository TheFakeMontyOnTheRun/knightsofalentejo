package br.odb;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import br.odb.knights.Knight;

/**
 * Created by monty on 13/08/16.
 */
public class KnightSelectionAdapter extends ArrayAdapter<Knight> {

	View[] knightIcons;

	public KnightSelectionAdapter(Context context, int resource, Knight[] objects, View[] knightImages) {
		super(context, resource, objects);

		this.knightIcons = knightImages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return knightIcons[ position ];
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return knightIcons[ position ];
	}
}
