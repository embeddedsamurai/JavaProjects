/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.util.*;

public class MyFFT extends Thread{
	
	/** �f�t�H���g�̐��x */
	private static final int DEFAULT_N = 512;
	/** FFT�̐��x */
	private int n ;
	private int n34;
	
	//���֐��Ȃ�
	public static final int WND_NONE = 0;
	//�n�~���O
	public static final int WND_HAMMING  = 1;
	//�u���b�N�}��
	public static final int WND_BLKMAN  = 2;
	//�n��
	public static final int WND_HANN = 3;				
	/** ���֐� */
	public int wndFnc = WND_NONE; 
	
	/** ��]���q�p�z�� */
	private double[] wnfft;
	/** �r�b�g���]�p�z�� */
	private int[] brfft;

	/** ���͗p����*/
	private double[] xr;
	/** ���͗p����*/
	private double[] xi;
		
	/** �o�͗p����*/
	private double[] yr;
	/** �o�͗p����*/
	private double[] yi;
	/** �U���X�y�N�g��*/
	private double[] as;
	
	
	
	/**
	 *�f�t�H���g�R���X�g���N�^ 
	 */
	public MyFFT() {
		this(DEFAULT_N);
		
	}
	
	/**
	 *�R���X�g���N�^ 
	 */
	public MyFFT(int n) {
		this.n = n;
		this.n34 = (n*3)/4;	
		
		wnfft = new double[n34];
		brfft = new int[n];
		xr = new double[n];
		xi = new double[n];
		yr = new double[n];
		yi = new double[n];
		as = new double[n];
		
		//���͒l�A�o�͒l�p�z���������
		initFFTbuf();
		//��]���q�e�[�u���̍쐬
		fftTable();
		//�r�b�g���]�e�[�u���̍쐬
		bitReverseTable();
	}		
	
	/**
	 * ���͒l�A�o�͒l�p�z��������� 
	 */
	public void initFFTbuf(){
		for(int i = 0; i < xr.length ; i++){
			xr[i] = 0;
		}
		for(int i = 0; i < xi.length ; i++){
			xi[i] = 0;
		}
		for(int i = 0; i < yr.length ; i++){
			yr[i] = 0;
		}
		for(int i = 0; i < yi.length ; i++){
			yi[i] = 0;
		}
		for(int i = 0; i < as.length ; i++){
			as[i] = 0;
		}
	}
	

	/**
	 * FFT�e�[�u���̍쐬
	 */
	public void fftTable(){			

		//�z�񏉊���
		for(int i = 0; i < wnfft.length ; i++){
			wnfft[i] = 0;
		}
		
		//��������p�x
		double arg = 2*Math.PI/n;
		
		//COS�e�[�u���̍쐬
		for(int i = 0; i < wnfft.length ; i++){
			wnfft[i] = Math.cos(arg*i);			
		}		
	}
	
	/**
	 * �r�b�g���o�[�X�e�[�u���̍쐬
	 */
	public void bitReverseTable(){		
		int nHalf = n/2;

		//�z�񏉊���
		for(int i = 0; i < brfft.length ; i++){
			brfft[i] = 0;
		}
		
		for(int i = 1; i < n ; i = i << 1){		
			for(int j = 0 ; j < i; j++){
				brfft[i+j] = brfft[j] + nHalf;				
			}
			nHalf = nHalf >> 1;
		}
	}
	/**
	 * ���U���g���ϊ�
	 */
	public void dfft_time(double dt,double df){
		
		double yrr=0,yii=0;
		//System.out.println("dt="+dt);
		/*
		//���̓f�[�^���R�s�[(�f�B�[�v�R�s�[)
		for(int i = 0; i < xr.length ; i++){
			yr[i] = xr[i];
			yi[i] = 0;
		}
		*/
		//���֐��̓K�p
		window(yr);
		//System.out.println("dt="+dt);
		for(int i=0; i<xr.length; i++){
			yrr=0;
			yii=0;
			for(int j=0; j < xr.length ; j++){
				yrr = yrr+xr[j]*Math.cos(2*Math.PI*(i*df)*(j*dt));
				yii = yii-xr[j]*Math.sin(2*Math.PI*(i*df)*(j*dt));
			}
			yr[i]=yrr;
			yi[i]=yii;
		}
		
	}
	
