package com.whck.rainer.panel;

import java.util.HashMap;

public class Constants {

	public static HashMap<String, String> cn_en_map = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("��ǿ", "rainintn");
			put("ѹǿ", "pressure");
			put("����", "rainfall");
			put("����", "raintype");
			put("ˮλ", "wtrlevel");
			put("����ʱ", "timedur");
		}
	};
	public static final String[] MODES={"�ֶ�ģʽ","����ģʽ","��ʱģʽ","ͬ���ֶ�","ͬ������","ͬ����ʱ"};
}
