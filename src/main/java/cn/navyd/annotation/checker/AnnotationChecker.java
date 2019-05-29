package cn.navyd.annotation.checker;

import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public interface AnnotationChecker<T extends Annotation> {
  /**
   * 检查指定的注解T是否正确。
   * <p>如果不正确将返回true
   * @param annotation
   * @param element
   * @param annotationMirror
   * @return
   * @throws RuntimeException
   */
  boolean check(Element element) throws RuntimeException;
  
  boolean check(T annotation, Element element, AnnotationMirror mirror);
  
//  boolean check(T annotation, Element element, AnnotationMirror mirror, AnnotationValue value);
}
