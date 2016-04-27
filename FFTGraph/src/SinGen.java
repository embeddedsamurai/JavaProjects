/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class SinGen implements WaveGen{
	
	//サイン波生成用
	private double[] y = new double[3];
	private double coffSingen = 0;
	private double a1 = 0.0;
	private double a2 = 0.0;
	
	/**
	 * コンストラクタ
	 * @param freq 生成するサイン波の周波数
	 * @param sample サンプリング周波数
	 */
	public SinGen(double freq,double sample) {
		init(freq, sample);
	}
	
	/**
	 * サイン波生成のための変数の初期化処理をする
	 * @param freq 生成するサイン波の周波数
	 * @param sample サンプリング周波数
	 */
	public void init(double freq,double sample){
		coffSingen = (2 * Math.PI * (double)freq) / (double)sample;
		
		y[0] = 0.0;
		y[1] = Math.sin(coffSingen);
		y[2] = 0.0;

		a1 = 2 * Math.cos(this.coffSingen);
		a2 = -1;
	}

	/**
	 * サイン波生成用関数
	 */
	public double nextWave(){
		y[0] = a1 * y[1] + a2 * y[2];
		//シフト
		y[2] = y[1];
		y[1] = y[0];

		return y[0];
	}	

}