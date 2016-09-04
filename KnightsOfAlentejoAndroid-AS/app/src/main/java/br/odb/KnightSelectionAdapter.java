package br.odb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import br.odb.knights.Knight;
import br.odb.knights.R;

/**
 * Created by monty on 13/08/16.
 */
public class KnightSelectionAdapter extends ArrayAdapter<Knight> {

	private final Typeface font;
	private Map<String, String> localizedKnightsNames;
	private Map<String, Bitmap> bitmapForKnights;

	public KnightSelectionAdapter(Context context, int res, Knight[] knights, Map<String, String> localizedKnightsNames, Map<String, Bitmap> bitmapForKnights, Typeface font) {
		super(context, res, knights);
		this.font = font;
		this.localizedKnightsNames = localizedKnightsNames;
		this.bitmapForKnights = bitmapForKnights;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewForKnight(parent, getItem(position ));
	}

	@NonNull
	private View getViewForKnight(ViewGroup parent, Knight k) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.knightitem, parent, false);
		((TextView)v.findViewById( R.id.tvKnightName )).setText( localizedKnightsNames.get(k.getChar()) );
		((TextView)v.findViewById( R.id.tvHealth )).setText( k.toString() );

		((TextView)v.findViewById( R.id.tvKnightName )).setTypeface( font );
		((TextView)v.findViewById( R.id.tvHealth )).setTypeface( font );

		((ImageView)v.findViewById( R.id.ivKnightIcon )).setImageBitmap(bitmapForKnights.get( k.getChar()));
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getViewForKnight(parent, getItem(position ));
	}
}
