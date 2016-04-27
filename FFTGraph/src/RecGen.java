/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class RecGen implements WaveGen{
	
	/** 周波数 */
	private double freq;
	/** サンプリング周波数 */
	private double sample;
	
	private int count = 0;

	/**
	 * コンストラクタ 
	 * @param freq2   生成する矩形波の周波数
	 * @param sample2 生成する矩形波のサンプリング周波数
	 */
	public RecGen(double freq,double sample) {
		//初期化
		init(freq, sample);
	}
	
	/**
	 * 初期化
	 * @param freq   生成する矩形波の周波数
	 * @param sample 生成する矩形波のサンプリング周波数
	 */
	public void init(double freq, double sample){
		this.freq = freq;
		this.sample = sample;
		count = 0;
	}
	
	/**
	 * 矩形波を得る
	 * @return 生成した矩形波
	 */
	public double nextWave(){
		
		if(count++ < (sample/freq)/2){			
			return 1.0;			
		}else{
			if(count >= (sample/freq)) count = 0;			
			return -1.0;
		}		
		
	}
}
