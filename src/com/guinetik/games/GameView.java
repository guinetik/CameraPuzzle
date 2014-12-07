package com.guinetik.games;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.samples.SampleCvViewBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SampleCvViewBase implements OnTouchListener 
{
	
	GameListener gameListener									= null;
	
	private Mat[]   											celulas;
    private Mat     											celulasCores;
    private Mat    					 							celulasCores15;
    private Mat[]   											celulasFrames;
    private int[]   											posicoes;
    private int[]   											mTextWidths;
    private int[]  				 								mTextHeights;
    
    private boolean mostrarNumeros 								= true;

    int             gridSize        				 			= 4;
    int             gridArea        			 				= gridSize * gridSize;
    int             frameVazio    	 							= gridArea - 1;
    
    private int		numMoves									= 0;

    public GameView(Context context)
    {
    	
        super(context);
        setOnTouchListener(this);

        mTextWidths 											= new int[gridArea];
        mTextHeights 											= new int[gridArea];
        for (int i = 0; i < gridArea; i++) 
        {
        	
            Size s 												= Core.getTextSize(Integer.toString(i + 1), 3/* CV_FONT_HERSHEY_COMPLEX */, 1, 2, null);
            mTextHeights[i]		 								= (int) s.height;
            mTextWidths[i]										= (int) s.width;
            
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height)
    {
    	
    	// reinicia o game
    	
        super.surfaceChanged(_holder, format, width, height);
        
        synchronized (this) 
        {
            celulasCores 										= new Mat();
        }
        
    }

    public static void embaralharFrames(int[] array) 
    {
    	
        for (int i = array.length; i > 1; i--) 
        {
        	
            int temp 											= array[i - 1];
            int randIx 											= (int) (Math.random() * i);
            array[i - 1] 										= array[randIx];
            array[randIx] 										= temp;
            
        }
    }

    public boolean estaEmbaralhado() 
    {
    	
        if (gridSize != 4) return true;

        int soma 																	= 0;
        for (int i = 0; i < gridArea; i++) 
        {
        	
            if (posicoes[i] == frameVazio) soma += (i / gridSize) + 1;
            else 
            {
                int menorNumero	 													= 0;
                
                for (int j = i + 1; j < gridArea; j++) 
                {
                	
                    if (posicoes[j] < posicoes[i]) menorNumero++;
                    
                }
                
                soma 																+= menorNumero;
            }
        }

        return soma % 2 == 0;
    }

    private void montarPuzzle(int cols, int rows)
    {
    	
        celulas 																	= new Mat[gridArea];
        celulasFrames 																= new Mat[gridArea];

        celulasCores15							 									= new Mat(rows, cols, celulasCores.type());
        posicoes 																	= new int[gridArea];

        for (int i = 0; i < gridSize; i++) 
        {
        	
            for (int j = 0; j < gridSize; j++) 
            {
                int k 																= i * gridSize + j;
                posicoes[k] 														= k;
                celulas[k] 															= celulasCores.submat(i * rows / gridSize, (i + 1) * rows / gridSize, j * cols / gridSize, (j + 1) * cols / gridSize);
                celulasFrames[k] 													= celulasCores15.submat(i * rows / gridSize, (i + 1) * rows / gridSize, j * cols / gridSize, (j + 1) * cols / gridSize);
            }
            
        }

        novoJogo();
    }

    public void novoJogo() 
    {
    	
    	// pra dizer que nunca usei do while, rs
    	
        do 
        {
        	
            embaralharFrames(posicoes);
            
        } while (!estaEmbaralhado());
    }

    @Override
    protected Bitmap processFrame(VideoCapture capture) 
    {
    	
        capture.retrieve(celulasCores, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
        int colunas 																= celulasCores.cols();
        int linhas 																	= celulasCores.rows();

        if (celulas == null) montarPuzzle(colunas, linhas);

        // embaralhando
        for (int i = 0; i < gridArea; i++) 
        {
        	
            int idFrame 															= posicoes[i];
            
            if (idFrame == frameVazio)
            {
            	
            	celulasFrames[i].setTo(new Scalar(0x33, 0x33, 0x33, 0xFF));
            	
            }
            else 
            {
            	
                celulas[idFrame].copyTo(celulasFrames[i]);
                
                // sfs - sfo
                if (mostrandoNumeros()) 
                {
                	
                    Core.putText(celulasFrames[i], Integer.toString(1 + idFrame), new Point((colunas / gridSize - mTextWidths[idFrame]) / 2, (linhas / gridSize + mTextHeights[idFrame]) / 2), 2/* CV_FONT_HERSHEY_COMPLEX */, 1, new Scalar(255, 255, 255, 255), 1);
                    
                }
            }
        }

        montarGrade(colunas, linhas);

        Bitmap bmp 																	= Bitmap.createBitmap(colunas, linhas, Bitmap.Config.ARGB_8888);
        
        if (Utils.matToBitmap(celulasCores15, bmp)) return bmp;
        
        bmp.recycle();
        
        return null;
    }

    private void montarGrade(int colunas, int linhas) 
    {
    	
        for (int i = 1; i < gridSize; i++) 
        {

            Core.line(celulasCores15, new Point(0, i * linhas / gridSize), new Point(colunas, i * linhas / gridSize), new Scalar(100, 100, 100, 255), 1);
            Core.line(celulasCores15, new Point(i * colunas / gridSize, 0), new Point(i * colunas / gridSize, linhas), new Scalar(100, 100, 100, 255), 1);
            
        }
        
        /*for (int i = 1; i < gridSize; i++) old
        {
        	
            Core.line(celulasCores15, new Point(-i, (i * linhas) % gridSize), new Point(colunas, i * linhas / gridSize), new Scalar(0, 255, 0, 255), 3);
            Core.line(celulasCores15, new Point(i * colunas / gridSize, 0), new Point(i * colunas / gridSize, linhas), new Scalar(0, 255, 0, 255), 3);
            
        }*/
        
    }

    @Override
    public void run() 
    {
    	
        super.run();

        synchronized (this) 
        {
            // garbage collection
            if (celulas != null) 
            {
            	
                for (Mat m : celulas)
                    m.release();
            }
            
            if (celulasFrames != null) 
            {
                for (Mat m : celulasFrames)
                    m.release();
            }
            
            if (celulasCores != null) celulasCores.release();
            if (celulasCores15 != null) celulasCores15.release();

            celulasCores 													= null;
            celulasCores15 													= null;
            celulas 														= null;
            celulasFrames 													= null;
            posicoes 														= null;
            
        }
    }

    public boolean onTouch(View v, MotionEvent event) 
    {
    	
    	Log.d("GameView", "onTouch");
    	
        int colunas 														= celulasCores.cols();
        int linhas 															= celulasCores.rows();
        float xoffset 														= (this.getWidth() - colunas) / 2;
        float yoffset 														= (this.getHeight() - linhas) / 2;

        float x	 															= event.getX() - xoffset;
        float y 															= event.getY() - yoffset;

        int linha 															= (int) Math.floor(y * gridSize / linhas);
        int coluna 															= (int) Math.floor(x * gridSize / colunas);

        if (linha < 0 || linha >= gridSize || coluna < 0 || coluna >= gridSize) return false;

        int idFrame 														= linha * gridSize + coluna;
        int idTroca 														= -1;

        // esquerda
        if (idTroca < 0 && coluna > 0)
        {
        	
        	if (posicoes[idFrame - 1] == frameVazio) idTroca 				= idFrame - 1;
        	
        }
        // direita
        if (idTroca < 0 && coluna < gridSize - 1)
        {
        	
        	if (posicoes[idFrame + 1] == frameVazio) idTroca 				= idFrame + 1;
        	
        }
        // cima
        if (idTroca < 0 && linha > 0)
        {
        	
        	if (posicoes[idFrame - gridSize] == frameVazio) idTroca 		= idFrame - gridSize;
        	
        }
        // baixo
        if (idTroca < 0 && linha < gridSize - 1)
        {
        	
        	if (posicoes[idFrame + gridSize] == frameVazio) idTroca 		= idFrame + gridSize;
        	
        }

        // troca
        if (idTroca >= 0) 
        {
            synchronized (this) 
            {
                int touched 											= posicoes[idFrame];
                posicoes[idFrame] 										= posicoes[idTroca];
                posicoes[idTroca] 										= touched;
                
                numMoves++;
                
                Log.d("GameView", "Movimentos acumulados: " + String.valueOf(getNumMoves()));
                
                gameListener.onUpdateUI();
                
                checkSeGanhou();
                
            }
        }

        return false; 
    }

	private void checkSeGanhou() 
	{
		
		String s = "";
        
        for (int i : posicoes) 
        {
			
        	s+= String.valueOf(i);
        	
		}
        
        Log.d("GameView", "Ordem do Puzzle = " + s);
        
        if(s.equals("0123456789101112131415"))
        {
        	
        	Log.d("GameView", "Fim do Jogo!");
        	gameListener.onGameFinished();
        	
        }
		
	}	
	
	public void setGameListener(GameListener listener)
    {
    	
    	this.gameListener										= listener;
    	
    }

	public void setMostrarNumeros(boolean mostrarNumeros) 
	{
		
		this.mostrarNumeros 									= mostrarNumeros;
		
	}

	public boolean mostrandoNumeros() 
	{
		
		return mostrarNumeros;
		
	}

	public int getNumMoves() 
	{
		return numMoves;
	}

	public void reset() 
	{
		
		numMoves = 0;
		
	}
}