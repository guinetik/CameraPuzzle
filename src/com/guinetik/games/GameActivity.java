package com.guinetik.games;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class GameActivity extends Activity 
{
	
	private GameView 										game;
	private Handler 										mHandler 			= new Handler();
	private int 											tempoTotal;
	
	private TextView 										txtTimer;
	private TextView 										txtGame;
	private TextView 										txtNumMoves;
	
	private Boolean											isGameFinished 		= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		
		final Boolean customTitleSupported 					= requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		String modo 										= this.getIntent().getStringExtra("modo");
		game 												= new GameView(this);
		game.setGameListener(listener);
		
		if(modo.equals("SFO"))
		{
			
			game.setMostrarNumeros(false);
			
		} else 
		{
			
			game.setMostrarNumeros(true);
			
		}
		
		this.setContentView(game);
		
		
		if(customTitleSupported)
		{
			
			 getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
			 
			 txtGame					 					= (TextView) this.findViewById(R.id.txtGame);
			 txtTimer 										= (TextView) this.findViewById(R.id.txtTimer);
			 txtNumMoves 									= (TextView) this.findViewById(R.id.txtNumMoves);
			 
			 txtNumMoves.setText("Jogadas: " + mostrarO(game.getNumMoves()));
			 txtGame.setText("Camera Puzzle - Modo: " + modo);
			 
			 tempoTotal 									= 0;
			 mHandler.removeCallbacks(atualizarTempo);
			 mHandler.postDelayed(atualizarTempo, 1000);
			
		}		
		
	}
	
	

	@Override
	protected void onPause() 
	{
		
		mHandler.removeCallbacks(atualizarTempo);
		tempoTotal = 0;
		game.reset();
		txtNumMoves.setText("Jogadas: " + mostrarO(game.getNumMoves()));
		txtTimer.setText("00:00");
		super.onPause();
		
	}
	
	

	@Override
	protected void onRestart() 
	{
		mHandler.removeCallbacks(atualizarTempo);
		tempoTotal = 0;
		game.reset();
		txtNumMoves.setText("Jogadas: " + mostrarO(game.getNumMoves()));
		txtTimer.setText("00:00");
		mHandler.postDelayed(atualizarTempo, 1000);
		super.onRestart();
	}



	@Override
	public void finish() 
	{
		
		mHandler.removeCallbacks(atualizarTempo);
		super.finish();
		
	}
	
	private GameListener listener 							= new GameListener() 
	{
		
		public void onUpdateUI() 
		{
			
			txtNumMoves.setText("Jogadas: " + mostrarO(game.getNumMoves()));
			
		}
		
		public void onGameFinished() 
		{
			
			isGameFinished									= true;
			
		}
	};
	
	private void gameOver() 
	{
		
		Intent it 											= new Intent(this, GameOverActivity.class);
		it.putExtra("numMoves", mostrarO(game.getNumMoves()));
    	it.putExtra("time", txtTimer.getText());
    	startActivity(it);
		
	}
	
	private String mostrarO(int valor)
	{
		
		String s = "";
		
		if(valor < 10) s = "0" + String.valueOf(valor);
		else s = String.valueOf(valor);
		
		return s;
		
	}

	private Runnable atualizarTempo 						= new Runnable() 
	{
		   public void run() 
		   {
			   
			   tempoTotal++;		       
		       int segundos			 						= tempoTotal;
		       int minutos 									= segundos / 60;
		       segundos     								= segundos % 60;

		       txtTimer.setText("" + mostrarO(minutos) + ":" + mostrarO(segundos));
		       
		       Log.d("GameActivity", "run -->" + txtTimer.getText());
		     
		       if(isGameFinished)
		       {
		    	   
		    	   gameOver();
		    	   
		       } else 
		       {
		    	   
		    	   mHandler.postDelayed(atualizarTempo, 1000);
		    	   
		       }
		   }
		};

}
