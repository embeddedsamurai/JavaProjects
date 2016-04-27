/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     base program written by Masaki Aono       */
/*                     modify by      embedded.samurai           */
/*****************************************************************/


import java.awt.*;
import java.awt.image.*;
import java.applet.Applet;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class MyCanvas{

	/** Applet コピー用 */
	private Applet  mApplet;
	public Graphics back;
	
	// ユーザ座標の範囲 （デフォルトは[-1,1]ｘ[-1,1]）
	protected double[] userMinx;  //ユーザ座標系のX軸の最小値
	protected double[] userMaxx;  //ユーザ座標系のX軸の最大値
	protected double[] userMiny;  //ユーザ座標系のY軸の最小値
	protected double[] userMaxy;  //ユーザ座標系のY軸の最大値
	
	// ビューポートの範囲　（デフォルトは[0,1]x[0,1]）
	protected double[] viewMinx;  //ビューポートのX軸の最小値
	protected double[] viewMaxx;  //ビューポートのX軸の最大値
	protected double[] viewMiny;  //ビューポートのY軸の最小値
	protected double[] viewMaxy;  //ビューポートのY軸の最大値
	
	final static int DefaultViewportMax = 256;      //デフォルトのビューポート数
	protected int viewportMax = DefaultViewportMax; //ビューポートの数
	protected int viewportNum = 0;                  //現在のビューポートの数
	protected int userWinMax = 10;                  //ユーザウィンドウの総数
	protected int userWinNum = 0;                   //現在のユーザウィンドウの数
	
	//クリックしたビューポートの番号を保存する
	public int stViewPt=0;
	public int endViewPt=0;
	
	// ウィンドウのサイズ
	final static int DefaultWindowSize = 256;//デフォルトのウィンドウのサイズ
	protected int windowWidth = DefaultWindowSize;//ウィンドウの横幅
	protected int windowHeight = DefaultWindowSize;//ウィンドウの縦幅

	// MoveTo(x,y)とLineTo(x,y)のサポート用
	protected double lastx=0;//直前のX値
	protected double lasty=0;//直前のY値

	// フォント用
	final static int DefaultFontSize = 12;

	
	// コンストラクタ
	// デフォルトのコンストラクタでは viewportMax = 256
	public MyCanvas(FFTGraph applet){
		
		this.mApplet = applet;
		this.back    = applet.back;
		
		viewportMax = DefaultViewportMax; //ビューポートの数
		 
		//ウィンドウのサイズ/////////
		windowWidth = applet.getSize().width;  //ウィンドウの横幅
		windowHeight = applet.getSize().height; //ウィンドウの縦幅
		
		createViewport(DefaultViewportMax);//ビューポートの割り当て
	    createUserWindow(5);
	}
	
	//コンストラクタ
	public MyCanvas(FFTGraph applet,int width,int height)
	{

	  this.mApplet = applet;
	  this.back    = applet.back;
		
	  viewportMax = DefaultViewportMax; //ビューポートの数

	  //ウィンドウのサイズ/////////
	  windowWidth  = width;               //ウィンドウの横幅
	  windowHeight = height;              //ウィンドウの縦幅
	  createViewport(DefaultViewportMax); //ビューポートの割り当て
	  createUserWindow(5);
	  
	  //System.out.println("windowWidth"+windowWidth+"windowHeight"+windowHeight);

	}

	public void setWindow(int width,int height)
	{
	  windowWidth  = width;               //ウィンドウの横幅
	  windowHeight = height;              //ウィンドウの縦幅
	}
	
	private void createViewport(int max){
		
		viewportMax = max;//ビューポート数の最大値を設定
		viewMinx = new double[viewportMax];//ビューポートのX軸の最小値配列
		viewMaxx = new double[viewportMax];//ビューポートのX軸の最大値配列
		viewMiny = new double[viewportMax];//ビューポートのY軸の最小値配列
		viewMaxy = new double[viewportMax];//ビューポートのY軸の最大値配列
		viewMinx[0] = viewMiny[0] = 0.0;//ビューポートの最小値は０
		viewMaxx[0] = viewMaxy[0] = 1.0;//ビューポートの最大値は１
		viewportNum = 1;//ビューポートの現在インデックスを１とする
	}
	
	void createUserWindow(int max)
	{
	  userWinMax = max;   //ユーザ座標系の数の最大値を設定
	  userMinx = new double[userWinMax]; //ユーザ座標のx軸の最小値配列
	  userMaxx = new double[userWinMax]; //ユーザ座標のx軸の最大値配列
	  userMiny = new double[userWinMax]; //ユーザ座標のy軸の最小値配列
	  userMaxy = new double[userWinMax]; //ユーザ座標のy軸の最大値配列
	  userMinx[0] = userMiny[0] = -100;
	  userMaxx[0] = userMaxy[0] = 100;
	  userWinNum = 1; //ユーザ座標の現在インデックスを1とする。

	}
	
	//ウィンドウの横幅
	public int getWidth(){ return windowWidth; }
	//ウィンドウの縦幅
	public int getHeight(){	return windowHeight; }
	
	
	// ユーザ座標系の範囲の設定
	public void setUserWindow(double minx, double maxx, double miny, double maxy){
		  userMinx[userWinNum] = minx; //ウィンドウのx軸の最小値設定
		  userMaxx[userWinNum] = maxx; //ウィンドウのx軸の最大値設定
		  userMiny[userWinNum] = miny; //ウィンドウのy軸の最小値設定
		  userMaxy[userWinNum] = maxy; //ウィンドウのy軸の最大値設定
		  
		  userWinNum++;
	}
	
	public int getUserWindow(double[] minx,double[] maxx,double[] miny,double[] maxy,int num){
		 
		 if(userWinNum <= num) return 0;

		  minx[0] = userMinx[num]; 
		  maxx[0] = userMaxx[num]; 
		  miny[0] = userMiny[num]; 
		  maxy[0] = userMaxy[num]; 

		  //System.out.println("1 minx"+minx+"maxx"+maxx+"miny"+miny+"maxy"+maxy);
		  return num;

	}
	
	public int getWindowPosition(double[] minx,double[] maxx,double[] miny,double[] maxy,int num){
		
		minx[0] = getX(minx[0],num);//Java AWT座標値に変換
		miny[0] = getY(miny[0],num);//Java AWT座標値に変換
		maxx[0] = getX(maxx[0],num);//Java AWT座標値に変換
		maxy[0] = getY(maxy[0],num);//Java AWT座標値に変換
		
		return 0;
	}
	
	public void changeUserWindow(double minx, double maxx, double miny, double maxy,int num)
	{
		  userMinx[num] = minx; //ウィンドウのx軸の最小値設定
		  userMaxx[num] = maxx; //ウィンドウのx軸の最大値設定
		  userMiny[num] = miny; //ウィンドウのy軸の最小値設定
		  userMaxy[num] = maxy; //ウィンドウのy軸の最大値設定
	}
	
	// ビューポートの設定（クリッピングする）
	public void setViewport2(double minx, double maxx, double miny, double maxy){
		viewMinx[viewportNum] = minx;//現在のビューポートのX軸の最小値
		viewMaxx[viewportNum] = maxx;//現在のビューポートのX軸の最大値
		viewMiny[viewportNum] = miny;//現在のビューポートのY軸の最小値
		viewMaxy[viewportNum] = maxy;//現在のビューポートのY軸の最大値
		viewportNum++;//ビューポートの数を増加させる
		setClip(minx,miny,maxx,maxy,true);//ビューポートでクリッピングを設定
	}
	
	// ビューポートの設定（クリッピングする）
	public void setViewport(double minx, double maxx, double miny, double maxy){
		viewMinx[viewportNum] = minx;//現在のビューポートのX軸の最小値
		viewMaxx[viewportNum] = maxx;//現在のビューポートのX軸の最大値
		viewMiny[viewportNum] = miny;//現在のビューポートのY軸の最小値
		viewMaxy[viewportNum] = maxy;//現在のビューポートのY軸の最大値
		viewportNum++;//ビューポートの数を増加させる
	}
    
	// ビューポートのリセット
	public void resetViewport(){
		viewMinx[0] = viewMiny[0] = 0.0;//ビューポートの最小値０
		viewMaxx[0] = viewMaxy[0] = 1.0;//ビューポートの最大値１
		viewportNum = 1;//ビューポートの数を１とする
	}
	
	/**************************************************************/
	/*          ユーザ座標からWIN/Java座標を得るメソッド          */
	/*          プログラマはここさえ見えてればよい                */
	/**************************************************************/
	

	
	
	// Dimensionを得るメソッド
	public int getDimensionX(double w,int num){
		double x = viewMaxx[num] - viewMinx[num];
		x *= windowWidth * w / (userMaxx[num]-userMinx[num]);
		return ((int)Math.abs(x));
	}
	public int getDimensionY(double h,int num){
		double y = viewMaxy[num] - viewMiny[num];
		y *= windowHeight * h / (userMaxy[num]-userMiny[num]);
		return ((int)Math.abs(y));
	}
	
	// ユーザ座標からJava AWT座標を得るメソッド
	public int getX(double x,int num){
		double xx = viewX(x,num);//xをビューポートにマッピング
		int ix = getIntX(xx);//ビューポートをJava座標系にマッピング
		return ix;
	}
	public int getY(double y,int num){
		double yy = viewY(y,num);//yをビューポートにマッピング
		int iy = getIntY(yy);//ビューポートをJava座標系にマッピング
		return iy;
	}
	
	//ユーザ座標をビューポート座標にマッピングするメソッド
	public double viewX(double x,int num)
	{
	  double s = ( x - userMinx[num])/(userMaxx[num] - userMinx[num]);
	  double t = viewMinx[num] + s * (viewMaxx[num] - viewMinx[num]);
	  //System.out.println("userMinx["+num+"]="+userMinx[num]);
	  //System.out.println("userMaxx["+num+"]="+userMaxx[num]);
	  //System.out.println("viewMinx["+num+"]="+viewMinx[num]);
	  //System.out.println("viewMaxx["+num+"]="+viewMaxx[num]);
	  //System.out.println("t"+t);
	  return t;
	}

	public double viewY(double y,int num)
	{
	  double s = ( y - userMiny[num])/(userMaxy[num] - userMiny[num]);
	  double t = viewMiny[num] +
	  s * (viewMaxy[num] - viewMiny[num]);

	  return t;
	}
	
	// ビューポート座標をJava AWT座標にマッピングするメソッド
	public int getIntX(double x){
		return (int)(windowWidth * x);//ウィンドウの横幅倍する
	}
	public int getIntY(double y){
		return (int)(windowHeight * (1-y));//ウィンドウの縦幅倍する
	}
	
	// 線分の描画
	public void drawLine(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//Java AWT座標値に変換
		int iy1 = getY(y1,num);//Java AWT座標値に変換
		int ix2 = getX(x2,num);//Java AWT座標値に変換
		int iy2 = getY(y2,num);//Java AWT座標値に変換
		back.drawLine(ix1,iy1,ix2,iy2);
		//g.drawLine(0,0,500,500);
	}
	/************************************************************************/

	
	// 逆マッピング
	// Java AWT座標からビューポートに逆マッピング
	public int GetViewPort( int ix,int iy )
	{
		double s = (double) (ix) / (double)windowWidth;
		double t = (double)( windowHeight - iy ) / (double)windowHeight;

		for(int i=1; i < viewportNum; i++){
		
			if( s >= viewMinx[i] && s <= viewMaxx[i] &&
				t >= viewMiny[i] && t <= viewMaxy[i]){
					return i;
			}

		}
		
		return 0;
	}
	
	
	//ビューポートからユーザ座標系に逆マッピング(x座標)
	public double GetUserX(int ix,int v)
	{
		//windows座標からビューポートへ
		double xv = (double)ix / (double)windowWidth;
		//ビューポートからユーザ座標系に逆マッピング
		double x = userMinx[v] + ( userMaxx[v]-userMinx[v]) *  ((xv - viewMinx[v]) / (viewMaxx[v] - viewMinx[v]));

		//TRACE("viewMinx[%d]=%f\n",v,viewMinx[v]);
		//TRACE("viewMaxx[%d]=%f\n",v,viewMaxx[v]);
		return x;
	}

	//ビューポートからユーザ座標系に逆マッピング(y座標)
	public double GetUserY(int iy,int v)
	{
		double yv = (double)(windowHeight-iy) / (double)windowHeight;
		double y = userMiny[v] + (userMaxy[v] - userMiny[v]) * ( (yv-viewMiny[v] ) / (viewMaxy[v] - viewMiny[v]));

		//TRACE("viewMiny[%d]=%f\n",v,viewMiny[v]);
		//TRACE("viewMaxy[%d]=%f\n",v,viewMaxy[v]);

		return y;
	}
	
	
	
	// クリッピング
	public void clipRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.clipRect(x0,y0,width,height);
	}
	public void setClip(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.setClip(x0,y0,width,height);
	}
	
	public void setClip(double x1, double y1, double x2, double y2, 
		boolean flag){
		int ix1 = getIntX(x1);//４隅のどこかの点のビューポートｘ座標値をJava座標値に
		int iy1 = getIntY(y1);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getIntX(x2);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getIntY(y2);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.setClip(x0,y0,width,height);
	}

	//
	// 描画メソッド
	//
	
	// 矩形の描画
	public void drawRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.drawRect(x0,y0,width,height);
	}
	// 矩形の塗りつぶし
	public void fillRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.fillRect(x0,y0,width,height);
	}
	
	// 矩形で領域をクリア
	public void clearRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.clearRect(x0,y0,width,height);
	}
	
	
	// 角に丸みのある矩形の描画
	public void drawRoundRect(double x1, double y1, double x2, double y2, 
		double arcW, double arcH,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		int iarcWidth = getDimensionX(arcW,num);//角の丸みの横サイズ
		int iarcHeight = getDimensionY(arcH,num);//角の丸みの縦サイズ
		back.drawRoundRect(x0,y0,width,height,
			iarcWidth,iarcHeight);
	}
	// 角に丸みのある矩形の塗りつぶし
	public void fillRoundRect(double x1, double y1, double x2, double y2,
		double arcW, double arcH,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		int iarcWidth = getDimensionX(arcW,num);//角の丸みの横サイズ
		int iarcHeight = getDimensionY(arcH,num);//角の丸みの縦サイズ
		back.fillRoundRect(x0,y0,width,height,
			iarcWidth,iarcHeight);
	}
	
	// 浮かび上がる矩形の描画
	public void draw3DRect(double x1, double y1, double x2, double y2,
		boolean raised,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.draw3DRect(x0,y0,width,height,raised);
	}
	// 浮かび上がる矩形の塗りつぶし
	public void fill3DRect(double x1, double y1, double x2, double y2,
		boolean raised,int num){
		int ix1 = getX(x1,num);//４隅のどこかの点のｘ座標値をJava座標値に
		int iy1 = getY(y1,num);//x1と同じ点のｙ座標値をJava座標値に
		int ix2 = getX(x2,num);//x1に対し対角線上の隅のｘ座標値をJava座標値に
		int iy2 = getY(y2,num);//y1に対し対角線上の隅のｙ座標値をJava座標値に
		int width = Math.abs(ix1-ix2)+1;//横幅を計算
		int height = Math.abs(iy1-iy2)+1;//縦幅を計算
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//開始点のX座標（左上）
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//開始点のY座標（左上）
		back.fill3DRect(x0,y0,width,height,raised);
	}		
	// だ円の描画　（中心(x,y), 半径(xr,yr))
	public void drawOval(double x, double y, double xr, double yr,int num){
		int ix = getX(x,num);//だ円の中心のJava AWTでのX座標
		int iy = getY(y,num);//だ円の中心のJava AWTでのY座標
		int ixr = getDimensionX(xr,num);//半径の横幅
		int iyr = getDimensionY(yr,num);//半径の縦幅
		int x0 = ix - ixr;//だ円を囲む矩形の左上隅（X）
		int y0 = iy - iyr;//だ円を囲む矩形の左上隅（X）
		back.drawOval(x0,y0,2*ixr,2*iyr);
	}
	// だ円の塗りつぶし　（中心(x,y), 半径(xr,yr))
	public void fillOval(double x, double y, double xr, double yr,int num){
		int ix = getX(x,num);//だ円の中心のJava AWTでのX座標
		int iy = getY(y,num);//だ円の中心のJava AWTでのY座標
		int ixr = getDimensionX(xr,num);//半径の横幅
		int iyr = getDimensionY(yr,num);//半径の縦幅
		int x0 = ix - ixr;//だ円を囲む矩形の左上隅（X）
		int y0 = iy - iyr;//だ円を囲む矩形の左上隅（X）
		back.fillOval(x0,y0,2*ixr,2*iyr);
	}
	
	// 円弧の描画　（中心(x,y) 半径(xr,yr))
    	public void drawArc(double x, double y, double xr, 
		double yr, double startAngle, double arcAngle,int num){
		int ix = getX(x,num);//円弧の中心のJava AWTでのX座標
		int iy = getY(y,num);//円弧の中心のJava AWTでのY座標
		int ixr = getDimensionX(xr,num);//半径の横幅
		int iyr = getDimensionY(yr,num);//半径の縦幅
		int x0 = ix - ixr;//円弧を囲む矩形の左上隅（X）
		int y0 = iy - iyr;//円弧を囲む矩形の左上隅（X）
		int is = (int)(90-(startAngle+arcAngle));//開始アングル（デグリー）
		int ia = (int)arcAngle;//扇形の弧の角度（デグリー）
		back.drawArc(x0,y0,2*ixr,2*iyr,is,ia);
	}
    	
	// 扇形の塗りつぶし　（中心(x,y) 半径(xr,yr))
    	public void fillArc(double x, double y, double xr, 
		double yr, double startAngle, double arcAngle,int num){
		int ix = getX(x,num);//扇形の中心のJava AWTでのX座標
		int iy = getY(y,num);//扇形の中心のJava AWTでのY座標
		int ixr = getDimensionX(xr,num);//半径の横幅
		int iyr = getDimensionY(yr,num);//半径の縦幅
		int x0 = ix - ixr;//扇形を囲む矩形の左上隅（X）
		int y0 = iy - iyr;//扇形を囲む矩形の左上隅（X）
		int is = (int)(90-(startAngle+arcAngle));//開始アングル（デグリー）
		int ia = (int)arcAngle;//扇形の弧の角度（デグリー）
		back.fillArc(x0,y0,2*ixr,2*iyr,is,ia);
	}	
    	
	// 折れ線の描画	
	public void drawPolyline(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT座標値に変換
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.drawPolyline(ix,iy,numPoints);
	}
	
	// 多角形の描画
	public void drawPolygon(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT座標値に変換
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.drawPolygon(ix,iy,numPoints);
	}
	// 多角形の塗りつぶし
	public void fillPolygon(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT座標値に変換
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.fillPolygon(ix,iy,numPoints);
	}
	
	// 文字列の描画
	public void drawString(String str, double x, double y,int num){
		int ix = getX(x,num);//Java AWT座標値に変換
		int iy = getY(y,num);//Java AWT座標値に変換
		back.drawString(str,ix,iy);
	}
	
	// 画像の描画
    public boolean drawImage(Image img, double x, double y, 
		ImageObserver observer,int num){
		if (back == null) return false;
		int ix = getX(x,num);//Java AWT座標値に変換
		int iy = getY(y,num);//Java AWT座標値に変換
		return back.drawImage(img,ix,iy,observer);
	}		
    
   	public boolean drawImage(Image img, double x, double y, 
		double w, double h, ImageObserver observer,int num){
		if (back == null) return false;
		int ix = getX(x,num);//Java AWT座標値に変換
		int iy = getY(y,num);//Java AWT座標値に変換
		int iw = getDimensionX(w,num);//getX(w)-getX(0);//画像の横幅
		int ih = getDimensionY(h,num);//getY(0)-getY(h);//画像の縦幅
		return back.drawImage(img,ix,iy,iw,ih,observer);
	}
   	
   	public boolean drawImage(Image img, double x, double y, 
		Color bgcolor, ImageObserver observer,int num){
		int ix = getX(x,num);//Java AWT座標値に変換
		int iy = getY(y,num);//Java AWT座標値に変換
		return back.drawImage(img,ix,iy,bgcolor,observer);
	}		
   	public boolean drawImage(Image img, double x, double y, 
		double w, double h, 
		Color bgcolor, ImageObserver observer,int num){
		int ix = getX(x,num);//Java AWT座標値に変換
		int iy = getY(y,num);//Java AWT座標値に変換
		int iw = getDimensionX(w,num);//画像の横幅
		int ih = getDimensionY(h,num);//画像の縦幅
		return back.drawImage(img,ix,iy,iw,ih,bgcolor,observer);
	}		
	public boolean drawImage(Image img,
		double dx1, double dy1, double dx2, double dy2,
		double sx1, double sy1, double sx2, double sy2,
		ImageObserver observer,int num){
		int idx1 = getX(dx1,num);//Java AWT座標値に変換
		int idy1 = getY(dy1,num);//Java AWT座標値に変換
		int idx2 = getX(dx2,num);//Java AWT座標値に変換
		int idy2 = getY(dy2,num);//Java AWT座標値に変換
		int isx1 = getX(sx1,num);//Java AWT座標値に変換
		int isy1 = getY(sy1,num);//Java AWT座標値に変換
		int isx2 = getX(sx2,num);//Java AWT座標値に変換
		int isy2 = getY(sy2,num);//Java AWT座標値に変換
		return back.drawImage(img,
			idx1, idy1, idx2, idy2,	isx1, isy1, isx2, isy2, observer);
	}

	public boolean drawImage(Image img,
		double dx1, double dy1, double dx2, double dy2,
		double sx1, double sy1, double sx2, double sy2,
		Color bgcolor, ImageObserver observer,int num){
		int idx1 = getX(dx1,num);//Java AWT座標値に変換
		int idy1 = getY(dy1,num);//Java AWT座標値に変換
		int idx2 = getX(dx2,num);//Java AWT座標値に変換
		int idy2 = getY(dy2,num);//Java AWT座標値に変換
		int isx1 = getX(sx1,num);//Java AWT座標値に変換
		int isy1 = getY(sy1,num);//Java AWT座標値に変換
		int isx2 = getX(sx2,num);//Java AWT座標値に変換
		int isy2 = getY(sy2,num);//Java AWT座標値に変換
		return back.drawImage(img,
			idx1, idy1, idx2, idy2, isx1, isy1, isx2, isy2,
			bgcolor, observer);
	}
	
	// xの符号を返すメソッド
	public int Sign(int x){
		if (x > 0) return 1;
		else if (x < 0) return -1;
		return 0;
	}
	public int Sign(double x){
		if (x > 0.0) return 1;
		else if (x < 0.0) return -1;
		return 0;
	}
	
	
	// 直線描画の別のメソッド
	public void moveTo(double x, double y,int num){
		lastx = x;//もっとも最近のX位置をセット
		lasty = y;//もっとも最近のY位置をセット
	}
	public void lineTo(double x, double y,int num){
		drawLine(lastx,lasty,x,y,num);//直線を描画
		lastx = x; lasty = y;//最近の位置を更新
	}
}
