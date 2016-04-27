/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public class SinGen implements WaveGen{
	
	//�T�C���g�����p
	private double[] y = new double[3];
	private double coffSingen = 0;
	private double a1 = 0.0;
	private double a2 = 0.0;
	
	/**
	 * �R���X�g���N�^
	 * @param freq ��������T�C���g�̎��g��
	 * @param sample �T���v�����O���g��
	 */
	public SinGen(double freq,double sample) {
		init(freq, sample);
	}
	
	/**
	 * �T�C���g�����̂��߂̕ϐ��̏���������������
	 * @param freq ��������T�C���g�̎��g��
	 * @param sample �T���v�����O���g��
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
	 * �T�C���g�����p�֐�
	 */
	public double nextWave(){
		y[0] = a1 * y[1] + a2 * y[2];
		//�V�t�g
		y[2] = y[1];
		y[1] = y[0];

		return y[0];
	}	

}