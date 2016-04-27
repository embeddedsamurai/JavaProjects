/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SignalPanel extends GraphPanel{

	/** For Debug */
	public static final boolean DEBUG=true;

	//���͔g�`�̐F
	private static final Color INPUT_COLOR = Color.RED;
	//�o�͔g�`�̐F
	private static final Color OUTPUT_COLOR = Color.BLUE;
	
	//�`��o�b�t�@ 512�|�C���g
	private double[] input  = new double[PNT_WIDTH ];
	private double[] output = new double[PNT_HEIGHT ];
	
	//���g���Ȃǂ�\��	
	private String infoLabel = "";

	MyCanvas mc;

	//�T���v�����O���� 1/250=0.004
	//public double stime=0.004;
	public double alltime=0.004*input.length;
	/**
	  * �R���X�g���N�^
	  */
	public SignalPanel(MyCanvas mc) {
		this.mc=mc;
		alltime=0.004*input.length;
		mc.changeUserWindow(0,alltime,-1,1,1);
		init();
	}	
	
	public void init(){
		//�g�`�`��o�b�t�@������������
		for(int i = 0; i < input.length; i++)input[i] = 0;
		for(int i = 0; i < output.length; i++)output[i] = 0;
		
	}
	
	/**
	 * �`��p���\�b�h
	 */
	public void paintComponent(double dt) {
		//�t�H���g�̐ݒ�
		mc.back.setFont(FONT);
		//�w�i�̕`��
		drawBackground();
		//�g�`�̕`��
		drawWave(dt);
		//���x���̕`��
		drawInfoLabel();
	}	
	
	/**
	 * �w�i��`��
	 * @param g Graphics�I�u�W�F�N�g
	 */
	private void drawBackground(){
		
		double[]  bminx={0},bmaxx={0},bminy={0},bmaxy={0};
		mc.getUserWindow(bminx,bmaxx,bminy,bmaxy,1);
		
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
		mc.fillRect(minx,miny,maxx,maxy, 1);
		
		//draw ground line
		mc.back.setColor(BG_LINE_COLOR);
		mc.drawLine(minx,0,maxx,0,1);
	
		mc.back.setFont(BG_FONT);
		
		
		//�ڐ���� ��
		int j=0;
		for(double i = minx; i < maxx; i += userTickX){
			
			if(j == 5){
				double k=((int)(i*100))/100.;
				mc.back.setColor(BG_STR_COLOR);
				mc.drawLine(i, userMemoriY, i, -userMemoriY,1);
				mc.drawString(k + "", i, -userMemoriY*4,1);
				mc.back.setColor(BG_LINE_COLOR);
				j=0;
			}else{
				mc.drawLine(i, userMemoriY, i, -userMemoriY,1);	
			}
			j++;
		}
		
		//�ڐ���� �c	
		j=0;
		for(double i = miny*10; i <= maxy*10 ; i += userTickY*10){
			
			double ii=i/10;
			if(j == 5){
				double k=((int)(ii*100))/100.;
				mc.back.setColor(BG_STR_COLOR);
				mc.drawLine(0,ii,userMemoriX,ii,1);				
				mc.drawString( k +"", userMemoriX, ii,1);
				mc.back.setColor(BG_LINE_COLOR);
				j=0;
			}else{				
				mc.drawLine(0,ii,userMemoriX,ii,1);	
			}
			j++;
		}
	}
	
	/**
	 * ���͐M����`�悷��
	 * @param val
	 */
	public void putInput(double val){	
		for(int i = 0; i < input.length-1 ; i++) input[i] = input[i+1];

		//set new value
		input[input.length-1] = val;
	}
	
	
	/**
	 * �o�͐M����`�悷��
	 * @param val
	 */
	public void putOutput(double val){
		for(int i = 0; i < output.length - 1 ; i++) output[i] = output[i+1];
	
		output[input.length-1] = val;
	}
	
	
	
	/**
	 * �g�`��`��
	 * @param g Graphics�I�u�W�F�N�g
	 */
	private void drawWave(double dt){
		
		mc.back.setColor(INPUT_COLOR);
		//���͐M����`��
		for(int i = 0; i < input.length - 1 ; i++){
			mc.drawLine(i*dt, input[i],(i + 1)*dt, input[i + 1],1);
		}		
		
		mc.back.setColor(OUTPUT_COLOR);
		//�o�͐M����`��
		for(int i = 0; i < output.length - 1; i++){
			mc.drawLine(i*dt, output[i],(i + 1)*dt,output[i + 1],1);
		}
	}
	
	/**
	 * ���g���Ȃǂ̏���`�� 
	 * @param g Graphics�I�u�W�F�N�g
	 */
	private void drawInfoLabel(){
		mc.back.setFont(FONT);
		mc.back.setColor(BG_STR_COLOR);
		
		mc.drawString(infoLabel,1,0.8,1);
	}
	
	/**
	 * ���g���Ȃǂ̏���ύX 
	 * @param sample �T���v�����O���[�g
	 * @param freq�@�@�@���g��
	 */
	public void setInfoLabel(double freq,double sample){
		this.infoLabel = "Sample:" + sample + "Hz" + " Freq:" + freq + "Hz";
	}		
	
	/**
	 * X�����̊g��k������ύX
	 * @param value
	 */
	public void setXGain(int value){
		
	}
}
