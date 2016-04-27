/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/*
    <applet code="MainGraph" width=600 height=700>
    <param name="FRAMERATE" value="10">
    <param name="BGCOLOR" value="FFFFFF">
    </applet>
*/

public class FFTGraph extends Applet implements Runnable,MouseMotionListener,MouseListener,KeyListener,ActionListener,ItemListener,AdjustmentListener
{
	/** For Debug */
	public static final boolean DEBUG=false;
	
	/** ���[�v���ł̃X���[�v����*/
	private static final int SLEEP_TIME = 20;
	
	private static int sleepTime=SLEEP_TIME;
	/** �^�C�g�����[�h */
	public static final int TITLE_MODE = 0;
	/** �v�Z���[�h   */
	public static final int CALC_MODE = 1;
	/** �I�����[�h   */
	public static final int EXIT_MODE = 2;
	
	/** ��ʂ̃T�C�Y */
	public static final int SCREEN_W=600,SCREEN_H=700;

	/** �t���[�� */
	public static final int FRAME=10;
	
	/** main thread */
	private Thread  thread = null;   //�X���b�h
	
	/** for double buffering */
	public Image    offimage;       //�_�u���o�b�t�@�����O�p�̃C���[�W
	public Graphics back;           //�_�u���o�b�t�@�����O�p�̃O���t�B�b�N�X(�o�b�N�o�b�t�@)

	private Color backColor       = Color.white;
	public static int width       = SCREEN_W;
	public static int height      = SCREEN_H;
	private static int framerate  = FRAME;
	
	public Key key;
	public TitleManager titleManager;
	public FilterManager filterManager;
	public MyCanvas                 mc;//MyCanvas�p�̃f�[�^
	
		/** ��� */
	public static int mode_state;
	/** �ЂƂO�̏�� */
	public int back_mode_state;

	/** Button */
	Button btn1,btn2;	
	
	/** Scroll Bar */
	Scrollbar freqSlider,xGainSlider,xFFTGainSlider,speedSlider;
	
	/** Scroll Bar */
	Scrollbar sampleSlider;
	
	/** ���W�I�{�^��(���֐�) */
	CheckboxGroup wndFncBtn = new CheckboxGroup();
	Checkbox chk_none,chk_hamming,chk_hann,chk_blackman;

	/** ���W�I�{�^��(�����M��) */
	CheckboxGroup signalBtn = new CheckboxGroup();
	Checkbox chk_Sine,chk_Rect,chk_Sawtooth,chk_Triangle,chk_ECG,chk_PLS;
	
	/** ���W�I�{�^��(�`�����) */
	CheckboxGroup displayBtn = new CheckboxGroup();
	Checkbox chk_leftRight,chk_RightLeft;

	/** ���W�I�{�^��(���g���ϊ�) */
	CheckboxGroup FreqBtn = new CheckboxGroup();
	Checkbox chk_DFT,chk_FFT,chk_DWVD,chk_SFFT,chk_Wavelet,chk_DWavelet,chk_ALLFT;
	//�T�C���g
	public static final int INPUT_SIN = 0;
	
	//----------------------------------------------------------
	//�A�v���b�g�̂��񑩏���
	//
	//1 init
	//2 start
	//3 update
	//4 paint
	//5 stop
	//----------------------------------------------------------
	public void init()
	{
		/** �o�b�N�O���E���h�̐ݒ� */
		String backs=getParameter("BGCOLOR");
		Color c;
		
		if(backs != null && (c = stringToColor(backs)) != null)
			backColor = c;
		
		setBackground(backColor);
		
		/** �}�E�X�̐ݒ� */
		addMouseMotionListener(this);
		addMouseListener(this);
		
		/** �L�[�̐ݒ� */
		addKeyListener(this);
		this.width = this.getSize().width;
		this.height = this.getSize().height;

		if (offimage == null) {
			offimage  = createImage(width, height);
			back      = offimage.getGraphics();
		}
		
		key = new Key();
		mc  = new MyCanvas(this);
		//minx maxx miny maxy
		mc.setViewport(0, 1, 0.6, 1);
		mc.setViewport(0, 1, 0.2, 0.6);
		mc.setViewport(0, 1,   0, 0.2);
		
		//���[�U���W�n�̐ݒ� 
		//minx maxx miny maxy
		mc.setUserWindow(0,200,0,100);
		mc.setUserWindow(0,200,0,100);
		mc.setUserWindow(0,100,0,20);
		
		titleManager  = new TitleManager(this);
		filterManager = new FilterManager(this);
		
		
		//�ŏ��̓^�C�g����\��
		mode_state = TITLE_MODE;
		back_mode_state = mode_state;
		
	}

