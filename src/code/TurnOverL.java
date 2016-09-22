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
		CalMode = "M";// 計算反作用力，乘M，除D
		CalVal = 30;// 計算反作用力的值
		// TurnOver.CalMode = "D";//計算反作用力，乘M，除D
		// TurnOver.CalVal = 1.8f;//計算反作用力的值
		return lRun(fileName, modifiedFileName, step, rIsWeight,
				checkRIsWeight, obj, CalMode, CalVal);
	}

	public String lRun(String fileName, String modifiedFileName, int step,
			boolean rIsWeight, boolean checkRIsWeight, TurnOverL obj,
			String calMode, float calVal) throws Exception {
		CalMode = calMode;// 計算反作用力，乘M，除D
		CalVal = calVal;// 計算反作用力的值
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
			// 有時D和A會認不出來，就要用前一個位置去區分，並且在前加一個Y，代表不確定
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
			// 有時C和B會認不出來，就要用前一個位置去區分，並且在前加一個Y，代表不確定
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
	 * 下二點比本點大，下三點比下一點大，下十五點比本點大30，下35點比下5點大60，前後十筆間本筆最小，比中位數小
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
	 * 前30點比本點大30，前後十筆間本筆最大，比中位數大
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
	 * 下3點比本點小，下4點比下1點小，下20點比本點小100，前後十筆間本筆最大，比中位數大
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
	 * 前15點比本點大30，前20點比前5點大30，前後十筆間本筆最小，比中位數小
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
