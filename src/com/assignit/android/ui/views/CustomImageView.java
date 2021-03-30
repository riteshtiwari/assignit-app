package com.assignit.android.ui.views;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Custom Image View
 * 
 * @author Innoppl
 * 
 */
public class CustomImageView extends ImageView {

	public interface OnImageViewListener {

		public void onImageLoadingCompleted(int position, Drawable d);
	}

	private OnImageViewListener mOnImageViewListener;
	private int position = 0;

	private Path clippingPath;
	private int cornerRadius;
	@SuppressWarnings("unused")
	private final Context mContext;

	public CustomImageView(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * Constructor for the class
	 * 
	 * @param context
	 * @param attrs
	 */
	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * Method to set the image url
	 * 
	 * @param position
	 * @param URL
	 * @param onImageViewListener
	 */
	public void setImageURL(int position, final String URL, OnImageViewListener onImageViewListener) {

		mOnImageViewListener = onImageViewListener;
		this.position = position;

		new DownloadWebPageTask().execute(URL);

	}

	/**
	 * Method to set the round corner radius
	 * 
	 * @param radiusInPixelSize
	 */
	public void setCornerRadius(int radiusInPixelSize) {

		cornerRadius = radiusInPixelSize;

	}

	/* 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		final RectF bounds = new RectF(0, 0, w, h);

		clippingPath = new Path();
		clippingPath.addRoundRect(bounds, cornerRadius, cornerRadius, Path.Direction.CW);

	}

	/*
	 * @see android.view.View#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		canvas.clipPath(clippingPath);
		super.dispatchDraw(canvas);

		canvas.restore();
	}

	/* 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.clipPath(clippingPath);
		super.onDraw(canvas);

		canvas.restore();
	}

	/**
	 * Asynctask class to download web page task
	 * 
	 * @author android3
	 *
	 */
	private class DownloadWebPageTask extends AsyncTask<String, Void, Bitmap> {
		// String --> parameter in execute
		// Bitmap --> return type of doInBackground and parameter of
		// onPostExecute
		@Override
		protected Bitmap doInBackground(String... urls) {

			InputStream i = null;
			BufferedInputStream bis = null;
			ByteArrayOutputStream out = null;
			Bitmap bitmap = null;

			try {
				URL m = new URL(urls[0]);
				i = (InputStream) m.getContent();
				bis = new BufferedInputStream(i, 1024 * 8);
				out = new ByteArrayOutputStream();
				int len = 0;
				byte[] buffer = new byte[4096];
				while ((len = bis.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				bis.close();

				byte[] data = out.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			int h = (int) convertDpToPixel(getHeight(), getContext()); // height
																		// in
																		// pixels
			int w = (int) convertDpToPixel(getWidth(), getContext()); // width
																		// in
																		// pixels

			if (result != null) {
				Bitmap scaled = Bitmap.createScaledBitmap(result, h, w, true);
				@SuppressWarnings("deprecation")
				Drawable d = new BitmapDrawable((scaled));

				if (d != null) {
					setImageBitmap(scaled);

					if (mOnImageViewListener != null) {
						mOnImageViewListener.onImageLoadingCompleted(position, d);
					}
				}
			}
		}
	}

	/**
	 * Method to convert dp to pixel to set imageview size 
	 * 
	 * @param dp
	 * @param context
	 * @return
	 */
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

}