	public void start()
	{
		thread = new Thread(this);
		if (thread!=null){
			 thread.start();
		}
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(offimage,0,0,this);
	}
	
	public void update(Graphics g)
	{	
		paint(g);
	}


	public void run(){
		
		// �L�[�̏�����
		key.init();
		//�X���[�v����
		long startTime = System.currentTimeMillis();
		//�v�Z�ɂ�����������
		long pastTime = 0;
				
		while (true){
			//�L�[���X�V
			key.registKeyEvent();					
					
			//�L�[�A�v�Z�A�`��̊e�폈��
			if(mode_state == TITLE_MODE){//�^�C�g�����[�h
				titleManager.process();
			}else if(mode_state == CALC_MODE){//�v�Z���[�h
				filterManager.process();
			}	

			if(back_mode_state != mode_state){//���[�h�̕ύX���Ȃ����ǂ����`�F�b�N�B
				//���̏�Ԃ�����Ă���
				Color color = new Color(255,255,255);
				back.setColor(color);
				back.fillRect(0, 0, width,height);
				Font f0 = SetFont(back.getFont().getName(),back.getFont().getStyle(), 1.0);
				//�W���T�C�Y�̃t�H���g�𐶐�
				back.setFont(f0);//���݃t�H���g�̐ݒ�
				back.setColor(Color.black);
				
				if(mode_state == CALC_MODE ){
					SetCalcWindow();
				}
				back_mode_state = mode_state;
			}
			
			// repaint()��paint(g)�̌Ăяo��,���߂ĕ`�悪�X�V�����
			repaint();
				
			//�v�Z�ɂ�����������
			pastTime = System.currentTimeMillis() - startTime;					
					
			if(pastTime < sleepTime){
				//�x�~
				pause(sleepTime+5 - pastTime);
			}
			
			startTime = System.currentTimeMillis();
		}
	}

