package org.springframework.cloud.stream.app.fraud.detection.processor;

/**
 * @author Christian Tzolov
 */
public class CdcEntryNormalizationUtils {
	public static String[] columns = new String[] { "time", "v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8", "v9", "v10",
			"v11", "v12", "v13", "v14", "v15", "v16", "v17", "v18", "v19", "v20", "v21", "v22", "v23", "v24", "v25",
			"v26", "v27", "v28", "amount"};

	public static final int Time = 0;
	public static final int V1 = 1;
	public static final int V2 = 2;
	public static final int V3 = 3;
	public static final int V4 = 4;
	public static final int V5 = 5;
	public static final int V6 = 6;
	public static final int V7 = 7;
	public static final int V9 = 8;
	public static final int V10 = 9;
	public static final int V11 = 10;
	public static final int V12 = 11;
	public static final int V14 = 12;
	public static final int V16 = 13;
	public static final int V17 = 14;
	public static final int V18 = 15;
	public static final int V19 = 16;
	public static final int V21 = 17;
	public static final int Amount = 18;
	public static final int Amount_max_fraud = 19;
	public static final int V1_ = 20;
	public static final int V2_ = 21;
	public static final int V3_ = 22;
	public static final int V4_ = 23;
	public static final int V5_ = 24;
	public static final int V6_ = 25;
	public static final int V7_ = 26;
	public static final int V9_ = 27;
	public static final int V10_ = 28;
	public static final int V11_ = 29;
	public static final int V12_ = 30;
	public static final int V14_ = 31;
	public static final int V16_ = 32;
	public static final int V17_ = 33;
	public static final int V18_ = 34;
	public static final int V19_ = 35;
	public static final int V21_ = 36;

