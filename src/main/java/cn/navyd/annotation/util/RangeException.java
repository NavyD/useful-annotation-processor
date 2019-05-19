package cn.navyd.annotation.util;

public class RangeException extends RuntimeException {
  private static final long serialVersionUID = 7757422266473739136L;
  private final boolean upperOrLowerBound;

  
  public RangeException(boolean upperOrLowerBound, String message) {
    super(message);
    this.upperOrLowerBound = upperOrLowerBound;
  }
  
  public RangeException(boolean upperOrLowerBound) {
    this(upperOrLowerBound, null);
  }
  
  /**
   * 如果为true则表示上界溢出，false表示下界溢出
   * @return
   */
  public boolean isUpperOrLowerBound() {
    return upperOrLowerBound;
  }

}
