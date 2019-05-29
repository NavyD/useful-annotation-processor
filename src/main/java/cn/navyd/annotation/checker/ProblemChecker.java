package cn.navyd.annotation.checker;

import static cn.navyd.annotation.util.CheckerUtil.checkRange;
import static cn.navyd.annotation.util.CheckerUtil.errorMessage;
import static cn.navyd.annotation.util.CheckerUtil.isUrl;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.leetcode.Problem;
import cn.navyd.annotation.leetcode.Problem.Tag;
import cn.navyd.annotation.util.AnnotationUtils;
import cn.navyd.annotation.util.RangeException;

public class ProblemChecker extends AbstractAnnotationChecker<Problem> {
  private final Messager messager;
  
  public ProblemChecker(Messager messager) {
    super(Problem.class);
    this.messager = messager;
  }

  @Override
  protected boolean doCheck(Problem annotation, TypeElement element, AnnotationMirror mirror) {
    return checkElementKind(element, mirror)
        & checkNumber(annotation, element, mirror)
        & checkUrl(annotation, element, mirror)
        & checkTags(annotation, element, mirror);
  }
  
  private boolean checkElementKind(Element element, AnnotationMirror mirror) {
    if (element.getKind() != ElementKind.INTERFACE) {
      errorMessage(messager, element, mirror, "仅允许注解在接口上");
      return false;
    }
    return true;
  }
  
  private boolean checkNumber(Problem annotation, Element element, AnnotationMirror mirror) {
    final String name = "number";
    final short number = annotation.number(), min = 0;
    try {
      checkRange(number, min, null);
    } catch (RangeException e) {
      var value = AnnotationUtils.getAnnotationValueByName(mirror, name);
      errorMessage(messager, element, mirror, value, "最小值：%s", min);
      return false;
    }
    return true;
  }
  
  private boolean checkUrl(Problem annotation, Element element, AnnotationMirror mirror) {
    final String name = "url", url = annotation.url();
    if (!isUrl(url)) {
      var annotationValue = AnnotationUtils.getAnnotationValueByName(mirror, name);
      errorMessage(messager, element, mirror, annotationValue, "非法的url");
      return false;
    }
    return true;
  }
  
  /**
   * <li>不允许重复
   * <li>不允许非标准的tag
   * @param annotation
   * @param element
   * @param mirror
   * @return
   */
  private boolean checkTags(Problem annotation, Element element, AnnotationMirror mirror) {
    final String name = "tags";
    final var value = AnnotationUtils.getAnnotationValueByName(mirror, name);
    final Set<Tag> tags = new HashSet<>();
    boolean hasRepeatedTag = false, hasUnStandardTag = false;
    for (Tag tag : annotation.tags())
      if (!tags.add(tag)) {
        hasRepeatedTag = true;
        break;
      }
    // 移除正常的tags
    tags.removeIf(tag -> tag.isStandard());
    hasUnStandardTag = !tags.isEmpty();
    String msg = (hasRepeatedTag ? "存在重复的tags." : "") + (hasUnStandardTag ? "存在不标准的tags." : "");
    if (msg.isEmpty())
      return true;
    errorMessage(messager, element, mirror, value, msg);
    return false;
  }

}