	public static float[][] normalize(float[] rawInput) {
		float[][] in = new float[1][37];

		in[0][Time] = rawInput[0];
		in[0][V1] = rawInput[1];
		in[0][V2] = rawInput[2];
		in[0][V3] = rawInput[3];
		in[0][V4] = rawInput[4];
		in[0][V5] = rawInput[5];
		in[0][V6] = rawInput[6];
		in[0][V7] = rawInput[7];
		in[0][V9] = rawInput[9];
		in[0][V10] = rawInput[10];
		in[0][V11] = rawInput[11];
		in[0][V12] = rawInput[12];
		in[0][V14] = rawInput[14];
		in[0][V16] = rawInput[16];
		in[0][V17] = rawInput[17];
		in[0][V18] = rawInput[18];
		in[0][V19] = rawInput[19];
		in[0][V21] = rawInput[21];
		in[0][Amount] = rawInput[22];

		in[0][Amount_max_fraud] = (in[0][Amount] <= 2125.87) ? 0 : 1;

		in[0][V1_] = (in[0][V1] < -3) ? 1 : 0;
		in[0][V2_] = (in[0][V2] > 2.5) ? 1 : 0;
		in[0][V3_] = (in[0][V3] < -4) ? 1 : 0;
		in[0][V4_] = (in[0][V4] > 2.5) ? 1 : 0;
		in[0][V5_] = (in[0][V5] < -4.5) ? 1 : 0;
		in[0][V6_] = (in[0][V6] < -2.5) ? 1 : 0;
		in[0][V7_] = (in[0][V7] < -3) ? 1 : 0;
		in[0][V9_] = (in[0][V9] < -2) ? 1 : 0;
		in[0][V10_] = (in[0][V10] < -2.5) ? 1 : 0;
		in[0][V11_] = (in[0][V11] > 2) ? 1 : 0;
		in[0][V12_] = (in[0][V12] < -2) ? 1 : 0;
		in[0][V14_] = (in[0][V14] < -2.5) ? 1 : 0;
		in[0][V16_] = (in[0][V16] < -2) ? 1 : 0;
		in[0][V17_] = (in[0][V17] < -2) ? 1 : 0;
		in[0][V18_] = (in[0][V18] < -2) ? 1 : 0;
		in[0][V19_] = (in[0][V19] > 1.5) ? 1 : 0;
		in[0][V21_] = (in[0][V21] > 0.6) ? 1 : 0;

		// value = (value - mean) / std
		in[0][Time] = (in[0][Time] - (94813.85957508067f)) / 47488.14595456617f;
		in[0][V1] = (in[0][V1] - (1.165979954793388e-15f)) / 1.9586958038574858f;
		in[0][V2] = (in[0][V2] - (3.416908049651284e-16f)) / 1.6513085794769975f;
		in[0][V3] = (in[0][V3] - (-1.3731499638785534e-15f)) / 1.5162550051777715f;
		in[0][V4] = (in[0][V4] - (2.0868686078944993e-15f)) / 1.4158685749409203f;
		in[0][V5] = (in[0][V5] - (9.604066317127324e-16f)) / 1.3802467340314395f;
		in[0][V6] = (in[0][V6] - (1.4901072137089068e-15f)) / 1.3322710897575756f;
		in[0][V7] = (in[0][V7] - (-5.556467295694611e-16f)) / 1.2370935981826663f;
		in[0][V9] = (in[0][V9] - (-2.406455290984693e-15f)) / 1.0986320892243193f;
		in[0][V10] = (in[0][V10] - (2.2397512928263264e-15f)) / 1.0888497654025169f;
		in[0][V11] = (in[0][V11] - (1.673326932726423e-15f)) / 1.0207130277115584f;
		in[0][V12] = (in[0][V12] - (-1.2549951995448174e-15f)) / 0.9992013895301447f;
		in[0][V14] = (in[0][V14] - (1.206296276407264e-15f)) / 0.9585956112570637f;
		in[0][V16] = (in[0][V16] - (1.4376660577482834e-15f)) / 0.8762528873883703f;
		in[0][V17] = (in[0][V17] - (-3.800112690733671e-16f)) / 0.8493370636743893f;
		in[0][V18] = (in[0][V18] - (9.572132597037126e-16f)) / 0.8381762095288414f;
		in[0][V19] = (in[0][V19] - (1.0398168122182818e-15f)) / 0.8140405007685785f;
		in[0][V21] = (in[0][V21] - (1.6565617296790687e-16f)) / 0.734524014371313f;
		in[0][Amount] = (in[0][Amount] - (88.34961925093133f)) / 250.1201092401885f;
		in[0][Amount_max_fraud] = (in[0][Amount_max_fraud] - (0.002117223242406261f)) / 0.04596463886861922f;
		in[0][V1_] = (in[0][V1_] - (0.04704238308749434f)) / 0.2117299097534859f;
		in[0][V2_] = (in[0][V2_] - (0.02477116082118768f)) / 0.15542726669999432f;
		in[0][V3_] = (in[0][V3_] - (0.009838241335360437f)) / 0.09869895919713517f;
		in[0][V4_] = (in[0][V4_] - (0.052793646223582985f)) / 0.22362167319569817f;
		in[0][V5_] = (in[0][V5_] - (0.004578539151074236f)) / 0.06750994099171147f;
		in[0][V6_] = (in[0][V6_] - (0.006274424434792684f)) / 0.07896250961751704f;
		in[0][V7_] = (in[0][V7_] - (0.010059443763671539f)) / 0.09979121364063397f;
		in[0][V9_] = (in[0][V9_] - (0.031530123908471354f)) / 0.17474576507470227f;
		in[0][V10_] = (in[0][V10_] - (0.0050490332049422946f)) / 0.07087706333576473f;
		in[0][V11_] = (in[0][V11_] - (0.01824393361118231f)) / 0.13383256474535693f;
		in[0][V12_] = (in[0][V12_] - (0.048408220303574f)) / 0.21462764559072f;
		in[0][V14_] = (in[0][V14_] - (0.01369699480700966f)) / 0.1162300932371344f;
		in[0][V16_] = (in[0][V16_] - (0.021165210124751147f)) / 0.14393511297360986f;
		in[0][V17_] = (in[0][V17_] - (0.0021734016368979694f)) / 0.046569148336665075f;
		in[0][V18_] = (in[0][V18_] - (0.013942775282910883f)) / 0.11725366763172193f;
		in[0][V19_] = (in[0][V19_] - (0.03295213951904272f)) / 0.17851164642086356f;
		in[0][V21_] = (in[0][V21_] - (0.041958238385994724f)) / 0.20049410404880114f;

		return in;
	}

}
