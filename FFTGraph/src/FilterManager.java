/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.applet.*;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;


public class FilterManager  implements Runnable{
	
	/** For Debug */
	public static final boolean DEBUG=false;

	/** Applet コピー用 */
	private Applet  mApplet;
	
	/** 正弦波などのグラフを表示するためのパネル */
	SignalPanel panel;

	/** スペクトル用のパネル */
	SpectrumPanel spectrum;
	
	/** Graphics */
	public Graphics back;
	
	/** Key */
	public Key key;
		
	/** データ数 */
	public int Num=512;
	/** 生成波形の周波数 */
	private double freq = 5;
	
	/** サンプリングレート周波数 */
	private double sample = 250;
	
	/** サンプリング周期 */
	public double dt= 1./sample;
	
	/** 基本周波数 */
	public double df=dt/(double)Num;
	/** 入力信号生成クラス */
	private WaveGen waveGen;

	/** 波形を進めるためのフラグ */
	private static boolean waveFlag=false;

	public static boolean runFlag;
	
	public static final int SLEEP_TIME=500;

	MyFFT fft;
	
	public static int fftmode;
	
	Thread thread;
	public MyCanvas mc;
	
	/**
	 * コンストラクタ
	 */
	public FilterManager(FFTGraph applet) {

		fftmode=SET_DFT;
		
		this.mApplet = applet;
		this.back    = applet.back;
		this.key     = applet.key;
		this.mc      = applet.mc;
		
		//dt
		dt= 1./sample;
		df= sample/(double)Num;
		
		//サイン生成クラス(初期ではサイン波)
		waveGen = new SinGen(freq,sample);    	
		//サイン波生成用変数の初期化処理
		waveGen.init(freq, sample);
				
		//パネルの設定
		panel=new SignalPanel(this.mc);
		panel.setInfoLabel(freq,sample);

		//スペクトル表示用パネルの作成
		spectrum = new SpectrumPanel(this.mc,this.sample);
		spectrum.setInfoLabel(512);


		waveFlag=false;
		
		//FFT計算クラス
		fft = new MyFFT();
		fftstart();

	}
	
	/**
		* 実行の開始
		*/
	public void fftstart(){
		//初期化処理
		//init();
		//実行フラグを立てる
		runFlag = true;
		//スレッドの起動
		thread = new Thread(this);
		//実行の開始
		thread.start();
	}
	
	public void run(){
		try{
			//スリープ時間
			long startTime = System.currentTimeMillis();
			//計算にかかった時間
			long pastTime = 0;

			while(runFlag){

				startTime = System.currentTimeMillis();

				//ここでフーリエ変換する
				if(fftmode==SET_FFT){
					
					spectrum.setSpectrum(fft.getASpectrum());
				}else if(fftmode==SET_DFT){
					//ここで離散フーリエ変換する
					
					spectrum.setSpectrum2(fft.getDFFTSpectrum(this.dt,this.df));
				}else if(fftmode==SET_ALLFT){
					
					spectrum.setSpectrum(fft.getASpectrum());
					spectrum.setSpectrum2(fft.getDFFTSpectrum(this.dt,this.df));
				}
				//計算にかかった時間
				pastTime = System.currentTimeMillis() - startTime;

				if(pastTime < SLEEP_TIME){
					//休止
					pause(SLEEP_TIME+5 - pastTime);
				}

			}//end of while(1)
		}catch(Exception e){
			e.printStackTrace();
		}

	}//end of run

