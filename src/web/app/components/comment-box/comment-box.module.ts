import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { RichTextEditorModule } from "../rich-text-editor/rich-text-editor.module";
import { CommentEditFormComponent } from './comment-edit-form.component';

@NgModule({
  declarations: [CommentEditFormComponent],
  imports: [
    CommonModule,
    RichTextEditorModule,
    NgbModule,
    FormsModule,
  ],
  exports: [
      CommentEditFormComponent,
  ]
})
export class CommentBoxModule { }
