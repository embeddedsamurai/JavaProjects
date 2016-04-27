/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

public interface WaveGen {
	
	/**
	 * ������
	 * @param freq ��������M���̎��g��
	 * @param sample �T���v�����O���g��
	 */
	abstract public void init(double freq,double sample);
	
	/**
	 * ���̐M���𓾂�
	 * @return ���̐M��
	 */
	abstract public double nextWave();
}