	/**
	 * スレッドの休止
	 */
	public void pause(long time){
		try {
			//スリープ
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(){
		waveFlag=true;
	}
	
	public void stop(){
		waveFlag=false;
	}

	/**
	 * メイン処理
	 */
	public void process(){
		//キー処理
		key();
		//リクエストの処理
		if(waveFlag) newDataInput();
		//描画
		draw();
	}
	
		
	/**
		* キー処理
		*/
	private void key(){
	}
	
	/**
		* キューの中のリクエストを処理する
		*/
	private void newDataInput(){
		
		//新たなデータを一つ入れる
		//サンプリングレート250Hzの場合は1つのデータは0.004秒ごとに置かれる
		double data = waveGen.nextWave();
		panel.putInput(data);
		//FFT入力バッファの更新
		fft.putInput(data);
	}

	
	/**
		* 描画処理
		*/
	private void draw() {
		panel.paintComponent(this.dt);
		spectrum.paintComponent(this.df);
	}

	
	/**
		* panelのx Gainの変更
		* @param value
		*/
	public void setXGain(int value){
		if(panel!=null) panel.setXGain(value);
		System.out.println("alltime"+panel.alltime);
		mc.changeUserWindow(0,panel.alltime-(value*0.01),-1,1,1);
		//mc.changeUserWindow(0,spectrum.nyquist-value,-10,200,2);
	}

	public void setXFFTGain(int value){
		//mc.changeUserWindow(0,panel.alltime-(panel.stime*value*3),-1,1,1);
		mc.changeUserWindow(0,spectrum.nyquist-value,-10,200,2);
	}
	
	//窓関数なし
	public static final int SET_FFT = 0;
	//ハミング
	public static final int SET_DFT  = 1;
	//ブラックマン
	public static final int SET_ALLFT  = 2;
	
	public void setFFTMode(int value){
		fftmode = value;
	}
	/**
	 * 生成する信号の周波数の変更
	 * @param value
	 */
	public void setFrequency(double freq){
		this.freq = freq;
		//this.sample = sample;
		waveGen.init(this.freq, this.sample);
		panel.setInfoLabel(this.freq,this.sample);
	}

	public void setSampleRate(double sample){
		//this.freq = freq;
		this.sample = sample;
		this.dt = 1/sample;
		this.df = sample/(double)Num;
		waveGen.init(this.freq, this.sample);
		panel.setInfoLabel(freq,sample);
	}
	/**
	 * 生成する信号の周波数を取得
	 * @return
	 */
	public double getFrequency(){
		return this.freq;
	}

	/**
	 * サンプリング周波数を取得
	 * @return
	 */
	public double getSampleRate(){
		return this.sample;
	}

	/**
		* 生成する信号の種類を変更
		* @param gen
		*/
	//サイン波
	public static final int INPUT_SIN = 0;
	//矩形波
	public static final int INPUT_REC = 1;
	//のこぎり波
	public static final int INPUT_SAW = 2;
	//三角波
	public static final int INPUT_TRI = 3;
	//心電図
	public static final int INPUT_ECG = 4;
	//脈波
	public static final int INPUT_PLS = 5;

	//入力信号
	//private int inputWave = INPUT_SIN;

	//MainGraph.javaのitemStateChangedから呼ばれる。
	public void setWaveGen(int gen){
		switch (gen) {
		case INPUT_SIN://サイン波
			waveGen = new SinGen(freq,sample);
			break;
		case INPUT_REC://矩形波
			waveGen = new RecGen(freq,sample);
			break;
		case INPUT_SAW://のこぎり波
			waveGen = new SawtoothGen(freq,sample);
			break;
		case INPUT_TRI://三角波
			waveGen = new TriangleGen(freq,sample);
			break;
		case INPUT_ECG://ECG
			waveGen = new ECG(mApplet.getCodeBase().toString() + "ecg1.txt");
			break;
		case INPUT_PLS://脈波
			waveGen = new PLS(mApplet.getCodeBase().toString() + "pls1.txt");
		default:
			break;
		}
	}
	
	//窓関数なし
	public static final int WND_NONE = 0;
	//ハミング
	public static final int WND_HAMMING  = 1;
	//ブラックマン
	public static final int WND_BLKMAN  = 2;
	//ハン
	public static final int WND_HANN = 3;	
	
	public void setWndFuc(int wndFnc){
		fft.setWndFnc(wndFnc);
	}
	
	

}
