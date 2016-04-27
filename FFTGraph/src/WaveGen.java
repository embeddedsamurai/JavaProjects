/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public interface WaveGen {
	
	/**
	 * 初期化
	 * @param freq 生成する信号の周波数
	 * @param sample サンプリング周波数
	 */
	abstract public void init(double freq,double sample);
	
	/**
	 * 次の信号を得る
	 * @return 次の信号
	 */
	abstract public double nextWave();
}