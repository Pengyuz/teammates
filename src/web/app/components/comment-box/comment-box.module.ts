import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { RichTextEditorModule } from "../rich-text-editor/rich-text-editor.module";
import { CommentEditFormComponent } from './comment-edit-form.component';
import { CommentTableComponent } from "./comment-table/comment-table.component";
import { CommentRowComponent } from './comment-table/comment-row.component';
import { ConfirmDeleteCommentModalComponent } from './confirm-delete-comment-modal/confirm-delete-comment-modal.component';
import { CommentTableModalComponent } from './comment-table-modal/comment-table-modal.component';

@NgModule({
  declarations: [
    CommentEditFormComponent,
    CommentTableComponent,
    CommentRowComponent,
    ConfirmDeleteCommentModalComponent,
    CommentTableModalComponent
  ],
  imports: [
    CommonModule,
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
  ]
})
export class CommentBoxModule { }
