/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.applet.*;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;


public class FilterManager  implements Runnable{
	
	/** For Debug */
	public static final boolean DEBUG=false;

	/** Applet �R�s�[�p */
	private Applet  mApplet;
	
	/** �����g�Ȃǂ̃O���t��\�����邽�߂̃p�l�� */
	SignalPanel panel;

	/** �X�y�N�g���p�̃p�l�� */
	SpectrumPanel spectrum;
	
	/** Graphics */
	public Graphics back;
	
	/** Key */
	public Key key;
		
	/** �f�[�^�� */
	public int Num=512;
	/** �����g�`�̎��g�� */
	private double freq = 5;
	
	/** �T���v�����O���[�g���g�� */
	private double sample = 250;
	
	/** �T���v�����O���� */
	public double dt= 1./sample;
	
	/** ��{���g�� */
	public double df=dt/(double)Num;
	/** ���͐M�������N���X */
	private WaveGen waveGen;

	/** �g�`��i�߂邽�߂̃t���O */
	private static boolean waveFlag=false;

	public static boolean runFlag;
	
	public static final int SLEEP_TIME=500;

	MyFFT fft;
	
	public static int fftmode;
	
	Thread thread;
	public MyCanvas mc;
	
	/**
	 * �R���X�g���N�^
	 */
	public FilterManager(FFTGraph applet) {

		fftmode=SET_DFT;
		
		this.mApplet = applet;
		this.back    = applet.back;
		this.key     = applet.key;
		this.mc      = applet.mc;
		
		//dt
		dt= 1./sample;
		df= sample/(double)Num;
		
		//�T�C�������N���X(�����ł̓T�C���g)
		waveGen = new SinGen(freq,sample);    	
		//�T�C���g�����p�ϐ��̏���������
		waveGen.init(freq, sample);
				
		//�p�l���̐ݒ�
		panel=new SignalPanel(this.mc);
		panel.setInfoLabel(freq,sample);

		//�X�y�N�g���\���p�p�l���̍쐬
		spectrum = new SpectrumPanel(this.mc,this.sample);
		spectrum.setInfoLabel(512);


		waveFlag=false;
		
		//FFT�v�Z�N���X
		fft = new MyFFT();
		fftstart();

	}
	
	/**
		* ���s�̊J�n
		*/
	public void fftstart(){
		//����������
		//init();
		//���s�t���O�𗧂Ă�
		runFlag = true;
		//�X���b�h�̋N��
		thread = new Thread(this);
		//���s�̊J�n
		thread.start();
	}
	
	public void run(){
		try{
			//�X���[�v����
			long startTime = System.currentTimeMillis();
			//�v�Z�ɂ�����������
			long pastTime = 0;

			while(runFlag){

				startTime = System.currentTimeMillis();

				//�����Ńt�[���G�ϊ�����
				if(fftmode==SET_FFT){
					
					spectrum.setSpectrum(fft.getASpectrum());
				}else if(fftmode==SET_DFT){
					//�����ŗ��U�t�[���G�ϊ�����
					
					spectrum.setSpectrum2(fft.getDFFTSpectrum(this.dt,this.df));
				}else if(fftmode==SET_ALLFT){
					
					spectrum.setSpectrum(fft.getASpectrum());
					spectrum.setSpectrum2(fft.getDFFTSpectrum(this.dt,this.df));
				}
				//�v�Z�ɂ�����������
				pastTime = System.currentTimeMillis() - startTime;

				if(pastTime < SLEEP_TIME){
					//�x�~
					pause(SLEEP_TIME+5 - pastTime);
				}

			}//end of while(1)
		}catch(Exception e){
			e.printStackTrace();
		}

	}//end of run

