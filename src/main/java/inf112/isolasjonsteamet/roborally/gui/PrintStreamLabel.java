package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link Label} which exposes an {@link PrintStream} to write text that will be shown in it.
 */
public class PrintStreamLabel extends Label {

	private final LabelOutputStream labelOutputStream;
	private final PrintStream printStream;

	/**
	 * Creates a print stream label using the default {@link LabelStyle} found in the skin.
	 */
	public PrintStreamLabel(int maxLines, @Nullable OutputStream alsoTo, Skin skin) {
		super("", skin);
		labelOutputStream = new LabelOutputStream(this, maxLines, alsoTo);
		printStream = new PrintStream(labelOutputStream);
	}

	/**
	 * Creates a print stream label, using a {@link LabelStyle} that has a BitmapFont with the specified name from the
	 * skin.
	 */
	public PrintStreamLabel(int maxLines, @Nullable OutputStream alsoTo, Skin skin, String styleName) {
		super("", skin, styleName);
		labelOutputStream = new LabelOutputStream(this, maxLines, alsoTo);
		printStream = new PrintStream(labelOutputStream);
	}

	/**
	 * Creates a print stream label, using a {@link LabelStyle} that has a BitmapFont with the specified name from the
	 * skin and the specified color.
	 */
	public PrintStreamLabel(int maxLines, @Nullable OutputStream alsoTo, Skin skin, String fontName, Color color) {
		super("", skin, fontName, color);
		labelOutputStream = new LabelOutputStream(this, maxLines, alsoTo);
		printStream = new PrintStream(labelOutputStream);
	}

	/**
	 * Creates a print stream label, using a {@link LabelStyle} that has a BitmapFont with the specified name and the
	 * specified color from the skin.
	 */
	public PrintStreamLabel(int maxLines, @Nullable OutputStream alsoTo, Skin skin, String fontName, String colorName) {
		super("", skin, fontName, colorName);
		labelOutputStream = new LabelOutputStream(this, maxLines, alsoTo);
		printStream = new PrintStream(labelOutputStream);
	}

	/**
	 * Creates a print stream label, using a {@link LabelStyle}.
	 */
	public PrintStreamLabel(int maxLines, @Nullable OutputStream alsoTo, LabelStyle style) {
		super("", style);
		labelOutputStream = new LabelOutputStream(this, maxLines, alsoTo);
		printStream = new PrintStream(labelOutputStream);
	}

	public int getMaxLines() {
		return labelOutputStream.getMaxLines();
	}

	public void setMaxLines(int maxLines) {
		labelOutputStream.setMaxLines(maxLines);
	}

	public PrintStream getStream() {
		return printStream;
	}

	private static class LabelOutputStream extends OutputStream {

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		private final Label label;

		private int maxLines;
		private final Deque<String> lines;
		private boolean nextIsNewLine = true;

		@Nullable
		private final OutputStream alsoTo;

		private LabelOutputStream(Label label, int maxLines, @Nullable OutputStream alsoTo) {
			this.label = label;
			this.maxLines = maxLines;
			this.alsoTo = alsoTo;

			lines = new ArrayDeque<>(maxLines);
		}

		@Override
		public void write(int b) throws IOException {
			if (alsoTo != null) {
				alsoTo.write(b);
			}
			baos.write(b);
		}

		@Override
		public void flush() throws IOException {
			if (alsoTo != null) {
				alsoTo.flush();
			}

			String content;
			synchronized (this) {
				content = baos.toString();
				baos.reset();
			}

			if (content.isEmpty()) {
				return;
			}

			boolean endsWithNewline = content.endsWith("\n");
			final String[] contentLines = content.split("\n");

			int startIdx = nextIsNewLine ? 0 : 1;

			if (!nextIsNewLine) {
				continueLine(contentLines[0]);
			}

			for (int i = startIdx; i < contentLines.length; i++) {
				writeLine(contentLines[i]);
			}

			nextIsNewLine = endsWithNewline;

			var fullString = String.join("\n", lines);
			label.setText(fullString);
		}

		private void continueLine(String continuation) {
			lines.addLast(lines.removeLast() + continuation);
		}

		private void writeLine(String line) {
			if (lines.size() >= maxLines) {
				lines.removeFirst();
			}

			lines.addLast(line);
		}

		public int getMaxLines() {
			return maxLines;
		}

		public void setMaxLines(int maxLines) {
			this.maxLines = maxLines;
		}
	}
}
