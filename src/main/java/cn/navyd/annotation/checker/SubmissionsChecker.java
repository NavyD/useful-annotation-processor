package cn.navyd.annotation.checker;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import cn.navyd.annotation.leetcode.Submission;
import cn.navyd.annotation.leetcode.Submissions;

public class SubmissionsChecker extends AbstractAnnotationChecker<Submissions> {
  private final AnnotationChecker<Submission> submissionChecker;

  public SubmissionsChecker(Messager messager, AnnotationChecker<Submission> submissionChecker) {
    super(Submissions.class);
    this.submissionChecker = submissionChecker; 
  }

  @Override
  protected boolean doCheck(Submissions annotation, TypeElement element, AnnotationMirror mirror) {
    for (var anno : annotation.value())
      if (!submissionChecker.check(anno, element, mirror))
        return false;
    return true;
  }
}
