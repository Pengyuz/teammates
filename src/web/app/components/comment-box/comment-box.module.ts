import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SingleResponseModule } from '../question-responses/single-response/single-response.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from './comment-edit-form.component';
import { CommentTableModalComponent } from './comment-table-modal/comment-table-modal.component';
import { CommentRowComponent } from './comment-table/comment-row.component';
import { CommentTableComponent } from './comment-table/comment-table.component';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe,
  CommentVisibilityTypeNamePipe,
} from './comment-visibility-setting.pipe';
import {
  ConfirmDeleteCommentModalComponent,
} from './confirm-delete-comment-modal/confirm-delete-comment-modal.component';

/**
 * Module for comments table
 */
@NgModule({
  declarations: [
    CommentVisibilityControlNamePipe,
    CommentVisibilityTypeDescriptionPipe,
    CommentVisibilityTypeNamePipe,
    CommentEditFormComponent,
    CommentTableComponent,
    CommentRowComponent,
    ConfirmDeleteCommentModalComponent,
    CommentTableModalComponent,
  ],
  imports: [
    TeammatesCommonModule,
    CommonModule,
    SingleResponseModule,
    RichTextEditorModule,
    NgbModule,
    FormsModule,
  ],
  exports: [
    CommentEditFormComponent,
    CommentTableComponent,
  ],
  entryComponents: [
    CommentTableModalComponent,
    ConfirmDeleteCommentModalComponent,
  ],
})
export class CommentBoxModule { }
