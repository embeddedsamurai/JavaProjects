/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class TitleManager{
	
	/** For Debug */
	public static final boolean DEBUG=true;

	/** Applet �R�s�[�p */
	private Applet  mApplet;

	/** ���j���[�̃C���f�b�N�X���C���N�������g */
	private static final int INCREMENT_MENU_INDEX = 0;
	
	/** ���j���[�̃C���f�b�N�X���f�N�������g */
	private static final int DECREMENT_MENU_INDEX = 1;
	
	private Graphics back;
	private Key key;
	/**
	 * �R���X�g���N�^
	 */
	public TitleManager(FFTGraph applet) {

		this.mApplet = applet;
		this.back    = applet.back;
		this.key     =  applet.key;

	}
	
	/**
	 * ���C������
	 */
	public void process(){
		//�L�[����
		key();
		//���N�G�X�g�̏���
		doRequest();
		//�`��
		draw();		
	}
	
		
	/**
		* �L�[����
		*/
	private void key(){
		if(key.isKeyPressed(Key.KEY_RIGHT)){
			if(DEBUG) System.out.println("�E");
		}else if(key.isKeyPressed(Key.KEY_LEFT)){
			if(DEBUG) System.out.println("��");
		}else if(key.isKeyPressed(Key.KEY_0)){
			if(DEBUG) System.out.println("0");
		}else if(key.isKeyPressed(Key.KEY_ENTER)){
			FFTGraph.setMode(FFTGraph.CALC_MODE);
		}
	}
	
	/**
		* �L���[�̒��̃��N�G�X�g����������
		*/
	private void doRequest(){
	}
	
	
	/**
		* �`�揈��
		*/
	private void draw() {
		
		int   red=0;
		int green=0;
		int  blue=0;
    
		Color color = new Color(255,255,255);
		back.setColor(color);
		back.clearRect(0, 0, FFTGraph.width,FFTGraph.height);
		
		color = new Color((int)red,(int)green,(int)blue);
		back.setColor(color);
		
		Font f0 = FFTGraph.SetFont(back.getFont().getName(),back.getFont().getStyle(), 3.0);
		//�W���T�C�Y�̃t�H���g�𐶐�
		back.setFont(f0);//���݃t�H���g�̐ݒ�
		
		back.drawString("DFT FFT Check Program",FFTGraph.width/2-170,FFTGraph.height/2-40);
		back.drawString("(c)2005-2008 embedded.samurai",FFTGraph.width/2-220,FFTGraph.height/2+40);

	}
	
	
}
