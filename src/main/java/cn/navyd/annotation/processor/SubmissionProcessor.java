package cn.navyd.annotation.processor;

import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationMirror;
import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationValueByName;
import static cn.navyd.annotation.util.AnnotationUtils.isUrl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import cn.navyd.annotation.leetcode.Solution;
import cn.navyd.annotation.leetcode.Submission;

public class SubmissionProcessor extends AbstractProcessor {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private Messager messager;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean hasError = false;
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Submission.class)) {
      var submissionMirror = getAnnotationMirror(annotatedElement, Submission.class);
      // 检查是否存在solution注解
      if (annotatedElement.getAnnotation(Solution.class) == null) {
        messager.printMessage(Diagnostic.Kind.ERROR, "当前注解class不存在solution注解", annotatedElement, submissionMirror);
        hasError = true;
      }
      
      Submission submission = annotatedElement.getAnnotation(Submission.class);
      if (!isValidValue(submission, annotatedElement, submissionMirror))
        hasError = true;
    }
    return hasError;
  }


  private boolean isValidValue(Submission submission, Element element, AnnotationMirror mirror) {
    boolean hasError = false;
    if (!isValidDate(submission, element, mirror)) {
      hasError = true;
    }
    
    int runtime = submission.runtime();
    if (runtime < 0) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不允许为负数", element, mirror, getAnnotationValueByName(mirror, "runtime"));
      hasError = true;
    }
    double memory = submission.memory();
    if (memory < 0) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不允许为负数", element, mirror, getAnnotationValueByName(mirror, "memory"));
      hasError = true;
    }
    
    double runtimeBeatRate = submission.runtimeBeatRate();
    var runtimeBeatRateAnnotationValue = getAnnotationValueByName(mirror, "runtimeBeatRate");
    if (runtimeBeatRate < 0) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不允许为负数", element, mirror, runtimeBeatRateAnnotationValue);
      hasError = true;
    }
    if (runtimeBeatRate > 100) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不能超过100", element, mirror, runtimeBeatRateAnnotationValue);
      hasError = true;
    }
    
    double memoryBeatRate = submission.memoryBeatRate();
    var memoryBeatRateAnnotationValue = getAnnotationValueByName(mirror, "memoryBeatRate");
    if (memoryBeatRate < 0) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不允许为负数", element, mirror, memoryBeatRateAnnotationValue);
      hasError = true;
    }
    if (memoryBeatRate > 100) {
      messager.printMessage(Diagnostic.Kind.ERROR, "不能超过100", element, mirror, memoryBeatRateAnnotationValue);
      hasError = true;
    }
    
    if (!isUrl(submission.url())) {
      messager.printMessage(Diagnostic.Kind.ERROR, "非法的url", element, mirror, getAnnotationValueByName(mirror, "url"));
      hasError = true;
    }
    return !hasError;
  }
  
  /**
   * 检查日期是否合法
   * @param submission
   * @param element
   * @param mirror
   * @param annotationFieldName
   * @return
   */
  private boolean isValidDate(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "date", date = submission.date();
    // 检查格式
    if (date == null || date.isBlank()) {
      messager.printMessage(Diagnostic.Kind.ERROR, "date为空", element, mirror, getAnnotationValueByName(mirror, name));
      return false;
    }
    try {
      var submissionDate = LocalDate.parse(date, DATE_FORMATTER);
      // date不能超前
      var nowDate = LocalDate.now();
      if (submissionDate.isAfter(nowDate)) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format("date不允许超过当前时间：%s", nowDate), 
            element, mirror, getAnnotationValueByName(mirror, name));
        return false;
      }
    } catch (DateTimeParseException e) {
      // 格式错误
      messager.printMessage(Diagnostic.Kind.ERROR, "date格式错误", element, mirror, getAnnotationValueByName(mirror, name));
      return false;
    }
    return true;
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
