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

public class ECG implements WaveGen{
	
	/** ファイル読み込み用*/
	public static final boolean DEBUG=false;
	URL mURL;
	private InputStream       is;
	private InputStreamReader in;
	private BufferedReader    br;
	private double gain = 1;
	private double max  = 0;
	private double min  = 0;
	private static int cnt=0;
	/**
	 *コンストラクタ 
	 */
	public ECG(String url) {
		try {
			mURL= new URL(url);
			is=mURL.openConnection().getInputStream();
			in = new InputStreamReader(is);
			br = new BufferedReader(in);
			
			
			//最初の500の最大値からgainを決める
			max = 0;
			min = Double.MAX_VALUE;
			
			if(DEBUG) cnt=0;
			
			for(int i = 0; i < 500 ; i++){
				String tmpStr = br.readLine();
				
				if(tmpStr == null){
					br.close();
					in.close();
					is.close();
					br = null;
					in = null;
					is = null;
					is=mURL.openConnection().getInputStream();
					in = new InputStreamReader(is);
					br = new BufferedReader(in);
					if(DEBUG) cnt=0;
				}
				
				double d = Double.parseDouble(tmpStr);
				if(max < d){
					max = d;
				}
				if(min > d){
					min = d;
				}
				if(DEBUG) System.out.println("cnt:"+(cnt++)+" d="+d);
			}
			gain = (1/(max-min));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初期化
	 */
	public void init(double freq, double sample) {}
	
	/**
	 * 次の波形を得る
	 */
	public double nextWave() {
		try {
			//一行読む
			String tmpStr = br.readLine();
			//ファイルの終わりに達したとき
			if(tmpStr == null){
				br.close();
				in.close();
				is.close();
				br = null;
				in = null;
				is = null;
				is=mURL.openConnection().getInputStream();
				in = new InputStreamReader(is);
				br = new BufferedReader(in);
				tmpStr = br.readLine();
				if(DEBUG) cnt=0;
			}
			//ダブルに変換
			double d = Double.parseDouble(tmpStr);
			
			if(DEBUG) System.out.println("cnt:"+(cnt++)+" d2="+d);

			//最小値がGLになるように移動
			return (d - min)*gain;
		
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
		
	}
}