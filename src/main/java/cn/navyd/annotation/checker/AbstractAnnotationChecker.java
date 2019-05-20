package cn.navyd.annotation.checker;

import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationMirror;
import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;

public abstract class AbstractAnnotationChecker<T extends Annotation> implements AnnotationChecker<T> {
  protected final Class<T> annotationClazz;
  
  public AbstractAnnotationChecker(Class<T> annotationClazz) {
    if (annotationClazz == null)
      throw new NullPointerException("annotationClazz is null");
    this.annotationClazz = annotationClazz;
  }
    
  @Override
  public boolean check(Element element) throws RuntimeException {
    if (element == null)
      throw new NullPointerException("element is null");
    T annotation = null;
    try {
      // 通过element获取的注解实例不是一个注解clazz.isAnnotation()=false。mirror也不是annotation
      /**
       * annotation: @cn.navyd.annotation.leetcode.Submission(memoryBeatRate=100.0, memory=34.2, runtimeBeatRate=100.0, runtime=0, status=ACCEPTED, date="2019-05-19", url="https://leetcode.com/submissions/detail/229819292/"), 
       * isAnnotation: false
       * element: cn.navyd.leetcode.sort.SortColors.SolutionByPartition, 
       * java.lang.IllegalArgumentException: Not an annotation type: class com.sun.proxy.$Proxy6
       */
      annotation = element.getAnnotation(annotationClazz);
    } catch (MirroredTypeException e) {
      var mirror = e.getTypeMirror();
      if (mirror == null)
        throw new NullPointerException(e.getMessage());
      annotation = mirror.getAnnotation(annotationClazz);
    }
    if (annotation == null)
      throw new IllegalArgumentException(String.format("element: %s 不存在注解: %s", element, annotationClazz.getCanonicalName()));
    return doCheck(annotation, element, getAnnotationMirror(element, annotationClazz));
  }
  
  protected abstract boolean doCheck(T annotation, Element element, AnnotationMirror mirror);
  
}
