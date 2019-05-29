package cn.navyd.annotation.processor;

import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.checker.AnnotationChecker;
import cn.navyd.annotation.checker.SubmissionChecker;
import cn.navyd.annotation.checker.SubmissionsChecker;
import cn.navyd.annotation.leetcode.Submissions;

public class SubmissionsProcessor extends AbstractProcessor {
  private AnnotationChecker<Submissions> checker;
  private Messager messager;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
    this.checker = new SubmissionsChecker(messager, new SubmissionChecker(messager));
  }
  
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean hasError = false;
    System.out.format("--------------------\n");
    for (var element : roundEnv.getElementsAnnotatedWith(Submissions.class)) {
      try {
        hasError |= !checker.check(element);
      } catch (RuntimeException e) {
        e.printStackTrace();
        hasError = true;
      }
    }
    System.out.format("--------------------\n");
    return hasError;
  }
  
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections
        .unmodifiableSet(new HashSet<>(Arrays
            .asList(Repeatable.class.getCanonicalName(), 
                Submissions.class.getCanonicalName())));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
