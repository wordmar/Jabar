package code;

public class TurnOverR extends TurnOver {

	public void main(String[] args) throws Exception {
		String fileName = "D:/temp/case101ror.txt";
		String modifiedFileName = "D:/temp/temp.txt";
		int step = 1;
		boolean rIsWeight = false;
		boolean checkRIsWeight = true;
		new TurnOverR().rRun(fileName, modifiedFileName, step, rIsWeight,
				checkRIsWeight);
	}

	public String rRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight) throws Exception {
		TurnOverR obj = new TurnOverR();
		CalMode = "M";// �p��ϧ@�ΤO�A��M�A��D
		CalVal = 30;// �p��ϧ@�ΤO����
		// CalMode = "D";//�p��ϧ@�ΤO�A��M�A��D
		// CalVal = 1.8f;//�p��ϧ@�ΤO����
		return rRun(fileName, modifiedFileName, step, rIsWeight, checkRIsWeight, obj, CalMode,
				CalVal);
	}

	public String rRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight, TurnOverR obj,
			String calMode, float calVal) throws Exception {
		CalMode = calMode;// �p��ϧ@�ΤO�A��M�A��D
		CalVal = calVal;// �p��ϧ@�ΤO����
		if (step == 0) {
			String orgAvgR = getRreactionAverage(obj, fileName, rIsWeight, checkRIsWeight, right);
			return orgAvgR;//getReactionCal(orgAvgR);
		}
		if (step == 1) {
			return produceDataFirstStep(obj, fileName, rIsWeight, right);
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
		if (isCA(time)) {
			result = "C";
			temp = temp + result;
			isY++;
		}
		if (isDB(time)) {
			result = "D";
			temp = temp + result;
			isY++;
		}
		if (isAC(time)) {
			result = "A";
			temp = temp + result;
			isY++;
		}
		if (isBD(time)) {
			result = "B";
			temp = temp + result;
			isY++;
		}
		if (isY > 1) {
			result = "Y";
			// ����D�MA�|�{���X�ӡA�N�n�Ϋe�@�Ӧ�m�h�Ϥ��A�åB�b�e�[�@��Y�A�N���T�w
			if (temp.compareTo("DA") == 0) {
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
			if (temp.compareTo("CB") == 0) {
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
	 * �쥻�b��½�ɡA�Oa�I�A�b�k½���A�t��k�ܦ�c�I�F �U�G�I���I�j�A�U�T�I��U�@�I�j�A�U�Q���I���I�j100�A�e��Q���������̤p�A�񤤦�Ƥp
	 * 
	 * @param time
	 *            �ɶ��s��
	 * @return
	 */
	private boolean isCA(final Integer time) {
		boolean result = false;
		if (biggerThen(time + 2, time, 0) && biggerThen(time + 3, time + 1, 0)
		// �H�U�G��Υ��Ӫ���k�椣�q�A�令�{�b�o�ˤ~��A���D�h���ª��ݦ椣��
				&& biggerThen(time + 15, time, con100)
				// && biggerThen(time + 15, time, con30)
				// && biggerThen(time + 35, time + 5, con60)
				&& isMin(time, 10) && lessThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �쥻�b��½�ɡA�Ob�I�A�b�k½���A�t��k�ܦ�d�I�F �e�T�Q�I���I�p�T�Q�A�e��Q���������̤j�A�񤤦�Ƥj
	 * 
	 * @param time
	 *            �ɶ��s��
	 * @return
	 */
	private boolean isDB(final Integer time) {
		boolean result = false;
		if (lessThen(time - 30, time, con30) && isMax(time, 10)
				&& biggerThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �쥻�b��½�ɡA�Oc�I�A�b�k½���A�t��k�ܦ�a�I�F �U�T�I���I�p�A�U�|�I��U�@�I�p�A�U�G�Q�I���I�p�@�ʡA�e��Q���������̤j�A�񤤦�Ƥj
	 * 
	 * @param time
	 *            �ɶ��s��
	 * @return
	 */
	private boolean isAC(final Integer time) {
		boolean result = false;
		if (lessThen(time + 3, time, 0) && lessThen(time + 4, time + 1, 0)
				&& lessThen(time + 20, time, con100) && isMax(time, 10)
				&& biggerThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * �쥻�b��½�ɡA�Od�I�A�b�k½���A�t��k�ܦ�b�I�F �e�Q���I���I�j�T�Q�A�e�G�Q�I��e���I�j�T�Q�A�e��Q���������̤p�A�񤤦�Ƥp
	 * 
	 * @param time
	 *            �ɶ��s��
	 * @return
	 */
	private boolean isBD(final Integer time) {
		boolean result = false;
		if (biggerThen(time - 15, time, con30)
				&& biggerThen(time - 20, time - 5, con30) && isMin(time, 5)
				&& lessThenMedian(time)) {
			return true;
		}
		return result;
	}
}