	public void SetCalcWindow(){
		
		//x 0 100 y 0 10�͈̔�
		double[]  bminx={40},bmaxx={50},bminy={2.5},bmaxy={6};
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		//�{�^���֌W����
		btn1=new Button(" Start ");
		btn2=new Button(" Stop ");
		add(btn1);
		add(btn2);
		btn1.setBounds((int)bminx[0],(int)bminy[0],(int)(bmaxx[0]-bminx[0]),(int)(bminy[0]-bmaxy[0]));
		
		System.out.println("2 minx"+bminx[0]+"maxx"+bmaxx[0]+"miny"+bminy[0]+"maxy"+bmaxy[0]);
		
		bminx[0]=50;
		bmaxx[0]=60;
		bminy[0]=2.5;
		bmaxy[0]=6.;
		
		
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		
		btn2.setBounds((int)bminx[0],(int)bminy[0],(int)(bmaxx[0]-bminx[0]),(int)(bminy[0]-bmaxy[0]));
		btn1.addActionListener(this);
		btn2.addActionListener(this);
					
					
		// Scroll Bar
		
		bminx[0]=10;
		bmaxx[0]=12;
		
		bminy[0]=18;
		bmaxy[0]=20;
		
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		int mado_x=0;
		int mado_y=16;
		
		/////////////////////////////////////////////////
		mc.drawString("���S�̐ݒ�",0,16,3);
		
		mc.drawString("�����Ԃ͈̔�",0,14,3);
		//�w�肳�ꂽ�����A�����l0�A����10�A�ŏ��l1�A�ő�l100�̃X�N���[���o�[�����
		xGainSlider = new Scrollbar(Scrollbar.HORIZONTAL,0,10,1,110);
		xGainSlider.setBackground(Color.gray);
		//����200 ��20�̃o�[�����
		bminx[0]=13;
		bminy[0]=16;
		bmaxx[0]=0;
		bmaxy[0]=0;
		
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		xGainSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		xGainSlider.addAdjustmentListener(this);
		add(xGainSlider);
		
        ////////////////////////////////////////////////////////
		mc.drawString("�����g���͈̔�",30,14,3);
		//�w�肳�ꂽ�����A�����l0�A����10�A�ŏ��l1�A�ő�l100�̃X�N���[���o�[�����
		xFFTGainSlider = new Scrollbar(Scrollbar.HORIZONTAL,0,10,1,110);
		xFFTGainSlider.setBackground(Color.gray);
		//����200 ��20�̃o�[�����
		bminx[0]=30+15;
		bminy[0]=16;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		xFFTGainSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		xFFTGainSlider.addAdjustmentListener(this);
		add(xFFTGainSlider);
		////////////////////////////////////////////////////////////
		mc.drawString("���T���v�����O���g��",62,14,3);
		//�w�肳�ꂽ�����A�����l0�A����10�A�ŏ��l1�A�ő�l100�̃X�N���[���o�[�����
		sampleSlider = new Scrollbar(Scrollbar.HORIZONTAL,250,10,100,1000);
		sampleSlider.setBackground(Color.gray);
		//����200 ��20�̃o�[�����
		bminx[0]=82;
		bminy[0]=16;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		sampleSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		sampleSlider.addAdjustmentListener(this);
		add(sampleSlider);
		
		////////////////////////////////////////////////////
		////////////////////////////////////////////////////
		mc.drawString("�����͔g�`",0,12,3);
		mc.drawString("�����g���̐ݒ�",0,10,3);
		//�w�肳�ꂽ�����A�����l5�A����10�A�ŏ��l1�A�ő�l100�̃X�N���[���o�[�����
		freqSlider = new Scrollbar(Scrollbar.HORIZONTAL,5,10,1,110);
		freqSlider.setBackground(Color.gray);
		//����200 ��20�̃o�[�����
		bminx[0]=13;
		bminy[0]=12;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		freqSlider.setBounds((int)(bminx[0]+10),(int)bminy[0],100,20);
		freqSlider.addAdjustmentListener(this);
		add(freqSlider);
		//
		
		mc.drawString("�����͔g�`�̑I��",0,7,3);
					
		mado_y =9;
		mado_x =17;
		bminx[0]=mado_x;
		bminy[0]=mado_y;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		
		mado_x = (int)bminx[0];
		mado_y = (int)bminy[0];
		
		chk_Sine = new Checkbox("Sine",signalBtn,true);
		chk_Sine.setBounds(new Rectangle((int)bminx[0], (int)bminy[0], 50, 20));
		chk_Sine.addItemListener(this);
					
		mado_x = mado_x + 50;
		chk_Rect = new Checkbox("Rect",signalBtn,false);
		chk_Rect.setBounds(new Rectangle(mado_x, mado_y, 50, 20));
		chk_Rect.addItemListener(this);
					
		mado_x = mado_x + 50;
		chk_Sawtooth = new Checkbox("Sawtooth",signalBtn,false);
		chk_Sawtooth.setBounds(new Rectangle(mado_x, mado_y, 80, 20));
		chk_Sawtooth.addItemListener(this);
					
		mado_x = mado_x + 80;
		chk_Triangle = new Checkbox("Triangle",signalBtn,false);
		chk_Triangle.setBounds(new Rectangle(mado_x, mado_y, 70, 20));
		chk_Triangle.addItemListener(this);
					
		mado_x = mado_x + 70;
		chk_ECG = new Checkbox("ECG",signalBtn,false);
		chk_ECG.setBounds(new Rectangle(mado_x, mado_y, 50, 20));
		chk_ECG.addItemListener(this);

		mado_x = mado_x + 50;
		chk_PLS = new Checkbox("PLS",signalBtn,false);
		chk_PLS.setBounds(new Rectangle(mado_x, mado_y, 50, 20));
		chk_PLS.addItemListener(this);

		this.add(chk_Sine, null);
		this.add(chk_Rect, null);
		this.add(chk_Sawtooth, null);
		this.add(chk_Triangle, null);	
		this.add(chk_ECG, null);	
		this.add(chk_PLS, null);	
		
		
		
		////////////////////////////////////////////////////////////////
	
		mc.drawString("�����g���ϊ�",0,4.5,3);
		
		mado_y =4;
		mado_x =0;
		bminx[0]=mado_x;
		bminy[0]=mado_y;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		
		mado_x = (int)bminx[0];
		mado_y = (int)bminy[0];
		
		
		chk_DFT = new Checkbox("���U�t�[���G",FreqBtn,true);
		chk_DFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_DFT.addItemListener(this);
					
		mado_x = mado_x + 100;
		chk_FFT = new Checkbox("�����t�[���G",FreqBtn,false);
		chk_FFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_FFT.addItemListener(this);
		
		mado_x = mado_x + 100;
		chk_ALLFT = new Checkbox("�����\��",FreqBtn,false);
		chk_ALLFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_ALLFT.addItemListener(this);
		
		this.add(chk_DFT, null);
		this.add(chk_FFT, null);
		this.add(chk_ALLFT,null);
		///////////////////////////////////////////////////////
		//CheckboxGroup wndFncBtn = new CheckboxGroup();
		//Checkbox chk_none,chk_hamming,chk_hann,chk_blackman;
		//���W�I�{�^��(���֐�)
		mc.drawString("�����֐�",50,4.5,3);
		
		mado_y =4;
		mado_x =50;
		bminx[0]=mado_x;
		bminy[0]=mado_y;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		
		mado_x = (int)bminx[0];
		mado_y = (int)bminy[0];
		
		chk_none = new Checkbox("none",wndFncBtn,true);
		chk_none.setBounds(new Rectangle(mado_x, mado_y, 50, 20));
		chk_none.addItemListener(this);
					
		mado_x = mado_x + 50;
		chk_hamming = new Checkbox("hamming",wndFncBtn,false);
		chk_hamming.setBounds(new Rectangle(mado_x, mado_y, 80, 20));
		chk_hamming.addItemListener(this);
					
		mado_x = mado_x + 80;
		chk_hann = new Checkbox("hann",wndFncBtn,false);
		chk_hann.setBounds(new Rectangle(mado_x, mado_y, 50, 20));
		chk_hann.addItemListener(this);
					
		mado_x = mado_x + 50;
		chk_blackman = new Checkbox("blackman",wndFncBtn,false);
		chk_blackman.setBounds(new Rectangle(mado_x, mado_y, 80, 20));
		chk_blackman.addItemListener(this);
					
		this.add(chk_none, null);
		this.add(chk_hamming, null);
		this.add(chk_hann, null);
		this.add(chk_blackman, null);
		
	}
	
	
	public void stop(){
		if (thread!=null){
			extracted();
			thread = null;
		}
	}

