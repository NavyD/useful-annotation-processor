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
import cn.navyd.annotation.checker.SubmissionChecker;
import cn.navyd.annotation.leetcode.Submission;

public class SubmissionProcessor extends AbstractProcessor {
  private Messager messager;
  private AnnotationChecker<Submission> checker;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
    checker = new SubmissionChecker(messager);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean hasError = false;
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Submission.class)) {
      // 注意注解与容器注解间是互斥关系，只能同时存在一个
      try {
          hasError = !checker.check(annotatedElement);
      } catch (RuntimeException e) {
        e.printStackTrace();
        hasError = true;
      }
    }
    return hasError;
  }
  
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Submission.class.getCanonicalName())));
  }
  
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

}
