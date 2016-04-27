/*****************************************************************/
/* Digital Signal Proccessing Program                            */
/*                     programming by embedded.samurai           */
/*****************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

abstract public class GraphPanel{
	//フォント	
	protected static final Font FONT =  FFTGraph.SetFont("Serif",Font.BOLD,2);
	//フォント(目盛り用)
	protected static final Font BG_FONT =  FFTGraph.SetFont("Serif",Font.PLAIN,1);
		
	//データポイント数 
	protected static final int PNT_WIDTH = 512;
	//暫定的な高さ     
	protected static int PNT_HEIGHT = 200;
	
	//背景色
	protected static final Color BG_COLOR = Color.WHITE;
	//目盛り線の色
	protected static final Color BG_LINE_COLOR = Color.LIGHT_GRAY;
	//目盛りの字の色
	protected static final Color BG_STR_COLOR = Color.BLACK;
	
	public GraphPanel() {
		//パネルの大きさを設定
	
	}
	

	
}