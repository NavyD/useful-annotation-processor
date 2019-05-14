package cn.navyd.annotation.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import cn.navyd.annotation.leetcode.Problem;
import cn.navyd.annotation.leetcode.Solution;
import cn.navyd.annotation.util.AnnotationUtils;

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
    boolean hasError = false;
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Solution.class)) {
      final TypeElement annotatedTypeElement = (TypeElement) annotatedElement;
      final AnnotationMirror solutionAnnotationMirror = AnnotationUtils.getAnnotationMirror(annotatedElement, Solution.class);
      // 仅存在class中
      if (annotatedElement.getKind() != ElementKind.CLASS) {
        messager.printMessage(Diagnostic.Kind.ERROR, "仅允许定义在class上", annotatedElement, solutionAnnotationMirror);
        hasError = true;
      }
      
      // 检查 所有接口 超类是否存在Problem注解
      var interfaces = AnnotationUtils.getAllInterfaces(annotatedTypeElement);
      System.out.format("\nannotatedElement: %s, interfaces: %s\n", annotatedElement, interfaces);
      int annotatedProblemInterfaceCounter = 0;
      for (var ifEle : interfaces)
        if (ifEle.getAnnotation(Problem.class) != null)
          annotatedProblemInterfaceCounter++;
      if (annotatedProblemInterfaceCounter == 0) {
        messager.printMessage(Diagnostic.Kind.ERROR, "不存在任何接口注解@Problem", annotatedElement, solutionAnnotationMirror);
        hasError = true;
      } else if (annotatedProblemInterfaceCounter > 1) {
        messager.printMessage(Diagnostic.Kind.ERROR, "存在多个接口注解@Problem。", annotatedElement, solutionAnnotationMirror);
        hasError = true;
      }
    }
    return hasError;
  }
  
  @SuppressWarnings({"unused", "deprecation"})
  private boolean processProblemValue(TypeElement solutionElement) {
    Solution solution = solutionElement.getAnnotation(Solution.class);
    if (solution == null) {
//      error(solutionElement, "不存在solution注解");
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
//      error(solutionElement, "solution.problem:%s非法，实现接口：%s", problemClassName,
//          solutionInterfaceMap.get(solutionElement));
      return true;
    }
    return false;
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
