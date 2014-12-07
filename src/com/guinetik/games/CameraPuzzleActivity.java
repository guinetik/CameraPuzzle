package com.guinetik.games;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class CameraPuzzleActivity extends Activity 
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView txtIntro = (TextView) this.findViewById(R.id.txtIntro);
        txtIntro.setText(Html.fromHtml(getResources().getString(R.string.instructions)));
        
    }
    
    public void visualizarRecordes(View v)
    {
    	
    	Intent it = new Intent(this, RecordesActivity.class);
    	this.startActivity(it);
    	
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
    	
    	Intent it = new Intent(this, GameActivity.class);
    	it.putExtra("modo", modo);
    	this.startActivity(it);
    	
    }
    
}