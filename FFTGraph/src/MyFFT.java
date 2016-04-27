/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.util.*;

public class MyFFT extends Thread{
	
	/** デフォルトの精度 */
	private static final int DEFAULT_N = 512;
	/** FFTの精度 */
	private int n ;
	private int n34;
	
	//窓関数なし
	public static final int WND_NONE = 0;
	//ハミング
	public static final int WND_HAMMING  = 1;
	//ブラックマン
	public static final int WND_BLKMAN  = 2;
	//ハン
	public static final int WND_HANN = 3;				
	/** 窓関数 */
	public int wndFnc = WND_NONE; 
	
	/** 回転因子用配列 */
	private double[] wnfft;
	/** ビット反転用配列 */
	private int[] brfft;

	/** 入力用実数*/
	private double[] xr;
	/** 入力用虚数*/
	private double[] xi;
		
	/** 出力用実数*/
	private double[] yr;
	/** 出力用実数*/
	private double[] yi;
	/** 振幅スペクトル*/
	private double[] as;
	
	
	
	/**
	 *デフォルトコンストラクタ 
	 */
	public MyFFT() {
		this(DEFAULT_N);
		
	}
	
	/**
	 *コンストラクタ 
	 */
	public MyFFT(int n) {
		this.n = n;
		this.n34 = (n*3)/4;	
		
		wnfft = new double[n34];
		brfft = new int[n];
		xr = new double[n];
		xi = new double[n];
		yr = new double[n];
		yi = new double[n];
		as = new double[n];
		
		//入力値、出力値用配列を初期化
		initFFTbuf();
		//回転因子テーブルの作成
		fftTable();
		//ビット反転テーブルの作成
		bitReverseTable();
	}		
	
	/**
	 * 入力値、出力値用配列を初期化 
	 */
	public void initFFTbuf(){
		for(int i = 0; i < xr.length ; i++){
			xr[i] = 0;
		}
		for(int i = 0; i < xi.length ; i++){
			xi[i] = 0;
		}
		for(int i = 0; i < yr.length ; i++){
			yr[i] = 0;
		}
		for(int i = 0; i < yi.length ; i++){
			yi[i] = 0;
		}
		for(int i = 0; i < as.length ; i++){
			as[i] = 0;
		}
	}
	

	/**
	 * FFTテーブルの作成
	 */
	public void fftTable(){			

		//配列初期化
		for(int i = 0; i < wnfft.length ; i++){
			wnfft[i] = 0;
		}
		
		//分割する角度
		double arg = 2*Math.PI/n;
		
		//COSテーブルの作成
		for(int i = 0; i < wnfft.length ; i++){
			wnfft[i] = Math.cos(arg*i);			
		}		
	}
	
	/**
	 * ビットリバーステーブルの作成
	 */
	public void bitReverseTable(){		
		int nHalf = n/2;

		//配列初期化
		for(int i = 0; i < brfft.length ; i++){
			brfft[i] = 0;
		}
		
		for(int i = 1; i < n ; i = i << 1){		
			for(int j = 0 ; j < i; j++){
				brfft[i+j] = brfft[j] + nHalf;				
			}
			nHalf = nHalf >> 1;
		}
	}
	/**
	 * 離散周波数変換
	 */
	public void dfft_time(double dt,double df){
		
		double yrr=0,yii=0;
		//System.out.println("dt="+dt);
		/*
		//入力データをコピー(ディープコピー)
		for(int i = 0; i < xr.length ; i++){
			yr[i] = xr[i];
			yi[i] = 0;
		}
		*/
		//窓関数の適用
		window(yr);
		//System.out.println("dt="+dt);
		for(int i=0; i<xr.length; i++){
			yrr=0;
			yii=0;
			for(int j=0; j < xr.length ; j++){
				yrr = yrr+xr[j]*Math.cos(2*Math.PI*(i*df)*(j*dt));
				yii = yii-xr[j]*Math.sin(2*Math.PI*(i*df)*(j*dt));
			}
			yr[i]=yrr;
			yi[i]=yii;
		}
		
	}
	
