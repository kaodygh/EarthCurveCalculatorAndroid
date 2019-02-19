package tool.dox.com.test;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity
{
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new AsyncTask<String, String, byte[]>()
		{
			@Override
			protected byte[] doInBackground(String... strings)
			{
				ByteBuffer bf = getByteBuffer();

				int length = bf.remaining();
				double half   = Math.ceil(length / 2);

				Log.d(TAG, "!!length: " + length);
				Log.d(TAG, "!!half: "   + half);

				return null;
			}

			private ByteBuffer getByteBuffer()
			{
				try {
					InputStream is = getAssets().open("red_apple.png");

					byte[] buffer = new byte[8192];
					int bytesRead;
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					while ((bytesRead = is.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
					}
					byte bytes[] = output.toByteArray();

					ByteBuffer buf = ByteBuffer.wrap(bytes);
					return buf;

				}
				catch(IOException ex) {
					return null;
				}
			}
		}.execute();
	}
}
