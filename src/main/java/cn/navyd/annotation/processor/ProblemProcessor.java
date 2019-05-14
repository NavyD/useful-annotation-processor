package cn.navyd.annotation.processor;

import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationMirror;
import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationValueByName;
import static cn.navyd.annotation.util.AnnotationUtils.isUrl;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import javax.tools.Diagnostic;
import cn.navyd.annotation.leetcode.Problem;

public class ProblemProcessor extends AbstractProcessor {
  private Messager messager;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean hasError = false;
    for (Element problemElement : roundEnv.getElementsAnnotatedWith(Problem.class)) {
      final AnnotationMirror problemAnnotationMirror = getAnnotationMirror(problemElement, Problem.class);
      // 仅允许存在interface
      if (problemElement.getKind() != ElementKind.INTERFACE) {
        messager.printMessage(Diagnostic.Kind.ERROR, "仅允许注解到interface", problemElement, problemAnnotationMirror);
        hasError = true;
      }
      // problem实例必定存在
      Problem problem = problemElement.getAnnotation(Problem.class);
      // 检查number
      if (problem.number() < 0) {
        var value = getAnnotationValueByName(problemAnnotationMirror, "number");
        messager.printMessage(Diagnostic.Kind.ERROR, "非法的 number < 0", problemElement, problemAnnotationMirror, value);
        hasError = true;
      }
      // 检查resolvedTimes
      if (problem.resolvedTimes() <= 0) {
        var value = getAnnotationValueByName(problemAnnotationMirror, "resolvedTimes");
        messager.printMessage(Diagnostic.Kind.ERROR, "非法的 resolvedTimes <= 0", problemElement, problemAnnotationMirror, value);
        hasError = true;
      }
      // 检查url
      if (!isUrl(problem.url())) {
        var value = getAnnotationValueByName(problemAnnotationMirror, "url");
        messager.printMessage(Diagnostic.Kind.ERROR, "非法的 url", problemElement, problemAnnotationMirror, value);
        hasError = true;
      }
    }
    return hasError;
  }
  
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Problem.class.getCanonicalName())));
  }
}
