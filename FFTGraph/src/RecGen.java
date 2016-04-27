/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class RecGen implements WaveGen{
	
	/** ���g�� */
	private double freq;
	/** �T���v�����O���g�� */
	private double sample;
	
	private int count = 0;

	/**
	 * �R���X�g���N�^ 
	 * @param freq2   ���������`�g�̎��g��
	 * @param sample2 ���������`�g�̃T���v�����O���g��
	 */
	public RecGen(double freq,double sample) {
		//������
		init(freq, sample);
	}
	
	/**
	 * ������
	 * @param freq   ���������`�g�̎��g��
	 * @param sample ���������`�g�̃T���v�����O���g��
	 */
	public void init(double freq, double sample){
		this.freq = freq;
		this.sample = sample;
		count = 0;
	}
	
	/**
	 * ��`�g�𓾂�
	 * @return ����������`�g
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
