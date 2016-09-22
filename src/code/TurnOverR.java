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
		CalMode = "M";// 計算反作用力，乘M，除D
		CalVal = 30;// 計算反作用力的值
		// CalMode = "D";//計算反作用力，乘M，除D
		// CalVal = 1.8f;//計算反作用力的值
		return rRun(fileName, modifiedFileName, step, rIsWeight, checkRIsWeight, obj, CalMode,
				CalVal);
	}

	public String rRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight, TurnOverR obj,
			String calMode, float calVal) throws Exception {
		CalMode = calMode;// 計算反作用力，乘M，除D
		CalVal = calVal;// 計算反作用力的值
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
		// 辨識結果不好的時候，用肉眼先定位，值再讓這個程式去計算平均值
		if (step == 3) {
			return reProduceXList(obj, fileName, modifiedFileName, rIsWeight);
		}
		// 辨識結果不好的時候，用肉眼先定位，值再讓這個程式去計算平均值並且連後續的反作用力也一併算完
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
			// 有時D和A會認不出來，就要用前一個位置去區分，並且在前加一個Y，代表不確定
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
			// 有時C和B會認不出來，就要用前一個位置去區分，並且在前加一個Y，代表不確定
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
	 * 原本在左翻時，是a點，在右翻中，演算法變成c點了 下二點比本點大，下三點比下一點大，下十五點比本點大100，前後十筆間本筆最小，比中位數小
	 * 
	 * @param time
	 *            時間編號
	 * @return
	 */
	private boolean isCA(final Integer time) {
		boolean result = false;
		if (biggerThen(time + 2, time, 0) && biggerThen(time + 3, time + 1, 0)
		// 以下二行用本來的方法行不通，改成現在這樣才行，除非去改舊的看行不行
				&& biggerThen(time + 15, time, con100)
				// && biggerThen(time + 15, time, con30)
				// && biggerThen(time + 35, time + 5, con60)
				&& isMin(time, 10) && lessThenMedian(time)) {
			return true;
		}
		return result;
	}

	/**
	 * 原本在左翻時，是b點，在右翻中，演算法變成d點了 前三十點比本點小三十，前後十筆間本筆最大，比中位數大
	 * 
	 * @param time
	 *            時間編號
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
	 * 原本在左翻時，是c點，在右翻中，演算法變成a點了 下三點比本點小，下四點比下一點小，下二十點比本點小一百，前後十筆間本筆最大，比中位數大
	 * 
	 * @param time
	 *            時間編號
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
	 * 原本在左翻時，是d點，在右翻中，演算法變成b點了 前十五點比本點大三十，前二十點比前五點大三十，前後十筆間本筆最小，比中位數小
	 * 
	 * @param time
	 *            時間編號
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
