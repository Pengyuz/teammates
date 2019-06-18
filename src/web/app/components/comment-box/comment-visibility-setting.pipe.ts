import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackParticipantType, FeedbackVisibilityType } from '../../../types/api-output';
import { CommentVisibilityControl } from "../../../types/comment-visibility-control";

/**
 * Pipe to handle the simple display of {@link CommentVisibilityControl}.
 */
@Pipe({
  name: 'commentVisibilityControlName',
})
export class CommentVisibilityControlNamePipe implements PipeTransform {

  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: CommentVisibilityControl): any {
    switch (type) {
      case CommentVisibilityControl.SHOW_COMMENT:
        return 'Can see comment';
      case CommentVisibilityControl.SHOW_GIVER_NAME:
        return "Can see comment giver's name";
      default:
        return 'Unknown';
    }
  }

}

/**
 * Pipe to handle the detailed display of {@link FeedbackVisibilityType} in the context of
 * visibility control.
 */
@Pipe({
  name: 'commentVisibilityTypeDescription',
})
export class CommentVisibilityTypeDescriptionPipe implements PipeTransform {

  /**
   * Transforms {@code type} to a detailed description.
   */
  transform(type: FeedbackVisibilityType): any {
    switch (type) {
      case FeedbackVisibilityType.RECIPIENT:
        return 'Control what feedback recipient(s) can view';
      case FeedbackVisibilityType.INSTRUCTORS:
        return 'Control what instructors can view';
      case FeedbackVisibilityType.GIVER_TEAM_MEMBERS:
        return 'Control what team members of feedback giver can view';
      case FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS:
        return 'Control what team members of feedback recipients can view';
      case FeedbackVisibilityType.STUDENTS:
        return 'Control what other students can view';
      default:
        return 'Unknown';
    }
  }

}

/**
 * Pipe to handle the simple display of {@link FeedbackVisibilityType}.
 */
@Pipe({
  name: 'commentVisibilityTypeName',
})
export class CommentVisibilityTypeNamePipe implements PipeTransform {

  /**
   * Transforms {@code type} to a simple name.
   */
  transform(type: FeedbackParticipantType): any {
    switch (type) {
      case FeedbackParticipantType.GIVER:
        return 'Giver';
      case FeedbackParticipantType.RECEIVER:
        return 'Recipient(s)';
      case FeedbackParticipantType.INSTRUCTORS:
        return 'Instructors';
      case FeedbackParticipantType.OWN_TEAM_MEMBERS:
        return "Giver's Team Members";
      case FeedbackParticipantType.RECEIVER_TEAM_MEMBERS:
        return "Recipient's Team Members";
      case FeedbackParticipantType.STUDENTS:
        return 'Other students';
      default:
        return 'Unknown';
    }
  }

}
