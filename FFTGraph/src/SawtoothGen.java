/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class SawtoothGen implements WaveGen {

	/** ���g�� */
	private double freq = 0;
	/** �T���v�����O���g�� */
	private double sample = 0;
	
	private int count = 0;
	
	public SawtoothGen(double freq2,double sample2) {
		init(freq2, sample2);
	}
	
	/**
	 * ������
	 * @param freq   ��������̂�����g�`�̎��g��
	 * @param sample ��������̂�����g�`�̃T���v�����O���g��
	 */
	public void init(double freq, double sample) {
		this.freq = freq;
		this.sample = sample;
		count = (int)(sample / freq) ;
	}

	/**
	 * ���̔g�`���擾����
	 * @return ���̔g�`
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
