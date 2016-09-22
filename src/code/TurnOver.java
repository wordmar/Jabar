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
	// ������ƪ�����ơA���U��{�O�i�p�٬O�i��(ad��bc)
	Float median;
	protected Float avgR;

	public Float getAvgR() {
		return avgR;
	}

	public Float getAvgRCal() {
		return Float.valueOf(getReactionCal(avgR.toString()));
	}

	
	/**
	 * ��ϧ@�ΤO�A�qA->B,C->D���ɶ����Z���h��̤j�ȡA�ç��̤j�Ȫ��ɶ��I�A�����p�վ�쥻�_�ʪ��ɶ��A�~��ѫ᭱excel�p��ɶ��t
	 * ���t�~�A�[��s�e���F�A������finalXList
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
			// �Ĥ@�����O�d�쪬
			if (count == 0) {
				// finalList.add(new TurnOverResultBean(resultBean.getTime(),
				// resultBean.getPosition(), resultBean.getOriginalX()));
			} else {
				if (!dataPosition.contains("X")) {
					// B,D�Υi��OB,D��Y(�e����A,C)
					if ((dataPosition.contains("B")
							|| dataPosition.contains("D") || (dataPosition
							.contains("Y") && (oldDataPosition.contains("A")) || oldDataPosition
								.contains("C")))) {
						if (!oldDataPosition.contains("X")) {
							// �W�@���򥻵����ɶ��Z������X�̤j���ϧ@�ΤO�A��ӵo�{�`�`�|�X�{�b�e1�B2�B3�I���̤j�ϧ@�ΤO�A�ҥH-3�}�l��
							TurnOverDataSrcBean maxReactionBean = getMaxReaction(
									oldTime - 3, time);
							float maxReaction = maxReactionBean.getReaction();
							int maxReactionTime = maxReactionBean.getTime();
							// �e�@�I�A���ӬOA,C�A�]�N�O�p�⪺�_�l�I
							// �_�l�I���̤j�ϧ@�ΤO�]�w���ۦP�A�o�˦bexcel���ϧΤ��~�|�ݰ_�ӬO����
							finalXList.get(count - 1).setReaction(
									String.valueOf(maxReaction));
							// �_�l�I���ɶ��A�o��excel�~���
							finalXList.get(count - 1).setMaxReactionTime(
									finalXList.get(count - 1).getTime());
							// ��̤j�ϧ@�ΤO�X�{�b�}�l½����x�첾���Ȥ��e�A�N�γ̤j�ϧ@�ΤO�o�ͪ��ɶ�
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
							// ������
						}
					}
					if (oldDataPosition.contains("X")) {
						// ������
					}
				} else {// ��ثe��m�P�_���X�ӡA��x��
					if ((oldDataPosition.contains("A")
							|| oldDataPosition.contains("C") || oldDataPosition
								.contains("Y"))) {
						// ������
					}
					// ������
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
	}

	/**
	 * ��ϧ@�ΤO�A�qA->B,C->D���ɶ����Z���h��̤j�ȡA�ç��̤j�Ȫ��ɶ��I�A�~��ѫ᭱excel�p��ɶ��t
	 * ���t�~�A�[��s�e���F�A������finalXList
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
			// �Ĥ@�����O�d�쪬
			if (count == 0) {
				time = Integer.parseInt(resultBean.getTime());
				float newTurnOverVal = getAverage(0, time,
						getAvgFromMedianRange, turnOverXValList);
				finalXList.add(new TurnOverResultBean(resultBean.getTime(),
						resultBean.getPosition(), String
								.valueOf(newTurnOverVal)));
			} else {
				if (!dataPosition.contains("X")) {
					// B,D�Υi��OB,D��Y(�e����A,C)
					if ((dataPosition.contains("A")
							|| dataPosition.contains("C") || (dataPosition// �i��OA,C��Y
							.contains("Y") && (oldDataPosition.contains("B")) || oldDataPosition
								.contains("D")))) {
						if (!oldDataPosition.contains("X")) {
							float newTurnOverVal = getAverage(oldTime, time,
									getAvgFromMedianRange, turnOverXValList);
							finalXList.add(new TurnOverResultBean(String
									.valueOf(oldTime), oldDataPosition, String// ����firstStopTime�O��oldTime
									.valueOf(newTurnOverVal)));
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, String
									.valueOf(newTurnOverVal)));
						}
						if (oldDataPosition.contains("X")) {
							// ������
						}
					}
					if (oldDataPosition.contains("X")) {
						// ������
					}
				} else {// ��ثe��m�P�_���X�ӡA��x��
					if ((oldDataPosition.contains("A")
							|| oldDataPosition.contains("C") || oldDataPosition
								.contains("Y"))) {
						// ������
					}
					// ������
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
		// �̫�@��
		finalXList.add(new TurnOverResultBean(String.valueOf(oldTime),
				oldDataPosition, String.valueOf(turnOverVal)));

	}

	/**
	 * �qreadyList���A�h��DA,BC�G�I����������(�|�Q�Τ���Ƨⷥ�ȥh��)�A���s�ᤩ�C���I����
	 * �]���n�bexcel����z�A�ҥH���Ǧ����D���ȭn�]0�A�i�p�i�����Ȥ]�@�_�k0�A�bexcel������n��
	 * �t�~�q��ӥη��ȧP�_BD�A��u����BD�I�A�qB->
	 * C,D->A���ɶ����Z���h�䮶�T�}�l��֪��_�l�I(�N��u������ʤF)�A�ç�쮶�T�̤p�_�l�Ȫ��ɶ��I�A �~�O�u���w�q�W������½��(��ʧ@����)
	 * ���t�~�A�[��s�e���F�A������finalXList
	 */
	protected void produceFinalXList() {
		String dataPosition = null;
		String oldDataPosition = null;
		Float turnOverVal = null;
		Integer time = null;
		// �Φy�p�Ȩ��X���ɶ�
		Integer oldTime = null;
		int count = 0;
		for (TurnOverResultBean resultBean : readyList) {
			dataPosition = resultBean.getPosition();
			// �Ĥ@�����B�z�@�U
			if (count == 0) {
				time = Integer.parseInt(resultBean.getTime());
				float newTurnOverVal = getAverage(0, time,
						getAvgFromMedianRange, turnOverXValList);
				finalXList.add(new TurnOverResultBean(resultBean.getTime(),
						resultBean.getPosition(), String
								.valueOf(newTurnOverVal)));
			} else {
				if (!dataPosition.contains("X")) {
					// ��time�o�@���b�~���AX���|�줣��
					time = Integer.parseInt(resultBean.getTime());
					turnOverVal = Float.parseFloat(resultBean.getOriginalX());
					// A,C�Υi��OA,C��Y(�e����B,D)
					if ((dataPosition.contains("A")
							|| dataPosition.contains("C") || (dataPosition// �i��OA,C��Y
							.contains("Y") && (oldDataPosition.contains("B")) || oldDataPosition
								.contains("D")))) {
						// �e�@������x
						if (!oldDataPosition.contains("X")) {
							float newTurnOverVal = getAverage(oldTime, time,
									getAvgFromMedianRange, turnOverXValList);
							// ���oBD�y�p�ȫ�Ĥ@�ӽ������ɶ��A���NBD�쥻�Φy�p�Ȫ��ɶ�(�s��3�I���ܾa�񥭧��Ȥ~��u������F)
							Integer firstStopTime = getFirstStopTime(
									newTurnOverVal, FirstStopTimeRange,
									oldTime, time);
							finalXList.add(new TurnOverResultBean(String
									.valueOf(firstStopTime), oldDataPosition,
									String// ����firstStopTime�O��oldTime
									.valueOf(newTurnOverVal)));
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, String
									.valueOf(newTurnOverVal)));
						}
						// �e�@���Ox�A�����]0�A�bexcel������n��
						if (oldDataPosition.contains("X")) {
							finalXList.add(new TurnOverResultBean(String
									.valueOf(time), dataPosition, "0"));
						}
					}
				} else {// ��ثe��m�P�_���X�ӡA��x��
					// �p�G�e�@���OB��D��Y(�e�@�I�n�@�_���������_�l�I)�A��e�@�����ȧאּ0�A�A�[�JfinalXList�A�bEXCEL������n��
					if ((oldDataPosition.contains("B")
							|| oldDataPosition.contains("D") || oldDataPosition
								.contains("Y"))) {
						finalXList.add(new TurnOverResultBean(String
								.valueOf(oldTime), oldDataPosition, "0"));
					}
					// X��time�έȭn��-1�ΪŦr��
					finalXList.add(new TurnOverResultBean("-1", dataPosition,
							""));
				}
			}
			oldDataPosition = dataPosition;
			oldTime = time;
			count++;
		}
		// �̫�@��
		time = turnOverValList.get(turnOverValList.size() - 1).getTime();
		float newTurnOverVal = getAverage(oldTime, time, getAvgFromMedianRange,
				turnOverXValList);
		Integer firstStopTime = getFirstStopTime(newTurnOverVal,
				FirstStopTimeRange, oldTime, time);
		finalXList.add(new TurnOverResultBean(String.valueOf(firstStopTime),
				oldDataPosition, String.valueOf(newTurnOverVal)));
	}

	/**
	 * �q�ʭFpositionList�A�i�@�B�z�ﭫ�Ъ��B��{���~���A���ʪ���z�@�U�A���readyList
	 * 
	 * @param rightOrLeft
	 *            1����2���k
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
			// �Ĥ@�����B�z�@�U
			if (count == 0) {
				oldDataPosition = dataPosition;
				oldTurnOverVal = turnOverVal;
				oldTime = time;
			}
			isCorrectChange = isCorrectChange(oldDataPosition, dataPosition);
			// �p�G�G���b�ʭF���ɭԧP�_�O�ۦP��m�A�̪i�p�Ϊi���|�P�_�n�Τ���j�٬O����p����
			// (�����򥻨ӭn��AC,BD�Ӥ��OAD,BC�A�ثe�ѤF��]�A�����ӬOAD,BC���X�z)
			// (���D�]��AC,BD�e�����O�����A�Ϊ̥����O�ݩ�u�q�����I�H)
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
			} else if (isCorrectChange) {// �o��Pı���ӬO�i�H�W�ߩ�W�@��if�A�����Q����F
				readyList.add(new TurnOverResultBean(String.valueOf(oldTime),
						oldDataPosition, String.valueOf(oldTurnOverVal)));
			} else {
				// ���������A��e�@���@�˩�i�h�A�M��A�[�@���ťժ�
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
		// �̫�@��
		readyList.add(new TurnOverResultBean(String.valueOf(oldTime),
				oldDataPosition, String.valueOf(oldTurnOverVal)));
	}

	/**
	 * �qturnOverValList��l��Ƥ��@���@����B��{�ϧΪ�����ABCD�I�A�Y���T�w���A�e���|�[�WY ���positionList��
	 */
	protected void preparePositionList() {
		Integer time = 0;
		String dataPosition = null;
		String oldDataPosition = "D";// �]���n�qa�I�}�l�A�ҥH�]a�I�e�@�I����l�Ȭ�d
		for (TurnOverDataSrcBean srcBean : turnOverValList) {
			dataPosition = getPosition(time, oldDataPosition);// �o�O��template
																// method
			if (dataPosition != null) {
				positionList.add(new TurnOverResultBean(String.valueOf(time),
						dataPosition, String.valueOf(srcBean.getX())));
				oldDataPosition = dataPosition;// �ndataPosition != null�~���N�q
			}
			time++;
		}
		if (positionList.size() == 0) {
			throw new RuntimeException("��ƫ��A�L�k�P�_");
		}
	}

	// �@�ߤ���ϧ@�ΤO���ˬd
	protected void prepareDataFromFile(String fileName, boolean rIsWeight)
			throws Exception {
		prepareDataFromFile(fileName, rIsWeight, false);
	}

	/**
	 * ���Ʊq��l��Ū�J�A���@�Ǯe�����A���᭱���{���ϥΡA�åB�p�⤤���
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
				// ²�檺�ˬd�A�����魫�o�j��10��(�O�魫���p��10�Τj��120)�A��X�ҥ~
				if ((!rIsWeight && srcBean.getReaction() > 10)
						|| (rIsWeight && (srcBean.getReaction() < 10 || srcBean
								.getReaction() > 120))) {
					throw new RuntimeException("�ϧ@�ΤO���G" + srcBean.getReaction()
							+ "�A�ˬd�ϧ@�ΤO�O�_���魫�H");
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
	 * ���Ʊq��l��Ū�J�A���@�Ǯe�����A���᭱���{���ϥΡA�åB�p�⤤���
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
	 * AB��BC��CD��DA���զX�N���`
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
		// �����OY�N���L
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
	 * �s��3�I���ܾa�񥭧��Ȥ~��u������F
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

		// ���F���nout of index�A�ӥB����̫�]�S�ΡA�ҥH-2
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
		File file = new File(filename);// �إ��ɮסA�ǳƼg��
		try {
			BufferedWriter bufWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, append),
							format));
			bufWriter.write(text);
			bufWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(filename + "�g�ɵo�Ϳ��~");
			return false;
		}
		return true;
	}

	/**
	 * �C�L�B�zx��m�᪺��ơA�ϥi�H�K��excel�A���ײ����ѧO ���l��ƪ��ϧ@�ΤO�O�魫�ɭn��true�A�Y���O�A�n��30
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
				//��Ӫ��{���A�i�H�]�Xx���A���ܷСA�������X���a��A�H�U���@��
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
		resultStirng.append(finalXList.get(i).getTime() + "\t");// ���B�qEXCEL�Ӫ��A���ݭn�A�[1�F
		resultStirng.append(finalXList.get(i).getOriginalX());
		return resultStirng;
	}

	/**
	 * ��x��m�B�z�L����ơA�A���{���B�z�A�C�L�ϧ@�ΤO�����
	 * ���l��ƪ��ϧ@�ΤO�O�魫�ɭn��true�A�Y���O�A�n�����ե��᪺���ơA�]�wE,H�欰�Fexcel�n����K
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