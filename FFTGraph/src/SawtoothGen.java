/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class SawtoothGen implements WaveGen {

	/** Žü”g” */
	private double freq = 0;
	/** ƒTƒ“ƒvƒŠƒ“ƒOŽü”g” */
	private double sample = 0;
	
	private int count = 0;
	
	public SawtoothGen(double freq2,double sample2) {
		init(freq2, sample2);
	}
	
	/**
	 * ‰Šú‰»
	 * @param freq   ¶¬‚·‚é‚Ì‚±‚¬‚è”gŒ`‚ÌŽü”g”
	 * @param sample ¶¬‚·‚é‚Ì‚±‚¬‚è”gŒ`‚ÌƒTƒ“ƒvƒŠƒ“ƒOŽü”g”
	 */
	public void init(double freq, double sample) {
		this.freq = freq;
		this.sample = sample;
		count = (int)(sample / freq) ;
	}

	/**
	 * ŽŸ‚Ì”gŒ`‚ðŽæ“¾‚·‚é
	 * @return ŽŸ‚Ì”gŒ`
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