	private void extracted() {
		thread.stop();
	}
	
	
	/**
	 * ���[�h��ύX����
	 * ���ۂɔ��f�����̂́A���[�v���ł̎��̏�Ԃ̃`�F�b�N��
	 * 
	 * @param mode
	 */
	public static void setMode(int mode){
		mode_state = mode;
	}
	
	
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

	
	/** 
		"rrggbb"�`���̕������Color�I�u�W�F�N�g�ɕϊ� 
	*/
	private static Color stringToColor(String paramValue)
	{
		int red, green, blue;
		
		try{
			red   = (Integer.decode("0x" + paramValue.substring(0,2))).intValue();
			green = (Integer.decode("0x" + paramValue.substring(2,4))).intValue();
			blue  = (Integer.decode("0x" + paramValue.substring(4,6))).intValue();
			return new Color(red,green,blue);
		}catch(Exception e){
			return null;
		}
		
	}
	
	public void mouseDragged(MouseEvent e)
	{ 
		// �}�E�X���h���b�O���ꂽ
		Key.mouseX = e.getX();
		Key.mouseY = e.getY();
	}

	public void mousePressed(MouseEvent e)
	{ 
		// �}�E�X�{�^���̍��������ꂽ
		if(e.getModifiers() == 16)	//���N���b�N
		{
			Key.isPressMouse = true;
			mouseDragged(e);
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{ 
		// �}�E�X�{�^���������ꂽ
		Key.isPressMouse = false;
	}
	
	
	//�g�p���Ȃ����A�L�q���Ă����Ȃ��ƃG���[
	public void mouseMoved(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	
	public void keyPressed(KeyEvent e){
		int param = e.getKeyCode();

		if(48 <= param && param <=57){
			param=param-48;
			Key.keyFlag[0] |= (1L << param);
			Key.keyFlag[2] |= (1L << param);
			if(DEBUG) System.out.println("param="+param);
		}else if(37 <= param && param <= 40){
			param=param-21;
			Key.keyFlag[0] |= (1L << param);
			Key.keyFlag[2] |= (1L << param);
			if(DEBUG) System.out.println("param="+param);
		}
		
		
		//space�������ꂽ
		if(param==32){
			param=Key.KEY_SPACE;
			Key.keyFlag[0] |= (1L << param);
			Key.keyFlag[2] |= (1L << param);
		}
		
		//enter�������ꂽ
		if(param==10){
			param=Key.KEY_ENTER;
			Key.keyFlag[0] |= (1L << param);
			Key.keyFlag[2] |= (1L << param);
		}
		
	}
	public void keyReleased(KeyEvent e){
		int param = e.getKeyCode();
		if(48 <= param && param <=57){
			param=param-48;
			Key.keyFlag[2] &= ~(1L << param);
		}else if(37 <= param && param <= 40){
			param=param-21;
			Key.keyFlag[2] &= ~(1L << param);
		}
		
		//space���͂Ȃꂽ
		if(param==32){
			param=Key.KEY_SPACE;
			Key.keyFlag[2] &= ~(1L << param);
		}
		
		//enter���͂Ȃꂽ
		if(param==10){
			param=Key.KEY_ENTER;
			Key.keyFlag[2] &= ~(1L << param);
		}
		
	}
	public void keyTyped(KeyEvent e){}
	
	/** 
		* ------------------ �{�^���֌W���\�b�h ------------------------ */
	//�e�L�X�g�G���A�ɐ��l���L�����ꂽ
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==btn1){ //calc start
			btn1.setBackground(new Color(255,220,150));
			btn2.setBackground(new Color(215,215,195));
			filterManager.start();
		}
		if(e.getSource()==btn2){ //calc reset
			btn2.setBackground(new Color(255,220,150));
			btn1.setBackground(new Color(215,215,195));
			filterManager.stop();
		}
	}

	/** 
		* ------------------ Check�{�^���֌W���\�b�h ------------------------ */
	public void itemStateChanged(ItemEvent e){
		if(e.getItemSelectable()==chk_Sine){
			if(DEBUG) System.out.println("sine");
			filterManager.setWaveGen(FilterManager.INPUT_SIN);
		}else if(e.getItemSelectable()==chk_Rect){
			if(DEBUG) System.out.println("Rect");
			filterManager.setWaveGen(FilterManager.INPUT_REC);
		}else if(e.getItemSelectable()==chk_Sawtooth){
			if(DEBUG) System.out.println("Saw");
			filterManager.setWaveGen(FilterManager.INPUT_SAW);
		}else if(e.getItemSelectable()==chk_Triangle){
			if(DEBUG) System.out.println("Triangle");
			filterManager.setWaveGen(FilterManager.INPUT_TRI);
		}else if(e.getItemSelectable()==chk_ECG){
			if(DEBUG) System.out.println("ECG");
			filterManager.setWaveGen(FilterManager.INPUT_ECG);
		}else if(e.getItemSelectable()==chk_PLS){
			if(DEBUG) System.out.println("PLS");
			filterManager.setWaveGen(FilterManager.INPUT_PLS);
		}
		
		if(e.getItemSelectable()==chk_none){
			filterManager.setWndFuc(FilterManager.WND_NONE);
		}else if(e.getItemSelectable()==chk_hamming){
			filterManager.setWndFuc(FilterManager.WND_HAMMING);
		}else if(e.getItemSelectable()==chk_hann){
			filterManager.setWndFuc(FilterManager.WND_HANN);
		}else if(e.getItemSelectable()==chk_blackman){
			filterManager.setWndFuc(FilterManager.WND_BLKMAN);
		}
		
		if(e.getItemSelectable()==chk_DFT){
			filterManager.setFFTMode(FilterManager.SET_DFT);
		}else if(e.getItemSelectable()==chk_FFT){
			filterManager.setFFTMode(FilterManager.SET_FFT);
		}else if(e.getItemSelectable()==chk_ALLFT){
			filterManager.setFFTMode(FilterManager.SET_ALLFT);
		}
		
	}

	/** 
		* ------------------ �o�[�֌W���\�b�h ------------------------ */
	public void adjustmentValueChanged(AdjustmentEvent ae){
	
		
		////////////////////////////////////
		//�S�̐ݒ�
		///////////////////////////////////
		//���Ԃ͈̔�
		if(ae.getAdjustable()==xGainSlider){
			int value=xGainSlider.getValue();
			System.out.println("current value="+value);
			filterManager.setXGain(value);
			
		}
		//���g���͈̔�
		if(ae.getAdjustable()==xFFTGainSlider){
			int value=xFFTGainSlider.getValue();
			System.out.println("current value="+value);
			filterManager.setXFFTGain(value);
		}
		//�T���v�����O���g��
		if(ae.getAdjustable()==sampleSlider){
			int sample=sampleSlider.getValue();
			filterManager.setSampleRate((double)sample);
			//filterManager.setFrequency((double)freq,(double)sample);
			System.out.println("sample freq="+sample);
			//this.sleepTime=value;
		}

		///////////////////////////////////
		//���͔g�`
		//////////////////////////////////
		//���g���̐ݒ�
		if(ae.getAdjustable()==freqSlider){
			int freq=freqSlider.getValue();
			if(DEBUG) System.out.println("current freq="+freq);
			filterManager.setFrequency((double)freq);
			
		}
		
		
		
		
	}
	
	// �t�H���g�p
	final static int DefaultFontSize = 12;

	public static Font SetFont(String name, int style, double size){
		if (size <= 0) size = 1.0;//�T�C�Y�����Ȃ�f�t�H���g�l
		int isize = (int)(DefaultFontSize*size);//�t�H���g���X�P�[�����O
		Font f = new Font(name,style,isize);
		return f;
	}
}