	/**
	 * 時間間引きFFT 
	 */
	public void fft_time(){		

		//入力データをコピー(ディープコピー)
		for(int i = 0; i < xr.length ; i++){
			yr[i] = xr[i];
			yi[i] = 0;
		}
		
		//窓関数の適用
		window(yr);
		
		double xtmpr,xtmpi;
		int jnh,jxC,nHalf,nHalf2;
		int step;
		double arg;
				
		//時間間引きのためデータを反転
		for(int j=0 ; j < n ; j++){
			if(j<brfft[j]){
				double tmp = 0;
				tmp = yr[j];
				
				yr[j] = yr[brfft[j]];
				yr[brfft[j]] = tmp;
				
				tmp = yi[j];
				yi[j] = yi[brfft[j]];
				yi[brfft[j]] = tmp;
			}
		}
		
		nHalf  = 1;
		nHalf2 = 2;
		
		for(step = (n>>1) ; step >= 1; step = (step>>1)){
			
			for(int k = 0; k<n; k= k+nHalf2){
				
				jxC = 0;				
				for(int j = k ; j < (k+nHalf);j++){
					
					jnh = j + nHalf;
					
					xtmpr = yr[jnh];
					xtmpi = yi[jnh];
					
					arg = 2*Math.PI / n;
					
					yr[jnh] = xtmpr*Math.cos(arg*jxC) + xtmpi*Math.sin(arg*jxC); 
					yi[jnh] = xtmpi*Math.cos(arg*jxC) - xtmpr*Math.sin(arg*jxC);

					xtmpr = yr[j];
					xtmpi = yi[j];
					
					yr[j] = xtmpr + yr[jnh];
					yi[j] = xtmpi + yi[jnh];
					
					yr[jnh] = xtmpr - yr[jnh];
					yi[jnh] = xtmpi - yi[jnh];
					
					jxC = jxC + step;
				}				
			}
			nHalf = nHalf << 1;
			nHalf2 = nHalf2 << 1;			
		}				
	}
	
	/**
	 * 窓関数の適用
	 */
	public void window(double[] array){
		double weight = 0;
		switch (wndFnc) {		
		case WND_NONE:   //なし
			return;						
		case WND_HAMMING://ハミング
			for(int i = 0; i < array.length ; i++){
				weight = 0.54 - 0.46 * Math.cos(2*Math.PI*i/(array.length - 1));				
				array[i] = array[i]*weight;			 
			}
			System.out.println("hamming");
			break;
		case WND_BLKMAN: //ブラックマン
			for(int i = 0; i < array.length ; i++){
				weight = 0.42 - 0.5 * Math.cos(2*Math.PI*i/(array.length - 1)) 
				        +0.08 * Math.cos(4*Math.PI*i/(array.length - 1));			
				array[i] = array[i]*weight;			 
			}
			System.out.println("blackman");
			break;
		case WND_HANN:   //ハン
			for(int i = 0; i < array.length ; i++){
				weight = 0.5 - 0.5 * Math.cos(2*Math.PI*i/(array.length - 1));						
				array[i] = array[i]*weight;			 
			}
			System.out.println("hann");
			break;
		default:
			break;
		
		}
	}
	
	/**
	 * 入力信号を更新する
	 * @param val 新たに入れる入力信号
	 */
	public void putInput(double val){
		//シフト
		for(int i = xr.length - 1; i > 0 ;i--){
			xr[i] = xr[i - 1];
		}
		xr[0] = val;
	}
	
	/**
	 * 振幅スペクトルの配列を得る
	 * @return 振幅スペクトルの配列
	 */
	public double[] getASpectrum(){
		//FFT
		fft_time();
		
		for(int i = 0; i < as.length ; i++){
			//振幅スペクトルの計算
			as[i] = Math.sqrt(yr[i]*yr[i] + yi[i]*yi[i]);
		}
		
		return as;
	}	
	
	public double[] getDFFTSpectrum(double dt,double df){
		//DFFT
		dfft_time(dt, df);
		
		for(int i = 0; i < as.length ; i++){
			//振幅スペクトルの計算
			as[i] = Math.sqrt(yr[i]*yr[i] + yi[i]*yi[i]);
		}
		
		return as;
	}
	
	/**
	 * FFTの精度Nを返す
	 * @return
	 */
	public int getN(){
		return this.n;
	}
	
	/**
	 * 窓関数の設定
	 * @param wndFnc 窓関数定数
	 */
	public void setWndFnc(int wndFnc){
		this.wndFnc = wndFnc;	
	}

}
