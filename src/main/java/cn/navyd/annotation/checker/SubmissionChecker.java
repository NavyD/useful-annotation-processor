package cn.navyd.annotation.checker;

import static cn.navyd.annotation.util.AnnotationUtils.getAnnotationValueByName;
import static cn.navyd.annotation.util.CheckerUtil.checkRange;
import static cn.navyd.annotation.util.CheckerUtil.errorMessage;
import static cn.navyd.annotation.util.CheckerUtil.isUrl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import cn.navyd.annotation.leetcode.Solution;
import cn.navyd.annotation.leetcode.Submission;
import cn.navyd.annotation.util.RangeException;

public class SubmissionChecker extends AbstractAnnotationChecker<Submission> {
  private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private DateTimeFormatter dateFormatter = DEFAULT_DATE_FORMATTER;
  private final Messager messager;
  
  public SubmissionChecker(Messager messager) {
    super(Submission.class);
    this.messager = messager;
  }
  
  public DateTimeFormatter getDateFormatter() {
    return dateFormatter;
  }

  public void setDateFormatter(DateTimeFormatter dateFormatter) {
    this.dateFormatter = dateFormatter;
  }

  @Override
  public boolean doCheck(Submission annotation, Element element, AnnotationMirror annotationMirror)
      throws RuntimeException {
    return checkSolution(annotation, element, annotationMirror)
        & checkDate(annotation, element, annotationMirror) 
        & checkRuntime(annotation, element, annotationMirror)
        & checkRuntimeBeatRate(annotation, element, annotationMirror)
        & checkMemory(annotation, element, annotationMirror)
        & checkMemoryBeatRate(annotation, element, annotationMirror)
        & checkUrl(annotation, element, annotationMirror);
  }
  
  /**
   * 检查submission注解上是否存在solution注解
   * @param annotation
   * @param element
   * @param annotationMirror
   * @return
   */
  private boolean checkSolution(Submission annotation, Element element, AnnotationMirror annotationMirror) {
    if (element.getAnnotation(Solution.class) == null) {
      errorMessage(messager, element, annotationMirror, "不存在solution注解");
      return false;
    }
    return true;
  }
  
  /**
   * 检查date
   * <ol>
   * <li>date不允许为null或空白字符
   * <li>date格式为{@link #DEFAULT_DATE_FORMATTER}或主动设置的{@link #dateFormatter}
   * <li>date不允许超过当前时间
   * </ol>
   * @param submission
   * @param element
   * @param mirror
   * @return
   */
  private boolean checkDate(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "date", date = submission.date();
    final var annotationValue = getAnnotationValueByName(mirror, name);
    // 检查格式
    if (date == null || date.isBlank()) {
      messager.printMessage(Diagnostic.Kind.ERROR, "date is blank", element, mirror, getAnnotationValueByName(mirror, name));
      errorMessage(messager, element, mirror, annotationValue, "date is blank");
      return false;
    }
    LocalDate submissionDate = null;
    try {
      // 验证格式
      submissionDate = LocalDate.parse(date, dateFormatter);
    } catch (DateTimeParseException e) {
      // 格式错误
      errorMessage(messager, element, mirror, annotationValue, "date formatter error.");
      return false;
    }
    // date不能超前
    var nowDate = LocalDate.now();
    if (submissionDate.isAfter(nowDate)) {
      errorMessage(messager, element, mirror, annotationValue, "date不允许超过当前时间：%s", nowDate);
      return false;
    }
    return true;
  }
  
  private boolean checkRuntime(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "runtime";
    final int runtime = submission.runtime(), min = 0;
    try {
      checkRange(runtime, min, null);
    } catch (RangeException e) {
      var annotationValue = getAnnotationValueByName(mirror, name);
      errorMessage(messager, element, mirror, annotationValue, "最小值为：%s", Integer.valueOf(min));
      return false;
    }
    return true;
  }
  
  private boolean checkRuntimeBeatRate(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "runtimeBeatRate";
    final double runtimeBeatRate = submission.runtimeBeatRate(), min = 0, max = 100;
    try {
      checkRange(runtimeBeatRate, min, max);
    } catch (RangeException e) {
      var annotationValue = getAnnotationValueByName(mirror, name);
      if (e.isUpperOrLowerBound())
        errorMessage(messager, element, mirror, annotationValue, "最大值为：%s", max);
      else 
        errorMessage(messager, element, mirror, annotationValue, "最小值为：%s", min);
      return false;
    }
    return true;
  }
  
  private boolean checkMemory(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "memory";
    final double memory = submission.memory(), min = 0;
    try {
      checkRange(memory, min, null);
    } catch (RangeException e) {
      var annotationValue = getAnnotationValueByName(mirror, name);
      errorMessage(messager, element, mirror, annotationValue, "最小值为：%s", min);
      return false;
    }
    return true;
  }
  
  private boolean checkMemoryBeatRate(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "memoryBeatRate";
    final double memoryBeatRate = submission.memoryBeatRate(), min = 0, max = 100;
    try {
      checkRange(memoryBeatRate, min, max);
    } catch (RangeException e) {
      var annotationValue = getAnnotationValueByName(mirror, name);
      if (e.isUpperOrLowerBound())
        errorMessage(messager, element, mirror, annotationValue, "最大值为：%s", max);
      else 
        errorMessage(messager, element, mirror, annotationValue, "最小值为：%s", min);
      return false;
    }
    return true;
  }
  
  private boolean checkUrl(Submission submission, Element element, AnnotationMirror mirror) {
    final String name = "url", url = submission.url();
    if (!isUrl(url)) {
      var annotationValue = getAnnotationValueByName(mirror, name);
      errorMessage(messager, element, mirror, annotationValue, "非法的url");
      return false;
    }
    return true;
  }

}
