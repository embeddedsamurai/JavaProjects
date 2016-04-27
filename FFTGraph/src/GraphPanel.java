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
	//�t�H���g	
	protected static final Font FONT =  FFTGraph.SetFont("Serif",Font.BOLD,2);
	//�t�H���g(�ڐ���p)
	protected static final Font BG_FONT =  FFTGraph.SetFont("Serif",Font.PLAIN,1);
		
	//�f�[�^�|�C���g�� 
	protected static final int PNT_WIDTH = 512;
	//�b��I�ȍ���     
	protected static int PNT_HEIGHT = 200;
	
	//�w�i�F
	protected static final Color BG_COLOR = Color.WHITE;
	//�ڐ�����̐F
	protected static final Color BG_LINE_COLOR = Color.LIGHT_GRAY;
	//�ڐ���̎��̐F
	protected static final Color BG_STR_COLOR = Color.BLACK;
	
	public GraphPanel() {
		//�p�l���̑傫����ݒ�
	
	}
	

	
}