package cn.navyd.annotation.checker;

import static cn.navyd.annotation.util.CheckerUtil.errorMessage;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.leetcode.Problem;
import cn.navyd.annotation.leetcode.Solution;
import cn.navyd.annotation.util.AnnotationUtils;

public class SolutionChecker extends AbstractAnnotationChecker<Solution> {
  private final Messager messager;
  
  public SolutionChecker(Messager messager) {
    super(Solution.class);
    this.messager = messager;
  }

  @Override
  protected boolean doCheck(Solution annotation, TypeElement element, AnnotationMirror mirror) {
    return checkElementKind(annotation, element, mirror)
        & checkProblemAnnotation(annotation, element, mirror);
  }
  
  private boolean checkElementKind(Solution annotation, Element element, AnnotationMirror mirror) {
    // 仅存在class中
    if (element.getKind() != ElementKind.CLASS) {
      errorMessage(messager, element, mirror, "仅允许注解到class上");
      return false;
    }
    return true;
  }
  
  private boolean checkProblemAnnotation(Solution annotation, TypeElement element, AnnotationMirror mirror) {
    // 检查 所有接口 超类是否存在Problem注解
    var interfaces = AnnotationUtils.getAllInterfaces(element);
    System.out.format("\nannotatedElement: %s, interfaces: %s\n", element, interfaces);
    int problemAnnotationCount = 0;
    for (var ele : interfaces)
      if (ele.getAnnotation(Problem.class) != null)
        problemAnnotationCount++;
    if (problemAnnotationCount == 0) {
      errorMessage(messager, element, mirror, "未发现任何接口注解Problem");
      return false;
    } else if (problemAnnotationCount > 1) {
      errorMessage(messager, element, mirror, "存在多个接口注解Problem");
      return false;
    }
    return true;
  }

}
