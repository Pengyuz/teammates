import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { CommentTableMode } from '../../comment-box/comment-table/comment-table-model';

/**
 * A list of responses grouped in GRQ/RGQ mode.
 */
@Component({
  selector: 'tm-grouped-responses',
  templateUrl: './grouped-responses.component.html',
  styleUrls: ['./grouped-responses.component.scss'],
})
export class GroupedResponsesComponent implements OnInit, OnChanges{

  @Input() responses: any = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() timeZone: string = '';
  @Input() isGrq: boolean = true;
  @Input() header: string = '';

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  constructor() { }

  ngOnInit(): void {
    this.mapComments();
  }

  ngOnChanges(): void {
    this.mapComments();
  }

  mapComments(): void {
    this.responses.forEach((question: any, questionIndex: number) => {
      question.allResponses.forEach((response: any, responseIndex: number) => {
        this.responses[questionIndex].allResponses[responseIndex].mappedComments =
            response.allComments.map((comment: any) => {
          return {
            commentId: comment.commentId,
            createdAt: comment.createdAt,
            editedAt: comment.updatedAt,
            timeZone: comment.timezone,
            commentGiver: comment.commentGiver,
            commentText: comment.commentText,
            isEditable: true,
          };
        });
      });
    });
  }
}
