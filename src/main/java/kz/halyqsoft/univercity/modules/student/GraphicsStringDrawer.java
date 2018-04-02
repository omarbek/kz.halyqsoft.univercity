package kz.halyqsoft.univercity.modules.student;

import java.awt.*;

/**
 * Рисует строки на графическом контексте
 *
 * @author Omarbek
 * @created 03.05.2017.
 */
public class GraphicsStringDrawer {

	private static final char DEFAULT_SPLITTER = ' ';

	private static final int DEFAULT_LINES_COUNT = 1;

	private Graphics graphics;

	private int linesCount = DEFAULT_LINES_COUNT;

	private int minFontSize = 25;

	private char splitter = DEFAULT_SPLITTER;

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public void setLinesCount(int linesCount) {
		if (linesCount < 1) {
			throw new IllegalArgumentException("Количество строк, на которые может быть разбита исходная строка, "
					+ "не может быть меньше 1");
		}
		this.linesCount = linesCount;
	}

	public void setMinFontSize(int minFontSize) {
		this.minFontSize = minFontSize;
	}

	public void setSplitter(char splitter) {
		this.splitter = splitter;
	}

	/**
	 * Рисует с переносом строк.
	 * Если требуется, уменьшает шрифт до предельно допустимого {@link #minFontSize}.
	 * Обрубает строку, если не влазиет.
	 *
	 * @return количество строк.
	 */
	public int wordWrap(String string, int x, int y, int delta, int width) {
		if (graphics == null || string == null) {
			throw new IllegalArgumentException("Парметры функции не должны быть null");
		}
		if (!string.isEmpty()) {
			String[] strings = new String[0];
			boolean stringsIsValid = false;
			int fontSize = 0;
			for (int lineNumber = 1; lineNumber <= linesCount; lineNumber++) {
				strings = splitString(string.trim(), lineNumber);
				fontSize = graphics.getFont().getSize();
				stringsIsValid = validateStringsWidth(strings, width, fontSize);
				if (stringsIsValid) {
					break;
				}
			}
			if (!stringsIsValid) {
				for (fontSize = graphics.getFont().getSize(); fontSize >= minFontSize; fontSize--) {
					stringsIsValid = validateStringsWidth(strings, width, fontSize);
					if (stringsIsValid) {
						break;
					}
				}
			}
			if (!stringsIsValid) {
				for (int stringSize = string.trim().length(); stringSize > 0; stringSize--) {
					strings = splitString(string.trim().substring(0, stringSize), linesCount);
					stringsIsValid = validateStringsWidth(strings, width, fontSize);
					if (stringsIsValid) {
						break;
					}
				}
			}
			if (stringsIsValid) {
				Font tmpFont = graphics.getFont();
				Font font = new Font(tmpFont.getFontName(), Font.PLAIN, fontSize);
				graphics.setFont(font);
				int count = 0;
				for (String drawString : strings) {
					graphics.drawString(drawString, x, y + count * delta);
					count++;
				}
				graphics.setFont(tmpFont);
				return strings.length;
			}
		}
		return DEFAULT_LINES_COUNT;
	}

	private boolean validateStringsWidth(String[] strings, int width, int fontSize) {
		Font tmpFont = graphics.getFont();
		try {
			Font font = new Font(tmpFont.getFontName(), Font.PLAIN, fontSize);
			graphics.setFont(font);
			for (String string : strings) {
				if (graphics.getFontMetrics().stringWidth(string) > width) {
					return false;
				}
			}
			return true;
		} finally {
			graphics.setFont(tmpFont);
		}
	}

	private String[] splitString(String string, int numberLines) throws IllegalArgumentException {
		if (numberLines < 1) {
			throw new IllegalArgumentException("Количество строк, на которые может быть разбита исходная строка, "
					+ "не может быть меньше 1");
		}
		String[] result = new String[numberLines];
		if (numberLines == 1) {
			result[0] = string;
		} else {
			String substring = string;
			for (int i = 2; i <= numberLines; i++) {
				String leftSubstring = getLeftSubstring(substring, numberLines - i + 2);
				result[i - 2] = leftSubstring;
				if (leftSubstring.length() == substring.length()) {
					substring = null;
					break;
				}
				substring = substring.substring(leftSubstring.length() + 1);
			}
			if (substring != null) {
				result[numberLines - 1] = substring;
			}
		}
		return result;
	}

	private String getLeftSubstring(String string, int divider) {
		int startPosition = string.length() / divider;
		int splitterPosition = findSplitter(string, startPosition);
		if (splitterPosition > -1) {
			return string.substring(0, splitterPosition);
		}
		return string;
	}

	private int findSplitter(String string, int position) throws IllegalArgumentException {
		if (position < 0 || position > string.length() - 1) {
			throw new IllegalArgumentException("Стартовая позиция поиска выходит за пределы длины строки");
		}
		if (string.charAt(position) == splitter) {
			return position;
		}
		int currentPosition = position;
		int currentNegativePosition;
		do {
			currentPosition++;
			if (currentPosition <= string.length() && string.charAt(currentPosition) == splitter) {
				return currentPosition;
			}
			currentNegativePosition = position * 2 - currentPosition;
			if (currentNegativePosition >= 0 && string.charAt(currentNegativePosition) == splitter) {
				return currentNegativePosition;
			}
		} while (currentNegativePosition >= 0 && currentPosition <= string.length());
		return -1;
	}
}
