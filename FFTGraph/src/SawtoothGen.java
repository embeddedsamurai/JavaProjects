/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class SawtoothGen implements WaveGen {

	/** 周波数 */
	private double freq = 0;
	/** サンプリング周波数 */
	private double sample = 0;
	
	private int count = 0;
	
	public SawtoothGen(double freq2,double sample2) {
		init(freq2, sample2);
	}
	
	/**
	 * 初期化
	 * @param freq   生成するのこぎり波形の周波数
	 * @param sample 生成するのこぎり波形のサンプリング周波数
	 */
	public void init(double freq, double sample) {
		this.freq = freq;
		this.sample = sample;
		count = (int)(sample / freq) ;
	}

	/**
	 * 次の波形を取得する
	 * @return 次の波形
	 */
	public double nextWave() {
		
		if(count >= (sample /freq)){
			count = 0;		
		}
		double tmp = 2*((double)count /(double)(sample /freq)) - 1;
		count++;		
		
		return tmp;
	}

}
