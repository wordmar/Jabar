package code;

public class TurnOverL extends TurnOver {

	public static void main(String[] args) throws Exception {
		String fileName = "D:/temp/case90rol.txt";
		String modifiedFileName = "D:/temp/temp.txt";
		int step = 1;
		boolean rIsWeight = false;
		boolean checkRIsWeight = true;
		new TurnOverL().lRun(fileName, modifiedFileName, step, rIsWeight,
				checkRIsWeight);
	}

	public String lRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight) throws Exception {
		TurnOverL obj = new TurnOverL();
		CalMode = "M";// �p��ϧ@�ΤO�A��M�A��D
		CalVal = 30;// �p��ϧ@�ΤO����
		// TurnOver.CalMode = "D";//�p��ϧ@�ΤO�A��M�A��D
		// TurnOver.CalVal = 1.8f;//�p��ϧ@�ΤO����
		return lRun(fileName, modifiedFileName, step, rIsWeight,
				checkRIsWeight, obj, CalMode, CalVal);
	}

	public String lRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight, TurnOverL obj,
			String calMode, float calVal) throws Exception {
		CalMode = calMode;// �p��ϧ@�ΤO�A��M�A��D
		CalVal = calVal;// �p��ϧ@�ΤO����
		if (step == 0) {
			String orgAvgR = getRreactionAverage(obj, fileName, rIsWeight,
					checkRIsWeight, left);
			return orgAvgR;// getReactionCal(orgAvgR);
		}
		if (step == 1) {
			return produceDataFirstStep(obj, fileName, rIsWeight, left);
		}
		if (step == 2) {
			return produceDataTwoStep(obj, fileName, modifiedFileName,
					rIsWeight);
		}
		// ���ѵ��G���n���ɭԡA�Φײ����w��A�ȦA���o�ӵ{���h�p�⥭����
		if (step == 3) {
			return reProduceXList(obj, fileName, modifiedFileName, rIsWeight);
		}
		// ���ѵ��G���n���ɭԡA�Φײ����w��A�ȦA���o�ӵ{���h�p�⥭���ȨåB�s���򪺤ϧ@�ΤO�]�@�ֺ⧹
		if (step == 4) {
			return reProduceXListAndGetFinalReactionString(obj, fileName,
					modifiedFileName, rIsWeight);
		}
		return "";
	}

	String getPosition(Integer time, String oldDataPosition) {
		String result = null;
		String temp = "";
		int isY = 0;
		if (isA(time)) {
			result = "A";
			temp = temp + result;
			isY++;
		}
		if (isB(time)) {
			result = "B";
			temp = temp + result;
			isY++;
		}
		if (isC(time)) {
			result = "C";
			temp = temp + result;
			isY++;
		}
		if (isD(time)) {
			result = "D";
			temp = temp + result;
			isY++;
		}
		if (isY > 1) {
			result = "Y";
			// ����D�MA�|�{���X�ӡA�N�n�Ϋe�@�Ӧ�m�h�Ϥ��A�åB�b�e�[�@��Y�A�N���T�w
			if (temp.compareTo("AD") == 0) {
				if (oldDataPosition != null
						&& (oldDataPosition.contains("Y") || oldDataPosition
								.contains("C"))) {
					result = "YD";
				}
				if (oldDataPosition != null
						&& (oldDataPosition.contains("Y") || oldDataPosition
								.contains("D"))) {
					result = "YA";
				}
			}
			// ����C�MB�|�{���X�ӡA�N�n�Ϋe�@�Ӧ�m�h�Ϥ��A�åB�b�e�[�@��Y�A�N���T�w
			if (temp.compareTo("BC") == 0) {
				if (oldDataPosition != null
						&& (oldDataPosition.contains("Y") || oldDataPosition
								.contains("B"))) {
					result = "YC";
				}
				if (oldDataPosition != null
						&& (oldDataPosition.contains("Y") || oldDataPosition
								.contains("A"))) {
					result = "YB";
				}
			}
		}
		return result;
	}

	/**
	 * �U�G�I���I�j�A�U�T�I��U�@�I�j�A�U�Q���I���I�j30�A�U35�I��U5�I�j60�A�e��Q���������̤p�A�񤤦�Ƥp
	 * 
	 * @param time
	 * @return
	 */
	private boolean isA(final Integer time) {
		boolean result = false;
		if (biggerThen(time + 2, time, 0) && biggerThen(time + 3, time + 1, 0)
				&& biggerThen(time + 15, time, con30)
				&& biggerThen(time + 35, time + 5, con60) && isMin(time, 10)
				&& lessThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �e30�I���I�j30�A�e��Q���������̤j�A�񤤦�Ƥj
	 * 
	 * @param time
	 * @return
	 */
	private boolean isB(final Integer time) {
		boolean result = false;
		if (lessThen(time - 30, time, con30) && isMax(time, 10)
				&& biggerThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �U3�I���I�p�A�U4�I��U1�I�p�A�U20�I���I�p100�A�e��Q���������̤j�A�񤤦�Ƥj
	 * 
	 * @param time
	 * @return
	 */
	private boolean isC(final Integer time) {
		boolean result = false;
		if (lessThen(time + 3, time, 0) && lessThen(time + 4, time + 1, 0)
				&& lessThen(time + 20, time, con100) && isMax(time, 10)
				&& biggerThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �e15�I���I�j30�A�e20�I��e5�I�j30�A�e��Q���������̤p�A�񤤦�Ƥp
	 * 
	 * @param time
	 * @return
	 */
	private boolean isD(final Integer time) {
		boolean result = false;
		if (biggerThen(time - 15, time, con30)
				&& biggerThen(time - 20, time - 5, con30) && isMin(time, 5)
				&& lessThenMedian(time)) {
			return true;
		}
		return result;
	}

}