	/**
	 * �X���b�h�̋x�~
	 */
	public void pause(long time){
		try {
			//�X���[�v
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(){
		waveFlag=true;
	}
	
	public void stop(){
		waveFlag=false;
	}

	/**
	 * ���C������
	 */
	public void process(){
		//�L�[����
		key();
		//���N�G�X�g�̏���
		if(waveFlag) newDataInput();
		//�`��
		draw();
	}
	
		
	/**
		* �L�[����
		*/
	private void key(){
	}
	
	/**
		* �L���[�̒��̃��N�G�X�g����������
		*/
	private void newDataInput(){
		
		//�V���ȃf�[�^��������
		//�T���v�����O���[�g250Hz�̏ꍇ��1�̃f�[�^��0.004�b���Ƃɒu�����
		double data = waveGen.nextWave();
		panel.putInput(data);
		//FFT���̓o�b�t�@�̍X�V
		fft.putInput(data);
	}

	
	/**
		* �`�揈��
		*/
	private void draw() {
		panel.paintComponent(this.dt);
		spectrum.paintComponent(this.df);
	}

	
	/**
		* panel��x Gain�̕ύX
		* @param value
		*/
	public void setXGain(int value){
		if(panel!=null) panel.setXGain(value);
		System.out.println("alltime"+panel.alltime);
		mc.changeUserWindow(0,panel.alltime-(value*0.01),-1,1,1);
		//mc.changeUserWindow(0,spectrum.nyquist-value,-10,200,2);
	}

	public void setXFFTGain(int value){
		//mc.changeUserWindow(0,panel.alltime-(panel.stime*value*3),-1,1,1);
		mc.changeUserWindow(0,spectrum.nyquist-value,-10,200,2);
	}
	
	//���֐��Ȃ�
	public static final int SET_FFT = 0;
	//�n�~���O
	public static final int SET_DFT  = 1;
	//�u���b�N�}��
	public static final int SET_ALLFT  = 2;
	
	public void setFFTMode(int value){
		fftmode = value;
	}
	/**
	 * ��������M���̎��g���̕ύX
	 * @param value
	 */
	public void setFrequency(double freq){
		this.freq = freq;
		//this.sample = sample;
		waveGen.init(this.freq, this.sample);
		panel.setInfoLabel(this.freq,this.sample);
	}

	public void setSampleRate(double sample){
		//this.freq = freq;
		this.sample = sample;
		this.dt = 1/sample;
		this.df = sample/(double)Num;
		waveGen.init(this.freq, this.sample);
		panel.setInfoLabel(freq,sample);
	}
	/**
	 * ��������M���̎��g�����擾
	 * @return
	 */
	public double getFrequency(){
		return this.freq;
	}

	/**
	 * �T���v�����O���g�����擾
	 * @return
	 */
	public double getSampleRate(){
		return this.sample;
	}

	/**
		* ��������M���̎�ނ�ύX
		* @param gen
		*/
	//�T�C���g
	public static final int INPUT_SIN = 0;
	//��`�g
	public static final int INPUT_REC = 1;
	//�̂�����g
	public static final int INPUT_SAW = 2;
	//�O�p�g
	public static final int INPUT_TRI = 3;
	//�S�d�}
	public static final int INPUT_ECG = 4;
	//���g
	public static final int INPUT_PLS = 5;

	//���͐M��
	//private int inputWave = INPUT_SIN;

	//MainGraph.java��itemStateChanged����Ă΂��B
	public void setWaveGen(int gen){
		switch (gen) {
		case INPUT_SIN://�T�C���g
			waveGen = new SinGen(freq,sample);
			break;
		case INPUT_REC://��`�g
			waveGen = new RecGen(freq,sample);
			break;
		case INPUT_SAW://�̂�����g
			waveGen = new SawtoothGen(freq,sample);
			break;
		case INPUT_TRI://�O�p�g
			waveGen = new TriangleGen(freq,sample);
			break;
		case INPUT_ECG://ECG
			waveGen = new ECG(mApplet.getCodeBase().toString() + "ecg1.txt");
			break;
		case INPUT_PLS://���g
			waveGen = new PLS(mApplet.getCodeBase().toString() + "pls1.txt");
		default:
			break;
		}
	}
	
	//���֐��Ȃ�
	public static final int WND_NONE = 0;
	//�n�~���O
	public static final int WND_HAMMING  = 1;
	//�u���b�N�}��
	public static final int WND_BLKMAN  = 2;
	//�n��
	public static final int WND_HANN = 3;	
	
	public void setWndFuc(int wndFnc){
		fft.setWndFnc(wndFnc);
	}
	
	

}
