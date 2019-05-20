package cn.navyd.annotation.util;

/**
 * 一个在每次append时自动添加指定的separator的 StringBuilder
 * @author navyd
 *
 */
public class StringSeparatorBuilder implements CharSequence {
  private final String separator;
  private final StringBuilder builder;
  
  public StringSeparatorBuilder(String separator) {
    this.builder = new StringBuilder();
    this.separator = separator;
  }
  
  /**
   * 将参数o添加到stringbuilder中时自动在后面添加separator。
   * @param o
   * @return
   */
  public StringSeparatorBuilder append(Object o) {
    builder.append(o).append(separator);
    return this;
  }
  
  /**
   * 自动在最后去除separator字符
   */
  public String toString() {
    String s = builder.toString();
    if (s.length() > 0)
      s = s.substring(0, s.length()-separator.length());
    return s;
  }
  
  public static StringSeparatorBuilder of(String separator) {
    return new StringSeparatorBuilder(separator);
  }

  @Override
  public int length() {
    return builder.length();
  }

  @Override
  public char charAt(int index) {
    return builder.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return builder.subSequence(start, end);
  }
}