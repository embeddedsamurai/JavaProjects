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
	
	/** ループ内でのスリープ時間*/
	private static final int SLEEP_TIME = 20;
	
	private static int sleepTime=SLEEP_TIME;
	/** タイトルモード */
	public static final int TITLE_MODE = 0;
	/** 計算モード   */
	public static final int CALC_MODE = 1;
	/** 終了モード   */
	public static final int EXIT_MODE = 2;
	
	/** 画面のサイズ */
	public static final int SCREEN_W=600,SCREEN_H=700;

	/** フレーム */
	public static final int FRAME=10;
	
	/** main thread */
	private Thread  thread = null;   //スレッド
	
	/** for double buffering */
	public Image    offimage;       //ダブルバッファリング用のイメージ
	public Graphics back;           //ダブルバッファリング用のグラフィックス(バックバッファ)

	private Color backColor       = Color.white;
	public static int width       = SCREEN_W;
	public static int height      = SCREEN_H;
	private static int framerate  = FRAME;
	
	public Key key;
	public TitleManager titleManager;
	public FilterManager filterManager;
	public MyCanvas                 mc;//MyCanvas用のデータ
	
		/** 状態 */
	public static int mode_state;
	/** ひとつ前の状態 */
	public int back_mode_state;

	/** Button */
	Button btn1,btn2;	
	
	/** Scroll Bar */
	Scrollbar freqSlider,xGainSlider,xFFTGainSlider,speedSlider;
	
	/** Scroll Bar */
	Scrollbar sampleSlider;
	
	/** ラジオボタン(窓関数) */
	CheckboxGroup wndFncBtn = new CheckboxGroup();
	Checkbox chk_none,chk_hamming,chk_hann,chk_blackman;

	/** ラジオボタン(生成信号) */
	CheckboxGroup signalBtn = new CheckboxGroup();
	Checkbox chk_Sine,chk_Rect,chk_Sawtooth,chk_Triangle,chk_ECG,chk_PLS;
	
	/** ラジオボタン(描画方向) */
	CheckboxGroup displayBtn = new CheckboxGroup();
	Checkbox chk_leftRight,chk_RightLeft;

	/** ラジオボタン(周波数変換) */
	CheckboxGroup FreqBtn = new CheckboxGroup();
	Checkbox chk_DFT,chk_FFT,chk_DWVD,chk_SFFT,chk_Wavelet,chk_DWavelet,chk_ALLFT;
	//サイン波
	public static final int INPUT_SIN = 0;
	
	//----------------------------------------------------------
	//アプレットのお約束処理
	//
	//1 init
	//2 start
	//3 update
	//4 paint
	//5 stop
	//----------------------------------------------------------
	public void init()
	{
		/** バックグラウンドの設定 */
		String backs=getParameter("BGCOLOR");
		Color c;
		
		if(backs != null && (c = stringToColor(backs)) != null)
			backColor = c;
		
		setBackground(backColor);
		
		/** マウスの設定 */
		addMouseMotionListener(this);
		addMouseListener(this);
		
		/** キーの設定 */
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
		
		//ユーザ座標系の設定 
		//minx maxx miny maxy
		mc.setUserWindow(0,200,0,100);
		mc.setUserWindow(0,200,0,100);
		mc.setUserWindow(0,100,0,20);
		
		titleManager  = new TitleManager(this);
		filterManager = new FilterManager(this);
		
		
		//最初はタイトルを表示
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
		
		// キーの初期化
		key.init();
		//スリープ時間
		long startTime = System.currentTimeMillis();
		//計算にかかった時間
		long pastTime = 0;
				
		while (true){
			//キーを更新
			key.registKeyEvent();					
					
			//キー、計算、描画の各種処理
			if(mode_state == TITLE_MODE){//タイトルモード
				titleManager.process();
			}else if(mode_state == CALC_MODE){//計算モード
				filterManager.process();
			}	

			if(back_mode_state != mode_state){//モードの変更がないかどうかチェック。
				//今の状態を取っておく
				Color color = new Color(255,255,255);
				back.setColor(color);
				back.fillRect(0, 0, width,height);
				Font f0 = SetFont(back.getFont().getName(),back.getFont().getStyle(), 1.0);
				//標準サイズのフォントを生成
				back.setFont(f0);//現在フォントの設定
				back.setColor(Color.black);
				
				if(mode_state == CALC_MODE ){
					SetCalcWindow();
				}
				back_mode_state = mode_state;
			}
			
			// repaint()でpaint(g)の呼び出し,初めて描画が更新される
			repaint();
				
			//計算にかかった時間
			pastTime = System.currentTimeMillis() - startTime;					
					
			if(pastTime < sleepTime){
				//休止
				pause(sleepTime+5 - pastTime);
			}
			
			startTime = System.currentTimeMillis();
		}
	}

	public void SetCalcWindow(){
		
		//x 0 100 y 0 10の範囲
		double[]  bminx={40},bmaxx={50},bminy={2.5},bmaxy={6};
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		//ボタン関係準備
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
		mc.drawString("●全体設定",0,16,3);
		
		mc.drawString("□時間の範囲",0,14,3);
		//指定された方向、初期値0、可視量10、最小値1、最大値100のスクロールバーを作る
		xGainSlider = new Scrollbar(Scrollbar.HORIZONTAL,0,10,1,110);
		xGainSlider.setBackground(Color.gray);
		//長さ200 幅20のバーを作る
		bminx[0]=13;
		bminy[0]=16;
		bmaxx[0]=0;
		bmaxy[0]=0;
		
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		xGainSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		xGainSlider.addAdjustmentListener(this);
		add(xGainSlider);
		
        ////////////////////////////////////////////////////////
		mc.drawString("□周波数の範囲",30,14,3);
		//指定された方向、初期値0、可視量10、最小値1、最大値100のスクロールバーを作る
		xFFTGainSlider = new Scrollbar(Scrollbar.HORIZONTAL,0,10,1,110);
		xFFTGainSlider.setBackground(Color.gray);
		//長さ200 幅20のバーを作る
		bminx[0]=30+15;
		bminy[0]=16;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		xFFTGainSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		xFFTGainSlider.addAdjustmentListener(this);
		add(xFFTGainSlider);
		////////////////////////////////////////////////////////////
		mc.drawString("□サンプリング周波数",62,14,3);
		//指定された方向、初期値0、可視量10、最小値1、最大値100のスクロールバーを作る
		sampleSlider = new Scrollbar(Scrollbar.HORIZONTAL,250,10,100,1000);
		sampleSlider.setBackground(Color.gray);
		//長さ200 幅20のバーを作る
		bminx[0]=82;
		bminy[0]=16;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		sampleSlider.setBounds((int)bminx[0],(int)bminy[0],100,20);
		sampleSlider.addAdjustmentListener(this);
		add(sampleSlider);
		
		////////////////////////////////////////////////////
		////////////////////////////////////////////////////
		mc.drawString("●入力波形",0,12,3);
		mc.drawString("□周波数の設定",0,10,3);
		//指定された方向、初期値5、可視量10、最小値1、最大値100のスクロールバーを作る
		freqSlider = new Scrollbar(Scrollbar.HORIZONTAL,5,10,1,110);
		freqSlider.setBackground(Color.gray);
		//長さ200 幅20のバーを作る
		bminx[0]=13;
		bminy[0]=12;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		freqSlider.setBounds((int)(bminx[0]+10),(int)bminy[0],100,20);
		freqSlider.addAdjustmentListener(this);
		add(freqSlider);
		//
		
		mc.drawString("□入力波形の選択",0,7,3);
					
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
	
		mc.drawString("●周波数変換",0,4.5,3);
		
		mado_y =4;
		mado_x =0;
		bminx[0]=mado_x;
		bminy[0]=mado_y;
		mc.getWindowPosition(bminx,bmaxx,bminy,bmaxy,3);
		
		mado_x = (int)bminx[0];
		mado_y = (int)bminy[0];
		
		
		chk_DFT = new Checkbox("離散フーリエ",FreqBtn,true);
		chk_DFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_DFT.addItemListener(this);
					
		mado_x = mado_x + 100;
		chk_FFT = new Checkbox("高速フーリエ",FreqBtn,false);
		chk_FFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_FFT.addItemListener(this);
		
		mado_x = mado_x + 100;
		chk_ALLFT = new Checkbox("両方表示",FreqBtn,false);
		chk_ALLFT.setBounds(new Rectangle(mado_x, mado_y, 100, 20));
		chk_ALLFT.addItemListener(this);
		
		this.add(chk_DFT, null);
		this.add(chk_FFT, null);
		this.add(chk_ALLFT,null);
		///////////////////////////////////////////////////////
		//CheckboxGroup wndFncBtn = new CheckboxGroup();
		//Checkbox chk_none,chk_hamming,chk_hann,chk_blackman;
		//ラジオボタン(窓関数)
		mc.drawString("●窓関数",50,4.5,3);
		
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
	 * モードを変更する
	 * 実際に反映されるのは、ループ内での次の状態のチェック時
	 * 
	 * @param mode
	 */
	public static void setMode(int mode){
		mode_state = mode;
	}
	
	
	/**
	 * スレッドの休止 
	 */
	public void pause(long time){		
		try {
			//スリープ
			Thread.sleep(time);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	
	/** 
		"rrggbb"形式の文字列をColorオブジェクトに変換 
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
		// マウスがドラッグされた
		Key.mouseX = e.getX();
		Key.mouseY = e.getY();
	}

	public void mousePressed(MouseEvent e)
	{ 
		// マウスボタンの左が押された
		if(e.getModifiers() == 16)	//左クリック
		{
			Key.isPressMouse = true;
			mouseDragged(e);
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{ 
		// マウスボタンが離された
		Key.isPressMouse = false;
	}
	
	
	//使用しないが、記述しておかないとエラー
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
		
		
		//spaceが押された
		if(param==32){
			param=Key.KEY_SPACE;
			Key.keyFlag[0] |= (1L << param);
			Key.keyFlag[2] |= (1L << param);
		}
		
		//enterが押された
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
		
		//spaceがはなれた
		if(param==32){
			param=Key.KEY_SPACE;
			Key.keyFlag[2] &= ~(1L << param);
		}
		
		//enterがはなれた
		if(param==10){
			param=Key.KEY_ENTER;
			Key.keyFlag[2] &= ~(1L << param);
		}
		
	}
	public void keyTyped(KeyEvent e){}
	
	/** 
		* ------------------ ボタン関係メソッド ------------------------ */
	//テキストエリアに数値が記入された
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
		* ------------------ Checkボタン関係メソッド ------------------------ */
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
		* ------------------ バー関係メソッド ------------------------ */
	public void adjustmentValueChanged(AdjustmentEvent ae){
	
		
		////////////////////////////////////
		//全体設定
		///////////////////////////////////
		//時間の範囲
		if(ae.getAdjustable()==xGainSlider){
			int value=xGainSlider.getValue();
			System.out.println("current value="+value);
			filterManager.setXGain(value);
			
		}
		//周波数の範囲
		if(ae.getAdjustable()==xFFTGainSlider){
			int value=xFFTGainSlider.getValue();
			System.out.println("current value="+value);
			filterManager.setXFFTGain(value);
		}
		//サンプリング周波数
		if(ae.getAdjustable()==sampleSlider){
			int sample=sampleSlider.getValue();
			filterManager.setSampleRate((double)sample);
			//filterManager.setFrequency((double)freq,(double)sample);
			System.out.println("sample freq="+sample);
			//this.sleepTime=value;
		}

		///////////////////////////////////
		//入力波形
		//////////////////////////////////
		//周波数の設定
		if(ae.getAdjustable()==freqSlider){
			int freq=freqSlider.getValue();
			if(DEBUG) System.out.println("current freq="+freq);
			filterManager.setFrequency((double)freq);
			
		}
		
		
		
		
	}
	
	// フォント用
	final static int DefaultFontSize = 12;

	public static Font SetFont(String name, int style, double size){
		if (size <= 0) size = 1.0;//サイズが負ならデフォルト値
		int isize = (int)(DefaultFontSize*size);//フォントをスケーリング
		Font f = new Font(name,style,isize);
		return f;
	}
}
