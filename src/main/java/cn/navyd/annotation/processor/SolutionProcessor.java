package cn.navyd.annotation.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import cn.navyd.annotation.leetcode.Problem;
import cn.navyd.annotation.leetcode.Solution;

public class SolutionProcessor extends AbstractProcessor {
  private Messager messager;
  private Types types;
  // 保存注解solution的class element和对应唯一接口class element
  private final Map<TypeElement, TypeElement> solutionInterfaceMap = new HashMap<>();
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
    this.types = processingEnv.getTypeUtils();
  }
  
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element solutionElement : roundEnv.getElementsAnnotatedWith(Solution.class)) {
        if (processAnnotatedKind(solutionElement))
          return false;
        TypeElement typeElement = (TypeElement) solutionElement;
        if (processProblemInterface(typeElement, typeElement.getInterfaces()))
          return false;
    }
    return false;
  }
  
  @SuppressWarnings({"unused", "deprecation"})
  private boolean processProblemValue(TypeElement solutionElement) {
    Solution solution = solutionElement.getAnnotation(Solution.class);
    if (solution == null) {
      error(solutionElement, "不存在solution注解");
      return true;
    }
    // 处理solution.problem
    String problemClassName = null;
    try {
      problemClassName = solution.problem().getCanonicalName();
    } catch (MirroredTypeException e) {
      // 如果problem class为被加载编译，则会抛出异常
      var mirror = e.getTypeMirror();
      if (mirror == null)
        problemClassName = null;
      else {
        var ele = (TypeElement)types.asElement(mirror);
        problemClassName = ele.getQualifiedName().toString();
      }
    }
    // 如果solution.problem与获取接口不一致 除了默认值
    if (!Object.class.getCanonicalName().equals(problemClassName) 
        && !solutionInterfaceMap.get(solutionElement)
        .getQualifiedName().toString()
        .equals(problemClassName)) {
      error(solutionElement, "solution.problem:%s非法，实现接口：%s", problemClassName,
          solutionInterfaceMap.get(solutionElement));
      return true;
    }
    return false;
  }
  
  // 仅允许注解到class上
  private boolean processAnnotatedKind(Element solutionElement) {
    if (solutionElement.getKind() == ElementKind.CLASS) {
      return false;
    }
    error(solutionElement, "仅允许注解到class");
    return true;
  }
  
  /**
   * 处理problem接口元素。检查指定接口是否有且仅有一个实现了@Problem
   * @param solutionElement
   * @param problemInterfaces
   * @return
   */
  private boolean processProblemInterface(TypeElement solutionElement, List<? extends TypeMirror> problemInterfaces) {
    for (TypeMirror type : problemInterfaces) {
      final var problemInterface = (TypeElement)types.asElement(type);
      var problem = problemInterface.getAnnotation(Problem.class);
      // 如果存在多个@problem接口
      if (solutionInterfaceMap.containsKey(solutionElement)) {
        error(solutionElement, "仅允许存在一个@problem的接口");
        return true;
      } 
      // 存在@problem
      else if (problem != null) {
        solutionInterfaceMap.put(solutionElement, problemInterface);
      }
    }
    // 如果不存在@problem接口
    if (!solutionInterfaceMap.containsKey(solutionElement)) {
      error(solutionElement, "不存在一个@problem的接口");
      return true;
    }
    return false;
  }
  
  private void error(Element e, String msg, Object... args) {
    messager.printMessage(
        Diagnostic.Kind.ERROR,
        String.format(msg, args),
        e);
  }
  
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Solution.class.getCanonicalName())));
  }
}
