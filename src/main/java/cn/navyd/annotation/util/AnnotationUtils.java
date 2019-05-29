package cn.navyd.annotation.util;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class AnnotationUtils {
  private static final Map<TypeElement, Set<TypeElement>> CACHED_INTERFACES = new HashMap<>(50);
  
  /**
   * 在指定的type element上查找所有接口
   * @param <T>
   * @param element
   * @param annotationClass
   * @return
   */
  public static Collection<TypeElement> getAllInterfaces(TypeElement typeElement) {
    if (CACHED_INTERFACES.containsKey(typeElement))
      return CACHED_INTERFACES.get(typeElement);
    Set<TypeElement> interfaces = new HashSet<>();
    findAllInterfaces(typeElement, interfaces);
    var result = Collections.unmodifiableSet(interfaces);
    CACHED_INTERFACES.put(typeElement, result);
    return result;
  }
  
  /**
   * 从指定的element上递归获取所有接口包括超类、接口继承的接口等
   * @param typeElement
   * @param interfaces
   */
  private static void findAllInterfaces(TypeElement typeElement, Collection<TypeElement> interfaces) {
    var superType = typeElement.getSuperclass();
    var interfaceMirrors = typeElement.getInterfaces();
    // 如果element不存在接口，并且超类是object.class或接口（排除接口了）
    if (interfaceMirrors.size() == 0 && superType.getKind() == TypeKind.NONE)
      return;
    for (TypeMirror typeMirror : interfaceMirrors) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement interfaceTypeElement = (TypeElement) declaredType.asElement();
      interfaces.add(interfaceTypeElement);
      findAllInterfaces(interfaceTypeElement, interfaces);
    }
    findAllInterfaces((TypeElement)((DeclaredType)superType).asElement(), interfaces);
  }
  
  public static AnnotationMirror getAnnotationMirror(Element element, Class<? extends Annotation> clazz) {
    for (AnnotationMirror am : element.getAnnotationMirrors()) {
      // am是注解实例，必定是TypeElement类型
      var annotationEle = (TypeElement) am.getAnnotationType().asElement();
      if (annotationEle.getQualifiedName().toString().equals(clazz.getCanonicalName())) {
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
  
  /**
   * 通过方法名称返回指定注解的方法默认值
   * @param element
   * @param simpleName
   * @return
   */
  public static Optional<AnnotationValue> getDefaultValueByMethodName(Element element, String simpleName) {
    if (element.getKind() != ElementKind.ANNOTATION_TYPE)
      throw new IllegalArgumentException(element.toString() + " 不是一个注解类型");
    for (var e : element.getEnclosedElements()) {
      if (e.getKind() != ElementKind.METHOD || !e.getSimpleName().toString().equals(simpleName))
        continue;
      return Optional.ofNullable(((ExecutableElement)e).getDefaultValue());
    }
    return Optional.empty();
  }
}
