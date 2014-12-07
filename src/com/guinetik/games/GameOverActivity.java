package com.guinetik.games;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GameOverActivity extends Activity 
{
	
	private EditText txtNome;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.game_over);
		
		final TextView txtJogadas 						= (TextView) this.findViewById(R.id.txtJogadas);
		final TextView txtTempoDecorrido				= (TextView) this.findViewById(R.id.txtTempoDecorrido);
		
		txtNome											= (EditText) this.findViewById(R.id.txtNome);
		
		txtJogadas.setText(getResources().getString(R.string.num_jogadas) + " " + this.getIntent().getStringExtra("numMoves"));
		txtTempoDecorrido.setText(getResources().getString(R.string.tempo_decorrido) + " " + this.getIntent().getStringExtra("time"));
		
	}
	
	public void registrarRecorde(View v)
	{
		
		if(!TextUtils.isEmpty(txtNome.getText()))
		{
			
			Intent it 									= new Intent(this, RecordesActivity.class);
			
			it.putExtra("nome", txtNome.getText());
			it.putExtra("numMoves", this.getIntent().getStringExtra("numMoves"));
			it.putExtra("time", this.getIntent().getStringExtra("time"));
			
			this.startActivity(it);
			
		}
		
	}

	public void newGameSFS(View v)
    {
    	
    	this.startNewGame("SFS");
    	
    }
    
    public void newGameSFO(View v)
    {
    	
    	this.startNewGame("SFO");
    	
    }
    
    public void startNewGame(String modo)
    {
    	
    	Intent it 										= new Intent(this, GameActivity.class);
    	it.putExtra("modo", modo);
    	this.startActivity(it);
    	
    }
    
}
