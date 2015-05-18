package br.odb.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import br.odb.droidlib.Updatable;
import br.odb.knights.Actor;
import br.odb.knights.GameView;
import br.odb.knights.Knight;
import br.odb.knights.R;

public class GameActivity extends Activity implements Updatable, OnItemSelectedListener, OnClickListener {
	
	private GameView view;
	Spinner spinner;
	int level;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.game_layout );
    	
    	spinner = ( Spinner ) findViewById( R.id.spinner1 );
    	
    	findViewById( R.id.btnUp ).setOnClickListener( this );
    	findViewById( R.id.btnDown ).setOnClickListener( this );
    	findViewById( R.id.btnLeft ).setOnClickListener( this );
    	findViewById( R.id.btnRight ).setOnClickListener( this );

    	findViewById( R.id.btnUp ).setSoundEffectsEnabled(false);
    	findViewById( R.id.btnDown ).setSoundEffectsEnabled(false);
    	findViewById( R.id.btnLeft ).setSoundEffectsEnabled(false);
    	findViewById( R.id.btnRight ).setSoundEffectsEnabled(false);    	
    	
    	
    	
    	spinner.setOnItemSelectedListener( this );
        view = (GameView) findViewById( R.id.gameView1 );
        level = getIntent().getIntExtra(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, 0);
        
        if ( level > 0 ) {
        	Toast.makeText( this, "You advanced! Any killed knight was resurrected.", Toast.LENGTH_SHORT ).show();
        } else {
        	Toast.makeText( this, "Purge this level of demons or run for the exit.", Toast.LENGTH_SHORT ).show();
        }
        
        view.init( this, this, level );
    }
    
    @Override
    protected void onDestroy() {
    	view.running = false;
    	super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    
    	view.playing = hasFocus;
    }

	@Override
	public void update() {
		
		Knight[] knights = view.currentLevel.getKnights();
		
		if ( view.currentLevel.getMonsters() == 0 || ( knights.length == 0 && view.exitedKnights > 0 ) ) {
			Intent intent = new Intent();
			intent.putExtra( KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESFUL_LEVEL_COMPLETION, 1 );
			setResult( RESULT_OK, intent );
			finish();
		}  
		
		if ( knights.length == 0 ) {
			Intent intent = new Intent();
			intent.putExtra( KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESFUL_LEVEL_COMPLETION, 2 );
			setResult( RESULT_OK, intent );
			finish();
		}
		
		
		spinner.setAdapter(new ArrayAdapter<Knight>(
				this, android.R.layout.simple_spinner_item,
				knights));
		
		int position = 0;
		
		for ( int c = 0; c < knights.length; ++c ) {
			
			
			if ( knights[ c ] == view.selectedPlayer ) {
				position = c;
				knights[ c ].visual.setFrame( 1 );
			} else {
				knights[ c ].visual.setFrame( knights[ c ].isAlive() ? 0 : 2 );				
			}
		}
		
		spinner.setSelection( position );
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		if ( view.selectedPlayer == null || !view.selectedPlayer.isAlive() || ( (Knight)view.selectedPlayer ).hasExited ) {
			view.selectedPlayer = view.currentLevel.getKnights()[ 0 ];
			spinner.setSelection( 0 );
		} else {
			view.selectedPlayer = (Actor) spinner.getSelectedItem();
		}
		
		view.selectedTile = view.currentLevel.getTile( view.selectedPlayer.getPosition() );
		view.centerOn( view.selectedPlayer );
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {
		
		boolean[] keyMap = view.keyMap;

		for ( int c = 0; c < keyMap.length; ++c ) {
			keyMap[ c ] = false;
		}
		
		switch ( v.getId() ) {
		case R.id.btnUp:
			keyMap[ GameView.KB.UP.ordinal() ] = true;
			break;
		case R.id.btnDown:
			keyMap[ GameView.KB.DOWN.ordinal() ] = true;
			break;
		case R.id.btnLeft:
			keyMap[ GameView.KB.LEFT.ordinal() ] = true;
			break;
		case R.id.btnRight:
			keyMap[ GameView.KB.RIGHT.ordinal() ] = true;
			break;
		}
		
		if ( view.selectedPlayer != null && view.selectedPlayer.visual != null ) {
			view.selectedPlayer.visual.setFrame( 1 );
		}
		
		view.handleKeys(keyMap);
		
	}
}
