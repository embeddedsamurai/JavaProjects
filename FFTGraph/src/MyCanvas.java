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

	/** Applet �R�s�[�p */
	private Applet  mApplet;
	public Graphics back;
	
	// ���[�U���W�͈̔� �i�f�t�H���g��[-1,1]��[-1,1]�j
	protected double[] userMinx;  //���[�U���W�n��X���̍ŏ��l
	protected double[] userMaxx;  //���[�U���W�n��X���̍ő�l
	protected double[] userMiny;  //���[�U���W�n��Y���̍ŏ��l
	protected double[] userMaxy;  //���[�U���W�n��Y���̍ő�l
	
	// �r���[�|�[�g�͈̔́@�i�f�t�H���g��[0,1]x[0,1]�j
	protected double[] viewMinx;  //�r���[�|�[�g��X���̍ŏ��l
	protected double[] viewMaxx;  //�r���[�|�[�g��X���̍ő�l
	protected double[] viewMiny;  //�r���[�|�[�g��Y���̍ŏ��l
	protected double[] viewMaxy;  //�r���[�|�[�g��Y���̍ő�l
	
	final static int DefaultViewportMax = 256;      //�f�t�H���g�̃r���[�|�[�g��
	protected int viewportMax = DefaultViewportMax; //�r���[�|�[�g�̐�
	protected int viewportNum = 0;                  //���݂̃r���[�|�[�g�̐�
	protected int userWinMax = 10;                  //���[�U�E�B���h�E�̑���
	protected int userWinNum = 0;                   //���݂̃��[�U�E�B���h�E�̐�
	
	//�N���b�N�����r���[�|�[�g�̔ԍ���ۑ�����
	public int stViewPt=0;
	public int endViewPt=0;
	
	// �E�B���h�E�̃T�C�Y
	final static int DefaultWindowSize = 256;//�f�t�H���g�̃E�B���h�E�̃T�C�Y
	protected int windowWidth = DefaultWindowSize;//�E�B���h�E�̉���
	protected int windowHeight = DefaultWindowSize;//�E�B���h�E�̏c��

	// MoveTo(x,y)��LineTo(x,y)�̃T�|�[�g�p
	protected double lastx=0;//���O��X�l
	protected double lasty=0;//���O��Y�l

	// �t�H���g�p
	final static int DefaultFontSize = 12;

	
	// �R���X�g���N�^
	// �f�t�H���g�̃R���X�g���N�^�ł� viewportMax = 256
	public MyCanvas(FFTGraph applet){
		
		this.mApplet = applet;
		this.back    = applet.back;
		
		viewportMax = DefaultViewportMax; //�r���[�|�[�g�̐�
		 
		//�E�B���h�E�̃T�C�Y/////////
		windowWidth = applet.getSize().width;  //�E�B���h�E�̉���
		windowHeight = applet.getSize().height; //�E�B���h�E�̏c��
		
		createViewport(DefaultViewportMax);//�r���[�|�[�g�̊��蓖��
	    createUserWindow(5);
	}
	
	//�R���X�g���N�^
	public MyCanvas(FFTGraph applet,int width,int height)
	{

	  this.mApplet = applet;
	  this.back    = applet.back;
		
	  viewportMax = DefaultViewportMax; //�r���[�|�[�g�̐�

	  //�E�B���h�E�̃T�C�Y/////////
	  windowWidth  = width;               //�E�B���h�E�̉���
	  windowHeight = height;              //�E�B���h�E�̏c��
	  createViewport(DefaultViewportMax); //�r���[�|�[�g�̊��蓖��
	  createUserWindow(5);
	  
	  //System.out.println("windowWidth"+windowWidth+"windowHeight"+windowHeight);

	}

	public void setWindow(int width,int height)
	{
	  windowWidth  = width;               //�E�B���h�E�̉���
	  windowHeight = height;              //�E�B���h�E�̏c��
	}
	
	private void createViewport(int max){
		
		viewportMax = max;//�r���[�|�[�g���̍ő�l��ݒ�
		viewMinx = new double[viewportMax];//�r���[�|�[�g��X���̍ŏ��l�z��
		viewMaxx = new double[viewportMax];//�r���[�|�[�g��X���̍ő�l�z��
		viewMiny = new double[viewportMax];//�r���[�|�[�g��Y���̍ŏ��l�z��
		viewMaxy = new double[viewportMax];//�r���[�|�[�g��Y���̍ő�l�z��
		viewMinx[0] = viewMiny[0] = 0.0;//�r���[�|�[�g�̍ŏ��l�͂O
		viewMaxx[0] = viewMaxy[0] = 1.0;//�r���[�|�[�g�̍ő�l�͂P
		viewportNum = 1;//�r���[�|�[�g�̌��݃C���f�b�N�X���P�Ƃ���
	}
	
	void createUserWindow(int max)
	{
	  userWinMax = max;   //���[�U���W�n�̐��̍ő�l��ݒ�
	  userMinx = new double[userWinMax]; //���[�U���W��x���̍ŏ��l�z��
	  userMaxx = new double[userWinMax]; //���[�U���W��x���̍ő�l�z��
	  userMiny = new double[userWinMax]; //���[�U���W��y���̍ŏ��l�z��
	  userMaxy = new double[userWinMax]; //���[�U���W��y���̍ő�l�z��
	  userMinx[0] = userMiny[0] = -100;
	  userMaxx[0] = userMaxy[0] = 100;
	  userWinNum = 1; //���[�U���W�̌��݃C���f�b�N�X��1�Ƃ���B

	}
	
	//�E�B���h�E�̉���
	public int getWidth(){ return windowWidth; }
	//�E�B���h�E�̏c��
	public int getHeight(){	return windowHeight; }
	
	
	// ���[�U���W�n�͈̔͂̐ݒ�
	public void setUserWindow(double minx, double maxx, double miny, double maxy){
		  userMinx[userWinNum] = minx; //�E�B���h�E��x���̍ŏ��l�ݒ�
		  userMaxx[userWinNum] = maxx; //�E�B���h�E��x���̍ő�l�ݒ�
		  userMiny[userWinNum] = miny; //�E�B���h�E��y���̍ŏ��l�ݒ�
		  userMaxy[userWinNum] = maxy; //�E�B���h�E��y���̍ő�l�ݒ�
		  
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
		
		minx[0] = getX(minx[0],num);//Java AWT���W�l�ɕϊ�
		miny[0] = getY(miny[0],num);//Java AWT���W�l�ɕϊ�
		maxx[0] = getX(maxx[0],num);//Java AWT���W�l�ɕϊ�
		maxy[0] = getY(maxy[0],num);//Java AWT���W�l�ɕϊ�
		
		return 0;
	}
	
	public void changeUserWindow(double minx, double maxx, double miny, double maxy,int num)
	{
		  userMinx[num] = minx; //�E�B���h�E��x���̍ŏ��l�ݒ�
		  userMaxx[num] = maxx; //�E�B���h�E��x���̍ő�l�ݒ�
		  userMiny[num] = miny; //�E�B���h�E��y���̍ŏ��l�ݒ�
		  userMaxy[num] = maxy; //�E�B���h�E��y���̍ő�l�ݒ�
	}
	
	// �r���[�|�[�g�̐ݒ�i�N���b�s���O����j
	public void setViewport2(double minx, double maxx, double miny, double maxy){
		viewMinx[viewportNum] = minx;//���݂̃r���[�|�[�g��X���̍ŏ��l
		viewMaxx[viewportNum] = maxx;//���݂̃r���[�|�[�g��X���̍ő�l
		viewMiny[viewportNum] = miny;//���݂̃r���[�|�[�g��Y���̍ŏ��l
		viewMaxy[viewportNum] = maxy;//���݂̃r���[�|�[�g��Y���̍ő�l
		viewportNum++;//�r���[�|�[�g�̐��𑝉�������
		setClip(minx,miny,maxx,maxy,true);//�r���[�|�[�g�ŃN���b�s���O��ݒ�
	}
	
	// �r���[�|�[�g�̐ݒ�i�N���b�s���O����j
	public void setViewport(double minx, double maxx, double miny, double maxy){
		viewMinx[viewportNum] = minx;//���݂̃r���[�|�[�g��X���̍ŏ��l
		viewMaxx[viewportNum] = maxx;//���݂̃r���[�|�[�g��X���̍ő�l
		viewMiny[viewportNum] = miny;//���݂̃r���[�|�[�g��Y���̍ŏ��l
		viewMaxy[viewportNum] = maxy;//���݂̃r���[�|�[�g��Y���̍ő�l
		viewportNum++;//�r���[�|�[�g�̐��𑝉�������
	}
    
	// �r���[�|�[�g�̃��Z�b�g
	public void resetViewport(){
		viewMinx[0] = viewMiny[0] = 0.0;//�r���[�|�[�g�̍ŏ��l�O
		viewMaxx[0] = viewMaxy[0] = 1.0;//�r���[�|�[�g�̍ő�l�P
		viewportNum = 1;//�r���[�|�[�g�̐����P�Ƃ���
	}
	
	/**************************************************************/
	/*          ���[�U���W����WIN/Java���W�𓾂郁�\�b�h          */
	/*          �v���O���}�͂������������Ă�΂悢                */
	/**************************************************************/
	

	
	
	// Dimension�𓾂郁�\�b�h
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
	
	// ���[�U���W����Java AWT���W�𓾂郁�\�b�h
	public int getX(double x,int num){
		double xx = viewX(x,num);//x���r���[�|�[�g�Ƀ}�b�s���O
		int ix = getIntX(xx);//�r���[�|�[�g��Java���W�n�Ƀ}�b�s���O
		return ix;
	}
	public int getY(double y,int num){
		double yy = viewY(y,num);//y���r���[�|�[�g�Ƀ}�b�s���O
		int iy = getIntY(yy);//�r���[�|�[�g��Java���W�n�Ƀ}�b�s���O
		return iy;
	}
	
	//���[�U���W���r���[�|�[�g���W�Ƀ}�b�s���O���郁�\�b�h
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
	
	// �r���[�|�[�g���W��Java AWT���W�Ƀ}�b�s���O���郁�\�b�h
	public int getIntX(double x){
		return (int)(windowWidth * x);//�E�B���h�E�̉����{����
	}
	public int getIntY(double y){
		return (int)(windowHeight * (1-y));//�E�B���h�E�̏c���{����
	}
	
	// �����̕`��
	public void drawLine(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//Java AWT���W�l�ɕϊ�
		int iy1 = getY(y1,num);//Java AWT���W�l�ɕϊ�
		int ix2 = getX(x2,num);//Java AWT���W�l�ɕϊ�
		int iy2 = getY(y2,num);//Java AWT���W�l�ɕϊ�
		back.drawLine(ix1,iy1,ix2,iy2);
		//g.drawLine(0,0,500,500);
	}
	/************************************************************************/

	
	// �t�}�b�s���O
	// Java AWT���W����r���[�|�[�g�ɋt�}�b�s���O
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
	
	
	//�r���[�|�[�g���烆�[�U���W�n�ɋt�}�b�s���O(x���W)
	public double GetUserX(int ix,int v)
	{
		//windows���W����r���[�|�[�g��
		double xv = (double)ix / (double)windowWidth;
		//�r���[�|�[�g���烆�[�U���W�n�ɋt�}�b�s���O
		double x = userMinx[v] + ( userMaxx[v]-userMinx[v]) *  ((xv - viewMinx[v]) / (viewMaxx[v] - viewMinx[v]));

		//TRACE("viewMinx[%d]=%f\n",v,viewMinx[v]);
		//TRACE("viewMaxx[%d]=%f\n",v,viewMaxx[v]);
		return x;
	}

	//�r���[�|�[�g���烆�[�U���W�n�ɋt�}�b�s���O(y���W)
	public double GetUserY(int iy,int v)
	{
		double yv = (double)(windowHeight-iy) / (double)windowHeight;
		double y = userMiny[v] + (userMaxy[v] - userMiny[v]) * ( (yv-viewMiny[v] ) / (viewMaxy[v] - viewMiny[v]));

		//TRACE("viewMiny[%d]=%f\n",v,viewMiny[v]);
		//TRACE("viewMaxy[%d]=%f\n",v,viewMaxy[v]);

		return y;
	}
	
	
	
	// �N���b�s���O
	public void clipRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.clipRect(x0,y0,width,height);
	}
	public void setClip(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.setClip(x0,y0,width,height);
	}
	
	public void setClip(double x1, double y1, double x2, double y2, 
		boolean flag){
		int ix1 = getIntX(x1);//�S���̂ǂ����̓_�̃r���[�|�[�g�����W�l��Java���W�l��
		int iy1 = getIntY(y1);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getIntX(x2);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getIntY(y2);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.setClip(x0,y0,width,height);
	}

	//
	// �`�惁�\�b�h
	//
	
	// ��`�̕`��
	public void drawRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.drawRect(x0,y0,width,height);
	}
	// ��`�̓h��Ԃ�
	public void fillRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.fillRect(x0,y0,width,height);
	}
	
	// ��`�ŗ̈���N���A
	public void clearRect(double x1, double y1, double x2, double y2,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.clearRect(x0,y0,width,height);
	}
	
	
	// �p�Ɋۂ݂̂����`�̕`��
	public void drawRoundRect(double x1, double y1, double x2, double y2, 
		double arcW, double arcH,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		int iarcWidth = getDimensionX(arcW,num);//�p�̊ۂ݂̉��T�C�Y
		int iarcHeight = getDimensionY(arcH,num);//�p�̊ۂ݂̏c�T�C�Y
		back.drawRoundRect(x0,y0,width,height,
			iarcWidth,iarcHeight);
	}
	// �p�Ɋۂ݂̂����`�̓h��Ԃ�
	public void fillRoundRect(double x1, double y1, double x2, double y2,
		double arcW, double arcH,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		int iarcWidth = getDimensionX(arcW,num);//�p�̊ۂ݂̉��T�C�Y
		int iarcHeight = getDimensionY(arcH,num);//�p�̊ۂ݂̏c�T�C�Y
		back.fillRoundRect(x0,y0,width,height,
			iarcWidth,iarcHeight);
	}
	
	// �����яオ���`�̕`��
	public void draw3DRect(double x1, double y1, double x2, double y2,
		boolean raised,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.draw3DRect(x0,y0,width,height,raised);
	}
	// �����яオ���`�̓h��Ԃ�
	public void fill3DRect(double x1, double y1, double x2, double y2,
		boolean raised,int num){
		int ix1 = getX(x1,num);//�S���̂ǂ����̓_�̂����W�l��Java���W�l��
		int iy1 = getY(y1,num);//x1�Ɠ����_�̂����W�l��Java���W�l��
		int ix2 = getX(x2,num);//x1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int iy2 = getY(y2,num);//y1�ɑ΂��Ίp����̋��̂����W�l��Java���W�l��
		int width = Math.abs(ix1-ix2)+1;//�������v�Z
		int height = Math.abs(iy1-iy2)+1;//�c�����v�Z
		int x0 = (ix1 <= ix2) ? ix1 : ix2;//�J�n�_��X���W�i����j
		int y0 = (iy1 <= iy2) ? iy1 : iy2;//�J�n�_��Y���W�i����j
		back.fill3DRect(x0,y0,width,height,raised);
	}		
	// ���~�̕`��@�i���S(x,y), ���a(xr,yr))
	public void drawOval(double x, double y, double xr, double yr,int num){
		int ix = getX(x,num);//���~�̒��S��Java AWT�ł�X���W
		int iy = getY(y,num);//���~�̒��S��Java AWT�ł�Y���W
		int ixr = getDimensionX(xr,num);//���a�̉���
		int iyr = getDimensionY(yr,num);//���a�̏c��
		int x0 = ix - ixr;//���~���͂ދ�`�̍�����iX�j
		int y0 = iy - iyr;//���~���͂ދ�`�̍�����iX�j
		back.drawOval(x0,y0,2*ixr,2*iyr);
	}
	// ���~�̓h��Ԃ��@�i���S(x,y), ���a(xr,yr))
	public void fillOval(double x, double y, double xr, double yr,int num){
		int ix = getX(x,num);//���~�̒��S��Java AWT�ł�X���W
		int iy = getY(y,num);//���~�̒��S��Java AWT�ł�Y���W
		int ixr = getDimensionX(xr,num);//���a�̉���
		int iyr = getDimensionY(yr,num);//���a�̏c��
		int x0 = ix - ixr;//���~���͂ދ�`�̍�����iX�j
		int y0 = iy - iyr;//���~���͂ދ�`�̍�����iX�j
		back.fillOval(x0,y0,2*ixr,2*iyr);
	}
	
	// �~�ʂ̕`��@�i���S(x,y) ���a(xr,yr))
    	public void drawArc(double x, double y, double xr, 
		double yr, double startAngle, double arcAngle,int num){
		int ix = getX(x,num);//�~�ʂ̒��S��Java AWT�ł�X���W
		int iy = getY(y,num);//�~�ʂ̒��S��Java AWT�ł�Y���W
		int ixr = getDimensionX(xr,num);//���a�̉���
		int iyr = getDimensionY(yr,num);//���a�̏c��
		int x0 = ix - ixr;//�~�ʂ��͂ދ�`�̍�����iX�j
		int y0 = iy - iyr;//�~�ʂ��͂ދ�`�̍�����iX�j
		int is = (int)(90-(startAngle+arcAngle));//�J�n�A���O���i�f�O���[�j
		int ia = (int)arcAngle;//��`�̌ʂ̊p�x�i�f�O���[�j
		back.drawArc(x0,y0,2*ixr,2*iyr,is,ia);
	}
    	
	// ��`�̓h��Ԃ��@�i���S(x,y) ���a(xr,yr))
    	public void fillArc(double x, double y, double xr, 
		double yr, double startAngle, double arcAngle,int num){
		int ix = getX(x,num);//��`�̒��S��Java AWT�ł�X���W
		int iy = getY(y,num);//��`�̒��S��Java AWT�ł�Y���W
		int ixr = getDimensionX(xr,num);//���a�̉���
		int iyr = getDimensionY(yr,num);//���a�̏c��
		int x0 = ix - ixr;//��`���͂ދ�`�̍�����iX�j
		int y0 = iy - iyr;//��`���͂ދ�`�̍�����iX�j
		int is = (int)(90-(startAngle+arcAngle));//�J�n�A���O���i�f�O���[�j
		int ia = (int)arcAngle;//��`�̌ʂ̊p�x�i�f�O���[�j
		back.fillArc(x0,y0,2*ixr,2*iyr,is,ia);
	}	
    	
	// �܂���̕`��	
	public void drawPolyline(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT���W�l�ɕϊ�
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.drawPolyline(ix,iy,numPoints);
	}
	
	// ���p�`�̕`��
	public void drawPolygon(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT���W�l�ɕϊ�
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.drawPolygon(ix,iy,numPoints);
	}
	// ���p�`�̓h��Ԃ�
	public void fillPolygon(double[] x, double[] y, int numPoints,int num){
		int[] ix = new int[numPoints];
		int[] iy = new int[numPoints];
		for (int i=0; i < numPoints ; i++){//Java AWT���W�l�ɕϊ�
			ix[i] = getX(x[i],num);
			iy[i] = getY(y[i],num);
		}
		back.fillPolygon(ix,iy,numPoints);
	}
	
	// ������̕`��
	public void drawString(String str, double x, double y,int num){
		int ix = getX(x,num);//Java AWT���W�l�ɕϊ�
		int iy = getY(y,num);//Java AWT���W�l�ɕϊ�
		back.drawString(str,ix,iy);
	}
	
	// �摜�̕`��
    public boolean drawImage(Image img, double x, double y, 
		ImageObserver observer,int num){
		if (back == null) return false;
		int ix = getX(x,num);//Java AWT���W�l�ɕϊ�
		int iy = getY(y,num);//Java AWT���W�l�ɕϊ�
		return back.drawImage(img,ix,iy,observer);
	}		
    
   	public boolean drawImage(Image img, double x, double y, 
		double w, double h, ImageObserver observer,int num){
		if (back == null) return false;
		int ix = getX(x,num);//Java AWT���W�l�ɕϊ�
		int iy = getY(y,num);//Java AWT���W�l�ɕϊ�
		int iw = getDimensionX(w,num);//getX(w)-getX(0);//�摜�̉���
		int ih = getDimensionY(h,num);//getY(0)-getY(h);//�摜�̏c��
		return back.drawImage(img,ix,iy,iw,ih,observer);
	}
   	
   	public boolean drawImage(Image img, double x, double y, 
		Color bgcolor, ImageObserver observer,int num){
		int ix = getX(x,num);//Java AWT���W�l�ɕϊ�
		int iy = getY(y,num);//Java AWT���W�l�ɕϊ�
		return back.drawImage(img,ix,iy,bgcolor,observer);
	}		
   	public boolean drawImage(Image img, double x, double y, 
		double w, double h, 
		Color bgcolor, ImageObserver observer,int num){
		int ix = getX(x,num);//Java AWT���W�l�ɕϊ�
		int iy = getY(y,num);//Java AWT���W�l�ɕϊ�
		int iw = getDimensionX(w,num);//�摜�̉���
		int ih = getDimensionY(h,num);//�摜�̏c��
		return back.drawImage(img,ix,iy,iw,ih,bgcolor,observer);
	}		
	public boolean drawImage(Image img,
		double dx1, double dy1, double dx2, double dy2,
		double sx1, double sy1, double sx2, double sy2,
		ImageObserver observer,int num){
		int idx1 = getX(dx1,num);//Java AWT���W�l�ɕϊ�
		int idy1 = getY(dy1,num);//Java AWT���W�l�ɕϊ�
		int idx2 = getX(dx2,num);//Java AWT���W�l�ɕϊ�
		int idy2 = getY(dy2,num);//Java AWT���W�l�ɕϊ�
		int isx1 = getX(sx1,num);//Java AWT���W�l�ɕϊ�
		int isy1 = getY(sy1,num);//Java AWT���W�l�ɕϊ�
		int isx2 = getX(sx2,num);//Java AWT���W�l�ɕϊ�
		int isy2 = getY(sy2,num);//Java AWT���W�l�ɕϊ�
		return back.drawImage(img,
			idx1, idy1, idx2, idy2,	isx1, isy1, isx2, isy2, observer);
	}

	public boolean drawImage(Image img,
		double dx1, double dy1, double dx2, double dy2,
		double sx1, double sy1, double sx2, double sy2,
		Color bgcolor, ImageObserver observer,int num){
		int idx1 = getX(dx1,num);//Java AWT���W�l�ɕϊ�
		int idy1 = getY(dy1,num);//Java AWT���W�l�ɕϊ�
		int idx2 = getX(dx2,num);//Java AWT���W�l�ɕϊ�
		int idy2 = getY(dy2,num);//Java AWT���W�l�ɕϊ�
		int isx1 = getX(sx1,num);//Java AWT���W�l�ɕϊ�
		int isy1 = getY(sy1,num);//Java AWT���W�l�ɕϊ�
		int isx2 = getX(sx2,num);//Java AWT���W�l�ɕϊ�
		int isy2 = getY(sy2,num);//Java AWT���W�l�ɕϊ�
		return back.drawImage(img,
			idx1, idy1, idx2, idy2, isx1, isy1, isx2, isy2,
			bgcolor, observer);
	}
	
	// x�̕�����Ԃ����\�b�h
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
	
	
	// �����`��̕ʂ̃��\�b�h
	public void moveTo(double x, double y,int num){
		lastx = x;//�����Ƃ��ŋ߂�X�ʒu���Z�b�g
		lasty = y;//�����Ƃ��ŋ߂�Y�ʒu���Z�b�g
	}
	public void lineTo(double x, double y,int num){
		drawLine(lastx,lasty,x,y,num);//������`��
		lastx = x; lasty = y;//�ŋ߂̈ʒu���X�V
	}
}
