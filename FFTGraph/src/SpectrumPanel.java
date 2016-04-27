/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class SpectrumPanel extends GraphPanel{
	
	//�i�C�L�X�g���g��
	public double nyquist;
	//�`��p�X�y�N�g���̃o�b�t�@
	private int[] drawBuffer = new int[PNT_WIDTH];
	
	//�`��p�X�y�N�g���̃o�b�t�@
	private int[] drawBuffer2 = new int[PNT_WIDTH];
	
	//�U���X�y�N�g���̐F
	private static final Color WAVE_COLOR = Color.MAGENTA;
	//�p�l���ɕ\��������
	private String infoLabel ="";
	
	Graphics g;
	MyCanvas mc;


	/**
	 * �R���X�g���N�^
	 * @param sample �T���v�����O���[�g
	 */
	public SpectrumPanel(MyCanvas mc,double sample) {
		this.mc = mc;
		this.nyquist = sample /2.;
		
		mc.changeUserWindow(0,nyquist,-10,PNT_HEIGHT,2);
		init();
	}
	
	/**
	 * ������
	 */
	public void init(){
		for(int i = 0; i < drawBuffer.length ; i++){
			drawBuffer[i] = 0;
			drawBuffer2[i] = 0;
		}		
	}
	

	/**
	 * �`�惁�\�b�h
	 * @param g Graphics�I�u�W�F�N�g
	 */
	public void paintComponent(double df) {
		drawBackground();
		drawWave(df);
		drawLabel();
	}
	
	/**
	 * �w�i��`�悷��
	 * @param g Graphics�I�u�W�F�N�g
	 */
	private void drawBackground(){
		
		double[]  bminx={0},bmaxx={0},bminy={0},bmaxy={0};
		mc.getUserWindow(bminx,bmaxx,bminy,bmaxy,2);
		
		//if(DEBUG2) System.out.println("2 minx"+minx[0]+"maxx"+maxx[0]+"miny"+miny[0]+"maxy"+maxy[0]);
		double minx=bminx[0];
		double maxx=bmaxx[0];
		double miny=bminy[0];
		double maxy=bmaxy[0];
		//System.out.println("2 minx"+minx+"maxx"+maxx+"miny"+miny+"maxy"+maxy);
		
		double userXLength= maxx - minx;
		double userYLength= maxy - miny;

		double userTickX = userXLength / 50.;
		double userTickY = userYLength / 50.;
		
		double userMemoriX = userXLength / 100.;
		double userMemoriY = userYLength / 100.;
		
		//fill background
		mc.back.setColor(BG_COLOR);				
		mc.fillRect(minx,miny,maxx,maxy, 2);
		
		//draw ground line
		mc.back.setColor(BG_LINE_COLOR);
		mc.drawLine(minx,0,maxx,0,2);
	
		mc.back.setFont(BG_FONT);
		
		
		//�ڐ���� ��
		int j=0;
		for(double i = minx; i < maxx; i += userTickX){
			
			if(j == 5){
				double k=((int)(i*10))/10.;
				mc.back.setColor(BG_STR_COLOR);
				mc.drawLine(i, -userMemoriY, i,userMemoriY,2);
				mc.drawString(k + "", i, -userMemoriY*4,2);
				mc.back.setColor(BG_LINE_COLOR);
				j=0;
			}else{
				mc.drawLine(i, -userMemoriY, i, userMemoriY,2);	
			}
			j++;
		}
		
		//�ڐ���� �c	
		j=0;
		for(double i = miny*10; i <= maxy*10 ; i += userTickY*10){
			
			double ii=i/10;
			if(j == 5){
				double k=((int)(ii*10))/10.;
				mc.back.setColor(BG_STR_COLOR);
				mc.drawLine(0,ii,userMemoriX,ii,2);				
				mc.drawString( k +"", userMemoriX, ii,2);
				mc.back.setColor(BG_LINE_COLOR);
				j=0;
			}else{				
				mc.drawLine(0,ii,userMemoriX,ii,2);	
			}
			j++;
		}
	}
	
	/**
	 * �g�`��`�悷��
	 * @param g
	 */
	private void drawWave(double df){
		
		
		//�X�y�N�g����`��	
		//System.out.printf("dt="+df);
		for(int i = 0; i < drawBuffer.length - 1 ; i++){
			if(FilterManager.fftmode == FilterManager.SET_DFT){
				mc.back.setColor(Color.GREEN);
				mc.drawLine(i*df, drawBuffer2[i],(i + 1)*df, drawBuffer2[i + 1],2);
			}else if(FilterManager.fftmode == FilterManager.SET_FFT){
				mc.back.setColor(WAVE_COLOR);
				mc.drawLine(i*df, drawBuffer[i],(i + 1)*df, drawBuffer[i + 1],2);
			}else{
				mc.back.setColor(Color.GREEN);
				mc.drawLine(i*df, drawBuffer2[i],(i + 1)*df, drawBuffer2[i + 1],2);
				mc.back.setColor(WAVE_COLOR);
				mc.drawLine(i*df, drawBuffer[i],(i + 1)*df, drawBuffer[i + 1],2);
			}
		}	
	}
	
	/**
	 * ���x���̕`��
	 * @param g Graphics�I�u�W�F�N�g
	 */
	private void drawLabel(){
		mc.back.setFont(FONT);
		mc.back.setColor(BG_STR_COLOR);
		mc.drawString(infoLabel,100,200,2);
	}
	
	/**
	 * �X�y�N�g�����X�V
	 * @param val
	 */
	public void setSpectrum(double val[]){		
			
		double max = val[0];
		
		//X�������̃}�b�s���O
		for(int i = 0; i < drawBuffer.length ; i++){
			drawBuffer[i] = (int)val[i];
			if(max < drawBuffer[i])max = drawBuffer[i];
		}
		
		//Max��Panel�̍����Ƃ��Đ��K��
		double ygain = (double)PNT_HEIGHT /(double) max;
		
		for(int i = 0; i < drawBuffer.length ; i++){
			drawBuffer[i] = (int)(ygain*drawBuffer[i]);
		}
	}
	
	public void setSpectrum2(double val[]){		
		
		double max = val[0];
		
		//X�������̃}�b�s���O
		for(int i = 0; i < drawBuffer2.length ; i++){
			drawBuffer2[i] = (int)val[i];
			if(max < drawBuffer2[i])max = drawBuffer2[i];
		}
		
		//Max��Panel�̍����Ƃ��Đ��K��
		double ygain = (double)PNT_HEIGHT /(double) max;
		
		for(int i = 0; i < drawBuffer2.length ; i++){
			drawBuffer2[i] = (int)(ygain*drawBuffer2[i]);
		}
	}
	
	/**
	 * ���xN�̕\��
	 * @param n
	 */
	public void setInfoLabel(double n){
		this.infoLabel ="N:" + n;			
	}
	
 
}
