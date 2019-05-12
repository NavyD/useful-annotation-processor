package cn.navyd.annotation.util;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.leetcode.Problem;

public class MyUtils {

  static final String REGEX_URL = "^(?:([A-Za-z]+):)?(/{0,3})([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$";//设置正则表达式
  static final Pattern PATTERN_URL = Pattern.compile(REGEX_URL); 
  
  public static boolean isUrl(String url) {
    if (url == null | url.isBlank())
      return false;
    return PATTERN_URL.matcher(url).matches();
  }
  
  public static AnnotationMirror getAnnotationMirror(Element element, Class<? extends Annotation> clazz) {
    for (AnnotationMirror am : element.getAnnotationMirrors()) {
      // am是注解实例，必定是TypeElement类型
      var annotationEle = (TypeElement) am.getAnnotationType().asElement();
      if (annotationEle.getQualifiedName().toString().equals(Problem.class.getCanonicalName())) {
        return am;
      }
    }
    return null;
  }
  
  /**
   * 通过指定的name从AnnotationMirror中获取AnnotationValue
   * @param annotationMirror
   * @param name
   * @return
   */
  public static AnnotationValue getAnnotationValueByName(AnnotationMirror annotationMirror, String name) {
    for (var entity : annotationMirror.getElementValues().entrySet()) {
      if (entity.getKey().getSimpleName().toString().equals(name))
        return entity.getValue();
    }
    return null;
  }
}
