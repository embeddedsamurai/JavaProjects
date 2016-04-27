/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class TriangleGen implements WaveGen {
		
	private int count = 0;
	private double sample = 0;
	private double freq = 0;
	private boolean flag = true;
	/**
	 * コンストラクタ
	 * @param freq　生成する三角波の周波数
	 * @param sample サンプル周波数
	 */
	public TriangleGen(double freq,double sample) {		
		init(freq, sample);
	}

	public void init(double freq, double sample) {
		this.freq = freq;
		this.sample = sample;		
	}

	public double nextWave() {
		double tmp = 2*((double)count /(double)((sample/2)/freq)) - 1.;
		
		if(flag){
			if(count >= ((sample/2)/freq)){
				flag = false;
				count--;
			}else{
				count++;	
			}
													
			return tmp;
		}else{
			if(count <= 0){
				flag = true;
				count++;
			}else{
				count--;	
			}			
			return tmp;
		}
		
	}

}