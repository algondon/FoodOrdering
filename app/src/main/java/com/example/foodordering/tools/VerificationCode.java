package com.example.foodordering.tools;

import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 验证码
 * @author xch
 */
@SuppressLint("DefaultLocale")
public class VerificationCode {

	public static final int line = 1;
	public static final int spot = 2;
	public static final int circle = 3;
	public static final int mixed = 10;

	private static final int Default_Length = 4;
	private static final int Default_Font_Size = 50;
	private static final int Default_Height = 88;
	private static final int Default_Lines = 1;
//	private static final char[] CHARS = { '2', '3', '4', '5', '6', '7', '8',
//			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
//			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
//			'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
//			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private static final char[] CHARS = {'1', '2', '3', '4', '5', '6', '7', '8',
		'9','0' };
	private static Random random = new Random();
	private static String Verification_Code;
	private static float Padding_Left;
	private static float Padding_Top;
	private static int Lines;
	private static int Height;
	private static int Width;
	private static int Font_Size;
	private static int Length;
	private static int Graph;

	public static Bitmap getVerificationCode() {
		InitData(Default_Font_Size, Default_Length, Default_Lines, line);
		return CreateBitmap();
	}

	public static Bitmap getVerificationCode(int font_Size) {
		InitData(font_Size, Default_Length, Default_Lines, line);
		return CreateBitmap();
	}

	public static Bitmap getVerificationCode(int font_Size, int length) {
		InitData(font_Size, length, Default_Lines, line);
		return CreateBitmap();
	}

	public static Bitmap getVerificationCode(int font_Size, int length,
			int lines) {
		InitData(font_Size, length, lines, line);
		return CreateBitmap();
	}

	public static Bitmap getVerificationCode(int font_Size, int length,
			int lines, int graph) {
		InitData(font_Size, length, lines, graph);
		return CreateBitmap();
	}

	private static void InitData(int font_Size, int length, int lines, int graph) {
		// 初始化参数
		Verification_Code = "";
		Padding_Left = 0;
		Padding_Top = 0;
		Width = (font_Size - 20) * length + 20;
		Height = Default_Height;
		Font_Size = font_Size;
		Length = length;
		Lines = lines;
		Graph = graph;
	}

	@SuppressLint("DefaultLocale")
	public static boolean checkVerificationCode(String words) {
		if (Verification_Code != null && words != null) {
			if (!Verification_Code.equals("") && !words.equals("")) {
				if (words.toLowerCase().equals(Verification_Code.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private static Bitmap CreateBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(Width, Height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setTextSize(Font_Size);
		CreateCodeString();
		for (int i = 0; i < Verification_Code.length(); i++) {
			RandomTextStyle(paint, i);
			RandomPadding(i);
			canvas.drawText(Verification_Code.charAt(i) + "", Padding_Left,
					Padding_Top, paint);
		}

		switch (Graph) {
		case line: {
			DrawLines(canvas, paint);
		}
			break;
		case spot: {
			DrawSpots(canvas, paint);
		}
			break;
		case circle: {
			DrawCircles(canvas, paint);
		}
			break;
		case mixed: {
			DrawGraphMixed(canvas, paint);
		}
			break;
		default: {
			DrawLines(canvas, paint);
		}
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
		canvas.restore();
		return bitmap;
	}

	private static void CreateCodeString() {
		for (int i = 0; i < Length; i++) {
			Verification_Code = Verification_Code
					+ CHARS[random.nextInt(CHARS.length)];
		}
	}

	private static void RandomTextStyle(Paint paint, int i) {
		paint.setColor(-random.nextInt(-Color.BLACK));
		paint.setFakeBoldText(random.nextBoolean()); // true为粗体，false为非粗体
		float skewX = random.nextInt(11) / 10;// 计算倾斜值
		switch (i) {
		case 0: {
			skewX = -skewX;// 倾斜值为负数，往右倾
		}
			break;
		default: {
			skewX = random.nextBoolean() ? skewX : -skewX;
		}
		}
		paint.setTextSkewX(skewX); // float类型参数，负数表示右斜，正数左斜
	}

	private static void RandomPadding(int i) {
		Padding_Left = i * (Font_Size - 20) + 5 + random.nextInt(5);
		Padding_Top = (Height / 2) + (Font_Size / 3);
	}

	private static void DrawLines(Canvas canvas, Paint paint) {
		int startX = 0;
		int startY = 0;
		int stopX = 0;
		int stopY = 0;
		int times=Lines + random.nextInt(Default_Lines);
		for (int i = 0; i < times; i++) {
			startX = 5+random.nextInt(Width - 10);
			startY = 5+random.nextInt(Height - 10);
			stopX = 5+random.nextInt(Height - 10);
			stopY = 5+random.nextInt(Width - 10);
			paint.setStrokeWidth(3 + random.nextInt(5));// 设置线条宽度
			paint.setColor(-random.nextInt(-Color.BLACK));// 颜色随机
			canvas.drawLine(startX, startY, stopX, stopY, paint);// 画线
		}
		paint.setStrokeWidth(2);
		paint.setColor(Color.RED);
		canvas.drawLine(0, Height / 2, Width, Height / 2, paint);
	}

	private static void DrawSpots(Canvas canvas, Paint paint) {
		int X = 0;
		int Y = 0;
		int times=Lines + random.nextInt(Default_Lines);
		for (int i = 0; i < times; i++) {
			X = 10+random.nextInt(Width-20);
			Y = 10+random.nextInt(Height-20);
			paint.setStrokeWidth(5 + random.nextInt(5));// 设置点宽度
			paint.setColor(-random.nextInt(-Color.BLACK));// 随机颜色
			canvas.drawPoint(X, Y, paint);// 画点
		}
		paint.setStrokeWidth(2);
		paint.setColor(Color.RED);
		canvas.drawLine(0, Height / 2, Width, Height / 2, paint);
	}

	private static void DrawCircles(Canvas canvas, Paint paint) {
		int X = 0;
		int Y = 0;
		int R = 0;
		int times=Lines + random.nextInt(Default_Lines);
		for (int i = 0; i < times; i++) {
			X =15+ random.nextInt(Width-30);
			Y =15+ random.nextInt(Height-30);
			R = 5 + random.nextInt(10);
			paint.setStrokeWidth(5 + random.nextInt(3));// 设置点宽度
			paint.setColor(-random.nextInt(-Color.BLACK));// 随机颜色
			canvas.drawCircle(X, Y, R, paint);//画圆
		}
		paint.setStrokeWidth(2);
		paint.setColor(Color.RED);
		canvas.drawLine(0, Height / 2, Width, Height / 2, paint);
	}

	private static void DrawGraphMixed(Canvas canvas, Paint paint) {
		int times=Lines + random.nextInt(Default_Lines);
		Lines=random.nextInt(times);
		DrawLines(canvas, paint);
		times=times-Lines;
		Lines=random.nextInt(times);
		DrawSpots(canvas, paint);
		times=times-Lines;
		Lines=random.nextInt(times);
		DrawCircles(canvas, paint);
	}

}
