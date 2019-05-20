package cn.navyd.annotation.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.checker.AnnotationChecker;
import cn.navyd.annotation.checker.ProblemChecker;
import cn.navyd.annotation.leetcode.Problem;

public class ProblemProcessor extends AbstractProcessor {
  private AnnotationChecker<Problem> checker;
  private Messager messager;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
    this.checker = new ProblemChecker(messager);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean hasError = false;
    for (Element element : roundEnv.getElementsAnnotatedWith(Problem.class)) {
      try {
        hasError = !checker.check(element);
      } catch (RuntimeException e) {
        e.printStackTrace();
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
