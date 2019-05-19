package cn.navyd.annotation.util;

import java.util.regex.Pattern;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class CheckerUtil {
  
  static final String REGEX_URL = "^(?:([A-Za-z]+):)?(/{0,3})([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$";//设置正则表达式
  static final Pattern PATTERN_URL = Pattern.compile(REGEX_URL); 
  
  public static boolean isUrl(String url) {
    return url != null && !url.isBlank() && PATTERN_URL.matcher(url).matches();
  } 

  public static <T extends Comparable<? super T>> void checkRange(T value, T min, T max) throws RangeException {
    if (value == null || (min == null && max == null))
      throw new IllegalArgumentException();
    if (min != null && value.compareTo(min) < 0)
      throw new RangeException(false);
    else if (max != null && value.compareTo(max) > 0)
      throw new RangeException(true);
  }
  
  public static void errorMessage(Messager messager, Element element, AnnotationMirror mirror, 
      AnnotationValue value, String message, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), 
        element, mirror, value);
  }
}
