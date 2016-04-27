/*
 * �쐬��: 2005/9/10
 *
 * Copyright 2005 Embedded.Samurai, Inc. All rights reserved.
 *
 * $Id: Key.java,v 1.1 2005/9/10 06:11:11 esamurai Exp $
 */




/**
	* �C���X�^���X�����Ȃ��ꍇ��final�N���X
	*/
public final class Key{
	
	public static final int KEY_0 = 0;
	public static final int KEY_1 = 1;
	public static final int KEY_2 = 2;
	public static final int KEY_3 = 3;
	public static final int KEY_4 = 4;
	public static final int KEY_5 = 5;
	public static final int KEY_6 = 6;
	public static final int KEY_7 = 7;
	public static final int KEY_8 = 8;
	public static final int KEY_9 = 9;
	public static final int KEY_LEFT  = 16;
	public static final int KEY_UP    = 17;
	public static final int KEY_RIGHT = 18;
	public static final int KEY_DOWN  = 19;
	public static final int KEY_SPACE = 20;
	public static final int KEY_ENTER = 21;


	/**
	 * �L�[�擾�p�t���O.
	 * <p>
	 * keyFlag[0]: �L�[�������ꂽ���Ƃ��L������B<br>
	 * keyFlag[1]: �L�[�������ꂽ���Ƃ̔���Ɏg���B<br>
	 * keyFlag[2]: �L�[�������ꂽ���Ƃ��L������B<br>
	 * keyFlag[3]: �L�[�����������ꂽ���Ƃ̔���Ɏg���B<br>
	 * </p>
	 */
	public static long keyFlag[] = new long[4];
	public static long mouseX;
	public static long mouseY;
	public static boolean isPressMouse = false;

	/**
		* �R���X�g���N�^
		*/
	public Key(){
		for(int i=0;i<4;i++) keyFlag[i] = 0;
		
		mouseX=0;
		mouseY=0;
		isPressMouse = false;
	}

	/**
		* ������
		*/
	public static void init(){
		// �L�[�̏�����
		keyFlag[0] = 0;
		keyFlag[2] = 0;
	}

	/**
	 * �L�[������o�^���܂��B
	 * <br>
	 */
	public static void registKeyEvent() {
		keyFlag[1] = keyFlag[0];
		keyFlag[3] = keyFlag[2];
		keyFlag[0] = 0;
	}

	/**
	 * �L�[�������f�B
	 * @param key - �L�[�R�[�h
	 */
	public static boolean isKeyPressed(int key) {
		if ((keyFlag[1] & (1L << key)) != 0) {
			return true;
		}
		return false;
	}

	/**
		* �L�[���������f�B
		* @param key - �L�[�R�[�h
		*/
	public static boolean isKeyRepeated(int key) {
		if ((keyFlag[3] & (1L << key)) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * �L�[�����[�X���f
	 * @param key - �L�[�R�[�h
	 */
	public static boolean isKeyReleased(int key){
		if ((keyFlag[2] & (1L << key)) == 0) {
			return true;
		}
		return false;
	}

	/**
		* �L�[���Ƃ肠����������Ă��邩�ǂ����m���߂�B<BR>
		* ���̏ꍇ�A�����ƒ������̗������܂܂��B
		* @param key - �L�[�R�[�h
		* @return <lo><li>true - �Ƃ肠����������Ă���<li>false - �G���Ă��炢�Ȃ�</lo>
		*/
	public static boolean isKeyPressOrRepeated(int key){
		return isKeyPressed(key) || isKeyRepeated(key);
	}

	/**
		* �L�[��������Ă��邩�A���s�[�g����Ă����true��Ԃ��B<BR>
		* ������keyWaitTimer��keyRepeatWait�ɂ���Ă���킳���<BR>
		* ���s�[�g�Ԋu��true���A��B<BR>
		* �܂�isKeyPressed()�ɔ��������Ƃ���keyWaitTimer�͈Ӗ��𐬂��Ȃ��B
		* ����͉������ςȂ��ɂ���Ƃ�邭�J�[�\�����������A
		* �L�[�A�ł���ƃJ�[�\����������������Ƃ����@�\�ɑ�������B
		*/

	private static int keyWaitTimer;
	private static int keyRepeatWait = 250;

	public static boolean isKeyPressOrRepeatedWithWait(int key){
		if(isKeyPressed(key)){
			keyWaitTimer = 0;
			return true;
		}else if(isKeyRepeated(key)){
			if(keyWaitTimer > keyRepeatWait){
				keyWaitTimer = 0;
				return true;
			}
		}
		return false;
	}

}//end of Class