	/**
	 * ���ԊԈ���FFT 
	 */
	public void fft_time(){		

		//���̓f�[�^���R�s�[(�f�B�[�v�R�s�[)
		for(int i = 0; i < xr.length ; i++){
			yr[i] = xr[i];
			yi[i] = 0;
		}
		
		//���֐��̓K�p
		window(yr);
		
		double xtmpr,xtmpi;
		int jnh,jxC,nHalf,nHalf2;
		int step;
		double arg;
				
		//���ԊԈ����̂��߃f�[�^�𔽓]
		for(int j=0 ; j < n ; j++){
			if(j<brfft[j]){
				double tmp = 0;
				tmp = yr[j];
				
				yr[j] = yr[brfft[j]];
				yr[brfft[j]] = tmp;
				
				tmp = yi[j];
				yi[j] = yi[brfft[j]];
				yi[brfft[j]] = tmp;
			}
		}
		
		nHalf  = 1;
		nHalf2 = 2;
		
		for(step = (n>>1) ; step >= 1; step = (step>>1)){
			
			for(int k = 0; k<n; k= k+nHalf2){
				
				jxC = 0;				
				for(int j = k ; j < (k+nHalf);j++){
					
					jnh = j + nHalf;
					
					xtmpr = yr[jnh];
					xtmpi = yi[jnh];
					
					arg = 2*Math.PI / n;
					
					yr[jnh] = xtmpr*Math.cos(arg*jxC) + xtmpi*Math.sin(arg*jxC); 
					yi[jnh] = xtmpi*Math.cos(arg*jxC) - xtmpr*Math.sin(arg*jxC);

					xtmpr = yr[j];
					xtmpi = yi[j];
					
					yr[j] = xtmpr + yr[jnh];
					yi[j] = xtmpi + yi[jnh];
					
					yr[jnh] = xtmpr - yr[jnh];
					yi[jnh] = xtmpi - yi[jnh];
					
					jxC = jxC + step;
				}				
			}
			nHalf = nHalf << 1;
			nHalf2 = nHalf2 << 1;			
		}				
	}
	
	/**
	 * ���֐��̓K�p
	 */
	public void window(double[] array){
		double weight = 0;
		switch (wndFnc) {		
		case WND_NONE:   //�Ȃ�
			return;						
		case WND_HAMMING://�n�~���O
			for(int i = 0; i < array.length ; i++){
				weight = 0.54 - 0.46 * Math.cos(2*Math.PI*i/(array.length - 1));				
				array[i] = array[i]*weight;			 
			}
			System.out.println("hamming");
			break;
		case WND_BLKMAN: //�u���b�N�}��
			for(int i = 0; i < array.length ; i++){
				weight = 0.42 - 0.5 * Math.cos(2*Math.PI*i/(array.length - 1)) 
				        +0.08 * Math.cos(4*Math.PI*i/(array.length - 1));			
				array[i] = array[i]*weight;			 
			}
			System.out.println("blackman");
			break;
		case WND_HANN:   //�n��
			for(int i = 0; i < array.length ; i++){
				weight = 0.5 - 0.5 * Math.cos(2*Math.PI*i/(array.length - 1));						
				array[i] = array[i]*weight;			 
			}
			System.out.println("hann");
			break;
		default:
			break;
		
		}
	}
	
	/**
	 * ���͐M�����X�V����
	 * @param val �V���ɓ������͐M��
	 */
	public void putInput(double val){
		//�V�t�g
		for(int i = xr.length - 1; i > 0 ;i--){
			xr[i] = xr[i - 1];
		}
		xr[0] = val;
	}
	
	/**
	 * �U���X�y�N�g���̔z��𓾂�
	 * @return �U���X�y�N�g���̔z��
	 */
	public double[] getASpectrum(){
		//FFT
		fft_time();
		
		for(int i = 0; i < as.length ; i++){
			//�U���X�y�N�g���̌v�Z
			as[i] = Math.sqrt(yr[i]*yr[i] + yi[i]*yi[i]);
		}
		
		return as;
	}	
	
	public double[] getDFFTSpectrum(double dt,double df){
		//DFFT
		dfft_time(dt, df);
		
		for(int i = 0; i < as.length ; i++){
			//�U���X�y�N�g���̌v�Z
			as[i] = Math.sqrt(yr[i]*yr[i] + yi[i]*yi[i]);
		}
		
		return as;
	}
	
	/**
	 * FFT�̐��xN��Ԃ�
	 * @return
	 */
	public int getN(){
		return this.n;
	}
	
	/**
	 * ���֐��̐ݒ�
	 * @param wndFnc ���֐��萔
	 */
	public void setWndFnc(int wndFnc){
		this.wndFnc = wndFnc;	
	}

}
