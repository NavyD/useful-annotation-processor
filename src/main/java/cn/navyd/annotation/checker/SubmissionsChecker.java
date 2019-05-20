package cn.navyd.annotation.checker;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import cn.navyd.annotation.leetcode.Submission;
import cn.navyd.annotation.leetcode.Submissions;
import cn.navyd.annotation.util.CheckerUtil;

public class SubmissionsChecker extends AbstractAnnotationChecker<Submissions> {
  private final AnnotationChecker<Submission> submissionChecker;
  private final Messager messager;

  public SubmissionsChecker(Messager messager, AnnotationChecker<Submission> submissionChecker) {
    super(Submissions.class);
    this.messager = messager;
    this.submissionChecker = submissionChecker; 
  }

  @Override
  protected boolean doCheck(Submissions annotation, Element element, AnnotationMirror mirror) {
    Set<Submission> submissions = new HashSet<>();
    // 检查重复的submission
    for (Submission sub : annotation.value()) {
      if (!submissions.add(sub)) {
        CheckerUtil.errorMessage(messager, element, mirror, "重复的submission");
        return false;
      }
      // 检查submission
      if (!submissionChecker.check(element))
        return false;
    }
    return false;
  }

}
