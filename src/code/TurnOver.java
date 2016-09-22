package code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class TurnOver {

	private static final Integer getAvgFromMedianRange = 4;
	final int left = 1;
	final int right = 2;
	private final int FirstStopTimeRange = 5;
	static String CalMode = null;
	static float CalVal = 0;
	ArrayList<TurnOverDataSrcBean> turnOverValList = new ArrayList<TurnOverDataSrcBean>();
	ArrayList<Float> turnOverXValList = new ArrayList<Float>();
	ArrayList<Float> turnOverReactionValList = new ArrayList<Float>();
	HashMap<Integer, TurnOverDataSrcBean> hm = new HashMap<Integer, TurnOverDataSrcBean>();
	ArrayList<TurnOverResultBean> positionList = new ArrayList<TurnOverResultBean>();
	ArrayList<TurnOverResultBean> readyList = new ArrayList<TurnOverResultBean>();
	ArrayList<TurnOverResultBean> finalXList = new ArrayList<TurnOverResultBean>();
	protected final Integer con30 = 30;
	protected final Integer con60 = 60;
	protected final Integer con100 = 100;
	// 全部資料的中位數，幫助辨認是波峰還是波谷(ad或bc)
	Float median;
	protected Float avgR;

	public Float getAvgR() {
		return avgR;
	}

	public Float getAvgRCal() {
		return Float.valueOf(getReactionCal(avgR.toString()));
	}

	
	/**
	 * 算反作用力，從A->B,C->D的時間間距中去找最大值，並找到最大值的時間點，視情況調整原本起動的時間，才能供後面excel計算時間差
	 * 不另外再加到新容器了，直接用finalXList
	 */
	protected void produceFinalList() {
		String dataPosition = null;
		String oldDataPosition = null;
		Integer time = null;
		Integer oldTime = null;
		int count = 0;
		for (TurnOverResultBean resultBean : finalXList) {
			dataPosition = resultBean.getPosition();
			time = Integer.parseInt(resultBean.getTime());
			resultBean.setReaction("");
			resultBean.setMaxReactionTime("-1");
			// 第一筆先保留原狀
			if (count == 0) {
				// finalList.add(new TurnOverResultBean(resultBean.getTime(),
				// resultBean.getPosition(), resultBean.getOriginalX()));
			} else {
				if (!dataPosition.contains("X")) {
					// B,D及可能是B,D的Y(前面為A,C)
					if ((dataPosition.contains("B")
							|| dataPosition.contains("D") || (dataPosition
							.contains("Y") && (oldDataPosition.contains("A")) || oldDataPosition
								.contains("C")))) {
						if (!oldDataPosition.contains("X")) {
							// 上一筆跟本筆的時間距離中找出最大的反作用力，後來發現常常會出現在前1、2、3點有最大反作用力，所以-3開始找
							TurnOverDataSrcBean maxReactionBean = getMaxReaction(
									oldTime - 3, time);
							float maxReaction = maxReactionBean.getReaction();
							int maxReactionTime = maxReactionBean.getTime();
							// 前一點，應該是A,C，也就是計算的起始點
							// 起始點的最大反作用力設定成相同，這樣在excel的圖形中才會看起來是平的
							finalXList.get(count - 1).setReaction(
									String.valueOf(maxReaction));
							// 起始點的時間，這樣excel才能減
							finalXList.get(count - 1).setMaxReactionTime(
									finalXList.get(count - 1).getTime());
							// 當最大反作用力出現在開始翻身的x位移極值之前，就用最大反作用力發生的時間
							if (maxReactionTime < Integer.parseInt((finalXList
									.get(count - 1).getTime()))) {
								finalXList.get(count - 1).setTime(
										String.valueOf(maxReactionTime));
							}
							resultBean.setReaction(String.valueOf(maxReaction));
							resultBean.setMaxReactionTime(String
									.valueOf(maxReactionTime));
						}
						if (oldDataPosition.contains("X")) {
							// 不做事
						}
					}
					if (oldDataPosition.contains("X")) {
						// 不做事
					}
				} else {// 當目前位置判斷不出來，為x時
					if ((oldDataPosition.contains("A")
							|| oldDataPosition.contains("C") || oldDataPosition
								.contains("Y"))) {
						// 不做事
					}
					// 不做事
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
	}

	/**
	 * 算反作用力，從A->B,C->D的時間間距中去找最大值，並找到最大值的時間點，才能供後面excel計算時間差
	 * 不另外再加到新容器了，直接用finalXList
	 */
	protected void reProduceFinalXList(ArrayList<TurnOverResultBean> tempXList) {
		String dataPosition = null;
		String oldDataPosition = null;
		Float turnOverVal = null;
		Integer time = null;
		Integer oldTime = null;
		int count = 0;
		for (TurnOverResultBean resultBean : tempXList) {
			dataPosition = resultBean.getPosition();
			time = Integer.parseInt(resultBean.getTime());
			turnOverVal = Float.parseFloat(resultBean.getOriginalX());
			// 第一筆先保留原狀
			if (count == 0) {
				time = Integer.parseInt(resultBean.getTime());
				float newTurnOverVal = getAverage(0, time,
						getAvgFromMedianRange, turnOverXValList);
				finalXList.add(new TurnOverResultBean(resultBean.getTime(),
						resultBean.getPosition(), String
								.valueOf(newTurnOverVal)));
			} else {
				if (!dataPosition.contains("X")) {
					// B,D及可能是B,D的Y(前面為A,C)
					if ((dataPosition.contains("A")
							|| dataPosition.contains("C") || (dataPosition// 可能是A,C的Y
							.contains("Y") && (oldDataPosition.contains("B")) || oldDataPosition
								.contains("D")))) {
						if (!oldDataPosition.contains("X")) {
							float newTurnOverVal = getAverage(oldTime, time,
									getAvgFromMedianRange, turnOverXValList);
							finalXList.add(new TurnOverResultBean(String
									.valueOf(oldTime), oldDataPosition, String// 本來firstStopTime是填oldTime
									.valueOf(newTurnOverVal)));
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, String
									.valueOf(newTurnOverVal)));
						}
						if (oldDataPosition.contains("X")) {
							// 不做事
						}
					}
					if (oldDataPosition.contains("X")) {
						// 不做事
					}
				} else {// 當目前位置判斷不出來，為x時
					if ((oldDataPosition.contains("A")
							|| oldDataPosition.contains("C") || oldDataPosition
								.contains("Y"))) {
						// 不做事
					}
					// 不做事
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
		// 最後一筆
		finalXList.add(new TurnOverResultBean(String.valueOf(oldTime),
				oldDataPosition, String.valueOf(turnOverVal)));

	}

	/**
	 * 從readyList中再去算DA,BC二點間的平均數(會利用中位數把極值去掉)，重新賦予每個點的值
	 * 因為要在excel中整理，所以有些有問題的值要設0，波峰波谷的值也一起歸0，在excel中比較好調
	 * 另外從原來用極值判斷BD再算真正的BD點，從B->
	 * C,D->A的時間間距中去找振幅開始減少的起始點(代表真正停止不動了)，並找到振幅最小起始值的時間點， 才是真正定義上的完成翻身(把動作做完)
	 * 不另外再加到新容器了，直接用finalXList
	 */
	protected void produceFinalXList() {
		String dataPosition = null;
		String oldDataPosition = null;
		Float turnOverVal = null;
		Integer time = null;
		// 用尖峰值取出的時間
		Integer oldTime = null;
		int count = 0;
		for (TurnOverResultBean resultBean : readyList) {
			dataPosition = resultBean.getPosition();
			// 第一筆先處理一下
			if (count == 0) {
				time = Integer.parseInt(resultBean.getTime());
				float newTurnOverVal = getAverage(0, time,
						getAvgFromMedianRange, turnOverXValList);
				finalXList.add(new TurnOverResultBean(resultBean.getTime(),
						resultBean.getPosition(), String
								.valueOf(newTurnOverVal)));
			} else {
				if (!dataPosition.contains("X")) {
					// 取time這一行放在外面，X的會抓不到
					time = Integer.parseInt(resultBean.getTime());
					turnOverVal = Float.parseFloat(resultBean.getOriginalX());
					// A,C及可能是A,C的Y(前面為B,D)
					if ((dataPosition.contains("A")
							|| dataPosition.contains("C") || (dataPosition// 可能是A,C的Y
							.contains("Y") && (oldDataPosition.contains("B")) || oldDataPosition
								.contains("D")))) {
						// 前一筆不為x
						if (!oldDataPosition.contains("X")) {
							float newTurnOverVal = getAverage(oldTime, time,
									getAvgFromMedianRange, turnOverXValList);
							// 取得BD尖峰值後第一個躺平的時間，取代BD原本用尖峰值的時間(連續3點都很靠近平均值才算真正停止了)
							Integer firstStopTime = getFirstStopTime(
									newTurnOverVal, FirstStopTimeRange,
									oldTime, time);
							finalXList.add(new TurnOverResultBean(String
									.valueOf(firstStopTime), oldDataPosition,
									String// 本來firstStopTime是填oldTime
									.valueOf(newTurnOverVal)));
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, String
									.valueOf(newTurnOverVal)));
						}
						// 前一筆是x，本筆設0，在excel中比較好調
						if (oldDataPosition.contains("X")) {
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, "0"));
						}
					}
				} else {// 當目前位置判斷不出來，為x時
					// 如果前一筆是B或D或Y(前一點要一起取平均的起始點)，把前一筆的值改為0，再加入finalXList，在EXCEL中比較好調
					if ((oldDataPosition.contains("B")
							|| oldDataPosition.contains("D") || oldDataPosition
								.contains("Y"))) {
						finalXList.add(new TurnOverResultBean(String
								.valueOf(oldTime), oldDataPosition, "0"));
					}
					// X的time及值要放-1及空字串
					finalXList.add(new TurnOverResultBean("-1", dataPosition,
							""));
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
		// 最後一筆
		time = turnOverValList.get(turnOverValList.size() - 1).getTime();
		float newTurnOverVal = getAverage(oldTime, time, getAvgFromMedianRange,
				turnOverXValList);
		Integer firstStopTime = getFirstStopTime(newTurnOverVal,
				FirstStopTimeRange, oldTime, time);
		finalXList.add(new TurnOverResultBean(String.valueOf(firstStopTime),
				oldDataPosition, String.valueOf(newTurnOverVal)));
	}

	/**
	 * 從粗胚positionList再進一步篩選重覆的、辨認錯誤的，有缺的整理一下，放到readyList
	 * 
	 * @param rightOrLeft
	 *            1為左2為右
	 */
	protected void processReadyList(int rightOrLeft) {
		String dataPosition = null;
		String oldDataPosition = null;
		Float turnOverVal = null;
		Float oldTurnOverVal = 0f;
		Integer time = null;
		Integer oldTime = null;
		boolean isCorrectChange = false;
		int count = 0;
		for (TurnOverResultBean resultBean : positionList) {
			time = Integer.parseInt(resultBean.getTime());
			dataPosition = resultBean.getPosition();
			turnOverVal = Float.parseFloat(resultBean.getOriginalX());
			// 第一筆先處理一下
			if (count == 0) {
				oldDataPosition = dataPosition;
				oldTurnOverVal = turnOverVal;
				oldTime = time;
			}
			isCorrectChange = isCorrectChange(oldDataPosition, dataPosition);
			// 如果二筆在粗胚的時候判斷是相同位置，依波峰或波谷會判斷要用比較大還是比較小的值
			// (為什麼本來要用AC,BD而不是AD,BC，目前忘了原因，但應該是AD,BC較合理)
			// (除非因為AC,BD前面都是平的，或者它都是屬於線段的終點？)
			if (isTheSamePosition(dataPosition, oldDataPosition)) {
				if (rightOrLeft == left
						&& (dataPosition.contains("A") || dataPosition
								.contains("D"))) {
					if (turnOverVal < oldTurnOverVal) {
						oldTurnOverVal = turnOverVal;
						oldTime = time;
					}
				}
				if (rightOrLeft == left
						&& (dataPosition.contains("B") || dataPosition
								.contains("C"))) {
					if (turnOverVal > oldTurnOverVal) {
						oldTurnOverVal = turnOverVal;
						oldTime = time;
					}
				}

				if (rightOrLeft == right
						&& (dataPosition.contains("A") || dataPosition
								.contains("D"))) {
					if (turnOverVal > oldTurnOverVal) {
						oldTurnOverVal = turnOverVal;
						oldTime = time;
					}
				}
				if (rightOrLeft == right
						&& (dataPosition.contains("B") || dataPosition
								.contains("C"))) {
					if (turnOverVal < oldTurnOverVal) {
						oldTurnOverVal = turnOverVal;
						oldTime = time;
					}
				}
			} else if (isCorrectChange) {// 這行感覺應該是可以獨立於上一個if，但不想試驗了
				readyList.add(new TurnOverResultBean(String.valueOf(oldTime),
						oldDataPosition, String.valueOf(oldTurnOverVal)));
			} else {
				// 中間有跳，把前一筆一樣放進去，然後再加一筆空白的
				readyList.add(new TurnOverResultBean(String.valueOf(oldTime),
						oldDataPosition, String.valueOf(oldTurnOverVal)));
				readyList.add(new TurnOverResultBean(String.valueOf(""), "X",
						""));
			}
			oldDataPosition = dataPosition;
			oldTurnOverVal = turnOverVal;
			oldTime = time;
			count++;
		}
		// 最後一筆
		readyList.add(new TurnOverResultBean(String.valueOf(oldTime),
				oldDataPosition, String.valueOf(oldTurnOverVal)));
	}

	/**
	 * 從turnOverValList原始資料中一筆一筆初步辨認圖形的極值ABCD點，若不確定的，前面會加上Y 放到positionList中
	 */
	protected void preparePositionList() {
		Integer time = 0;
		String dataPosition = null;
		String oldDataPosition = "D";// 因為要從a點開始，所以設a點前一點的初始值為d
		for (TurnOverDataSrcBean srcBean : turnOverValList) {
			dataPosition = getPosition(time, oldDataPosition);// 這是個template
																// method
			if (dataPosition != null) {
				positionList.add(new TurnOverResultBean(String.valueOf(time),
						dataPosition, String.valueOf(srcBean.getX())));
				oldDataPosition = dataPosition;// 要dataPosition != null才有意義
			}
			time++;
		}
		if (positionList.size() == 0) {
			throw new RuntimeException("資料型態無法判斷");
		}
	}

	// 一律不對反作用力做檢查
	protected void prepareDataFromFile(String fileName, boolean rIsWeight)
			throws Exception {
		prepareDataFromFile(fileName, rIsWeight, false);
	}

	/**
	 * 把資料從原始檔讀入，放到一些容器中，讓後面的程式使用，並且計算中位數
	 * 
	 * @param fileName
	 * @param rIsWeight
	 *            TODO
	 * @throws Exception
	 */
	protected void prepareDataFromFile(String fileName, boolean rIsWeight,
			boolean checkRIsWeight) throws Exception {
		ArrayList<String[]> turnOverList = readFile(fileName);
		Integer time = 0;
		TurnOverDataSrcBean srcBean;
		for (String[] turnOverVal : turnOverList) {
			srcBean = new TurnOverDataSrcBean();
			srcBean.setX(new Float(turnOverVal[0]));
			srcBean.setY(new Float(turnOverVal[1]));
			srcBean.setReaction(new Float(turnOverVal[2]));
			srcBean.setTime(time);
			if (checkRIsWeight) {
				// 簡單的檢查，不為體重卻大於10或(是體重但小於10或大於120)，丟出例外
				if ((!rIsWeight && srcBean.getReaction() > 10)
						|| (rIsWeight && (srcBean.getReaction() < 10 || srcBean
								.getReaction() > 120))) {
					throw new RuntimeException("反作用力為：" + srcBean.getReaction()
							+ "，檢查反作用力是否為體重？");
				}
			}
			hm.put(time, srcBean);
			turnOverValList.add(srcBean);
			turnOverXValList.add(srcBean.getX());
			turnOverReactionValList.add(srcBean.getReaction());
			time++;
		}
		median = getMedian(turnOverXValList);
	}

	/**
	 * 把資料從原始檔讀入，放到一些容器中，讓後面的程式使用，並且計算中位數
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	protected void prepareDataFromString(String fileName) throws Exception {
		ArrayList<String[]> turnOverList = readFile(fileName);
		Integer time = 0;
		TurnOverDataSrcBean srcBean;
		for (String[] turnOverVal : turnOverList) {
			srcBean = new TurnOverDataSrcBean();
			srcBean.setX(new Float(turnOverVal[0]));
			srcBean.setY(new Float(turnOverVal[1]));
			srcBean.setReaction(new Float(turnOverVal[2]));
			srcBean.setTime(time);
			hm.put(time, srcBean);
			turnOverValList.add(srcBean);
			turnOverXValList.add(srcBean.getX());
			turnOverReactionValList.add(srcBean.getReaction());
			time++;
		}
		median = getMedian(turnOverXValList);
	}

	private boolean isTheSamePosition(String dataPosition,
			String oldDataPosition) {
		dataPosition = dataPosition.length() == 2 ? dataPosition.substring(1)
				: dataPosition;
		oldDataPosition = oldDataPosition.length() == 2 ? oldDataPosition
				.substring(1) : oldDataPosition;
		if (dataPosition.compareTo(oldDataPosition) == 0) {
			return true;
		}

		return false;
	}

	/**
	 * AB或BC或CD或DA的組合代表正常
	 * 
	 * @param oldDataPosition
	 * @param dataPosition
	 * @return
	 */
	private boolean isCorrectChange(String oldDataPosition, String dataPosition) {
		if ((oldDataPosition.contains("A") && dataPosition.contains("B"))
				|| (oldDataPosition.contains("B") && dataPosition.contains("C"))
				|| (oldDataPosition.contains("C") && dataPosition.contains("D"))
				|| (oldDataPosition.contains("D") && dataPosition.contains("A"))) {
			return true;
		}
		// 本筆是Y就跳過
		if (oldDataPosition.contains("Y") || dataPosition.contains("Y")) {
			return true;
		}
		return false;
	}

	abstract String getPosition(Integer time, String oldDataPosition);

	protected boolean biggerThen(Integer time1, Integer time2,
			Integer compareVal) {
		boolean result = false;
		if (time1 > 0 && time2 > 0 && time1 < turnOverValList.size()
				&& time2 < turnOverValList.size()) {
			return (turnOverValList.get(time1).getX() - turnOverValList.get(
					time2).getX()) > compareVal;
		}
		return result;
	}

	protected boolean lessThen(Integer time1, Integer time2, Integer compareVal) {
		boolean result = false;
		if (time1 > 0 && time2 > 0 && time1 < turnOverValList.size()
				&& time2 < turnOverValList.size()) {
			return (turnOverValList.get(time2).getX() - turnOverValList.get(
					time1).getX()) > compareVal;
		}
		return result;
	}

	protected boolean biggerThenMedian(Integer time) {
		return turnOverValList.get(time).getX() > median;
	}

	protected boolean lessThenMedian(Integer time) {
		return turnOverValList.get(time).getX() < median;
	}

	private TurnOverDataSrcBean getMaxReaction(Integer oldTime, Integer time) {
		TurnOverDataSrcBean max = Collections.max(
				turnOverValList.subList(oldTime, time + 1),
				new Comparator<TurnOverDataSrcBean>() {
					@Override
					public int compare(TurnOverDataSrcBean b1,
							TurnOverDataSrcBean b2) {
						if (b1.getReaction() - b2.getReaction() == 0) {
							return 0;
						}
						return b1.getReaction() - b2.getReaction() > 0 ? 1 : -1;
					}
				});
		return max;
	}

	/**
	 * 連續3點都很靠近平均值才算真正停止了
	 * 
	 * @param average
	 * @param range
	 * @param startPosition
	 * @param endPosition
	 * @return
	 */
	private Integer getFirstStopTime(Float average, Integer range,
			Integer startPosition, Integer endPosition) {
		ArrayList<TurnOverDataSrcBean> cp = new ArrayList<TurnOverDataSrcBean>();
		cp.addAll(turnOverValList.subList(startPosition, endPosition + 1));
		Integer firstStopPointTime = -1;
		float x = -1;
		float nextX = -1;
		float next2X = -1;

		// 為了不要out of index，而且取到最後也沒用，所以-2
		TurnOverDataSrcBean srcData = null;
		for (int i = 0; i < cp.size() - 2; i++) {
			srcData = cp.get(i);
			x = cp.get(i).getX();
			nextX = cp.get(i + 1).getX();
			next2X = cp.get(i + 2).getX();
			if ((Math.abs(x - average) < range)
					&& (Math.abs(nextX - average) < range)
					&& (Math.abs(next2X - average) < range)) {
				return srcData.getTime();
			}
		}

		return firstStopPointTime;
	}

	protected boolean isMax(Integer time, Integer range) {
		boolean result = false;
		if (time - range > 0 && time + range < turnOverXValList.size()) {
			Float max = Collections.max(turnOverXValList.subList(time - range,
					time + range + 1));
			return turnOverXValList.get(time) == max;
		}
		return result;
	}

	protected boolean isMin(Integer time, Integer range) {
		boolean result = false;
		if (time - range > 0 && time + range < turnOverXValList.size()) {
			Float min = Collections.min(turnOverXValList.subList(time - range,
					time + range + 1));
			return turnOverXValList.get(time) == min;
		}
		return result;
	}

	private Float getMedian(List<Float> srcList) {
		ArrayList<Float> cp = new ArrayList<Float>(srcList.size());
		cp.addAll(srcList);
		Collections.sort(cp);
		int middle = cp.size() / 2;
		if (cp.size() % 2 == 1) {
			return cp.get(middle);
		} else {
			return ((cp.get(middle - 1) + cp.get(middle)) / 2f);
		}
	}

	private Float getAverage(Integer startPosition, Integer endPosition,
			Integer range, ArrayList<Float> src) {
		ArrayList<Float> cp = new ArrayList<Float>();
		cp.addAll(src.subList(startPosition, endPosition + 1));
		Float median = getMedian(cp);
		Float data;
		for (int i = cp.size() - 1; i >= 0; i--) {
			data = cp.get(i);
			if (data > median + range || data < median - range) {
				cp.remove(i);
			}
		}
		Float result = calculateAverage(cp);
		return result;

	}

	private Float calculateAverage(List<Float> list) {
		Float sum = 0f;
		for (Float mark : list) {
			sum += mark;
		}
		return list.isEmpty() ? 0 : 1f * sum / list.size();
	}

	public Float calculateRreactionAverage() {
		avgR = calculateAverage(turnOverReactionValList);
		return avgR;
	}

	public ArrayList<String[]> readFile(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			ArrayList<String[]> sb = new ArrayList<String[]>();
			String line = br.readLine();

			while (line != null) {
				String[] splitLine = line.split("	");
				sb.add(splitLine);
				line = br.readLine();
			}
			return sb;
		} finally {
			br.close();
		}

	}

	public ArrayList<String[]> readString(String content) throws Exception {
		BufferedReader br = new BufferedReader(new StringReader(content));
		try {
			ArrayList<String[]> sb = new ArrayList<String[]>();
			String line = br.readLine();

			while (line != null) {
				String[] splitLine = line.split("	");
				sb.add(splitLine);
				line = br.readLine();
			}
			return sb;
		} finally {
			br.close();
		}

	}

	protected ArrayList<TurnOverResultBean> readModifiedFinalXList(
			String fileName) throws Exception {
		// ArrayList<String[]> turnOverList = readFile(fileName);
		ArrayList<String[]> turnOverList = readString(fileName);
		ArrayList<TurnOverResultBean> modifiedFinalXList = new ArrayList<TurnOverResultBean>();
		TurnOverResultBean bean;
		for (String[] excelModifiedVal : turnOverList) {
			bean = new TurnOverResultBean(excelModifiedVal[1],
					excelModifiedVal[0], excelModifiedVal[2]);
			modifiedFinalXList.add(bean);
		}
		return modifiedFinalXList;
	}

	public boolean writeText(String text, String filename, String format,
			boolean append) {
		File file = new File(filename);// 建立檔案，準備寫檔
		try {
			BufferedWriter bufWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, append),
							format));
			bufWriter.write(text);
			bufWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(filename + "寫檔發生錯誤");
			return false;
		}
		return true;
	}

	/**
	 * 列印處理x位置後的資料，使可以貼到excel，做肉眼的識別 當原始資料的反作用力是體重時要傳true，若不是，要乘30
	 * 
	 * @param rIsWeight
	 */
	protected String printFirstResult(boolean rIsWeight) {
		StringBuffer srcString = new StringBuffer();
		StringBuffer resultStirng = new StringBuffer();
		for (int i = 0; i < turnOverValList.size(); i++) {
			srcString.append(turnOverValList.get(i).getX() + "\t");
			if (rIsWeight) {
				srcString.append(turnOverValList.get(i).getReaction());
			} else {
				srcString.append(turnOverValList.get(i).getReactionCal());
			}
			if (i < finalXList.size()) {
				resultStirng = new StringBuffer();
				resultStirng.append(finalXList.get(i).getPosition() + "\t");
				resultStirng.append(finalXList.get(i).getTimePlus1() + "\t");
				//原來的程式，可以跑出x的，改到很煩，直接改輸出的地方，以下那一行
				//resultStirng.append(finalXList.get(i).getOriginalX());
				resultStirng.append("=VLOOKUP($E" + (i + 2) + ",$A:$B,2,FALSE)");
				srcString.append("\t").append(resultStirng);
			}
			srcString.append(System.getProperty("line.separator"));
		}
		System.out.println(srcString);
		return srcString.toString();
	}

	protected String reGenXList() {
		StringBuffer srcString = new StringBuffer();
		StringBuffer resultStirng = new StringBuffer();
		for (int i = 0; i < finalXList.size(); i++) {
			resultStirng = getXDataForExcel(i);
			srcString.append(resultStirng);
			srcString.append(System.getProperty("line.separator"));
		}
		return srcString.toString();
	}

	public StringBuffer getXDataForExcel(int i) {
		StringBuffer resultStirng;
		resultStirng = new StringBuffer();
		resultStirng.append(finalXList.get(i).getPosition() + "\t");
		resultStirng.append(finalXList.get(i).getTime() + "\t");// 此處從EXCEL來的，不需要再加1了
		resultStirng.append(finalXList.get(i).getOriginalX());
		return resultStirng;
	}

	/**
	 * 把x位置處理過的資料，再給程式處理，列印反作用力的資料
	 * 當原始資料的反作用力是體重時要傳true，若不是，要乘除校正後的倍數，設定E,H欄為了excel登打方便
	 * 
	 * @param rIsWeight
	 */
	protected String genSecondResult(boolean rIsWeight) {
		StringBuffer srcString = new StringBuffer();
		StringBuffer xStirng = new StringBuffer();
		StringBuffer resultStirng = new StringBuffer();
		for (int i = 0; i < finalXList.size(); i++) {
			xStirng = getXDataForExcel(i);
			srcString.append(xStirng).append("\t");
			resultStirng = getReactionForExcel(rIsWeight, i);
			srcString.append(resultStirng).append(
					System.getProperty("line.separator"));
		}
		return srcString.toString();
	}

	public StringBuffer getReactionForExcel(boolean rIsWeight, int i) {
		StringBuffer resultStirng;
		resultStirng = new StringBuffer();
		if (i % 2 == 0) {
			resultStirng.append("=E" + (i + 2) + "\t");
		} else {
			resultStirng.append(finalXList.get(i).getMaxReactionTime() + "\t");
		}
		if (i % 2 == 0) {
			resultStirng.append("=H" + (i + 3));
		} else {
			if (rIsWeight) {
				resultStirng.append(finalXList.get(i).getReaction());
			} else {
				resultStirng.append(finalXList.get(i).getReactionCal());
			}
		}
		return resultStirng;
	}

	protected String getRreactionAverage(TurnOver obj, String fileName,
			boolean rIsWeight, boolean checkRIsWeight, int rightOrLeft)
			throws Exception {
		obj.prepareDataFromFile(fileName, rIsWeight, checkRIsWeight);
		obj.calculateRreactionAverage();
		return obj.getAvgRCal().toString();
	}

	protected String produceDataFirstStep(TurnOver obj, String fileName,
			boolean rIsWeight, int rightOrLeft) throws Exception {
		obj.prepareDataFromFile(fileName, rIsWeight);
		obj.preparePositionList();
		obj.processReadyList(rightOrLeft);
		obj.produceFinalXList();
		// obj.produceFinalList();
		obj.calculateRreactionAverage();
		String result = obj.printFirstResult(rIsWeight);
		return result;
	}

	protected String produceDataTwoStep(TurnOver obj, String fileName,
			String content, boolean rIsWeight) throws Exception {
		obj.prepareDataFromFile(fileName, rIsWeight);
		obj.finalXList = readModifiedFinalXList(content);
		obj.produceFinalList();
		String result = obj.genSecondResult(rIsWeight);
		// obj.writeText("", modifiedFileName, "big5", false);
		System.out.println(result);
		return result;
	}

	protected String reProduceXList(TurnOver obj, String fileName,
			String content, boolean rIsWeight) throws Exception {
		obj.prepareDataFromFile(fileName, rIsWeight);
		ArrayList<TurnOverResultBean> tempXList = readModifiedFinalXList(content);
		obj.reProduceFinalXList(tempXList);
		String result = obj.reGenXList();
		System.out.println(result);
		// obj.writeText("", modifiedFileName, "big5", false);
		return result;
	}

	protected String reProduceXListAndGetFinalReactionString(TurnOver obj,
			String fileName, String content, boolean rIsWeight)
			throws Exception {
		obj.prepareDataFromFile(fileName, rIsWeight);
		ArrayList<TurnOverResultBean> tempXList = readModifiedFinalXList(content);
		obj.reProduceFinalXList(tempXList);
		String xDataResult = obj.reGenXList();
		obj.finalXList = readModifiedFinalXList(xDataResult);
		obj.produceFinalList();
		String result = obj.genSecondResult(rIsWeight);
		System.out.println(result);
		return result;
	}

	public static String getReactionCal(String reaction) {
		if (reaction.compareTo("") != 0) {
			if (TurnOver.CalMode.compareTo("M") == 0) {
				return String.valueOf(Float.parseFloat(reaction)
						* TurnOver.CalVal);
			}
			if (TurnOver.CalMode.compareTo("D") == 0) {
				return String.valueOf(Float.parseFloat(reaction)
						/ TurnOver.CalVal);
			}

		}
		return "";
	}

	public TurnOver() {
		super();
	}

}